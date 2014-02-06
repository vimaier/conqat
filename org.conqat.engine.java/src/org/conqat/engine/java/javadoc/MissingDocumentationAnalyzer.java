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
package org.conqat.engine.java.javadoc;

import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.java.resource.IJavaElement;
import org.conqat.lib.commons.string.StringUtils;

import com.sun.javadoc.ProgramElementDoc;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: D80588E56B5AB9AC0BF121564D39AF3C
 */
@AConQATProcessor(description = "This analyzer simply checks if there is a JavaDoc comment at all.")
public class MissingDocumentationAnalyzer extends ProgramElementDocAnalyzerBase {

	/** {@inheritDoc} */
	@Override
	public void analyze(ProgramElementDoc docElement, IJavaElement element)
			throws ConQATException {
		if (StringUtils.isEmpty(docElement.getRawCommentText())) {
			createFinding(
					docElement.qualifiedName() + " has no documentation.",
					docElement, element);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected String getFindingGroupName() {
		return "Missing Documentation";
	}

}