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
package org.conqat.engine.io;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.analysis.ElementAnalyzerBase;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.lib.commons.reflect.ReflectionUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: juergens $
 * @version $Rev: 35195 $
 * @ConQAT.Rating GREEN Hash: BF2373ABA0C174BDC14407B7DA399F48
 */
@AConQATProcessor(description = "Interprets elements as CSV (comma separated "
		+ "value) files and writes their content into keys. "
		+ "The first line in the element is expected to contain column names. The second "
		+ "line is expected to contain values. ")
public class CSVAnnotator extends
		ElementAnalyzerBase<ITextResource, ITextElement> {

	/** String used to split columns */
	private String separator = ";";

	/** Converts value strings into typed objects */
	private final TypeConverter converter = new TypeConverter();

	/** Set of keys encountered in the leafs */
	private final Set<String> keys = new HashSet<String>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "separator", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "String used to split columns.")
	public void setSeparator(
			@AConQATAttribute(name = "separator", defaultValue = ";", description = "Default: \";\"") String separator) {
		this.separator = separator;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "type", description = "Set the type for a column")
	public void addType(
			@AConQATAttribute(name = "column", description = "Name of the column") String columnName,
			@AConQATAttribute(name = "typename", description = "Name of the type. Allowed values are all java primitives. (int, boolean, long, ...)") String typeName) {

		try {
			Class<?> clazz = ReflectionUtils.resolveType(typeName);
			converter.addTypeAssociation(columnName, clazz);
		} catch (ClassNotFoundException e) {
			getLogger().error("Could not find type: " + typeName);
		}
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "default", description = "Default type that gets used", minOccurrences = 0, maxOccurrences = 1)
	public void setDefaultType(
			@AConQATAttribute(name = "type", description = "If no default type is set, String is used as default type.") String defaultTypeName)
			throws ConQATException {
		try {
			converter.setDefaultType(Class.forName(defaultTypeName));
		} catch (ClassNotFoundException e) {
			throw new ConQATException("Cannot set default type: ", e);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void analyzeElement(ITextElement element) throws ConQATException {

		String[] lines = StringUtils.splitLines(element.getTextContent());
		assertFormat(element, lines);

		String[] columnNames = lines[0].split(separator);
		String[] values = lines[1].split(separator);

		if (columnNames.length != values.length) {
			getLogger().warn(
					"Different number of keys and values found in CSV element: "
							+ element);
			return;
		}

		for (int i = 0; i < columnNames.length; i++) {
			String key = columnNames[i];
			Object typedValue = converter.typedValueFor(key, values[i]);
			element.setValue(key, typedValue);
		}

		keys.addAll(Arrays.asList(columnNames));
	}

	/** Assert that CSV element has expected format */
	private void assertFormat(ITextElement element, String[] lines)
			throws ConQATException {
		if (lines.length != 2) {
			throw new ConQATException(
					"CSV element "
							+ element
							+ " has unexpected length. Expecting a header and a values line.");
		}
	}

	/**
	 * Keys are not known when this method is called. We thus return an empty
	 * array here and add them to the display list ourselves.
	 */
	@Override
	protected String[] getKeys() {
		return new String[0];
	}

	/** {@inheritDoc} */
	@Override
	protected void finish(ITextResource root) {
		NodeUtils.addToDisplayList(root, keys);
	}

}