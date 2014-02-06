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
package org.conqat.engine.html_presentation.seesoft;

import java.awt.Rectangle;

import org.conqat.engine.commons.node.IConQATNode;

/**
 * A tile in a SeeSoft image representing (part of) a ConQAT node.
 * 
 * @author $Author: juergens $
 * @version $Rev: 41802 $
 * @ConQAT.Rating GREEN Hash: 8D15511C3858DA2D77EE00C43E9C4F1F
 */
public class SeeSoftTile {

	/** The node this tile represents. */
	private IConQATNode node;

	/** The position, width, and height of the tile. */
	private Rectangle bounds;

	/** Creates a tile for the node in the given bounds. */
	public SeeSoftTile(IConQATNode node, Rectangle bounds) {
		this.node = node;
		this.bounds = bounds;
	}

	/** Returns the node this tile represents. */
	public IConQATNode getNode() {
		return node;
	}

	/** Returns the bounds of the tile. */
	public Rectangle getBounds() {
		return bounds;
	}
}
