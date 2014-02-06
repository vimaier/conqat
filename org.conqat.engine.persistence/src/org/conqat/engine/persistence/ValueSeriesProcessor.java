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

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.statistics.DateValueSeries;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.date.DateUtils;

/**
 * {@ConQAT.Doc}
 * 
 * This class uses a somewhat simplified strategy for SQL exception handling. If
 * an SQL exception occurs, we assume that no further reasonable processing will
 * be done. Therefore, the code does not always ensure that all free resources
 * are closed explicitly (BTW: According to the JDBC spec free resources should
 * be close anyway during GC. However, some JDCB driver do not fully comply to
 * this.)
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: E5BC01C63E5F3DB5AB1C208D49533E9B
 */
@AConQATProcessor(description = "This processor takes a single numeric value "
		+ "and stores it in a database table with the current time (or a "
		+ "explicitly specified time and returns "
		+ "a org.conqat.engine.commons.statistics.DateValueSeries with all values stored "
		+ "in the table. If the database table does not exist it will be "
		+ "created. This is currently tested for Microsoft SQL Server only.")
public class ValueSeriesProcessor extends ValueSeriesCreatorBase {

	/** The node the value is attached to. */
	protected IConQATNode node;

	/** Key for the value. */
	protected String key;

	/**
	 * If set to true, exceptions occurring during insertion are treated as log
	 * messages on level debug
	 */
	private boolean lenientInsertionBehavior = false;

	/**
	 * The time to use of the stored value. The driver instantiates the
	 * processor just before running it, so the difference between this value
	 * and the actual time of writing to the database should be neglectable.
	 */
	private long time = DateUtils.getNow().getTime();

	/** Set node and key to obtain the value from. */
	@AConQATParameter(name = "value", minOccurrences = 1, maxOccurrences = 1, description = "Describes the value being stored in the database.")
	public void setNodeAndKey(
			@AConQATAttribute(name = "noderef", description = "Reference to the node containing the value.") IConQATNode node,
			@AConQATAttribute(name = "name", description = "Name of the key where the value is stored.") String key) {
		this.node = node;
		this.key = key;
	}

	/** Set time. */
	@AConQATParameter(name = "time", maxOccurrences = 1, description = ""
			+ "The time to use for the stored value. If this parameter is not "
			+ "present, the current point in time is used.")
	public void setTime(
			@AConQATAttribute(name = "value", description = "Time value defined by a Data object") Date time) {
		this.time = time.getTime();
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "insertion-behavior", description = "If set to true, exceptions during insertion of the new value are treated as debug log messages.", minOccurrences = 0, maxOccurrences = 1)
	public void setLenientInsertionMode(
			@AConQATAttribute(name = "lenient", description = "Default is false. Exceptions are treated as errors.") boolean lenientInsertionBehavior) {
		this.lenientInsertionBehavior = lenientInsertionBehavior;
	}

	/** {@inheritDoc} */
	@Override
	public DateValueSeries process() throws ConQATException {
		storeValue();
		return createSeries();
	}

	/**
	 * Store the the value in the database.
	 * 
	 * @throws ConQATException
	 *             if the value is <code>null</code>, not a number or a database
	 *             exception occurs
	 */
	private void storeValue() throws ConQATException {
		Object value = node.getValue(key);
		if (value == null) {
			throw new ConQATException("Value not found.");
		}
		if (!(value instanceof Number)) {
			throw new ConQATException("Value is not a number.");
		}

		getLogger().debug("Storing value: " + value);

		try {
			insertValue(value);
		} catch (SQLException e) {
			throw wrap(e);
		}
	}

	/** Insert value into datbase */
	private void insertValue(Object value) throws SQLException {
		ensureTableExists();
		Statement statement = dbConnection.createStatement();
		try {
			Number number = (Number) value;
			statement.executeUpdate(createInsertString(number));
		} catch (SQLException e) {
			if (lenientInsertionBehavior) {
				getLogger().debug("Could not insert value: " + e.getMessage());
			} else {
				throw e;
			}
		} finally {
			DatabaseUtils.closeSilently(statement);
		}
	}

	/**
	 * Ensure that database table exists.
	 * 
	 * @throws SQLException
	 *             if a problem with database connection occurred.
	 */
	private void ensureTableExists() throws SQLException {
		String createTableString = createCreateTableString();
		Statement statement = dbConnection.createStatement();

		try {
			statement.executeUpdate(createTableString);
		} catch (SQLException e) {
			// if the error is caused by a table that already exists, everything
			// is fine. Otherwise, we throw an exception. This can, e.g., be
			// caused by a invalid table name (see also CR#3611).
			String sqlState = e.getSQLState();
			if (!"S0001".equals(sqlState) && !"42S01".equals(sqlState)) {
				// the SQLSTATEs are defined the X/Open consortium. We are
				// interested in the state for
				// "Base table or view already exists" which now
				// has the state identifier 42S01. However, ODBC 2.0
				// implementation use the older "S0001" identifier.
				// BTW: X/Open is (or was) a consortium who's role I don't fully
				// understand. Details on X/Open can be found at
				// http://en.wikipedia.org/wiki/X/Open. Details on the error
				// codes can (amongst others) be found here
				// http://www.kieser.net/linux/jdbc_xopen.html and here
				// http://publib.boulder.ibm.com/infocenter/dzichelp/v2r2/index.jsp?topic=/com.ibm.db2z9.doc.odbc/src/tpc/db2z_sqlstmig.htm.
				// I didn't find an official specification.
				throw e;
			}
		}
		statement.close();
	}

	/** Create SQL string for table creation. */
	private String createCreateTableString() {
		StringBuilder result = new StringBuilder();
		result.append("CREATE TABLE ");
		result.append(tableName);
		result.append(" (" + DATE + " bigint PRIMARY KEY, " + VALUE + " real) ");
		return result.toString();
	}

	/** Create SQL string for insertion. */
	private String createInsertString(Number number) {
		StringBuilder result = new StringBuilder();
		result.append("INSERT INTO ");
		result.append(tableName);
		result.append(" (" + DATE + ", " + VALUE + ") VALUES (");
		result.append(time);
		result.append(", ");
		result.append(number.doubleValue());
		result.append(")");
		return result.toString();
	}
}