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

import java.util.EnumMap;
import java.util.Map;

import org.conqat.engine.code_clones.core.Unit;
import org.conqat.engine.code_clones.detection.UnitProcessorBase;
import org.conqat.engine.code_clones.index.store.ICloneIndexStore;
import org.conqat.engine.code_clones.normalization.provider.IUnitProvider;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.driver.instance.ConQATStringPool;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.engine.sourcecode.resource.TokenElementProcessorBase;
import org.conqat.lib.scanner.ELanguage;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: juergens $
 * @version $Rev: 41414 $
 * @ConQAT.Rating GREEN Hash: 38CE049E88C86D8F419928B295F9C6D9
 */
@AConQATProcessor(description = "Processor for constructing a clone index, which is a datastructure "
		+ "for performing fast clone lookup. "
		+ "This processor also calculates and annotates the unit size of the files.")
public class CloneIndexBuilder extends TokenElementProcessorBase {

	/** Default chunk length used for the constructed index. */
	protected final static int DEFAULT_CHUNK_LENGTH = 5;

	/** Frequency of logging in number of processed files between log messages. */
	protected final static int LOGGING_FREQUENCY = 100;

	/** The store. */
	protected ICloneIndexStore store;

	/** The index */
	private CloneIndex index;

	/** Counter that keeps track of processed files */
	private int fileCount = 0;

	/** Normalizations used. */
	private final Map<ELanguage, IUnitProvider<ITokenResource, Unit>> normalizations = new EnumMap<ELanguage, IUnitProvider<ITokenResource, Unit>>(
			ELanguage.class);

	/** The chunk length used for the index. */
	private int chunkLength = DEFAULT_CHUNK_LENGTH;

	/** The number of elements to process (used for logging progress messages). */
	private int overallElementsCount;

	/**
	 * Key that contains flag that determines whether elements get ignored.
	 * Influences log message generation, but not unit draining.
	 */
	protected String ignoreKey;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "store", description = "The clone index store used to access and persist the clone index.", minOccurrences = 1, maxOccurrences = 1)
	public void setStore(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) ICloneIndexStore store) {
		this.store = store;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "chunk", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Allows to set the chunk length used. A higher chunk length improves performance, "
			+ "but the chunk length also marks the minimal clone length being detectable. "
			+ "If this is not used, a default chunk length of "
			+ DEFAULT_CHUNK_LENGTH + " is used.")
	public void setChunkLength(
			@AConQATAttribute(name = "length", description = "The chunk length used for the index (must be positive).") int chunkLength)
			throws ConQATException {
		if (chunkLength <= 0) {
			throw new ConQATException("Chunk index must be positive!");
		}
		this.chunkLength = chunkLength;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "normalization", description = "Sets the normalization used for a given language.", minOccurrences = 1)
	public void setNormalization(
			@AConQATAttribute(name = "language", description = "The language for which the normalization applies.") ELanguage language,
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) IUnitProvider<ITokenResource, Unit> normalization)
			throws ConQATException {

		if (normalizations.put(language, normalization) != null) {
			throw new ConQATException(
					"Duplicate normalization applied for language " + language);
		}
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.IGNORE_NAME, minOccurrences = 0, maxOccurrences = 1, description = ConQATParamDoc.IGNORE_DESC)
	public void setIgnoreKey(
			@AConQATAttribute(name = ConQATParamDoc.IGNORE_KEY_NAME, description = ConQATParamDoc.IGNORE_KEY_DESC) String ignoreKey) {
		this.ignoreKey = ignoreKey;
	}

	/** {@inheritDoc} */
	@Override
	protected void setUp(ITokenResource root) throws ConQATException {
		setOptions();
		index = new CloneIndex(store, getLogger());
		overallElementsCount = ResourceTraversalUtils.listNonIgnoredElements(
				root, ignoreKey, ITextElement.class).size();
	}

	/** {@inheritDoc} */
	@Override
	protected void processElement(ITokenElement element) throws ConQATException {
		if (!NodeUtils.isIgnored(element, ignoreKey)) {
			int units = index.insertFile(element);
			element.setValue(UnitProcessorBase.UNITS_KEY, units);

			if (++fileCount % LOGGING_FREQUENCY == 0) {
				getLogger().info(
						"Completed " + fileCount + " elements of "
								+ overallElementsCount + " ["
								+ index.getPerformanceInfo() + "]");
			}

			// Clear the string pool. Otherwise string pool fills up until
			// processor finishes, i.e., until all files are processed.
			// This requires too much memory (and provides no benefit
			// for index creation)
			ConQATStringPool.clear();
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void finish(ITokenResource root) {
		getLogger().info("Overall performance: " + index.getPerformanceInfo());
	}

	/** Sets the options used for the store. */
	private void setOptions() throws StorageException {
		PersistedOptions options = new PersistedOptions(store);
		for (ELanguage language : normalizations.keySet()) {
			if (options.getNormalization(language) != null) {
				getLogger().info(
						"Reusing existing normalization for language "
								+ language);
			} else {
				PersistedOptions.setNormalization(store, language,
						normalizations.get(language));
			}
		}

		PersistedOptions.setChunkLength(store, chunkLength);
	}
}