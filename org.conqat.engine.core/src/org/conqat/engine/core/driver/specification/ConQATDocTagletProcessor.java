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
package org.conqat.engine.core.driver.specification;

import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.conqat.engine.core.driver.declaration.DeclarationAttribute;
import org.conqat.engine.core.driver.declaration.DeclarationOutput;
import org.conqat.engine.core.driver.declaration.DeclarationParameter;
import org.conqat.engine.core.driver.declaration.IDeclaration;
import org.conqat.engine.core.driver.util.IDocumentable;
import org.conqat.engine.core.driver.util.IInputReferencable;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.ListMap;
import org.conqat.lib.commons.string.StringUtils;

/**
 * This class interprets and processes the user provided documentation of block
 * specifications. During this step any ConQATDoc tags are processed. This is
 * performed during the initialization of a block specification in a late stage,
 * as we require resolved reference for this task.
 * 
 * @author $Author: goede $
 * @version $Rev: 38019 $
 * @ConQAT.Rating GREEN Hash: CB17F26B21F589CB54BD616B0B086212
 */
public class ConQATDocTagletProcessor {

	/** The name used for the childDoc tag (including a trailing space). */
	public static final String CHILDDOC_TAG = "childDoc ";

	/** The name used for the connDoc tag. */
	public static final String CONNDOC_TAG = "connDoc";

	/**
	 * The pattern used to find ConQATDoc tags. This corresponds to the "usual"
	 * term starting with an at-sign and enclosed in braces.
	 */
	private static final Pattern TAG_PATTERN = Pattern
			.compile("[{][@](.*?)[}]");

	/** The block specification being processed. */
	private final BlockSpecification blockSpecification;

	/** Extracted documentation of child elements. */
	private final Map<String, String> childDoc = new HashMap<String, String>();

	/**
	 * Stores for each {@link BlockSpecificationAttribute} the declaration
	 * attributes connected to it.
	 */
	private final ListMap<BlockSpecificationAttribute, DeclarationAttribute> connectedAttributes = new ListMap<BlockSpecificationAttribute, DeclarationAttribute>(
			new IdentityHashMap<BlockSpecificationAttribute, List<DeclarationAttribute>>());

	/** Constructor. */
	public ConQATDocTagletProcessor(BlockSpecification blockSpecification) {
		this.blockSpecification = blockSpecification;
		prepareMaps();
	}

	/**
	 * Fills the {@link #childDoc} map with the documentation of all
	 * (declaration) child elements. Additionally the
	 * {@link #connectedAttributes} map is constructed.
	 */
	private void prepareMaps() {
		for (IDeclaration declaration : blockSpecification.getDeclarationList()) {
			String declarationName = declaration.getName();
			childDoc.put(declarationName, declaration.getSpecification()
					.getDoc());

			for (DeclarationParameter parameter : declaration.getParameters()) {
				String parameterName = declarationName + "."
						+ parameter.getName();
				childDoc.put(parameterName, parameter
						.getSpecificationParameter().getDoc());

				for (DeclarationAttribute attribute : parameter.getAttributes()) {
					childDoc.put(parameterName + "." + attribute.getName(),
							attribute.getSpecificationAttribute().getDoc());

					IInputReferencable reference = attribute.getReference();
					if (reference != null
							&& reference.asBlockSpecificationAttribute() != null) {
						connectedAttributes.add(
								reference.asBlockSpecificationAttribute(),
								attribute);
					}
				}
			}

			for (DeclarationOutput output : declaration.getOutputs()) {
				childDoc.put(declarationName + "." + output.getName(), output
						.getSpecificationOutput().getDoc());
			}
		}
	}

	/** Performs tag replacement on all documentable entities. */
	public void process() {
		for (BlockSpecificationParameter parameter : blockSpecification
				.getParameters()) {
			for (BlockSpecificationAttribute attribute : parameter
					.getAttributes()) {
				replaceTags(attribute);
			}
			replaceTags(parameter);
		}

		for (BlockSpecificationOutput output : blockSpecification.getOutputs()) {
			replaceTags(output);
		}

		replaceTags(blockSpecification);
	}

	/** Attempts to replace all recognized ConQATDoc tags in the comment. */
	private void replaceTags(IDocumentable documentable) {
		String doc = documentable.getDoc();

		if (StringUtils.isEmpty(doc)) {
			return;
		}

		Matcher m = TAG_PATTERN.matcher(doc);

		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			String replacement = evaluateTag(m.group(1), documentable);
			if (replacement == null) {
				replacement = m.group();
			}

			m.appendReplacement(sb, Matcher.quoteReplacement(replacement));
		}
		m.appendTail(sb);

		documentable.setDoc(sb.toString());
	}

	/**
	 * Evaluates the given ConQATDoc tag (within the starting at-sign). Returns
	 * the replacement for the tag or null if no replacement exists.
	 */
	private String evaluateTag(String tag, IDocumentable element) {
		if (tag.equals(CONNDOC_TAG)) {
			// use sorting to make order of documentation independent of hash
			// function used.
			return joinDocumentation(CollectionUtils
					.sort(collectConnectedDocumentation(element)));
		}

		if (tag.startsWith(CHILDDOC_TAG)) {
			String childName = StringUtils.stripPrefix(CHILDDOC_TAG, tag)
					.trim();
			if (!childDoc.containsKey(childName)) {
				return null;
			}
			String replacement = childDoc.get(childName);
			if (replacement == null) {
				return childName;
			}
			return replacement;
		}

		return null;
	}

	/** Collects documentation from connected elements. */
	private Set<String> collectConnectedDocumentation(IDocumentable element) {

		Set<String> result = new HashSet<String>();

		if (element instanceof BlockSpecificationOutput) {
			DeclarationOutput output = ((BlockSpecificationOutput) element)
					.getReference().asDeclarationOutput();
			result.add(output.getSpecificationOutput().getDoc());
		} else if (element instanceof BlockSpecificationAttribute) {
			List<DeclarationAttribute> attributes = connectedAttributes
					.getCollection(((BlockSpecificationAttribute) element));
			if (attributes != null) {
				for (DeclarationAttribute attribute : attributes) {
					result.add(attribute.getSpecificationAttribute().getDoc());
				}
			}
		} else if (element instanceof BlockSpecificationParameter) {
			for (BlockSpecificationAttribute specAttribute : ((BlockSpecificationParameter) element)
					.getAttributes()) {
				List<DeclarationAttribute> attributes = connectedAttributes
						.getCollection(specAttribute);
				if (attributes != null) {
					for (DeclarationAttribute attribute : attributes) {
						result.add(attribute.getParameter()
								.getSpecificationParameter().getDoc());
					}
				}
			}
		}

		result.remove(null);
		return result;
	}

	/**
	 * Joins multiple documentation strings. For this, equal documentation is
	 * filtered (equality is determined modulo whitespace, casing) and each
	 * unique string is concatenated.
	 */
	private String joinDocumentation(List<String> documentation) {
		if (documentation.isEmpty()) {
			return null;
		}

		Map<String, String> m = new HashMap<String, String>();
		for (String doc : documentation) {
			m.put(normalize(doc), doc);
		}

		return StringUtils.concat(m.values());
	}

	/**
	 * Performs normalization on the string as described in
	 * {@link #joinDocumentation(List)}.
	 */
	private String normalize(String doc) {
		return doc.replaceAll("\\s", "").toLowerCase();
	}
}