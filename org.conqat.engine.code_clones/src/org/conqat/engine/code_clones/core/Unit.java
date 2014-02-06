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
package org.conqat.engine.code_clones.core;

import org.conqat.engine.core.driver.instance.ConQATStringPool;

/**
 * Base class for units.
 * <p>
 * 
 * We have observed that in large code bases, the number of different units
 * after normalization is significantly smaller than the total number of units.
 * We exploit this observation in order to reduce the memory footprint of the
 * units by pooling unit content strings. This way, each unit content string is
 * only kept in memory once, independent of how often it occurs in the source
 * code. Since Java's {@link String#intern()} facility is too slow for very
 * large String pools, we use the {@link ConQATStringPool} in the constructor
 * {@link Unit}.
 * <p>
 * <b>Note:</b> The implementation of {@link #hashCode()} and
 * {@link #equals(Object)} are crucial for the detection algorithm.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43764 $
 * @ConQAT.Rating GREEN Hash: E4BB0672C545E8992FB74EB7692E9DA3
 */
public abstract class Unit {

	/** The uniform path of the element this unit stems from. */
	private final String elementUniformPath;

	/** The index of the unit in the element */
	private final int indexInElement;

	/** The content of this unit. */
	private final String content;

	/** The content of this unit without normalization */
	private final String unnormalizedContent;

	/** Position of first character in element */
	private final int filteredStartOffset;

	/** Position of last character in element (inclusive) */
	private final int filteredEndOffset;

	/** Create a new unit with identical normalized and unnormalized content */
	protected Unit(int filteredStartOffset, int filteredEndOffset,
			String elementUniformPath, String content, int indexInElement) {
		this(filteredStartOffset, filteredEndOffset, elementUniformPath, content,
				content, indexInElement);
	}

	/** Create new unit */
	protected Unit(int filteredStartOffset, int filteredEndOffset,
			String elementUniformPath, String content,
			String unnormalizedContent, int indexInElement) {
		this.filteredStartOffset = filteredStartOffset;
		this.filteredEndOffset = filteredEndOffset;
		this.elementUniformPath = elementUniformPath;
		this.content = ConQATStringPool.intern(content);
		this.unnormalizedContent = ConQATStringPool.intern(unnormalizedContent);
		this.indexInElement = indexInElement;
	}

	/** Uniform path of the element this unit stems from. */
	public String getElementUniformPath() {
		return elementUniformPath;
	}

	/** Position of first character in element */
	public int getFilteredStartOffset() {
		return filteredStartOffset;
	}

	/** Position of last character in element (inclusive) */
	public int getFilteredEndOffset() {
		return filteredEndOffset;
	}

	/** Gets the index of the unit in the source element */
	public int getIndexInElement() {
		return indexInElement;
	}

	/** Textual content of the unit. */
	public String getContent() {
		return content;
	}

	/** Unnormalized textual content of this unit */
	public String getUnnormalizedContent() {
		return unnormalizedContent;
	}

	/**
	 * The hash code of a unit object is identical to the hash code of its
	 * content string.
	 * 
	 * @see #getContent()
	 */
	@Override
	public int hashCode() {
		return content.hashCode();
	}

	/** Two unit objects are equal if there content strings are equal. */
	@Override
	public boolean equals(Object other) {
		// contrary to the myths on the net this is not slower than catching
		// the class cast exception
		if (!(other instanceof Unit)) {
			return false;
		}

		// we use equals(), which is fast for the same string (i.e. same
		// reference) but still can cope with different references (if there are
		// non-interned strings).
		return content.equals(((Unit) other).content);
	}

	/**
	 * Returns whether this unit is synthetic. Default implementation returns
	 * false. Override for synthetic units
	 */
	public boolean isSynthetic() {
		return false;
	}

	/** Checks whether other unit stems from same element */
	public boolean inSameElement(Unit other) {
		return getElementUniformPath().equals(other.getElementUniformPath());
	}
}