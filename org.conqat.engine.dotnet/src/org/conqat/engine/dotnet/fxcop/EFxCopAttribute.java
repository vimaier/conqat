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
 * The relevant XML attributes for FxCop reports.
 * 
 * @author herrmama
 * @author $Author: poehlmann $
 * @version $Rev: 44418 $
 * @ConQAT.Rating YELLOW Hash: 2003572D5D830AE31065936DC7A8BC2B
 */
/* package */enum EFxCopAttribute {

	/** Path */
	Path,

	/** File */
	File,

	/** Rule name */
	TypeName,

	/** Rule CheckID */
	CheckId,

	/** Code line */
	Line,

	/** Name (used for multiple elements) */
	Name,

}