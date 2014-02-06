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
package org.conqat.engine.dotnet.test;

/**
 * Enumeration of XML attributes required to read .trx-files.
 * 
 * @author Martin Feilkas
 * @author feilkas
 * @author $Author: juergens $
 * @version $Rev: 35167 $
 * @ConQAT.Rating GREEN Hash: FF25729EB50D24C1E02E312F86D23F32
 */
public enum ETrxXmlAttribute {

	/** The id of the test */
	testId,

	/** The name of the test */
	testName,

	/** The duration of the test run */
	duration,

	/** The duration of the test run */
	outcome,

	/** The number of test cases that have been executed */
	executed,

	/** The number of passed tests */
	passed,

	/** The number of failed tests */
	failed,

	/** The start time of the test run */
	startTime,

	/** The end time of the test run */
	endTime,

	/** The number of tests that resulted in an error */
	error;
}