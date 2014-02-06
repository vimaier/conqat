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

package org.conqat.engine.text.comments.shallowparsing;

import java.util.List;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.shallowparser.IShallowParser;
import org.conqat.engine.sourcecode.shallowparser.ShallowParserFactory;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;
import org.conqat.lib.scanner.ELanguage;
import org.conqat.lib.scanner.IToken;

/**
 * Implementation of a shallow parser for method and field extraction
 * 
 * @author $Author: steidl $
 * @version $Rev: 46589 $
 * @ConQAT.Rating YELLOW Hash: E6F40FF87E50A6270752005EB5E106C7
 */
public class MemberExtractor {

	/** the underlying token element */
	private ITokenElement element;

	/** the underlying tokens */
	private List<IToken> tokens;

	/** the underlying programming language */
	private ELanguage lng;

	/** set of extracted methods */
	private List<Method> methods;

	/** set of extracted fields */
	private List<String> fields;

	/**
	 * Constructor which also starts the extraction of methods and fields.
	 */
	public MemberExtractor(ITokenElement element, List<IToken> tokens)
			throws ConQATException {
		this.element = element;
		this.tokens = tokens;
		lng = element.getLanguage();
		extract();

	}

	/** Parsing call to extract all methods and fields. */
	private void extract() throws ConQATException {

		IShallowParser parser = ShallowParserFactory.createParser(lng);

		List<ShallowEntity> entities = parser.parseTopLevel(tokens);
		MethodVisitor methodVisitor = new MethodVisitor();
		methodVisitor.calculateMetrics(element, entities);

		methods = methodVisitor.getMethods();
		fields = methodVisitor.getFields();

	}

	/**
	 * Returns true if the given name is a method name and the position of the
	 * name is within the methods token.
	 */
	public boolean containsMethodIdentifier(String methodName, int positionName) {
		for (Method method : methods) {
			if (method.getMethodName().equals(methodName)
					&& positionName >= method.getStartToken().getOffset()
					&& positionName <= method.getEndToken().getOffset()) {

				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if the given name is the name of a field.
	 */
	public boolean containsFieldIdentifier(String fieldName) {
		return fields.contains(fieldName);

	}

	/**
	 * Returns true if the given token position is within a method.
	 */
	public boolean isInsideMethod(int position) {
		for (Method method : methods) {

			IToken comment = tokens.get(position);

			if (method.getStartToken().getOffset() < comment.getOffset()
					&& comment.getOffset() < method.getEndToken().getOffset()) {
				return true;
			}
		}
		return false;
	}

}
