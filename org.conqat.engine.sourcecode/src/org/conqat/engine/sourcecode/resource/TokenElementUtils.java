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
package org.conqat.engine.sourcecode.resource;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.conqat.engine.commons.findings.location.ElementLocation;
import org.conqat.engine.commons.findings.location.TextRegionLocation;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.IConQATLogger;
import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.text.TextElementUtils;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.region.Region;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.scanner.ELanguage;
import org.conqat.lib.scanner.ETokenType.ETokenClass;
import org.conqat.lib.scanner.IToken;

/**
 * Utility code for dealing with {@link ITokenElement}s.
 * 
 * @author $Author: goede $
 * @version $Rev: 45260 $
 * @ConQAT.Rating YELLOW Hash: B1985CCC646CBBDDFD966E47A1CF6A89
 */
public class TokenElementUtils {

	/** Returns a list of all {@link ITokenElement}s from a resource hierarchy. */
	public static List<ITokenElement> listTokenElements(IResource resource) {
		return ResourceTraversalUtils.listElements(resource,
		        ITokenElement.class);
	}

	/**
	 * Returns the unique language from a {@link ITokenResource} hierarchy.
	 * 
	 * @throws ConQATException
	 *             if there is no unique language.
	 */
	public static ELanguage getUniqueLanguage(ITokenResource root)
	        throws ConQATException {
		Set<ELanguage> languages = EnumSet.noneOf(ELanguage.class);
		for (ITokenElement element : listTokenElements(root)) {
			languages.add(element.getLanguage());
		}

		if (languages.isEmpty()) {
			throw new ConQATException("No elements found!");
		}

		if (languages.size() > 1) {
			throw new ConQATException(
			        "Multiple languages found, which is not supported!");
		}

		return CollectionUtils.getAny(languages);
	}

	/**
	 * Creates a representation of the file that contains only the tokens
	 * specified in the parameter. This method keeps the original line breaks.
	 * 
	 * @throws ConQATException
	 *             if the file could not be scanned.
	 */
	public static String getFilteredTokenContent(ITokenElement element,
	        EnumSet<ETokenClass> tokenClasses, IConQATLogger logger)
	        throws ConQATException {
		List<IToken> tokens = element.getTokens(logger);

		return getFilteredTokenContent(tokenClasses, tokens);
	}

	/**
	 * Creates a representation of the tokens that contains only the tokens
	 * specified in the parameter. This method keeps the original line breaks.
	 */
	public static String getFilteredTokenContent(
	        EnumSet<ETokenClass> tokenClasses, List<IToken> tokens) {
		StringBuilder content = new StringBuilder();

		IToken lastToken = null;
		for (IToken token : tokens) {
			if (tokenClasses.contains(token.getType().getTokenClass())) {
				content.append(determineSeperator(token, lastToken));
				content.append(token.getText().trim());
				lastToken = token;
			}
		}

		return content.toString();
	}

	/**
	 * Returns the separator to be used in
	 * {@link #getFilteredTokenContent(ITokenElement, EnumSet, IConQATLogger)}.
	 * This is either a space or a new line, depending on whether the token were
	 * in the same line.
	 */
	private static String determineSeperator(IToken token, IToken lastToken) {
		// this is the first token or we strangely found a null-token -> no
		// separator needed
		if (lastToken == null || token == null) {
			return StringUtils.EMPTY_STRING;
		}

		// on same line
		if (token.getLineNumber() == lastToken.getLineNumber()) {
			return StringUtils.SPACE;
		}

		// different lines
		return StringUtils.CR;
	}

	/**
	 * Constructs an {@link ElementLocation} from the given start and end token
	 * inside the given element. The method tries to create a
	 * {@link TextRegionLocation} for the region between the given boundary
	 * tokens (including both). If anything fails, the method falls back to
	 * creating a simple {@link ElementLocation}.
	 */
	public static ElementLocation createLocation(ITokenElement element,
	        IToken start, IToken end) {
		Region filtered = new Region(start.getOffset(), end.getEndOffset());
		try {
			Region raw =
			        TextElementUtils
			                .convertFilteredOffsetRegionToRawLineRegion(
			                        element, filtered);
			return new TextRegionLocation(element.getLocation(),
			        element.getUniformPath(), element.getUnfilteredOffset(start
			                .getOffset()), element.getUnfilteredOffset(end
			                .getEndOffset()), raw.getStart(), raw.getEnd());
		} catch (ConQATException e) {
			return new ElementLocation(element.getLocation(),
			        element.getUniformPath());
		}
	}
}