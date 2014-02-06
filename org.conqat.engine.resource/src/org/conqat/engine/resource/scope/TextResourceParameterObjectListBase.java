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
package org.conqat.engine.resource.scope;

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.resource.text.ITextResource;
import org.conqat.lib.commons.clone.CloneUtils;
import org.conqat.lib.commons.clone.DeepCloneException;

/**
 * Base class for parameter objects that carry a list of values.
 * 
 * @param <T>
 *            Type of objects in the list
 * 
 * @author $Author: juergens $
 * @version $Rev: 35198 $
 * @ConQAT.Rating GREEN Hash: C645F90089F694868BF3EC6D86D6D2C2
 */
public abstract class TextResourceParameterObjectListBase<T> extends
		TextResourceParameterObjectBase {

	/** List parameter */
	private List<T> list;

	/**
	 * Default constructor
	 * 
	 * @param root
	 *            Root of the resource tree
	 * @param list
	 *            List of data elements
	 */
	public TextResourceParameterObjectListBase(ITextResource root, List<T> list) {
		super(root);
		this.list = new ArrayList<T>(list);
	}

	/** Copy constructor */
	@SuppressWarnings("unchecked")
	protected TextResourceParameterObjectListBase(
			TextResourceParameterObjectListBase<T> element)
			throws DeepCloneException {

		super(element.getRoot().deepClone());
		list = (List<T>) CloneUtils.cloneAsDeepAsPossible(element.list);
	}

	/** Gets the list */
	public List<T> getList() {
		return list;
	}

	/** Sets the list */
	public void setList(List<T> list) {
		this.list = new ArrayList<T>(list);
	}

}