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
 * Annotation for formal method parameters accessible via the configuration
 * file. All parameters of exported methods must be annotated with this
 * annotation to make the mapping from attributes to formal method parameters
 * possible.
 * <p>
 * Details on the annoations used for ConQAT can be found in the package
 * documentation {@link org.conqat.engine.core.core} and the <a
 * href="http://conqat.cs.tum.edu">ConQAT manual</a>.
 * 
 * @author Benjamin Hummel
 * @author Lukas Kuhn
 * @author Elmar Juergens
 * @author $Author: kinnen $
 * 
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: EA0183A7F4502BBC3A96A93C689A9D0B
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface AConQATAttribute {
	/**
	 * The name as it appears in the configuration XML file as an attribute
	 * name.
	 */
	String name();

	/**
	 * The default value for this formal method parameter used if no value is
	 * given in the configuration file.
	 * <p>
	 * If the default value is omitted (set to the empty string), the
	 * parameter/attribute is required. In order to use an empty string as
	 * default, use a string containing a single space or something similar
	 * depending on the type of the parameter. Note that the default is
	 * interpreted using the same rules as an immediate attribute value would
	 * be.
	 */
	String defaultValue() default "";

	/** A description of this element, used e.g. for user documentation. */
	String description();
}