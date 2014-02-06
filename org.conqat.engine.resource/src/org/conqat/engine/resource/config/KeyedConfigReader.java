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
package org.conqat.engine.resource.config;

import org.conqat.engine.commons.config.KeyedConfig;
import org.conqat.engine.commons.util.ConQATInputProcessorBase;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.driver.util.PropertyUtils;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.engine.resource.text.TextElementUtils;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 98A7EBF384FD0498FF9CFF3C74B75F31
 */
@AConQATProcessor(description = "Reads key/value configuration from a tree of text resources. "
		+ "The files must contain one key/value pair in each line, pairs are separated with the equals sign.")
public class KeyedConfigReader extends ConQATInputProcessorBase<ITextResource> {

	/** The resulting keyed config. */
	private final KeyedConfig config = new KeyedConfig();

	/** {@inheritDoc} */
	@Override
	public KeyedConfig process() throws ConQATException {
		for (ITextElement element : ResourceTraversalUtils
				.listTextElements(input)) {
			parseElement(element);
		}

		return config;
	}

	/** Parses a single element and inserts the values into {@link #config}. */
	private void parseElement(ITextElement element) throws ConQATException {
		int lineNumber = 0;
		for (String line : TextElementUtils.getLines(element)) {
			lineNumber += 1;
			if (StringUtils.isEmpty(line) || PropertyUtils.isCommentLine(line)) {
				continue;
			}

			String[] parts = line.split("=", 2);
			if (parts.length < 2) {
				throw new ConQATException("Invalid entry in line " + lineNumber
						+ " in element " + element.getLocation()
						+ ": missing equals sign!");
			}

			config.set(parts[0].trim(), parts[1]);
		}
	}
}
