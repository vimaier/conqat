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
package org.conqat.engine.java.library;

import java.lang.ref.WeakReference;

import org.conqat.engine.sourcecode.resource.ITokenElement;

/**
 * Key for identifying an element for the Java AST cache.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 47044 $
 * @ConQAT.Rating GREEN Hash: D170007698E72E98ADD9349551D5761B
 * 
 * @see org.conqat.engine.java.library.JavaLibrary
 * @see net.sourceforge.pmd.lang.java.ast.JavaParser
 */
/* package */class JavaASTCacheKey {

	/**
	 * We need a reference to the token element, but do not want to keep this
	 * key (which may live a long time in the cache) to stop the element from
	 * being collected by the GC later on. Thus we use a weak reference here.
	 */
	private final WeakReference<ITokenElement> elementRef;

	/** The path that identifies the element. */
	private final String uniformPath;

	/** Constructor. */
	public JavaASTCacheKey(ITokenElement element) {
		elementRef = new WeakReference<ITokenElement>(element);
		uniformPath = element.getUniformPath();
	}

	/**
	 * Returns the element (may be null if the weak reference to the element was
	 * lost).
	 */
	public ITokenElement getElement() {
		return elementRef.get();
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof JavaASTCacheKey)
				&& ((JavaASTCacheKey) obj).uniformPath.equals(uniformPath);
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return uniformPath.hashCode();
	}

}