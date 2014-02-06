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
package org.conqat.engine.code_clones.core;

import java.util.ArrayList;
import java.util.Collection;

import org.conqat.lib.commons.clone.IDeepCloneable;

/**
 * List of clone classes that can be passed between processors w/o the need for
 * generics.
 * 
 * @author juergens
 * @author $Author: hummelb $
 * @version $Rev: 36296 $
 * @ConQAT.Rating GREEN Hash: A315E8F256B019E6EDA27EF350F60189
 */
public class CloneClassList extends ArrayList<CloneClass> implements
		IDeepCloneable {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** Constructor */
	public CloneClassList(Collection<CloneClass> values) {
		super(values);
	}

	/**
	 * {@inheritDoc}.
	 * 
	 * We do not deepclone {@link CloneClass}es here to save memory.
	 */
	@Override
	public CloneClassList deepClone() {
		return new CloneClassList(this);
	}
}