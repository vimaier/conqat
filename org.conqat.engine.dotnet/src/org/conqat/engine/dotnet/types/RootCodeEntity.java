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

import org.conqat.lib.commons.string.StringUtils;

/**
 * Root node in code entities hierarchy.
 * 
 * @author $Author: goede $
 * @version $Rev: 40970 $
 * @ConQAT.Rating GREEN Hash: EDD54F88037A4EF673C8B9D0D1812C06
 */
public class RootCodeEntity extends CodeEntityBase {

	/** Constructor */
	public RootCodeEntity() {
		super(StringUtils.EMPTY_STRING);
	}

}