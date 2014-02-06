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
package org.conqat.engine.html_presentation.util;

import static org.conqat.lib.commons.html.EHTMLElement.BR;

import java.text.NumberFormat;
import java.util.Iterator;

import org.conqat.engine.commons.format.IValueFormatter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.IConQATLogger;
import org.conqat.engine.html_presentation.BundleContext;
import org.conqat.engine.html_presentation.HTMLPresentationManager;
import org.conqat.engine.html_presentation.formatters.IHTMLFormatter;
import org.conqat.lib.commons.html.HTMLWriter;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Collection of utility methods used in the presentation.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 92D8B1557B42BCDB3C7C852B5C45F280
 */
public class PresentationUtils {

	/** Number formatter instance. */
	public static final NumberFormat NUMBER_FORMATTER = NumberFormat
			.getInstance();

	/**
	 * Appends a string to an HTML writer verbatim but replaces line breaks by
	 * &lt;BR&gt;s.
	 */
	public static void appendPreservingLineBreaks(HTMLWriter writer,
			String string) {
		Iterator<String> it = StringUtils.splitLinesAsList(string).iterator();

		while (it.hasNext()) {
			writer.addText(it.next());
			if (it.hasNext()) {
				writer.addClosedElement(BR);
			}
		}
	}

	/**
	 * Append HTML description of value to a writer. The formatting is performed
	 * based on {@link IHTMLFormatter}s registered in the
	 * {@link HTMLPresentationManager}. Problems during formatting are logged.
	 * 
	 * @param valueFormatter
	 *            the key-specific formatter (typically from the display list)
	 *            to be used. May be null.
	 * @param localFormatters
	 *            optional formatters for context/node specific formatting. The
	 *            first applicable formatter will be used. If none is
	 *            applicable, a formatter will be selected from the global
	 *            formatter pool.
	 */
	public static void appendValue(Object value,
			IValueFormatter valueFormatter, HTMLWriter writer,
			IConQATLogger logger,
			IContextSensitiveFormatter<?>... localFormatters) {

		if (value != null && valueFormatter != null) {
			try {
				value = valueFormatter.format(value);
			} catch (ConQATException e) {
				logger.error(
						"Had problems while applying formatter: "
								+ e.getMessage(), e);
			}
		}

		appendValue(value, writer, localFormatters);
	}

	/**
	 * Append HTML description of value to a writer. The formatting is performed
	 * based on {@link IHTMLFormatter}s registered in the
	 * {@link HTMLPresentationManager}.
	 * 
	 * @param localFormatters
	 *            optional formatters for context/node specific formatting. The
	 *            first applicable formatter will be used. If none is
	 *            applicable, a formatter will be selected from the global
	 *            formatter pool.
	 */
	@SuppressWarnings("unchecked")
	public static void appendValue(Object value, HTMLWriter writer,
			IContextSensitiveFormatter<?>... localFormatters) {
		if (value == null) {
			writer.addNonBreakingSpace();
			return;
		}

		IHTMLFormatter<?> formatter = null;
		for (IContextSensitiveFormatter<?> localFormatter : localFormatters) {
			if (localFormatter.isApplicable(value)) {
				formatter = localFormatter;
				break;
			}
		}

		if (formatter == null) {
			formatter = BundleContext.getInstance()
					.getHtmlPresentationManager()
					.getFormatterForClass(value.getClass());
		}

		if (formatter == null) {
			writer.addNonBreakingSpace();
		} else {
			((IHTMLFormatter<Object>) formatter).formatObject(value, writer);
		}
	}

	/** Interface for formatters supporting more complex formatting decisions. */
	public static interface IContextSensitiveFormatter<T> extends
			IHTMLFormatter<T> {

		/**
		 * Returns whether this formatter is applicable for the given value. If
		 * so, this formatters {@link #formatObject(Object, HTMLWriter)} may be
		 * safely called with the same object.
		 */
		boolean isApplicable(Object value);
	}
}