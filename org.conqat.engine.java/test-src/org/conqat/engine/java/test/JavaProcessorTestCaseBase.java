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
package org.conqat.engine.java.test;

import java.io.File;

import org.conqat.engine.java.resource.IJavaResource;
import org.conqat.engine.java.resource.JavaElementFactory;
import org.conqat.engine.java.resource.JavaResourceSelector;
import org.conqat.engine.resource.IContentAccessor;
import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.scope.filesystem.FileSystemScope;
import org.conqat.engine.sourcecode.resource.TokenTestCaseBase;

/**
 * Base class for tests working with Java processors.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: 5B76EF3B8D3E551C5E01668D1E01770D
 */
public abstract class JavaProcessorTestCaseBase extends TokenTestCaseBase {

	/**
	 * Creates a token scope from a directory, i.e. a hierarchy of text
	 * elements/containers. The pattern arrays may be null.
	 */
	public IJavaResource createJavaScope(File sourceCodeDirectory,
			File byteCodeDirectory) throws Exception {
		IContentAccessor[] byteCode = (IContentAccessor[]) executeProcessor(
				FileSystemScope.class, "(root=(dir='", byteCodeDirectory
						.getAbsolutePath(),
				"'), project=(name=TEST), include=(pattern='**/*.class'))");
		Object factory = executeProcessor(JavaElementFactory.class,
				"('byte-code'=(ref=", byteCode, "))");
		IResource resource = createScope(sourceCodeDirectory,
				new String[] { "**/*.java" }, null, factory);
		return (IJavaResource) executeProcessor(JavaResourceSelector.class,
				"(input=(ref=", resource, "))");
	}
}