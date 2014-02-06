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
package org.conqat.engine.html_presentation.portfolio;

import java.io.IOException;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.html_presentation.base.ConfigJSONWriter;
import org.conqat.engine.html_presentation.javascript.config.StatisticsDataJson;
import org.conqat.engine.html_presentation.util.JsonUtils;

/**
 * Base class for strategies to retrieve the JSON statistics data of a
 * dashboard.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 39890 $
 * @ConQAT.Rating GREEN Hash: A5A47F2B46930D146E8EF600A6FF54F2
 */
public abstract class PortfolioDataFetcherBase {

	/** The name of the dashboard. */
	private final String dashboardName;

	/** The location of the dashboard. */
	private final String dashboardLocation;

	/** Constructor. */
	public PortfolioDataFetcherBase(String dashboardName, String location) {
		this.dashboardName = dashboardName;
		this.dashboardLocation = location;
	}

	/** Returns the dashboard's name. */
	public String getDashboardName() {
		return dashboardName;
	}

	/** Returns the dashboard's location. */
	public String getDashboardLocation() {
		return dashboardLocation;
	}

	/** Returns the data JSON fetched from the given dashboard location. */
	public StatisticsDataJson retrieveData() throws ConQATException,
			IOException {
		// we don't have to worry about additional slashes. both http and local
		// file access can compensate for them
		String dataJsonFileLocation = dashboardLocation.toString() + "/"
				+ ConfigJSONWriter.STATISTICS_JSON;
		String jsonString = getJsonString(dataJsonFileLocation);
		return JsonUtils.deserializeFromJSON(jsonString,
				StatisticsDataJson.class);
	}

	/**
	 * Must return the JSON data as a String, fetched from the given dashboard
	 * location.
	 */
	abstract protected String getJsonString(String jsonFileLocation)
			throws ConQATException, IOException;

}
