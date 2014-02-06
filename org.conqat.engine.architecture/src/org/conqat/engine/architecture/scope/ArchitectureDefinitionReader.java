/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 The ConQAT Project                                   |
|                                                                          |
| Licensed under the Apache License, Version 2.0 (the "License");          |
| you may not use this file except in compliance with the License.         |
| You may obtain a copy of the License at                                  |
|                                                                          |
|    http://www.apache.org/licenses/LICENSE-2.0                            |
|                                                                          |
| Unless required by applicable law or agreed to in writing, software      |
| distributed under the License is distributed on an "AS IS" BASIS,        |
| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. |
| See the License for the specific language governing permissions and      |
| limitations under the License.                                           |
+-------------------------------------------------------------------------*/
package org.conqat.engine.architecture.scope;

import static org.conqat.engine.architecture.format.EArchitectureIOAttribute.DIM;
import static org.conqat.engine.architecture.format.EArchitectureIOAttribute.NAME;
import static org.conqat.engine.architecture.format.EArchitectureIOAttribute.POS;
import static org.conqat.engine.architecture.format.EArchitectureIOAttribute.REGEX;
import static org.conqat.engine.architecture.format.EArchitectureIOAttribute.SOURCE;
import static org.conqat.engine.architecture.format.EArchitectureIOAttribute.STEREOTYPE;
import static org.conqat.engine.architecture.format.EArchitectureIOAttribute.TARGET;
import static org.conqat.engine.architecture.format.EArchitectureIOElement.ALLOW;
import static org.conqat.engine.architecture.format.EArchitectureIOElement.CODE_MAPPING;
import static org.conqat.engine.architecture.format.EArchitectureIOElement.COMMENT;
import static org.conqat.engine.architecture.format.EArchitectureIOElement.COMPONENT;
import static org.conqat.engine.architecture.format.EArchitectureIOElement.DENY;
import static org.conqat.engine.architecture.format.EArchitectureIOElement.TOLERATE;
import static org.conqat.engine.architecture.format.ECodeMappingType.EXCLUDE;
import static org.conqat.engine.architecture.format.ECodeMappingType.INCLUDE;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.conqat.engine.architecture.assessment.shared.IComponent;
import org.conqat.engine.architecture.assessment.shared.TypeDependency;
import org.conqat.engine.architecture.format.ArchitectureFormats;
import org.conqat.engine.architecture.format.EArchitectureIOAttribute;
import org.conqat.engine.architecture.format.EArchitectureIOElement;
import org.conqat.engine.architecture.format.EPolicyType;
import org.conqat.engine.architecture.format.EStereotype;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.commons.util.ConQATXMLReader;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.enums.EnumUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.commons.xml.IXMLElementProcessor;
import org.conqat.lib.commons.xml.LowercaseResolver;

/**
 * This processor reads an architecture definition from an XML file and creates
 * a {@link ComponentNode} hierarchy that represents the architecture.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43813 $
 * @ConQAT.Rating GREEN Hash: 7EF534A0DD6C862D673AE2BE39E7CAEC
 */
@AConQATProcessor(description = "This processor reads an architecture "
		+ "definition from a given input file (using our proprietary XML format)")
public class ArchitectureDefinitionReader extends ConQATProcessorBase {

	/** Regexp that matches digits potentially prefixed with "-" */
	private static final String DIGITS = "(-?\\d+)";

	/** Pattern used to extract positions and dimensions. */
	private static final Pattern PAIR_PATTERN = Pattern.compile(DIGITS + ", ?"
			+ DIGITS);

	/** Input file containing the xml architecture description */
	private File architectureFile;

	/** Input scope containing one xml file with the architecture description. */
	private ITextElement architectureTextElement;

	/** Mapping from component name to component. */
	private final Map<String, ComponentNode> componentByName = new HashMap<String, ComponentNode>();

	/** Root node of resulting architecture hierarchy */
	private ArchitectureDefinition result;

	/** Set input filename. */
	@AConQATParameter(name = "input", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Name of the input XML file describing the architecture.")
	public void setInputFile(
			@AConQATAttribute(name = "file", description = "The name of the XML file.") File architectureFile)
			throws ConQATException {
		if (!architectureFile.canRead() || !architectureFile.isFile()) {
			throw new ConQATException("Cannot read architecture file '"
					+ architectureFile + "'");
		}
		this.architectureFile = architectureFile;
	}

	/** Set input filename. */
	@AConQATParameter(name = "input-scope", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "TextResource Scope containing exactly one architecture definition file.")
	public void setInputScope(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) ITextResource textResource)
			throws ConQATException {
		List<ITextElement> textElements = ResourceTraversalUtils
				.listTextElements(textResource);
		if (textElements.size() != 1) {
			throw new ConQATException(
					"The architecture scope must include exactly one file.");
		}
		architectureTextElement = textElements.get(0);
	}

	/** Read the architecture definition from the given file. */
	@Override
	public ArchitectureDefinition process() throws ConQATException {

		if (architectureFile == null && architectureTextElement == null) {
			throw new ConQATException(
					"Neither an architecture file nor an architecture text scope is specified.");
		}

		try {
			if (architectureFile != null) {
				getLogger().debug(
						"Reading architecture definition file: "
								+ architectureFile.getAbsolutePath());
				new Reader(architectureFile).read();
			} else {
				getLogger().debug(
						"Reading architecture definition file: "
								+ architectureTextElement.getLocation());
				new Reader(architectureTextElement).read();
			}
		} catch (MalformedURLException e) {
			throw new IllegalStateException(
					"This can only happen due to missing resources.", e);
		} catch (IOException e) {
			throw new ConQATException("An i/o error occured!", e);
		}

		checkDependencies();

		return result;
	}

	/** Perform some sanity checks on dependencies. */
	private void checkDependencies() throws ConQATException {
		List<DependencyPolicy> policies = new ArrayList<DependencyPolicy>();
		result.collectPolicies(policies);

		checkTreeFollowing(policies);
		checkCrossing(policies);
	}

	/**
	 * Checks for policies that follow the hierarchy tree. These policies are
	 * unnecessary, since dependencies between parents and successors, or vice
	 * versa, are always allowed.
	 */
	private void checkTreeFollowing(List<DependencyPolicy> policies)
			throws ConQATException {
		for (DependencyPolicy policy : policies) {
			if (isTreeFollowing(policy)) {
				throw new ConQATException("Tree-following policy: " + policy);
			}
		}
	}

	/**
	 * Checks if the given policy is "tree-following", i.e. source or target is
	 * a parent of the other.
	 */
	private boolean isTreeFollowing(DependencyPolicy policy) {
		IComponent source = policy.getSource();
		IComponent target = policy.getTarget();
		return source.getAncestors().contains(target)
				|| target.getAncestors().contains(source);
	}

	/**
	 * Check for crossing edges. These edges have no clear semantics and are
	 * thus illegal.
	 */
	private void checkCrossing(List<DependencyPolicy> policies)
			throws ConQATException {
		for (DependencyPolicy policy : policies) {
			for (DependencyPolicy policy2 : policies) {
				if (policy != policy2) {
					checkNotSame(policy, policy2);
					checkNotCrossing(policy, policy2);
					checkNotCrossing(policy2, policy);
				}
			}
		}
	}

	/** Make sure the policies differ it at least source or target. */
	private void checkNotSame(DependencyPolicy policy, DependencyPolicy policy2)
			throws ConQATException {
		if (policy.getSource() == policy2.getSource()
				&& policy.getTarget() == policy2.getTarget()) {
			throw new ConQATException("Duplicate policy for " + policy);
		}
	}

	/** Make sure the two policies are not crossing. */
	private void checkNotCrossing(DependencyPolicy policy,
			DependencyPolicy policy2) throws ConQATException {
		boolean source2IsLower = policy.getSource().getAncestors()
				.contains(policy2.getSource());
		boolean target2IsHigher = policy2.getSource().getAncestors()
				.contains(policy.getTarget());

		if (source2IsLower && target2IsHigher) {
			String message = "The policies " + policy + " and " + policy2
					+ " are crossing each other!";
			throw new ConQATException(message);
		}
	}

	/** Insert single dependency rule into the component tree. */
	private DependencyPolicy insertPolicy(String sourceName, String targetName,
			EPolicyType policyType) throws ConQATException {
		ComponentNode source = componentByName.get(sourceName);
		if (source == null) {
			throw new ConQATException("Source component " + sourceName
					+ " not found!");
		}
		ComponentNode target = componentByName.get(targetName);
		if (target == null) {
			throw new ConQATException("Target component " + targetName
					+ " not found!");
		}
		return createPolicy(source, target, policyType);
	}

	/** Creates a policy */
	private DependencyPolicy createPolicy(ComponentNode source,
			ComponentNode target, EPolicyType policyType)
			throws ConQATException {
		DependencyPolicy policy = new DependencyPolicy(source, target,
				policyType);
		policy.registerWithComponents();
		return policy;
	}

	/** The reader class used. */
	private class Reader
			extends
			ConQATXMLReader<EArchitectureIOElement, EArchitectureIOAttribute, ConQATException> {

		/** Constructor. */
		public Reader(File file) throws IOException {
			super(
					file,
					ArchitectureFormats.getArchitectureDefinitionSchema(),
					new LowercaseResolver<EArchitectureIOElement, EArchitectureIOAttribute>(
							EArchitectureIOAttribute.class));
		}

		/** Constructor. */
		public Reader(ITextElement textElement) throws ConQATException {
			super(
					textElement.getUnfilteredTextContent(),
					ArchitectureFormats.getArchitectureDefinitionSchema(),
					new LowercaseResolver<EArchitectureIOElement, EArchitectureIOAttribute>(
							EArchitectureIOAttribute.class));
		}

		/** The reader's main method. */
		public void read() throws ConQATException {
			parseAndWrapExceptions();

			result = new ArchitectureDefinition();

			processChildElements(new ComponentProcessor(result));
			processChildElements(new PolicyProcessor(ALLOW));
			processChildElements(new PolicyProcessor(DENY));
			processChildElements(new PolicyProcessor(TOLERATE));
		}

		/** {@inheritDoc} */
		@Override
		protected String getLocation() {
			if (architectureFile != null) {
				return architectureFile.getAbsolutePath();
			}
			return architectureTextElement.getLocation();
		}

		/** Processor for components. */
		private final class ComponentProcessor implements
				IXMLElementProcessor<EArchitectureIOElement, ConQATException> {

			/** The parent node. */
			private final ComponentNode parent;

			/** Constructor. */
			public ComponentProcessor(ComponentNode parent) {
				this.parent = parent;
			}

			/** {@inheritDoc} */
			@Override
			public EArchitectureIOElement getTargetElement() {
				return COMPONENT;
			}

			/** {@inheritDoc} */
			@Override
			public void process() throws ConQATException {
				String name = getStringAttribute(NAME);
				if (componentByName.containsKey(name)) {
					throw new ConQATException("Duplicate unique Name: " + name);
				}

				String stereotypeString = getStringAttribute(STEREOTYPE);
				EStereotype stereoType = EStereotype.NONE;
				if (!StringUtils.isEmpty(stereotypeString)) {
					stereoType = EnumUtils.valueOfIgnoreCase(EStereotype.class,
							stereotypeString);
				}

				Matcher posMatcher = pairMatcher(getStringAttribute(POS));
				Matcher dimMatcher = pairMatcher(getStringAttribute(DIM));
				Point position = new Point(
						Integer.parseInt(posMatcher.group(1)),
						Integer.parseInt(posMatcher.group(2)));
				Dimension dimension = new Dimension(Integer.parseInt(dimMatcher
						.group(1)), Integer.parseInt(dimMatcher.group(2)));

				ComponentNode node = new ComponentNode(name, position,
						dimension, stereoType);

				componentByName.put(name, node);

				processChildElements(new ComponentProcessor(node));
				processChildElements(new CodeMappingProcessor(node));
				processChildElements(new CommentProcessor(node));

				parent.addChild(node);
			}

			/**
			 * Creates a new matcher for pairs (position/dimension), matches,
			 * and throws an exception if it does not match.
			 */
			private Matcher pairMatcher(String pairString)
					throws ConQATException {
				Matcher m = PAIR_PATTERN.matcher(pairString);
				if (!m.matches()) {
					throw new ConQATException(
							"Invalid position/dimension string: " + pairString);
				}
				return m;
			}
		}

		/** Processor for code mappings. */
		private final class CodeMappingProcessor implements
				IXMLElementProcessor<EArchitectureIOElement, ConQATException> {

			/** The parent node. */
			private final ComponentNode node;

			/** Constructor. */
			public CodeMappingProcessor(ComponentNode node) {
				this.node = node;
			}

			/** {@inheritDoc} */
			@Override
			public EArchitectureIOElement getTargetElement() {
				return CODE_MAPPING;
			}

			/** {@inheritDoc} */
			@Override
			public void process() throws ConQATException {
				String type = getStringAttribute(EArchitectureIOAttribute.TYPE);
				if (type.equalsIgnoreCase(INCLUDE.toString())) {
					node.addIncludeRegex(getStringAttribute(REGEX));
				} else if (type.equalsIgnoreCase(EXCLUDE.toString())) {
					node.addExcludeRegex(getStringAttribute(REGEX));
				} else {
					CCSMAssert.fail("Should be checked in schema/impossible!");
				}
			}
		}

		/** Processor for comments. */
		private final class CommentProcessor implements
				IXMLElementProcessor<EArchitectureIOElement, ConQATException> {

			/** The parent node. */
			private final ComponentNode node;

			/** Constructor. */
			public CommentProcessor(ComponentNode node) {
				this.node = node;
			}

			/** {@inheritDoc} */
			@Override
			public EArchitectureIOElement getTargetElement() {
				return COMMENT;
			}

			/** {@inheritDoc} */
			@Override
			public void process() {
				String description = getText();
				CCSMAssert.isNotNull(description);
				node.setDescription(description);
			}
		}

		/** Processor for policies. */
		private final class PolicyProcessor implements
				IXMLElementProcessor<EArchitectureIOElement, ConQATException> {

			/** Target element */
			private final EArchitectureIOElement targetElement;

			/** Type of policy being read */
			private final EPolicyType policyType;

			// Replace this if cascade by adding a parameter? Would probably
			// make this easier to read.
			/** Creates PolicyProcessor for a policy type */
			public PolicyProcessor(final EArchitectureIOElement targetElement) {
				this.targetElement = targetElement;
				String tagName = targetElement.toString();
				if (ALLOW.toString().equals(tagName)) {
					policyType = EPolicyType.ALLOW_EXPLICIT;
				} else if (DENY.toString().equals(tagName)) {
					policyType = EPolicyType.DENY_EXPLICIT;
				} else if (TOLERATE.toString().equals(tagName)) {
					policyType = EPolicyType.TOLERATE_EXPLICIT;
				} else {
					throw new AssertionError("Tag " + tagName
							+ " not implemented");
				}
			}

			/** {@inheritDoc} */
			@Override
			public EArchitectureIOElement getTargetElement() {
				return targetElement;
			}

			/** {@inheritDoc} */
			@Override
			public void process() throws ConQATException {
				DependencyPolicy policy = insertPolicy(
						getStringAttribute(SOURCE), getStringAttribute(TARGET),
						policyType);
				if (policyType == EPolicyType.TOLERATE_EXPLICIT) {
					processChildElements(new ToleratePolicyProcessor(policy));
				}
			}
		}

		/** Processor for policies. */
		private final class ToleratePolicyProcessor implements
				IXMLElementProcessor<EArchitectureIOElement, ConQATException> {

			/** The parent policy we work on */
			private final DependencyPolicy policy;

			/**
			 * @param policy
			 */
			public ToleratePolicyProcessor(DependencyPolicy policy) {
				this.policy = policy;
			}

			/** {@inheritDoc} */
			@Override
			public EArchitectureIOElement getTargetElement() {
				return EArchitectureIOElement.DEPENDENCY;
			}

			/** {@inheritDoc} */
			@Override
			public void process() {
				policy.addToleratedTypeDependency(new TypeDependency(
						getStringAttribute(EArchitectureIOAttribute.SOURCE),
						getStringAttribute(EArchitectureIOAttribute.TARGET)));
			}
		}

	}
}