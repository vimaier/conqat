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
package org.conqat.engine.core.driver.instance;

import java.util.List;

import org.conqat.engine.core.driver.ConQATInstrumentation;
import org.conqat.engine.core.driver.declaration.IDeclaration;

/**
 * This interfaces describes the actual instances of declarations. As a
 * declaration can be part of a (block) specification, which in turn can be
 * referenced multiple times, many instances can be constructed from the same
 * (sub)declaration.
 * <p>
 * Furthermore, instances are organized strictly hierarchically.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 660DBD4E6C473A57918D56795E164607
 */
public interface IInstance {

	/**
	 * Returns the name of this instance, which is the fully qualified name in
	 * the hierarchy where the individual local names are separated by dots.
	 */
	public String getName();

	/**
	 * Returns the parent in the hierarchy or <code>null</code> if this is the
	 * root instance.
	 */
	public IInstance getParent();

	/** Returns the declaration to which this instance belongs. */
	public IDeclaration getDeclaration();

	/** Returns the ordered list of outputs for this instance. */
	public List<InstanceOutput> getOutputs();

	/** Returns the ordered list of parameters for this instance. */
	public List<InstanceParameter> getParameters();

	/** Executes this instance. */
	public void execute(ExecutionContext contextInfo,
			ConQATInstrumentation instrumentation);

	/**
	 * Disables the instance, i.e. marks it with the given state and frees all
	 * possible resources.
	 * 
	 * @param disablementState
	 *            this should be one of {@link EInstanceState#DISABLED} or
	 *            {@link EInstanceState#FAILED_DUE_TO_MISSING_INPUT}
	 */
	public void disable(EInstanceState disablementState);
}