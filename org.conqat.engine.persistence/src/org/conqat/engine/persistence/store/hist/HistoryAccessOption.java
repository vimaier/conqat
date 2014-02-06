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
package org.conqat.engine.persistence.store.hist;

import org.conqat.engine.persistence.store.IStore;
import org.conqat.lib.commons.assertion.CCSMPre;

/**
 * This class describes the options used to access a historized store.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 45610 $
 * @ConQAT.Rating GREEN Hash: 72D2385F88EE753EACCA3BC7AFC7F8DD
 */
public class HistoryAccessOption {

	/** The access mode. */
	private final EHistoryAccessMode access;

	/** The timestamp (interpretation depends on {@link #access}). */
	private final long timestamp;

	/** Hidden constructor. Use one of the factory methods instead, */
	private HistoryAccessOption(EHistoryAccessMode access, long timestamp) {
		CCSMPre.isTrue(timestamp > 0, "Timestamp must be positive.");
		this.access = access;
		this.timestamp = timestamp;
	}

	/** Read-only access to the head revision. */
	public static HistoryAccessOption readHead() {
		return new HistoryAccessOption(EHistoryAccessMode.READ_HEAD, 1);
	}

	/** Read from specified timestamp. */
	public static HistoryAccessOption readTimestamp(long timestamp) {
		return new HistoryAccessOption(EHistoryAccessMode.READ_TIMESTAMP,
				timestamp);
	}

	/** Write as new timestamp (reading from head). */
	public static HistoryAccessOption readHeadWriteTimestamp(long timestamp) {
		return new HistoryAccessOption(EHistoryAccessMode.WRITE_TIMESTAMP,
				timestamp);
	}

	/** Factory method for applying the options by wrapping a store. */
	public IStore createStore(IStore store) {
		switch (access) {
		case READ_HEAD:
			return new HeadReadOnlyHistorizingStore(store);
		case READ_TIMESTAMP:
			return new TimestampReadOnlyHistorizingStore(store, timestamp);
		case WRITE_TIMESTAMP:
			return new HeadInsertingHistorizingStore(store, timestamp);

		default:
			throw new AssertionError("Unkown access mode: " + access);
		}
	}

	/**
	 * Returns the timestamp of this history access option. This is 1 in case of
	 * reading head.
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/** Returns whether this is configured to read head */
	public boolean isReadHead() {
		return access == EHistoryAccessMode.READ_HEAD;
	}

	/** The access flags. */
	protected static enum EHistoryAccessMode {

		/** Read from head. */
		READ_HEAD,

		/** Read from specific timestamp. */
		READ_TIMESTAMP,

		/** Read from head, write to timestamp. */
		WRITE_TIMESTAMP
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return access + "@" + timestamp;
	}
}
