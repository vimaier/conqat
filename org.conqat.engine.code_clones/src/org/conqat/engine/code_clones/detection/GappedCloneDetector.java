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
package org.conqat.engine.code_clones.detection;

import java.util.List;

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.code_clones.core.Unit;
import org.conqat.engine.code_clones.detection.suffixtree.ApproximateCloneDetectingSuffixTree;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.lib.commons.algo.Diff;
import org.conqat.lib.commons.algo.Diff.Delta;
import org.conqat.lib.commons.region.Region;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43764 $
 * @ConQAT.Rating GREEN Hash: 386CC5B9ECDBE560B598A9391CBCDFE7
 */
@AConQATProcessor(description = "Approximate clone detection based on suffix trees. Does find approximate (aka"
		+ "gapped) clones. Internally the {@link ApproximateCloneDetectingSuffixTree} is"
		+ "used.")
public class GappedCloneDetector extends CloneDetectorBase {

	/** The maximal number of errors we are allowed. */
	private int maxErrors;

	/** Number of units that must be equal at the start of a clone */
	private int initialEquality = 1;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "errors", minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "Sets the maximal allowed number of errors in a clone.")
	public void setMaxErrors(
			@AConQATAttribute(name = "max", description = "Sets the maximal allowed number of errors in a clone.") int maxErrors) {
		this.maxErrors = maxErrors;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "initial", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Number of units that must be equal at the start of a clone.")
	public void setInitialEquality(
			@AConQATAttribute(name = "equality", description = "Default is 1.") int initialEquality) {
		this.initialEquality = initialEquality;
	}

	/** {@inheritDoc} */
	@Override
	protected List<CloneClass> detectClones() throws ConQATException {
		ApproximateCloneDetectingSuffixTree cdstree = new ApproximateCloneDetectingSuffixTree(
				units) {

			@Override
			protected boolean mayNotMatch(Object character) {
				return character instanceof SentinelUnit;
			}

			@Override
			protected void reportBufferShortage(int leafStart, int leafLength) {
				getLogger()
						.debug("Encountered suffix tree node, whose word is too large for the buffer. "
								+ "This might occur with very long clones. "
								+ "As a result the clone will be chopped into parts and "
								+ "potentially some small parts of the clone might get lost. Start at "
								+ leafStart + "; length " + leafLength);
			}
		};
		getLogger().debug("Suffix tree created. Detecting clones...");
		GapDetectingCloneConsumer cloneConsumer = new GapDetectingCloneConsumer();
		cdstree.findClones(minLength, maxErrors, initialEquality, cloneConsumer);
		getLogger().debug("Clone detection finished!");
		return cloneConsumer.getCloneClasses();
	}

	/** A clone consumer, which in addition detects gaps on the clones reported. */
	private class GapDetectingCloneConsumer extends CloneConsumer {

		/** The first clone of the clone class. */
		private Clone firstClone = null;

		/** The position of the first clone. */
		private int firstPos = 0;

		/** The length of the first clone. */
		private int firstLength = 0;

		/** Constructor. */
		private GapDetectingCloneConsumer() {
			super();
		}

		/** {@inheritDoc} */
		@Override
		public void startCloneClass(int normalizedLength) {
			super.startCloneClass(normalizedLength);
			firstClone = null;
		}

		/** Adds a clone to the current {@link CloneClass} */
		@Override
		public Clone addClone(int globalPosition, int length)
				throws ConQATException {
			// get clone without gap information
			Clone clone = super.addClone(globalPosition, length);

			Delta<Unit> delta = Diff.computeDelta(
					units.subList(firstPos, firstPos + firstLength),
					units.subList(globalPosition, globalPosition + length));

			if (firstClone != null) {
				clone.setDeltaInUnits(delta.getSize());
				ITextElement element = resolveElement(clone.getUniformPath());
				fillGaps(clone, delta, globalPosition, element);
			} else {
				clone.setDeltaInUnits(0);
				firstClone = clone;
				firstPos = globalPosition;
				firstLength = length;
			}

			return clone;
		}

		/** Fills the gaps for the given clone. */
		private void fillGaps(Clone clone, Delta<Unit> delta,
				int globalPosition, ITextElement element)
				throws ConQATException {
			boolean firstNeedsGaps = !firstClone.containsGaps();

			for (int i = 0; i < delta.getSize(); ++i) {
				int pos = delta.getPosition(i);
				if (pos > 0) {
					pos--;
					Unit unit = units.get(globalPosition + pos);
					int rawStartOffset = element.getUnfilteredOffset(unit
							.getFilteredStartOffset());
					int rawEndOffset = element.getUnfilteredOffset(unit
							.getFilteredEndOffset());
					clone.addGap(new Region(rawStartOffset, rawEndOffset));
				} else if (firstNeedsGaps) {
					pos = -pos - 1;
					Unit unit = units.get(firstPos + pos);
					int rawStartOffset = element.getUnfilteredOffset(unit
							.getFilteredStartOffset());
					int rawEndOffset = element.getUnfilteredOffset(unit
							.getFilteredEndOffset());
					firstClone.addGap(new Region(rawStartOffset, rawEndOffset));
				}
			}
		}
	}
}