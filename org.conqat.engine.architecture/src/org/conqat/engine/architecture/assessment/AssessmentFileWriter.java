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
package org.conqat.engine.architecture.assessment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.conqat.engine.architecture.format.ArchitectureFormats;
import org.conqat.engine.architecture.format.EArchitectureIOAttribute;
import org.conqat.engine.architecture.format.EArchitectureIOElement;
import org.conqat.engine.architecture.scope.ArchitectureDefinition;
import org.conqat.engine.architecture.scope.ComponentNode;
import org.conqat.engine.architecture.scope.DependencyPolicy;
import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.sorting.NameSorter.NameComparator;
import org.conqat.engine.core.logging.IConQATLogger;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.SetMap;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.xml.LowercaseResolver;
import org.conqat.lib.commons.xml.XMLWriter;

/**
 * Writes an assessed architecture to disk.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 41275 $
 * @ConQAT.Rating GREEN Hash: DF3FD0EFE88FE1C4D94BBC0358C15E5B
 */
/* package */class AssessmentFileWriter extends
		XMLWriter<EArchitectureIOElement, EArchitectureIOAttribute> {

	/** Architecture that gets written to file */
	private final ArchitectureDefinition architecture;

	/** Logger */
	private final IConQATLogger logger;

	/** Constructor. */
	public AssessmentFileWriter(OutputStream outputStream,
			ArchitectureDefinition arch, IConQATLogger logger) {
		super(
				outputStream,
				new LowercaseResolver<EArchitectureIOElement, EArchitectureIOAttribute>(
						EArchitectureIOAttribute.class));
		this.architecture = arch;
		this.logger = logger;
	}

	/**
	 * Constructor
	 * 
	 * @param file
	 *            Target file into which assessment results are written
	 */
	public AssessmentFileWriter(File file, ArchitectureDefinition arch,
			IConQATLogger logger) throws FileNotFoundException {
		this(getStream(file), arch, logger);
	}

	/** Creates the stream used for output. */
	private static PrintStream getStream(File file)
			throws FileNotFoundException {
		try {
			return new PrintStream(file, FileSystemUtils.UTF8_ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new AssertionError("Error: " + FileSystemUtils.UTF8_ENCODING
					+ " not supported!");
		}
	}

	/** Write architecture to file */
	public void writeArchitecture() {
		// add XML header with version and encoding information
		addHeader("1.0", FileSystemUtils.UTF8_ENCODING);

		openElement(EArchitectureIOElement.ASSESSMENT);
		addAttribute(EArchitectureIOAttribute.XMLNS,
				ArchitectureFormats.ASSESSMENT_RESULT_XML_NAMESPACE);

		if (architecture.hasChildren()) {
			ComponentNode[] children = architecture.getChildren();
			// Sort by name to get stable output
			Arrays.sort(children, NameComparator.getInstance());
			for (ComponentNode topLevelComponent : children) {
				writeComponent(topLevelComponent);
			}
		}

		writeOrphans();

		writeDependencyGroups();
		closeElement(EArchitectureIOElement.ASSESSMENT);

		// close the underlying stream
		close();
	}

	/** Write a component and its descendants to XML */
	private void writeComponent(ComponentNode component) {
		openElement(EArchitectureIOElement.COMPONENT);
		addAttribute(EArchitectureIOAttribute.NAME, component.getName());
		writeMatchedVertices(component);
		if (component.hasChildren()) {
			for (ComponentNode childComponent : component.getChildren()) {
				writeComponent(childComponent);
			}
		}
		closeElement(EArchitectureIOElement.COMPONENT);
	}

	/** Writes the vertices contained in the component to XML */
	@SuppressWarnings("unchecked")
	private void writeMatchedVertices(ComponentNode component) {
		Object matchedTypesObject = component
				.getValue(ArchitectureAnalyzer.MATCHED_TYPES_KEY);
		if (matchedTypesObject == null) {
			return;
		}

		if (!(matchedTypesObject instanceof Collection)) {
			logger.warn("Unexpected type found for key "
					+ ArchitectureAnalyzer.MATCHED_TYPES_KEY + " at node "
					+ component.getId());
			return;
		}

		Collection<String> matchedTypes = (Collection<String>) matchedTypesObject;
		for (String matchedType : CollectionUtils.sort(matchedTypes)) {
			addClosedElement(EArchitectureIOElement.TYPE,
					EArchitectureIOAttribute.NAME, matchedType);
		}
	}

	/** Writes the orphans list. */
	private void writeOrphans() {
		List<String> orphans = NodeUtils.getStringList(architecture,
				ArchitectureAnalyzer.ORPHANS_KEY);
		if (orphans == null) {
			return;
		}

		for (String orphan : CollectionUtils.sort(orphans)) {
			addClosedElement(EArchitectureIOElement.ORPHAN,
					EArchitectureIOAttribute.NAME, orphan);
		}
	}

	/** Write dependency groups section */
	private void writeDependencyGroups() {
		List<DependencyPolicy> policies = architecture.getSortedPolicies();
		for (DependencyPolicy policy : policies) {
			writeDependencyPolicy(policy);
		}
	}

	/** Write a single dependency policy */
	private void writeDependencyPolicy(DependencyPolicy policy) {
		openElement(EArchitectureIOElement.DEPENDENCY_POLICY);
		writeDependencyPolicyAttributes(policy);
		writeDependencies(policy);
		closeElement(EArchitectureIOElement.DEPENDENCY_POLICY);
	}

	/** Write attributes for a dependency policy */
	private void writeDependencyPolicyAttributes(DependencyPolicy policy)
			throws AssertionError {
		addAttribute(EArchitectureIOAttribute.SOURCE, policy.getSource()
				.getName());
		addAttribute(EArchitectureIOAttribute.TARGET, policy.getTarget()
				.getName());

		addAttribute(EArchitectureIOAttribute.POLICY_TYPE,
				policy.getType());
		addAttribute(EArchitectureIOAttribute.ASSESSMENT_TYPE,
				policy.getAssessment());
	}

	/** Write dependencies list */
	private void writeDependencies(DependencyPolicy policy) {
		// use sets to reduce the dependencies to unique pairs (there might be
		// multiple findings for each pair of files)
		SetMap<String, String> filteredDependencies = new SetMap<String, String>();
		for (Finding dependency : policy.getDependencies()) {
			filteredDependencies.add(dependency.getDependencySource(),
					dependency.getDependencyTarget());
		}

		for (String source : CollectionUtils.sort(filteredDependencies
				.getKeys())) {
			for (String target : CollectionUtils.sort(filteredDependencies
					.getCollection(source))) {
				addClosedElement(EArchitectureIOElement.DEPENDENCY,
						EArchitectureIOAttribute.SOURCE, source,
						EArchitectureIOAttribute.TARGET, target);
			}
		}
	}
}