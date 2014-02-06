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
package org.conqat.engine.code_clones.index.store;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.conqat.engine.code_clones.index.Chunk;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.lib.commons.collections.UnmodifiableList;
import org.conqat.lib.commons.digest.MD5Digest;

/**
 * Interface for a clone index store. A clone index store is used to persist
 * chunk information collected and used during index-based clone detection.
 * 
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: 260FF4D89E29EDB758204B96DABFBFDF
 */
public interface ICloneIndexStore {

	/**
	 * Returns the object stored under the given key. If no object is stored,
	 * <code>null</code> should be returned.
	 */
	Serializable getOption(String key) throws StorageException;

	/**
	 * Stores an object under the given key. This is used to store certain clone
	 * detection options which have to be reused when updating data in the
	 * store.
	 */
	void setOption(String key, Serializable value) throws StorageException;

	/**
	 * Returns all stored {@link Chunk} objects for the given originId. Returns
	 * <code>null</code> if the file was not found.
	 */
	List<Chunk> getChunksByOrigin(String originId) throws StorageException;

	/**
	 * Returns all {@link Chunk}s stored for one of the given chunk hashes.
	 */
	UnmodifiableList<Chunk> getChunksByHashes(Set<MD5Digest> chunkHashes)
			throws StorageException;

	/**
	 * Inserts a batch of {@link Chunk}s. All chunks must belong to the same
	 * origin.
	 */
	void batchInsertChunks(List<Chunk> chunks) throws StorageException;

	/**
	 * Removes all chunks from the store which belong to the given originId
	 * name.
	 */
	void removeChunks(String originId) throws StorageException;

	/**
	 * Closes this store. No operations should be performed on the store after
	 * calling this method.
	 */
	void close() throws StorageException;
}