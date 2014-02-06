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
package org.conqat.engine.commons.collections;

import java.util.Collection;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.commons.node.IRemovableConQATNode;
import org.conqat.engine.commons.node.SetNode;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * A simple processor that creates a node hierarchy from any collection.
 * 
 * @author Florian Deissenboeck
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: 9691194407642FE50AF518DF795FB9F9
 */
@AConQATProcessor(description = "A simple processor that creates a node hierarchy from a collection.")
public class CollectionConverter extends ConQATProcessorBase {

	/** The collection. */
	private Collection<?> collection;

	/** Set the collection to measure. */
	@AConQATParameter(name = ConQATParamDoc.INPUT_NAME, minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "The collection to be converted.")
	public void setCollection(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC)
			Collection<?> collection) {
		this.collection = collection;
	}

	/** {@inheritDoc} */
	@Override
	public IRemovableConQATNode process() {
		SetNode<Object> result = new SetNode<Object>("<root>");
		for (Object o : collection) {
			result.addChild(new SetNode<Object>(o));
		}
		return result;
	}
}