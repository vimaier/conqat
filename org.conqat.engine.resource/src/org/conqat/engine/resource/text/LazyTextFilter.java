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
package org.conqat.engine.resource.text;

import java.util.List;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.filter.base.ITextFilter;
import org.conqat.engine.resource.text.filter.util.StringOffsetTransformer;
import org.conqat.lib.commons.region.Region;

/**
 * Class that encapsulated an {@link ITextFilter} and a
 * {@link StringOffsetTransformer}. The transformer is initialized lazily, as we
 * might not even need filtering during a ConQAT run. All filtering operations
 * are delegated to the transformer.
 * 
 * This is located next to the TextElement to make it package visible.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46849 $
 * @ConQAT.Rating GREEN Hash: C4A05C94365DA35F1B68F51464D2D7AF
 */
/* package */class LazyTextFilter {

	/** The filter used to initialize the transformer. */
	/* package */final ITextFilter filter;

	/** The offset transformer, which is initialized lazily. */
	private StringOffsetTransformer transformer;

	/** Constructor. */
	public LazyTextFilter(ITextFilter filter) {
		this.filter = filter;
	}

	/** Returns the string after transformation. */
	public String filterString(ITextElement element) throws ConQATException {
		String string = ensureTransformerInitialized(element);

		// avoid duplicate text access
		if (string == null) {
			string = element.getUnfilteredTextContent();
		}

		return transformer.filterString(string);
	}

	/** Converts a filtered offset to an unfiltered offset. */
	public int getUnfilteredOffset(int offset, ITextElement element)
			throws ConQATException {
		ensureTransformerInitialized(element);
		return transformer.getUnfilteredOffset(offset);
	}

	/**
	 * Converts an unfiltered offset to a filtered offset. If the offset was
	 * filtered, the first offset after the filtered region is returned (which
	 * might be one after the last offset in the filtered string, if the
	 * provided offset in a filtered tail region).
	 */
	public int getFilteredOffset(int unfilteredOffset, ITextElement element)
			throws ConQATException {
		ensureTransformerInitialized(element);
		return transformer.getFilteredOffset(unfilteredOffset);
	}

	/** Returns whether an offset is contained in a deleted region.. */
	public boolean isFilteredOffset(int offset, ITextElement element)
			throws ConQATException {
		ensureTransformerInitialized(element);
		return transformer.isFilteredOffset(offset);
	}

	/**
	 * Returns whether there is a filtering gap between the two offsets. The
	 * first offset must be strictly smaller than the second offset. Both
	 * offsets should be "filtered offsets".
	 */
	public boolean isFilterGapBetween(int firstOffset, int secondOffset,
			ITextElement element) throws ConQATException {
		ensureTransformerInitialized(element);
		return transformer.isFilterGapBetween(firstOffset, secondOffset);
	}

	/**
	 * Returns the filtered regions. The returned regions will be not
	 * overlapping and sorted be start offset.
	 */
	public List<Region> getFilteredRegions(TextElement element)
			throws ConQATException {
		ensureTransformerInitialized(element);
		return transformer.extractFilteredRegions();
	}

	/**
	 * Initializes the {@link #transformer} if needed. If initialization was
	 * required, the unfiltered text is returned, null otherwise. This return
	 * value is used to avoid duplicate calls to the (potentially costly)
	 * {@link ITextElement#getUnfilteredTextContent()} method. This way we do
	 * not have to repeat the call outside of the method.
	 */
	private String ensureTransformerInitialized(ITextElement element)
			throws ConQATException {
		if (transformer != null) {
			return null;
		}

		String text = element.getUnfilteredTextContent();
		transformer = new StringOffsetTransformer(filter.getDeletions(text,
				element.getUniformPath()));
		return text;
	}
}