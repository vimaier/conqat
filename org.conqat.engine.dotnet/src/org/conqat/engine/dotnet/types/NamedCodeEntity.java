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
package org.conqat.engine.dotnet.types;

import org.conqat.lib.scanner.ETokenType;

/**
 * Named code entity, such as a type, interface or class.
 * 
 * @author $Author: goede $
 * @version $Rev: 39906 $
 * @ConQAT.Rating GREEN Hash: 1AB347E6AD670BC87B4D7C1F8F6EDC4B
 */
public class NamedCodeEntity extends CodeEntityBase {

	/** Name of the named code entity */
	private final String fqName;

	/** Keyword starting its declaration */
	private final ETokenType type;

	/** Constructor */
	public NamedCodeEntity(String fqName, ETokenType type, String childSeparator) {
		super(childSeparator);
		this.fqName = fqName;
		this.type = type;
	}

	/** Returns type. */
	public ETokenType getType() {
		return type;
	}

	/** Get fully qualified name */
	@Override
	public String getFqName() {
		return fqName;
	}

}
