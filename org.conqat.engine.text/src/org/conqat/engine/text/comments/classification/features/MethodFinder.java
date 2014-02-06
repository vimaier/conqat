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
package org.conqat.engine.text.comments.classification.features;

import java.util.List;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.text.comments.shallowparsing.MemberExtractor;
import org.conqat.lib.scanner.ETokenType.ETokenClass;
import org.conqat.lib.scanner.IToken;

/**
 * Helper class to find next method/field definition
 * 
 * @author $Author: steidl $
 * @version $Rev: 46589 $
 * @ConQAT.Rating YELLOW Hash: 9B657F8D0EE6614884DEBB829FB58D40
 */
public class MethodFinder {

	/** complete token list */
	private List<IToken> tokenList;

	/** the element containing the comment */
	ITokenElement element;

	/** analyzer for the source code */
	private MemberExtractor memberExtractor;

	/** Constructor */
	public MethodFinder(ITokenElement element, List<IToken> tokenList)
			throws ConQATException {
		this.tokenList = tokenList;
		this.element = element;

		memberExtractor = new MemberExtractor(element, tokenList);

	}

	/**
	 * returns the name of the next method, searching from the given position
	 * (returns -1 if an other comment preceeds the next method or no method is
	 * found)
	 */
	private String getNextMethodDefinition(int position, int lineNumber) {
		// search in same line
		for (int i = position - 1; i >= 0; i--) {
			IToken token = tokenList.get(i);
			if (token.getLineNumber() == lineNumber
					&& memberExtractor.containsMethodIdentifier(
							token.getText(), token.getOffset())) {
				return token.getText();
			} else if (token.getLineNumber() < lineNumber) {
				break;
			}
		}

		for (int i = position + 1; i < tokenList.size(); i++) {
			if (tokenList.get(i).getType().getTokenClass()
					.equals(ETokenClass.COMMENT)) {
				return "";
			}
			if (memberExtractor.containsMethodIdentifier(tokenList.get(i)
					.getText(), tokenList.get(i).getOffset())) {
				return tokenList.get(i).getText();
			}

		}
		return "";
	}

	/**
	 * returns the distance (measured in token numbers) from the given position
	 * to the next method definition (returns -1 if an other comment preceeds
	 * the next method or no method is found)
	 */
	private int getDistanceToNextMethod(int position, int lineNumber) {
		// search in same line
		for (int i = position - 1; i >= 0; i--) {
			IToken token = tokenList.get(i);
			if (token.getLineNumber() == lineNumber
					&& memberExtractor.containsMethodIdentifier(
							token.getText(), tokenList.get(i).getOffset())) {
				return 0;
			} else if (token.getLineNumber() < lineNumber) {
				break;
			}
		}

		// search from the current position forward
		for (int i = position + 1; i < tokenList.size(); i++) {

			if (tokenList.get(i).getType().getTokenClass()
					.equals(ETokenClass.COMMENT)) {
				return -1;
			}

			if (memberExtractor.containsMethodIdentifier(tokenList.get(i)
					.getText(), tokenList.get(i).getOffset())) {

				return (i - position);
			}

		}
		return -1;
	}

	/**
	 * returns the name of the next field, searching from the given position
	 * (returns -1 if an other comment preceeds the next field or no field is
	 * found)
	 */
	private String getNextFieldDefinition(int position, int lineNumber) {
		// search in same line
		for (int i = position - 1; i >= 0; i--) {
			IToken token = tokenList.get(i);
			if (token.getLineNumber() == lineNumber
					&& memberExtractor.containsFieldIdentifier(token.getText())) {
				return token.getText();
			} else if (token.getLineNumber() < lineNumber) {
				break;
			}
		}
		// search in following lines after comment
		for (int i = position + 1; i < tokenList.size(); i++) {
			if (tokenList.get(i).getType().getTokenClass()
					.equals(ETokenClass.COMMENT)) {
				return "";
			}

			if (memberExtractor.containsFieldIdentifier(tokenList.get(i)
					.getText())) {
				return tokenList.get(i).getText();

			}

		}
		return "";
	}

	/**
	 * returns the distance (measured in token numbers) from the given position
	 * to the next field definition (returns -1 if an other comment preceeds the
	 * next method or no method is found)
	 */
	private int getDistanceToNextField(int position, int lineNumber) {

		// search in same line
		for (int i = position - 1; i >= 0; i--) {
			IToken token = tokenList.get(i);
			if (token.getLineNumber() == lineNumber
					&& memberExtractor.containsFieldIdentifier(token.getText())) {
				return 0;
			} else if (token.getLineNumber() < lineNumber) {
				break;
			}
		}

		// search in following lines after comment
		for (int i = position + 1; i < tokenList.size(); i++) {
			if (tokenList.get(i).getType().getTokenClass()
					.equals(ETokenClass.COMMENT)) {
				return -1;
			}

			if (memberExtractor.containsFieldIdentifier(tokenList.get(i)
					.getText())) {
				return (i - position);
			}

		}
		return -1;
	}

	/**
	 * returns the name of the next method or field definition
	 */
	public String getNextDefinition(int position) {
		int field = getDistanceToNextField(position, tokenList.get(position)
				.getLineNumber());
		int method = getDistanceToNextMethod(position, tokenList.get(position)
				.getLineNumber());
		// neither field nor method found
		if (field == -1 && method == -1)
			return "";

		// field is closer than method
		if (field < method) {
			if (field == -1) {
				return getNextMethodDefinition(position, tokenList
						.get(position).getLineNumber());
			}
			return getNextFieldDefinition(position, tokenList.get(position)
					.getLineNumber());
		}

		// method is closer than field
		if (method == -1) {
			return getNextFieldDefinition(position, tokenList.get(position)
					.getLineNumber());
		}
		return getNextMethodDefinition(position, tokenList.get(position)
				.getLineNumber());

	}

	/**
	 * returns the distance (measured in tokens) to the next method or field
	 * definition
	 */
	public int getDistanceToNextDefinition(int position) {
		// subtract one to match interface distance condition
		if (getDistanceToNextMethod(position, tokenList.get(position)
				.getLineNumber()) - 1 >= 0) {

			return Math.max(
					0,
					(getDistanceToNextMethod(position, tokenList.get(position)
							.getLineNumber()) - 1));
		}
		if (getDistanceToNextField(position, tokenList.get(position)
				.getLineNumber()) - 1 >= 0) {

			return Math.max(
					0,
					(getDistanceToNextField(position, tokenList.get(position)
							.getLineNumber()) - 1));
		}
		return -1;
	}

	/**
	 * returns the shallow parser used. use this method to avoid additional
	 * parsing calls
	 */
	public MemberExtractor getShallowParser() {
		return memberExtractor;
	}

}
