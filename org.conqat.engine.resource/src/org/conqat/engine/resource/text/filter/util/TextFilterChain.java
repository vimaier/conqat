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
package org.conqat.engine.resource.text.filter.util;

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.filter.base.Deletion;
import org.conqat.engine.resource.text.filter.base.ITextFilter;

/**
 * A list of filters that supports the application of all filters in order. For
 * this the filters are applied in the order that is implied by the list order.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 44513 $
 * @ConQAT.Rating GREEN Hash: CCBC6E2438C328E19B26D01FC8ED7EDD
 */
public class TextFilterChain extends ArrayList<ITextFilter> implements
		ITextFilter {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;
	
	/** Constructor that adds the given filters to the chain. */
	public TextFilterChain(ITextFilter... filters) {
		for (ITextFilter filter : filters) {
			add(filter);
		}
	}

	/** {@inheritDoc} */
	@Override
	public List<Deletion> getDeletions(String string, String originUniformPath)
			throws ConQATException {
		if (isEmpty()) {
			return new ArrayList<Deletion>();
		}

		List<Deletion> deletions = get(0).getDeletions(string,
				originUniformPath);
		for (int i = 1; i < size(); ++i) {
			StringOffsetTransformer transformer = new StringOffsetTransformer(
					deletions);
			string = transformer.filterString(string);
			List<Deletion> filteredDeletions = get(i).getDeletions(string,
					originUniformPath);
			deletions = mergeDeletions(deletions, transformer,
					filteredDeletions);
		}

		return deletions;
	}

	/**
	 * Merges filtered deletions into given target deletions, but applies
	 * transformation to filtered offsets first.
	 */
	private List<Deletion> mergeDeletions(List<Deletion> target,
			StringOffsetTransformer transformer,
			List<Deletion> filteredDeletions) {
		for (Deletion filteredDeletion : filteredDeletions) {
			int startOffset = transformer.getUnfilteredOffset(filteredDeletion
					.getStartOffset());
			int endOffset = transformer.getUnfilteredOffset(filteredDeletion
					.getEndOffset());
			target.add(new Deletion(startOffset, endOffset, filteredDeletion
					.isGap()));
		}
		return Deletion.compactDeletions(target);
	}
}