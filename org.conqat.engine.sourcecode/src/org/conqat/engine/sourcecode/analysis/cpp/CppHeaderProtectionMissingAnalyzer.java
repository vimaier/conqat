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
package org.conqat.engine.sourcecode.analysis.cpp;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.util.UniformPathUtils;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.shallowparser.framework.EShallowEntityType;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: goede $
 * @version $Rev: 43214 $
 * @ConQAT.Rating GREEN Hash: EC71E1D6FA1410A42B3E6723DEEAD2D9
 */
@AConQATProcessor(description = "Checks C/C++ header files if a valid header protection has been installed.")
public class CppHeaderProtectionMissingAnalyzer extends CppFindingAnalyzerBase {

	/** Extensions of files treated as header files. */
	private final Set<String> headerExtensions = new HashSet<String>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "header", description = "Defines an extension used to identify header files. "
			+ "If none is given, all elements are treated as headers.")
	public void addExtension(
			@AConQATAttribute(name = "extension", description = "The extension (compared case insensitive).") String extension) {
		headerExtensions.add(extension.toLowerCase());
	}

	/** {@inheritDoc} */
	@Override
	protected void analyzeElement(ITokenElement element) throws ConQATException {
		String extension = UniformPathUtils.getExtension(element
				.getUniformPath());
		if (extension == null
				|| (!headerExtensions.isEmpty() && !headerExtensions
						.contains(extension.toLowerCase()))) {
			// skip non-headers
			return;
		}

		super.analyzeElement(element);
	}

	/** {@inheritDoc} */
	@Override
	protected void analyzeShallowEntities(ITokenElement element,
			List<ShallowEntity> entities) throws ConQATException {

		if (entities.size() < 3) {
			createHeaderProtectFinding(element);
			return;
		}

		String checkedConstant = checkAndExtractIfndef(entities.get(0));
		String definedConstant = extractDefineConstant(entities.get(1));

		if (checkedConstant == null || definedConstant == null
				|| !checkClosingEndIf(CollectionUtils.getLast(entities))) {
			createHeaderProtectFinding(element);
		} else if (!checkedConstant.equals(definedConstant)) {
			createFindingForEntityStart(
					"Header protection defines different constant than the one checked for!",
					element, entities.get(1));
		}
	}

	/**
	 * The first macro must be either "#if !defined" or "#ifndef". Returns the
	 * constant checked for or null if constraint not satisfied.
	 */
	private String checkAndExtractIfndef(ShallowEntity entity) {
		IToken token = entity.includedTokens().get(0);
		if (token.getType() != ETokenType.PREPROCESSOR_DIRECTIVE) {
			return null;
		}

		String[] parts = token.getText().trim().split("(\\s|[()])+");
		if (parts.length >= 2 && parts[0].equals("#ifndef")) {
			return parts[1];
		}

		if (parts.length < 3 || !parts[0].equals("#if")) {
			return null;
		}

		if (parts[1].equals("!defined")) {
			return parts[2];
		}

		if (parts[1].equals("!") && parts[2].equals("defined")
				&& parts.length > 3) {
			return parts[3];
		}

		return null;
	}

	/**
	 * The second macro must be a "#define". Returns the name of the defined
	 * constant or null if constraint not satisfied.
	 */
	private String extractDefineConstant(ShallowEntity entity) {
		IToken token = entity.includedTokens().get(0);
		if (token.getType() != ETokenType.PREPROCESSOR_DIRECTIVE) {
			return null;
		}

		String[] parts = token.getText().trim().split("\\s+");
		if (parts.length < 2 || !parts[0].equals("#define")) {
			return null;
		}

		return parts[1];
	}

	/** Last non-comment list must be "#endif". */
	private boolean checkClosingEndIf(ShallowEntity last) {
		return last.getType() == EShallowEntityType.META
				&& last.includedTokens().get(0).getText().trim()
						.startsWith("#endif");
	}

	/** Creates the finding. */
	private void createHeaderProtectFinding(ITokenElement element) {
		createFinding(
				"Header protection missing or not implemented correctly!",
				element);
	}

	/** {@inheritDoc} */
	@Override
	protected String getFindingGroupName() {
		return "Header protection";
	}

}
