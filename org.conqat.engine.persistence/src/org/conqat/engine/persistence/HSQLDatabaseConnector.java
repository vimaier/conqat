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
package org.conqat.engine.persistence;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.hsqldb.jdbcDriver;

/**
 * {@ConQAT.Doc}
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: E03561C1429B5321C83F364517253320
 */
@AConQATProcessor(description = "This processor creates a connection "
		+ "to a file-based HSQLDB database.")
public class HSQLDatabaseConnector extends DatabaseConnectorBase {

	/** Database file. */
	private String dbFilePath;

	/** Flag for using cached tables. */
	private boolean useCachedTables = false;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "file", minOccurrences = 1, maxOccurrences = 1, description = "Database file.")
	public void setFile(
			@AConQATAttribute(name = "path", description = "Path to database file.") String path) {
		dbFilePath = FileSystemUtils.normalizeSeparators(path);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "tables", minOccurrences = 0, maxOccurrences = 1, description = "Set the type of tables used.")
	public void setUseCachedTables(
			@AConQATAttribute(name = "cached", description = "If this is set to true, HSQLDB cached tables are used. "
					+ "These are binary and do not have to reside in memory. Default is false.") boolean useCachedTables) {
		this.useCachedTables = useCachedTables;
	}

	/** {@inheritDoc} */
	@Override
	protected Connection setupConnection() throws SQLException, ConQATException {
		File dbFile = new File(dbFilePath);

		try {
			FileSystemUtils.ensureParentDirectoryExists(dbFile);
		} catch (IOException e) {
			throw new ConQATException("Could not acces file: " + dbFile);
		}

		try {
			Class.forName(jdbcDriver.class.getName());
		} catch (ClassNotFoundException e) {
			throw new ConQATException("Can't load driver for HSQLDB.");
		}

		Connection connection = DriverManager.getConnection("jdbc:hsqldb:file:"
				+ dbFilePath + ";shutdown=true", "SA", "");

		if (useCachedTables) {
			DatabaseUtils.executeUpdate(connection,
					"SET PROPERTY \"hsqldb.default_table_type\" 'cached' ;");
		}

		return connection;
	}

}