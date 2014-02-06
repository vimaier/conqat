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

import static org.conqat.engine.html_presentation.CSSMananger.DEFAULT_CONTAINER;
import static org.conqat.engine.html_presentation.CSSMananger.TABLE_HEADER_CELL;
import static org.conqat.lib.commons.html.EHTMLAttribute.CELLPADDING;
import static org.conqat.lib.commons.html.EHTMLAttribute.CELLSPACING;
import static org.conqat.lib.commons.html.EHTMLAttribute.CLASS;
import static org.conqat.lib.commons.html.EHTMLAttribute.WIDTH;
import static org.conqat.lib.commons.html.EHTMLElement.TABLE;
import static org.conqat.lib.commons.html.EHTMLElement.TH;
import static org.conqat.lib.commons.html.EHTMLElement.TR;

import java.util.HashMap;
import java.util.Map;

import org.conqat.engine.core.bundle.BundleInfo;
import org.conqat.engine.core.core.IConQATProcessorInfo;
import org.conqat.engine.html_presentation.HTMLPresentation;
import org.conqat.engine.html_presentation.IPageDescriptor;
import org.conqat.engine.html_presentation.PageDescriptor;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.html.HTMLWriter;
import org.conqat.lib.commons.version.Version;

/**
 * This generator generates the config page that contains information on the
 * installed bundles and its versions as well as the ConQAT version.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating YELLOW Hash: 41144816EFDFA86D7016C978ED01CA88
 */
public class ConfigPageGenerator {

	/** Processor info to obtain config information from. */
	private final IConQATProcessorInfo info;

	/** Create new generator. */
	public ConfigPageGenerator(IConQATProcessorInfo processorInfo) {
		this.info = processorInfo;
	}

	/** Create config page. */
	public IPageDescriptor createPage() {
		PageDescriptor page = new PageDescriptor("ConQAT version", "Version",
				HTMLPresentation.INFO_GROUP_NAME, null, "config.gif",
				"conqat_config.html");

		HTMLWriter writer = page.getWriter();

		writer.openElement(TABLE, CLASS, DEFAULT_CONTAINER, WIDTH, "100%",
				CELLSPACING, "2", CELLPADDING, "0");
		writer.openElement(TR);
		writer.addClosedTextElement(TH, "Element", CLASS, TABLE_HEADER_CELL);
		writer.addClosedTextElement(TH, "Version", CLASS, TABLE_HEADER_CELL);
		writer.closeElement(TR);

		BaseUtils.createInfoTableRow(writer, "ConQAT Core", info
				.getConQATCoreVersion().toString());

		Map<String, Version> bundles = new HashMap<String, Version>();
		for (BundleInfo bundle : info.getBundlesConfiguration().getBundles()) {
			bundles.put("Bundle " + bundle.getId(), bundle.getVersion());
		}

		for (String element : CollectionUtils.sort(bundles.keySet())) {
			BaseUtils.createInfoTableRow(writer, element, bundles.get(element)
					.toString());
		}

		writer.closeElement(TABLE);
		return page;
	}
}