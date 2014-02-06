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
package org.conqat.engine.sourcecode.analysis.javascript;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.commons.findings.FindingGroup;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.engine.resource.util.ResourceUtils;
import org.conqat.lib.commons.collections.CollectionUtils;

import com.google.javascript.jscomp.BasicErrorManager;
import com.google.javascript.jscomp.CheckLevel;
import com.google.javascript.jscomp.ClosureCodingConvention;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.JSError;
import com.google.javascript.jscomp.JSSourceFile;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating RED Hash: 58F5D091E8D4169A3C1411627667ED35
 */
@AConQATProcessor(description = "Checks JavaScript files using Google's closure compiler. "
		+ "Missing annotations are reported as findings. For more information see http://code.google.com/closure/")
public class ClosureAnalyzer extends ConQATPipelineProcessorBase<ITextResource> {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "The key to store the findings found.", type = "java.lang.List<Finding>")
	public static final String KEY = "Closure Findings";

	/** Mapping from unifirm path to element. */
	private Map<String, ITextElement> uniformPathToElementMap;

	/** The group used to attach findings to. */
	private FindingGroup group;

	/** {@inheritDoc} */
	@Override
	protected void processInput(ITextResource input) throws ConQATException {

		NodeUtils.addToDisplayList(input, KEY);

		uniformPathToElementMap = ResourceTraversalUtils
				.createUniformPathToElementMap(input, ITextElement.class);

		group = NodeUtils.getFindingReport(input)
				.getOrCreateCategory("Google Closure")
				.getOrCreateFindingGroup("Closure Findings");

		FindingErrorManager errorManager = new FindingErrorManager();
		Compiler compiler = new Compiler(errorManager);
		Compiler.setLoggingLevel(Level.OFF);
		compiler.compile(new JSSourceFile[0], CollectionUtils.toArray(
				determineInputFiles(input), JSSourceFile.class),
				determineOptions());

		if (errorManager.hadErrors()) {
			throw new ConQATException("Closure compile error: '"
					+ errorManager.error.description + "' at "
					+ errorManager.error.sourceName + ":"
					+ errorManager.error.lineNumber);
		}
	}

	/** Determines the input files. */
	private List<JSSourceFile> determineInputFiles(ITextResource input)
			throws ConQATException {
		List<JSSourceFile> inputFiles = new ArrayList<JSSourceFile>();
		for (ITextElement element : ResourceTraversalUtils
				.listTextElements(input)) {
			inputFiles.add(JSSourceFile.fromCode(element.getUniformPath(),
					element.getTextContent()));
		}
		return inputFiles;
	}

	/** Returns the compiler options used. */
	private CompilerOptions determineOptions() {
		CompilerOptions options = new CompilerOptions();
		CompilationLevel.ADVANCED_OPTIMIZATIONS
				.setOptionsForCompilationLevel(options);
		options.setCodingConvention(new ClosureCodingConvention());

		options.checkEs5Strict = true;
		options.checkTypes = false;
		options.setCheckUnreachableCode(CheckLevel.WARNING);
		options.setCheckMissingReturn(CheckLevel.WARNING);

		options.checkDuplicateMessages = true;
		options.checkControlStructures = true;
		options.checkTypedPropertyCalls = true;
		options.checkSuspiciousCode = true;

		options.setReportMissingOverride(CheckLevel.WARNING);
		options.setCheckFunctions(CheckLevel.WARNING);
		options.setCheckGlobalNamesLevel(CheckLevel.OFF);
		options.setCheckGlobalThisLevel(CheckLevel.OFF);
		options.setCheckMethods(CheckLevel.WARNING);
		options.setCheckProvides(CheckLevel.OFF);
		options.setCheckRequires(CheckLevel.OFF);
		options.setCheckShadowVars(CheckLevel.WARNING);
		options.setAggressiveVarCheck(CheckLevel.WARNING);

		return options;
	}

	/** An error manager that creates findings. */
	private class FindingErrorManager extends BasicErrorManager {
		/** Last error found. */
		private JSError error;

		/** {@inheritDoc} */
		@Override
		public void println(CheckLevel level, JSError error) {
			if (level == CheckLevel.ERROR) {
				this.error = error;
			}
			ITextElement element = uniformPathToElementMap
					.get(error.sourceName);
			if (element == null) {
				getLogger().error("No element found for " + error.sourceName);
			} else {
				try {
					ResourceUtils.createAndAttachFindingForFilteredLine(group,
							error.description, element, error.lineNumber, KEY);
				} catch (ConQATException e) {
					getLogger().error(
							"Offset conversion failed: " + e.getMessage(), e);
				}
			}
		}

		/** {@inheritDoc} */
		@Override
		protected void printSummary() {
			// does nothing
		}

		/** Returns true if errors where encountered. */
		public boolean hadErrors() {
			return error != null;
		}
	}
}
