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
 * Enumeration of XML elements required to read .trx-files.
 * 
 * @author Martin Feilkas
 * @author feilkas
 * @author $Author: juergens $
 * @version $Rev: 35167 $
 * @ConQAT.Rating GREEN Hash: 4626973113A821162663A098A58B43B4
 */
public enum ETrxXmlElement {

	/**
	 * The XML element containing aggregated information about all the test runs
	 */
	Counters,

	/** The results of a test run */
	UnitTestResult;
}