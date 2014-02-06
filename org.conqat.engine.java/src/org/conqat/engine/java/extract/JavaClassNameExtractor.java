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
package org.conqat.engine.java.extract;

import java.io.StringReader;

import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.java.library.JavaLibrary;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.engine.resource.text.TextElementProcessorBase;

/**
 * This processor extracts the full qualified name of Java types by analyzing
 * the package statement and the name of the file. The name is stored as a
 * value.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: C2A89AD1260389B0BB98326435BE666D
 */
@AConQATProcessor(description = "This processor extracts the full qualified "
		+ "name of Java types by analyzing the package statement and "
		+ "the name of the file. The name is stored as a value. If the name "
		+ "could not be extracted, a warning is logged.")
public class JavaClassNameExtractor extends TextElementProcessorBase {

	/** The key to use for saving. */
	@AConQATKey(description = "The fully qualified class name.", type = "java.lang.String")
	public static final String KEY = "ClassName";

	/** Set key to display list. */
	@Override
	protected void setUp(ITextResource root) {
		NodeUtils.addToDisplayList(root, KEY);
	}

	/** {@inheritDoc} */
	@Override
	protected void processElement(ITextElement element) {
		try {
			String name = JavaLibrary.getFQClassName(element.getLocation(),
					new StringReader(element.getTextContent()));
			element.setValue(KEY, name);
		} catch (ConQATException e) {
			getLogger().warn(
					"Could not extract package name from element: " + element
							+ " (" + e.getMessage() + ")");
		}
	}

}