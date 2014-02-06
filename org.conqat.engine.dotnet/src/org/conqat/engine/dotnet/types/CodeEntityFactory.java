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
package org.conqat.engine.dotnet.types;

import static org.conqat.lib.scanner.ETokenType.CLASS;
import static org.conqat.lib.scanner.ETokenType.DELEGATE;
import static org.conqat.lib.scanner.ETokenType.ENUM;
import static org.conqat.lib.scanner.ETokenType.INTERFACE;
import static org.conqat.lib.scanner.ETokenType.NAMESPACE;
import static org.conqat.lib.scanner.ETokenType.STRUCT;

import java.util.List;
import java.util.Stack;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.shallowparser.IShallowParser;
import org.conqat.engine.sourcecode.shallowparser.ShallowParserFactory;
import org.conqat.engine.sourcecode.shallowparser.TokenStreamUtils;
import org.conqat.engine.sourcecode.shallowparser.framework.IShallowEntityVisitor;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableList;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.scanner.ELanguage;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;

/**
 * Uses a shallow parser to extract the code entities from tokens.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 46378 $
 * @ConQAT.Rating GREEN Hash: C3DBD5235BB9B87E2A00F5DE0FE545CD
 */
public class CodeEntityFactory implements IShallowEntityVisitor {

	/** Root entity under which created entities are collected */
	private final RootCodeEntity root;

	/** Keeps track of code entity nesting during parse */
	private final Stack<CodeEntityBase> codeEntities = new Stack<CodeEntityBase>();

	/** Constructor */
	public CodeEntityFactory(RootCodeEntity root) {
		this.root = root;
		codeEntities.push(root);
	}

	/** Create code entities for list of tokens */
	public static RootCodeEntity codeEntitiesFor(List<IToken> tokens)
			throws ConQATException {
		if (tokens.size() == 0) {
			return new RootCodeEntity();
		}
		ELanguage language = CollectionUtils.getAny(tokens).getLanguage();
		IShallowParser parser = ShallowParserFactory.createParser(language);
		List<ShallowEntity> entities = parser.parseTopLevel(tokens);
		return CodeEntityFactory.codeEntitiesForShallowEntities(entities);
	}

	/** Create code entities from {@link ShallowEntity}s */
	public static RootCodeEntity codeEntitiesForShallowEntities(
			List<ShallowEntity> entities) {
		CodeEntityFactory factory = new CodeEntityFactory(new RootCodeEntity());

		ShallowEntity.traverse(entities, factory);

		return factory.root;
	}

	/** {@inheritDoc} */
	@Override
	public boolean visit(ShallowEntity entity) {
		String subtype = entity.getSubtype();
		CodeEntityBase parent = codeEntities.peek();
		String name = entity.getName();
		if (!StringUtils.isEmpty(parent.getFqName())) {
			name = parent.getFqName() + parent.getChildSeparator() + name;
		}

		NamedCodeEntity child = null;
		if (subtype.equalsIgnoreCase(NAMESPACE.name())) {
			child = new NamedCodeEntity(name, NAMESPACE, ".");
		} else if (subtype.equalsIgnoreCase(CLASS.name())) {
			child = new NamedCodeEntity(fqNameWithTypeSignature(entity, name),
					CLASS, "/");
		} else if (subtype.equalsIgnoreCase(ENUM.name())) {
			child = new NamedCodeEntity(name, ENUM, "/");
		} else if (subtype.equalsIgnoreCase(INTERFACE.name())) {
			child = new NamedCodeEntity(fqNameWithTypeSignature(entity, name),
					INTERFACE, StringUtils.EMPTY_STRING);
		} else if (subtype.equalsIgnoreCase(STRUCT.name())) {
			child = new NamedCodeEntity(fqNameWithTypeSignature(entity, name),
					STRUCT, "/");
		} else if (subtype.equalsIgnoreCase(DELEGATE.name())) {
			child = new NamedCodeEntity(name, DELEGATE, "/");
		}

		if (child != null) {
			parent.addChild(child);
			codeEntities.push(child);
		}

		return true;
	}

	/**
	 * Returns fully qualified name with type signature. I.e., converts
	 * a.b.Class to a.b.Class<T0>
	 */
	private String fqNameWithTypeSignature(ShallowEntity entity, String name) {
		String typeSignature = typeSignature(entity);

		if (!StringUtils.isEmpty(typeSignature)) {
			name = name + typeSignature;
		}

		return name;
	}

	/**
	 * @return the type signature of the form <T0,T1> or null, if the type is
	 *         not generic.
	 */
	private String typeSignature(ShallowEntity entity) throws AssertionError {
		UnmodifiableList<IToken> tokens = entity.includedTokens();

		int classNameIndex = TokenStreamUtils.find(tokens,
				ETokenType.IDENTIFIER);
		IToken className = tokens.get(classNameIndex);
		CCSMAssert.isTrue(className.getText().equals(entity.getName()),
				"Expecting first identifier to be classname, but was "
						+ className.getText());

		IToken next = tokens.get(classNameIndex + 1);
		if (next.getType() != ETokenType.LT) {
			return null;
		}

		int gtIndex = TokenStreamUtils.find(tokens, ETokenType.GT,
				classNameIndex);
		CCSMAssert.isFalse(gtIndex == -1,
				"Found no closing angular bracket for generic definition");

		// count type identifiers
		int searchIndex = classNameIndex + 1;
		int numberOfTypes = 0;
		while (searchIndex < gtIndex && searchIndex > 0) {
			searchIndex = 1 + TokenStreamUtils.find(tokens,
					ETokenType.IDENTIFIER, searchIndex, gtIndex);
			if (searchIndex > 0) {
				numberOfTypes++;
			}
		}

		CCSMAssert
				.isTrue(numberOfTypes > 0, "malformed generic arguments list");

		return generateSignature(numberOfTypes);
	}

	/**
	 * Generate signature of the form <T0,T1> with the specified number of type
	 * parameters
	 */
	private String generateSignature(int numberOfTypes) {
		StringBuilder nameBuilder = new StringBuilder();
		nameBuilder.append("<");

		for (int i = 0; i < numberOfTypes; i++) {
			if (nameBuilder.length() > 1) {
				nameBuilder.append(",");
			}
			nameBuilder.append("T" + i);
		}

		nameBuilder.append(">");

		return nameBuilder.toString();
	}

	/** {@inheritDoc} */
	@Override
	public void endVisit(ShallowEntity entity) {
		String subtype = entity.getSubtype();
		if (subtype.equalsIgnoreCase(NAMESPACE.name())
				|| subtype.equalsIgnoreCase(CLASS.name())
				|| subtype.equalsIgnoreCase(ENUM.name())
				|| subtype.equalsIgnoreCase(INTERFACE.name())
				|| subtype.equalsIgnoreCase(STRUCT.name())
				|| subtype.equalsIgnoreCase(DELEGATE.name())) {
			codeEntities.pop();
		}
	}

}
