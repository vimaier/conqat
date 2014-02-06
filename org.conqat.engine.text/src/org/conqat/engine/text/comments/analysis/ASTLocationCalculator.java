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
package org.conqat.engine.text.comments.analysis;

import java.util.List;

import org.conqat.engine.sourcecode.shallowparser.framework.EShallowEntityType;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.scanner.IToken;

/**
 * Helper class to transform the ast location of a comment into a number, i.e. 0
 * for coypright and header comments, 1 for interface comments, and 2 for inline
 * comments.
 * 
 * @author $Author: steidl $
 * @version $Rev: 45905 $
 * @ConQAT.Rating GREEN Hash: 93D2970AF42B77A12553935DD96FF03F
 */
public class ASTLocationCalculator {

	/** AST location for header and copyright comments */
	public static final int HEADER_LOCATION = 0;

	/** AST location for interface comments */
	public static final int INTERFACE_LOCATION = 1;

	/** AST location for inline comments */
	public static final int INLINE_LOCATION = 2;

	/**
	 * Returns the location of the comment in the AST, represented as a number.
	 * The comment is represented by its token position in the given token list.
	 * 
	 * Returns 0 if the comment is outside of a class, 1 if the comment is
	 * inside a class, and 2 if the comment is inside a method.
	 * 
	 * @param commentToken
	 *            the comment token for which the ast location number is
	 *            calculated
	 * @param entities
	 *            shallow parsed entities for the tokens
	 */
	public static int getASTLocationAsNumber(IToken commentToken,
			List<ShallowEntity> entities) {

		if (!hasNonMetaEntities(entities)) {
			// this is e.g. a file that is completely commented out.
			return HEADER_LOCATION;
		}

		return traverseEntities(entities, commentToken);
	}

	/**
	 * Returns whether the given list of entities contains any non-meta
	 * entities.
	 */
	private static boolean hasNonMetaEntities(List<ShallowEntity> entities) {
		for (ShallowEntity entity : entities) {
			if (entity.getType() != EShallowEntityType.META) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Traverses all entities and returns the AST location for the given comment
	 * token.
	 */
	private static int traverseEntities(List<ShallowEntity> entities,
			IToken commentToken) {
		ShallowEntity previous = null;
		for (ShallowEntity entity : entities) {
			if (entity.getType() == EShallowEntityType.META) {
				// we skip meta entities, as they are not target of comments
				continue;
			}

			if (commentToken.getEndOffset() < entity.getStartOffset()) {
				return determineLocationForReferenceEntity(entity);
			}
			if (commentToken.getEndOffset() < entity.getEndOffset()) {
				if (hasNonMetaEntities(entity.getChildren())) {
					return traverseEntities(entity.getChildren(), commentToken);
				}
				// comments in leaf entities are always inline, as they are
				// either in statements or in locations where no interface
				// comments are expected (e.g. between the class keyword and
				// the class name).
				return INLINE_LOCATION;
			}
			previous = entity;
		}

		CCSMAssert
				.isTrue(previous != null,
						"May not call with empty entities list or entities list containing only meta entities.");
		return determineLocationForReferenceEntity(previous);
	}

	/**
	 * Handles the case that the comment token is between two entities of the
	 * AST. Each of the entities may be null to indicate that the comment is at
	 * the begin or end of the file. But not both of the entities may be null.
	 */
	private static int determineLocationForReferenceEntity(
			ShallowEntity referenceEntity) {
		switch (referenceEntity.getType()) {
		case ATTRIBUTE:
		case METHOD:
			return INTERFACE_LOCATION;
		case STATEMENT:
			return INLINE_LOCATION;
		case MODULE:
		case TYPE:
			return HEADER_LOCATION;
		case META:
			throw new AssertionError(
					"May not call with META entity as reference!");
		default:
			throw new AssertionError("Unknown entity type: "
					+ referenceEntity.getType());
		}
	}
}
