/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
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
package org.conqat.engine.sourcecode.analysis.plsql;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.analysis.FindingsTokenTestCaseBase;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.lib.scanner.ELanguage;

/**
 * Tests {@link PlsqlExceptionHandlerOthersAnalyzer}.
 * 
 * @author $Author: goede $
 * @version $Rev: 43228 $
 * @ConQAT.Rating GREEN Hash: 095E5225CAA0078CC116ABF4CD52743E
 */
public class PlsqlExceptionHandlerOthersAnalyzerTest extends
        FindingsTokenTestCaseBase {

    /** Constructor. */
    public PlsqlExceptionHandlerOthersAnalyzerTest() {
        super(PlsqlExceptionHandlerOthersAnalyzer.class, ELanguage.PLSQL);
    }

    /** Basic testing of findings creation. */
    public void testFindings() throws ConQATException {
        ITokenElement element =
                createTokenElement(
                        useCanonicalTestFile("PlsqlExceptionHandlerOthersAnalyzer.plsql"),
                        ELanguage.PLSQL);
        executeProcessor(PlsqlExceptionHandlerOthersAnalyzer.class,
                "(input=(ref=", element, "))");

        assertFindingCount(element, 1);
        assertFinding(element, 30);
    }
}
