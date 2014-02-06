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
package org.conqat.lib.commons.region;

/**
 * Regions represent intervals. Both the start and the end position are
 * considered to be part of the region. Regions can i.e. be used to represent
 * fragments of files.
 * <p>
 * This class is immutable.
 * 
 * @author Elmar Juergens
 * @author $Author: heinemann $
 * 
 * @version $Revision: 44900 $
 * @ConQAT.Rating GREEN Hash: 6BEDF58991EDD596D01ED3C6E906CF6A
 */
public final class Region implements Comparable<Region> {

	/** Name that is used if region is created without name */
	public static final String UNKNOWN_ORIGIN = "Unkwnon origin";

	/**
	 * Origin of the region. Can be used to store information about who created
	 * the region.
	 */
	private final String origin;

	/** Region start position */
	private final int start;

	/** Region end position. */
	private final int end;

	/**
	 * Creates a region with an origin. An empty region can be denoted with and
	 * end position smaller than start.
	 * 
	 * @param start
	 *            Start position of the region
	 * @param end
	 *            End position of the region
	 * @param origin
	 *            Region origin. (i.e. region producer)
	 */
	public Region(int start, int end, String origin) {
		this.start = start;
		this.end = end;
		this.origin = origin;
	}

	/**
	 * Creates a region with an unknown origin
	 * 
	 * @param start
	 *            Start position of the region
	 * @param end
	 *            End position of the region
	 */
	public Region(int start, int end) {
		this(start, end, UNKNOWN_ORIGIN);
	}

	/** Checks if the region contains a position */
	public boolean containsPosition(int position) {
		return (start <= position && end >= position);
	}

	/** Checks if two regions are overlapping */
	public boolean overlaps(Region r) {
		// Region with smaller start value performs overlap check
		if (r.start < start) {
			return r.overlaps(this);
		}

		return (start <= r.start && end >= r.start);
	}

	/** Checks if two regions are adjacent */
	public boolean adjacent(Region r) {
		// Region with smaller start value performs adjacency check
		if (r.start < start) {
			return r.adjacent(this);
		}

		return (end + 1 == r.start);
	}

	/** Get origin. */
	public String getOrigin() {
		return origin;
	}

	/**
	 * Gets the end position of the region. This may be less than start for an
	 * empty region (see also {@link #isEmpty()}).
	 */
	public int getEnd() {
		return end;
	}

	/** Gets the start position of the region */
	public int getStart() {
		return start;
	}

	/**
	 * Gets the length of the region. Empty regions have a length of 0.
	 */
	public int getLength() {
		if (isEmpty()) {
			return 0;
		}
		return end - start + 1;
	}

	/** Returns whether this region is empty. */
	public boolean isEmpty() {
		return end < start;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "[" + start + "-" + end + "]";
	}

	/** Compares regions by their start position */
	@Override
	public int compareTo(Region compareTo) {
		return new Integer(start).compareTo(new Integer(compareTo.start));
	}

	/**
	 * Returns whether start and end of the region is the same as for this
	 * region.
	 */
	public boolean equalsStartEnd(Region other) {
		return start == other.start && end == other.end;
	}
}