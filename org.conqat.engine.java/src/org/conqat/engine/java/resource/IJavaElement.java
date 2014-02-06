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

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.resource.ITokenElement;

/**
 * Element representing a Java type.
 * 
 * @author $Author: juergens $
 * @version $Rev: 35196 $
 * @ConQAT.Rating GREEN Hash: 02FD3B854BA93FA3574D0ED9EE7C27CA
 */
public interface IJavaElement extends ITokenElement, IJavaResource {

	/** Returns the byte-code */
	byte[] getByteCode() throws ConQATException;

	/** Returns the (platform dependent) location of the byte code. */
	String getByteCodeLocation();

	/** Returns the uniform path of the byte code. */
	String getByteCodeUniformPath();

	/** Returns the Java context for the project this file belongs to. */
	JavaContext getJavaContext();

	/** Returns the name of the represented class. */
	String getClassName();
}