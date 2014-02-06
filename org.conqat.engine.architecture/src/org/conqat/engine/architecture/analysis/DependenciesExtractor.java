/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
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
package org.conqat.engine.architecture.analysis;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.conqat.engine.architecture.assessment.shared.ArchitectureAssessor;
import org.conqat.engine.commons.findings.EFindingKeys;
import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.findings.FindingGroup;
import org.conqat.engine.commons.findings.FindingReport;
import org.conqat.engine.commons.findings.location.ElementLocation;
import org.conqat.engine.commons.findings.util.FindingUtils;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.commons.util.ConQATInputProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IElement;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating GREEN Hash: 37834147FD8D4A1BA4812061743D7EC4
 */
@AConQATProcessor(description = "This processor extracts the findings "
		+ "embedded in a ConQAT node structure which represent dependencies "
		+ "between elements. It also creates artifical findings for "
		+ "dependencies which are stored as strings and for each type found "
		+ "in the system.")
public class DependenciesExtractor extends
		ConQATInputProcessorBase<IConQATNode> {

	/** Set of keys storing the string lists containing the dependencies. */
	private final Set<String> keys = new HashSet<String>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "list-key", description = "Add a key to read a dependency list from.")
	public void addListKey(
			@AConQATAttribute(name = "key", description = "The name of the key.") String key) {
		keys.add(key);
	}

	/** The extracted dependencies stored as a finding report. */
	private FindingReport report;

	/** Finding group used for the creation of artificial findings. */
	private FindingGroup findingGroup;

	/**
	 * Finding group used for the creation of artificial findings that document
	 * the types found in the system. This is needed because otherwise, types
	 * which do not belong to any dependency would be missing in the final
	 * finding report.
	 */
	private FindingGroup typesGroup;

	/** Maps type names that were encountered to locations. */
	private Map<String, ElementLocation> typeToLocationMap;

	/** {@inheritDoc} */
	@Override
	public FindingReport process() throws ConQATException {
		report = new FindingReport();
		createFindingGroups(report);
		typeToLocationMap = new HashMap<String, ElementLocation>();

		for (IConQATNode leaf : TraversalUtils.listLeavesDepthFirst(input)) {
			extractDependencies(leaf);
		}

		for (Entry<String, ElementLocation> entry : typeToLocationMap
				.entrySet()) {
			Finding finding = typesGroup.createFinding(entry.getValue());
			finding.setValue(EFindingKeys.MESSAGE.toString(), entry.getKey());
		}

		return report;
	}

	/** Extracts dependencies from the given node. */
	private void extractDependencies(IConQATNode node) throws ConQATException {
		String type = node.getId();

		ElementLocation location;
		if (node instanceof IElement) {
			IElement element = (IElement) node;
			location = new ElementLocation(element.getLocation(),
					element.getUniformPath());
		} else {
			location = new ElementLocation(node.getId(), node.getId());
		}
		typeToLocationMap.put(type, location);

		for (String key : keys) {
			Collection<Object> targetList = NodeUtils.getTypedCollection(node,
					key, Object.class);
			if (targetList != null) {
				for (Object target : targetList) {
					extractDependency(type, location, target);
				}
			}
		}
	}

	/**
	 * Creates the artificial groups to store types and dependencies in the
	 * findings report.
	 */
	private void createFindingGroups(FindingReport report) {
		findingGroup = report.getOrCreateCategory(
				ArchitectureAssessor.DEPENDENCY_CATEGORY)
				.getOrCreateFindingGroup(ArchitectureAssessor.DEPENDENY_GROUP);
		typesGroup = report.getOrCreateCategory(
				ArchitectureAssessor.TYPES_CATEGORY).getOrCreateFindingGroup(
				ArchitectureAssessor.TYPES_GROUP);
	}

	/**
	 * Creates a new dependency between the given source type and the target
	 * which must either be a string or a finding.
	 */
	private void extractDependency(String source, ElementLocation location,
			Object target) throws ConQATException {

		String targetID;

		if (target instanceof String) {
			targetID = extractDependencyFromStringTarget(source, location,
					(String) target);
		} else if (target instanceof Finding) {
			targetID = extractDependencyFromFinding((Finding) target);
		} else {
			throw new ConQATException("Dependency list must contain either "
					+ "strings or findings!");
		}

		if (!typeToLocationMap.containsKey(targetID)) {
			// create with dummy location; if we can find a better one, it will
			// be overwritten
			typeToLocationMap.put(targetID, new ElementLocation(targetID,
					targetID));
		}
	}

	/**
	 * Extracts a dependency between the source and the target both of which are
	 * strings. A new finding is created that represents this dependency.
	 * Returns the ID of the target.
	 */
	private String extractDependencyFromStringTarget(String source,
			ElementLocation location, String target) {
		Finding finding = findingGroup.createFinding(location);
		finding.setValue(EFindingKeys.DEPENDENCY_SOURCE.toString(), source);
		finding.setValue(EFindingKeys.DEPENDENCY_TARGET.toString(), target);
		finding.setValue(EFindingKeys.MESSAGE.toString(), source + " -> "
				+ target);

		return target;
	}

	/**
	 * Extracts a dependency from the given finding that represents the
	 * dependency. Returns the dependency's target ID.
	 */
	private String extractDependencyFromFinding(Finding finding)
			throws ConQATException {
		if (finding.getDependencyTarget() == null) {
			throw new ConQATException("Findings without target cannot be used "
					+ "for dependency annotation!");
		}
		FindingUtils.adoptFinding(report, finding);
		return finding.getDependencyTarget();
	}
}
