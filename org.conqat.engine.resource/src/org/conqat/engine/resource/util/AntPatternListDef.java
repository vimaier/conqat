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
package org.conqat.engine.resource.util;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 961694F2D379ED0A92759768EC3D5EF1
 */
@AConQATProcessor(description = "Defines a pattern list based on Ant patterns.")
public class AntPatternListDef extends ConQATProcessorBase {

	/** Resulting pattern list. */
	private final PatternList patternList = new PatternList();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "ant-pattern", description = "Definition of a pattern.")
	public void addPattern(
			@AConQATAttribute(name = ConQATParamDoc.ANT_PATTERN_NAME, description = ConQATParamDoc.ANT_PATTERN_DESC) String antPattern,
			@AConQATAttribute(name = "case-sensitive", description = "true makes the pattern case sensitive [default ius true]", defaultValue = "true") boolean caseSensitive)
			throws ConQATException {

		patternList.add(ConQATDirectoryScanner.convertPattern(antPattern,
				caseSensitive));

	}

	/** {@inheritDoc} */
	@Override
	public PatternList process() {
		return patternList;
	}
}