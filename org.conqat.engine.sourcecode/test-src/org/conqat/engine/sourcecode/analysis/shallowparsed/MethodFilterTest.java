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
package org.conqat.engine.sourcecode.analysis.shallowparsed;

import java.io.IOException;
import java.util.List;

import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.commons.pattern.PatternListDefTest;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.filter.TextFilterTestBase;
import org.conqat.engine.resource.text.filter.base.Deletion;
import org.conqat.engine.resource.text.filter.base.ITextFilter;
import org.conqat.lib.commons.filesystem.FileSystemUtils;

/**
 * Tests the {@link MethodFilter}.
 * 
 * @author $Author: juergens $
 * @version $Rev: 40964 $
 * @ConQAT.Rating GREEN Hash: 2F54926EDC216AF909D4266B469E2731
 */
public class MethodFilterTest extends TextFilterTestBase {

	/** Test if method gets recognized in file */
	public void testProcessFile() throws IOException, ConQATException {
		String content = FileSystemUtils
				.readFile(useTestFile("Form1.Designer.cs"));
		assertCleanDeletions(
				calculateDeletions(content, "InitializeComponent"), 901, 3691);
	}

	/** Calculates the filter's deletions for the given string. */
	private List<Deletion> calculateDeletions(String string, String... patterns)
			throws ConQATException {
		PatternList patternList = PatternListDefTest
				.createPatternList(patterns);

		ITextFilter filter = (ITextFilter) executeProcessor(MethodFilter.class,
				"('method-name-patterns'=(ref=", patternList,
				"), language=(name=CS))");
		return filter.getDeletions(string,
				MethodFilterTest.class.getCanonicalName());
	}
}
