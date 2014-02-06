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
package org.conqat.engine.cpp.dependency;

import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.lib.commons.collections.PairList;

/**
 * Interface for injecting additional dependencies based on parsing information.
 * The idea is to map identifiers (potential method calls) to unique strings,
 * i.e. matching read/write functions must be mapped to the same string by the
 * methods of this interface.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 41240 $
 * @ConQAT.Rating YELLOW Hash: B81C487C9556EAA725BEC8EE75A9BBA8
 */
public interface IDependencyInjector {

	/**
	 * Returns a unique identifier for a source call (or null if this is not a
	 * source call).
	 */
	String asNormalizedSourceCall(String text, ITokenElement element);

	/**
	 * Returns a unique identifier for a target call (or null if this is not a
	 * target call).
	 */
	String asNormalizedTargetCall(String text, ITokenElement element);

	/**
	 * Returns a dependency target for the given identifier text. Returns null
	 * if no direct dependency can be determined.
	 * 
	 * @return pairs of uniform path (target element) and message describing the
	 *         dependency. The element described by the uniform path is not
	 *         required to exist and may be artificial.
	 */
	PairList<String, String> getDirectDependencyTarget(String text,
			ITokenElement element);

	/** Returns the type of dependency created. */
	String getType();
}
