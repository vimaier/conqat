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
package org.conqat.engine.commons.findings.location;

import org.conqat.lib.commons.assertion.CCSMAssert;

/**
 * This class denotes a region of text in an element.
 * 
 * <b>Context:</b> Due to the way ConQAT deals with text, the class is a little
 * bit more complex than expected. First, in ConQAT all text is normalized to
 * use Unix style line endings (regardless of the line endings in the file).
 * Second, ConQAT may apply filters, i.e. the internal (filtered) text
 * representation may be different from the (raw) text in the file.
 * Additionally, a location is best described by character offsets into the
 * string, while a user typically expects line numbers. Conversion between all
 * these representations is easy, as long as ConQAT internal representation is
 * available. Without it, conversion is not possible.
 * 
 * <b>Rationale:</b> When findings are reported to a user, the raw offsets
 * and/or lines should be used, as these are more meaningful (visible in other
 * editors as well). Also for persisting in a report, the raw positions are
 * preferred, as the filtered ones depend on the ConQAT configuration, while raw
 * offsets are independent of filter configuration. When working with findings
 * within ConQAT, typically the filtered positions are needed, as most
 * processors also work on the filtered representation. However, in such a case
 * the corresponding element is typically available and thus conversion to the
 * filtered representation is easy.
 * 
 * <b>Implementation:</b> The finding (as well as the findings report) only
 * stores raw positions. While the offsets would be sufficient, we also store
 * line numbers to be able to provide meaningful user output. Filtered positions
 * are not stored, but are made available via utility methods in the resource
 * bundle. All fields are mandatory, i.e., it is not allowed to fill any
 * position entry with invalid data (contrary to the old CodeRegionLocation,
 * where -1 could be used to denote missing information).
 * 
 * @author $Author: goede $
 * @version $Rev: 41698 $
 * @ConQAT.Rating GREEN Hash: 17529D0E53A92D53C5A9E732B5BA55C8
 */
public class TextRegionLocation extends ElementLocation {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/**
	 * The absolute start position of the region in the (raw) text (zero based,
	 * inclusive).
	 */
	private final int rawStartOffset;

	/**
	 * The absolute end position in the (raw) text (zero based, inclusive).
	 */
	private final int rawEndOffset;

	/**
	 * The line corresponding to {@link #rawStartOffset} (one-based, inclusive).
	 */
	private final int rawStartLine;

	/**
	 * The line corresponding to {@link #rawEndOffset} (one-based, inclusive).
	 */
	private final int rawEndLine;

	/** Constructor. */
	public TextRegionLocation(String location, String uniformPath,
			int rawStartOffset, int rawEndOffset, int rawStartLine,
			int rawEndLine) {
		super(location, uniformPath);

		CCSMAssert.isTrue(rawStartOffset <= rawEndOffset,
				"Start offset may not be after end offset.");
		CCSMAssert.isTrue(rawStartLine <= rawEndLine,
				"Start line may not be after end line.");

		this.rawStartOffset = rawStartOffset;
		this.rawEndOffset = rawEndOffset;
		this.rawStartLine = rawStartLine;
		this.rawEndLine = rawEndLine;
	}

	/**
	 * Returns the absolute start position of the region in the (raw) text (zero
	 * based, inclusive).
	 */
	public int getRawStartOffset() {
		return rawStartOffset;
	}

	/**
	 * Returns the absolute end position in the (raw) text (zero based,
	 * inclusive).
	 */
	public int getRawEndOffset() {
		return rawEndOffset;
	}

	/**
	 * Returns the line corresponding to {@link #getRawStartOffset()}
	 * (one-based, inclusive).
	 */
	public int getRawStartLine() {
		return rawStartLine;
	}

	/**
	 * Returns the line corresponding to {@link #getRawEndOffset()} (one-based,
	 * inclusive).
	 */
	public int getRawEndLine() {
		return rawEndLine;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This includes the start and end line which is typically sufficient for
	 * debugging and showing to a user.
	 */
	@Override
	public String toLocationString() {
		return super.toLocationString() + ":" + rawStartLine + "-" + rawEndLine;
	}
}
