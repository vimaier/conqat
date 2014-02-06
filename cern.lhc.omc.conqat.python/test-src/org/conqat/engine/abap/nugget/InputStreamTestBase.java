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
package org.conqat.engine.abap.nugget;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.Assert;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.test.CCSMTestCaseBase;

/**
 * Base class for tests of different kinds of {@link InputStream}s.
 * 
 * @author herrmama
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: 8EC4C743EED02D69DF53EE3FD23A0107
 */
public abstract class InputStreamTestBase extends CCSMTestCaseBase {

	/** Assert that the stream produces a certain output for a certain input. */
	protected void assertEqualsStreamOutput(String expectedOut, String in) {
		try {
			Assert.assertEquals(
					"Output from stream is not equal to expected output.",
					expectedOut, sendThroughStream(in));
		} catch (IOException e) {
			Assert.assertTrue("Error during streaming", false);
		}
	}

	/** Send a string through the input stream. */
	private String sendThroughStream(String in) throws IOException {
		ByteArrayInputStream input = new ByteArrayInputStream(in.getBytes());
		InputStream stream = createInputStream(input);
		return FileSystemUtils.readStream(stream);
	}

	/** Create the input stream that should be tested. */
	protected abstract InputStream createInputStream(InputStream input);
}