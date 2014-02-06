/*-----------------------------------------------------------------------+
 | eu.cqse.conqat.engine.abap
 |                                                                       |
   $Id: CsvFileAnnotatorBase.java 44652 2013-04-25 15:33:59Z hummelb $            
 |                                                                       |
 | Copyright (c)  2009-2013 CQSE GmbH                                 |
 +-----------------------------------------------------------------------*/
package org.conqat.engine.io.external_annotator;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.base.ElementTraversingProcessorBase;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.lib.commons.enums.EnumUtils;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

/**
 * Base class for processors which read data from CSV files and store the date
 * at corresponding {@link IElement}s.
 * 
 * The first line of the CSV file is treated as header which holds the column
 * names. The column names must be the same is in the enumeration type <C>.
 * 
 * For parsing, <a href="http://supercsv.sourceforge.net/">Simple CSV</a> is
 * used.
 * 
 * Each further line in the CSV file must correspond to a an Element of the
 * input node. Multiple lines may be annotated to the same Element. To determine
 * if a line and an element correspond to each other
 * {@link #getRecordElementIdentifier(ExternalDataRecord)} and
 * {@link #getElementIdentifier(IElement)} are used.
 * 
 * The resulting lines are stored under the key given by {@link #getKey()} at
 * the elements.
 * 
 * @param <R>
 *            the type of resources on the input scope to be traversed (holds
 *            the elements which should receive the annotation)
 * 
 * @param <E>
 *            the type of element this works on. The element class should
 *            implement R and must match with the class returned from
 *            {@link ElementTraversingProcessorBase#getElementClass()}
 * @param <C>
 *            enumeration type of column names
 * 
 * @author $Author: hummelb $
 * @version $Rev: 44652 $
 * @ConQAT.Rating GREEN Hash: 0AF3F5B026DD9D10B1F9574F2DE3C05D
 */
public abstract class CsvFileAnnotatorBase<R extends IResource, E extends IElement, C extends Enum<C>>
		extends ExternalDataAnnotatorBase<R, E, C> {

	/** Preferences object for SuperCSV CSV parser */
	private CsvPreference csvParserPreference = (new CsvPreference.Builder('"',
			';', "\n")).build();

	/** {@ConQAT.Coc} */
	@AConQATParameter(name = "csv-preferences", maxOccurrences = 1, description = ""
			+ "Preferences for CSV parsing")
	public void setCsvPreferences(
			@AConQATAttribute(name = "delimiter-char", defaultValue = ";", description = ""
					+ "the character separating each CSV column") char delimiterChar,
			@AConQATAttribute(name = "quote-char", defaultValue = "\"", description = ""
					+ "matching pairs of this character are used to escape columns containing the delimiter") char quoteChar) {
		// end of line symbol is only used for writing CSV thus it is not
		// exposed as a ConQAT attribute
		csvParserPreference = (new CsvPreference.Builder(quoteChar,
				delimiterChar, "\n")).build();
	}

	/** Constructor. */
	protected CsvFileAnnotatorBase(Class<C> columnsEnumClass) {
		super(columnsEnumClass);
	}

	/**
	 * Parses a an {@link ITextElement} as CSV file and returns a list of parsed
	 * lines.
	 */
	@Override
	protected List<ExternalDataRecord<C>> parseExternalDataElement(
			ITextElement element) throws ConQATException {

		List<ExternalDataRecord<C>> parsedLines = new ArrayList<ExternalDataRecord<C>>();

		// only working on strings, thus resource is not required to be closed
		@SuppressWarnings("resource")
		ICsvListReader csvListReader = new CsvListReader(new StringReader(
				element.getTextContent()), csvParserPreference);
		try {

			List<C> headerCells = getHeaderObjects(csvListReader
					.getHeader(true));
			List<String> lineCells;
			while ((lineCells = csvListReader.read()) != null) {
				ExternalDataRecord<C> parsedCsvLine = new ExternalDataRecord<C>(
						fieldsEnumClass, headerCells, lineCells);
				parsedLines.add(parsedCsvLine);
			}

		} catch (IOException e) {
			throw new ConQATException("CSV parser exception occured", e);
		}

		return parsedLines;
	}

	/**
	 * Helper method to convert a String array holding the header
	 */
	private List<C> getHeaderObjects(String[] headerCells)
			throws ConQATException {
		List<C> header = new ArrayList<C>(headerCells.length);
		for (String column : headerCells) {
			C columnObject = EnumUtils.valueOfIgnoreCase(fieldsEnumClass,
					column);
			if (columnObject == null) {
				throw new ConQATException("CSV header mismatch: Enum class "
						+ fieldsEnumClass + " does not contain '" + column
						+ "'.");
			}
			header.add(columnObject);
		}
		return header;
	}

}
