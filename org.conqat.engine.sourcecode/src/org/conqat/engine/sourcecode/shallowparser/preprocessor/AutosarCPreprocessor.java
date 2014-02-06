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
package org.conqat.engine.sourcecode.shallowparser.preprocessor;

import java.util.Arrays;

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.lib.scanner.ETokenType;

/**
 * {@ConQAT.Doc}
 * <p>
 * The specification is available here:
 * http://autosar.org/download/R4.0/AUTOSAR_SWS_CompilerAbstraction.pdf (Version
 * 4.0). The relevant information is in chapter 9.
 * 
 * @author $Author: goede $
 * @version $Rev: 41630 $
 * @ConQAT.Rating GREEN Hash: 9ABFE40B10E5CA8C9039315D0B19F2A8
 */
@AConQATProcessor(description = "Parser preprocessor to support AUTOSAR macros in C code as defined in the Compiler Abstraction Specification.")
public class AutosarCPreprocessor extends MacroSupportingPreprocessorBase {

	/** {@ConQAT.Doc} */
	@Override
	@AConQATParameter(name = "collapsed-macro", description = "Adds a macro to be collapsed into a single indentifer (including parentheses.")
	public void addCollapseMacro(
			@AConQATAttribute(name = "name", description = "The name of the macro (case-sensitive)") String name) {
		super.addCollapseMacro(name);
	}

	/** {@ConQAT.Doc} */
	@Override
	@AConQATParameter(name = "discard-macro", description = "Adds a macro to be discarded completely (including parentheses.")
	public void addDiscardMacro(
			@AConQATAttribute(name = "name", description = "The name of the macro (case-sensitive)") String name) {
		super.addDiscardMacro(name);
	}

	/** Constructor. */
	public AutosarCPreprocessor() {
		addFilteredIdentifier("AUTOMATIC");
		addFilteredIdentifier("TYPEDEF");

		addMappedIdentifier("STATIC", "static", ETokenType.STATIC);
		addMappedIdentifier("INLINE", "inline", ETokenType.INLINE);
		addMappedIdentifier("LOCAL_INLINE", "inline", ETokenType.INLINE);

		for (String name : Arrays.asList("FUNC", "FUNC_P2CONST", "FUNC_P2VAR",
				"P2VAR", "P2CONST", "CONSTP2VAR", "CONSTP2CONST", "P2FUNC",
				"CONST", "VAR")) {
			addCollapseMacro(name);
		}
	}
}
