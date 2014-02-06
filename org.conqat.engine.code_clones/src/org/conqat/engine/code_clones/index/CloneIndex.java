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
package org.conqat.engine.code_clones.index;

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.code_clones.core.Unit;
import org.conqat.engine.code_clones.index.report.ICloneClassReporter;
import org.conqat.engine.code_clones.index.store.ICloneIndexStore;
import org.conqat.engine.code_clones.normalization.provider.IUnitProvider;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.IConQATLogger;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.lib.scanner.ELanguage;

/**
 * An index used to store cloning information. This class supports both querying
 * the index and modifying it.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 45617 $
 * @ConQAT.Rating YELLOW Hash: AA34CAF83E5A5CB14891EBC6EC471492
 */
public class CloneIndex {

	/** The store used for the operations. */
	private final ICloneIndexStore store;

	/** The options of the store. */
	private final PersistedOptions options;

	/** The logger used. */
	private final IConQATLogger logger;

	/** Milliseconds used for reading access to the store. */
	private long readMilliSeconds = 0;

	/** Milliseconds used for processing of read data. */
	private long readProcessMilliSeconds = 0;

	/** Milliseconds used for writing data into the store. */
	private long writeMilliSeconds = 0;

	/** Milliseconds used for preparing the data for writing. */
	private long writeProcessMilliSeconds = 0;

	/** Constructor. */
	public CloneIndex(ICloneIndexStore store, IConQATLogger logger) {
		this.store = store;
		options = new PersistedOptions(store);
		this.logger = logger;
	}

	/**
	 * Reports all ungapped clones for the given originId.
	 * 
	 * @param onlyStartingHere
	 *            if this is true, only clone classes for which this origin
	 *            contributes the first clone instance are reported. If this is
	 *            false, all clone classes are reported. This should be set to
	 *            true when querying all origins consecutively to avoid
	 *            duplicate clone groups.
	 * 
	 * @return true, if this originId is found in the database, false if the
	 *         originId could not be found (might also indicate origins without
	 *         units (ignored files))
	 */
	public boolean reportClones(String originId, ICloneClassReporter reporter,
			boolean onlyStartingHere, int minLength) throws StorageException,
			ConQATException {
		return reportClones(originId, reporter, onlyStartingHere, minLength,
				false);
	}

	/**
	 * Reports all clones for the given originId.
	 * 
	 * @param onlyStartingHere
	 *            if this is true, only clone classes for which this origin
	 *            contributes the first clone instance are reported. If this is
	 *            false, all clone classes are reported. This should be set to
	 *            true when querying all origins consecutively to avoid
	 *            duplicate clone groups.
	 * @param allowGaps
	 *            if this is true, the clone classes will also include clones
	 *            with gaps. Note that the algorithm used has slightly different
	 *            characteristics, hence you are not guaranteed to get all
	 *            ungapped clones as well (but most should be found).
	 * 
	 * @return true, if this originId is found in the database, false if the
	 *         originId could not be found (might also indicate origins without
	 *         units (ignored files))
	 */
	public boolean reportClones(String originId, ICloneClassReporter reporter,
			boolean onlyStartingHere, int minLength, boolean allowGaps)
			throws StorageException, ConQATException {
		long startTime = System.currentTimeMillis();
		List<Chunk> chunks = store.getChunksByOrigin(originId);
		if (chunks == null) {
			return false;
		}

		List<ChunkList> orderedChunks = ChunkUtils.obtainOrderedChunks(store,
				chunks);
		readMilliSeconds += System.currentTimeMillis() - startTime;

		startTime = System.currentTimeMillis();
		if (allowGaps) {
			new CloneIndexGappedCloneSearcher(originId, reporter,
					onlyStartingHere, minLength, orderedChunks,
					options.getChunkLength()).reportClones();
		} else {
			new CloneIndexCloneSearcher(originId, reporter, onlyStartingHere,
					minLength, orderedChunks, options.getChunkLength())
					.reportClones();
		}
		readProcessMilliSeconds += System.currentTimeMillis() - startTime;

		return true;
	}

	/**
	 * Advances the head index so far, that its entry in headList corresponds to
	 * the given <code>tail</code> chunk (after correcting the unit index by the
	 * given amount). This works, as we know that the list is sorted and
	 * contains all chunks corresponding to the head list.
	 * 
	 * @return the new head index.
	 */
	static int advanceHeadIndex(Chunk tail, List<Chunk> headList,
			int headIndex, int unitSkip) {
		while (true) {
			boolean indexMatches = tail.getFirstUnitIndex() == headList.get(
					headIndex).getFirstUnitIndex()
					+ unitSkip;
			boolean originMatches = tail.getOriginId().equals(
					headList.get(headIndex).getOriginId());
			if (indexMatches && originMatches) {
				return headIndex;
			}
			++headIndex;
		}
	}

	/** Removes the given element from the underlying store. */
	public void removeFile(ITextElement element) throws StorageException {
		store.removeChunks(element.getUniformPath());
	}

	/**
	 * Inserts a file into the index.
	 * 
	 * @return the number of units processed.
	 */
	public int insertFile(ITokenElement element) throws ConQATException {
		IUnitProvider<ITextResource, Unit> normalizer = obtainNormalization(element
				.getLanguage());

		long start = System.currentTimeMillis();

		normalizer.init(element, logger);

		List<Unit> units = new ArrayList<Unit>();
		Unit unit = null;
		int unitCount = 0;
		while ((unit = normalizer.getNext()) != null) {
			if (!unit.isSynthetic()) {
				unitCount += 1;
			}
			units.add(unit);
		}
		if (unitCount == 0) {
			return 0;
		}

		List<Chunk> chunks = ChunkUtils.calculateChunks(units,
				options.getChunkLength(), element, unitCount);
		writeProcessMilliSeconds += System.currentTimeMillis() - start;

		start = System.currentTimeMillis();
		store.batchInsertChunks(chunks);
		writeMilliSeconds += System.currentTimeMillis() - start;

		return unitCount;
	}

	/** Returns the normalization to be used. */
	private IUnitProvider<ITextResource, Unit> obtainNormalization(
			ELanguage language) throws StorageException {
		IUnitProvider<ITextResource, Unit> normalizer = options
				.getNormalization(language);
		if (normalizer == null) {
			throw new StorageException("No normalization for language "
					+ language + " found!");
		}
		return normalizer;
	}

	/** Returns a string which summarizes current performance characteristics. */
	public String getPerformanceInfo() {
		return "read from store: " + readMilliSeconds / 1000.
				+ " sec, read postprocessing: " + readProcessMilliSeconds
				/ 1000. + " sec, write preprocessing: "
				+ writeProcessMilliSeconds / 1000. + " sec, write to store: "
				+ writeMilliSeconds / 1000. + " sec";
	}
}