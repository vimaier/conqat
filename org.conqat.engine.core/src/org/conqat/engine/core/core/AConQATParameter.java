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
 * Annotation for methods accessible via the configuration file. All formal
 * parameters for these methods must be annotated with
 * {@link org.conqat.engine.core.core.AConQATAttribute}.
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
 * @ConQAT.Rating GREEN Hash: 143E44A486EF738BDEB913858760590B
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AConQATParameter {

	/** The value indicating an unlimited number of occurrences. */
	public static final int UNLIMITED = -1;

	/** The name of the sub element as it appears in the XML file. */
	String name();

	/**
	 * The minimal required number of occurences of this subelement. Defaults to
	 * 0, i.e. optional.
	 */
	int minOccurrences() default 0;

	/**
	 * The maximal allowed number of occurences of this subelement. Negative
	 * values indicate no limit. Defaults to {@link #UNLIMITED}.
	 */
	int maxOccurrences() default UNLIMITED;

	/** A description of this element, used e.g. for user documentation. */
	String description();
}