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
package org.conqat.engine.abap.nugget;

/**
 * Elements contained in ABAP nugget files.
 * 
 * @author herrmama
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: E4A1BDCCA4F975DFBF6AD66BBD324619
 */
public enum ENuggetElementType {

	/** Classes */
	CLAS,

	/** Function groups */
	FUGR,

	/** Message groups */
	MSAG,

	/** Programs */
	PROG,

	/** Web DynPros */
	WDYN
}