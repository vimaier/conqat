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

import java.io.IOException;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

import org.conqat.engine.code_clones.core.Unit;
import org.conqat.engine.code_clones.index.store.ICloneIndexStore;
import org.conqat.engine.code_clones.normalization.provider.IUnitProvider;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.io.SerializationUtils;
import org.conqat.lib.scanner.ELanguage;

/**
 * Helper class to access persisted options in a {@link ICloneIndexStore}. For
 * reading access this class implements a cache as especially reads of
 * normalizations can be expensive.
 * 
 * @author $Author: steidl $
 * @version $Rev: 45216 $
 * @ConQAT.Rating GREEN Hash: 58927E5759612669E7C1B01D40730841
 */
public class PersistedOptions {

	/** Prefix used for keys to store normalization. */
	private static final String NORMALIZATION_KEY_PREFIX = "NORMALIZATION:";

	/** Key used to store the chunk length. */
	private static final String CHUNK_LENGTH_KEY = "CHUNK_LENGTH";

	/** The store used. */
	private final ICloneIndexStore store;

	/** Cache for normalizations. */
	private final Map<ELanguage, IUnitProvider<ITextResource, Unit>> normalizationCache = new EnumMap<ELanguage, IUnitProvider<ITextResource, Unit>>(
			ELanguage.class);

	/** Value used for unset/uncached chunk length. */
	public static final int UNSET = -1;

	/** Caches the chunk length. */
	private int cachedChunkLength = UNSET;

	/** Constructor. */
	public PersistedOptions(ICloneIndexStore store) {
		this.store = store;
	}

	/** Returns the normalization to be used for a given language. */
	public IUnitProvider<ITextResource, Unit> getNormalization(
			ELanguage language) throws StorageException {
		IUnitProvider<ITextResource, Unit> normalization = normalizationCache
				.get(language);
		if (normalization == null) {
			normalization = readNormalization(store, language);
			normalizationCache.put(language, normalization);
		}
		return normalization;
	}

	/**
	 * Returns the stored chunk length. If no length is set, an exception is
	 * thrown as this is a required field.
	 */
	public int getChunkLength() throws StorageException {
		if (cachedChunkLength <= 0) {
			cachedChunkLength = readChunkLength(store);
			if (cachedChunkLength <= 0) {
				throw new StorageException(
						"No chunk length found in store! Clone index not initialized?");
			}
		}
		return cachedChunkLength;
	}

	/**
	 * Sets the chunk length used. If the chunk length was already set and
	 * differs from the new provided value, a {@link StorageException} is
	 * thrown, as this would cause an inconsistent index.
	 * 
	 * @param chunkLength
	 *            the chunk length (must be positive).
	 */
	public static void setChunkLength(ICloneIndexStore store, int chunkLength)
			throws StorageException {
		CCSMAssert.isTrue(chunkLength > 0, "Only positive values allowed.");
		int oldLength = readChunkLength(store);
		if (oldLength < 0) {
			store.setOption(CHUNK_LENGTH_KEY, chunkLength);
		} else if (oldLength != chunkLength) {
			throw new StorageException(
					"Attempt to replace chunk length with different value!");
		}
	}

	/**
	 * Returns the stored chunk length. If no length is set, {@link #UNSET} is
	 * returned.
	 */
	public static int readChunkLength(ICloneIndexStore store)
			throws StorageException {
		Integer chunkLength = (Integer) store.getOption(CHUNK_LENGTH_KEY);
		if (chunkLength == null) {
			return UNSET;
		}
		return chunkLength;
	}

	/**
	 * Sets the normalization for the given language. If the normalization was
	 * already set and differs from the new provided normalization, a
	 * {@link StorageException} is thrown, as this would cause an inconsistent
	 * index. Equality of normalizations is checked by their serializations.
	 */
	public static void setNormalization(ICloneIndexStore store,
			ELanguage language,
			IUnitProvider<? extends ITextResource, Unit> normalization)
			throws StorageException {
		CCSMAssert.isNotNull(normalization);

		IUnitProvider<ITextResource, Unit> oldNormalization = readNormalization(
				store, language);
		if (oldNormalization == null) {
			store.setOption(normalizationKey(language), normalization);
		} else {
			ensureNormalizationsEqual(language, normalization, oldNormalization);
		}
	}

	/**
	 * Ensures that both normalizations are equals by serializing them and
	 * comparing bytes.
	 */
	private static void ensureNormalizationsEqual(ELanguage language,
			IUnitProvider<? extends ITextResource, Unit> normalization,
			IUnitProvider<? extends ITextResource, Unit> oldNormalization)
			throws StorageException {
		try {
			byte[] normalizationData = SerializationUtils
					.serializeToByteArray(normalization);
			byte[] oldNormalizationData = SerializationUtils
					.serializeToByteArray(oldNormalization);
			if (!Arrays.equals(normalizationData, oldNormalizationData)) {
				throw new StorageException(
						"Attempt to replace normalization for language "
								+ language + " with different normalization!");
			}
		} catch (IOException e) {
			throw new StorageException("Error during serialization!", e);
		}
	}

	/** Reads a normalization from a store. */
	@SuppressWarnings("unchecked")
	private static IUnitProvider<ITextResource, Unit> readNormalization(
			ICloneIndexStore store, ELanguage language) throws StorageException {
		return (IUnitProvider<ITextResource, Unit>) store
				.getOption(normalizationKey(language));
	}

	/** Returns the name used to store normalization for the given language. */
	private static String normalizationKey(ELanguage language) {
		return NORMALIZATION_KEY_PREFIX + language.name();
	}
}