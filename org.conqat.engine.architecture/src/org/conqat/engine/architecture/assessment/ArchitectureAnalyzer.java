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

import java.util.List;

import org.conqat.engine.architecture.assessment.shared.ArchitectureAssessmentUtils;
import org.conqat.engine.architecture.assessment.shared.ArchitectureAssessor;
import org.conqat.engine.architecture.assessment.shared.Dependency;
import org.conqat.engine.architecture.assessment.shared.IComponent;
import org.conqat.engine.architecture.assessment.shared.TypeDependency;
import org.conqat.engine.architecture.scope.AnnotatedArchitecture;
import org.conqat.engine.architecture.scope.ArchitectureDefinition;
import org.conqat.engine.architecture.scope.ComponentNode;
import org.conqat.engine.architecture.scope.DependencyPolicy;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.findings.FindingReport;
import org.conqat.engine.commons.findings.util.FindingUtils;
import org.conqat.engine.commons.logging.ListStructuredLogMessage;
import org.conqat.engine.commons.logging.StructuredLogTags;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.ELogLevel;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.clone.DeepCloneException;
import org.conqat.lib.commons.collections.ListMap;
import org.conqat.lib.commons.collections.SetMap;
import org.conqat.lib.commons.string.StringUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46753 $
 * @ConQAT.Rating GREEN Hash: 7E004FE2B8B6D3BED8A45BA07BC9E824
 */
@AConQATProcessor(description = "This processor assesses an architecture and "
		+ "adds dependencies to the policies. The result is an assessed and "
		+ "annotated architecture, which also holds a reference to the "
		+ "scope from which the dependencies were extracted. Dependencies are "
		+ "represented using findings, i.e., each finding stands for a "
		+ "type-level dependency. Note, that this applies to all dependencies "
		+ "and is not limited to violations. By representing dependencies with "
		+ "findings, all of the existing infrastructure for handling findings "
		+ "can be reused.")
public class ArchitectureAnalyzer extends ConQATProcessorBase {

	/** The tag used for user-level logging of architecture specific messages. */
	public static final String ARCHITECTURE_LOGGING_TAG = "architecture";

	/** Key for elements w/o a component. */
	@AConQATKey(description = "Stores the list of orphaned nodes.", type = "java.util.List<String>")
	public static final String ORPHANS_KEY = "orphans";

	/** Key under which the set of matched types is stored */
	@AConQATKey(description = "Stores the names of types matched to a component.", type = "java.util.Set<String>")
	public static final String MATCHED_TYPES_KEY = "matched_types";

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "scope", attribute = "ref", optional = false, description = "Input scope for which the assessment is to be done.")
	public IConQATNode root;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "architecture", attribute = "definition", optional = false, description = "The architecture definition used for the analysis.")
	public ArchitectureDefinition architecture;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "dependencies", attribute = "report", optional = false, description = "Finding report that contains the dependencies between types. Each finding represents a single dependency. Note that this is not limited to violations.")
	public FindingReport report;

	/** {ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "log", attribute = "dependencies", optional = true, description = ""
			+ "Enable or disable logging of dependencies used for "
			+ "architecture analysis. By default, logging is enabled. "
			+ "Dependencies are always logged at user level.")
	public boolean logDependencies = true;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "orphan-log-level", attribute = "value", optional = true, description = "Level on which orphans are logged. Default is WARN.")
	public ELogLevel orphanLogLevel = ELogLevel.WARN;

	/**
	 * Keeps track of which finding belongs to which dependency. This is needed,
	 * because after assessment, the type-level dependencies have to be
	 * annotated to the architecture's policies as findings.
	 */
	private final ListMap<TypeDependency, Finding> typeDependencyTofinding = new ListMap<TypeDependency, Finding>();

	/** {@inheritDoc} */
	@Override
	public AnnotatedArchitecture process() throws ConQATException {
		// Store the mapping between type-level dependencies and findings.
		createTypeDependenciesToFindingsMap();

		// Extract ALL known types and their dependencies (which may be empty)
		// from the system, regardless of whether they are part of a dependency.
		SetMap<String, String> types = ArchitectureAssessmentUtils
				.getTypeDependencies(report);

		// Do the actual assessment.
		ArchitectureAssessor assessor = new ArchitectureAssessor(architecture,
				types);

		// Fail if no suitable input was provided.
		if (assessor.getAllDependencies().isEmpty()) {
			throw new ConQATException("No dependencies found in provided "
					+ "scope! Probably this is a configuration error.");
		}

		annotateArchitectureWithResult(assessor);

		doLogging(assessor);

		// What we basically need to do is to copy the input architecture. As
		// this object has a lot of fields this a bit tedious. On the other
		// hand, this is already implemented in the deep cloning facility of the
		// ArchitectureDefinition. Hence, we reuse this here. This has two
		// drawbacks: First, it performs a deep clone which wouldn't really be
		// needed at this point. Second, we need to care about the deep clone
		// exceptions. The former problem is not serious as architectures are
		// usually rather small. The latter, is addressed by the construct
		// below.
		try {
			return new AnnotatedArchitecture(architecture, root);
		} catch (DeepCloneException e) {
			throw new AssertionError("Deep cloning produced an exception ("
					+ e.getMessage() + ") which results from an invalid "
					+ "architecture. However, since the assessment has been "
					+ "done already, the architecture is supposed to be valid "
					+ "at his point.");
		}
	}

	/**
	 * Creates the map from type-level dependencies to the corresponding
	 * findings.
	 */
	private void createTypeDependenciesToFindingsMap() {
		for (Finding finding : FindingUtils.getAllFindings(report,
				ArchitectureAssessor.DEPENDENCY_CATEGORY)) {
			String source = finding.getDependencySource();
			String target = finding.getDependencyTarget();
			CCSMAssert.isNotNull(source);
			CCSMAssert.isNotNull(target);
			typeDependencyTofinding.add(new TypeDependency(source, target),
					finding);
		}
	}

	/**
	 * Annotates the assessment result from the given assessor to the
	 * architecture.
	 */
	private void annotateArchitectureWithResult(ArchitectureAssessor assessor)
			throws ConQATException {
		// Add matched types to each component.
		for (ComponentNode component : architecture.getAllComponents()) {
			NodeUtils.getOrCreateStringSet(component, MATCHED_TYPES_KEY)
					.addAll(assessor.getMappedTypes(component));
		}

		// Add orphans to the architecture root node.
		NodeUtils.addToDisplayList(architecture, ORPHANS_KEY);
		NodeUtils.getOrCreateStringList(architecture, ORPHANS_KEY).addAll(
				assessor.getOrphans());

		insertDependenciesToArchitecture(assessor.getAllDependencies());
	}

	/** Add dependencies to the architecture. */
	private void insertDependenciesToArchitecture(
			List<Dependency<?>> dependencies) throws ConQATException {
		for (Dependency<?> dependency : dependencies) {
			insert(dependency);
		}
	}

	/** Inserts the given dependency into the architecture. */
	private void insert(Dependency<? extends IComponent> dependency)
			throws ConQATException {
		ComponentNode source = (ComponentNode) dependency.getSource();
		ComponentNode target = (ComponentNode) dependency.getTarget();

		DependencyPolicy policy = source.getPolicyTo(target);
		if (policy == null) {
			// If there is no policy yet, create a new one.
			policy = new DependencyPolicy(source, target, dependency.getType());
			policy.registerWithComponents();
		}

		// Add all type-level dependencies and corresponding findings to the
		// policy.
		for (TypeDependency typeDependency : dependency.getTypeDependencies()) {
			policy.addTypeDependency(typeDependency);
			CCSMAssert
					.isTrue(typeDependencyTofinding
							.containsCollection(typeDependency),
							"For each type-level dependency there has to be at least one finding.");
			for (Finding finding : typeDependencyTofinding
					.getCollection(typeDependency)) {
				policy.addDependency(finding);
			}
		}
	}

	/** Logs information from the given assessor. */
	private void doLogging(ArchitectureAssessor assessor) {
		// Creates user-visible log messages for elements that were ignored as
		// they were not mapped to a component (orphans).
		if (!assessor.getOrphans().isEmpty()) {
			ListStructuredLogMessage message = new ListStructuredLogMessage(
					"Had " + assessor.getOrphans().size()
							+ " orphans (elements that could "
							+ "not be mapped to a component and were ignored)!",
					assessor.getOrphans(), ARCHITECTURE_LOGGING_TAG);
			getLogger().log(orphanLogLevel, message);
		}

		logTypesMatchingMultipleComponents(assessor
				.getTypesMatchingMultipleComponents());

		if (logDependencies) {
			logDependencies(assessor.getAllDependencies());
		}
	}

	/** Logs a warning for each type that matches multiple components. */
	private void logTypesMatchingMultipleComponents(
			ListMap<String, IComponent> typesMatchingMultipleComponents) {
		for (String type : typesMatchingMultipleComponents.getKeys()) {
			List<IComponent> matchingComponents = typesMatchingMultipleComponents
					.getCollection(type);
			StringBuilder message = new StringBuilder();
			message.append("Type '");
			message.append(type);
			message.append("' can be mapped to more than one component (");
			for (IComponent component : matchingComponents) {
				if (component != matchingComponents.get(0)) {
					message.append(", ");
				}
				message.append(component.getName());
			}
			message.append(")");
			getLogger().warn(message.toString());
		}
	}

	/** Create user-visible log message for all processed dependencies. */
	private void logDependencies(List<Dependency<?>> dependencies) {
		getLogger().info(
				new ListStructuredLogMessage("Dependencies: "
						+ dependencies.size(), StringUtils
						.asStringList(dependencies), StructuredLogTags.FILES));
	}
}