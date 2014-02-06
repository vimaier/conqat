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
package org.conqat.engine.html_presentation.base;

import static org.conqat.engine.html_presentation.CSSMananger.DEFAULT_FONT;
import static org.conqat.engine.html_presentation.CSSMananger.SAN_SERIF_FONT;
import static org.conqat.lib.commons.color.ECCSMColor.LIGHT_BLUE;
import static org.conqat.lib.commons.html.ECSSProperty.BACKGROUND_COLOR;
import static org.conqat.lib.commons.html.ECSSProperty.COLOR;
import static org.conqat.lib.commons.html.ECSSProperty.FONT_SIZE;
import static org.conqat.lib.commons.html.ECSSProperty.FONT_WEIGHT;
import static org.conqat.lib.commons.html.ECSSProperty.MARGIN_LEFT;
import static org.conqat.lib.commons.html.ECSSProperty.PADDING_LEFT;
import static org.conqat.lib.commons.html.ECSSProperty.TEXT_ALIGN;
import static org.conqat.lib.commons.html.ECSSProperty.WHITE_SPACE;
import static org.conqat.lib.commons.html.EHTMLAttribute.ALIGN;
import static org.conqat.lib.commons.html.EHTMLAttribute.BORDER;
import static org.conqat.lib.commons.html.EHTMLAttribute.CELLPADDING;
import static org.conqat.lib.commons.html.EHTMLAttribute.CELLSPACING;
import static org.conqat.lib.commons.html.EHTMLAttribute.CLASS;
import static org.conqat.lib.commons.html.EHTMLAttribute.COLSPAN;
import static org.conqat.lib.commons.html.EHTMLAttribute.HREF;
import static org.conqat.lib.commons.html.EHTMLAttribute.ROWSPAN;
import static org.conqat.lib.commons.html.EHTMLAttribute.SRC;
import static org.conqat.lib.commons.html.EHTMLAttribute.STYLE;
import static org.conqat.lib.commons.html.EHTMLAttribute.TITLE;
import static org.conqat.lib.commons.html.EHTMLAttribute.VALIGN;
import static org.conqat.lib.commons.html.EHTMLAttribute.WIDTH;
import static org.conqat.lib.commons.html.EHTMLElement.A;
import static org.conqat.lib.commons.html.EHTMLElement.BODY;
import static org.conqat.lib.commons.html.EHTMLElement.DIV;
import static org.conqat.lib.commons.html.EHTMLElement.IMG;
import static org.conqat.lib.commons.html.EHTMLElement.TABLE;
import static org.conqat.lib.commons.html.EHTMLElement.TD;
import static org.conqat.lib.commons.html.EHTMLElement.TH;
import static org.conqat.lib.commons.html.EHTMLElement.TR;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.conqat.engine.commons.format.DeltaSummary;
import org.conqat.engine.commons.format.Summary;
import org.conqat.engine.html_presentation.IPageDescriptor;
import org.conqat.engine.html_presentation.util.ResourcesManager;
import org.conqat.engine.html_presentation.util.WriterBase;
import org.conqat.lib.commons.assessment.Assessment;
import org.conqat.lib.commons.collections.ListMap;
import org.conqat.lib.commons.html.CSSDeclarationBlock;
import org.conqat.lib.commons.html.ECSSProperty;
import org.conqat.lib.commons.string.StringUtils;

/**
 * This class generates the overview page ({@value #PAGE_NAME}).
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46960 $
 * @ConQAT.Rating GREEN Hash: 2EDD329A784A5647C08544AAF39FB23C
 */
public class OverviewPageWriter extends WriterBase {

	/** The style used for the cells containing page names. */
	private static final CSSDeclarationBlock PAGE_CELL_STYLE = new CSSDeclarationBlock(
			WHITE_SPACE, "nowrap", ECSSProperty.HEIGHT, "30px", PADDING_LEFT,
			"5px");

	/** Name of the page used for the group summary on the overview page. */
	public static final String SUMMARY_PAGE_NAME = "::group-summary";

	/** CSS for the tables that display each group. */
	private static final CSSDeclarationBlock GROUP_TABLE = new CSSDeclarationBlock(
			DEFAULT_FONT).setMargin("13px")
			.setBorder("2px", "solid", LIGHT_BLUE.getHTMLColorCode())
			.setPadding("0px");

	/** CSS for the header of the group tables. */
	public static final CSSDeclarationBlock GROUP_TABLE_HEADER = new CSSDeclarationBlock(
			SAN_SERIF_FONT, FONT_SIZE, "16px", FONT_WEIGHT, "bold",
			BACKGROUND_COLOR, LIGHT_BLUE.getHTMLColorCode(), TEXT_ALIGN,
			"left", WHITE_SPACE, "nowrap");

	/** Name of the footer page: {@value} */
	public static final String PAGE_NAME = "overview.html";

	/** Prefix for result pages. */
	private static final String ASSESSMENT_IMAGE_PREFIX = "assessment";

	/** Logger. */
	private static final Logger LOGGER = Logger
			.getLogger(OverviewPageWriter.class);

	/** Used for visualization of aggregated assessments of the pages. */
	private final AssessmentDrawer assessmentVisualizer = new AssessmentDrawer(
			60, 14);

	/** Used for visualization of delta summaries. */
	private final DeltaSummaryDrawer deltaDrawer = new DeltaSummaryDrawer(24);

	/** Image directory. */
	private final File imagesOutputDirectory;

	/** List of of pages. */
	private final ListMap<String, IPageDescriptor> groups2Pages;

	/** Number of columns on the overview page. */
	private final int columnCount;

	/** Used for column width. */
	private final NumberFormat percentageFormat = NumberFormat
			.getPercentInstance();

	/** Names of groups to be included in the overview page. */
	private final ArrayList<String> groupNames;

	/** An additional page to be displayed below the overview (may be null). */
	private final IPageDescriptor additionalOverviewPage;

	/**
	 * Create new overview page writer.
	 * 
	 * @param outputDirectory
	 *            output directory
	 * @param groups2Pages
	 *            pages to display
	 * @param excludedGroupNames
	 *            name of groups to exclude from overview page
	 * @param columnCount
	 *            number of columns on overview page.
	 */
	public OverviewPageWriter(File outputDirectory,
			ListMap<String, IPageDescriptor> groups2Pages,
			Collection<String> excludedGroupNames, int columnCount,
			IPageDescriptor additionalOverviewPage) {
		super(new File(outputDirectory, PAGE_NAME));
		imagesOutputDirectory = new File(outputDirectory,
				ResourcesManager.IMAGES_DIRECTORY_NAME);
		this.groups2Pages = groups2Pages;

		groupNames = new ArrayList<String>(groups2Pages.getKeys());
		// remove group names that should not be displayed in overview page
		groupNames.removeAll(excludedGroupNames);

		if (groupNames.size() >= columnCount) {
			this.columnCount = columnCount;
		} else {
			this.columnCount = groupNames.size();
		}

		this.additionalOverviewPage = additionalOverviewPage;
	}

	/** {@inheritDoc} */
	@Override
	protected void addBody() {
		writer.openElement(BODY);
		writer.openElement(TABLE, WIDTH, "100%", BORDER, "0", CELLSPACING, "2");

		String[][] columns = getColumnLayout();

		writer.openElement(TR);

		for (String[] column : columns) {
			writer.openElement(TD, VALIGN, "top", WIDTH,
					percentageFormat.format(1.0 / columnCount));
			for (String groupName : column) {
				// matrix may have empty cells in the last row
				if (!StringUtils.isEmpty(groupName)) {
					addGroup(groupName, groups2Pages.getCollection(groupName));
				}
			}
			writer.closeElement(TD);
		}
		writer.closeElement(TR);

		if (additionalOverviewPage != null) {
			writer.openElement(TR);
			writer.openElement(TD, VALIGN, "top", COLSPAN, 2);

			writer.openElement(DIV, STYLE,
					new CSSDeclarationBlock().setMargin("13px"));
			writer.addRawString(additionalOverviewPage.getContent());
			writer.closeElement(DIV);

			writer.closeElement(TD);
			writer.closeElement(TR);
		}

		writer.closeElement(TABLE);
		writer.closeElement(BODY);
	}

	/**
	 * Returns a columns x rows-matrix of the group names. Update this method to
	 * implement more sophisticated layout strategies.
	 */
	private String[][] getColumnLayout() {
		int rowCount = (int) Math
				.ceil((double) groupNames.size() / columnCount);

		String[][] columns = new String[columnCount][rowCount];

		for (int i = 0; i < groupNames.size(); i++) {
			int columnIndex = i % columnCount;
			int rowIndex = i / columnCount;
			columns[columnIndex][rowIndex] = groupNames.get(i);
		}

		return columns;
	}

	/** Add a group. */
	private void addGroup(String groupName, List<IPageDescriptor> pages) {
		writer.openElement(TABLE, CLASS, GROUP_TABLE, CELLSPACING, "0",
				CELLPADDING, "2", WIDTH, "90%", BORDER, "0");

		IPageDescriptor summaryDescriptor = findSummaryDescriptor(pages);

		int columns = 4;
		if (summaryDescriptor != null) {
			columns = 5;
		}

		writer.openElement(TR);
		writer.openElement(TH, CLASS, GROUP_TABLE_HEADER, WIDTH, "100%",
				COLSPAN, columns);
		writer.addText(groupName);
		writer.closeElement(TH);
		writer.closeElement(TR);

		boolean first = true;
		for (IPageDescriptor page : pages) {
			if (SUMMARY_PAGE_NAME.equals(page.getName())) {
				continue;
			}

			writer.setSuppressLineBreaks(true);
			writer.openElement(TR);
			addPage(page);

			if (first && summaryDescriptor != null) {
				writer.openElement(TD, VALIGN, "top", ALIGN, "right", ROWSPAN,
						pages.size());
				writer.addRawString(summaryDescriptor.getContent());
				writer.closeElement(TD);
			}
			first = false;

			writer.closeElement(TR);
			writer.setSuppressLineBreaks(false);
		}
		if (summaryDescriptor != null) {
			writer.openElement(TR);
			writer.addClosedTextElement(TD, " ");
			writer.closeElement(TR);
		}

		writer.closeElement(TABLE);
	}

	/** Returns the summary descriptor from a list of pages or null. */
	private IPageDescriptor findSummaryDescriptor(List<IPageDescriptor> pages) {
		for (IPageDescriptor page : pages) {
			if (SUMMARY_PAGE_NAME.equals(page.getName())) {
				return page;
			}
		}
		return null;
	}

	/** Add a page. */
	private void addPage(IPageDescriptor page) {
		// First column: The summary as an image.
		writer.openElement(TD, VALIGN, "center", WIDTH, "1%", STYLE,
				PAGE_CELL_STYLE);
		try {
			appendSummary(page);
		} catch (IOException e) {
			LOGGER.warn("Couldn't create assessment image: " + e.getMessage());
		}
		writer.closeElement(TD);

		// Second column: The name of the page.
		writer.openElement(TD, VALIGN, "center", WIDTH, "20%", STYLE,
				PAGE_CELL_STYLE);
		writer.addClosedTextElement(A, page.getName(), HREF,
				page.getFilename(), STYLE, new CSSDeclarationBlock(MARGIN_LEFT,
						"10px"));
		writer.closeElement(TD);

		appendSummaryColumns(page);
	}

	/** Appends the summary columns. */
	private void appendSummaryColumns(IPageDescriptor page) {
		// Third column: Current value in case of a DeltaSummary or TextSumamry
		DeltaSummary summary = null;
		if (page.getSummary() instanceof DeltaSummary) {
			summary = (DeltaSummary) page.getSummary();
		}

		writer.openElement(TD, VALIGN, "center", WIDTH, "3%", ALIGN, "right",
				STYLE, PAGE_CELL_STYLE);
		if (summary != null) {
			writer.addText(summary.format());
		} else if (page.getSummary() != null
				&& !(page.getSummary() instanceof Assessment)) {
			writer.addText(((Summary) page.getSummary()).format());
		}
		writer.closeElement(TD);

		// Fourth column: Delta in case of a DeltaSummary
		writer.openElement(TD, VALIGN, "center", STYLE,
				new CSSDeclarationBlock(PAGE_CELL_STYLE, COLOR, "#666666"));
		if (summary != null && summary.getDelta() != 0.0) {
			writer.addText(" (" + summary.formatDelta() + ")");
		}
		writer.closeElement(TD);
	}

	/**
	 * Append page summary. If the summary is an {@link Assessment}, an
	 * assessment image is created. If result doesn't have a summary an empty
	 * assessment is created. If the page is external, no summary is created.
	 * For all other object, a textual representation is appended.
	 */
	private void appendSummary(IPageDescriptor page) throws IOException {
		if (page.isExternal()) {
			return;
		}

		Object summary = page.getSummary();
		if (summary == null) {
			return;
		}

		if (!(summary instanceof DeltaSummary)
				&& !(summary instanceof Assessment)) {
			return;
		}

		String filename = ASSESSMENT_IMAGE_PREFIX + "_" + page.getFilename()
				+ ".png";
		String imageHRef = ResourcesManager.IMAGES_DIRECTORY_NAME + "/"
				+ filename;
		File file = new File(imagesOutputDirectory, filename);

		if (summary instanceof DeltaSummary) {
			deltaDrawer.generatePNGImage((DeltaSummary) summary, file);
		} else if (summary instanceof Assessment) {
			assessmentVisualizer.generatePNGImage((Assessment) summary, file);
		}

		writer.addClosedElement(IMG, SRC, imageHRef, TITLE, summary.toString());
	}

	/** {@inheritDoc} */
	@Override
	protected String getTitle() {
		return "Overview";
	}

}