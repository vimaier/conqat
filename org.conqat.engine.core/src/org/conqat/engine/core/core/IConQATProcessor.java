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
package org.conqat.engine.core.core;

/**
 * The interface for ConQAT processors.
 * 
 * @author Benjamin Hummel
 * @author Lukas D. Kuhn
 * @author $Author: kinnen $
 * 
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 929B97A15A2277CDA28A5F6A159DA5B5
 */
public interface IConQATProcessor {

	/**
	 * Initialization of the processor. The processor will receive information
	 * on itself and its execution context via this function.
	 * <p>
	 * This method is guaranteed to be called exactly once for each processor
	 * object, and before any other method is called on it. This way the
	 * processor can use this {@link IConQATProcessorInfo} in its setters
	 * already.
	 * 
	 * @param processorInfo
	 *            information about the runtime context in which the processor
	 *            is executed.
	 */
	public void init(IConQATProcessorInfo processorInfo) throws ConQATException;

	/**
	 * Perform processing on the parameters provided earlier and return the
	 * result. You should use covariance to supply a more specialized return
	 * type in your implementation, as the return type is used for type checking
	 * when using the result of this processor as input to another processor.
	 * <p>
	 * Most processors will accept additional parameters. To make these visible
	 * from the XML configuration file, the driver requires hints in the form of
	 * annotations. Details on the available annotations and their usage can be
	 * found in the package documentation: {@link org.conqat.engine.core.core}
	 * 
	 * @return the result from this processor's execution.
	 * @throws ConQATException
	 *             if something goes wrong.
	 */
	public Object process() throws ConQATException;
}