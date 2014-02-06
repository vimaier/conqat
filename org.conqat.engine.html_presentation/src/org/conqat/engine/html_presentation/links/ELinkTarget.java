/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
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
package org.conqat.engine.html_presentation.links;

/**
 * Enumeration of link target attribute values.
 * 
 * @author $Author: juergens $
 * @version $Rev: 42168 $
 * @ConQAT.Rating GREEN Hash: A3EBFE5078CB4CCD49020173172FFA4C
 */
public enum ELinkTarget {
	/** Target reference for the same frame. */
	SELF("_self"),

	/** Target reference for a new tab or window. */
	BLANK("_blank"),

	/** Target reference for the parent frame. */
	PARENT("_parent"),

	/** Target reference for the top frame. */
	TOP("_top");

	/** The attribute value of the target. */
	private final String value;

	/** Constructor. */
	private ELinkTarget(String name) {
		this.value = name;
	}

	/** Returns the value as used in the HTML target attribute. */
	public String getValue() {
		return value;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return value;
	}
}