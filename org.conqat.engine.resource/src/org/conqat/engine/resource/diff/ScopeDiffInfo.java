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
package org.conqat.engine.resource.diff;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.pattern.PatternTransformationList;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.IConQATLogger;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.engine.resource.text.TextElementUtils;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.engine.resource.util.TransformedUniformPathToElementMap;
import org.conqat.lib.commons.clone.IDeepCloneable;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableSet;

/**
 * This class describes the difference between two scopes. We introduced it to
 * have a proper object that describes the distribution.
 * <p>
 * The term <i>main</i> refers to the primary resource tree (typically the
 * versioning system's trunk), while <i>comparee</i> is the version we compare
 * to (typically some older snapshot).
 * <p>
 * Instances of this class are immutable.
 * 
 * @author $Author: pfaller $
 * @version $Rev: 45937 $
 * @ConQAT.Rating YELLOW Hash: 9F23B50BFABEBB920895127C9F2C9FC5
 */
public class ScopeDiffInfo implements IDeepCloneable {

	/** Set of main elements. */
	private final Set<ITextElement> mainElements = new HashSet<ITextElement>();

	/** Set of comparee elements. */
	private final Set<ITextElement> compareeElements = new HashSet<ITextElement>();

	/** Set of added elements (only present in main scope). */
	private final Set<ITextElement> addedElements = new HashSet<ITextElement>();

	/** Set of removed elements (only present in comparee scope). */
	private final Set<ITextElement> removedElements = new HashSet<ITextElement>();

	/** Set of modified elements (all elements are present in both scopes). */
	private final Set<ITextElement> modifiedElements = new HashSet<ITextElement>();

	/** Set of unmodified elements (all elements are present in both scopes). */
	private final Set<ITextElement> unmodifiedElements = new HashSet<ITextElement>();

	/**
	 * Sum of all line churn, evaluates {@link ScopeDiffer#KEY_CHURN_LINES}.
	 */
	private int churnLines = 0;

	/**
	 * Flag which indicates if line churn information is valid, e. g. for all
	 * elements in the main root line churn information is stored.
	 */
	boolean validLineChurn = true;

	/** Sum of lines in removed elements. */
	private int linesInRemovedElements;

	/**
	 * Sum of normalized lines (base for relative churn)
	 */
	private int normalizedLines;

	/** Constructor. */
	public ScopeDiffInfo(ITextResource mainRoot, ITextResource compareeRoot,
			PatternTransformationList transformations, IConQATLogger logger)
			throws ConQATException {
		mainElements.addAll(ResourceTraversalUtils.listTextElements(mainRoot));
		compareeElements.addAll(ResourceTraversalUtils
				.listTextElements(compareeRoot));

		determineModifiedElements(mainRoot, compareeRoot, transformations,
				logger);
	}

	/**
	 * Determine which elements are modified and store both the
	 * {@link #modifiedElements} as well as the {@link #unmodifiedElements}.
	 */
	@SuppressWarnings("unchecked")
	private void determineModifiedElements(ITextResource mainRoot,
			ITextResource compareeRoot,
			PatternTransformationList transformations, IConQATLogger logger)
			throws ConQATException {

		Map<String, ITextElement> mainMap = ResourceTraversalUtils
				.createUniformPathToElementMap(mainRoot, ITextElement.class);
		TransformedUniformPathToElementMap<ITextElement> compareeMap = ResourceTraversalUtils
				.createTransformedUniformPathToElementMap(compareeRoot,
						ITextElement.class, transformations, logger);

		for (String uniformPath : CollectionUtils.unionSet(mainMap.keySet(),
				compareeMap.lookupPaths())) {

			ITextElement mainElement = TransformedUniformPathToElementMap
					.getElementFromMap(mainMap, uniformPath);
			ITextElement compareeElement = compareeMap.getElement(uniformPath);

			if (mainElement == null) {
				removedElements.add(compareeElement);
				int compareeLines = TextElementUtils.getNormalizedContent(
						compareeElement).size();
				linesInRemovedElements += compareeLines;
			} else if (compareeElement == null) {
				addedElements.add(mainElement);
				addLineCount(mainElement);
			} else if (isElementContentEqual(mainElement, compareeElement)) {
				unmodifiedElements.add(mainElement);
				addLineCount(mainElement);
			} else {
				modifiedElements.add(mainElement);
				addLineCount(mainElement);
			}
		}
	}

	/**
	 * Adds the count of churn lines and normalized lines of the given element
	 * to this diff info. Requires that the keys
	 * {@value ScopeDiffer#KEY_CHURN_LINES} and
	 * {@value ScopeDiffer#KEY_NORMALIZED_LINES} are set for the given value.
	 */
	private void addLineCount(ITextElement mainElement) {
		try {
			churnLines += NodeUtils.getDoubleValue(mainElement,
					ScopeDiffer.KEY_CHURN_LINES);
			normalizedLines += NodeUtils.getDoubleValue(mainElement,
					ScopeDiffer.KEY_NORMALIZED_LINES);
		} catch (ConQATException e) {
			validLineChurn = false;
			churnLines = -1;
			normalizedLines = -1;
		}
	}

	/** Checks whether the normalized content of the elements equals. */
	private boolean isElementContentEqual(ITextElement mainElement,
			ITextElement compareeElement) throws ConQATException {
		List<String> elementLines = TextElementUtils
				.getNormalizedContent(mainElement);
		List<String> compareeLines = TextElementUtils
				.getNormalizedContent(compareeElement);

		return elementLines.equals(compareeLines);
	}

	/** Get all elements form the main scope. */
	public UnmodifiableSet<ITextElement> getMainElements() {
		return CollectionUtils.asUnmodifiable(mainElements);
	}

	/** Get all elements form the comparee scope. */
	public UnmodifiableSet<ITextElement> getCompareeElements() {
		return CollectionUtils.asUnmodifiable(compareeElements);
	}

	/** Get all elements that have been added to the main scope. */
	public UnmodifiableSet<ITextElement> getAddedElements() {
		return CollectionUtils.asUnmodifiable(addedElements);
	}

	/** Get all elements that have been removed from the comparee scope. */
	public UnmodifiableSet<ITextElement> getRemovedElements() {
		return CollectionUtils.asUnmodifiable(removedElements);
	}

	/**
	 * Get all elements that have been modified but are present in both scopes.
	 * The elements from the main scope are returned.
	 */
	public UnmodifiableSet<ITextElement> getModifiedElements() {
		return CollectionUtils.asUnmodifiable(modifiedElements);
	}

	/**
	 * Get all elements that have <b>not</b> been modified but are present in
	 * both scopes. The elements from the main scope are returned.
	 */
	public UnmodifiableSet<ITextElement> getUnmodifiedElements() {
		return CollectionUtils.asUnmodifiable(unmodifiedElements);
	}

	/** Get a count of all elements present in either main or comparee. */
	public int getTotalElementCount() {
		return addedElements.size() + removedElements.size()
				+ unmodifiedElements.size() + modifiedElements.size();
	}

	/** Returns <code>this</code> as this is immutable. */
	@Override
	public IDeepCloneable deepClone() {
		return this;
	}

	/** Gets the total number of churn lines. */
	public int getChurnLines() {
		return churnLines;
	}

	/**
	 * Determines if a valid line churn information is present, e. g. line churn
	 * information was set at all elements in the scope.
	 */
	public boolean hasValidLineChurn() {
		return validLineChurn;
	}

	/** Gets the number of lines in removed elements. */
	public int getLinesInRemovedElements() {
		return linesInRemovedElements;
	}

	/**
	 * Gets the total normalized line length. That is the base for relative
	 * churn. Does not include the lines in removed elements.
	 */
	public int getNormalizedLines() {
		return normalizedLines;
	}

}
