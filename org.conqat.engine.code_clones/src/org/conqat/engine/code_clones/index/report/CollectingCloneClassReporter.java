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
package org.conqat.engine.code_clones.index.report;

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.core.core.ConQATException;

/**
 * An {@link ICloneClassReporter} that collects its results in a list.
 * 
 * @author hummelb
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: F57EB701BEF9A880C52D962A70C5CF6A
 */
public class CollectingCloneClassReporter extends CloneClassReporterBase {

	/** The reported clone classes. */
	private final List<CloneClass> cloneClasses = new ArrayList<CloneClass>();

	/** {@inheritDoc} */
	@SuppressWarnings("unused")
	@Override
	public void report(CloneClass cloneClass) throws ConQATException {
		cloneClasses.add(cloneClass);
	}

	/**
	 * Returns the clone classes found so far. This list is not unmodifiable, so
	 * the caller may also reset this list after reading.
	 */
	public List<CloneClass> getCloneClasses() {
		return cloneClasses;
	}
}