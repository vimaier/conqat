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
package org.conqat.engine.code_clones.detection;

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.code_clones.core.Unit;
import org.conqat.engine.code_clones.lazyscope.TokenElementProvider;
import org.conqat.engine.code_clones.normalization.provider.IUnitProvider;
import org.conqat.engine.code_clones.normalization.statement.StatementNormalization;
import org.conqat.engine.code_clones.normalization.token.ITokenProvider;
import org.conqat.engine.code_clones.normalization.token.TokenProvider;
import org.conqat.engine.code_clones.normalization.token.configuration.ITokenConfiguration;
import org.conqat.engine.code_clones.normalization.token.configuration.TokenConfigurationDef;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.testutils.ProcessorInfoMock;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.engine.sourcecode.resource.TokenTestCaseBase;

/**
 * Tests whether all units from a number of files are returned correctly. This
 * unit test has been created for CR#1488.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 41159 $
 * @ConQAT.Rating GREEN Hash: 20C1380EC3639E5C4AAE70C92B3C3725
 */
public class SentinelizerTest extends TokenTestCaseBase {

	/** Test method for CR#1488. */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	// Due to ConQATs inability to deal with generics
	public void testDrainUnitProvider() throws ConQATException {

		ITokenResource root = createTokenResourceHierarchyFor(useTestFile(""));

		// Create normalization pipeline
		TokenElementProvider elementProvider = new TokenElementProvider();
		ITokenProvider tokenProvider = new TokenProvider(elementProvider);

		StatementNormalization normalization = new StatementNormalization(
				tokenProvider, new ArrayList<ITokenConfiguration>(),
				new TokenConfigurationDef());
		Sentinelizer sentinelizedNormalization = new Sentinelizer(
				(IUnitProvider) normalization);
		sentinelizedNormalization.init(root,
				new ProcessorInfoMock().getLogger());

		// drain units into list
		List<Unit> units = new ArrayList<Unit>();
		Unit unit = sentinelizedNormalization.getNext();
		while (unit != null) {
			units.add(unit);
			unit = sentinelizedNormalization.getNext();
		}

		// file A1 contains 3 statements, A2 contains 1 statement. Including 2
		// sentinel units, we expect to see 6 units
		assertEquals(6, units.size());
	}

}