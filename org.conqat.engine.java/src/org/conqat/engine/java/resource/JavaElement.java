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
package org.conqat.engine.java.resource;

import java.nio.charset.Charset;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IContentAccessor;
import org.conqat.engine.resource.text.filter.base.ITextFilter;
import org.conqat.engine.sourcecode.resource.TokenElement;
import org.conqat.lib.commons.clone.DeepCloneException;
import org.conqat.lib.scanner.ELanguage;

/**
 * Default implementation of a Java element.
 * 
 * @author $Author: juergens $
 * @version $Rev: 35196 $
 * @ConQAT.Rating GREEN Hash: ACE47607B2078B47ECBECCF8A988C70A
 */
public class JavaElement extends TokenElement implements IJavaElement {

	/** The accessor for the byte code. */
	private final IContentAccessor byteCodeAccessor;

	/** The class name. */
	private final String className;

	/** The context used. */
	private final JavaContext context;

	/** Constructor. */
	public JavaElement(String className, IContentAccessor sourceAccessor,
			IContentAccessor byteCodeAccessor, Charset encoding,
			JavaContext context) {
		this(className, sourceAccessor, byteCodeAccessor, encoding, context,
				null);
	}

	/** Constructor. */
	public JavaElement(String className, IContentAccessor sourceAccessor,
			IContentAccessor byteCodeAccessor, Charset encoding,
			JavaContext context, ITextFilter filter) {
		super(sourceAccessor, encoding, ELanguage.JAVA, filter);
		this.className = className;
		this.byteCodeAccessor = byteCodeAccessor;
		this.context = context;

		context.updateContext(this, sourceAccessor.getLocation(),
				byteCodeAccessor.getLocation());
	}

	/** Copy constructor. */
	protected JavaElement(JavaElement other) throws DeepCloneException {
		super(other);
		className = other.className;
		byteCodeAccessor = other.byteCodeAccessor;
		// we don't deep clone this as the context is "mostly" immutable, i.e.
		// it cannot be change outside this package
		context = other.context;
	}

	/** {@inheritDoc} */
	@Override
	public byte[] getByteCode() throws ConQATException {
		return byteCodeAccessor.getContent();
	}

	/** {@inheritDoc} */
	@Override
	public String getByteCodeLocation() {
		return byteCodeAccessor.getLocation();
	}

	/** {@inheritDoc} */
	@Override
	public String getByteCodeUniformPath() {
		return byteCodeAccessor.getUniformPath();
	}

	/** {@inheritDoc} */
	@Override
	public JavaContext getJavaContext() {
		return context;
	}

	/** {@inheritDoc} */
	@Override
	public String getClassName() {
		return className;
	}

	/** {@inheritDoc} */
	@Override
	public IJavaElement[] getChildren() {
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public JavaElement deepClone() throws DeepCloneException {
		return new JavaElement(this);
	}
}