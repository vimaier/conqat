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
package org.conqat.engine.java.resource;

import org.conqat.engine.sourcecode.resource.ITokenResource;

/**
 * Resource for Java.
 * 
 * @author $Author: juergens $
 * @version $Rev: 35196 $
 * @ConQAT.Rating GREEN Hash: 835392D24AA790055EF179535B1063BE
 */
public interface IJavaResource extends ITokenResource {

	/** {@inheritDoc} */
	@Override
	public IJavaResource[] getChildren();
}