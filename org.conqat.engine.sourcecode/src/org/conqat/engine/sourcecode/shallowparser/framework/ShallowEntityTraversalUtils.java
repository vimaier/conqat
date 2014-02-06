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
package org.conqat.engine.sourcecode.shallowparser.framework;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.conqat.lib.commons.predicate.IPredicate;

/**
 * Utility methods for working with shallow entities.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 46378 $
 * @ConQAT.Rating GREEN Hash: 1EE5C09BA16F6296C02550C9A5CEC922
 */
public class ShallowEntityTraversalUtils {

	/** Lists all entities. */
	public static List<ShallowEntity> listAllEntities(
			Collection<ShallowEntity> entities) {
		return listEntitiesOfTypes(entities,
				EnumSet.allOf(EShallowEntityType.class));
	}

	/** Lists all entities of the given type. */
	public static List<ShallowEntity> listEntitiesOfType(
			Collection<ShallowEntity> entities, EShallowEntityType type) {
		return listEntitiesOfTypes(entities, EnumSet.of(type));
	}

	/** Lists all entities of the given types. */
	public static List<ShallowEntity> listEntitiesOfTypes(
			Collection<ShallowEntity> entities,
			final Set<EShallowEntityType> types) {
		return new CollectingVisitorBase() {
			@Override
			protected boolean collect(ShallowEntity entity) {
				return types.contains(entity.getType());
			}
		}.apply(entities);
	}

	/** Lists all entities that are selected by the given predicate. */
	public static List<ShallowEntity> selectEntities(
			Collection<ShallowEntity> entities,
			final IPredicate<ShallowEntity> predicate) {
		return new CollectingVisitorBase() {
			@Override
			protected boolean collect(ShallowEntity entity) {
				return predicate.isContained(entity);
			}
		}.apply(entities);
	}

	/**
	 * Returns the first incomplete entity found (or null). Unclosed entities
	 * correspond to parsing errors.
	 */
	private static ShallowEntity findIncompleteEntity(ShallowEntity entity) {
		if (!entity.isCompleted()) {
			return entity;
		}
		return findIncompleteEntity(entity.getChildren());
	}

	/**
	 * Returns the first incomplete entity found (or null). Unclosed entities
	 * correspond to parsing errors.
	 */
	public static ShallowEntity findIncompleteEntity(
			List<ShallowEntity> entities) {
		for (ShallowEntity entity : entities) {
			ShallowEntity incomplete = findIncompleteEntity(entity);
			if (incomplete != null) {
				return incomplete;
			}
		}
		return null;
	}

	/**
	 * Traverses the given collection of entities and returns a flat list of
	 * entities.
	 */
	public static List<ShallowEntity> getAllEntities(
			Collection<ShallowEntity> entities) {
		return new CollectingVisitorBase() {
			@Override
			protected boolean collect(ShallowEntity entity) {
				return true;
			}
		}.apply(entities);
	}

	/** Empty default implementation of {@link IShallowEntityVisitor}. */
	public static abstract class ShallowEntityVisitorBase implements
			IShallowEntityVisitor {
		/** {@inheritDoc} */
		@Override
		public boolean visit(ShallowEntity entity) {
			return true;
		}

		/** {@inheritDoc} */
		@Override
		public void endVisit(ShallowEntity entity) {
			// nothing
		}
	}

	/** Base class for visitors that collect shallow entities. */
	public static abstract class CollectingVisitorBase extends
			ShallowEntityVisitorBase {

		/** The collected entities. */
		private final List<ShallowEntity> entities = new ArrayList<ShallowEntity>();

		/** {@inheritDoc} */
		@Override
		public boolean visit(ShallowEntity entity) {
			if (collect(entity)) {
				entities.add(entity);
			}
			return true;
		}

		/**
		 * Template method that returns true if the entity should be collected.
		 */
		protected abstract boolean collect(ShallowEntity entity);

		/** Applies this collecting visitor and returns the collected result. */
		public List<ShallowEntity> apply(Collection<ShallowEntity> entities) {
			ShallowEntity.traverse(entities, this);
			return this.entities;
		}
	}
}
