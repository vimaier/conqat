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
package org.conqat.engine.graph.nodes;

import org.conqat.lib.commons.clone.CloneUtils;
import org.conqat.lib.commons.clone.DeepCloneException;
import edu.uci.ics.jung.utils.UserDataContainer;

/**
 * A copy action that uses smart cloning, i.e. clone as deep as possible. This
 * class works in a singleton fashion, as it is immutable and might be used in
 * many places.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 7BD0E8D9F8BB097EE6C825CD499DA351
 */
public class DeepCloneCopyAction implements UserDataContainer.CopyAction {

	/** Shared instance. */
	private static DeepCloneCopyAction instance = null;

	/** Returns the shared instance of this class. */
	public static DeepCloneCopyAction getInstance() {
		if (instance == null) {
			instance = new DeepCloneCopyAction();
		}
		return instance;
	}

	/** Private constructor. */
	private DeepCloneCopyAction() {
		// prevents init
	}

	/** {@inheritDoc} */
	@Override
	public Object onCopy(Object value, UserDataContainer arg1,
			UserDataContainer arg2) {
		try {
			return CloneUtils.cloneAsDeepAsPossible(value);
		} catch (DeepCloneException e) {
			throw new IllegalStateException(e);
		}
	}
}