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
package org.conqat.engine.resource.scope;

import java.util.Arrays;
import java.util.Comparator;

import org.conqat.engine.resource.IContentAccessor;
import org.conqat.engine.resource.test.ResourceProcessorTestCaseBase;

import org.conqat.lib.commons.string.StringUtils;
import org.conqat.engine.core.core.ConQATException;

/**
 * Base class for tests dealing with scope processors.
 * 
 * @author hummelb
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: 4298252B96E2B634BFDC7C02B656B2FB
 */
public abstract class ScopeProcessorTestCaseBase extends
		ResourceProcessorTestCaseBase {

	/**
	 * Asserts that a given scope contains the same accessors as the expected
	 * ones. Here the expected ones are described by strings that form key/value
	 * pairs (separated by '='). As the strings are interpreted by CQDDL,
	 * special characters should be used with care. The comparison only deals
	 * with the uniform path and the content. The location is not checked.
	 */
	protected void assertScopesAreEqual(String[] expected,
			IContentAccessor[] resultScope) throws ConQATException {
		IContentAccessor[] expectedScope = (IContentAccessor[]) parseCQDDL("memScope("
				+ StringUtils.concat(expected, ", ") + ")");

		Arrays.sort(resultScope, new IContentAccessorComparator());
		Arrays.sort(expectedScope, new IContentAccessorComparator());

		assertEquals(expectedScope.length, resultScope.length);
		for (int i = 0; i < expectedScope.length; ++i) {
			assertEquals(expectedScope[i].getUniformPath(), resultScope[i]
					.getUniformPath());
			assertTrue(Arrays.equals(expectedScope[i].getContent(),
					resultScope[i].getContent()));
		}
	}

	/** Comparator for sorting {@link IContentAccessor}s by uniform path. */
	public static class IContentAccessorComparator implements
			Comparator<IContentAccessor> {
		/** {@inheritDoc} */
		@Override
		public int compare(IContentAccessor ca1, IContentAccessor ca2) {
			return ca1.getUniformPath().compareTo(ca2.getUniformPath());
		}
	}
}