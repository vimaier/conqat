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
package org.conqat.engine.io.external_annotator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.base.ElementTraversingProcessorBase;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.util.TextElementXMLReader;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.commons.xml.IXMLElementProcessor;

/**
 * Base class for processors which read data from simple XML files and store the
 * date at corresponding {@link IElement}s.
 * 
 * It is expected that the XML element refereed by
 * {@link #getRecordXmlElement()} holds the data objects which should be
 * annotated and the children of this XML element are the fields of that data
 * elements.
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
 *            enumeration type of XML elements, containing the field elements as
 *            well as the data object element
 * 
 * @author $Author: hummelb $
 * @version $Rev: 45339 $
 * @ConQAT.Rating GREEN Hash: F0336A89286F6155EC5D0D5B1AB49971
 */
public abstract class XmlFileAnnotatorBase<R extends IResource, E extends IElement, C extends Enum<C>>
		extends ExternalDataAnnotatorBase<R, E, C> {

	/** Cache for the XML element list */
	private List<C> xmlElements;

	/**
	 * Constructor.
	 */
	protected XmlFileAnnotatorBase(Class<C> fieldsEnumClass) {
		super(fieldsEnumClass);
	}

	/** {@inheritDoc} */
	@Override
	protected List<ExternalDataRecord<C>> parseExternalDataElement(
			ITextElement element) throws ConQATException {
		XmlDataReader reader = new XmlDataReader(element);
		reader.process();
		return reader.dataObjects;
	}

	/**
	 * Determines the XML element which encloses a single data object entry
	 */
	protected abstract C getRecordXmlElement();

	/**
	 * Gets the list of elements in C which are used as fields of the resulting
	 * data type. By default these are all elements of <C> without
	 * {@link #getRecordXmlElement()}. This method may be overwritten if
	 * necessary.
	 */
	protected List<C> getFieldXmlElements() {
		if (xmlElements == null) {
			xmlElements = new ArrayList<C>(Arrays.asList(fieldsEnumClass
					.getEnumConstants()));
			xmlElements.remove(getRecordXmlElement());
		}
		return xmlElements;
	}

	/**
	 * XML Reader to parse the XML data objects. Since attributes are not
	 * evaluated the attribute type is irrelevant and <C> is used to match the
	 * interface of the base class.
	 */
	private class XmlDataReader extends
			TextElementXMLReader<C, C, ConQATException> {

		/** List where the resulting data objects are stored */
		public List<ExternalDataRecord<C>> dataObjects = new ArrayList<ExternalDataRecord<C>>();

		/** Constructor. */
		public XmlDataReader(ITextElement element) throws ConQATException {
			super(element, fieldsEnumClass);
		}

		/**
		 * Processes the input XML.
		 */
		public void process() throws ConQATException {
			parseAndWrapExceptions();
			processDecendantElements(new RecordProcessor(this, dataObjects));
		}

		/** {@inheritDoc} */
		@Override
		public void processDecendantElements(
				IXMLElementProcessor<C, ConQATException> processor)
				throws ConQATException {
			// redefined to make visible to processors
			super.processDecendantElements(processor);
		}

		/** {@inheritDoc} */
		@Override
		public String getText() {
			// redefined to make visible to processors
			return super.getText();
		}

	}

	/** XML processor for a single data record */
	private class RecordProcessor implements
			IXMLElementProcessor<C, ConQATException> {

		/** The XML reader which uses this processor */
		private final XmlDataReader reader;

		/** List were to store the resulting data objects */
		private final List<ExternalDataRecord<C>> dataObjects;

		/**
		 * Constructor
		 */
		public RecordProcessor(XmlDataReader reader,
				List<ExternalDataRecord<C>> dataObjects) {
			this.reader = reader;
			this.dataObjects = dataObjects;
		}

		/** {@inheritDoc} */
		@Override
		public C getTargetElement() {
			return getRecordXmlElement();
		}

		/** {@inheritDoc} */
		@Override
		public void process() throws ConQATException {
			List<String> lineCells = new ArrayList<String>();
			for (C field : getFieldXmlElements()) {

				// field may not be a direct child thus we process
				// all descendant elements, there should be only
				// one element of that type
				FieldProcessor fieldProcessor = new FieldProcessor(reader,
						field);
				reader.processDecendantElements(fieldProcessor);

				if (fieldProcessor.value == null) {
					getLogger().warn(
							"XML element '" + field + "' not found within '"
									+ getTargetElement() + "'.");
					lineCells.add(StringUtils.EMPTY_STRING);
				} else {
					lineCells.add(fieldProcessor.value);
				}
			}
			dataObjects.add(new ExternalDataRecord<C>(fieldsEnumClass,
					getFieldXmlElements(), lineCells));
		}
	}

	/** XML processor for a field within the data record */
	private class FieldProcessor implements
			IXMLElementProcessor<C, ConQATException> {

		/** Field to process */
		private C field;

		/** Stores the value to read */
		public String value;

		/** The XML reader which uses this processor */
		private final XmlDataReader reader;

		/** Constructor */
		public FieldProcessor(XmlDataReader reader, C field) {
			this.reader = reader;
			this.field = field;
		}

		/** {@inheritDoc} */
		@Override
		public C getTargetElement() {
			return field;
		}

		/** {@inheritDoc} */
		@Override
		public void process() throws ConQATException {
			if (value != null) {
				throw new ConQATException("Multiple values for '" + field
						+ "' are available at record.");
			}
			value = reader.getText();
		}
	}
}
