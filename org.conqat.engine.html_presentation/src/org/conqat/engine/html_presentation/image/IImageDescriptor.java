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

import java.awt.Dimension;
import java.awt.Graphics2D;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.clone.DeepCloneException;
import org.conqat.lib.commons.clone.IDeepCloneable;

/**
 * Generic image descriptor.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 24AA4278A42D261ADB3745D977FC2206
 */
public interface IImageDescriptor extends IDeepCloneable {

	/** Get preferred size. */
	public Dimension getPreferredSize() throws ConQATException;

	/** Draw the image. */
	public void draw(Graphics2D graphics, int width, int height)
			throws ConQATException;

	/**
	 * Checks if output to vector formats like PDF is supported. Technically,
	 * all descriptors can be written to PDF. However, some creators, e.g. the
	 * one for tree maps, draw individual pixels and are, hence, not well-suited
	 * for vector output.
	 */
	public boolean isVectorFormatSupported();

	/**
	 * Image descriptors must be immutable. Hence, this method should return the
	 * <code>this</code>.
	 */
	@Override
	public IImageDescriptor deepClone() throws DeepCloneException;

	/** Obtain the name of the icon used for the page containing this image. */
	public String getIconName();

	/**
	 * Obtain the tooltip descriptor. This may be null if no tool tips are
	 * required.
	 */
	public ITooltipDescriptor<Object> getTooltipDescriptor(int width, int height)
			throws ConQATException;

	/**
	 * Returns the summary for this descriptor. This is used to store summaries
	 * for assessment results. This may return null.
	 */
	public Object getSummary();
}