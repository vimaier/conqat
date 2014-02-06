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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.html_presentation.javascript.JavaScriptFile.EType;
import org.conqat.engine.html_presentation.javascript.JavaScriptModuleBase;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Module for the <a href="http://code.google.com/p/flot/">Flot</a> charting
 * library.
 * 
 * @author $Author: streitel$
 * @version $Rev: 45378 $
 * @ConQAT.Rating GREEN Hash: 3B0E11FF00870EC6052D05DA169E7565
 */
public class FlotModule extends JavaScriptModuleBase {

	/** Namespace used for identifying the main file. */
	public static final String NAMESPACE = "flot";

	/** {@inheritDoc} */
	@Override
	protected void createJavaScriptFiles() throws ConQATException {
		String mainFile = "jquery.flot.js";
		String resizeFile = "jquery.flot.resize.js";
		List<String> allFlotFiles = Arrays.asList(mainFile,
				"jquery.flot.crosshair.js", "jquery.flot.navigate.js",
				"jquery.flot.selection.js", "jquery.flot.stack.js",
				"jquery.flot.pie.js", "jquery.flot.time.js", resizeFile);

		for (String name : allFlotFiles) {
			// all flot plugins depend on flot main, but flot depends on jQuery
			String dependency = mainFile;
			if (name.equals(mainFile)) {
				dependency = JQueryModule.NAMESPACE;
			}

			if (name.equals(resizeFile)) {
				// we need to patch the flot resize plugin to work
				// asynchronously
				String content = loadScript(name);
				content = patchResizePlugin(content);
				addJavaScriptFileFromText(EType.CODE_LIBRARY, name, content,
						Collections.singletonList(name),
						Collections.singletonList(dependency));
			} else {
				addSimpleLibraryFromClassPath(name, EType.CODE_LIBRARY, name,
						dependency);
			}
		}

		// add empty dummy script that depends on all other files and thus pulls
		// them in
		addJavaScriptFileFromText(EType.CODE_LIBRARY, NAMESPACE, "",
				Collections.singletonList(NAMESPACE), allFlotFiles);
	}

	/**
	 * Patches the resize plugin by inserting an asynchronous call in the resize
	 * handler. The reason for patching here is to be more robust by version
	 * changes.
	 */
	private String patchResizePlugin(String content) {
		StringBuilder builder = new StringBuilder();

		boolean foundStart = false;
		boolean foundEnd = false;
		for (String line : StringUtils.splitLinesAsList(content)) {
			builder.append(line);
			builder.append(StringUtils.CR);

			if (!foundStart && line.contains("function onResize() {")) {
				foundStart = true;
				builder.append("setTimeout (function () {");
				builder.append(StringUtils.CR);
			} else if (!foundEnd && line.contains("plot.draw();")) {
				foundEnd = true;
				builder.append("}, 0);");
				builder.append(StringUtils.CR);
			}
		}

		CCSMAssert.isTrue(foundStart && foundEnd,
				"Patching the resize script failed!");

		return builder.toString();
	}
}
