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

package org.conqat.engine.code_clones.core.matching;

import java.util.Set;

import org.conqat.lib.commons.algo.Diff.Delta;
import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Describes the similarity between two lists of units. Contrary to
 * {@link Delta}, this similarity measure supports consistent renames of units.
 * 
 * The class is filled step-wise with data during the calculation algorithm in
 * {@link UnitListDiffer} using the package visible methods. This class is
 * immutable outside of this package.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46687 $
 * @ConQAT.Rating GREEN Hash: B8171B42A495144B4903AA357E31D7D2
 */
public final class UnitListDelta {

	/** Number of equal units */
	private int equals = 0;

	/** Number of times units were different, but consistently renamed */
	private int renames = 0;

	/** Number of different units */
	private int differences = 0;

	/** Set based term equality. */
	private double termEquality = 0;

	/** Get number of equal units */
	public int getEquals() {
		return equals;
	}

	/** Get number consistent renames */
	public int getRenames() {
		return renames;
	}

	/** Get number of differences */
	public int getDifferences() {
		return differences;
	}

	/** Get number of equal or consistently renamed units */
	public int getMatches() {
		return getEquals() + getRenames();
	}

	/** Get ratio of matches w.r.t. the size of the unit list */
	public double getSimilarity() {
		return (double) getMatches() / getSize();
	}

	/** Get ratio of equals w.r.t. the size of the unit list */
	public double getEquality() {
		return (double) getEquals() / getSize();
	}

	/** Get ratio of equal terms (treated as set). */
	public double getTermEquality() {
		return termEquality;
	}

	/** Total number of units */
	public int getSize() {
		return equals + renames + differences;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();

		result.append("Similarity: " + getSimilarity() + StringUtils.CR);
		result.append("Equality: " + getEquality() + StringUtils.CR);
		result.append("Term equality: " + getTermEquality() + StringUtils.CR);
		result.append("\tEquals: " + getEquals() + StringUtils.CR);
		result.append("\tMatches: " + getMatches() + StringUtils.CR);
		result.append("\tDifferences: " + getDifferences() + StringUtils.CR);

		return result.toString();
	}

	/** Increment equals */
	/* package */void incEquals(int inc) {
		CCSMPre.isTrue(inc > 0, "Increment must be positive!");
		equals += inc;
	}

	/** Increment renames */
	/* package */void incRenames(int inc) {
		CCSMPre.isTrue(inc > 0, "Increment must be positive!");
		renames += inc;
	}

	/** Increment differences */
	/* package */void incDifferences(int inc) {
		CCSMPre.isTrue(inc > 0, "Increment must be positive!");
		differences += inc;
	}

	/** Calculates term equality from two sets of terms and stores the result. */
	@SuppressWarnings("unchecked")
	/* package */void calculateAndSetTermEquality(Set<String> terms1,
			Set<String> terms2) {
		int intersectionSize = CollectionUtils.intersectionSet(terms1, terms2)
				.size();
		int unionSize = terms1.size() + terms2.size() - intersectionSize;
		termEquality = (double) intersectionSize / unionSize;
	}
}
