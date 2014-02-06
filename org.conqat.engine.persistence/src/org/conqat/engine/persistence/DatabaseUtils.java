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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.assertion.CCSMAssert;

/**
 * Utility methods for database handling
 * 
 * @author juergens
 * @author $Author: juergens $
 * @version $Rev: 35197 $
 * @ConQAT.Rating GREEN Hash: 06CEC3E5D2F065C92FCAD436014D4E25
 */
public class DatabaseUtils {

	/**
	 * Executes an update query.
	 * 
	 * @return either (1) the row count for SQL Data Manipulation Language (DML)
	 *         statements or (2) 0 for SQL statements that return nothing.
	 */
	public static int executeUpdate(Connection dbConnection, String query)
			throws SQLException {
		Statement statement = dbConnection.createStatement();
		int updated = statement.executeUpdate(query);
		statement.close();
		return updated;
	}

	/** Close {@link Statement} and swallow exception, if closing failed. */
	public static void closeSilently(Statement statement) {
		try {
			if (statement != null) {
				statement.close();
			}
		} catch (SQLException e) {
			// swallow exception
		}
	}

	/** Close {@link ResultSet} and swallow exception, if closing failed. */
	public static void closeSilently(ResultSet result) {
		try {
			if (result != null) {
				result.close();
			}
		} catch (SQLException e) {
			// swallow exception
		}
	}

	/**
	 * Executes a select query. The statement is returned. Use
	 * statement.getResults() to access results. The statement is returned so
	 * that the caller can close it himself.
	 */
	public static Statement executeQuery(Connection dbConnection,
			String selectString) throws SQLException {
		Statement statement = dbConnection.createStatement(
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		statement.executeQuery(selectString);
		return statement;
	}

	/** Executes a statement */
	public static void executeAndClose(Statement stmt) throws SQLException,
			ConQATException {
		try {
			int[] results = stmt.executeBatch();

			for (int result : results) {
				if (result == Statement.EXECUTE_FAILED) {
					throw new ConQATException(
							"Execution of insert statement failed");
				}
			}
		} finally {
			stmt.close();
		}
	}

	/** Value stored if date is null */
	private static final int NULL_VALUE = -1;

	/** Return the time, or -1, if <code>null is entered</code> */
	public static long nullDateToLong(Date date) {
		if (date == null) {
			return NULL_VALUE;
		}
		return date.getTime();
	}

	/**
	 * Reads date from result set. If no date is stored, <code>null</code> is
	 * returned.
	 */
	public static Date getNullDate(ResultSet result, String columnName)
			throws SQLException {
		long birthTime = result.getLong(columnName);
		if (birthTime == NULL_VALUE) {
			return null;
		}
		return new Date(birthTime);
	}

	/**
	 * Executes select statement and returns the int value stored in the
	 * selected column, or <code>null</code>, if no record was returned. Throws
	 * an assertion error if more than one result was returned.
	 */
	public static Integer selectSingleIntValue(Connection dbConnection,
			String select, String columnName) throws SQLException {

		Statement stmt = DatabaseUtils.executeQuery(dbConnection, select);
		ResultSet result = stmt.getResultSet();
		if (!result.next()) {
			return null;
		}
		int intValue = result.getInt(columnName);

		// assert that only a single entry was returned for select query
		boolean hasNext = result.next();
		stmt.close();
		CCSMAssert.isFalse(hasNext,
				"More than one entry found for select statement: " + select);

		return intValue;
	}
}