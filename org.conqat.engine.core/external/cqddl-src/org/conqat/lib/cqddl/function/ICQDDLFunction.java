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
package org.conqat.lib.cqddl.function;

import org.conqat.lib.commons.collections.PairList;

/**
 * An interface of a function working with CQDDL. Thus the function gets all
 * parameters as key/value pairs in a {@link PairList}.
 * 
 * @author hummelb
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating YELLOW Hash: F64DA22BBF7A1151BD61A8A648A251EA
 */
public interface ICQDDLFunction {

	/** Evalutes this function using the provided parameters. */
	Object eval(PairList<String, Object> parms) throws CQDDLEvaluationException;
}