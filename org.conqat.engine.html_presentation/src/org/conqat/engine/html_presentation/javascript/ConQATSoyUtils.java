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
package org.conqat.engine.html_presentation.javascript;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.html_presentation.javascript.JavaScriptFile.EType;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.collections.CollectionUtils;

import com.google.template.soy.SoyFileSet;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.jssrc.SoyJsSrcOptions;
import com.google.template.soy.jssrc.SoyJsSrcOptions.CodeStyle;
import com.google.template.soy.shared.SoyGeneralOptions;

/**
 * Utility methods for compiling SOY (aka closure templates).
 * 
 * @author $Author: deissenb $
 * @version $Rev: 39862 $
 * @ConQAT.Rating GREEN Hash: 01D070F329A1EFA979487A20FCE39064
 */
/* package */class ConQATSoyUtils {

	/**
	 * Replaces closure templates (aka Soy) with the compiled JavaScript form.
	 * 
	 * @param files
	 *            the files containing the templates. The templates will be
	 *            replaced during this process.
	 */
	public static void compileSoy(Map<String, JavaScriptFile> files)
			throws ConQATException {
		List<String> names = new ArrayList<String>();
		SoyFileSet.Builder builder = new SoyFileSet.Builder(
				new SoyGeneralOptions());
		for (JavaScriptFile file : files.values()) {
			if (file.getType() == EType.CLOSURE_TEMPLATE) {
				names.add(file.getName());
				builder.add(file.getContent(), file.getName());
			}
		}

		if (names.isEmpty()) {
			return;
		}

		try {
			List<String> sources = builder.build().compileToJsSrc(
					createSoyJsOptions(), null);
			CCSMAssert.isTrue(names.size() == sources.size(),
					"Contract is to generate code for each file!");
			replaceTemplateWithJavaScript(files, names, sources);
		} catch (SoySyntaxException e) {
			throw new ConQATException(
					"Compilation of closure templates failed! "
							+ e.getMessage(), e);
		}
	}

	/**
	 * Creates the options to be used for Soy compilation (Soy = closure
	 * templates).
	 */
	private static SoyJsSrcOptions createSoyJsOptions() {
		SoyJsSrcOptions jsSrcOptions = new SoyJsSrcOptions();
		jsSrcOptions.setCodeStyle(CodeStyle.STRINGBUILDER);
		jsSrcOptions.setShouldDeclareTopLevelNamespaces(true);
		jsSrcOptions.setShouldGenerateJsdoc(true);
		jsSrcOptions.setShouldProvideRequireSoyNamespaces(true);
		return jsSrcOptions;
	}

	/** Replaces closure templates with the corresponding JavaScript code. */
	private static void replaceTemplateWithJavaScript(
			Map<String, JavaScriptFile> files, List<String> names,
			List<String> sources) {
		for (int i = 0; i < names.size(); ++i) {
			String oldName = names.get(i);
			String newName = oldName + ".generated.js";

			files.remove(oldName);
			files.put(newName, JavaScriptModuleBase
					.createJavaScriptFileForClosure(EType.CODE_REQUIRED,
							newName, sources.get(i),
							CollectionUtils.<String> emptyList(),
							CollectionUtils.<String> emptyList()));
		}
	}
}
