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
 * Interface for a shut down hook which can be executed after all processors
 * have been executed. This can be used for resource cleanup.
 * 
 * @author hummelb
 * @author $Author: juergens $
 * @version $Rev: 35194 $
 * @ConQAT.Rating GREEN Hash: E9A59B9A1D83D6E02D1BAE0ACC417448
 */
public interface IShutdownHook {

	/**
	 * Performs the actual shut down of the processor. During this operation all
	 * resources of the processor, such as loggers, may be used.
	 */
	void performShutdown() throws ConQATException;
}