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
package org.conqat.engine.code_clones.index.store.adapt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.conqat.engine.code_clones.index.Chunk;
import org.conqat.engine.code_clones.index.store.ICloneIndexStore;
import org.conqat.engine.persistence.store.IKeyValueCallback;
import org.conqat.engine.persistence.store.IStore;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.engine.persistence.store.util.StorageUtils;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.PairList;
import org.conqat.lib.commons.collections.UnmodifiableList;
import org.conqat.lib.commons.digest.MD5Digest;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.io.SerializationUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Clone index store based on ConQAT storage system.
 * <p>
 * We store the clone index into a single store. To differentiate between the
 * different types of keys, we prefix them with unique prefixes.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46003 $
 * @ConQAT.Rating GREEN Hash: 513D37AFF0D2CCFD53E089909CBA6303
 */
public class CloneIndexStoreAdapter implements ICloneIndexStore {

	/** Number of bytes in an int. */
	private static final int SIZE_OF_INT = Integer.SIZE / Byte.SIZE;

	/** Prefix used for storing options. */
	private static final String OPTION_PREFIX = "o";

	/** Prefix used for origin ID keys. */
	public static final String ORIGIN_PREFIX = "f";

	/** Prefix used for chunk hash keys. */
	private static final byte HASH_PREFIX = 'h';

	/** Size of the prefix. */
	private static final int HASH_PREFIX_SIZE = 1;

	/** The store used. */
	private final IStore store;

	/** Constructor. */
	public CloneIndexStoreAdapter(IStore store) {
		this.store = store;
	}

	/** {@inheritDoc} */
	@Override
	public Serializable getOption(String key) throws StorageException {
		try {
			byte[] value = store.get(getOptionsKey(key));
			if (value == null) {
				return null;
			}

			// we have to use the thread's context class loader, as in the
			// ConQAT world this is where the classes are loaded from
			return SerializationUtils.deserializeFromByteArray(value, Thread
					.currentThread().getContextClassLoader());
		} catch (IOException e) {
			throw new StorageException("Could not deserialize option: " + key,
					e);
		} catch (ClassNotFoundException e) {
			throw new StorageException("Could not create option: " + key, e);
		}
	}

	/** Returns the key used for storing options. */
	private static byte[] getOptionsKey(String key) {
		return StringUtils.stringToBytes((OPTION_PREFIX + key));
	}

	/** {@inheritDoc} */
	@Override
	public void setOption(String key, Serializable value)
			throws StorageException {
		try {
			store.put(getOptionsKey(key),
					SerializationUtils.serializeToByteArray(value));
		} catch (IOException e) {
			throw new StorageException("Could not set option: " + key, e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void batchInsertChunks(List<Chunk> chunks) throws StorageException {
		if (chunks.isEmpty()) {
			return;
		}

		String originId = chunks.get(0).getOriginId();
		PairList<byte[], byte[]> batchData = new PairList<byte[], byte[]>();
		batchData.add(getOriginKey(originId),
				compressChunksForOrigin(originId, chunks));
		for (Chunk chunk : chunks) {
			batchData.add(createChunkKey(chunk), createChunkValue(chunk));
		}
		store.put(batchData);
	}

	/** Returns the key used for originId. */
	private static byte[] getOriginKey(String originId) {
		return StringUtils.stringToBytes((ORIGIN_PREFIX + originId));
	}

	/** {@inheritDoc} */
	@Override
	public void removeChunks(String originId) throws StorageException {
		byte[] key = getOriginKey(originId);
		byte[] value = store.get(key);
		store.remove(key);

		if (value == null) {
			return;
		}

		List<Chunk> chunks = decompressChunksForOrigin(originId, value);
		List<byte[]> keys = new ArrayList<byte[]>();
		for (Chunk chunk : chunks) {
			keys.add(createChunkKey(chunk));
		}
		store.remove(keys);
	}

	/**
	 * Creates the key used for storing a chunk. This has to start with the hash
	 * to ensure we can efficiently access all chunks with same hash. In
	 * addition we include the origin and the starting position to ensure unique
	 * keys.
	 */
	private static byte[] createChunkKey(Chunk chunk) {
		byte[] originId = StringUtils.stringToBytes(chunk.getOriginId());
		byte[] result = new byte[HASH_PREFIX_SIZE + MD5Digest.MD5_BYTES
				+ originId.length + SIZE_OF_INT];
		result[0] = HASH_PREFIX;
		System.arraycopy(chunk.getChunkHash().getBytes(), 0, result,
				HASH_PREFIX_SIZE, MD5Digest.MD5_BYTES);
		System.arraycopy(originId, 0, result, HASH_PREFIX_SIZE
				+ MD5Digest.MD5_BYTES, originId.length);
		StorageUtils.insertInt(chunk.getFirstUnitIndex(), result, result.length
				- SIZE_OF_INT);
		return result;
	}

	/**
	 * Creates the value used when storing a chunk. These are all parts not
	 * contained in the key from {@link #createChunkKey(Chunk)}.
	 */
	private static byte[] createChunkValue(Chunk chunk) {
		byte[] result = new byte[5 * SIZE_OF_INT];
		int offset = 0;
		StorageUtils.insertInt(chunk.getFirstRawLineNumber(), result,
				SIZE_OF_INT * offset++);
		StorageUtils.insertInt(chunk.getLastRawLineNumber(), result,
				SIZE_OF_INT * offset++);
		StorageUtils.insertInt(chunk.getRawStartOffset(), result, SIZE_OF_INT
				* offset++);
		StorageUtils.insertInt(chunk.getRawEndOffset(), result, SIZE_OF_INT
				* offset++);
		StorageUtils.insertInt(chunk.getElementUnits(), result, SIZE_OF_INT
				* offset++);
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public List<Chunk> getChunksByOrigin(String originId)
			throws StorageException {
		byte[] value = store.get(getOriginKey(originId));
		if (value == null) {
			return null;
		}
		return decompressChunksForOrigin(originId, value);
	}

	/** {@inheritDoc} */
	@Override
	public UnmodifiableList<Chunk> getChunksByHashes(Set<MD5Digest> chunkHashes)
			throws StorageException {

		List<byte[]> prefixes = new ArrayList<byte[]>();
		for (MD5Digest hash : chunkHashes) {
			byte[] prefix = new byte[HASH_PREFIX_SIZE + MD5Digest.MD5_BYTES];
			prefix[0] = HASH_PREFIX;
			System.arraycopy(hash.getBytes(), 0, prefix, HASH_PREFIX_SIZE,
					MD5Digest.MD5_BYTES);
			prefixes.add(prefix);
		}

		final List<Chunk> result = new ArrayList<Chunk>();
		store.scan(prefixes, new IKeyValueCallback() {
			@Override
			public void callback(byte[] key, byte[] value) {

				Chunk chunk = extractChunk(key, value);

				// the scan method may call this callback from different
				// threads.
				synchronized (result) {
					result.add(chunk);
				}
			}
		});
		return CollectionUtils.asUnmodifiable(result);
	}

	/** Extracts the chunk from the given key/value pair. */
	private static Chunk extractChunk(byte[] key, byte[] value) {
		String originId = StringUtils.bytesToString(Arrays.copyOfRange(key,
				HASH_PREFIX_SIZE + MD5Digest.MD5_BYTES, key.length
						- SIZE_OF_INT));
		MD5Digest hash = new MD5Digest(Arrays.copyOfRange(key,
				HASH_PREFIX_SIZE, HASH_PREFIX_SIZE + MD5Digest.MD5_BYTES));
		int firstUnitIndex = StorageUtils.extractInt(key, key.length
				- SIZE_OF_INT);

		int offset = 0;
		int firstRawLine = StorageUtils.extractInt(value, SIZE_OF_INT
				* offset++);
		int lastRawLine = StorageUtils
				.extractInt(value, SIZE_OF_INT * offset++);
		int rawStartOffset = StorageUtils.extractInt(value, SIZE_OF_INT
				* offset++);
		int rawEndOffset = StorageUtils.extractInt(value, SIZE_OF_INT
				* offset++);
		int unitCount = StorageUtils.extractInt(value, SIZE_OF_INT * offset++);
		return new Chunk(originId, hash, firstUnitIndex, firstRawLine,
				lastRawLine, rawStartOffset, rawEndOffset, unitCount);
	}

	/** {@inheritDoc} */
	@Override
	public void close() {
		// nothing to do
	}

	/**
	 * Calculates a compressed representation for the given list of chunks which
	 * all belong to the same origin. The originId is not stored in the list!
	 */
	public static byte[] compressChunksForOrigin(String originId,
			List<Chunk> chunks) throws StorageException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		DataOutputStream dos;
		try {
			dos = new DataOutputStream(new GZIPOutputStream(bos));
		} catch (IOException e) {
			throw new StorageException("Had problems setting up compression!",
					e);
		}

		try {
			dos.writeInt(chunks.size());
			for (Chunk chunk : chunks) {
				CCSMAssert.isTrue(originId.equals(chunk.getOriginId()),
						"All origins in a batch should be equal!");
				writeChunkWithoutName(chunk, dos);
			}
			dos.close();
		} catch (IOException e) {
			CCSMAssert
					.fail("Should not be possible as we are writing to memory!");
		}
		return bos.toByteArray();
	}

	/** Writes the given chunk (without name) into the stream. */
	public static void writeChunkWithoutName(Chunk chunk, DataOutputStream dos)
			throws IOException {
		byte[] bytes = chunk.getChunkHash().getBytes();
		dos.write(bytes);
		dos.writeInt(chunk.getFirstUnitIndex());
		dos.writeInt(chunk.getFirstRawLineNumber());
		dos.writeInt(chunk.getLastRawLineNumber());
		dos.writeInt(chunk.getRawStartOffset());
		dos.writeInt(chunk.getRawEndOffset());
		dos.writeInt(chunk.getElementUnits());
	}

	/**
	 * Decompresses a list of chunks compressed with
	 * {@link #compressChunksForOrigin(String, List)}.
	 */
	public static List<Chunk> decompressChunksForOrigin(String originId,
			byte[] data) throws AssertionError {
		List<Chunk> result = new ArrayList<Chunk>();
		try {
			DataInputStream dis = new DataInputStream(new GZIPInputStream(
					new ByteArrayInputStream(data)));
			int size = dis.readInt();
			for (int i = 0; i < size; ++i) {
				result.add(readChunkWithoutName(originId, dis));
			}
		} catch (IOException e) {
			CCSMAssert.fail("Should not happen, as operates in memory!");
		}
		return result;
	}

	/** Reads a chunk without name. */
	public static Chunk readChunkWithoutName(String originId,
			DataInputStream dis) throws IOException {
		byte[] bytes = new byte[MD5Digest.MD5_BYTES];
		FileSystemUtils.safeRead(dis, bytes);
		return new Chunk(originId, new MD5Digest(bytes), dis.readInt(),
				dis.readInt(), dis.readInt(), dis.readInt(), dis.readInt(),
				dis.readInt());
	}
}
