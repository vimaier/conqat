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
import java.util.Collection;
import java.util.List;

import org.conqat.engine.sourcecode.shallowparser.TokenStreamUtils;
import org.conqat.engine.sourcecode.shallowparser.framework.EShallowEntityType;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntityTraversalUtils;
import org.conqat.lib.commons.collections.ILookahead;
import org.conqat.lib.commons.error.NeverThrownRuntimeException;
import org.conqat.lib.scanner.ELanguage;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;

/**
 * Utility methods for dealing with C++ code.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43899 $
 * @ConQAT.Rating GREEN Hash: 12261B524064A5027BC275CF4FEECD41
 */
public class CppUtils {

	/** Returns whether an entity is a class or struct. */
	public static boolean isClassOrStruct(ShallowEntity entity) {
		return entity.getType() == EShallowEntityType.TYPE
				&& ("class".equals(entity.getSubtype()) || "struct"
						.equals(entity.getSubtype()));
	}

	/** Returns true for entities of type TYPE and subtype class. */
	public static boolean isRealClass(ShallowEntity entity) {
		return entity.getType() == EShallowEntityType.TYPE
				&& entity.getSubtype().equals("class");
	}

	/** Lists all entities corresponding to classes in C++ (class and struct). */
	public static List<ShallowEntity> listClassesAndStructs(
			Collection<ShallowEntity> entities) {
		return new ShallowEntityTraversalUtils.CollectingVisitorBase() {
			@Override
			protected boolean collect(ShallowEntity entity) {
				return isClassOrStruct(entity);
			}
		}.apply(entities);
	}

	/** Lists all entities of type class (i. e. no structs). */
	public static List<ShallowEntity> listRealClasses(
			Collection<ShallowEntity> entities) {
		return new ShallowEntityTraversalUtils.CollectingVisitorBase() {
			@Override
			protected boolean collect(ShallowEntity entity) {
				return isRealClass(entity);
			}
		}.apply(entities);
	}

	/** List all top level classes. */
	public static List<ShallowEntity> listToplevelClasses(
			Collection<ShallowEntity> entities) {
		return new ShallowEntityTraversalUtils.CollectingVisitorBase() {
			@Override
			protected boolean collect(ShallowEntity entity) {
				return isToplevelClass(entity);
			}
		}.apply(entities);
	}

	/** Returns true for classes that are not nested inside other classes. */
	public static boolean isToplevelClass(ShallowEntity entity) {
		ShallowEntity parent = entity.getParent();
		boolean isToplevel = parent == null
				|| parent.getType() == EShallowEntityType.MODULE;
		return isToplevel && isClassOrStruct(entity);
	}

	/** Lists all namespaces. */
	public static List<ShallowEntity> listNamespaces(
			Collection<ShallowEntity> entities) {
		return new ShallowEntityTraversalUtils.CollectingVisitorBase() {
			@Override
			protected boolean collect(ShallowEntity entity) {
				return entity.getType() == EShallowEntityType.META
						&& entity.getSubtype().equals("namespace");
			}
		}.apply(entities);
	}

	/**
	 * Returns true for entities of type METHOD and subtype
	 * "destructor declaration".
	 */
	public static boolean isDestructor(ShallowEntity entity) {
		return entity.getType() == EShallowEntityType.METHOD
				&& (entity.getSubtype().startsWith("destructor"));
	}

	/**
	 * Returns the index of the end of the preceding statement in the token
	 * stream. Returns <code>0</code> if the given token belongs to the first
	 * statement.
	 */
	public static int findPrecedingStatementEndIndex(List<IToken> tokens,
			int fromIndex) {
		for (int index = fromIndex; index >= 0; index--) {
			if (isStatementEnd(tokens.get(index))) {
				return index;
			}
		}

		return 0;
	}

	/** Returns if the given token denotes the ending of a statement. */
	public static boolean isStatementEnd(IToken token) {
		// C++ doesn't need Lookahead, so use null
		return ELanguage.CPP.getStatementOracle().isEndOfStatementToken(
				token.getType(),
				(ILookahead<IToken, NeverThrownRuntimeException>) null);
	}

	/**
	 * Filters out all non-<code>static const</code> attributes from the given
	 * collection.
	 * 
	 * @param attributeEntities
	 *            A collection of attributes.
	 */
	public static List<ShallowEntity> listConstants(
			Collection<ShallowEntity> attributeEntities) {
		List<ShallowEntity> constants = new ArrayList<ShallowEntity>();
		for (ShallowEntity attribute : attributeEntities) {
			if (isStaticConst(attribute)) {
				constants.add(attribute);
			}
		}
		return constants;
	}

	/**
	 * Returns <code>true</code> if the given attribute declaration contains the
	 * <code>static</code> and <code>const</code> modifiers.
	 */
	public static boolean isStaticConst(ShallowEntity entity) {
		List<IToken> tokens = entity.includedTokens();
		return TokenStreamUtils.tokenStreamContains(tokens, ETokenType.STATIC)
				&& TokenStreamUtils.tokenStreamContains(tokens,
						ETokenType.CONST);
	}

	/** Lists all entities of type attribute. */
	public static List<ShallowEntity> listAttributes(
			Collection<ShallowEntity> entities) {
		return ShallowEntityTraversalUtils.listEntitiesOfType(entities,
				EShallowEntityType.ATTRIBUTE);
	}
}
