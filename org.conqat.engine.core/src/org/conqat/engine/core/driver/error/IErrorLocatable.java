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
package org.conqat.engine.core.driver.error;

/**
 * Interface that indicates that a {@link ErrorLocation} object can be
 * retrieved.
 * 
 * @author Elmar Juergens
 * @author $Author: juergens $
 * @version $Rev: 35194 $
 * @ConQAT.Rating GREEN Hash: 2F8AA62104557EC2DAF7EF51EC3EF147
 */
public interface IErrorLocatable {

	/** Retrieve location object */
	public ErrorLocation getErrorLocation();

}