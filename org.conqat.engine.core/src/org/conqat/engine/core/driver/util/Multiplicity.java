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
package org.conqat.engine.core.driver.util;

import java.io.Serializable;

/**
 * This class represents multiplicities, i.e., ranges with upper and lower
 * bounds, where the lower bound is always non-negative and the upper bound
 * potentially infinite.
 * <p>
 * The class is immutable, so all modification will result in new objects being
 * created.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 832175AC812C7E3C8ABE5C5459D8F2BC
 */
public final class Multiplicity implements Serializable {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** An "infinite" value. */
	public static final int INFINITY = Integer.MAX_VALUE;

	/** Empty multiplicity zero, i.e. [0,0]. */
	public static final Multiplicity ZERO = new Multiplicity(0, 0);

	/** The lower bound */
	private final int lower;

	/** The upper bound */
	private final int upper;

	/** Create interval ranging form 0 to infinity. */
	public Multiplicity() {
		lower = 0;
		upper = INFINITY;
	}

	/** Create interval with given bounds. */
	public Multiplicity(int lower, int upper) {
		this.lower = lower;
		this.upper = upper;
	}

	/** Intersects this multiplicity with another one. The result is returned. */
	public Multiplicity intersect(Multiplicity m) {
		return new Multiplicity(Math.max(lower, m.lower), Math.min(upper,
				m.upper));
	}

	/**
	 * Adds this multiplicity with another one by adding individual bounds. The
	 * result is returned.
	 */
	public Multiplicity add(Multiplicity m) {
		int newUpper = upper + m.upper;
		if (isUnbounded() || m.isUnbounded()) {
			newUpper = INFINITY;
		}

		return new Multiplicity(lower + m.lower, newUpper);
	}

	/** Returns the lower bound. */
	public int getLower() {
		return lower;
	}

	/** Returns the upper bound. */
	public int getUpper() {
		return upper;
	}

	/** Returns whether the upper bound is missing. */
	public boolean isUnbounded() {
		return upper == INFINITY;
	}

	/** Returns whether this interval is empty. */
	public boolean isEmpty() {
		return upper < lower;
	}

	/**
	 * Returns a new multiplicity whose bounds are both increased by the given
	 * amount (which also may be negative). For this the lower bound always
	 * saturates at zero and an infinite upper bound behaves correctly.
	 * 
	 * @param amount
	 *            the amount by which the boundaries are increased.
	 * @return the adjusted multiplicity.
	 */
	public Multiplicity shiftBounds(int amount) {
		int newLower = Math.max(0, lower + amount);
		int newUpper = isUnbounded() ? INFINITY : upper + amount;
		return new Multiplicity(newLower, newUpper);
	}

	/**
	 * Returns a new multiplicity <code>m</code> divided by a
	 * <code>factor</code>, i.e. any value in the returned range will fit into
	 * this range after multiplication with the factor. Unbounded multiplicities
	 * are handled correctly.
	 * <p>
	 * In formulas:
	 * 
	 * <pre>
	 *         this.lower &lt;= factor * m.lower
	 *         this.upper &gt;= factor * m.upper
	 * </pre>
	 * 
	 * @param factor
	 *            the factor by which to divide (must be positive).
	 * @return the adjusted multiplicity.
	 */
	public Multiplicity divideBy(int factor) {
		if (factor < 1) {
			throw new IllegalArgumentException("Factor must be positive!");
		}
		// round up
		int newLower = (lower + factor - 1) / factor;

		// round down
		int newUpper = isUnbounded() ? INFINITY : upper / factor;

		return new Multiplicity(newLower, newUpper);
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		String result = "[" + lower + ", ";
		if (isUnbounded()) {
			result += "INF[";
		} else {
			result += upper + "]";
		}
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Multiplicity)) {
			return false;
		}
		Multiplicity m = (Multiplicity) obj;

		return (lower == m.lower) && (upper == m.upper);
	}

	/** Returns a hash code based on the lower and upper bound. */
	@Override
	public int hashCode() {
		return lower + 93 * upper;
	}

	/**
	 * Returns whether this is a singleton, i.e. the lower and upper bounds are
	 * finite and the same.
	 */
	public boolean isSingleton() {
		return !isUnbounded() && lower == upper;
	}
}