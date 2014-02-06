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
 * This annotation is a marker annotation, so that the driver can find the
 * source(s) of a pipeline processor. A pipeline processor is a processor whose
 * output type depends on the type of one of its inputs used (e.g. most
 * aggregators will return exactly their input). The contract is that the
 * processor returns one of the inputs provided via pipeline attributes. If no
 * pipeline attributes are provided (e.g. optional pipeline attribute) the
 * processor must fail (i.e. throw an exception). More on pipeline processors is
 * in the ConQAT manual.
 * <p>
 * For a formal method parameter to be marked with this annotation, the
 * following limitations apply:
 * <ul>
 * <li>The type of the formal method parameter must be exactly the same as the
 * return type of the processor's process method</li>
 * <li>The annotated formal method parameter must be in a method annotated with
 * {@link AConQATParameter}</li>
 * <li>The {@link AConQATAttribute} annotation for this parameter must not
 * specify a defaultValue</li>
 * </ul>
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 94881B582E5D95D6E493F9E4ECD02460
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface APipelineSource {
	// This is just a marker annotation
}