/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
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
package org.conqat.engine.java.ecj;

import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.java.resource.IJavaElement;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 38222 $
 * @ConQAT.Rating GREEN Hash: 0C500347C65B5E1A2A2BB9F882728E1F
 */
@AConQATProcessor(description = "Annotates each Java element with the list of "
		+ "methods called within the source code. The methods are stored"
		+ "as lists of string. The notation is <FQN>#<method signature>."
		+ "This processor computes the list of all method calls, while "
		+ "JavaMethodUsageAnnotator determines the set of distinct methods "
		+ "called, thus eliminating duplicates.")
public class JavaMethodCallAnnotator extends JavaMethodCallAnalyzerBase {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "List of all method calls, potentially including a "
			+ "method multiple times", type = "java.util.List<String>")
	public static final String KEY = "method calls";

	/** {@inheritDoc} */
	@Override
	protected void processMethodCall(MethodBinding binding,
			IJavaElement javaElement) {
		NodeUtils.getOrCreateStringList(javaElement, KEY).add(
				EcjUtils.methodBindingToString(binding));
	}

	/** {@inheritDoc} */
	@Override
	protected String[] getKeys() {
		return new String[] { KEY };
	}

}
