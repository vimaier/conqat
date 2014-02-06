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
package org.conqat.engine.core.logging.testutils;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.conqat.engine.core.driver.Driver;
import org.conqat.engine.core.driver.error.DriverException;
import org.conqat.engine.core.driver.processors.DataSinkProcessor;
import org.conqat.lib.commons.test.CCSMTestCaseBase;

/**
 * Base class for all driver test cases. This class is part of the (non-test)
 * source, as tests in the bundles depend on it and the test-src is not included
 * in cq.edit.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 1323A86EC244D3F1D1D6D144018C3840
 * 
 */
public abstract class DriverTestBase extends CCSMTestCaseBase {

	/** Setup the instance. */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		initLogger();
	}

	/** Init logger. */
	protected void initLogger() {
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.OFF);
	}

	/** Load configuration, resolver processors and parameters. */
	public void runDriver(String filename, String... propertyFiles)
			throws DriverException {
		DataSinkProcessor.resetDataStore();
		Driver driver = new Driver();
		driver.setConfigFileName(useTestFile(filename).toString());
		for (String propertyFile : propertyFiles) {
			driver.readPropertyFile(useTestFile(propertyFile).toString());
		}
		driver.drive(null);
	}

}