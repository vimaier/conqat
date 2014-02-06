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
package org.conqat.engine.sourcecode.analysis;

import java.util.EnumSet;

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.analysis.SearchAnalyzerBase;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.engine.sourcecode.resource.TokenElementUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.scanner.ETokenType.ETokenClass;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: FA482DBDF6E77CB00BEA34AD9101E894
 */
@AConQATProcessor(description = "Counts occurrences of regular expressions in "
		+ "text content. Multiple regular expressions may be specified. This allows "
		+ "searches limited to a system's identifiers. At least one "
		+ "token class and one search pattern must be specified. "
		+ "This processor preserves the line structure, so multiline "
		+ "expressions may be used. Tokens within a line are separated "
		+ "by a single space.")
public class SourceCodeSearchAnalyzer extends
		SearchAnalyzerBase<ITokenResource, ITokenElement> {

	/** Set of token classes to include in analysis. */
	private final EnumSet<ETokenClass> tokenClasses = EnumSet
			.noneOf(ETokenClass.class);

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "token", minOccurrences = 1, description = "Add a token class.")
	public void addTokenClass(
			@AConQATAttribute(name = "class", description = "Specifies "
					+ "token class.") ETokenClass tokenClass) {
		tokenClasses.add(tokenClass);
	}

	/**
	 * Create a string from the tokens in the file that are in one of the
	 * specified token classes.
	 */
	@Override
	protected String getText(ITokenElement element) {

		try {
			return TokenElementUtils.getFilteredTokenContent(element,
					tokenClasses, getLogger());
		} catch (ConQATException e1) {
			getLogger().warn("Empty source element: " + element);
			return StringUtils.EMPTY_STRING;
		}
	}

}