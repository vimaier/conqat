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
package org.conqat.engine.commons.collections;

import java.util.Collection;

import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: pfaller $
 * @version $Rev: 37401 $
 * @ConQAT.Rating GREEN Hash: E8E322346C0AF805A754E95E03C5711F
 */
@AConQATProcessor(description = "A simple processor that creates a dummy ConQATNode and attaches "
		+ "the size of a collection as value.")
public class CollectionSizeProcessor extends SizeProcessorBase<Collection<?>> {

	/** {@inheritDoc} */
	@Override
	protected int determineSize(Collection<?> input) {
		return input.size();
	}
}