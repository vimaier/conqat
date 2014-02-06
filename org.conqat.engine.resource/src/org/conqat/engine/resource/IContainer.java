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
package org.conqat.engine.resource;

/**
 * This interface describes resources that can contain other resources and do
 * <b>not</b> provide content.
 * 
 * @author deissenb
 * @author $Author: juergens $
 * @version $Rev: 35198 $
 * @ConQAT.Rating GREEN Hash: AEF589123F079CA97B77C34DA683B209
 */
public interface IContainer extends IResource {

	/** Remove child from this container. */
	void removeChild(IResource resource);

	/**
	 * Returns the name of a child element. The element must not be actually a
	 * child, but will be treated as if the element would be in the context of
	 * this container. This method allows different representations of a child
	 * based on the context (i.e. the container used).
	 */
	String getElementName(IElement element);

	/**
	 * Returns the id of a child element. The element must not be actually a
	 * child, but will be treated as if the element would be in the context of
	 * this container. This method allows different representations of a child
	 * based on the context (i.e. the container used).
	 */
	String getElementId(IElement element);
}