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
import com.sun.javadoc.Tag;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 91729C18079DBD58023F268C8A03940C
 */
@AConQATProcessor(description = "This analyzer checks if a component contains a"
		+ " flawed inline tag, i.e. that is either not enclosed in braces or has "
		+ "the wrong capitalization.")
public class InvalidInlineTagAnalyzer extends ProgramElementDocAnalyzerBase {

	/** Tag name. */
	private String tagName;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "tag", description = "Specifies inline tag to analyze", minOccurrences = 1, maxOccurrences = 1)
	public void setTagName(
			@AConQATAttribute(name = "name", description = "Name of the tag without @-sign") String tagName) {
		this.tagName = "@" + tagName;
	}

	/** Checks if an element has an inline tag with the specified name. */
	private boolean hasInlineTag(ProgramElementDoc docElement, String tagName) {
		for (Tag tag : docElement.inlineTags()) {
			if (tag.name().equals(tagName)) {
				return true;
			}
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public void analyze(ProgramElementDoc docElement, IJavaElement element) throws ConQATException{
		if (docElement.getRawCommentText().toLowerCase()
				.contains(tagName.toLowerCase())
				&& !hasInlineTag(docElement, tagName)) {
			createFinding(docElement.qualifiedName() + " has invalid "
					+ tagName + " tag.", docElement, element);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected String getFindingGroupName() {
		return "Invalid @" + tagName + " tag";
	}

}