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
package org.conqat.engine.sourcecode.shallowparser.util;

import java.util.List;

import org.conqat.engine.sourcecode.shallowparser.framework.EShallowEntityType;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableList;
import org.conqat.lib.scanner.ELanguage;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;

/**
 * Utility methods that can be used to extract the fully qualified names of
 * elements in the shallow AST.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 44109 $
 * @ConQAT.Rating GREEN Hash: B63B8AF9B30B6AFBC8A14B4F1FED9CE1
 */
public class ShallowParsedNameUtils {

	/** C++ scope separator */
	private static final String CPP_SCOPE = "::";

	/**
	 * Returns the fully qualified name of the programming construct the given
	 * entity represents. This includes surrounding classes and namespaces. This
	 * method employs various language-specific heuristics and thus probably
	 * needs to be extended for new languages after extending the parser.
	 * 
	 * @param entity
	 *            the entity must be of type MODULE, TYPE, METHOD, or ATTRIBUTE.
	 */
	public static String getFullyQualifiedName(ShallowEntity entity) {
		return getFullyQualifiedName(entity, determineLanguage(entity));
	}

	/**
	 * Returns the fully qualified name of the programming construct the given
	 * entity represents. This includes surrounding classes and namespaces. This
	 * method employs various language-specific heuristics and thus probably
	 * needs to be extended for new languages after extending the parser.
	 * 
	 * @param entity
	 *            the entity must be of type MODULE, TYPE, METHOD, or ATTRIBUTE.
	 */
	public static String getFullyQualifiedName(ShallowEntity entity,
			ELanguage language) {

		EShallowEntityType type = entity.getType();
		CCSMPre.isTrue(type == EShallowEntityType.MODULE
				|| type == EShallowEntityType.TYPE
				|| type == EShallowEntityType.METHOD
				|| type == EShallowEntityType.ATTRIBUTE,
				"May only pass MODULE, TYPE, METHOD, or ATTRIBUTE!");

		String name = entity.getName();

		if (language == ELanguage.CPP) {
			if (type == EShallowEntityType.METHOD
					|| type == EShallowEntityType.ATTRIBUTE) {
				name = extractCppMethodOrAttributeName(entity);
			}
		}

		// the previous code dealt with local name expansion, while the final
		// call adds global expansion by adding the names of parent types and
		// modules (think surrounding class).
		return expandParentNames(name, entity, language);
	}

	/**
	 * Returns the string obtained by prefixing the given name by the names of
	 * all TYPE/MODULE parent entities using the correct separator for the
	 * language.
	 */
	private static String expandParentNames(String name, ShallowEntity entity,
			ELanguage language) {

		String separator;
		switch (language) {
		case CPP:
			separator = CPP_SCOPE;
			break;
		case JAVA:
		case CS:
			separator = ".";
			break;
		default:
			// we use a non-standard default separator for unknown languages to
			// encourage updating the code but not hindering experiments on new
			// languages.
			separator = "/";
		}

		ShallowEntity parent = entity.getParent();
		while (parent != null) {
			if (parent.getType() == EShallowEntityType.TYPE
					|| parent.getType() == EShallowEntityType.MODULE) {
				name = parent.getName() + separator + name;
			}

			parent = parent.getParent();
		}

		return name;
	}

	/** Determines the programming language of an entity. */
	private static ELanguage determineLanguage(ShallowEntity entity) {
		UnmodifiableList<IToken> tokens = entity.includedTokens();
		CCSMAssert.isFalse(tokens.isEmpty(),
				"Shallow entities must have underlying tokens!");
		return CollectionUtils.getAny(tokens).getLanguage();
	}

	/**
	 * Extracts the complete name of a C++ method or attribute. This includes
	 * modifiers (e.g. for destructors) and the complete type prefix for fully
	 * qualified method names (e.g. A::B::c).
	 */
	private static String extractCppMethodOrAttributeName(
			ShallowEntity methodOrAttribute) {

		CCSMAssert
				.isTrue(methodOrAttribute.getType() == EShallowEntityType.METHOD
						|| methodOrAttribute.getType() == EShallowEntityType.ATTRIBUTE,
						"May only be used for method or attribute!");

		List<IToken> tokens = methodOrAttribute.includedTokens();
		String name = methodOrAttribute.getName();

		int index = 0;

		// We advance the index to find the position of the name in the token
		// stream. As for constructors the name of the method and the class are
		// the same, the second part of the condition checks if the identifier
		// is followed by the scope separator ("::"), which indicates that this
		// is a class name.
		while ((index < tokens.size() && !tokens.get(index).getText()
				.equals(name))
				|| (index + 1 < tokens.size() && tokens.get(index + 1)
						.getType() == ETokenType.SCOPE)) {
			index += 1;
		}

		if (index >= tokens.size()) {
			return name;
		}

		// for destructors, the scanner produces a separate token for the "~"
		// character, which thus must be handled explicitly here
		if (index >= 1 && tokens.get(index - 1).getType() == ETokenType.COMP) {
			index -= 1;
			name = "~" + name;
		}

		// expand the name while the current position is prepended by a scope
		// marker ("::")
		while (index >= 2
				&& tokens.get(index - 1).getType() == ETokenType.SCOPE) {
			index -= 2;
			name = tokens.get(index).getText() + CPP_SCOPE + name;
		}

		return name;
	}
}
