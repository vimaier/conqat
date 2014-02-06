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
package org.conqat.engine.html_presentation.javascript.third_party;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.html_presentation.javascript.JavaScriptFile.EType;
import org.conqat.engine.html_presentation.javascript.JavaScriptModuleBase;

/**
 * Module for the <a href="http://www.raphaeljs.com/">Raphael</a> library.
 * 
 * @author $Author: streitel$
 * @version $Rev: 38040 $
 * @ConQAT.Rating GREEN Hash: 2330BF9B95128FB67E3774E78E6973E7
 */
public class RaphaelModule extends JavaScriptModuleBase {

	/** Namespace used for identifying the main file. */
	public static final String NAMESPACE = "raphael";

	/** {@inheritDoc} */
	@Override
	protected void createJavaScriptFiles() throws ConQATException {
		addSimpleLibraryFromClassPath(NAMESPACE, EType.CODE_LIBRARY,
				"raphael.js");
	}
}
