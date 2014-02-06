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
package org.conqat.engine.architecture.assessment.shared;

import org.conqat.lib.commons.assertion.CCSMAssert;

/**
 * A dependency between two types. This is a low-level dependency on which
 * dependencies between components are based. Note, that we use the term
 * <em>type</em> for all elements that are mapped to components. In fact, these
 * elements do not necessarily have to be types in a programming language sense,
 * but may be arbitrary objects that can be identified using a string.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 41988 $
 * @ConQAT.Rating GREEN Hash: 553B40F47CEE996D543DC68124E39362
 */
public class TypeDependency {

	/** The source type of the dependency. */
	private String source;

	/** The target type of the dependency. */
	private String target;

	/**
	 * Constructs a new type-level dependency. Note that this dependency does
	 * not necessarily exist, i.e., the target type may not be included in the
	 * source type's dependencies. This is the case when type-level dependencies
	 * are tolerated in the architecture specification but do not exist in the
	 * implementation.
	 */
	public TypeDependency(String source, String target) {
		CCSMAssert.isNotNull(source);
		CCSMAssert.isNotNull(target);
		this.source = source;
		this.target = target;
	}

	/**
	 * Tests if this type-level dependency equals the given type-level
	 * dependency. Both dependencies are equal if their respective sources and
	 * targets are equal.
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof TypeDependency)) {
			return false;
		}

		TypeDependency other = (TypeDependency) o;

		return source.equals(other.getSource())
				&& target.equals(other.getTarget());
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return source.hashCode() + target.hashCode();
	}

	/** Retrieves the source of the dependency. */
	public String getSource() {
		return source;
	}

	/** Retrieves the target of the dependency. */
	public String getTarget() {
		return target;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return source + " -> " + target;
	}
}
