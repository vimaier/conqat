/*-----------------------------------------------------------------------+
 | com.teamscale.core
 |                                                                       |
   $Id: CassandraStorageSystemTest.java 46902 2013-11-13 10:07:17Z heinemann $            
 |                                                                       |
 | Copyright (c)  2009-2013 CQSE GmbH                                 |
 +-----------------------------------------------------------------------*/
package org.conqat.engine.persistence.store.cassandra;

import java.io.File;

import org.conqat.engine.persistence.store.IStorageSystem;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.engine.persistence.store.StorageSystemTestBase;
import org.conqat.engine.persistence.store.mem.InMemoryStorageSystem;

/**
 * Tests the {@link CassandraStorageSystem}.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46902 $
 * @ConQAT.Rating GREEN Hash: EA141B6700D25D165F6420DD3516FBBB
 */
public class CassandraStorageSystemTest extends StorageSystemTestBase {

	/**
	 * Flag that disables these tests in the repository and on the build server.
	 * If a cassandra instance is running on the current machine, this can be
	 * set to true.
	 */
	private static final boolean CASSANDRA_RUNNING = Boolean.valueOf(System
			.getProperty("conqat.test.cassandra-running"));

	/** {@inheritDoc} */
	@Override
	protected void setUp() throws Exception {
		if (CASSANDRA_RUNNING) {
			CassandraStorageSystem.clearAllData();
		}
		super.setUp();
	}

	/** {@inheritDoc} */
	@Override
	protected IStorageSystem openStorage(File baseDir) throws StorageException {
		if (!CASSANDRA_RUNNING) {
			return new InMemoryStorageSystem(baseDir);
		}
		return new CassandraStorageSystem();
	}
}
