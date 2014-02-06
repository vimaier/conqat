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
package org.conqat.engine.core.driver.instance;

import java.util.Arrays;

import org.conqat.engine.core.driver.error.DriverException;
import org.conqat.engine.core.driver.info.BlockInfo;
import org.conqat.engine.core.driver.info.IInfo;
import org.conqat.engine.core.driver.info.ProcessorInfo;
import org.conqat.engine.core.driver.processors.DataSinkProcessor;
import org.conqat.engine.core.logging.testutils.DriverTestBase;
import org.conqat.lib.commons.collections.UnmodifiableList;

/**
 * Tests the actual execution process.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 0966C952F1BD29F359976874FD4EFDB9
 */
public class ExecutionTest extends DriverTestBase {

	/** Test if Execution works as expected. */
	@SuppressWarnings("null")
	public void testExecution() throws DriverException {
		DataSinkProcessor.resetDataStore();

		runDriver("execution-01.cqb");

		assertNotNull(DataSinkProcessor.blockInfo);

		ProcessorInfo fail = null;
		ProcessorInfo id = null;
		ProcessorInfo sink = null;
		for (IInfo info : DataSinkProcessor.blockInfo.getChildren()) {
			if (info.getDeclarationName().equals("fail")) {
				fail = (ProcessorInfo) info;
			} else if (info.getDeclarationName().equals("id")) {
				id = (ProcessorInfo) info;
			} else if (info.getDeclarationName().equals("sink")) {
				sink = (ProcessorInfo) info;
			}
		}

		assertNotNull(fail);
		assertNotNull(id);
		assertNotNull(sink);

		// can not be null as we tested it before!
		assertEquals(EInstanceState.FAILED_BADLY, fail.getState());
		assertEquals(EInstanceState.FAILED_DUE_TO_MISSING_INPUT, id.getState());
		assertEquals(EInstanceState.RUN_SUCCESSFULLY, sink.getState());

		// check if data is available
		assertEquals(Arrays.asList("data"),
				DataSinkProcessor.accessData("test"));
		assertEquals(Arrays.asList("field_value"),
				DataSinkProcessor.accessData("field"));
	}

	/**
	 * Test if execution with split parameters (i.e. parameters referencing
	 * different block inputs) works as expected.
	 */
	public void testSplitExecution() throws DriverException {
		DataSinkProcessor.resetDataStore();

		runDriver("execution-02.cqb");

		assertEquals(Arrays.asList("hello world!"),
				DataSinkProcessor.accessData("child1"));
		assertEquals(Arrays.asList("hiho hiho"),
				DataSinkProcessor.accessData("child2"));
	}

	/**
	 * Test if execution with split parameters fails in a clean manner.
	 */
	@SuppressWarnings("null")
	public void testSplitExecutionWithError() throws DriverException {
		DataSinkProcessor.resetDataStore();

		runDriver("execution-03.cqb");

		assertNotNull(DataSinkProcessor.blockInfo);

		ProcessorInfo stringGenerator = null;
		for (IInfo info : DataSinkProcessor.blockInfo.getChildren()) {
			if (info.getDeclarationName().equals("child1")) {
				UnmodifiableList<IInfo> children = ((BlockInfo) info)
						.getChildren();
				assertEquals(1, children.size());
				stringGenerator = (ProcessorInfo) children.get(0);
			}
		}

		assertNotNull(stringGenerator);
		assertEquals(EInstanceState.FAILED_DUE_TO_MISSING_INPUT,
				stringGenerator.getState());
	}

}