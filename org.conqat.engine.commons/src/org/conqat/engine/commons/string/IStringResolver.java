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
package org.conqat.engine.commons.string;

import org.conqat.engine.core.core.ConQATException;

/**
 * Interface for resolving or manipulating strings.
 * 
 * @author $Author: $
 * @version $Rev: $
 * @ConQAT.Rating YELLOW Hash: A439C04F43147DC962E5F1DE67B9C298
 */
public interface IStringResolver {

	/** Executes the resolver for a given string. */
	public abstract String resolve(String string) throws ConQATException;

}
