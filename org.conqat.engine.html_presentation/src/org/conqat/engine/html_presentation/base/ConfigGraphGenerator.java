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
package org.conqat.engine.html_presentation.base;

import static org.conqat.lib.commons.html.EHTMLAttribute.ID;
import static org.conqat.lib.commons.html.EHTMLAttribute.STYLE;
import static org.conqat.lib.commons.html.EHTMLElement.DIV;

import java.util.Arrays;

import org.conqat.engine.core.driver.info.BlockInfo;
import org.conqat.engine.core.driver.instance.EInstanceState;
import org.conqat.engine.html_presentation.HTMLPresentation;
import org.conqat.engine.html_presentation.IPageDescriptor;
import org.conqat.engine.html_presentation.PageDescriptor;
import org.conqat.engine.html_presentation.javascript.config.ConfigJSModule;
import org.conqat.lib.commons.collections.UnmodifiableMap;
import org.conqat.lib.commons.html.CSSDeclarationBlock;
import org.conqat.lib.commons.html.HTMLWriter;

/**
 * This class renders a skeleton page, which will be filled by JavaScript
 * dynamically using values written to JSON files by a {@link ConfigJSONWriter}.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 12B8129E8D142508E54FD5C94919C742
 */
public class ConfigGraphGenerator {

	/** CSS for the graph paper. */
	private static CSSDeclarationBlock GRAPH_PAPER_STYLE = new CSSDeclarationBlock()
			.setPadding("5px");

	/** The root block info that describes the config graph. */
	private final BlockInfo configGraph;

	/**
	 * Create new config graph factory.
	 * 
	 * @param configurationInformation
	 *            The root block info that describes the config graph.
	 */
	public ConfigGraphGenerator(BlockInfo configurationInformation) {
		this.configGraph = configurationInformation;
	}

	/** Create the page describing the config graph. */
	public IPageDescriptor createPage() {
		PageDescriptor page = new PageDescriptor(
				"Top-level Configuration Graph.", determinePageName(),
				HTMLPresentation.INFO_GROUP_NAME, "graph.gif",
				"config_graph.html");
		createPage(page.getWriter());
		return page;
	}

	/** Writes the script tags for the JSON data to the page body. */
	private void createPage(HTMLWriter writer) {
		writer.insertJavaScript(ConfigJSModule.installCSSClassNames());
		writer.insertJavaScript(ConfigJSModule.installStatisticsDescriptions());

		writer.addExternalJavaScript(ConfigJSONWriter.CONFIG_JSON);
		writer.addExternalJavaScript(ConfigJSONWriter.LOG_JSON);

		writer.insertEmptyElement(DIV, ID, ConfigJSModule.GRAPH_PAPER_ID,
				STYLE, GRAPH_PAPER_STYLE);
		writer.insertJavaScript(ConfigJSModule
				.installConfig(ConfigJSModule.GRAPH_PAPER_ID));
	}

	/** Determines the name of the page based on the number of errors. */
	private String determinePageName() {
		UnmodifiableMap<EInstanceState, Integer> states = configGraph
				.getProcessorStateDistribution();

		int errorCount = 0;
		for (EInstanceState state : Arrays.asList(EInstanceState.FAILED_BADLY,
				EInstanceState.FAILED_GRACEFULLY,
				EInstanceState.FAILED_DUE_TO_CLONING_PROBLEM,
				EInstanceState.FAILED_DUE_TO_MISSING_INPUT)) {
			Integer count = states.get(state);
			if (count != null) {
				errorCount += count;
			}
		}

		String pageName = "Config";
		if (errorCount > 0) {
			pageName += " (" + errorCount + " failed)";
		}
		return pageName;
	}

}