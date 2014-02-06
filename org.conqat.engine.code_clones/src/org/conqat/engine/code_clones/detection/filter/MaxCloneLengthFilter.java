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
package org.conqat.engine.code_clones.detection.filter;

import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * Filters out clone classes that are too long
 * 
 * @author juergens
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: 45476D12036240B98503C66B46422EE9
 */
@AConQATProcessor(description = "Filters out clone classes that are too long")
public class MaxCloneLengthFilter extends CloneClassFilterBase {

	/** All clones that are longer are discarded by this filter */
	private int maxCloneLength;

	/** ConQAT Parameter */
	@AConQATParameter(name = "max", description = "Maximal length of a clone", minOccurrences = 1, maxOccurrences = 1)
	public void setMaxCloneLength(
			@AConQATAttribute(name = "length", description = "Maximal length of a clone") int maxCloneLength) {
		this.maxCloneLength = maxCloneLength;
	}

	/**
	 * Filters out all clone classes with a normalized length bigger than
	 * maxCloneLength
	 */
	@Override
	protected boolean filteredOut(CloneClass cloneClass) {
		return cloneClass.getNormalizedLength() > maxCloneLength;
	}

}