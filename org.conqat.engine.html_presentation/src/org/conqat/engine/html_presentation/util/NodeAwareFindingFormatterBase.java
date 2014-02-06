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
package org.conqat.engine.html_presentation.util;

import static org.conqat.lib.commons.html.EHTMLElement.BR;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.conqat.engine.commons.findings.EFindingDeltaState;
import org.conqat.engine.commons.findings.EFindingKeys;
import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.findings.FindingsList;
import org.conqat.engine.commons.findings.location.ElementLocation;
import org.conqat.engine.commons.findings.location.QualifiedNameLocation;
import org.conqat.engine.commons.findings.location.TextRegionLocation;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.html_presentation.layouters.TableLayouter;
import org.conqat.engine.html_presentation.util.PresentationUtils.IContextSensitiveFormatter;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.lib.commons.html.EHTMLAttribute;
import org.conqat.lib.commons.html.EHTMLElement;
import org.conqat.lib.commons.html.HTMLWriter;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Base class for node aware formatters dealing with formatting of findings.
 * This class has a notion of a local node, which is the node for which the
 * findings are formatted.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating GREEN Hash: CF1879957466DBF8E97A00B39B2E7446
 */
public abstract class NodeAwareFindingFormatterBase implements
		IContextSensitiveFormatter<FindingsList> {

	/** Uniform path of local node. */
	protected final String uniformPath;

	/** The element the findings belong to (or null). */
	protected final ITextElement element;

	/** Constructor. */
	protected NodeAwareFindingFormatterBase(IConQATNode localNode) {
		if (localNode instanceof IElement) {
			uniformPath = ((IElement) localNode).getUniformPath();
			if (localNode instanceof ITextElement) {
				element = (ITextElement) localNode;
			} else {
				element = null;
			}
		} else {
			uniformPath = StringUtils.EMPTY_STRING;
			element = null;
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean isApplicable(Object value) {
		return value instanceof FindingsList;
	}

	/** {@inheritDoc} */
	@Override
	public void formatObject(FindingsList findings, HTMLWriter writer) {

		List<StructuredFinding> structuredFindings = new ArrayList<StructuredFinding>();
		for (Finding finding : findings) {
			structuredFindings.add(new StructuredFinding(finding));
		}
		Collections.sort(structuredFindings);

		boolean needsBreak = false;
		for (StructuredFinding structuredFinding : structuredFindings) {
			if (needsBreak) {
				writer.addClosedElement(BR);
			}
			needsBreak = true;

			structuredFinding.format(writer);
		}
	}

	/**
	 * Formats a location in a local fashion (i.e. without including the known
	 * uniform path). May return null if the location can not be more precise
	 * than the local node (e.g. if the finding affects the entire element).
	 */
	protected String formatLocalLocation(ElementLocation location) {
		if (location instanceof TextRegionLocation) {
			TextRegionLocation regionLocation = (TextRegionLocation) location;
			return "lines " + regionLocation.getRawStartLine() + " - "
					+ regionLocation.getRawEndLine();
		}

		if (location instanceof QualifiedNameLocation) {
			return ((QualifiedNameLocation) location).getQualifiedName();
		}

		return null;
	}

	/** Returns the URL to be used for the given location (or null). */
	protected abstract String determineUrl(ElementLocation location);

	/** This class prestructures the locations of a finding. */
	private class StructuredFinding implements Comparable<StructuredFinding> {

		/** The finding. */
		private final Finding finding;

		/** Constructor. */
		public StructuredFinding(Finding finding) {
			this.finding = finding;
		}

		/** Formats this finding into the given writer. */
		public void format(HTMLWriter writer) {
			String url = determineUrl(finding.getLocation());

			if (url != null) {
				writer.openElement(EHTMLElement.A, EHTMLAttribute.HREF, url,
						EHTMLAttribute.CLASS, TableLayouter.LINK_CLASS);
			}

			if (EFindingDeltaState.isInState(finding, EFindingDeltaState.ADDED)) {
				writer.addText("+ ");
			}

			if (EFindingDeltaState.isInState(finding,
					EFindingDeltaState.IN_MODIFIED_CODE)) {
				writer.addText("* ");
			}

			String position = formatLocalLocation(finding.getLocation());
			if (position != null) {
				writer.addText(position + ": ");
			}
			writer.addText(getMessage());

			if (url != null) {
				writer.closeElement(EHTMLElement.A);
			}

			if (finding.getValue(EFindingKeys.FINGERPRINT.name()) != null) {
				writer.addText(" ");
				writer.openElement(
						EHTMLElement.A,
						EHTMLAttribute.ONCLICK,
						"prompt('Hash for finding is:','"
								+ finding.getValue("fingerprint") + "')",
						EHTMLAttribute.CLASS, TableLayouter.LINK_CLASS);
				writer.addText("#");
				writer.closeElement(EHTMLElement.A);
			}
		}

		/** Returns the message used. */
		private String getMessage() {
			// fallback to group name for missing message
			return NodeUtils.getStringValue(finding, EFindingKeys.MESSAGE
					.toString(), finding.getParent().getName());
		}

		/**
		 * {@inheritDoc}
		 * <p>
		 * Basically sorts by location.
		 */
		@SuppressWarnings("unchecked")
		@Override
		public int compareTo(StructuredFinding other) {
			int primaryKey1 = getPrimarySortKey();
			int primaryKey2 = other.getPrimarySortKey();
			if (primaryKey1 != primaryKey2) {
				return primaryKey1 - primaryKey2;
			}

			Comparable<Object> secondaryKey1 = (Comparable<Object>) getSecondaryKey();
			Comparable<Object> secondaryKey2 = (Comparable<Object>) other
					.getSecondaryKey();
			int compare = secondaryKey1.compareTo(secondaryKey2);
			if (compare != 0) {
				return compare;
			}

			return getMessage().compareTo(other.getMessage());
		}

		/**
		 * Returns a primary sorting key. This orders findings by kind of
		 * location.
		 */
		private int getPrimarySortKey() {
			if (finding.getLocation() instanceof QualifiedNameLocation) {
				return 2;
			}
			if (finding.getLocation() instanceof TextRegionLocation) {
				return 3;
			}
			return 1;
		}

		/**
		 * Returns a secondary sorting key. This only has to be comparable for
		 * findings where {@link #getPrimarySortKey()} returns the same value.
		 */
		private Comparable<?> getSecondaryKey() {
			if (finding.getLocation() instanceof QualifiedNameLocation) {
				return ((QualifiedNameLocation) finding.getLocation())
						.getQualifiedName();
			}
			if (finding.getLocation() instanceof TextRegionLocation) {
				return ((TextRegionLocation) finding.getLocation())
						.getRawStartOffset();
			}

			// sort others by message
			return getMessage();
		}
	}

}
