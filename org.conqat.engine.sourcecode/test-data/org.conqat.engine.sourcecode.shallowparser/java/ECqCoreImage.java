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
package org.conqat.ide.core;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

/**
 * Enumeration that provides all images used in the application. This
 * enumeration used the image registry provided by Eclipse.
 * 
 * @author deissenb
 * @author $Author: hummelb $
 * @version $Rev: 35946 $
 * @ConQAT.Rating GREEN Hash: 727AF9481EBC388291E775A239A7CF6B
 */
public enum ECqCoreImage {
	/** ConQAT icon. */
	CONQAT,

	/** CQB icon. */
	CQB,

	/** CQR icon. */
	CQR,

	/** New Bundle Wizard */
	NEW_BUNDLE_WIZARD,

	/** Bundle icon */
	BUNDLE,

	/** New bundle icon (this is only referenced from plugin.xml) */
	NEW_BUNDLE;

	/**
	 * Register all images defined in this enumeration with the given image
	 * registry. This needs to be called only once.
	 */
	public static void registerImages(ImageRegistry registry) {
		for (ECqCoreImage image : values()) {
			registry.put(image.getImageName(), image.getImageDescriptor());
		}
	}

	/** Get image registry from {@link CqCoreActivator}. */
	private static ImageRegistry getRegistry() {
		return CqCoreActivator.getDefault().getImageRegistry();
	}

	/** Get image for enumeration element. */
	public Image getImage() {
		return getRegistry().get(getImageName());
	}

	/** Get image descriptor for enumeration element. */
	public ImageDescriptor getImageDescriptor() {
		return CqCoreActivator.getImageDescriptor(getImagePath());
	}

	/** Get image name. */
	private String getImageName() {
		return name().toLowerCase();
	}

	/** Get path to image. */
	private String getImagePath() {
		return "icons/" + getImageName() + ".png";
	}
}