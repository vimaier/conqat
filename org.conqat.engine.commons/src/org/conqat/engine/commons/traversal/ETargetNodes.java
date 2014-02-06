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
package org.conqat.engine.commons.traversal;

/**
 * Enum for node targets in a tree. To work with these it is best to use
 * {@link org.conqat.engine.commons.traversal.TraversalUtils} instead of
 * handling it yourself, as this class is updated whenever new targets are added
 * to this enum.
 * 
 * @author Benjamin Hummel
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: D73D5CA30320BFE0295CBAC605398026
 */
public enum ETargetNodes {
	/** All nodes. */
	ALL,

	/** Only the root node. */
	ROOT,

	/** Only leaves ,i.e. nodes without children. */
	LEAVES,

	/** Inner nodes, i.e. nodes having children. */
	INNER;
}