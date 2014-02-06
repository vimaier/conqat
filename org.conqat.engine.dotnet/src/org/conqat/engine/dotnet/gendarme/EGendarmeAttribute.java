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
package org.conqat.engine.dotnet.gendarme;

/**
 * XML attributes of the Gendarme report.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: C737E394E7E97BE6426B6359A53E59CC
 */
public enum EGendarmeAttribute {

	/** Name attribute is used for different elements. */
	Name,

	/**
	 * Source attribute at {@link EGendarmeElement#defect} describes the source
	 * location.
	 */
	Source,

	/**
	 * Location attribute at {@link EGendarmeElement#defect} describes the
	 * location of the finding using the type and method names.
	 */
	Location

}