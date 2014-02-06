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
package org.conqat.engine.sourcecode.analysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.scanner.IScanner;
import org.conqat.lib.scanner.IToken;
import org.conqat.lib.scanner.ScannerException;
import org.conqat.lib.scanner.ScannerFactory;
import org.conqat.lib.scanner.ScannerUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: heinemann $
 * @version $Rev: 44267 $
 * @ConQAT.Rating GREEN Hash: 29273CC7980C1D6A634D01B39B0D9F26
 */
@AConQATProcessor(description = "Annotates each element with an assessment of whether it could be fully processed by the scanner or not. "
		+ "Optionally, findings can be created for scanner problems.")
public class ScannerSuccessAssessor extends AnalysisProblemsProcessorBase {

	/** Finding group name used. */
	private static final String FINDING_GROUP_NAME = "Scanner Problems";

	/** {ConQAT.Doc} */
	@AConQATKey(description = "Green if the element could be scanned successfully, red otherwise.", type = "org.conqat.lib.commons.assessment.Assessment")
	public static final String SCANNING_ASSESSMENT_KEY = "scannable";

	/** {ConQAT.Doc} */
	@AConQATKey(description = "Text description of the scanning problem.", type = "java.lang.String")
	public static final String SCANNING_ERROR_KEY = "scanner error";

	/** Constructor. */
	public ScannerSuccessAssessor() {
		super(SCANNING_ASSESSMENT_KEY, SCANNING_ERROR_KEY, FINDING_GROUP_NAME);
	}

	/** {@inheritDoc} */
	@Override
	protected String getAnalysisErrorMessage(ITokenElement element)
			throws ConQATException {
		IScanner scanner = ScannerFactory.newScanner(element.getLanguage(),
				element.getTextContent(), element.getUniformPath());

		List<ScannerException> exceptions = new ArrayList<ScannerException>();
		try {
			ScannerUtils.readTokens(scanner, new ArrayList<IToken>(),
					exceptions);
		} catch (IOException e) {
			CCSMAssert.fail("This should be impossible (we work on strings)!");
		}

		if (!exceptions.isEmpty()) {
			// We only report the first error here, as typically there are
			// either only few errors, or hundreds (e.g. if a wrong language is
			// set or a binary file is processed by accident). As these are
			// problems that often need an update of the scanner library, we are
			// mostly interested to find whether there is an error or not.
			return exceptions.get(0).getMessage();
		}
		return null;
	}
}