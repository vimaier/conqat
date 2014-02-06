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
package org.conqat.engine.core.driver.specification;

import org.conqat.engine.core.driver.error.DriverException;
import org.conqat.engine.core.driver.error.EDriverExceptionType;
import org.conqat.engine.core.logging.testutils.DriverTestBase;

/**
 * Base class for all driver.specification test cases
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @levd.rating GREEN Hash: 19E2D7F0A01641079F5B64BB6140BEF4
 * 
 */
public abstract class ProcessorSpecificationTestBase extends DriverTestBase {

	/**
	 * Creates a processor specification from the given classname and checks if
	 * an exception of provided type is thrown during construction.
	 * <p>
	 * This uses a string parameter to test for unknwon classes, too.
	 */
	protected void checkException(String classname,
			EDriverExceptionType expectedType) {
		try {
			new ProcessorSpecification(classname);
			fail("Exception should have been thrown!");
		} catch (DriverException e) {
			assertEquals(expectedType, e.getType());
			System.out.println(e);
		}
	}

	/**
	 * Creates a processor specification from the given class and checks if an
	 * exception of provided type is thrown during construction.
	 */
	protected void checkException(Class<?> processorClass,
			EDriverExceptionType expectedType) {

		checkException(processorClass.getName(), expectedType);
	}
}