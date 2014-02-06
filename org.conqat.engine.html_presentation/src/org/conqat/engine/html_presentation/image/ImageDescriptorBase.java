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
package org.conqat.engine.html_presentation.image;

/**
 * Base class for image descriptors.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating RED Hash: 2B446DC7E60CB7586E796DE3A25E4DAF
 */
public abstract class ImageDescriptorBase implements IImageDescriptor {

	/** Icon name. */
	private final String iconName;

	/**
	 * Constructor. TODO (LH) Document parameter icon name. Where will the icon
	 * be looked for ?
	 */
	protected ImageDescriptorBase(String iconName) {
		this.iconName = iconName;
	}

	/** {@inheritDoc} */
	@Override
	public String getIconName() {
		return iconName;
	}

	/** {@inheritDoc} */
	@Override
	public IImageDescriptor deepClone() {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public Object getSummary() {
		return null;
	}
}