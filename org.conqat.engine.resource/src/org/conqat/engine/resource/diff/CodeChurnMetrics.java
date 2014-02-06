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
package org.conqat.engine.resource.diff;

import java.util.List;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.IConQATLogger;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.lib.commons.algo.Diff;
import org.conqat.lib.commons.algo.Diff.Delta;

/**
 * Helper class for calculating code churn.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating RED Hash: BA1497E048CDABA1CF6AAC62C7500DFB
 * 
 * @param <T>
 *            the type of element used as parameter.
 */
public abstract class CodeChurnMetrics<T extends ITextElement> {

	/** Code churn between the given file versions. */
	private int codeChurn;

	/** The maximum number of lines contained in the given file versions. */
	private int maxLines;

	/** The code churn relative to the maximum lines of the file versions. */
	private double relativeChurn;

	/** Flag if the file is a new file. */
	private boolean isNew;

	/**
	 * Calculates the code churn metrics.
	 * 
	 * @param mainElement
	 *            The main element and reference for comparison.
	 * @param compareeElement
	 *            The element the mainElement is compared to, null if the
	 *            mainElement represents a new file.
	 * @throws ConQATException
	 */
	// TODO (BH): You call this method from the constructor of both subclasses.
	// Maybe make this a constructor as well?
	protected void calculateMetrics(T mainElement, T compareeElement,
			IConQATLogger logger) throws ConQATException {
		List<String> mainLines = normalize(mainElement, logger);
		if (compareeElement == null) {
			codeChurn = mainLines.size();
			maxLines = mainLines.size();
			relativeChurn = 1;
			isNew = true;
			return;
		}

		List<String> compareeLines = normalize(compareeElement, logger);
		Delta<String> delta = Diff.computeDelta(mainLines, compareeLines);

		// as added lines account for one of the files and removed for the other
		// one, removing delta.getSize() from both length given us the
		// unmodified (equal) lines in both files
		int sameLines = (mainLines.size() + compareeLines.size() - delta
				.getSize()) / 2;

		maxLines = Math.max(mainLines.size(), compareeLines.size());
		codeChurn = maxLines - sameLines;
		isNew = false;
		relativeChurn = (double) codeChurn / maxLines;
	}

	/** Normalizes the content of the given element. */
	protected abstract List<String> normalize(T element, IConQATLogger logger)
			throws ConQATException;

	/** Returns code churn. */
	public int getCodeChurn() {
		return codeChurn;
	}

	/** Returns the maximum lines of the compared file versions. */
	public int getMaxLines() {
		return maxLines;
	}

	/**
	 * Returns the code churn relative to the maximum lines of the file
	 * versions.
	 */
	public double getRelativeChurn() {
		return relativeChurn;
	}

	/** Returns if the file is a new file. */
	public boolean isNew() {
		return isNew;
	}
}
