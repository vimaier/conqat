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
package org.conqat.engine.sourcecode.analysis.cpp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;

import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.shallowparser.framework.EShallowEntityType;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;

/**
 * {@ConQATDoc}
 * 
 * @author $Author: goede $
 * @version $Rev: 41726 $
 * @ConQAT.Rating GREEN Hash: 979FCD69315CFECAB39EF251B011A18C
 */
@AConQATProcessor(description = "Creates findings for public attributes in C++ code.")
public class CppPublicAttributesAnalyzer extends CppFindingAnalyzerBase {

	/** {@inheritDoc} */
	@Override
	protected void analyzeShallowEntities(ITokenElement element,
			List<ShallowEntity> entities) throws ConQATException {

		for (ShallowEntity clazz : CppUtils.listRealClasses(entities)) {
			NavigableMap<Integer, ETokenType> visibilityModifiers = listVisibilityModifiers(clazz);
			for (ShallowEntity child : clazz.getChildren()) {
				if (child.getType() == EShallowEntityType.ATTRIBUTE) {
					analyzeAttribute(element, child, visibilityModifiers);
				}
			}
		}
	}

	/**
	 * Creates and returns a map of the positions of the visibility modifiers
	 * <code>public</code>, <code>protected</code>, and <code>private</code> for
	 * a given class. The map does not contain the modifier positions of nested
	 * classes.
	 */
	private static NavigableMap<Integer, ETokenType> listVisibilityModifiers(
			ShallowEntity clazz) {
		NavigableMap<Integer, ETokenType> modifierPositionsMap = new TreeMap<Integer, ETokenType>();
		int tokenIndex = 0;
		for (IToken token : clazz.includedTokens()) {
			switch (token.getType()) {
			case PUBLIC:
			case PROTECTED:
			case PRIVATE:
				modifierPositionsMap.put(
						tokenIndex + clazz.getStartTokenIndex(),
						token.getType());
			}
			tokenIndex++;
		}
		return removeModifiersFromNestedClasses(clazz, modifierPositionsMap);
	}

	/**
	 * Removes modifiers inside nested classes because otherwise we would get a
	 * wrong visibility for attributes declared after a nested class.
	 * 
	 * @returns A reference to the given map.
	 */
	private static NavigableMap<Integer, ETokenType> removeModifiersFromNestedClasses(
			ShallowEntity clazz,
			NavigableMap<Integer, ETokenType> modifierPositionsMap) {
		List<ShallowEntity> nestedClasses = CppUtils
				.listClassesAndStructs(clazz.getChildren());
		for (ShallowEntity entity : nestedClasses) {
			SortedMap<Integer, ETokenType> irrelevantModifiers = modifierPositionsMap
					.subMap(entity.getStartTokenIndex(),
							entity.getEndTokenIndex());
			modifierPositionsMap.keySet().removeAll(
					new ArrayList<Integer>(irrelevantModifiers.keySet()));
		}
		return modifierPositionsMap;
	}

	/** Analyzes a single statement for rule violation. */
	private void analyzeAttribute(ITokenElement element, ShallowEntity entity,
			NavigableMap<Integer, ETokenType> visibilityPositionsMap)
			throws ConQATException {

		if (!CppUtils.isStaticConst(entity)
				&& getVisibilityTokenType(entity, visibilityPositionsMap) == ETokenType.PUBLIC) {
			createFindingForEntityStart("Attribute declared public", element,
					entity);
		}
	}

	/**
	 * Returns the {@link ETokenType} that defines the visibility of the given
	 * {@link ShallowEntity}.
	 */
	private ETokenType getVisibilityTokenType(ShallowEntity entity,
			NavigableMap<Integer, ETokenType> visibilityPositionsMap) {
		Entry<Integer, ETokenType> modifierEntry = visibilityPositionsMap
				.lowerEntry(entity.getStartTokenIndex());
		if (modifierEntry == null) {
			return ETokenType.PRIVATE;
		}
		return modifierEntry.getValue();
	}

	/** {@inheritDoc} */
	@Override
	protected String getFindingGroupName() {
		return "Public attributes";
	}
}
