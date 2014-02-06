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
package org.conqat.engine.java.javadoc;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.java.resource.IJavaElement;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ProgramElementDoc;

/**
 * Base class for analyzer that work for all comment types.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: EE7F495ACF50F38C5B8C9E5411D4093A
 */
/* package */abstract class ProgramElementDocAnalyzerBase extends
		CommentAnalyzerBase implements IProgramElementDocAnalyzer {

	/** {@inheritDoc} */
	@Override
	public IProgramElementDocAnalyzer process() {
		return this;
	}

	/** Forwards to {@link #analyze(ProgramElementDoc, IJavaElement)}. */
	@Override
	public void analyze(MethodDoc docElement, IJavaElement element)
			throws ConQATException {
		analyze((ProgramElementDoc) docElement, element);
	}

	/** Forwards to {@link #analyze(ProgramElementDoc, IJavaElement)}. */
	@Override
	public void analyze(ClassDoc docElement, IJavaElement element)
			throws ConQATException {
		analyze((ProgramElementDoc) docElement, element);

	}

	/** Forwards to {@link #analyze(ProgramElementDoc, IJavaElement)}. */
	@Override
	public void analyze(FieldDoc docElement, IJavaElement element)
			throws ConQATException {
		analyze((ProgramElementDoc) docElement, element);
	}

	/** Template method for analyzing a comment. */
	public abstract void analyze(ProgramElementDoc docElement,
			IJavaElement element) throws ConQATException;

}