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
package org.conqat.engine.html_presentation.image;

import java.awt.geom.Rectangle2D;
import java.util.List;

import org.conqat.engine.commons.node.DisplayList;

/**
 * This interface can be used to equip {@link IImageDescriptor} with tooltips.
 * This interface assumes that the tool tips describe a tree-like a data
 * structure.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: CE656A0D874046B7558EF8E60E4D9739
 */
public interface ITooltipDescriptor<T> {

	/** Check if node has children. */
	boolean hasChildren(T node);

	/** Obtain a node's children. */
	List<T> obtainChildren(T node);

	/** Obtain bounds for a node. */
	Rectangle2D obtainBounds(T node);

	/** Obtain id for a node. */
	String obtainId(T node);

	/** Obtain the value stored for a key at a node. */
	Object obtainValue(T node, String key);

	/** Get root. */
	T getRoot();

	/** Get keys to display. */
	DisplayList getDisplayList();

	/**
	 * Determines if tool tips are created for inner nodes. If this is false,
	 * tool tips are created for leave nodes only.
	 */
	boolean isTooltipsForInnerNodes();
}