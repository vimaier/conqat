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
package org.conqat.engine.persistence.index;

import org.conqat.engine.persistence.store.IStore;
import org.conqat.lib.commons.assessment.Assessment;

/**
 * Index that stores assessments.
 * 
 * @author $Author: heineman $
 * @version $Rev: 37965 $
 * @ConQAT.Rating GREEN Hash: C9BC671E0D6A5EE96CD063ADB7C743D5
 */
public class AssessmentIndex extends
		SerializationBasedValueIndexBase<Assessment> {

	/** Constructor */
	public AssessmentIndex(IStore store) {
		super(store);
	}
}
