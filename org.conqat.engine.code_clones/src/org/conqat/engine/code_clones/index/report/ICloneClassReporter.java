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

import java.util.Date;

import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.code_clones.detection.suffixtree.ICloneReporter;
import org.conqat.engine.core.core.ConQATException;

/**
 * Interface used for reporting clone classes found by the index-based clone
 * detector. The difference to the {@link ICloneReporter} is that via this
 * interface entire clone classes are reported (not individual clones).
 * 
 * @author hummelb
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: B94A90511712C12D66C616BB7DC371C0
 */
public interface ICloneClassReporter {

	/**
	 * Reports a clone class. The passed object is not used by the caller of
	 * this method, so the method may modify or keep the passed object.
	 */
	void report(CloneClass cloneClass) throws ConQATException;

	/** Provides IDs used for clones and clone classes. */
	long provideId();

	/** Returns the birth date that should be used. */
	Date getBirthDate();
}