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
package org.conqat.engine.core.driver;

import java.util.ArrayList;
import java.util.List;

import org.conqat.lib.commons.test.CCSMTestCaseBase;
import org.conqat.engine.core.driver.processors.DataSinkProcessor;

/**
 * Test case that tests the implementation of command line options of the
 * {@link Driver}.
 * 
 * @author juergens
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: 789D18000233865EB7AC7C64360A1161
 */
public class DriverOptionsTest extends CCSMTestCaseBase {

	/** Clear data store in {@link DataSinkProcessor} */
	@Override
	protected void setUp() {
		DataSinkProcessor.resetDataStore();
	}

	/** Test property mechanism without any external properties */
	public void testNoPropertiesOption() {
		// prop is not externally set, so the driver ignores it
		assertNull(determinePropertyValueFromSpecificPropertiesFiles(
				"properties-in-config.cqb", "prop.in"));
		assertEquals("value-in-config-file",
				determinePropertyValueFromSpecificPropertiesFiles(
						"properties-in-config.cqb", "prop2"));
	}

	/** Test property mechanism with properties from default property file */
	public void testMultiplePropertyFiles() {
		assertEquals("value-in-default-properties-file1",
				determinePropertyValueFromSpecificPropertiesFiles(
						"multi-prop-files.cqb", "prop.in",
						"multi-prop-files1.properties",
						"multi-prop-files2.properties"));
		assertEquals("value-in-default-properties-file2",
				determinePropertyValueFromSpecificPropertiesFiles(
						"multi-prop-files.cqb", "prop.in2",
						"multi-prop-files1.properties",
						"multi-prop-files2.properties"));
		assertEquals("value-in-config-file",
				determinePropertyValueFromSpecificPropertiesFiles(
						"multi-prop-files.cqb", "prop2",
						"multi-prop-files1.properties",
						"multi-prop-files2.properties"));
	}

	/** Make sure that a property can be overridden via the command line */
	public void testCommandLineProperty() {
		assertEquals("value-from-command-line",
				determinePropertyValueSetViaCommandLine(
						"properties-in-config.cqb", "prop.in",
						"value-from-command-line",
						"multi-prop-files1.properties"));
	}

	/** Tests loading from a CQR file. */
	public void testLoadFromCQRFile() {
		assertEquals("value-in-cqr",
				determinePropertyValueFromSpecificPropertiesFiles(
						"properties-in-config.cqr", "prop.in"));
	}

	/**
	 * Runs the driver to determine the property value that was used during
	 * execution.
	 * <p>
	 * Sets both property file and property via command line, i.e. returns
	 * property value set via command line.
	 */
	private String determinePropertyValueSetViaCommandLine(String propertyFile,
			String propertyName, String propertyValue, String propertiesFilename) {
		String configFilename = useTestFile(propertyFile).getAbsolutePath();
		propertiesFilename = useTestFile(propertiesFilename).getAbsolutePath();
		String[] args = new String[] { "-f", configFilename, "-s",
				propertiesFilename, "-p", propertyName + "=" + propertyValue };

		return runAndReturnProperty(args, propertyName);
	}

	/**
	 * Runs the driver to determine the property value that was used during
	 * execution.
	 * <p>
	 * Sets properties file parameter, i.e. returns property value as set in
	 * specified property file.
	 */
	private String determinePropertyValueFromSpecificPropertiesFiles(
			String configFilename, String propertyName,
			String... propertiesFilenames) {
		List<String> args = new ArrayList<String>();
		args.add("-f");
		args.add(useTestFile(configFilename).getAbsolutePath());

		for (String propertiesFilename : propertiesFilenames) {
			args.add("-s");
			args.add(useTestFile(propertiesFilename).getAbsolutePath());
		}

		return runAndReturnProperty(args.toArray(new String[0]), propertyName);
	}

	/**
	 * Runs the driver and returns the named property from the
	 * {@link DataSinkProcessor}.
	 */
	private String runAndReturnProperty(String[] args, String propertyName) {
		Driver.main(args);
		List<Object> list = DataSinkProcessor.accessData(propertyName);
		if (list == null) {
			return null;
		}
		Object object = list.get(0);
		if (object == null) {
			return null;
		}
		return object.toString();
	}

}