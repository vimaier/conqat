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
package org.conqat.engine.sourcecode.analysis.shallowparsed;

import java.util.List;
import java.util.regex.Pattern;

import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.shallowparser.SubTypeNames;
import org.conqat.engine.sourcecode.shallowparser.framework.EShallowEntityType;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntityTraversalUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 47154 $
 * @ConQAT.Rating GREEN Hash: 28D88E15CB6FDD1A406ED6A33385FA17
 */
@AConQATProcessor(description = "Configurable naming convention check. Identifiers that violate the conventions are marked with findings.")
public class NamingConventionAnalyzer extends ShallowParsedFindingAnalyzerBase {

	/** Pattern that allows any name. */
	private static final PatternList ANY = new PatternList(
			Pattern.compile(".*"));

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "module-names", attribute = "pattern", optional = true, description = ""
			+ "Patterns that module/namespace/package names have to match by convention.")
	public PatternList modulePattern = ANY;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "type-names", attribute = "pattern", optional = true, description = ""
			+ "Patterns that type names have to match by convention.")
	public PatternList typePattern = ANY;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "method-names", attribute = "pattern", optional = true, description = ""
			+ "Patterns that method/function names have to match by convention.")
	public PatternList methodPattern = ANY;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "method-parameters", attribute = "pattern", optional = true, description = ""
			+ "Patterns that method/function parameters have to match by convention.")
	public PatternList methodParameterPattern = ANY;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "attribute-names", attribute = "pattern", optional = true, description = ""
			+ "Patterns that class attribute names have to match by convention.")
	public PatternList attributePattern = ANY;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "local-variables", attribute = "pattern", optional = true, description = ""
			+ "Patterns that local variable names have to match by convention.")
	public PatternList localVariablePattern = ANY;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "global-variables", attribute = "pattern", optional = true, description = ""
			+ "Patterns that global variable names have to match by convention.")
	public PatternList globalVariablePattern = ANY;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "constant-names", attribute = "pattern", optional = true, description = ""
			+ "Patterns that names of constants have to match by convention.")
	public PatternList constantPattern = ANY;

	/** {@inheritDoc} */
	@Override
	protected void analyzeShallowEntities(ITokenElement element,
			List<ShallowEntity> entities) throws ConQATException {
		for (ShallowEntity entity : ShallowEntityTraversalUtils
				.listAllEntities(entities)) {
			switch (entity.getType()) {
			case MODULE:
				checkName(element, entity, modulePattern);
				break;
			case TYPE:
				checkName(element, entity, typePattern);
				break;
			case METHOD:
				checkName(element, entity, methodPattern);
				checkMethodParameters(element, entity);
				break;
			case ATTRIBUTE:
				checkAttribute(element, entity);
				break;
			case META:
				checkMeta(element, entity);
				break;
			case STATEMENT:
				checkStatement(element, entity);
				break;
			}
		}
	}

	/** Checks the names for method parameters. */
	private void checkMethodParameters(ITokenElement element,
			ShallowEntity entity) throws ConQATException {
		for (IToken token : ShallowParsingUtils
				.extractParameterNameTokens(entity)) {
			checkName(element, token, methodParameterPattern);
		}
	}

	/** Checks an attribute (could also be a global variable or a constant). */
	private void checkAttribute(ITokenElement element, ShallowEntity entity)
			throws ConQATException {
		if (ShallowParsingUtils.isConstant(entity)) {
			checkNameWithMultipleVariables(element, entity, constantPattern);
		} else if (entity.getParent() == null
				|| entity.getParent().getType() == EShallowEntityType.MODULE) {
			checkNameWithMultipleVariables(element, entity,
					globalVariablePattern);
		} else {
			checkNameWithMultipleVariables(element, entity, attributePattern);
		}
	}

	/**
	 * Performs naming checks on meta elements. Currently, this is only used for
	 * checking Java package names.
	 */
	private void checkMeta(ITokenElement element, ShallowEntity entity)
			throws ConQATException {
		if (entity.getSubtype().equalsIgnoreCase(ETokenType.PACKAGE.name())) {
			checkName(element, entity, extractJavaPackageName(entity),
					modulePattern);
		}
	}

	/** Extracts the Java package name from a package meta entity. */
	private String extractJavaPackageName(ShallowEntity entity) {
		StringBuilder packageNameBuilder = new StringBuilder();
		for (IToken token : entity.includedTokens()) {
			if (token.getType() == ETokenType.IDENTIFIER) {
				if (packageNameBuilder.length() > 0) {
					packageNameBuilder.append(".");
				}
				packageNameBuilder.append(token.getText());
			}
		}
		return packageNameBuilder.toString();
	}

	/**
	 * Checks a single statement, which could be a local variable. This also
	 * handles the case of for loops with local variables in the loop header.
	 */
	private void checkStatement(ITokenElement element, ShallowEntity entity)
			throws ConQATException {
		if (entity.getSubtype().equals(SubTypeNames.LOCAL_VARIABLE)) {
			checkNameWithMultipleVariables(element, entity,
					localVariablePattern);
		}

		if (entity.getSubtype().equalsIgnoreCase(ETokenType.FOR.name())) {
			for (IToken token : ShallowParsingUtils
					.extractVariablesDeclaredInFor(entity)) {
				checkName(element, token, localVariablePattern);
			}
		}
	}

	/**
	 * Checks the name of the given entity that can potentially contain multiple
	 * variables separated by comma.
	 */
	private void checkNameWithMultipleVariables(ITokenElement element,
			ShallowEntity entity, PatternList patterns) throws ConQATException {
		for (IToken token : ShallowParsingUtils
				.extractVariableNameTokens(entity.ownStartTokens())) {
			checkName(element, token, patterns);
		}
	}

	/** Checks the name of a given entity. */
	private void checkName(ITokenElement element, ShallowEntity entity,
			PatternList patterns) throws ConQATException {
		checkName(element, entity, entity.getName(), patterns);
	}

	/** Checks the name of a given entity. */
	private void checkName(ITokenElement element, ShallowEntity entity,
			String name, PatternList patterns) throws ConQATException {
		if (!patterns.matchesAny(name)) {
			createFindingForEntityStart(getMessage(name, patterns), element,
					entity);
		}
	}

	/** Checks the name of a given token. */
	private void checkName(ITokenElement element, IToken token,
			PatternList patterns) throws ConQATException {
		if (!patterns.matchesAny(token.getText())) {
			createFindingForFilteredOffsets(
					getMessage(token.getText(), patterns), element,
					token.getOffset(), token.getEndOffset());
		}
	}

	/** Returns the message for the violation finding. */
	private String getMessage(String name, PatternList patterns) {
		StringBuilder builder = new StringBuilder();
		builder.append("Name violates naming convention: ");
		builder.append(name);
		builder.append(". Should be one of '");
		builder.append(StringUtils.concat(patterns, "', '"));
		builder.append("'.");
		return builder.toString();
	}

	/** {@inheritDoc} */
	@Override
	protected String getFindingGroupName() {
		return "Naming Convention";
	}

	/** {@inheritDoc} */
	@Override
	protected String getFindingCategoryName() {
		return "Naming";
	}
}
