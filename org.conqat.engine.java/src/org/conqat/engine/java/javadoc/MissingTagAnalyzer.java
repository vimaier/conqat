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

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.java.resource.IJavaElement;

import com.sun.javadoc.ProgramElementDoc;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 70F993A79A11316A7C4F87507F6FAD9C
 */
@AConQATProcessor(description = "This analyzer checks if a comment has the "
		+ "specified tag.")
public class MissingTagAnalyzer extends ProgramElementDocAnalyzerBase {

	/** Tag to analyze. */
	private String tagName;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "tag", description = "Specifies inline tag to analyze", minOccurrences = 1, maxOccurrences = 1)
	public void setTagName(
			@AConQATAttribute(name = "name", description = "Name of the tag without @-sign") String tagName) {
		this.tagName = tagName;
	}

	/** {@inheritDoc} */
	@Override
	public void analyze(ProgramElementDoc docElement, IJavaElement element)
			throws ConQATException {
		if (docElement.tags(tagName).length == 0) {
			createFinding(docElement.qualifiedName() + " has no " + tagName
					+ " tag.", docElement, element);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected String getFindingGroupName() {
		return "Missing @" + tagName + " tag";
	}
}