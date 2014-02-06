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
package org.conqat.engine.model_clones.detection.util;

import java.util.List;

import org.conqat.engine.model_clones.model.IDirectedEdge;
import org.conqat.engine.model_clones.model.INode;

/**
 * Interface for a class which is used for reporting clones. Clones are reported
 * in two steps. First a call to {@link #startModelCloneGroup(int, int, int)}
 * begins a new clone of size <code>numClones</code>. Then
 * <code>numClones</code> calls of {@link #addModelCloneInstance(List, List)}
 * report the node/edge sets of the clone instances.
 * 
 * @author hummelb
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: 8B8DEFB505739865E38D03B9EA2CBF34
 */
public interface ICloneReporter {

	/**
	 * Starts the "transmission" of a model clone class.
	 * 
	 * @param numClones
	 *            the number of clone instances being transmitted. This must be
	 *            at least 2.
	 * @param numNodes
	 *            the number of nodes each of those clones has.
	 * @param numEdges
	 *            the number of edges each of those clones has.
	 */
	void startModelCloneGroup(int numClones, int numNodes, int numEdges);

	/** Adds a clone instance to the currently reported clone class. */
	void addModelCloneInstance(List<INode> nodes, List<IDirectedEdge> edges);
}