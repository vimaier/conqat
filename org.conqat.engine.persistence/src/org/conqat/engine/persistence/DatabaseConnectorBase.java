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

import java.sql.Connection;
import java.sql.SQLException;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.core.IShutdownHook;

/**
 * Base class for database connectors that handles closing of the database
 * connection.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 0CFDD2DF8A9DDC973AFE934BE0AA122D
 */
public abstract class DatabaseConnectorBase extends ConQATProcessorBase {

	/** {@inheritDoc} */
	@Override
	public Connection process() throws ConQATException {
		try {
			Connection connection = setupConnection();
			registerShutdownHook(connection);
			return connection;
		} catch (SQLException e) {
			throw new ConQATException("Database error: " + e.getMessage());
		}
	}

	/**
	 * Register shutdown hook that closes the connection.
	 */
	private void registerShutdownHook(final Connection connection) {
		getProcessorInfo().registerShutdownHook(new IShutdownHook() {
			@Override
			public void performShutdown() throws ConQATException {
				try {
					connection.close();
				} catch (SQLException e) {
					throw new ConQATException("Error closing db connection: "
							+ e.getMessage());
				}

			}
		}, false);
	}

	/** Template method to create database connection. */
	protected abstract Connection setupConnection() throws SQLException,
			ConQATException;

}