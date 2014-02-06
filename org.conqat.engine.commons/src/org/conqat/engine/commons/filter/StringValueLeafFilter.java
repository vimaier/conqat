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
package org.conqat.engine.commons.filter;

import org.conqat.engine.commons.node.IRemovableConQATNode;
import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author Elmar Juergens
 * @author $Author: juergens $
 * @version $Rev: 34549 $
 * @ConQAT.Rating GREEN Hash: AE22F9643C6180E4F1F64B99BF14966E
 */
@AConQATProcessor(description = "This filter removes all leaf nodes based on a value stored in them. The node "
		+ "value is matched against a pattern list to determine which nodes are "
		+ "filtered. Since pattern matching is performed on strings, the value is converted to "
		+ "string, if necessary.")
public class StringValueLeafFilter extends
		KeyBasedFilterBase<Object, IRemovableConQATNode> {

	/** The value used for filtering */
	private PatternList filterPatterns;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "filter", minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "Pattern list defining which patterns are filtered")
	public void setFilterPatterns(
			@AConQATAttribute(name = "patterns", description = "Reference to processor that procuces pattern list") PatternList filterPatterns) {
		this.filterPatterns = filterPatterns;
	}

	/** {@inheritDoc} */
	@Override
	protected boolean isFilteredForValue(Object value) {
		String valueString = value.toString();
		return filterPatterns.matchesAny(valueString);
	}

	/** Restrict filtering to leaf nodes */
	@Override
	protected boolean isFiltered(IRemovableConQATNode node) {
		if (node.hasChildren()) {
			return false;
		}
		return super.isFiltered(node);
	}

}