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
package org.conqat.engine.html_presentation.listing;

import static org.conqat.lib.commons.html.ECSSProperty.BACKGROUND_COLOR;
import static org.conqat.lib.commons.html.ECSSProperty.DISPLAY;
import static org.conqat.lib.commons.html.ECSSProperty.FONT_SIZE;
import static org.conqat.lib.commons.html.ECSSProperty.OPACITY;
import static org.conqat.lib.commons.html.ECSSProperty.VERTICAL_ALIGN;
import static org.conqat.lib.commons.html.ECSSProperty.WHITE_SPACE;
import static org.conqat.lib.commons.html.EHTMLAttribute.CELLPADDING;
import static org.conqat.lib.commons.html.EHTMLAttribute.CELLSPACING;
import static org.conqat.lib.commons.html.EHTMLAttribute.CLASS;
import static org.conqat.lib.commons.html.EHTMLAttribute.COLSPAN;
import static org.conqat.lib.commons.html.EHTMLAttribute.STYLE;
import static org.conqat.lib.commons.html.EHTMLElement.BR;
import static org.conqat.lib.commons.html.EHTMLElement.TABLE;
import static org.conqat.lib.commons.html.EHTMLElement.TD;
import static org.conqat.lib.commons.html.EHTMLElement.TH;
import static org.conqat.lib.commons.html.EHTMLElement.TR;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.commons.findings.location.ElementLocation;
import org.conqat.engine.commons.findings.location.TextRegionLocation;
import org.conqat.engine.commons.node.DisplayList;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.html_presentation.CSSMananger;
import org.conqat.engine.html_presentation.base.ColorConstants;
import org.conqat.engine.html_presentation.formatters.LinkHTMLFormatter;
import org.conqat.engine.html_presentation.javascript.base.BaseJSModule;
import org.conqat.engine.html_presentation.layouters.TableLayouter;
import org.conqat.engine.html_presentation.util.HTMLLink;
import org.conqat.engine.html_presentation.util.NodeAwareFindingFormatterBase;
import org.conqat.engine.html_presentation.util.PresentationUtils;
import org.conqat.engine.html_presentation.util.PresentationUtils.IContextSensitiveFormatter;
import org.conqat.engine.html_presentation.util.WriterBase;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.engine.resource.text.TextElementProcessorBase;
import org.conqat.engine.resource.util.ConQATFileUtils;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.lib.commons.algo.Diff.Delta;
import org.conqat.lib.commons.filesystem.CanonicalFile;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.html.CSSDeclarationBlock;
import org.conqat.lib.commons.html.ECSSProperty;
import org.conqat.lib.commons.html.EHTMLAttribute;
import org.conqat.lib.commons.html.HTMLWriter;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.scanner.ELanguage;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: goede $
 * @version $Rev: 44698 $
 * @ConQAT.Rating GREEN Hash: C950573B902AE5C9B8043B203BCBFAE1
 */
@AConQATProcessor(description = "Writes out listings for the given elements.")
public class ListingWriter extends TextElementProcessorBase {

	/** The class used for the tooltips. */
	private static final CSSDeclarationBlock TOOLTIP_CLASS = new CSSDeclarationBlock(
			CSSMananger.DEFAULT_FONT, FONT_SIZE, "12px", BACKGROUND_COLOR,
			"#CCCCCC", WHITE_SPACE, "pre-line", OPACITY, "0.8").setBorder(
			"1px", "solid", "#666666");

	/** The class used for the spacer. */
	static final CSSDeclarationBlock SPACER_CLASS = new CSSDeclarationBlock(
			DISPLAY, "inline-block", VERTICAL_ALIGN, "bottom");

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "file-provider", attribute = "ref", description = "The file provider used for mapping elements to storage files.")
	public ListingFileProvider fileProvider;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "tab", attribute = "width", optional = true, description = "The length of a tab in spaces.")
	public int tabWidth = 4;

	/** The output directory. */
	private CanonicalFile outputDirectory;

	/** The marker generators used. */
	private final List<IListingMarkerGenerator> markerGenerators = new ArrayList<IListingMarkerGenerator>();

	/** The display list. */
	private DisplayList displayList;

	/** Formatter used to output HTML. */
	private final ListingHtmlFormatter formatter = new ListingHtmlFormatter();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "output", minOccurrences = 1, maxOccurrences = 1, description = "Output directory.")
	public void setOutputDirectory(
			@AConQATAttribute(name = "dir", description = "Name of the output directory.") String dir)
			throws ConQATException {
		outputDirectory = ConQATFileUtils.createCanonicalFile(dir);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "marker", description = "Adds a marker generator.")
	public void addMarkerGenerator(
			@AConQATAttribute(name = "generator", description = "Reference to the generator.") IListingMarkerGenerator generator) {
		markerGenerators.add(generator);
	}

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "highlight-delta", attribute = "key", optional = true, description = "Set to key that contains delta to comparee version to enable diff highlighting.")
	public String deltaKey = null;

	/** {@inheritDoc} */
	@Override
	protected void setUp(ITextResource root) {
		displayList = NodeUtils.getDisplayList(root);
		formatter.setTabReplacement(StringUtils.fillString(tabWidth, ' '));
	}

	/** {@inheritDoc} */
	@Override
	protected void processElement(ITextElement element) throws ConQATException {
		try {
			CanonicalFile outputFile = fileProvider.getListingFile(element,
					outputDirectory);
			FileSystemUtils.ensureParentDirectoryExists(outputFile);
			ListingPageWriter writer = new ListingPageWriter(outputFile,
					element);
			writer.write();
		} catch (IOException e) {
			// as these are I/O problems that are likely to be systematic, we
			// fail hard instead of just logging.
			throw new ConQATException("Could not output listing for element "
					+ element.getUniformPath() + ": " + e.getMessage(), e);
		}
	}

	/** Returns the ID used for the given line. */
	public static String getLineId(int line) {
		return "line" + line;
	}

	/** The actual writing code. */
	private final class ListingPageWriter extends WriterBase {

		/** The element. */
		private final ITextElement element;

		/** Line-Diff information provider. */
		private LineDiffColorProvider lineDiffProvider;

		/** Constructor. */
		private ListingPageWriter(File file, ITextElement element) {
			super(file, "../");
			this.element = element;

			if (!StringUtils.isEmpty(deltaKey)) {
				Delta<?> delta = NodeUtils.getValue(element, deltaKey,
						Delta.class, null);
				if (delta != null) {
					lineDiffProvider = new LineDiffColorProvider(delta);
				}
			}
		}

		/** {@inheritDoc} */
		@Override
		protected String getTitle() {
			return element.getUniformPath();
		}

		/** {@inheritDoc} */
		@Override
		protected void addBody() throws ConQATException {
			insertOverview();
			writer.addClosedElement(BR);

			List<ListingMarkerDescriptor> markers = determineMarkers();
			ListingMarkerDescriptor.calculateSlots(markers);
			int numLines = insertContent();

			writer.insertJavaScript(BaseJSModule.installListingMarkers(
					CSSMananger.getInstance().getCSSClassName(TOOLTIP_CLASS),
					numLines, markers));
		}

		/** Inserts the overview (key/value table). */
		private void insertOverview() {
			writer.openElement(TABLE, CELLSPACING, "0", CELLPADDING, "2",
					STYLE, TableLayouter.TABLE_BORDER_STYLE);
			writer.openElement(TR, CLASS, TableLayouter.ROW_CLASS);
			writer.addClosedTextElement(TH, getTitle(), CLASS,
					TableLayouter.HEADER_CLASS, COLSPAN, "2");
			writer.closeElement(TR);

			IContextSensitiveFormatter<?> findingFormatter = createFindingFormatter();
			boolean oddRow = true;
			for (String key : displayList) {
				if (element.getValue(key) == null) {
					continue;
				}
				insertOverviewRow(key, oddRow, findingFormatter);
				oddRow = !oddRow;
			}

			writer.closeElement(TABLE);
		}

		/** Creates a finding formatter for the {@link #element}. */
		private IContextSensitiveFormatter<?> createFindingFormatter() {
			return new NodeAwareFindingFormatterBase(element) {
				@Override
				protected String determineUrl(ElementLocation location) {
					if (location instanceof TextRegionLocation
							&& location.getUniformPath().equals(
									element.getUniformPath())) {
						return "#"
								+ getLineId(((TextRegionLocation) location)
										.getRawStartLine());
					}
					return null;
				}
			};
		}

		/** Inserts a single row into the overview table. */
		private void insertOverviewRow(String key, boolean oddRow,
				IContextSensitiveFormatter<?> findingFormatter) {
			writer.openElement(TR, CLASS, TableLayouter.ROW_CLASS);
			if (oddRow) {
				writer.addAttribute(EHTMLAttribute.STYLE,
						new CSSDeclarationBlock(ECSSProperty.BACKGROUND_COLOR,
								ColorConstants.TABLE_CELL_ODD));
			}

			writer.addClosedTextElement(TD, key, CLASS,
					TableLayouter.CELL_CLASS);
			writer.openElement(TD, CLASS, TableLayouter.CELL_CLASS);
			PresentationUtils.appendValue(element.getValue(key),
					displayList.getFormatter(key), writer, getLogger(),
					findingFormatter, new RelativeToRootLinkFormatter());
			writer.closeElement(TD);
			writer.closeElement(TR);
		}

		/** Determines the markers to be shown. */
		private List<ListingMarkerDescriptor> determineMarkers()
				throws ConQATException {
			List<ListingMarkerDescriptor> result = new ArrayList<ListingMarkerDescriptor>();
			for (IListingMarkerGenerator generator : markerGenerators) {
				List<ListingMarkerDescriptor> markers = generator
						.generateMarkers(element);
				if (markers != null) {
					result.addAll(markers);
				}
			}
			return result;
		}

		/** Inserts the file content and returns number of lines written. */
		private int insertContent() throws ConQATException {
			String content = element.getUnfilteredTextContent();

			ELanguage language = ELanguage.TEXT;
			if (element instanceof ITokenElement) {
				language = ((ITokenElement) element).getLanguage();
			}

			formatter.formatSourceCode(content, language,
					element.getFilteredRegions(), lineDiffProvider, writer);

			return StringUtils.splitLinesAsList(content).size();
		}

		/**
		 * Prepends a relative path to the root to the link's href to make the
		 * link work from pages that do not reside in the output directory
		 * itself.
		 */
		public class RelativeToRootLinkFormatter extends LinkHTMLFormatter
				implements IContextSensitiveFormatter<HTMLLink> {

			/** {@inheritDoc} */
			@Override
			public void formatObject(HTMLLink link, HTMLWriter writer) {
				link.setRelativePathToRoot(relativePathToRoot);
				super.formatObject(link, writer);
			}

			/** {@inheritDoc} */
			@Override
			public boolean isApplicable(Object value) {
				return value instanceof HTMLLink;
			}
		}
	}
}
