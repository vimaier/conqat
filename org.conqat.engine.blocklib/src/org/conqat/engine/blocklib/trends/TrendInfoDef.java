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

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.html_presentation.chart.ETimeResolution;
import org.conqat.engine.html_presentation.chart.annotation.AnnotationList;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: pfaller $
 * @version $Rev: 38370 $
 * @ConQAT.Rating GREEN Hash: 1F290B16A1C4DEBF87156D5BF182D3DF
 */
@AConQATProcessor(description = "Defines trend info parameters.")
public class TrendInfoDef extends ConQATProcessorBase {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "db-connection", attribute = ConQATParamDoc.INPUT_REF_NAME, description = "The database connection.")
	public Connection dbConnection;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "domain", attribute = "resolution", description = "Set time resolution of the domain.")
	public ETimeResolution timeResolution;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "annotations", attribute = ConQATParamDoc.INPUT_REF_NAME, description = "Chart annotations")
	public AnnotationList annotations;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "log-scale", attribute = "value", description = "Turn on log scaling of trends [default is false]", optional = true)
	public boolean logScaled = false;

	/** {@inheritDoc} */
	@Override
	public TrendInfo process() {
		return new TrendInfo(dbConnection, timeResolution, annotations,
				logScaled);
	}
}
