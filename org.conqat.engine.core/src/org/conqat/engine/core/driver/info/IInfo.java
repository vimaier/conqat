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
package org.conqat.engine.core.driver.info;

import java.util.List;

import org.conqat.engine.core.driver.instance.EInstanceState;
import org.conqat.engine.core.driver.instance.IInstance;
import org.conqat.lib.commons.collections.UnmodifiableList;
import org.conqat.lib.commons.collections.UnmodifiableMap;

/**
 * An info instance is used to present information on an {@link IInstance} to
 * the user, while hiding (potentially sensitive) internals.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 2F7193416537BE2B57B8F7DE0CE8618C
 */
public interface IInfo {

	/**
	 * Returns the full hierarchical name of the instance, which contains the
	 * names of all parent blocks.
	 */
	public String getInstanceName();

	/**
	 * Returns the name used in the declaration, which is exactly the name found
	 * in the XML file.
	 */
	public String getDeclarationName();

	/** Returns the name of the specification. */
	public String getSpecificationName();

	/**
	 * Returns the parent of this info object, or null if this is already the
	 * topmost object.
	 */
	public BlockInfo getParent();

	/**
	 * Returns the ordered list of parameters, as they are applied to this
	 * object.
	 */
	public UnmodifiableList<InfoParameter> getParameters();

	/**
	 * Returns the ordered list of non-synthetic parameters, as they are applied
	 * to this object.
	 */
	public List<InfoParameter> getNonsyntheticParameters();

	/** Returns the ordered list of outputs for this object. */
	public UnmodifiableList<InfoOutput> getOutputs();

	/**
	 * Returns the distribution of the states of the leaf instances (i.e.
	 * processors).
	 */
	public UnmodifiableMap<EInstanceState, Integer> getProcessorStateDistribution();

	/**
	 * Returns the state of the processor or block. For blocks this is the most
	 * dominant state of all childrens states.
	 */
	public EInstanceState getState();

	/**
	 * Returns the required execution time in milliseconds. If this has not been
	 * run yet, it will be 0.
	 */
	public long getExecutionTime();
}