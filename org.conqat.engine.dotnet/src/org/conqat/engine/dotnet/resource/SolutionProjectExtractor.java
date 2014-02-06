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
package org.conqat.engine.dotnet.resource;

import java.util.Set;

import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.dotnet.resource.parser.SolutionParser;
import org.conqat.engine.resource.text.ITextElement;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: heinemann $
 * @version $Rev: 45823 $
 * @ConQAT.Rating GREEN Hash: 1D5CA8ED954236B8479D4F32B889F285
 */
@AConQATProcessor(description = "Processor for extracting content accessors for projects listed in "
		+ "a Visual Studio solution.")
public class SolutionProjectExtractor extends ContentAccessorExtractorBase {

	/** {@inheritDoc} */
	@Override
	protected Set<String> extractRelativePaths(ITextElement solutionElement)
			throws ConQATException {
		return SolutionParser.parse(solutionElement, getLogger());
	}
}