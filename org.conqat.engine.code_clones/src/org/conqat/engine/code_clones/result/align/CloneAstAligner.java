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
package org.conqat.engine.code_clones.result.align;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.code_clones.detection.CloneDetectionResultElement;
import org.conqat.engine.code_clones.detection.UnitProcessorBase;
import org.conqat.engine.code_clones.result.DetectionResultProcessorBase;
import org.conqat.engine.commons.findings.location.TextRegionLocation;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.shallowparser.ShallowParserFactory;
import org.conqat.engine.sourcecode.shallowparser.framework.EShallowEntityType;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.IdentityHashSet;
import org.conqat.lib.commons.digest.Digester;
import org.conqat.lib.commons.region.Region;
import org.conqat.lib.commons.region.RegionSet;
import org.conqat.lib.scanner.IToken;

/**
 * {@ConQAT.Doc}
 * 
 * Note that the code uses 1-based lines internally, as this is the way we deal
 * with lines in elements and findings. As clones expect 0-based lines, we have
 * to perform conversions in some places.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 45636 $
 * @ConQAT.Rating GREEN Hash: C012EF459E6E0A85B57E5E0DF052AB10
 */
@AConQATProcessor(description = "Realigns the clones to the AST of the code. "
		+ "Clones are also trimmed to match entire AST subtrees. "
		+ "Alignment dos not work with gapped clones.")
public class CloneAstAligner extends DetectionResultProcessorBase {

	/**
	 * Maps each clone to the (unique) clone class it belongs to. Implicitly,
	 * this also stores the resulting clone classes after alignment.
	 */
	private final Map<Clone, CloneClass> alignedCloneClasses = new HashMap<Clone, CloneClass>();

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "clonelength", attribute = "min", description = "Minimal length of resulting clones in statements.")
	public int minLength;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "update-unit-keys", attribute = "value", optional = true, description = ""
			+ "Whether to update the unit count keys based on AST units. Default is true.")
	public boolean updateAllUnitKeys = true;

	/** Counter used for generating IDs for clones. */
	private long cloneIdCounter = 0;

	/** Counter used for generating IDs for clone classes. */
	private long cloneClassIdCounter = 0;

	/**
	 * The number of clone classes that could not be aligned consistently to the
	 * AST.
	 */
	private int unalignedCloneClassesCount = 0;

	/** Maps uniform path to token element. */
	private Map<String, ITokenElement> uniformPathToElement;

	/** Caches the {@link AlignerStatement}s for uniform paths. */
	private Map<String, List<AlignerStatement>> uniformPathToAlignerStatements = new HashMap<String, List<AlignerStatement>>();

	/** {@inheritDoc} */
	@Override
	public CloneDetectionResultElement process() throws ConQATException {

		if (minLength < 1) {
			throw new ConQATException("Min clone length must be positive!");
		}

		uniformPathToElement = ResourceTraversalUtils
				.createUniformPathToElementMap(detectionResult,
						ITokenElement.class);

		setOverallWork(detectionResult.getList().size() + 1);
		if (updateAllUnitKeys) {
			for (String uniformPath : uniformPathToElement.keySet()) {
				cacheStatementsAndUpdateUnits(uniformPath);
			}
		}
		workDone(1);

		for (CloneClass cloneClass : detectionResult.getList()) {
			alignCloneClass(cloneClass);
			workDone(1);
		}

		getLogger().info(
				"Processed " + detectionResult.getList().size()
						+ " clone classes.");
		getLogger().info(
				"Had to cache and parse "
						+ uniformPathToAlignerStatements.size() + " elements.");
		getLogger().info(
				"Alignment failed for " + unalignedCloneClassesCount
						+ " clone classes.");
		getLogger().info(
				"Alignment resulted in " + alignedCloneClasses.size()
						+ " clone classes.");

		// We use IdentityHashSet to get rid of multiply stored instances
		Set<CloneClass> cloneClasses = new IdentityHashSet<CloneClass>(
				alignedCloneClasses.values());
		return detectionResultForCloneClasses(new ArrayList<CloneClass>(
				cloneClasses));
	}

	/**
	 * Attempts to align the clone class and stores the result in
	 * {@link #alignedCloneClasses}. Aligning can result in one or more classes,
	 * as classes may be split as a result. If the split fragments are too
	 * small, the clone class is completely omitted.
	 */
	private void alignCloneClass(CloneClass cloneClass) throws ConQATException {

		List<List<Clone>> alignedClones = new ArrayList<List<Clone>>();
		for (Clone clone : cloneClass.getClones()) {
			if (clone.containsGaps()) {
				throw new ConQATException(
						"This processor can not work with gapped clones!");
			}

			alignedClones.add(alignClone(clone));
		}

		if (!checkAlignedClones(alignedClones)) {
			unalignedCloneClassesCount += 1;
			return;
		}

		int numAligned = alignedClones.get(0).size();
		for (int i = 0; i < numAligned; ++i) {
			int length = alignedClones.get(0).get(i).getLengthInUnits();
			if (length < minLength) {
				continue;
			}

			CloneClass newCloneClass = new CloneClass(length,
					cloneClassIdCounter++);
			for (List<Clone> fragments : alignedClones) {
				if (length != fragments.get(i).getLengthInUnits()) {
					unalignedCloneClassesCount += 1;
					return;
				}
				newCloneClass.add(fragments.get(i));
			}

			insertCloneClass(newCloneClass);
		}
	}

	/**
	 * Inserts a clone class and deals with merging of other (existing) clone
	 * classes that contain the same clones.
	 */
	private void insertCloneClass(CloneClass newCloneClass) {
		Set<CloneClass> existingClasses = new IdentityHashSet<CloneClass>();
		for (Clone clone : newCloneClass.getClones()) {
			CloneClass cloneClass = alignedCloneClasses.get(clone);
			if (cloneClass != null) {
				existingClasses.add(cloneClass);
			}
		}

		for (CloneClass cloneClass : existingClasses) {
			for (Clone clone : new ArrayList<Clone>(cloneClass.getClones())) {
				newCloneClass.add(clone);
			}
		}

		for (Clone clone : newCloneClass.getClones()) {
			alignedCloneClasses.put(clone, newCloneClass);
		}
	}

	/**
	 * Checks whether clone alignment resulted in the same structure for all
	 * clones, i.e. each clones was split into the same number of clones. This
	 * essentially ensures that all lists have the same size.
	 * 
	 * @return true if all lists have the same size.
	 */
	private boolean checkAlignedClones(List<List<Clone>> alignedClones) {
		CCSMPre.isFalse(alignedClones.isEmpty(), "May not pass empty list!");
		int numAligned = alignedClones.get(0).size();
		for (List<Clone> fragments : alignedClones) {
			if (fragments.size() != numAligned) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Aligns a single clone instance to the AST. For this, we manage a starts
	 * stack containing the start position for the last seen AST element of each
	 * depth. Hence, the size of the stack is the same as the current depth of
	 * the AST statement inspected. As we start as an arbitrary statement (start
	 * of clone), the stack is initially padded with null values to compensate
	 * for this.
	 */
	private List<Clone> alignClone(Clone clone) throws ConQATException {
		List<AlignerStatement> statements = getAlignerStatementsForUniformPath(clone
				.getUniformPath());

		Region statementRegion = determineStatementRegion(clone, statements);
		int start = statementRegion.getStart();
		int end = statementRegion.getEnd();

		if (statementRegion.getLength() <= 1) {
			return CollectionUtils.emptyList();
		}

		Stack<Integer> starts = initializeStarts(statements, start);
		RegionSet cloneRegions = new RegionSet();

		for (int i = start + 1; i <= end; ++i) {
			AlignerStatement currentStatement = statements.get(i);

			int nextDepth = currentStatement.getDepth() + 1;
			if (nextDepth > starts.size()) {
				// the depth in the AST increases, so this is an opening
				// statement and we add a new start position
				starts.push(i);
			} else if (nextDepth < starts.size()) {
				// the depth in the AST decreases, so this is a closing
				// statement and we remove a start position ...
				starts.pop();

				// ... and if we have a valid start, we register this region as
				// part of the clone
				if (starts.peek() != null) {
					cloneRegions.add(new Region(starts.peek(), i));
				}
			} else if (starts.peek() != null) {
				// if we remain on the same level (and have a valid start), we
				// enlarge the current clone region (must be performed now, as
				// we don't know, if a closing statement will be part of the
				// clone)
				cloneRegions.add(new Region(starts.peek(), i));
			} else {
				// else we are on the same level but do not have a start
				// position for this level, so insert this position as start
				// position.
				starts.pop();
				starts.push(i);
			}

			CCSMAssert
					.isTrue(nextDepth == starts.size(),
							"The stack should always resemble the depth, as we create all intermediate AST levels.");
		}

		return createClonesFromRegions(clone, statements,
				cloneRegions.createCompact());
	}

	/** Initializes the starts stack as described in {@link #alignClone(Clone)}. */
	private Stack<Integer> initializeStarts(List<AlignerStatement> statements,
			int start) {
		Stack<Integer> starts = new Stack<Integer>();
		for (int i = 0; i < statements.get(start).getDepth(); ++i) {
			starts.push(null);
		}
		if (statements.get(start).getStatementType() == EAlignerStatementType.COMPOUND_END) {
			starts.push(null);
		} else {
			starts.push(start);
		}
		return starts;
	}

	/**
	 * Creates the new clones based on the statements and the regions denoting
	 * start and end indexes in these statements.
	 */
	private List<Clone> createClonesFromRegions(Clone originClone,
			List<AlignerStatement> statements, RegionSet cloneRegions) {
		List<Clone> clones = new ArrayList<Clone>();
		for (Region region : cloneRegions) {
			int currentStart = region.getStart();
			AlignerStatement firstStatement = statements.get(currentStart);
			while (!firstStatement.hasPosition()
					|| firstStatement.getStatementType() == EAlignerStatementType.COMPOUND_END) {
				firstStatement = statements.get(++currentStart);
			}

			int currentEnd = region.getEnd();
			AlignerStatement lastStatement = statements.get(currentEnd);
			while (!lastStatement.hasPosition()
					|| lastStatement.getStatementType() == EAlignerStatementType.COMPOUND_START) {
				lastStatement = statements.get(--currentEnd);
			}

			List<AlignerStatement> cloneStatements = statements.subList(
					currentStart, currentEnd + 1);
			int size = countNonArtificialStatements(cloneStatements);
			if (size < minLength) {
				continue;
			}

			TextRegionLocation location = new TextRegionLocation(originClone
					.getLocation().getLocation(), originClone.getUniformPath(),
					firstStatement.getRawStartOffset(),
					lastStatement.getRawEndOffset(),
					firstStatement.getRawStartLine(),
					lastStatement.getRawEndLine());
			Clone newClone = new Clone(cloneIdCounter++, null, location,
					statements.get(currentStart)
							.getNonArtificialStatementIndex(), size,
					calculateFingerprint(cloneStatements));
			clones.add(newClone);
		}
		return clones;
	}

	/** Counts the number of non-artificial statements. */
	private int countNonArtificialStatements(List<AlignerStatement> statements) {
		int count = 0;
		if (!statements.isEmpty()) {
			AlignerStatement last = CollectionUtils.getLast(statements);
			count = last.getNonArtificialStatementIndex()
					- statements.get(0).getNonArtificialStatementIndex();
			if (!last.isArtificial()) {
				count += 1;
			}
		}
		return count;
	}

	/** Calculates a fingerprint based on statements content. */
	private String calculateFingerprint(List<AlignerStatement> statements) {
		List<String> contents = new ArrayList<String>();
		for (AlignerStatement statement : statements) {
			if (!statement.isArtificial()) {
				contents.add(statement.getContent());
			}
		}

		CCSMAssert.isFalse(contents.isEmpty(),
				"Had no non-artificial content in clone!");
		return Digester.createMD5Digest(contents);
	}

	/**
	 * Returns the (both sided inclusive) region if indexes into the statements
	 * list that represents the clone. This also includes the
	 * "artificial border", i.e. all statements directly before or after the
	 * clone that are artificial, are included. This is to ensure that tokens
	 * (such as closing braces) that are ignored by the clone detection are
	 * included to properly align to the AST.
	 */
	private Region determineStatementRegion(Clone clone,
			List<AlignerStatement> statements) {
		int startOffset = clone.getLocation().getRawStartOffset();
		int endOffset = clone.getLocation().getRawEndOffset();
		int startStatementIndex = 0;
		while (startStatementIndex < statements.size()
				&& (statements.get(startStatementIndex).isArtificial() || statements
						.get(startStatementIndex).getRawEndOffset() < startOffset)) {
			startStatementIndex += 1;
		}
		int endStatementIndex = Math.min(startStatementIndex,
				statements.size() - 1);
		while (endStatementIndex < statements.size() - 1
				&& (statements.get(endStatementIndex + 1).isArtificial() || statements
						.get(endStatementIndex + 1).getRawStartOffset() <= endOffset)) {
			endStatementIndex += 1;
		}

		while (startStatementIndex > 0
				&& statements.get(startStatementIndex - 1).isArtificial()) {
			startStatementIndex -= 1;
		}

		return new Region(startStatementIndex, endStatementIndex);
	}

	/**
	 * Returns the {@link AlignerStatement}s for the given uniform path if
	 * possible.
	 */
	private List<AlignerStatement> getAlignerStatementsForUniformPath(
			String uniformPath) throws ConQATException {
		List<AlignerStatement> statements = uniformPathToAlignerStatements
				.get(uniformPath);
		if (statements != null) {
			return statements;
		}

		return cacheStatementsAndUpdateUnits(uniformPath);
	}

	/**
	 * Caches the statements of the element identified by the given uniform path
	 * and updates the units key for this element to reflect the number of
	 * statements.
	 */
	private List<AlignerStatement> cacheStatementsAndUpdateUnits(
			String uniformPath) throws ConQATException {
		ITokenElement element = uniformPathToElement.get(uniformPath);
		if (element == null) {
			throw new ConQATException("No token element found for path "
					+ uniformPath);
		}

		List<AlignerStatement> statements = new ArrayList<AlignerStatement>();
		if (ShallowParserFactory.supportsLanguage(element.getLanguage())) {
			List<ShallowEntity> entities = ShallowParserFactory.parse(element,
					getLogger());
			insertStatements(entities, 0, element, statements);
			setNonArtificialStatementIndices(statements);
		} else {
			// fallback for languages without shallow parser is alignment to
			// tokens
			for (IToken token : element.getTokens(getLogger())) {
				statements.add(new AlignerStatement(Collections
						.singletonList(token), element, 0, false,
						EAlignerStatementType.PRIMITIVE));
			}
		}

		element.setValue(UnitProcessorBase.UNITS_KEY,
				countNonArtificialStatements(statements));

		uniformPathToAlignerStatements.put(uniformPath, statements);
		return statements;
	}

	/** Sets the non-artificial statement index for each statement. */
	private void setNonArtificialStatementIndices(
			List<AlignerStatement> statements) {
		int nonArtificialStatementIndex = 0;
		for (AlignerStatement statement : statements) {
			statement
					.setNonArtificialStatementIndex(nonArtificialStatementIndex);
			if (!statement.isArtificial()) {
				nonArtificialStatementIndex += 1;
			}
		}
	}

	/**
	 * Inserts the statements contained in the given entities into the provided
	 * list.
	 */
	private void insertStatements(List<ShallowEntity> entities, int depth,
			ITokenElement element, List<AlignerStatement> statements)
			throws ConQATException {
		for (ShallowEntity entity : entities) {
			insertStatements(entity, depth, element, statements);
		}
	}

	/**
	 * Inserts the statements contained in the given entity into the provided
	 * list.
	 */
	private void insertStatements(ShallowEntity entity, int depth,
			ITokenElement element, List<AlignerStatement> statements)
			throws ConQATException {
		// ignore defines, annotations, imports, etc.
		if (entity.getType() == EShallowEntityType.META) {
			return;
		}

		if (entity.getChildren().isEmpty()) {
			statements.add(new AlignerStatement(entity.includedTokens(),
					element, depth, false, EAlignerStatementType.PRIMITIVE));
			return;
		}

		ShallowEntity firstChild = entity.getChildren().get(0);
		ShallowEntity lastChild = CollectionUtils.getLast(entity.getChildren());

		statements.add(new AlignerStatement(entity.includedTokens().subList(0,
				firstChild.getRelativeStartTokenIndex()), element, depth, true,
				EAlignerStatementType.COMPOUND_START));

		insertStatements(entity.getChildren(), depth + 1, element, statements);

		statements.add(new AlignerStatement(entity.includedTokens().subList(
				lastChild.getRelativeEndTokenIndex(),
				entity.includedTokens().size()), element, depth, true,
				EAlignerStatementType.COMPOUND_END));
	}
}
