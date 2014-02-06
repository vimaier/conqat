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
package org.conqat.engine.code_clones.detection;

import static org.conqat.engine.code_clones.core.utils.ECloneClassComparator.CARDINALITY;
import static org.conqat.engine.code_clones.core.utils.ECloneClassComparator.NORMALIZED_LENGTH;
import static org.conqat.engine.code_clones.core.utils.ECloneClassComparator.VOLUME;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.code_clones.core.CloneDetectionStatistics;
import org.conqat.engine.code_clones.core.ECloneDetectionStatistic;
import org.conqat.engine.code_clones.core.IdProvider;
import org.conqat.engine.code_clones.core.Unit;
import org.conqat.engine.code_clones.core.constraint.CardinalityConstraint;
import org.conqat.engine.code_clones.core.constraint.ConstraintList;
import org.conqat.engine.code_clones.core.constraint.ICloneClassConstraint;
import org.conqat.engine.code_clones.core.utils.CloneUtils;
import org.conqat.engine.code_clones.core.utils.ECloneClassComparator;
import org.conqat.engine.code_clones.detection.suffixtree.ICloneReporter;
import org.conqat.engine.commons.exceptions.EmptyInputException;
import org.conqat.engine.commons.findings.location.TextRegionLocation;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.collections.BoundedPriorityQueue;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.ListMap;
import org.conqat.lib.commons.date.DateUtils;
import org.conqat.lib.commons.digest.Digester;

/**
 * Base class for clone detection processors.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43767 $
 * @ConQAT.Rating GREEN Hash: D21C3F4A47EDE3750253CAA85F63C42D
 */
public abstract class CloneDetectorBase extends UnitProcessorBase {

	/** Key used for storing detection statistics */
	public static final String DETECTION_STATS = "Detection Statistics";

	/** Constant indicating that all clones are stored */
	private static final int INFINITE = -1;

	/**
	 * Number of units that a clone must at least comprise. If it has less, it
	 * gets filtered out.
	 */
	protected int minLength;

	/** Stores clone detection statistics */
	private CloneDetectionStatistics statistics;

	/** List of constraints that all detected clone classes must satisfy */
	private final ConstraintList constraints = new ConstraintList();

	/** Timestamp at which detection started */
	private Date systemDate;

	/** Maps uniform path to element. */
	private Map<String, ITextElement> uniformPathToElement;

	/** {@link IdProvider} used to create Ids for clone classes and clones */
	protected final IdProvider idProvider = new IdProvider();

	/** List of units retrieved from the units provider */
	protected List<Unit> units = new ArrayList<Unit>();

	/** Number of clones that are retained */
	private int top = INFINITE;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "top", description = "If set, only the top n clone classes with highest length, cardinality and volume are retained", minOccurrences = 0, maxOccurrences = 1)
	public void setTop(
			@AConQATAttribute(name = "value", description = "Must be positive") int top)
			throws ConQATException {
		if (top <= 0) {
			throw new ConQATException("Top must be positive but was " + top);
		}
		this.top = top;
	}

	/** Sets the unit list */
	@AConQATParameter(name = "clonelength", description = "Minimal length of Clone", minOccurrences = 1, maxOccurrences = 1)
	public void setMinLength(
			@AConQATAttribute(name = "min", description = "Minimal length of Clone") int minLength) {
		this.minLength = minLength;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "constraint", minOccurrences = 0, maxOccurrences = -1, description = ""
			+ "Adds a constraint that each detected clone class must satisfy")
	public void addConstraint(
			@AConQATAttribute(name = "type", description = "Clone classes that do not match the constraint are filtered") ICloneClassConstraint constraint) {
		constraints.add(constraint);
	}

	/** {@ConQAT.Doc} */
	@Override
	@AConQATParameter(name = "store", description = "Flag that determines whether units are stored in clones.", minOccurrences = 0, maxOccurrences = 1)
	public void setStoreUnits(
			@AConQATAttribute(name = "units", description = "Increases memory requirements. Default is false. Automatically set if a database space is set.") boolean storeUnits) {
		this.storeUnits = storeUnits;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "system", minOccurrences = 0, maxOccurrences = 1, description = "Date denoting the system version on which clone detection is performed.")
	public void setSystemDate(
			@AConQATAttribute(name = "date", description = "If not set, system date is set to now") Date systemDate) {
		this.systemDate = systemDate;
	}

	/** Constructor */
	public CloneDetectorBase() {
		// only detect clone classes of cardinality 2 or greater.
		// (clone classes can have cardinality of 1, if all contained clones
		// are considered equal by the set that stores a clone classes'
		// clones. this can happen if clones only differ
		// in start and end units that are located on the same lines.)
		CardinalityConstraint constraint = new CardinalityConstraint();
		constraint.setCardinality(2, CardinalityConstraint.INFINITY);
		constraints.add(constraint);
	}

	/** Calls template method to perform clone detection in deriving class */
	@Override
	public CloneDetectionResultElement process() throws ConQATException {

		uniformPathToElement = ResourceTraversalUtils
				.createUniformPathToElementMap(input, ITextElement.class);

		// create detection statistics object
		statistics = new CloneDetectionStatistics();
		input.setValue(DETECTION_STATS, statistics);

		drainUnits(units);

		// check if any units were found
		if (units.size() == 0) {
			throw new EmptyInputException();
		}

		List<CloneClass> cloneClasses = detectClones();

		collectStatistics(cloneClasses);

		// create detection result parameter object
		return new CloneDetectionResultElement(getSystemDate(), input,
				cloneClasses, createUnitsMap());
	}

	/**
	 * Stores number of processed units and number of found clones in detection
	 * statistics object
	 */
	private void collectStatistics(List<CloneClass> cloneClasses) {
		int unitCount = units.size();
		int cloneCount = CloneUtils.countClones(cloneClasses);

		getLogger().info("# Units: " + unitCount);
		getLogger().info("# Clones: " + cloneCount);

		statistics.setStatistic(ECloneDetectionStatistic.PROCESSED_UNIT_COUNT,
				unitCount);
		statistics.setStatistic(ECloneDetectionStatistic.CLONE_COUNT,
				cloneCount);
	}

	/**
	 * Template method that deriving classes override to implement their clone
	 * detection
	 */
	protected abstract List<CloneClass> detectClones() throws ConQATException;

	/** Returns timestamp at which detection started */
	protected Date getSystemDate() {
		if (systemDate == null) {
			// if not set, set to now
			systemDate = DateUtils.getNow();
		}
		return systemDate;
	}

	/** Create map from elements to units on which detection was performed */
	private Map<String, Unit[]> createUnitsMap() {
		ListMap<String, Unit> unitsPerElement = new ListMap<String, Unit>();
		for (Unit unit : units) {
			String elementId = unit.getElementUniformPath();
			// skip synthetic units, since they are not contained in the
			// elements
			if (!unit.isSynthetic()) {
				unitsPerElement.add(elementId, unit);
			}
		}

		return unitsPerElement.collectionsToArrays(Unit.class);
	}

	/**
	 * Receives found clones found during clone detection and packages them into
	 * clone classes.
	 * <p>
	 * Since this class accesses the units list of the clone detector, it is
	 * internal.
	 */
	protected class CloneConsumer implements ICloneReporter {

		/** List in which the created clone classes are stored */
		private final MultiplexingCloneClassesCollection results = new MultiplexingCloneClassesCollection();

		/**
		 * Creates a ICloneConsumer that writes the {@link CloneClass}es it
		 * creates into the given set
		 */
		public CloneConsumer() {
			if (top == INFINITE) {
				results.addCollection(new ArrayList<CloneClass>());
			} else {
				results.addCollection(boundedCollection(NORMALIZED_LENGTH));
				results.addCollection(boundedCollection(CARDINALITY));
				results.addCollection(boundedCollection(VOLUME));
			}
		}

		/** Creates {@link BoundedPriorityQueue} */
		private BoundedPriorityQueue<CloneClass> boundedCollection(
				ECloneClassComparator dimension) {
			return new BoundedPriorityQueue<CloneClass>(top, dimension);
		}

		/** {@link CloneClass} currently being filled */
		protected CloneClass currentCloneClass;

		/** Start new clone class */
		@Override
		public void startCloneClass(int normalizedLength) {
			currentCloneClass = new CloneClass(normalizedLength,
					idProvider.provideId());
		}

		/** Adds a clone to the current {@link CloneClass} */
		@Override
		public Clone addClone(int globalPosition, int length)
				throws ConQATException {
			// compute length of clone in lines
			Unit firstUnit = units.get(globalPosition);
			Unit lastUnit = units.get(globalPosition + length - 1);
			List<Unit> cloneUnits = units.subList(globalPosition,
					globalPosition + length);

			ITextElement element = resolveElement(firstUnit
					.getElementUniformPath());
			int startUnitIndexInElement = firstUnit.getIndexInElement();
			int endUnitIndexInElement = lastUnit.getIndexInElement();
			int lengthInUnits = endUnitIndexInElement - startUnitIndexInElement
					+ 1;
			CCSMAssert.isTrue(lengthInUnits >= 0, "Negative length in units!");
			String fingerprint = createFingerprint(globalPosition, length);

			Clone clone = new Clone(idProvider.provideId(), currentCloneClass,
					createCloneLocation(element,
							firstUnit.getFilteredStartOffset(),
							lastUnit.getFilteredEndOffset()),
					startUnitIndexInElement, lengthInUnits, fingerprint);

			if (storeUnits) {
				CloneUtils.setUnits(clone, cloneUnits);
			}

			currentCloneClass.add(clone);

			return clone;
		}

		/** Creates the location for a clone. */
		private TextRegionLocation createCloneLocation(ITextElement element,
				int filteredStartOffset, int filteredEndOffset)
				throws ConQATException {
			int rawStartOffset = element
					.getUnfilteredOffset(filteredStartOffset);
			int rawEndOffset = element.getUnfilteredOffset(filteredEndOffset);
			int rawStartLine = element
					.convertUnfilteredOffsetToLine(rawStartOffset);
			int rawEndLine = element
					.convertUnfilteredOffsetToLine(rawEndOffset);

			return new TextRegionLocation(element.getLocation(),
					element.getUniformPath(), rawStartOffset, rawEndOffset,
					rawStartLine, rawEndLine);
		}

		/** Determine element for unit */
		protected ITextElement resolveElement(String elementUniformPath) {
			return uniformPathToElement.get(elementUniformPath);
		}

		/** Create fingerprint for current clone */
		protected String createFingerprint(int globalPosition, int length) {
			StringBuilder fingerprintBase = new StringBuilder();
			for (int pos = globalPosition; pos < globalPosition + length; pos++) {
				fingerprintBase.append(units.get(pos).getContent());
			}
			return Digester.createMD5Digest(fingerprintBase.toString());
		}

		/** Check constraints */
		@Override
		public boolean completeCloneClass() throws ConQATException {
			boolean constraintsSatisfied = constraints
					.allSatisfied(currentCloneClass);

			if (constraintsSatisfied) {
				results.add(currentCloneClass);
			}

			return constraintsSatisfied;
		}

		/** Return list containing all retained clone classes */
		public List<CloneClass> getCloneClasses() {
			return results.getCloneClasses();
		}
	}

	/** Collection that adds {@link CloneClass} to all contained collections */
	private class MultiplexingCloneClassesCollection {

		/** Underlying collections */
		private final List<Collection<CloneClass>> collections = new ArrayList<Collection<CloneClass>>();

		/** Adds a clone class to all collections */
		public void add(CloneClass cloneClass) {
			for (Collection<CloneClass> collection : collections) {
				collection.add(cloneClass);
			}
		}

		/**
		 * Returns a list with all clones in the contained collections. The list
		 * is sorted by normalized length and contains no duplicates.
		 */
		public List<CloneClass> getCloneClasses() {
			Set<CloneClass> resultSet = new HashSet<CloneClass>();

			for (Collection<CloneClass> boundedCollection : collections) {
				resultSet.addAll(boundedCollection);
			}

			return CollectionUtils.sort(resultSet,
					ECloneClassComparator.NORMALIZED_LENGTH);
		}

		/** Add a collection */
		public void addCollection(Collection<CloneClass> collection) {
			collections.add(collection);
		}
	}

}