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
package org.conqat.engine.html_presentation.layouters;

import static org.conqat.lib.commons.html.EHTMLAttribute.CELLPADDING;
import static org.conqat.lib.commons.html.EHTMLAttribute.CELLSPACING;
import static org.conqat.lib.commons.html.EHTMLAttribute.CLASS;
import static org.conqat.lib.commons.html.EHTMLAttribute.COLSPAN;
import static org.conqat.lib.commons.html.EHTMLAttribute.HREF;
import static org.conqat.lib.commons.html.EHTMLAttribute.ONCLICK;
import static org.conqat.lib.commons.html.EHTMLAttribute.STYLE;
import static org.conqat.lib.commons.html.EHTMLAttribute.WIDTH;
import static org.conqat.lib.commons.html.EHTMLElement.A;
import static org.conqat.lib.commons.html.EHTMLElement.TABLE;
import static org.conqat.lib.commons.html.EHTMLElement.TD;
import static org.conqat.lib.commons.html.EHTMLElement.TR;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.findings.DetachedFinding;
import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.findings.location.ElementLocation;
import org.conqat.engine.commons.findings.location.TextRegionLocation;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.ConQATNodePredicateBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.html_presentation.base.ColorConstants;
import org.conqat.engine.html_presentation.links.LinkProviderBase;
import org.conqat.engine.html_presentation.listing.ListingWriter;
import org.conqat.engine.html_presentation.util.JavaScriptUtils;
import org.conqat.engine.html_presentation.util.LayouterBase;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.engine.resource.util.UniformPathUtils;
import org.conqat.lib.commons.collections.PairList;
import org.conqat.lib.commons.html.CSSDeclarationBlock;
import org.conqat.lib.commons.html.ECSSProperty;
import org.conqat.lib.commons.html.EHTMLAttribute;
import org.conqat.lib.commons.string.StringUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating YELLOW Hash: D28024F9E5FE40D5794B283195DDF2CB
 */
@AConQATProcessor(description = "Layouter for findings lists. This layouter creates a result "
		+ "page that displays the findings stored in the specified key. "
		+ "This layouter works both on lists of findings or lists of strings that e.g. represent removed findings.")
public class FindingListLayouter extends LayouterBase {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.INPUT_NAME, attribute = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_DESC)
	public IResource input;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.INCLUSION_PREDICATE_PARAM, attribute = ConQATParamDoc.INCLUSION_PREDICATE_ATTRIBUTE, optional = true, description = ConQATParamDoc.INCLUSION_PREDICATE_DESC)
	public ConQATNodePredicateBase inclusionPredicate;

	/** The keys to be searched for findings. */
	private final Set<String> findingsKeys = new HashSet<String>();

	/** Maps from uniform paths to elements */
	private Map<String, IElement> uniformPathToElement;

	/** Counts the number of findings or messages for the current element. */
	private int messageOrFindingCount;

	/**
	 * The names of the additional columns. The first elements store the key for
	 * the displayed information, the second elements the (optional) hidden
	 * elements.
	 */
	private final PairList<String, String> additionalColumns = new PairList<String, String>();

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "table-width", attribute = "value", optional = true, description = ""
			+ "Specifies desired width of table (e.g. '100px', '20%', '300em',...). "
			+ "If the width is left unspecified, the table will be as wide as needed.")
	public String tableWidth;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "max-message-length", attribute = "value", optional = true, description = "If set, the message of the findings "
			+ "will be trimmed to the specified number of characters.")
	public int maxMessageLength = Integer.MAX_VALUE;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.FINDING_PARAM_NAME, description = ConQATParamDoc.FINDING_KEYS_PARAM_DOC)
	public void addFindingKey(
			@AConQATAttribute(name = ConQATParamDoc.READKEY_KEY_NAME, description = ConQATParamDoc.READKEY_KEY_DESC) String key) {
		findingsKeys.add(key);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "column", description = "Adds an additional column to the findings.")
	public void addColumn(
			@AConQATAttribute(name = ConQATParamDoc.READKEY_KEY_NAME, description = ConQATParamDoc.READKEY_KEY_DESC) String key,
			@AConQATAttribute(name = "secondary-key", defaultValue = "NONE", description = "If this is set and value is stored for this key in the finding, "
					+ "the value will be made available via a link on the primary value that opens a popup.") String secondaryKey) {
		additionalColumns.add(key, secondaryKey);
	}

	/** Returns {@inheritDoc}. */
	@Override
	protected String getIconName() {
		return TableLayouter.TABLE_ICON_NAME;
	}

	/** {@inheritDoc} */
	@Override
	protected void setUp() {
		if (findingsKeys.isEmpty()) {
			findingsKeys.addAll(NodeUtils.getDisplayList(input).getKeyList());
		}

		uniformPathToElement = ResourceTraversalUtils
				.createUniformPathToElementMap(input, IElement.class);
	}

	/** {@inheritDoc} */
	@Override
	protected void layoutPage() {
		writer.openElement(TABLE, CELLSPACING, "0", CELLPADDING, "2", STYLE,
				TableLayouter.TABLE_BORDER_STYLE);
		if (tableWidth != null) {
			writer.addAttribute(WIDTH, tableWidth);
		}

		for (IElement element : ResourceTraversalUtils.listElements(input)) {
			layoutMessagesAndFindingsForElement(element);
		}

		writer.closeElement(TABLE);
	}

	/** Layout findings for element. */
	private void layoutMessagesAndFindingsForElement(IElement element) {
		messageOrFindingCount = 0;
		for (String key : findingsKeys) {
			Object value = element.getValue(key);
			if (!(value instanceof Collection)) {
				continue;
			}

			for (Object message : (Collection<?>) value) {
				if (formatMessageOrFinding(message, element,
						messageOrFindingCount == 0)) {
					messageOrFindingCount += 1;
				}
			}
		}
	}

	/**
	 * Formats and outputs the given message or finding.
	 * 
	 * @return whether the message could be formatted.
	 */
	private boolean formatMessageOrFinding(Object messageOrFinding,
			IElement element, boolean needsHeader) {

		boolean isSurrogate = messageOrFinding instanceof String
				|| messageOrFinding instanceof DetachedFinding;
		boolean isFinding = messageOrFinding instanceof Finding;

		if (!isSurrogate && !isFinding || isFinding
				&& !isContained(messageOrFinding)) {
			return false;
		}

		if (messageOrFinding instanceof DetachedFinding) {
			messageOrFinding = messageOrFinding.toString();
		}

		if (needsHeader) {
			writeElementHeader(element);
		}

		writer.openElement(TR, CLASS, TableLayouter.ROW_CLASS);
		if (isSurrogate) {
			addClosedMessageCell((String) messageOrFinding, COLSPAN,
					getColumnCount());
		} else if (isFinding) {
			formatFindingMessage((Finding) messageOrFinding, element);
		}
		writer.closeElement(TR);

		return true;
	}

	/** Returns true, if the finding is contained (or containment is undefined) */
	private boolean isContained(Object messageOrFinding) {
		return inclusionPredicate == null
				|| inclusionPredicate.isContained((Finding) messageOrFinding);
	}

	/** Returns the number of columns. */
	private int getColumnCount() {
		return 2 + additionalColumns.size();
	}

	/** Writes the header for an element. */
	private void writeElementHeader(IElement element) {
		writer.openElement(TR, CLASS, TableLayouter.HEADER_CLASS);
		writer.openElement(TD, COLSPAN, getColumnCount());

		String link = LinkProviderBase.obtainLink(element);
		if (link == null) {
			writer.addText(element.getUniformPath());
		} else {
			writer.addClosedTextElement(A, element.getUniformPath(), HREF,
					link, CLASS, new CSSDeclarationBlock(
							TableLayouter.LINK_CLASS, ECSSProperty.COLOR,
							"white", ECSSProperty.FONT_WEIGHT, "bold"));
		}

		closeMessageCell();
		writer.closeElement(TR);
	}

	/** Format and output a single finding. */
	private void formatFindingMessage(Finding finding, IElement currentElement) {
		addClosedMessageCell(finding.getMessage());

		openMessageCell(CLASS, TableLayouter.CELL_CLASS);
		writeLocation(finding.getLocation(), currentElement);
		closeMessageCell();

		for (int i = 0; i < additionalColumns.size(); ++i) {
			addAdditionalColumn(finding, additionalColumns.getFirst(i),
					additionalColumns.getSecond(i));
		}
	}

	/** Adds an additional column for a finding. */
	private void addAdditionalColumn(Finding finding, String primaryKey,
			String secondaryKey) {

		Object primaryValue = finding.getValue(primaryKey);
		Object secondaryValue = finding.getValue(secondaryKey);

		if (primaryValue == null) {
			if (secondaryKey == null) {
				primaryValue = " ";
			} else {
				// we need a non-empty string to make the link available
				primaryValue = "<none>";
			}
		}

		openMessageCell(CLASS, TableLayouter.CELL_CLASS);

		if (secondaryValue == null) {
			writer.addText(primaryValue.toString());
		} else {
			String clickScript = "prompt('Value for "
					+ JavaScriptUtils.escapeJavaScript(secondaryKey)
					+ ":', '"
					+ JavaScriptUtils.escapeJavaScript(secondaryValue
							.toString()) + "');";
			writer.addClosedTextElement(A, primaryValue.toString(), ONCLICK,
					clickScript, CLASS, TableLayouter.LINK_CLASS);
		}

		closeMessageCell();
	}

	/** Adds a closed message cell containing given text. */
	private void addClosedMessageCell(String text, Object... attributes) {
		openMessageCell(attributes);
		if (text.length() > maxMessageLength) {
			text = text.substring(0, maxMessageLength - 3) + "...";
		}
		writer.addText(text);
		closeMessageCell();
	}

	/** Opens a TD element for a message. */
	private void openMessageCell(Object... attributes) {
		writer.openElement(TD, attributes);
		if (messageOrFindingCount % 2 != 0) {
			writer.addAttribute(EHTMLAttribute.STYLE, new CSSDeclarationBlock(
					ECSSProperty.BACKGROUND_COLOR,
					ColorConstants.TABLE_CELL_ODD));
		}
	}

	/** Closes a TD element for a message. */
	private void closeMessageCell() {
		writer.closeElement(TD);
	}

	/** Writes location information. */
	private void writeLocation(ElementLocation location, IElement currentElement) {
		IElement element = uniformPathToElement.get(location.getUniformPath());

		String locationText = location.toLocationString();
		locationText = StringUtils.stripPrefix(
				UniformPathUtils.getParentPath(currentElement.getUniformPath())
						+ UniformPathUtils.SEPARATOR, locationText);

		String link = LinkProviderBase.obtainLink(element);
		if (link == null) {
			writer.addText(locationText);
		} else {
			if (location instanceof TextRegionLocation) {
				link += "#"
						+ ListingWriter
								.getLineId(((TextRegionLocation) location)
										.getRawStartLine());
			}
			writer.addClosedTextElement(A, locationText, HREF, link, CLASS,
					TableLayouter.LINK_CLASS);
		}
	}
}