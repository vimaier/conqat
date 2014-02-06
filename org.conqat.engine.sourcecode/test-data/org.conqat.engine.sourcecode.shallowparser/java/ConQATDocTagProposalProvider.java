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
package org.conqat.ide.editor.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;

import org.conqat.engine.core.driver.specification.ConQATDocTagletProcessor;
import org.conqat.ide.editor.model.CQXmlAttribute;
import org.conqat.ide.editor.model.CQXmlBlockSpecModel;
import org.conqat.ide.editor.model.CQXmlParameter;
import org.conqat.ide.editor.model.CQXmlUnit;

/**
 * Proposal provider used to allow auto-completion for ConQATDoc tags.
 * 
 * @author hummelb
 * @author $Author: hummelb $
 * @version $Rev: 35946 $
 * @ConQAT.Rating GREEN Hash: BA7C55142A87B22D557130A9FF85E71B
 */
/* package */class ConQATDocTagProposalProvider implements
		IContentProposalProvider {

	/** The model for which proposals are generated. */
	private final CQXmlBlockSpecModel model;

	/** Constructor. */
	public ConQATDocTagProposalProvider(CQXmlBlockSpecModel model) {
		this.model = model;
	}

	/** {@inheritDoc} */
	@Override
	public IContentProposal[] getProposals(String contents, int position) {
		int openBrace = contents.substring(0, position).lastIndexOf("{@");
		if (openBrace < 0) {
			return new IContentProposal[] {};
		}

		int closeBrace = contents.indexOf("}", openBrace) + 1;
		if (closeBrace <= 0) {
			closeBrace = contents.length();
		}

		if (position > closeBrace) {
			return new IContentProposal[] {};
		}

		String prefix = contents.substring(openBrace, position);

		List<IContentProposal> proposals = new ArrayList<IContentProposal>();
		for (String rawTag : determineTags()) {
			String tag = "{@" + rawTag + "}";
			if (tag.startsWith(prefix)) {
				String newContent = contents.substring(0, openBrace) + tag
						+ contents.substring(closeBrace);
				int newCursorPosition = openBrace + tag.length();
				proposals.add(new ConQATDocProposal(newContent, tag,
						newCursorPosition));
			}
		}
		return proposals.toArray(new IContentProposal[proposals.size()]);
	}

	/**
	 * Determines the tags which can be used in the documentation (depends on
	 * the current model).
	 */
	private List<String> determineTags() {
		List<String> result = new ArrayList<String>();
		result.add(ConQATDocTagletProcessor.CONNDOC_TAG);
		appendChildDocs(model.getProcessors(), result);
		appendChildDocs(model.getBlocks(), result);
		return result;
	}

	/** Append childDoc tags for the given units. */
	private void appendChildDocs(List<? extends CQXmlUnit> units,
			List<String> result) {
		for (CQXmlUnit unit : units) {
			String unitTag = ConQATDocTagletProcessor.CHILDDOC_TAG
					+ unit.getName();
			result.add(unitTag);

			for (CQXmlParameter parameter : unit.getParameters()) {
				String parameterTag = unitTag + "." + parameter.getName();
				result.add(parameterTag);
				for (CQXmlAttribute attribute : parameter.getAttributes()) {
					result.add(parameterTag + "." + attribute.getName());
				}
			}
		}
	}

	/** Proposal implementation used. */
	private static class ConQATDocProposal implements IContentProposal {

		/** The new content for the text field. */
		private final String newContent;

		/** The name of the tag (as displayed to the user). */
		private final String tag;

		/** The cursor position to be used after replacement. */
		private final int newCursorPosition;

		/** Constructor. */
		private ConQATDocProposal(String newContent, String tag,
				int newCursorPosition) {
			this.newContent = newContent;
			this.tag = tag;
			this.newCursorPosition = newCursorPosition;
		}

		/** {@inheritDoc} */
		@Override
		public String getLabel() {
			return tag;
		}

		/** {@inheritDoc} */
		@Override
		public String getDescription() {
			return null;
		}

		/** {@inheritDoc} */
		@Override
		public int getCursorPosition() {
			return newCursorPosition;
		}

		/** {@inheritDoc} */
		@Override
		public String getContent() {
			return newContent;
		}
	}
}