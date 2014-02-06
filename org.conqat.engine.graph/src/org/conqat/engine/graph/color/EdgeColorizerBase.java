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
package org.conqat.engine.graph.color;

import java.awt.Color;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.graph.nodes.ConQATGraph;
import org.conqat.engine.graph.nodes.DeepCloneCopyAction;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;

/**
 * Base class for processors assigning colors to edges.
 * 
 * @author Benjamin Hummel
 * @author $Author: deissenb $
 * @version $Rev: 35147 $
 * @ConQAT.Rating GREEN Hash: 27412AA4F273FE1E62A94CCE0BE464AE
 * 
 * @param <E>
 *            the type expected to be read.
 */
public abstract class EdgeColorizerBase<E> extends
		ConQATPipelineProcessorBase<ConQATGraph> {

	/** Default value used for the color key. */
	private static final String COLOR_KEY_DEFAULT = "color";

	/** The key used for writing the color. */
	private String colorKey = COLOR_KEY_DEFAULT;

	/** The key used for reading. */
	private String readKey;

	/** Set size key. */
	@AConQATParameter(name = ConQATParamDoc.READKEY_NAME, minOccurrences = 1, maxOccurrences = 1, description = ConQATParamDoc.READKEY_DESC)
	public void setReadKey(
			@AConQATAttribute(name = ConQATParamDoc.READKEY_KEY_NAME, description = ConQATParamDoc.READKEY_KEY_DESC)
			String key) {

		this.readKey = key;
	}

	/** Set size key. */
	@AConQATParameter(name = COLOR_KEY_DEFAULT, minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "The key to write the color into. The default is to write into '"
			+ COLOR_KEY_DEFAULT + "'.")
	public void setColorKey(
			@AConQATAttribute(name = ConQATParamDoc.WRITEKEY_KEY_NAME, description = ConQATParamDoc.WRITEKEY_KEY_DESC)
			String key) {

		this.colorKey = key;
	}

	/** {@inheritDoc} */
	@Override
	@SuppressWarnings("unchecked")
	protected void processInput(ConQATGraph graph) {
		for (DirectedSparseEdge edge : graph.getEdges()) {
			Object value = edge.getUserDatum(readKey);
			if (value != null) {
				try {
					Color color = determineColor((E) value);
					if (color != null) {
						edge.setUserDatum(colorKey, color, DeepCloneCopyAction
								.getInstance());
					}
				} catch (ClassCastException e) {
					getLogger().warn(
							"Unexpected type in key " + readKey + " at edge!");
				}
			}
		}
	}

	/** Returns the color for the given value. */
	protected abstract Color determineColor(E value);
}