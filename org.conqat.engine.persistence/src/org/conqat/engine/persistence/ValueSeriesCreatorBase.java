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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import org.conqat.engine.commons.statistics.DateValueSeries;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.assertion.CCSMAssert;

/**
 * Base class for processors that read value series from a database.
 * 
 * @author juergens
 * @author $Author: juergens $
 * @version $Rev: 35197 $
 * @ConQAT.Rating GREEN Hash: CB4A4345EB08CDE99DDEECD508B80508
 */
public abstract class ValueSeriesCreatorBase extends DatabaseProcessorBase {

	/** Name of date column in database table */
	protected static final String DATE = "Date";

	/** Name of value column in database table */
	protected static final String VALUE = "Value";

	/** If set, only dates after this time stamp are included */
	private Date after = null;

	/** If set, only dates before this time stamp are included */
	private Date before = null;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "after", description = "If set, only dates after this timestamp are included", minOccurrences = 0, maxOccurrences = 1)
	public void setAfter(
			@AConQATAttribute(name = "date", description = "Default is to include all") Date after) {
		this.after = after;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "before", description = "If set, only dates before this timestamp are included", minOccurrences = 0, maxOccurrences = 1)
	public void setBefore(
			@AConQATAttribute(name = "date", description = "Default is to include all") Date before) {
		this.before = before;
	}

	/**
	 * Create actial {@link DateValueSeries}.
	 * 
	 * @throws ConQATException
	 *             if a database exception occurs, or if the interval is invalid
	 *             (e.g. ends before it starts).
	 */
	protected DateValueSeries createSeries() throws ConQATException {
		checkIntervalSane();
		CCSMAssert
				.isNotNull(dbConnection,
						"The ConQAT driver makes sure that the dbConnection is initialized.");
		try {
			return loadSeries();
		} catch (SQLException e) {
			throw wrap(e);
		}
	}

	/** Fills the series with data from the database */
	private DateValueSeries loadSeries() throws SQLException {
		DateValueSeries series = new DateValueSeries();

		Statement statement = null;
		ResultSet result = null;
		try {
			statement = dbConnection.createStatement();
			result = statement.executeQuery(createQueryString());

			while (result.next()) {
				long date = result.getLong(DATE);
				double value = result.getDouble(VALUE);
				series.addValue(new Date(date), value);
			}
		} finally {
			DatabaseUtils.closeSilently(result);
			DatabaseUtils.closeSilently(statement);
		}

		return series;
	}

	/** Make sure that the specified interval is sane. */
	private void checkIntervalSane() throws ConQATException {
		if (before == null || after == null) {
			return;
		}

		if (before.before(after)) {
			throw new ConQATException("Illegal interval: before " + before
					+ " after: " + after);
		}
	}

	/** Create SQL string for query. */
	private String createQueryString() {
		StringBuilder result = new StringBuilder();
		result.append("SELECT " + DATE + ", " + VALUE + " FROM ");
		result.append(tableName);
		if (after != null || before != null) {
			result.append(" WHERE ");
		}
		if (after != null) {
			result.append(DATE + ">" + after.getTime());
		}
		if (after != null && before != null) {
			result.append(" AND ");
		}
		if (before != null) {
			result.append(DATE + "<" + before.getTime());
		}
		return result.toString();
	}

}