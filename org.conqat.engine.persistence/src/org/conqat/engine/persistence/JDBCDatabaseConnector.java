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
import java.sql.DriverManager;
import java.sql.SQLException;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;

/**
 * This processor creates a database interface object based on JDBC.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 01A94356A5AF662A3F3B36FA1D5A35FA
 */
@AConQATProcessor(description = ""
		+ "This processor creates a database interface object based on JDBC.")
public class JDBCDatabaseConnector extends DatabaseConnectorBase {

	/** Name of the JDBC driver class. */
	private String driverClassName;

	/** User name for database. */
	private String userName;

	/** Password for database. */
	private String password;

	/** JDBC connection string. */
	private String connectionString;

	/** Set name of the driver class. */
	@AConQATParameter(name = "driver", minOccurrences = 1, maxOccurrences = 1, description = "JDBC driver class")
	public void setDriverClassName(
			@AConQATAttribute(name = "class-name", description = "Name of the driver class.") String driverClassName) {
		this.driverClassName = driverClassName;
	}

	/** Set authentification. */
	@AConQATParameter(name = ConQATParamDoc.AUTH_NAME, minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "Database authentification information.")
	public void setAuthentification(
			@AConQATAttribute(name = ConQATParamDoc.AUTH_USER_NAME, description = ConQATParamDoc.AUTH_USER_DESC) String userName,
			@AConQATAttribute(name = ConQATParamDoc.AUTH_PASS_NAME, description = ConQATParamDoc.AUTH_PASS_DESC) String password) {
		this.userName = userName;
		this.password = password;
	}

	/** Set connection string. */
	@AConQATParameter(name = "connection", minOccurrences = 1, maxOccurrences = 1, description = "Connection string")
	public void setConnectionString(
			@AConQATAttribute(name = "string", description = "Connection string") String connectionString) {
		this.connectionString = connectionString;
	}

	/** {@inheritDoc} */
	@Override
	protected Connection setupConnection() throws SQLException, ConQATException {
		try {
			Class.forName(driverClassName);
		} catch (ClassNotFoundException e) {
			throw new ConQATException("Can't load driver class: "
					+ driverClassName);
		}

		return DriverManager
				.getConnection(connectionString, userName, password);
	}
}