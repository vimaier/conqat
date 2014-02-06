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
package org.conqat.engine.html_presentation.javascript.config;

import static org.conqat.engine.html_presentation.CSSMananger.ATTRIBUTE_TABLE_CELL;
import static org.conqat.engine.html_presentation.CSSMananger.DEFAULT_CONTAINER;
import static org.conqat.engine.html_presentation.CSSMananger.TABLE_CELL;
import static org.conqat.engine.html_presentation.CSSMananger.TABLE_HEADER_CELL;
import static org.conqat.engine.html_presentation.CSSMananger.WIDE_TABLE_CELL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.driver.info.BlockInfo;
import org.conqat.engine.core.driver.info.InfoAttribute;
import org.conqat.engine.core.driver.info.InfoBase;
import org.conqat.engine.core.driver.info.InfoOutput;
import org.conqat.engine.core.driver.info.InfoParameter;
import org.conqat.engine.core.driver.info.InfoRefNode;
import org.conqat.engine.core.driver.instance.EInstanceState;
import org.conqat.engine.html_presentation.CSSMananger;
import org.conqat.engine.html_presentation.base.LoggingPagesGenerator;
import org.conqat.engine.html_presentation.javascript.JavaScriptModuleBase;
import org.conqat.engine.html_presentation.javascript.third_party.RaphaelModule;
import org.conqat.lib.commons.collections.UnmodifiableList;
import org.conqat.lib.commons.collections.UnmodifiableMap;
import org.conqat.lib.commons.html.CSSDeclarationBlock;
import org.conqat.lib.commons.string.StringUtils;

/**
 * This module contains code/scripts for displaying the current ConQAT
 * configuration (config graph and log).
 * 
 * The main class of this package is DashboardPageBase, which contains all the
 * entry points of JSON injection, CSS class injection, as well as the basic
 * setup of every dashboard page.
 * 
 * For more information about a specific page, see LogPage and ConfigPage.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 39832 $
 * @ConQAT.Rating GREEN Hash: 27FE37D7A6C1C47DCFC91D8BFB2DEAB5
 */
public class ConfigJSModule extends JavaScriptModuleBase {

	/** The name of the function used for registering CSS class names. */
	private static final String DASHBOARD_ADD_CSS_CLASS_CALL = "conqat.config.DashboardPageBase.addCSSClass";

	/** The name of the function used for registering statistics descriptions. */
	private static final String DASHBOARD_ADD_SET_STATISTICS_DESCRIPTIONS_CALL = "conqat.config.DashboardPageBase.setStatisticsDescriptions";

	/** The name of the function used for registering statistics descriptions. */
	private static final String LOG_SETUP_FILTER_CHECKBOX_CALL = "conqat.config.LogPage.setupFilterCheckbox";

	/** Call to initialize the log page. */
	private static final String LOG_SETUP_CALL = "conqat.config.LogPage.create";

	/** Call to enable the history management. */
	private static final String ENABLE_HISTORY_CALL = "conqat.config.DashboardPageBase.enableHistory";

	/** Call to initialize the config page. */
	private static final String CONFIG_SETUP_CALL = "conqat.config.ConfigGraphPage.create";

	/** Call to initialize the page with config json data. */
	private static final String JSON_CONFIG_SETUP_CALL = "conqat.config.DashboardPageBase.installConfigJSON";

	/** Call to initialize the page with log json data. */
	private static final String JSON_LOG_SETUP_CALL = "conqat.config.DashboardPageBase.installLogJSON";

	/** ID of the page element that holds the config graph drawn by Raphael. */
	public static final String GRAPH_PAPER_ID = "graph-paper";

	/** {@inheritDoc} */
	@Override
	protected void createJavaScriptFiles() throws ConQATException {
		registerExports("DashboardPageBase.js", DASHBOARD_ADD_CSS_CLASS_CALL,
				DASHBOARD_ADD_SET_STATISTICS_DESCRIPTIONS_CALL,
				JSON_LOG_SETUP_CALL, JSON_CONFIG_SETUP_CALL,
				ENABLE_HISTORY_CALL);
		registerExports("LogPage.js", LOG_SETUP_FILTER_CHECKBOX_CALL,
				LOG_SETUP_CALL);
		registerExports("ConfigGraphPage.js", CONFIG_SETUP_CALL);

		addCustomJavaScriptAndSoyFromCurrentPackage(Arrays
				.asList(RaphaelModule.NAMESPACE));
	}

	/**
	 * Returns JavaScript that registers the descriptive text for the statistics
	 * table.
	 */
	public static String installStatisticsDescriptions() {
		EInstanceState[] states = EInstanceState.values();

		List<Object> descriptions = new ArrayList<Object>();
		for (EInstanceState state : states) {
			descriptions.add("# processors "
					+ state.toString().toLowerCase()
							.replace('_', StringUtils.SPACE_CHAR));
		}
		descriptions.add("# processors");
		descriptions.add("Execution time [ms]");

		return javaScriptCall(DASHBOARD_ADD_SET_STATISTICS_DESCRIPTIONS_CALL,
				descriptions);
	}

	/**
	 * Returns JavaScript that registers the last inserted checkbox as a log
	 * table filter.
	 */
	public static String installCheckboxFilter(String level) {
		return javaScriptCall(LOG_SETUP_FILTER_CHECKBOX_CALL, level);
	}

	/** Returns JavaScript to display a log page. */
	public static String installLog() {
		return javaScriptCall(LOG_SETUP_CALL) + installHistoryManagement();
	}

	/** Returns JavaScript to display a config page. */
	public static String installConfig(String graphPaperId) {
		return javaScriptCall(CONFIG_SETUP_CALL, graphPaperId)
				+ installHistoryManagement();
	}

	/** Returns JavaScript to enable history management. */
	private static String installHistoryManagement() {
		return javaScriptCall(ENABLE_HISTORY_CALL);
	}

	/**
	 * Returns JavaScript to install JSON data about the config and its members
	 * for the current page.
	 */
	public static String installConfigJSON(
			ConfigGraphDataJson configGraphJsonObject) {
		return javaScriptCall(JSON_CONFIG_SETUP_CALL, configGraphJsonObject);
	}

	/** Returns JavaScript to install JSON log data for the current page. */
	public static String installLogJSON(List<List<Object>> logJsonObject) {
		return javaScriptCall(JSON_LOG_SETUP_CALL, logJsonObject);
	}

	/**
	 * Returns JavaScript that installs all CSS classes needed by the JavaScript
	 * templates.
	 */
	public static String installCSSClassNames() {
		StringBuilder sb = new StringBuilder();
		sb.append(ConfigJSModule.installCSSClass("attributeTableCell",
				ATTRIBUTE_TABLE_CELL));
		sb.append(ConfigJSModule.installCSSClass("tableCell", TABLE_CELL));
		sb.append(ConfigJSModule.installCSSClass("wideTableCell",
				WIDE_TABLE_CELL));
		sb.append(ConfigJSModule.installCSSClass("tableHeader",
				TABLE_HEADER_CELL));
		sb.append(ConfigJSModule.installCSSClass("table", DEFAULT_CONTAINER));
		sb.append(ConfigJSModule.installCSSClass("evenRow",
				LoggingPagesGenerator.EVEN_ROW));
		sb.append(ConfigJSModule.installCSSClass("oddRow",
				LoggingPagesGenerator.ODD_ROW));
		return sb.toString();
	}

	/**
	 * Returns JavaScript that registers the given {@link CSSDeclarationBlock}
	 * under the given alias.
	 */
	private static String installCSSClass(String alias,
			CSSDeclarationBlock block) {
		return javaScriptCall(DASHBOARD_ADD_CSS_CLASS_CALL, alias, CSSMananger
				.getInstance().getCSSClassName(block));
	}

	/**
	 * Data object that holds information about a parameter and its attributes
	 * and will be serialized to JSON.
	 * 
	 * The supposedly unused members of the class are used during JSON
	 * serialization.
	 */
	@SuppressWarnings("unused")
	private static class ParameterJsonData {

		/** The name of the parameter. */
		private final String name;

		/** The attributes of the parameter. */
		private final List<List<String>> attributes = new LinkedList<List<String>>();

		/** Constructor. */
		public ParameterJsonData(String name) {
			this.name = name;
		}

		/** Adds an attribute to the JSON data. */
		public void addAttribute(String name, String value) {
			attributes.add(Arrays.asList(name, value));
		}

	}

	/**
	 * Data object that holds the structural information of the config and will
	 * be serialized to JSON.
	 * 
	 * The supposedly unused fields in this class are used during JSON
	 * serialization.
	 */
	public static class ConfigGraphDataJson {

		/** The id of the main block of this config. */
		@SuppressWarnings("unused")
		private final int rootBlockId;

		/**
		 * Map of the blocks that are part of this config. The key is the id of
		 * the block, which is the same as the id of the element that represents
		 * it, except for the {@link #rootBlockId}.
		 */
		private final Map<Integer, BlockDataJson> blockData = new HashMap<Integer, BlockDataJson>();

		/**
		 * Map of the elements that are part of this config. The key is the id
		 * of the element. The data is a list of different properties, see
		 * {@link #addElement(Integer, String, String, String, String, int, int, int, int)}
		 * .
		 */
		private final Map<Integer, List<Object>> elements = new HashMap<Integer, List<Object>>();

		/**
		 * Map of parameter definitions. The key is the id of the block or
		 * processor. The values are parameters and their attributes that will
		 * be displayed in the table under the graph of the block and its log
		 * page.
		 */
		private final Map<Integer, List<ParameterJsonData>> parameters = new HashMap<Integer, List<ParameterJsonData>>();

		/** Constructor. */
		public ConfigGraphDataJson(int rootBlockId) {
			this.rootBlockId = rootBlockId;
		}

		/** Adds the parameters of the given block or processor. */
		public void addParameters(Integer id, InfoBase info) {
			List<ParameterJsonData> infoParameters = new ArrayList<ParameterJsonData>();
			for (InfoParameter param : info.getParameters()) {
				UnmodifiableList<InfoAttribute> attributes = param
						.getAttributes();
				ParameterJsonData data = new ParameterJsonData(param.getName());
				for (InfoAttribute attr : attributes) {
					String value;
					if (attr.isImmediateValue()) {
						value = attr.getImmediateValue();
					} else {
						InfoRefNode referenced = attr.getReferenced();
						value = formatInfoRefNode(referenced);
					}
					data.addAttribute(attr.getName(), value);
				}
				infoParameters.add(data);
			}

			if (!infoParameters.isEmpty()) {
				parameters.put(id, infoParameters);
			}
		}

		/**
		 * Formats the given {@link InfoRefNode} as
		 * <referring-element>.<attribute> or just <referring-element>, if there
		 * is no attribute.
		 */
		private String formatInfoRefNode(InfoRefNode node) {
			if (node instanceof InfoAttribute) {
				return formatAttributeReference((InfoAttribute) node);
			}
			return formatOutputReference((InfoOutput) node);
		}

		/**
		 * Formats the given {@link InfoOutput} as <instance>.<attribute> or
		 * just <instance>, if there is no attribute.
		 */
		private String formatOutputReference(InfoOutput output) {
			String instanceName = output.getInfo().getInstanceName();
			String outputReference = instanceName;
			if (!StringUtils.isEmpty(output.getName())) {
				outputReference += "." + output.getName();
			}
			return "@" + outputReference;
		}

		/** Formats the given {@link InfoAttribute} as <parameter>.<attribute> */
		private String formatAttributeReference(InfoAttribute attr) {
			InfoParameter param = attr.getParameter();
			return "@" + param.getName() + "." + attr.getName();
		}

		/** Adds a block to the block map. */
		public void addBlock(Integer id, BlockDataJson block) {
			blockData.put(id, block);
		}

		/**
		 * Adds the data for an element to the config. This is added as a flat
		 * list/array, to reduce the amout of space for storing it in the JSON
		 * file.
		 */
		public void addElement(Integer id, String type, String name,
				String typeName, String color, int x, int y, int width,
				int height) {
			elements.put(id, Arrays.asList((Object) type, name, typeName,
					color, x, y, width, height));
		}

	}

	/**
	 * Data object that holds the structural information of one block of the
	 * config and will be serialized to JSON.
	 */
	public static class BlockDataJson {

		/**
		 * List of statistical data that will be displayed in the table over the
		 * graph of the block.
		 * 
		 * Contains the data that corresponds to the descriptions created in
		 * {@link ConfigJSModule#installStatisticsDescriptions()}.
		 */
		private final List<Object> statistics = new LinkedList<Object>();

		/** The ids of the members of this block. */
		private final Set<Integer> members = new HashSet<Integer>();

		/**
		 * A list of tuples that indicate members that are connected with an
		 * arrow. The arrow head points from the first to the second entry in
		 * each tuple. The third entry is a boolean that signifies whether the
		 * connection is invisible.
		 */
		private final List<List<Object>> connections = new LinkedList<List<Object>>();

		/**
		 * Creates a new BlockData from the given {@link BlockInfo}. This
		 * constructor fills in parameters and attributes of the block and
		 * statistical data about the block.
		 */
		public BlockDataJson(BlockInfo info) {
			int sum = 0;
			UnmodifiableMap<EInstanceState, Integer> distribution = info
					.getProcessorStateDistribution();
			for (EInstanceState state : EInstanceState.values()) {
				Integer amount = distribution.get(state);
				if (amount == null) {
					amount = 0;
				}
				statistics.add(amount);
				sum += amount;
			}
			statistics.add(sum);
			statistics.add(info.getExecutionTime());
		}

		/** Inserts a new connection between the two members. */
		public void connect(int sourceId, int targetId, boolean invisible) {
			connections.add(Arrays.asList((Object) sourceId, targetId,
					invisible));
		}

		/** Adds a member to this block. */
		public void addMember(int id) {
			members.add(id);
		}

	}

	/** Creates a log entry for JSON serialization. */
	public static List<Object> createJsonLogEntry(int processorId,
			String level, String message, long time) {
		return Arrays.asList((Object) processorId, level, message, time);
	}

}
