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
package org.conqat.engine.sourcecode.shallowparser.framework;

/**
 * Visitor interface for traversal of entities.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46381 $
 * @ConQAT.Rating GREEN Hash: 83BE22FCBCF3390DCD14C650DB289A5E
 */
public interface IShallowEntityVisitor {

	/**
	 * Denotes that visiting the entity begins.
	 * 
	 * @return true if the children of this entity are to be visited as well.
	 */
	boolean visit(ShallowEntity entity);

	/**
	 * Called after visiting all children of an entity in the backtracking phase
	 * of the DFS. This is called regardless of whether
	 * {@link #visit(ShallowEntity)} returned true or false.
	 */
	void endVisit(ShallowEntity entity);
}