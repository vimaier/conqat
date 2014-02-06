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
package org.conqat.engine.dotnet.fxcop;

/**
 * The relevant XML elements for FxCop reports.
 * 
 * @author herrmama
 * @author $Author: juergens $
 * @version $Rev: 38444 $
 * @ConQAT.Rating GREEN Hash: 6F8A0F8F921E4FD87F98048A668170A5
 */
/* package */enum EFxCopElement {

	/** Rule */
	Rule,

	/** Rule description */
	Description,

	/** Message */
	Message,

	/** Issue */
	Issue, 
	
	/** Namespace */
	Namespace, 
	
	/** Type */	
	Type
}