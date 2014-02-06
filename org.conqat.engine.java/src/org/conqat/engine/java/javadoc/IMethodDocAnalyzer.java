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

import com.sun.javadoc.MethodDoc;

/**
 * Interface for analyzers that analyze method comments.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 71D1B7F7BCECE724784BD508F0FAF30A
 */
public interface IMethodDocAnalyzer extends ICommentAnalyzer {
	/**
	 * Analyze method. Findings should be created via the finding category that
	 * was provided by
	 * {@link #init(org.conqat.engine.commons.findings.FindingCategory)} and
	 * attached to the provided {@link IJavaElement}.
	 * 
	 * @param docElement
	 *            the method to analyze
	 * @param element
	 *            the class element that contains this method
	 */
	public void analyze(MethodDoc docElement, IJavaElement element)
			throws ConQATException;
}