package org.conqat.engine.html_presentation.listing;

import java.util.ArrayList;
import java.util.List;

import org.conqat.lib.commons.algo.Diff.Delta;
import org.conqat.lib.commons.collections.CollectionUtils;

/**
 * Provides line colors based on delta information.
 * 
 * @author $Author: juergens $
 * @version $Rev: 44789 $
 * @ConQAT.Rating RED Hash: DF41971342649EED33ED5BB3F3B06BDA
 */
public class LineDiffColorProvider {

	/** Inserted lines */
	private final List<Integer> insertions;

	/** Lines before which content was deleted */
	private final List<Integer> deletions;

	/** Flag that indicates deletion */
	private static final int DELETED = -1;

	/** Constructor. */
	public LineDiffColorProvider(Delta<?> delta) {
		insertions = computeInsertions(delta);
		deletions = computeDeletions(delta);
	}

	/** Compute lines that have been inserted */
	private List<Integer> computeInsertions(Delta<?> delta) {
		List<Integer> insertions = new ArrayList<Integer>();

		for (int pos = 0; pos < delta.getSize(); pos++) {
			int change = delta.getPosition(pos);

			if (change > 0) {
				insertions.add(change - 1);
			}

		}
		return insertions;
	}

	/** Compute lines that have been deleted */
	private List<Integer> computeDeletions(Delta<?> delta) {
		List<Integer> deletions = new ArrayList<Integer>();

		int[] positionMap = computePositionMap(delta, delta.getN());
		boolean pendingDeletion = false;
		for (int originalPos = 0; originalPos < positionMap.length; originalPos++) {
			if (positionMap[originalPos] == DELETED) {
				pendingDeletion = true;
			} else if (pendingDeletion) {
				int newPos = positionMap[originalPos];
				deletions.add(newPos);
				pendingDeletion = false;
			}
		}

		return deletions;
	}

	/** Compute mapping from old to new position */
	private static int[] computePositionMap(Delta<?> delta, int oldCount) {

		// initialize position map
		int[] positionMap = new int[oldCount];
		for (int i = 0; i < oldCount; i++) {
			positionMap[i] = i;
		}

		// TODO (BH): This is far more complicated and inefficient than it has
		// to be. The alternative solution is hard to explain in text, but if
		// you want we can co-program it (given the existing tests, this should
		// be save).
		for (int editOperation = 0; editOperation < delta.getSize(); editOperation++) {

			int editPosition = delta.getPosition(editOperation);
			if (editPosition > 0) {
				editPosition -= 1; // correct inc. of 1 of all pos. >0

				// shift right
				for (int i = 0; i < oldCount; i++) {
					int value = positionMap[i];
					if (value >= editPosition && value != DELETED) {
						positionMap[i] = value + 1;
					}
				}

			} else {
				editPosition = Math.abs(editPosition);
				editPosition -= 1;

				// mark deleted
				positionMap[editPosition] = DELETED;

				// shift all positions behind to the left
				for (int i = editPosition + 1; i < oldCount; i++) {
					int value = positionMap[i];
					if (value != DELETED) {
						positionMap[i] = value - 1;
					}
				}
			}
		}

		return positionMap;
	}

	/** Get inserted lines */
	public List<Integer> getInsertions() {
		return CollectionUtils.asUnmodifiable(insertions);
	}

	/** Get deleted lines */
	public List<Integer> getDeletions() {
		return CollectionUtils.asUnmodifiable(deletions);
	}

}
