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
package org.conqat.engine.blocklib.trends;

import java.sql.Connection;

import org.conqat.engine.html_presentation.chart.ETimeResolution;
import org.conqat.engine.html_presentation.chart.annotation.AnnotationList;
import org.conqat.lib.commons.clone.IDeepCloneable;

/**
 * This class is a parameter object that encapsulates all relevant parameters
 * used for displaying trend data.
 * <p>
 * This class is immutable.
 * 
 * @author $Author: pfaller $
 * @version $Rev: 38370 $
 * @ConQAT.Rating GREEN Hash: 8B1C68D1EB272D0716CF0FD4FD43108B
 */
public class TrendInfo implements IDeepCloneable {

	/** The database connection. */
	private final Connection dbConnection;

	/** The time resolution. */
	private final ETimeResolution timeResolution;

	/** Annotations. */
	private final AnnotationList annotations;

	/** Flag that enables log scaling. */
	private final boolean logScaled;

	/** Constructor. */
	public TrendInfo(Connection dbConnection, ETimeResolution timeResolution,
			AnnotationList annotations, boolean logScaled) {
		this.dbConnection = dbConnection;
		this.timeResolution = timeResolution;
		this.annotations = annotations;
		this.logScaled = logScaled;
	}

	/** Returns the DB connection. */
	public Connection getDbConnection() {
		return dbConnection;
	}

	/** Returns the time resolution. */
	public ETimeResolution getTimeResolution() {
		return timeResolution;
	}

	/** Returns the annotations. May be exposed as they are immutable as well. */
	public AnnotationList getAnnotations() {
		return annotations;
	}

	/** Returns the flag for log scaling. */
	public boolean isLogScaled() {
		return logScaled;
	}

	/** {@inheritDoc} */
	@Override
	public IDeepCloneable deepClone() {
		// no cloning as we are immutable
		return this;
	}
}
