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

import java.nio.charset.Charset;
import java.util.List;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IElement;
import org.conqat.lib.commons.region.Region;

/**
 * This interface describes element that provide text content.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46849 $
 * @ConQAT.Rating GREEN Hash: 0B76C316C002CF7448DAF64DB47BF745
 */
public interface ITextElement extends ITextResource, IElement {

	/** Returns the encoding used when reading the text content of this element. */
	Charset getEncoding();

	/**
	 * Returns the text content of this element, which is potentially filtered
	 * and has been subject to any kind of normalization. The line breaks
	 * returned here are guaranteed to follow Unix convention (i.e. a single
	 * '\n').
	 */
	String getTextContent() throws ConQATException;

	/**
	 * Returns the unfiltered text content, which directly reflects the
	 * underlying file. Line breaks are normalized to Unix-style (\n). Typically
	 * this method is <b>not</b> used by most processors. Instead they should
	 * access {@link #getTextContent()}.
	 */
	String getUnfilteredTextContent() throws ConQATException;

	/**
	 * Converts from 1-based lines to 0-based offsets in the unfiltered text.
	 * Due to caching, this may be way faster than obtaining the unfiltered text
	 * and performing the conversion manually.
	 */
	int convertUnfilteredLineToOffset(int line) throws ConQATException;

	/**
	 * Converts from 0-based offsets to 1-based lines in the unfiltered text.
	 * Due to caching, this may be way faster than obtaining the unfiltered text
	 * and performing the conversion manually.
	 */
	int convertUnfilteredOffsetToLine(int offset) throws ConQATException;

	/**
	 * Converts from 1-based lines to 0-based offsets in the filtered text. Due
	 * to caching, this may be way faster than obtaining the filtered text and
	 * performing the conversion manually.
	 */
	int convertFilteredLineToOffset(int line) throws ConQATException;

	/**
	 * Converts from 0-based offsets to 1-based lines in the filtered text. Due
	 * to caching, this may be way faster than obtaining the filtered text and
	 * performing the conversion manually.
	 */
	int convertFilteredOffsetToLine(int offset) throws ConQATException;

	/**
	 * Converts an offset in the string returned from {@link #getTextContent()}
	 * to an offset in the string returned from
	 * {@link #getUnfilteredTextContent()}.
	 */
	int getUnfilteredOffset(int offset) throws ConQATException;

	/**
	 * Converts an offset in the string returned from
	 * {@link #getUnfilteredTextContent()} to an offset in the string returned
	 * from {@link #getTextContent()}. If the offset was filtered (i.e.
	 * {@link #isFilteredOffset(int)} returns true for this offset), the first
	 * offset after the filtered region is returned (which might be one after
	 * the last offset in the filtered string, if the provided offset in a
	 * filtered tail region).
	 */
	int getFilteredOffset(int unfilteredOffset) throws ConQATException;

	/**
	 * Returns for an offset in the unfiltered string, whether this offset is
	 * subject to filtering, i.e. will not be represented in the filtered
	 * string.
	 */
	boolean isFilteredOffset(int offset) throws ConQATException;

	/**
	 * Returns whether there is a filtering gap between the two offsets. The
	 * first offset must be strictly smaller than the second offset. Both
	 * offsets should be "filtered offsets".
	 */
	boolean isFilterGapBetween(int firstOffset, int secondOffset)
			throws ConQATException;

	/**
	 * Returns the filtered offsets as a list of regions. The returned regions
	 * will be not overlapping and sorted be start offset. This is a potentially
	 * slow operation and should only be used if all regions are required (e.g.
	 * for formatting). Otherwise, the more specific methods in this interface
	 * should be used. May return null if no regions are filtered.
	 */
	List<Region> getFilteredRegions() throws ConQATException;
}
