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
package org.conqat.engine.code_clones.result.annotation;

import java.util.ArrayList;

import org.conqat.engine.code_clones.detection.CloneDetectionResultElement;
import org.conqat.engine.code_clones.detection.CloneDetector;
import org.conqat.engine.code_clones.lazyscope.TokenElementProvider;
import org.conqat.engine.code_clones.normalization.statement.StatementNormalization;
import org.conqat.engine.code_clones.normalization.token.TokenNormalization;
import org.conqat.engine.code_clones.normalization.token.TokenProvider;
import org.conqat.engine.code_clones.normalization.token.configuration.ITokenConfiguration;
import org.conqat.engine.code_clones.normalization.token.configuration.TokenConfigurationDef;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.TokenTestCaseBase;
import org.conqat.lib.commons.filesystem.CanonicalFile;
import org.conqat.lib.scanner.ELanguage;

/**
 * Test case for {@link RfssAnnotator}
 * 
 * @author juergens
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: A59C5BB6F9F8DC8514BB346FEC9A127D
 */
public class RfssAnnotatorTest extends TokenTestCaseBase {

	/** Test case of a single line that is cloned many times */
	public void testSingleLineCloneSequence() throws Exception {
		int minLength = 1;
		String filename = useTestFile("StatementSequence.java")
				.getAbsolutePath();

		double rfss = performDetection(minLength, filename);
		assertEqualsDouble(1.0, rfss);
	}

	/** Test case of a large clone that is repeated twice */
	public void testTwoConsecutiveClones() throws Exception {
		int minLength = 10;
		String filename = useTestFile("CloneSequence.java").getAbsolutePath();

		double rfss = performDetection(minLength, filename);
		assertEqualsDouble(16.0, rfss);
	}

	/** Test case in which no clone is found */
	public void testNoClone() throws Exception {
		int minLength = 20;
		String filename = useTestFile("CloneSequence.java").getAbsolutePath();

		double rfss = performDetection(minLength, filename);
		assertEqualsDouble(32.0, rfss);
	}

	/** Test overlapping clones */
	public void testOverlappingClones() throws Exception {
		int minLength = 5;
		String filename = useTestFile("OverlappingClones.java")
				.getAbsolutePath();

		double rfss = performDetection(minLength, filename);
		assertEqualsDouble(18.0, rfss);
	}

	/** Compares two doubles for equality */
	private void assertEqualsDouble(double expected, double actual) {
		double difference = expected - actual;
		assertTrue(Math.abs(difference) < 0.00000000001);
	}

	/** Performs clone detection */
	private double performDetection(int minLength, String filename)
			throws Exception {
		ITokenElement element = createTokenElement(new CanonicalFile(filename),
				ELanguage.JAVA);
		TokenNormalization tokenNormalization = new TokenNormalization(
				new TokenProvider(new TokenElementProvider()),
				new ArrayList<ITokenConfiguration>(),
				new TokenConfigurationDef().process());
		Object statementNormalization = new StatementNormalization(
				tokenNormalization, false);

		CloneDetectionResultElement result = (CloneDetectionResultElement) executeProcessor(
				CloneDetector.class, "(input=(ref=", element,
				"), normalization=(ref=", statementNormalization,
				"), clonelength=(min=", minLength, "))");

		executeProcessor(RfssAnnotator.class, "(input=(ref=", result, "))");
		return (Double) result.getValue(RfssAnnotator.RFSS_KEY);
	}

}