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

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.nio.charset.Charset;
import java.util.List;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IContentAccessor;
import org.conqat.engine.resource.binary.BinaryElement;
import org.conqat.engine.resource.text.filter.base.ITextFilter;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.cache4j.CacheFactory;
import org.conqat.lib.commons.cache4j.ICache;
import org.conqat.lib.commons.clone.DeepCloneException;
import org.conqat.lib.commons.factory.ForwardingFactory;
import org.conqat.lib.commons.factory.IFactory;
import org.conqat.lib.commons.filesystem.EByteOrderMark;
import org.conqat.lib.commons.region.Region;
import org.conqat.lib.commons.string.LineOffsetConverter;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Default implementation of {@link ITextElement}.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46849 $
 * @ConQAT.Rating GREEN Hash: 121C4108B01529579A22E756FBA559B5
 */
public class TextElement extends BinaryElement implements ITextElement {

	/** Cache used for the filtered text and line offset converters. */
	@SuppressWarnings("unchecked")
	private static final ICache<FilteredTextKey, CachedTextData, ConQATException> filteredTextCache = CacheFactory
			.obtainCache(TextElement.class, ForwardingFactory.INSTANCE);

	/** Encoding used by this element. */
	private final Charset encoding;

	/** The transformer applied to the text. */
	private final LazyTextFilter filter;

	/** Constructor. */
	public TextElement(IContentAccessor accessor, Charset encoding) {
		this(accessor, encoding, null);
	}

	/** Constructor. */
	public TextElement(IContentAccessor accessor, Charset encoding,
			ITextFilter filter) {
		super(accessor);
		this.encoding = encoding;
		if (filter == null) {
			this.filter = null;
		} else {
			this.filter = new LazyTextFilter(filter);
		}
	}

	/** Copy constructor. */
	protected TextElement(TextElement other) throws DeepCloneException {
		super(other);
		encoding = other.encoding;
		filter = other.filter;
	}

	/** {@inheritDoc} */
	@Override
	public Charset getEncoding() {
		return encoding;
	}

	/** {@inheritDoc} */
	@Override
	public String getUnfilteredTextContent() throws ConQATException {
		byte[] content = getContent();
		EByteOrderMark bom = EByteOrderMark.determineBOM(content);

		final String result;
		if (bom != null) {
			try {
				int offset = bom.getBOMLength();
				result = new String(content, offset, content.length - offset,
						bom.getEncoding());
			} catch (UnsupportedEncodingException e) {
				throw new AssertionError("UTF encoding not supported??");
			}
		} else {
			result = new String(content, encoding);
		}

		return StringUtils.replaceLineBreaks(result, "\n");
	}

	/** {@inheritDoc} */
	@Override
	public int convertUnfilteredLineToOffset(int line) throws ConQATException {
		return filteredTextCache.obtain(new FilteredTextKey(this))
				.getUnfilteredLineOffsetConverter().getOffset(line);
	}

	/** {@inheritDoc} */
	@Override
	public int convertUnfilteredOffsetToLine(int offset) throws ConQATException {
		return filteredTextCache.obtain(new FilteredTextKey(this))
				.getUnfilteredLineOffsetConverter().getLine(offset);
	}

	/** {@inheritDoc} */
	@Override
	public int convertFilteredLineToOffset(int line) throws ConQATException {
		return filteredTextCache.obtain(new FilteredTextKey(this))
				.getFilteredLineOffsetConverter().getOffset(line);
	}

	/** {@inheritDoc} */
	@Override
	public int convertFilteredOffsetToLine(int offset) throws ConQATException {
		return filteredTextCache.obtain(new FilteredTextKey(this))
				.getFilteredLineOffsetConverter().getLine(offset);
	}

	/** {@inheritDoc} */
	@Override
	public String getTextContent() throws ConQATException {
		return filteredTextCache.obtain(new FilteredTextKey(this))
				.getFilteredText();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isFilteredOffset(int offset) throws ConQATException {
		if (filter != null) {
			return filter.isFilteredOffset(offset, this);
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isFilterGapBetween(int firstOffset, int secondOffset)
			throws ConQATException {
		if (filter != null) {
			return filter.isFilterGapBetween(firstOffset, secondOffset, this);
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public int getUnfilteredOffset(int offset) throws ConQATException {
		if (filter != null) {
			return filter.getUnfilteredOffset(offset, this);
		}
		return offset;
	}

	/** {@inheritDoc} */
	@Override
	public int getFilteredOffset(int unfilteredOffset) throws ConQATException {
		if (filter != null) {
			return filter.getFilteredOffset(unfilteredOffset, this);
		}
		return unfilteredOffset;
	}

	/** {@inheritDoc} */
	@Override
	public List<Region> getFilteredRegions() throws ConQATException {
		if (filter != null) {
			return filter.getFilteredRegions(this);
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public ITextResource[] getChildren() {
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public TextElement deepClone() throws DeepCloneException {
		return new TextElement(this);
	}

	/**
	 * Contains all attributes of a {@link TextElement} that can be used to
	 * uniquely identify it.
	 */
	protected static class TextElementKey extends BinaryElementKey {

		/** The encoding. */
		protected final Charset encoding;

		/**
		 * The filter. This is compared by identity of the contained filter.
		 * This may be null!
		 */
		protected final LazyTextFilter filter;

		/** Constructor. */
		protected TextElementKey(TextElement element) {
			super(element);
			encoding = element.encoding;
			filter = element.filter;
		}

		/** {@inheritDoc} */
		@Override
		public boolean equals(Object obj) {
			if (!(super.equals(obj) && obj instanceof TextElementKey && ((TextElementKey) obj).encoding
					.equals(encoding))) {
				return false;
			}
			if (filter == null) {
				return ((TextElementKey) obj).filter == null;
			}
			return ((TextElementKey) obj).filter.filter == filter.filter;
		}

		/** {@inheritDoc} */
		@Override
		public int hashCode() {
			int filterHashCode = 0;
			if (filter != null) {
				filterHashCode = System.identityHashCode(filter.filter);
			}
			return (super.hashCode() * 17 + encoding.hashCode()) * 13
					+ filterHashCode;
		}
	}

	/** Relevant data on a text we want to cache. */
	protected static class CachedTextData {

		/** The filtered text. */
		private final String filteredText;

		/** The line/offset converter for the filtered text. */
		private final LineOffsetConverter filteredLineOffsetConverter;

		/** The line/offset converter for the unfiltered text. */
		private final LineOffsetConverter unfilteredLineOffsetConverter;

		/** Constructor. */
		public CachedTextData(TextElement element, LazyTextFilter filter)
				throws ConQATException {
			String unfilteredText = element.getUnfilteredTextContent();
			unfilteredLineOffsetConverter = new LineOffsetConverter(
					unfilteredText);

			if (filter == null) {
				filteredText = unfilteredText;
				filteredLineOffsetConverter = unfilteredLineOffsetConverter;
			} else {
				filteredText = filter.filterString(element);
				filteredLineOffsetConverter = new LineOffsetConverter(
						filteredText);
			}
		}

		/** Returns the filtered text. */
		public String getFilteredText() {
			return filteredText;
		}

		/** Returns the line/offset converter for the filtered text. */
		public LineOffsetConverter getFilteredLineOffsetConverter() {
			return filteredLineOffsetConverter;
		}

		/** Returns the line/offset converter for the unfiltered text. */
		public LineOffsetConverter getUnfilteredLineOffsetConverter() {
			return unfilteredLineOffsetConverter;
		}
	}

	/** The key/factory used for obtaining the filtered text. */
	protected static class FilteredTextKey extends TextElementKey implements
			IFactory<CachedTextData, ConQATException> {

		/**
		 * We need a reference to the text element, but do not want to keep this
		 * key (which may live a long time in the cache) to stop the element
		 * from being collected by the GC later on. Thus we use a weak reference
		 * here.
		 */
		private final WeakReference<TextElement> elementRef;

		/** Constructor. */
		protected FilteredTextKey(TextElement element) {
			super(element);
			elementRef = new WeakReference<TextElement>(element);
		}

		/** {@inheritDoc} */
		@Override
		public CachedTextData create() throws ConQATException {
			TextElement element = elementRef.get();
			CCSMAssert
					.isNotNull(
							element,
							"The element should not be null, "
									+ "as creation is only issued from the cache via a live "
									+ "TextElement, which in turn means that the reference must "
									+ "still be valid.");

			return new CachedTextData(element, filter);
		}
	}

}