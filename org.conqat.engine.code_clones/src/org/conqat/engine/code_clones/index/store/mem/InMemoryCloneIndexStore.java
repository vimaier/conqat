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
package org.conqat.engine.code_clones.index.store.mem;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.conqat.engine.code_clones.index.Chunk;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.ListMap;
import org.conqat.lib.commons.collections.UnmodifiableList;
import org.conqat.lib.commons.digest.MD5Digest;

/**
 * A clone index store that keeps all data in memory.
 * 
 * @author hummelb
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: 5D71B3967ACA30B8DEAD8A73BEE6212D
 */
public class InMemoryCloneIndexStore extends MemoryStoreBase {

	/** Map for storing {@link Chunk}s by originId. */
	protected final ListMap<String, Chunk> byOrigin = new ListMap<String, Chunk>();

	/** Map for storing {@link Chunk}s by hash. */
	protected final ListMap<MD5Digest, Chunk> byHash = new ListMap<MD5Digest, Chunk>();

	/** {@inheritDoc} */
	@Override
	public void batchInsertChunks(List<Chunk> chunks) {
		for (Chunk chunk : chunks) {
			byOrigin.add(chunk.getOriginId(), chunk);
			byHash.add(chunk.getChunkHash(), chunk);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void removeChunks(String originId) {
		List<Chunk> chunks = byOrigin.getCollection(originId);
		byOrigin.removeCollection(originId);

		for (Chunk chunk : chunks) {
			List<Chunk> list = byHash.getCollection(chunk.getChunkHash());
			list.remove(chunk);
		}
	}

	/** {@inheritDoc} */
	@Override
	public List<Chunk> getChunksByOrigin(String originId) {
		return byOrigin.getCollection(originId);
	}

	/** {@inheritDoc} */
	@Override
	public UnmodifiableList<Chunk> getChunksByHashes(Set<MD5Digest> chunkHashes) {
		List<Chunk> result = new ArrayList<Chunk>();
		for (MD5Digest hash : chunkHashes) {
			result.addAll(byHash.getCollection(hash));
		}
		return CollectionUtils.asUnmodifiable(result);
	}
}
