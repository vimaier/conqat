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
package org.conqat.engine.persistence.store.cassandra;

import java.util.Arrays;

import me.prettyprint.cassandra.model.BasicColumnDefinition;
import me.prettyprint.cassandra.serializers.BytesArraySerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.ThriftKsDef;
import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
import me.prettyprint.cassandra.service.template.ThriftColumnFamilyTemplate;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.exceptions.HectorException;
import me.prettyprint.hector.api.factory.HFactory;

import org.conqat.engine.persistence.store.IStore;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.engine.persistence.store.base.StorageSystemBase;

/**
 * Storage system using Apache Cassandra.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46935 $
 * @ConQAT.Rating GREEN Hash: CB3FF906A666A501F93780B88DFA3BC9
 */
public class CassandraStorageSystem extends StorageSystemBase {

	/** The default port Cassandra is running on. */
	public static final int DEFAULT_PORT = 9160;

	/**
	 * The name of the keyspace. This is kept short as it adds to the storage
	 * space required.
	 */
	/* package */static final String KEYSPACE_NAME = "K";

	/**
	 * The name of the column family. This is kept short as it adds to the
	 * storage space required.
	 */
	/* package */static final String COLUMN_FAMILY_NAME = "F";

	/**
	 * The name of the column. This is kept short as it adds to the storage
	 * space required.
	 */
	/* package */static final String COLUMN_NAME = "c";

	/** The keyspace used for storing data. */
	private final Keyspace keyspace;

	/** Template for the column family. */
	private final ColumnFamilyTemplate<byte[], String> template;

	/** Constructor. */
	public CassandraStorageSystem() throws StorageException {
		this(DEFAULT_PORT);
	}

	/** Constructor. */
	public CassandraStorageSystem(int port) throws StorageException {
		try {
			Cluster cluster = createCluster(port);
			if (cluster.describeKeyspace(KEYSPACE_NAME) == null) {
				createKeyspace(cluster);
			}
			keyspace = HFactory.createKeyspace(KEYSPACE_NAME, cluster);
			template = new ThriftColumnFamilyTemplate<byte[], String>(keyspace,
					COLUMN_FAMILY_NAME, BytesArraySerializer.get(),
					StringSerializer.get());
		} catch (HectorException e) {
			throw new StorageException(e);
		}
	}

	/** Factory method for creating the cluster. */
	private static Cluster createCluster(int port) {
		return HFactory.getOrCreateCluster("cluster1", "localhost:" + port);
	}

	/** Creates the keyspace. */
	private void createKeyspace(Cluster cluster) {
		ColumnFamilyDefinition cfDef = HFactory.createColumnFamilyDefinition(
				KEYSPACE_NAME, COLUMN_FAMILY_NAME, ComparatorType.BYTESTYPE);
		cfDef.setGcGraceSeconds(1);
		cfDef.setCompactionStrategy("LeveledCompactionStrategy");

		BasicColumnDefinition column = new BasicColumnDefinition();
		column.setName(StringSerializer.get().toByteBuffer(COLUMN_NAME));
		column.setValidationClass(ComparatorType.BYTESTYPE.getClassName());
		cfDef.addColumnDefinition(column);

		KeyspaceDefinition newKeyspace = HFactory.createKeyspaceDefinition(
				KEYSPACE_NAME, ThriftKsDef.DEF_STRATEGY_CLASS, 1,
				Arrays.asList(cfDef));
		cluster.addKeyspace(newKeyspace, true);
	}

	/** {@inheritDoc} */
	@Override
	public IStore openStore(String name) {
		return new CassandraStore(name, keyspace, template);
	}

	/** {@inheritDoc} */
	@Override
	public void close() {
		// nothing to do
	}

	/** Removes all data. This is used for testing. */
	/* package */static void clearAllData() {
		// As this method is only used from tests, we can use the default port
		// here.
		Cluster cluster = createCluster(DEFAULT_PORT);
		if (cluster.describeKeyspace(KEYSPACE_NAME) != null) {
			cluster.dropKeyspace(KEYSPACE_NAME);
		}
	}
}
