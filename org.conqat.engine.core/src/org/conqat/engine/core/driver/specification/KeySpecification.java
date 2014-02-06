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
package org.conqat.engine.core.driver.specification;

import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.driver.util.IDocumented;

/**
 * Specification of keys read or written by a processor. This is used to make
 * the {@link AConQATKey} information available.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: CDCE16BA39D487972C1B7F1086DDAD73
 */
public class KeySpecification implements IDocumented {

	/** Name of the key. */
	private final String name;

	/** Documentation for this key. */
	private final String doc;

	/** Type of this key. */
	private final String type;

	/** Create new KeySpecification. */
	public KeySpecification(String name, String type, String description) {
		this.name = name;
		this.type = type;
		doc = description;
	}

	/** Returns the documentation of this key. */
	@Override
	public String getDoc() {
		return doc;
	}

	/** Returns the name of this key. */
	public String getName() {
		return name;
	}

	/** Returns the type of this key. */
	public String getType() {
		return type;
	}

}