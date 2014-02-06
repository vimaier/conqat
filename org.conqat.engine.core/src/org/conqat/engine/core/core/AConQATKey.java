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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for string constants used as keys. May only be used only on static
 * final fields of type <code>String</code>. This annotation is evaluated for
 * documentation generation, so the user of a processor knows the names of the
 * relevant keys for this processor.
 * <p>
 * Details on the annoations used for ConQAT can be found in the package
 * documentation {@link org.conqat.engine.core.core} and the <a
 * href="http://conqat.cs.tum.edu">ConQAT manual</a>.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * 
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 8B90E4FE792C351E42B56D2C779571B7
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AConQATKey {

	/**
	 * Description of this key.
	 */
	String description();

	/**
	 * Type of objects stored under this key. The type is implemented as String
	 * in order to be able to express generic types.
	 */
	String type();
}