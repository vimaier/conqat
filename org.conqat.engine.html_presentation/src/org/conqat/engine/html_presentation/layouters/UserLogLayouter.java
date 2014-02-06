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

import static org.conqat.engine.html_presentation.CSSMananger.TABLE_CELL;
import static org.conqat.engine.html_presentation.CSSMananger.TABLE_HEADER_CELL;
import static org.conqat.lib.commons.html.ECSSProperty.BACKGROUND_COLOR;
import static org.conqat.lib.commons.html.EHTMLAttribute.BORDER;
import static org.conqat.lib.commons.html.EHTMLAttribute.CELLPADDING;
import static org.conqat.lib.commons.html.EHTMLAttribute.CELLSPACING;
import static org.conqat.lib.commons.html.EHTMLAttribute.CLASS;
import static org.conqat.lib.commons.html.EHTMLAttribute.STYLE;
import static org.conqat.lib.commons.html.EHTMLElement.BR;
import static org.conqat.lib.commons.html.EHTMLElement.TABLE;
import static org.conqat.lib.commons.html.EHTMLElement.TD;
import static org.conqat.lib.commons.html.EHTMLElement.TH;
import static org.conqat.lib.commons.html.EHTMLElement.TR;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.conqat.engine.commons.CommonUtils;
import org.conqat.engine.commons.logging.ListStructuredLogMessage;
import org.conqat.engine.commons.logging.StructuredLogMessage;
import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.core.IShutdownHook;
import org.conqat.engine.core.logging.ConQATLoggingEvent;
import org.conqat.engine.html_presentation.CSSMananger;
import org.conqat.engine.html_presentation.EHtmlPresentationIcons;
import org.conqat.engine.html_presentation.javascript.base.BaseJSModule;
import org.conqat.engine.html_presentation.util.LayouterBase;
import org.conqat.lib.commons.collections.UnmodifiableList;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.html.CSSDeclarationBlock;
import org.conqat.lib.commons.html.EHTMLAttribute;
import org.conqat.lib.commons.html.EHTMLElement;
import org.conqat.lib.commons.html.HTMLWriter;
import org.conqat.lib.commons.string.StringUtils;

/**
 * {@ConQAT.Doc}
 * 
 * Also see {@link StructuredLogMessage}.
 * 
 * One problem with this processor is that we have the full log only after all
 * other processors are complete. The solution is to return HTML code which
 * loads another page (called a delegate page). This delegate page is then
 * written later in the shutdown hook
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: BA4DC0A5AE17284ACE9980BFC1879F05
 */
@AConQATProcessor(description = "A layouter for displaying the structured part of the log messages.")
public class UserLogLayouter extends LayouterBase implements IShutdownHook {

	/** Number of lines displayed in preview. */
	private static final int MAX_DETAILS_PREVIEW = 5;

	/** The output directory. */
	private File outputDirectory;

	/**
	 * Patterns used for selecting processors and their captions displayed to
	 * the user. We use a {@link LinkedHashMap} to be able to iterate the
	 * patterns in the order they were added. This way, if more than one pattern
	 * matches, we can make the first one prevail deterministically.
	 */
	private final Map<Pattern, String> patternCaptions = new LinkedHashMap<Pattern, String>();

	/** The tags selected. */
	private final Set<String> tags = new HashSet<String>();

	/** Counter used to generate fresh ids */
	private int idCounter = 0;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "sort-by-message", attribute = "value", optional = true, description = ""
			+ "If this is true, the events in the output table will be sorted by message. Default is false.")
	public boolean sortByMessage = false;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "duplicates", attribute = "discard", optional = true, description = ""
			+ "If this is true, the from multiple events with the same message, only the first will be kept. "
			+ "Note that only the message is compared, not any other attached data. Default is false.")
	public boolean discardDuplicateMessages = false;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "display-processor-name", attribute = "value", optional = true, description = ""
			+ "Flag that determines whether processor name is included in user log. Default is false. Useful for debugging.")
	public boolean displayProcessorName = false;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "output", minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "Output directory; must be the same as specified for HtmlPresentation.")
	public void setOutputDirectory(
			@AConQATAttribute(name = "dir", description = "Name of the output directory") String outputDirectoryName) {
		outputDirectory = new File(outputDirectoryName);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "select", minOccurrences = 0, maxOccurrences = -1, description = "Selects the processors being considered using a regular expression on the processor instance name.")
	public void setSelectionPattern(
			@AConQATAttribute(name = "pattern", description = "A regular expression that is used to match against the processor instance name.") String patternString,
			@AConQATAttribute(name = "caption", description = "Caption displayed for the matching messages in the log.") String caption)
			throws ConQATException {
		Pattern pattern = CommonUtils.compilePattern(patternString);
		patternCaptions.put(pattern, caption);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "tag", description = "If this parameter is set, only log messages that are tagged with one of the selected tags are considered")
	public void addTag(
			@AConQATAttribute(name = "value", description = "The tag.") String tag) {
		tags.add(tag);
	}

	/** {@inheritDoc} */
	@Override
	protected String getIconName() {
		return EHtmlPresentationIcons.TABLE.getIconName();
	}

	/** {@inheritDoc} */
	@Override
	protected void layoutPage() {
		String id = "content-"
				+ getProcessorInfo().getName().replaceAll("\\.", "-");
		writer.insertEmptyElement(EHTMLElement.DIV, EHTMLAttribute.ID, id);
		writer.insertJavaScript(BaseJSModule.loadContent(id, getFilename()
				+ ".content"));
		getProcessorInfo().registerShutdownHook(this, false);
	}

	/** {@inheritDoc} */
	@Override
	public void performShutdown() throws ConQATException {
		File contentFile = new File(outputDirectory, getFilename() + ".content");
		try {
			HTMLWriter writer = new HTMLWriter(contentFile,
					CSSMananger.getInstance());
			writeDelegatePage(writer);
			writer.close();
		} catch (IOException e) {
			throw new ConQATException("Could not create delegate page: "
					+ e.getMessage(), e);
		}
	}

	/** Writes the delegate page that in included in the original one. */
	private void writeDelegatePage(HTMLWriter writer) throws IOException {
		writer.openElement(TABLE, BORDER, "0", CELLSPACING, "2", CELLPADDING,
				"0", STYLE, new CSSDeclarationBlock(BACKGROUND_COLOR, "white"));

		writeHeader(writer);

		List<ConQATLoggingEvent> orderedEvents = orderByPatterns(filterEvents());

		if (sortByMessage) {
			sortEventsByMessage(orderedEvents);
		}

		if (discardDuplicateMessages) {
			orderedEvents = filterDuplicateMessages(orderedEvents);
		}

		writeLogEvents(writer, orderedEvents);

		writer.closeElement(TABLE);
	}

	/**
	 * Returns a list of events containing for multiple events with the same
	 * message only the first one.
	 */
	private List<ConQATLoggingEvent> filterDuplicateMessages(
			List<ConQATLoggingEvent> events) {
		Set<String> seenMessages = new HashSet<String>();
		List<ConQATLoggingEvent> result = new ArrayList<ConQATLoggingEvent>();
		for (ConQATLoggingEvent event : events) {
			if (seenMessages.add(getUserSummaryMessage(event))) {
				result.add(event);
			}
		}
		return result;
	}

	/** Sorts the given list of events by message. */
	private void sortEventsByMessage(List<ConQATLoggingEvent> orderedEvents) {
		Collections.sort(orderedEvents, new Comparator<ConQATLoggingEvent>() {
			@Override
			public int compare(ConQATLoggingEvent event1,
					ConQATLoggingEvent event2) {
				return getUserSummaryMessage(event1).compareTo(
						getUserSummaryMessage(event2));
			}
		});
	}

	/** Extracts the user summary message. */
	private static String getUserSummaryMessage(ConQATLoggingEvent event) {
		return ((StructuredLogMessage) event.getMessage())
				.getUserSummaryMessage();
	}

	/**
	 * Returns a ordered list, where we sort events matched by patterns in order
	 * of patterns-
	 */
	private List<ConQATLoggingEvent> orderByPatterns(
			List<ConQATLoggingEvent> events) {
		List<ConQATLoggingEvent> orderedEvents = new ArrayList<ConQATLoggingEvent>();

		for (Pattern pattern : patternCaptions.keySet()) {
			List<ConQATLoggingEvent> eventsForPattern = selectMatchingEvents(
					events, pattern);
			orderedEvents.addAll(eventsForPattern);
			events.removeAll(eventsForPattern);
		}

		// add remaining events
		orderedEvents.addAll(events);

		return orderedEvents;
	}

	/** Determine events */
	private List<ConQATLoggingEvent> selectMatchingEvents(
			List<ConQATLoggingEvent> events, Pattern pattern) {
		List<ConQATLoggingEvent> matchedEvents = new ArrayList<ConQATLoggingEvent>();
		for (ConQATLoggingEvent event : events) {
			if (pattern.matcher(event.getProcessorName()).matches()) {
				matchedEvents.add(event);
			}
		}
		// sort to get stable outputs
		Collections.sort(matchedEvents,
				ConQATLoggingEvent.getByProcessorComparator());
		return matchedEvents;
	}

	/** Write a list of log events to log */
	private void writeLogEvents(HTMLWriter writer,
			List<ConQATLoggingEvent> logEvents) throws IOException {
		for (ConQATLoggingEvent event : logEvents) {
			writeEvent(writer, event);
		}
	}

	/** Writes the table header. */
	private void writeHeader(HTMLWriter writer) {
		writer.openElement(TR);
		for (String title : Arrays.asList("Caption", "Message", "Details")) {
			writer.addClosedTextElement(TH, title, CLASS, TABLE_HEADER_CELL);
		}
		if (displayProcessorName) {
			writer.addClosedTextElement(TH, "Processor Name", CLASS,
					TABLE_HEADER_CELL);
		}
		writer.closeElement(TR);
	}

	/** Writes the table body. */
	private void writeEvent(HTMLWriter writer, ConQATLoggingEvent event)
			throws IOException {
		writer.openElement(TR);

		writer.openElement(TD, CLASS, TABLE_CELL);
		String caption = captionFor(event);
		writer.addText(caption);
		writer.closeElement(TD);

		StructuredLogMessage message = (StructuredLogMessage) event
				.getMessage();

		writer.openElement(TD, CLASS, TABLE_CELL);
		writer.addText(message.getUserSummaryMessage());
		writer.closeElement(TD);

		writer.openElement(TD, CLASS, TABLE_CELL);
		dumpDetails(writer, message);
		writer.closeElement(TD);

		if (displayProcessorName) {
			writer.openElement(TD, CLASS, TABLE_CELL);
			writer.addText(event.getProcessorName());
			writer.closeElement(TD);
		}

		writer.closeElement(TR);
	}

	/** Determines the caption for the event */
	private String captionFor(ConQATLoggingEvent event) {
		String processorName = event.getProcessorName();
		for (Pattern pattern : patternCaptions.keySet()) {
			if (pattern.matcher(processorName).matches()) {
				return patternCaptions.get(pattern);
			}
		}

		// return processor name, if no caption set
		return StringUtils.getLastPart(processorName, '.');
	}

	/** Dumps the contents of the details column. */
	private void dumpDetails(HTMLWriter writer, StructuredLogMessage message)
			throws IOException {
		if (message instanceof ListStructuredLogMessage) {
			dumpListDetails(writer, (ListStructuredLogMessage) message);
		} else {
			writer.addNonBreakingSpace();
		}
	}

	/** Dumps details for a list message. */
	private void dumpListDetails(HTMLWriter writer,
			ListStructuredLogMessage message) throws IOException {

		UnmodifiableList<String> list = message.getDetails();

		boolean needsBR = false;
		int bound = Math.min(list.size(), MAX_DETAILS_PREVIEW);
		for (int i = 0; i < bound; ++i) {
			if (needsBR) {
				writer.addClosedElement(BR);
			}
			writer.addText(list.get(i));
			needsBR = true;
		}

		// more details
		if (list.size() > bound) {
			writer.addClosedElement(BR);
			String listName = getFilename() + ".details" + idCounter++ + ".txt";
			writer.addClosedTextElement(EHTMLElement.A, "more...",
					EHTMLAttribute.HREF, listName);
			FileSystemUtils.writeFile(new File(outputDirectory, listName),
					StringUtils.concat(list, StringUtils.CR));
		}
	}

	/** Filters the relevant log messages. */
	private List<ConQATLoggingEvent> filterEvents() {
		List<ConQATLoggingEvent> events = new ArrayList<ConQATLoggingEvent>();
		PatternList patterns = new PatternList(patternCaptions.keySet());

		for (ConQATLoggingEvent event : getProcessorInfo().getLogManager()
				.getLoggingEvents()) {
			if (includeLogEvent(patterns, event)) {
				StructuredLogMessage message = (StructuredLogMessage) event
						.getMessage();
				if (containsTags(message)) {
					events.add(event);
				}
			}
		}

		Collections.sort(events, ConQATLoggingEvent.getByTimeComparator());
		return events;
	}

	/** Determines whether to include the log event */
	private boolean includeLogEvent(PatternList allPatterns,
			ConQATLoggingEvent event) {
		return event.getMessage() instanceof StructuredLogMessage
				&& allPatterns.emptyOrMatchesAny(event.getProcessorName());
	}

	/**
	 * Checks whether the message is tagged with one of the tags from
	 * {@link #tags} (or no tags are used).
	 */
	private boolean containsTags(StructuredLogMessage message) {
		if (tags.isEmpty()) {
			return true;
		}

		for (String tag : message.getTags()) {
			if (tags.contains(tag)) {
				return true;
			}
		}

		return false;
	}

}