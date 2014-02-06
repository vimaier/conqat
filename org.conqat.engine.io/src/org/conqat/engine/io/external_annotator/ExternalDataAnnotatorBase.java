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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.base.ElementTraversingProcessorBase;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.lib.commons.collections.CollectionMap;
import org.conqat.lib.commons.collections.ListMap;
import org.conqat.lib.commons.collections.SetMap;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Base class for processors which read data from external data set files and
 * store the data at corresponding {@link IElement}s.
 * 
 * The external file is expected to hold a set of data objects where each data
 * object (record) should be annotated to an element in the input scope.
 * Multiple data objects may be annotated to the same element.
 * 
 * Typically this base class is used for processors which hold table-like data
 * where each row holds a data record which should be annotated.
 * 
 * The record type of the data object is determined by the typing enumeration
 * which defines the fields of the data object.
 * 
 * To determine if a data record and an element correspond to each other
 * {@link #getRecordElementIdentifier(ExternalDataRecord)} and
 * {@link #getElementIdentifier(IElement)} are used.
 * 
 * The resulting lines are stored under the key given by {@link #getKey()} at
 * the elements. Unless parameter <code>retain-duplicates</code> is set to
 * <code>true</code> the sequential order of the entries in the data set may not
 * be preserved.
 * 
 * @param <R>
 *            the type of resources on the input scope to be traversed (holds
 *            the elements which should receive the annotation)
 * 
 * @param <E>
 *            the type of element this works on. The element class should
 *            implement R and must match with the class returned from
 *            {@link ElementTraversingProcessorBase#getElementClass()}
 * 
 * @param <C>
 *            enumeration type of the record field names of the external data
 *            object
 * 
 * @author $Author: pfaller $
 * @version $Rev: 46804 $
 * @ConQAT.Rating YELLOW Hash: 14D360543CD980BF23CD12F4104D1495
 */
public abstract class ExternalDataAnnotatorBase<R extends IResource, E extends IElement, C extends Enum<C>>
		extends ElementTraversingProcessorBase<R, E> {

	/** Map from elements identifier to external data objects */
	protected CollectionMap<String, ExternalDataRecord<C>, ? extends Collection<ExternalDataRecord<C>>> elementsToExternalDataMap;

	/** Class of data fields enumeration */
	protected final Class<C> fieldsEnumClass;

	/** Key in which ignore flags are stored */
	protected final Set<String> ignoreKeys = new HashSet<String>();

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "external-input", attribute = ConQATParamDoc.INPUT_REF_NAME, description = ""
			+ "Input scope of external files.")
	public ITextResource externalScope = null;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "external-include", attribute = "pattern-list", description = ""
			+ "Pattern for included external data files. The patterns are matched against the uniform path.")
	public PatternList externalIncludePatternList = null;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "warn-on-missing", attribute = "value", optional = true, description = ""
			+ "If <code>true</code>, a warning is logged, if an element is without an matching entry "
			+ "in the external data. Default is <code>false</code>")
	public boolean warnOnMissing = false;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "retain-duplicates", attribute = "value", optional = true, description = ""
			+ "If <code>true</code>, duplicated entries for a record will be retained, "
			+ "and the resulting list preserved the sequential order of the entries. "
			+ "Otherwise duplicates are removed and ordering is not preserved. Default is <code>false</code>.")
	public boolean retainDuplicates = false;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "filter", description = "Determines whether elements that are marked as ignored, "
			+ "e.g. because they are in genereated code, are ignored.", minOccurrences = 0, maxOccurrences = -1)
	public void setFilterIgnored(
			@AConQATAttribute(name = "key", description = "Key that contains ignore flags.", defaultValue = "ignore") String ignoreKey)
			throws ConQATException {

		if (StringUtils.isEmpty(ignoreKey)) {
			throw new ConQATException("Ignore key must not be empty");
		}

		ignoreKeys.add(ignoreKey);
	}

	/** Constructor. */
	protected ExternalDataAnnotatorBase(Class<C> fieldsEnumClass) {
		this.fieldsEnumClass = fieldsEnumClass;
	}

	/**
	 * Parses a an external {@link ITextElement} as a file which holds lines of
	 * external data and returns a list of the parsed data objects.
	 */
	protected abstract List<ExternalDataRecord<C>> parseExternalDataElement(
			ITextElement element) throws ConQATException;

	/**
	 * During set-up the {@link #externalScope} is traversed and all data
	 * objects of the matching external files are collected.
	 */
	@Override
	protected void setUp(R root) throws ConQATException {
		if (retainDuplicates) {
			elementsToExternalDataMap = new ListMap<String, ExternalDataRecord<C>>();
		} else {
			elementsToExternalDataMap = new SetMap<String, ExternalDataRecord<C>>();
		}
		for (ITextElement element : ResourceTraversalUtils
				.listTextElements(externalScope)) {
			if (externalIncludePatternList.matchesAny(element.getUniformPath())) {
				fillElementsToExternalDataMap(parseExternalDataElement(element));
			}
		}
	}

	/**
	 * Fills {@link #elementsToExternalDataMap} with the given
	 * {@link ExternalDataRecord}s. Each record is added to a list which is
	 * stored under the key determined by
	 * {@link #getRecordElementIdentifier(ExternalDataRecord)}. If the element
	 * identifier returned by
	 * {@link #getRecordElementIdentifier(ExternalDataRecord)} is empty, the
	 * record will be ignored.
	 */
	private void fillElementsToExternalDataMap(
			List<ExternalDataRecord<C>> records) {
		for (ExternalDataRecord<C> record : records) {
			try {
				String elementIdentifier = getRecordElementIdentifier(record);
				if (!StringUtils.isEmpty(elementIdentifier)) {
					elementsToExternalDataMap.add(elementIdentifier, record);
				}
			} catch (ConQATException e) {
				getLogger().warn(
						"External data record '" + record
								+ "' can not be mapped to an element.", e);
			}
		}
	}

	/**
	 * {@inheritDoc} In subclasses,
	 * {@link #annotateDataRecords(IElement, Collection)} may be overwritten.
	 */
	@Override
	protected final void processElement(E element) {
		if (ResourceTraversalUtils.isIgnored(element, ignoreKeys)) {
			return;
		}
		String mapKey;
		try {
			mapKey = getElementIdentifier(element);
		} catch (ConQATException e) {
			getLogger().warn(
					"Element '" + element
							+ "' can not be mapped to an identifier.", e);
			return;
		}

		Collection<ExternalDataRecord<C>> elementResults = elementsToExternalDataMap
				.getCollection(mapKey);
		if (elementResults == null) {
			if (warnOnMissing) {
				getLogger().warn(
						"No '" + fieldsEnumClass + "' record found for "
								+ element.getUniformPath());
			}
		} else {
			annotateDataRecords(element, elementResults);
		}
	}

	/**
	 * Annotates the list of data records for an element at the element. In the
	 * default implementation, the list of records is simply stored at the key
	 * determined by {@link #getKey()}.
	 * 
	 * This method may be overwritten if a different treatment of external data
	 * is required.
	 */
	protected void annotateDataRecords(E element,
			Collection<ExternalDataRecord<C>> records) {
		element.setValue(getKey(), records);
	}

	/**
	 * Gets the key under which the data extracted from the external file is
	 * stored. Since usually the extracted data requires further processing the
	 * key is <b>not</b> added to the display list.
	 */
	protected abstract String getKey();

	/**
	 * Gets an identifier string of the element where the data object should be
	 * annotated to.
	 * 
	 * The methods {@link #getRecordElementIdentifier(ExternalDataRecord)} and
	 * {@link #getElementIdentifier(IElement)} must return equal identifiers for
	 * matching records / elements. If the returned identifier is empty or
	 * <code>null</code>, the given record will be ignored.
	 * 
	 * @throws ConQATException
	 *             If the record can not be mapped to an element identifier
	 */
	protected abstract String getRecordElementIdentifier(
			ExternalDataRecord<C> record) throws ConQATException;

	/**
	 * Gets an identifier string of the element where the data object should be
	 * annotated file should be stored.
	 * 
	 * The methods {@link #getRecordElementIdentifier(ExternalDataRecord)} and
	 * {@link #getElementIdentifier(IElement)} must return equal identifiers for
	 * matching records / elements.
	 * 
	 * @throws ConQATException
	 *             If the record can not be mapped to an identifier
	 */
	protected abstract String getElementIdentifier(E element)
			throws ConQATException;
}
