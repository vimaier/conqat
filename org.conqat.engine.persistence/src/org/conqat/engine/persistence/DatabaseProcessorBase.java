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
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;

/**
 * Base class for processors that work on a database table.
 * 
 * @author Elmar Juergens
 * @author $Author: juergens $
 * @version $Rev: 35197 $
 * @ConQAT.Rating GREEN Hash: 3A9A2CB7A444DA3B91507AD10E18E24D
 */
public abstract class DatabaseProcessorBase extends ConQATProcessorBase {

	/** Database connection. */
	protected Connection dbConnection;

	/** Name of the database table. */
	protected String tableName;

	/** Set database details. */
	@AConQATParameter(name = "database", minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "Describes the database and table used for storing the value.")
	public void setTable(
			@AConQATAttribute(name = "connection", description = "Database connection.") Connection dbConnection,
			@AConQATAttribute(name = "table", description = "The name of the table.") String tableName) {
		this.dbConnection = dbConnection;
		this.tableName = tableName;
	}

	/** Wraps an {@link SQLException} into a {@link ConQATException} */
	protected ConQATException wrap(SQLException e) {
		return new ConQATException("Problem with database.", e);
	}

}