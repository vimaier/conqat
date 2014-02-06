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
package org.conqat.engine.java.filter;

import org.apache.bcel.classfile.JavaClass;
import org.conqat.engine.commons.filter.FilterBase;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.java.resource.IJavaElement;
import org.conqat.engine.java.resource.IJavaResource;
import org.conqat.engine.java.resource.JavaElementUtils;

/**
 * Base class for Java class filters.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 45102 $
 * @ConQAT.Rating GREEN Hash: A3D197B3C911FF71C571A622CC2A7E9D
 */
public abstract class JavaClassFilterBase extends FilterBase<IJavaResource> {

	/** {@inheritDoc} */
	@Override
	protected final boolean isFiltered(IJavaResource resource) {
		if (resource instanceof IJavaElement) {
			IJavaElement classElement = (IJavaElement) resource;
			try {
				JavaClass analyzedClass = JavaElementUtils
						.obtainBcelClass(classElement);
				return isFiltered(classElement, analyzedClass);
			} catch (ConQATException ex) {
				getLogger()
						.warn("Could not analyze class: " + resource.getId());
			}
		}

		return isInverted();
	}

	/**
	 * Template method for filtering java classes.
	 * 
	 * @param classElement
	 *            the class element
	 * @param analyzedClass
	 *            the corresponding BCEL class object.
	 * @return <code>true</code> if element should be filtered,
	 *         <code>false</code> if not
	 */
	protected abstract boolean isFiltered(IJavaElement classElement,
			JavaClass analyzedClass);

}