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

import static org.conqat.engine.html_presentation.CSSMananger.ATTRIBUTE_TABLE_CELL;
import static org.conqat.engine.html_presentation.CSSMananger.DEFAULT_CONTAINER;
import static org.conqat.engine.html_presentation.CSSMananger.TABLE_CELL;
import static org.conqat.engine.html_presentation.CSSMananger.TABLE_HEADER_CELL;
import static org.conqat.engine.html_presentation.CSSMananger.TABLE_ICON;
import static org.conqat.engine.html_presentation.CSSMananger.TABLE_LINK;
import static org.conqat.lib.commons.html.EHTMLAttribute.CELLPADDING;
import static org.conqat.lib.commons.html.EHTMLAttribute.CELLSPACING;
import static org.conqat.lib.commons.html.EHTMLAttribute.CLASS;
import static org.conqat.lib.commons.html.EHTMLAttribute.COLSPAN;
import static org.conqat.lib.commons.html.EHTMLAttribute.HREF;
import static org.conqat.lib.commons.html.EHTMLAttribute.SRC;
import static org.conqat.lib.commons.html.EHTMLAttribute.STYLE;
import static org.conqat.lib.commons.html.EHTMLAttribute.WIDTH;
import static org.conqat.lib.commons.html.EHTMLElement.A;
import static org.conqat.lib.commons.html.EHTMLElement.IMG;
import static org.conqat.lib.commons.html.EHTMLElement.TABLE;
import static org.conqat.lib.commons.html.EHTMLElement.TD;
import static org.conqat.lib.commons.html.EHTMLElement.TH;
import static org.conqat.lib.commons.html.EHTMLElement.TR;

import java.util.Map;

import org.conqat.engine.core.driver.info.BlockInfo;
import org.conqat.engine.core.driver.info.IInfo;
import org.conqat.engine.core.driver.info.InfoAttribute;
import org.conqat.engine.core.driver.info.InfoOutput;
import org.conqat.engine.core.driver.info.InfoParameter;
import org.conqat.engine.core.driver.info.InfoRefNode;
import org.conqat.engine.core.driver.info.ProcessorInfo;
import org.conqat.engine.core.driver.instance.EInstanceState;
import org.conqat.engine.html_presentation.BundleContext;
import org.conqat.engine.html_presentation.util.PresentationUtils;
import org.conqat.lib.commons.html.HTMLWriter;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Utility methods used by the classes in this package.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 6A3F3238CF80507F25D12178EE8B3025
 */
/* package */class BaseUtils {

	/**
	 * Appends the info table that describes execution statistics of a block to
	 * a writer.
	 */
	public static void createInfoTable(BlockInfo blockInfo, String title,
			HTMLWriter writer) {
		writer.openElement(TABLE, CLASS, DEFAULT_CONTAINER, WIDTH, "100%",
				CELLSPACING, "2", CELLPADDING, "0");
		writer.openElement(TR);
		writer.addClosedTextElement(TH, title, CLASS, TABLE_HEADER_CELL,
				COLSPAN, "2");
		writer.closeElement(TR);

		int sum = 0;
		for (Map.Entry<EInstanceState, Integer> entry : blockInfo
				.getProcessorStateDistribution().entrySet()) {
			sum += entry.getValue();

			// format key nicely
			String key = "# processors "
					+ entry.getKey().toString().toLowerCase()
							.replace('_', StringUtils.SPACE_CHAR);

			createInfoTableRow(writer, key, entry.getValue());
		}

		createInfoTableRow(writer, "# processors", sum);
		createInfoTableRow(writer, "Execution time [ms]",
				blockInfo.getExecutionTime());
		writer.closeElement(TABLE);
	}

	/**
	 * Add a row to the info table.
	 * 
	 * @param key
	 *            the key to add (e.g.'# processors not run')
	 * @param value
	 *            the value associated with th key.
	 */
	private static void createInfoTableRow(HTMLWriter writer, String key,
			long value) {
		createInfoTableRow(writer, key,
				PresentationUtils.NUMBER_FORMATTER.format(value));
	}

	/**
	 * Add a row to the info table.
	 * 
	 * @param key
	 *            the key to add (e.g.'# processors not run')
	 * @param value
	 *            the value associated with th key.
	 */
	public static void createInfoTableRow(HTMLWriter writer, String key,
			String value) {
		writer.openElement(TR);
		writer.addClosedTextElement(TD, key, CLASS, TABLE_CELL);
		writer.addClosedTextElement(TD, value, CLASS, TABLE_CELL, WIDTH, "100%");
		writer.closeElement(TR);
	}

	/**
	 * Creates the table that describes the parameter values of a
	 * processor/block instance.
	 */
	public static void createParameterTable(HTMLWriter writer, IInfo info,
			String title) {

		writer.openElement(TABLE, CLASS, DEFAULT_CONTAINER, WIDTH, "100%",
				CELLSPACING, "2", CELLPADDING, "0");
		writer.openElement(TR);
		writer.addClosedTextElement(TH, title, CLASS, TABLE_HEADER_CELL,
				COLSPAN, "2");
		writer.closeElement(TR);

		for (InfoParameter param : info.getParameters()) {
			appendParameterToTable(writer, param);
		}

		writer.closeElement(TABLE);

	}

	/** Append a single parameter to the parameter table. */
	public static void appendParameterToTable(HTMLWriter writer,
			InfoParameter param) {

		writer.openElement(TR);
		writer.openElement(TD, CLASS, TABLE_CELL, COLSPAN, "2");
		writer.addClosedElement(IMG, CLASS, TABLE_ICON, SRC,
				"images/parameter_icon.gif");
		writer.addText(param.getName());
		writer.closeElement(TD);
		writer.closeElement(TR);

		for (InfoAttribute attr : param.getAttributes()) {
			appendAttributeToTable(writer, attr);
		}
	}

	/**
	 * Get file name of the log page for a processor.
	 */
	public static String getProcessorLogFilename(ProcessorInfo processor) {
		return getProcessorLogFilename(processor.getInstanceName());
	}

	/**
	 * Get file name of the log page for a processor.
	 */
	public static String getProcessorLogFilename(String processorInstanceName) {
		return "log_"
				+ BundleContext.getInstance().getHtmlPresentationManager()
						.getAbbreviation(processorInstanceName) + ".html";
	}

	/**
	 * Returns the name of the block config graph file for a given block
	 * (instance) name.
	 */
	public static String getConfigGraphFilename(String blockInstanceName) {
		return "config_graph_"
				+ BundleContext.getInstance().getHtmlPresentationManager()
						.getAbbreviation(blockInstanceName) + ".html";
	}

	/** Appends the link to another attribute (of a surrounding block). */
	private static void appendAttributeReference(HTMLWriter writer,
			InfoAttribute attr) {

		InfoParameter param = attr.getParameter();
		String instanceName = param.getInfo().getInstanceName();
		writer.addText("input " + param.getName() + "." + attr.getName() + " @");

		writer.addClosedTextElement(A, instanceName, CLASS, TABLE_LINK, HREF,
				getConfigGraphFilename(instanceName));
	}

	/** Append a single attribute to the parameter table. */
	private static void appendAttributeToTable(HTMLWriter writer,
			InfoAttribute attr) {
		writer.openElement(TR);
		writer.openElement(TD, CLASS, TABLE_CELL, STYLE, ATTRIBUTE_TABLE_CELL);
		writer.addClosedElement(IMG, CLASS, TABLE_ICON, SRC,
				"images/attribute_icon.gif");
		writer.addText(attr.getName());
		writer.closeElement(TD);
		writer.openElement(TD, CLASS, TABLE_CELL, WIDTH, "100%");

		if (attr.isImmediateValue()) {
			writer.addText("\"" + attr.getImmediateValue() + "\"");
		} else {
			appendRefNode(writer, attr.getReferenced());
		}
		writer.closeElement(TD);
		writer.closeElement(TR);
	}

	/** Appends the link to another output. */
	private static void appendOutputReference(HTMLWriter writer, InfoOutput out) {

		String instanceName = out.getInfo().getInstanceName();
		writer.addText("@");
		if (out.getInfo() instanceof ProcessorInfo) {

			writer.openElement(A, CLASS, TABLE_LINK, HREF,
					getProcessorLogFilename(instanceName));
		} else {
			writer.openElement(A, CLASS, TABLE_LINK, HREF,
					getConfigGraphFilename(instanceName));

		}
		writer.addText(instanceName);
		writer.closeElement(A);

		if (!StringUtils.isEmpty(out.getName())) {
			writer.addText("." + out.getName());
		}
	}

	/** Appends the link to a reference node. */
	public static void appendRefNode(HTMLWriter writer, InfoRefNode refNode) {

		if (refNode instanceof InfoAttribute) {
			appendAttributeReference(writer, (InfoAttribute) refNode);
		} else {
			appendOutputReference(writer, (InfoOutput) refNode);
		}
	}

}