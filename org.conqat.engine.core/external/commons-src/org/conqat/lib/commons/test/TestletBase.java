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
package org.conqat.lib.commons.test;

/**
 * Base class for testlets. Testlets are junit test cases that are part of a
 * test suite. We use them for smoke tests.
 * <p>
 * A testlet must pass the name of its test method to its base class in its
 * constructor. This constraint comes from JUnit. It is easy to screw up!. This
 * base class enforces this constraint, so its harder to violate it.
 * <p>
 * Deriving classes can be annotated with the attribute <code>Ignore</code> to
 * tell the JUnit runner not to execute them outside a smoke test.
 * (Unfortunately, the attribute does not get inherited, so it is not sufficient
 * to annotate this class.)
 * 
 * @author juergens
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 73E7824C06216C1CB8FE238469423480
 */
public abstract class TestletBase extends CCSMTestCaseBase {

	/** Name of the method that gets called by JUnit */
	private static final String TEST_METHOD_NAME = "test";

	/**
	 * Default constructor
	 */
	protected TestletBase() {
		super(TEST_METHOD_NAME);
	}

	/** Template method: Deriving classes override it with their test */
	public abstract void test() throws Exception;

}