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
package org.conqat.engine.core.bundle;

import org.conqat.lib.commons.xml.XMLReader;

/**
 * Element definition enumeration for {@link BundleDescriptorReader}. See
 * documentation of {@link XMLReader} for detailed explanation of this XML
 * handling mechanism.
 * <p>
 * This enumeration deliberately violates the style convention by having lower
 * case enum elements, in order to make the link to XML elements as direct as
 * possible.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: DC0DF22C528D220D63025171BBD52D10
 */
public enum EBundleXMLElement {
	/** Element for bundle version. */
	version,

	/** Element for bundle provider. */
	provider,

	/** Element for bundle dependency. */
	dependsOn,

	/** Element for bundle name. */
	name,

	/** Element for bundle description. */
	description,

	/** Element for required version of ConQAT core. */
	requiresCore

}