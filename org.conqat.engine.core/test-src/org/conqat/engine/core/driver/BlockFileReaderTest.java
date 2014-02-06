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

import org.conqat.engine.core.bundle.BundleInfo;
import org.conqat.engine.core.driver.error.DriverException;
import org.conqat.engine.core.driver.error.EDriverExceptionType;
import org.conqat.engine.core.driver.specification.BlockSpecification;
import org.conqat.engine.core.driver.specification.SpecificationLoader;
import org.conqat.engine.core.logging.testutils.DriverTestBase;

/**
 * Test for {@link BlockFileReader}.
 * 
 * @author Tilman Seifert
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @levd.rating GREEN Hash: 89212F61ED955DD65B3E4F4A6AC1A287
 * 
 */
public class BlockFileReaderTest extends DriverTestBase {

	/** The specification loader used for reading files. */
	private SpecificationLoader specLoader;

	/**
	 * Test exception for an non-existent file.
	 */
	public void testIOError() {
		checkException("unknown-file", EDriverExceptionType.IO_ERROR);
	}

	/**
	 * Test exception for a config file with parsing errors.
	 */
	public void testXMLParsingError() {
		checkException("config-file-reader-01.cqb",
				EDriverExceptionType.XML_PARSING_EXCEPTION);
	}

	/**
	 * Test exception for config files with duplicate names.
	 */
	public void testDuplicateNames() throws DriverException {
		checkException("config-file-reader-06.cqb",
				EDriverExceptionType.DUPLICATE_NAME);

		// this one is ok
		readConfig("config-file-reader-07.cqb");

		checkException("config-file-reader-08.cqb",
				EDriverExceptionType.DUPLICATE_ATTRIBUTE_NAME);
		checkException("config-file-reader-09.cqb",
				EDriverExceptionType.DUPLICATE_NAME);
	}

	/** Test the correct handling of child elements in a parameter. */
	public void testChildElementsInParameter() {
		checkException("config-file-reader-11.cqb",
				EDriverExceptionType.PARAMETER_HAS_CHILDELEMENTS);
	}

	/** Test the handling of text in elements. */
	public void testContentHandling() {
		checkException("config-file-reader-13.cqb",
				EDriverExceptionType.PARAMETER_HAS_TEXT_CONTENT);
	}

	/**
	 * Read a file with an expected exception. Checks if the exception has the
	 * expected type.
	 * 
	 * @param filename
	 *            file to read
	 * @param expectedType
	 *            expected exception type.
	 */
	private void checkException(String filename,
			EDriverExceptionType expectedType) {
		try {
			readConfig(filename);
			fail("Exception should have been thrown");
		} catch (DriverException e) {
			assertEquals(expectedType, e.getType());
		}
	}

	/** Read analysis spec. */
	private BlockSpecification readConfig(String filename)
			throws DriverException {
		specLoader = new SpecificationLoader(null, new ArrayList<BundleInfo>());
		return new BlockFileReader(specLoader)
				.readBlockFile(useTestFile(filename));
	}

}