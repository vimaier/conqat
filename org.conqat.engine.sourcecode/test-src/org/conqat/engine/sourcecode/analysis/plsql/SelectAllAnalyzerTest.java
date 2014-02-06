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

import org.conqat.engine.sourcecode.analysis.FindingsTokenTestCaseBase;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.lib.scanner.ELanguage;

/**
 * Tests {@link SelectAllAnalyzer}.
 * 
 * @author $Author: goede $
 * @version $Rev: 43228 $
 * @ConQAT.Rating GREEN Hash: 4554D9CF9332532BA2E403B0CFCD51EE
 */
public class SelectAllAnalyzerTest extends FindingsTokenTestCaseBase {

    /** Constructor. */
    public SelectAllAnalyzerTest() {
        super(SelectAllAnalyzer.class, ELanguage.PLSQL);
    }

    /** Simple test case. */
    public void testSimple() throws Exception {
        String code =
                "procedure test is\n" + "begin\n"
                        + "   call_some_function();\n"
                        + "   SELECT /*comment*/ * FROM foo INTO bar;\n"
                        + "end;\n";

        ITokenElement element = createTokenElement(code, ELanguage.PLSQL);
        executeProcessor(SelectAllAnalyzer.class, "(input=(ref=", element, "))");

        assertFindingCount(element, 1);
        assertFinding(element, 4);
    }
}
