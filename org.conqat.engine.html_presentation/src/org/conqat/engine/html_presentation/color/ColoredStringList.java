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
package org.conqat.engine.html_presentation.color;

import java.awt.Color;

import org.conqat.lib.commons.collections.PairList;

/**
 * A list of strings with color assigned. This is made an dedicated class to
 * allow registration of formatters.
 * 
 * @author $Author: heineman $
 * @version $Rev: 41618 $
 * @ConQAT.Rating GREEN Hash: 1D98E53FBAC0CE7C16DDA54E11B448E7
 */
public class ColoredStringList extends PairList<String, Color> {

	/** Version for serialization. */
	private static final long serialVersionUID = 1;
}
