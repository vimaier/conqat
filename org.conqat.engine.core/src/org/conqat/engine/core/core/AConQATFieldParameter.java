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
 * Annotation for fields that are exposed to the configuration. Internally,
 * fields that are annotated with this are made available as a parameter with a
 * single attribute.
 * 
 * @author Benjamin Hummel
 * @author $Author: juergens $
 * 
 * @version $Rev: 35194 $
 * @ConQAT.Rating GREEN Hash: 18CEE201284D3C5C5D70BFBAEFCD8313
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AConQATFieldParameter {

	/** The parameter name of the element. */
	String parameter();

	/** The attribute name of the element. */
	String attribute();

	/**
	 * Marks if this field is optional, i.e. has multiplicity [0,1] instead of
	 * [1,1].
	 */
	boolean optional() default false;

	/** A description of this element, used e.g. for user documentation. */
	String description();
}