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
package org.conqat.engine.simulink.clones;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.commons.node.NodeConstants;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.model_clones.detection.IModelCloneDetector;
import org.conqat.engine.model_clones.detection.util.AugmentedModelGraph;
import org.conqat.engine.model_clones.detection.util.ICloneReporter;
import org.conqat.engine.model_clones.metrics.IModelCloneMetric;
import org.conqat.engine.model_clones.model.IDirectedEdge;
import org.conqat.engine.model_clones.model.INode;
import org.conqat.engine.simulink.clones.model.SimulinkDirectedEdge;
import org.conqat.engine.simulink.clones.model.SimulinkModelGraphCreator;
import org.conqat.engine.simulink.clones.model.SimulinkNode;
import org.conqat.engine.simulink.clones.normalize.ISimulinkNormalizer;
import org.conqat.engine.simulink.clones.preprocess.ISimulinkPreprocessor;
import org.conqat.engine.simulink.clones.result.SimulinkClone;
import org.conqat.engine.simulink.clones.result.SimulinkCloneResultNode;
import org.conqat.engine.simulink.scope.ISimulinkResource;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.collections.IdentityHashSet;
import org.conqat.lib.commons.collections.UnmodifiableList;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.simulink.model.SimulinkBlock;
import org.conqat.lib.simulink.model.SimulinkLine;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 42074 $
 * @ConQAT.Rating GREEN Hash: E1500D7714A832C7E45B01A54E30ADEC
 */
@AConQATProcessor(description = "Performs clone detection on simulink models.")
public class SimulinkCloneDetector extends ConQATProcessorBase implements
		ICloneReporter {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "The names of the models in which the clone was found.", type = "java.util.Set<String>")
	public static final String MODELS_KEY = "models";

	/** The input node. */
	private ISimulinkResource input;

	/** The metrics being applied to the clones found. */
	private final List<IModelCloneMetric> metrics = new ArrayList<IModelCloneMetric>();

	/** The result of this processor. */
	private final SimulinkCloneResultNode result = new SimulinkCloneResultNode();

	/** The clone that is currently reported. */
	private SimulinkClone currentClone = null;

	/**
	 * Number of clone instances still to come. Use in
	 * {@link #addModelCloneInstance(List, List)}.
	 */
	private int expectedInstances = 0;

	/** Counter used for statistics and to generate IDs. */
	private int cloneCounter = 0;

	/** Preprocessors used. */
	private final List<ISimulinkPreprocessor> preprocessors = new ArrayList<ISimulinkPreprocessor>();

	/** The detector used. */
	private IModelCloneDetector detector;

	/** The normalizer used. */
	private ISimulinkNormalizer normalizer;

	/** The graph the detector works on. */
	private AugmentedModelGraph augmentedGraph;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.INPUT_NAME, minOccurrences = 1, maxOccurrences = 1, description = ConQATParamDoc.INPUT_DESC)
	public void setModel(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) ISimulinkResource input) {
		this.input = input;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "preprocessor", description = "Adds a preprocessor to be applied prior to detection.")
	public void addPreprocessor(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) ISimulinkPreprocessor preprocessor) {
		this.preprocessors.add(preprocessor);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "detection", minOccurrences = 0, maxOccurrences = 1, description = "The detection being used. "
			+ "It is possible to omit this parameter, but only if at least one preprocessor is used.")
	public void setDetection(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) IModelCloneDetector detector) {
		this.detector = detector;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "normalizer", minOccurrences = 1, maxOccurrences = 1, description = "Setup the normalizer used.")
	public void setNormalizers(
			@AConQATAttribute(name = "ref", description = "Reference to the normalizer") ISimulinkNormalizer normalizer) {
		this.normalizer = normalizer;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "metric", minOccurrences = 0, description = "Adds a metric which is calculated for the clones found. The metric will be added as a key to the clone groups.")
	public void addMetric(
			@AConQATAttribute(name = "ref", description = "Reference to the metric") IModelCloneMetric metric) {
		metrics.add(metric);
	}

	/** {@inheritDoc} */
	@Override
	public SimulinkCloneResultNode process() throws ConQATException {

		if (preprocessors.isEmpty() && detector == null) {
			throw new ConQATException(
					"If no detector if provided, at least one preprocessor must be used!");
		}

		for (IModelCloneMetric metric : metrics) {
			NodeUtils.addToDisplayList(result, metric.getName());
		}
		NodeUtils.addToDisplayList(result, MODELS_KEY);
		result.setValue(NodeConstants.HIDE_ROOT, true);

		Set<SimulinkBlock> ignoreSet = new IdentityHashSet<SimulinkBlock>();
		augmentedGraph = new AugmentedModelGraph(
				SimulinkModelGraphCreator.createModelGraph(input, normalizer,
						ignoreSet));
		int graphSize = augmentedGraph.getNodes().size();

		for (ISimulinkPreprocessor preprocessor : preprocessors) {
			preprocessor.preprocess(input, normalizer, ignoreSet, this);
		}

		if (detector != null) {
			if (!ignoreSet.isEmpty()) {
				// regenerate graph if ignore set changed
				augmentedGraph = new AugmentedModelGraph(
						SimulinkModelGraphCreator.createModelGraph(input,
								normalizer, ignoreSet));
			}
			detector.detect(augmentedGraph, this, getLogger());
		}

		dumpStatistics(graphSize);

		return result;
	}

	/** Dumps some statistics on clone size and coverage to the log. */
	private void dumpStatistics(int graphSize) {
		if (!result.hasChildren()) {
			getLogger().info("No clones found!");
		} else {
			SimulinkClone[] children = result.getChildren();
			getLogger().info("Found " + children.length + " clone groups");

			Set<SimulinkBlock> covered = new IdentityHashSet<SimulinkBlock>();
			int sizeSum = 0;
			int sizeMax = 0;
			int cardSum = 0;
			int cardMax = 0;

			for (SimulinkClone group : children) {
				UnmodifiableList<UnmodifiableList<SimulinkBlock>> blockLists = group
						.getBlockLists();
				cardSum += blockLists.size();
				cardMax = Math.max(cardMax, blockLists.size());
				sizeSum += blockLists.get(0).size();
				sizeMax = Math.max(sizeMax, blockLists.get(0).size());

				for (UnmodifiableList<SimulinkBlock> nodes : blockLists) {
					covered.addAll(nodes);
				}
			}

			getLogger().info("Largest group has " + cardMax + " instances");
			getLogger().info(
					"Average group has " + cardSum / (double) children.length
							+ " instances");
			getLogger().info("Largest instance has " + sizeMax + " nodes");
			getLogger().info(
					"Average instance has " + sizeSum
							/ (double) children.length + " nodes");

			getLogger().info(
					"Covered "
							+ covered.size()
							+ " of "
							+ graphSize
							+ " nodes ("
							+ StringUtils.formatAsPercentage(covered.size()
									/ (double) graphSize) + ")");
		}
	}

	/** {@inheritDoc} */
	@Override
	public void startModelCloneGroup(int numClones, int numNodes, int numEdges) {
		currentClone = new SimulinkClone(cloneCounter++);
		currentClone.setValue(MODELS_KEY, new TreeSet<String>());
		result.addChild(currentClone);

		expectedInstances = numClones;

		for (IModelCloneMetric metric : metrics) {
			metric.startCloneGroup(augmentedGraph);
		}
	}

	/** {@inheritDoc} */
	@Override
	@SuppressWarnings("unchecked")
	public void addModelCloneInstance(List<INode> nodes,
			List<IDirectedEdge> edges) {
		List<SimulinkBlock> blocks = nodesToBlocks(nodes);
		List<SimulinkLine> lines = edgesToLines(edges);
		currentClone.addBlocksLinesPair(blocks, lines);
		((Set<String>) currentClone.getValue(MODELS_KEY)).add(blocks.get(0)
				.getModel().getName());

		for (IModelCloneMetric metric : metrics) {
			metric.addCloneInstance(nodes, edges);
		}

		expectedInstances -= 1;
		if (expectedInstances == 0) {
			for (IModelCloneMetric metric : metrics) {
				currentClone.setValue(metric.getName(),
						metric.calculateMetricValue());
			}
		}
	}

	/**
	 * Converts a list of {@link SimulinkNode}s as returned from the detection
	 * (i.e. as {@link INode}s) into the corresponding {@link SimulinkBlock}
	 * list.
	 */
	private List<SimulinkBlock> nodesToBlocks(List<INode> nodes) {
		List<SimulinkBlock> blocks = new ArrayList<SimulinkBlock>();
		for (INode node : nodes) {
			CCSMAssert.isTrue(node instanceof SimulinkNode,
					"Detector returned unknown nodes!");
			SimulinkNode snode = (SimulinkNode) node;
			blocks.add(snode.getBlock());
		}
		return blocks;
	}

	/**
	 * Converts a list of {@link SimulinkLine}s as returned from the detection
	 * (i.e. as {@link IDirectedEdge}s) into the corresponding
	 * {@link SimulinkLine} list.
	 */
	private List<SimulinkLine> edgesToLines(List<IDirectedEdge> edges) {
		List<SimulinkLine> lines = new ArrayList<SimulinkLine>();
		for (IDirectedEdge edge : edges) {
			CCSMAssert.isTrue(edge instanceof SimulinkDirectedEdge,
					"Detector returned unknown edges!");
			SimulinkDirectedEdge sedge = (SimulinkDirectedEdge) edge;
			lines.add(sedge.getLine());
		}
		return lines;
	}
}