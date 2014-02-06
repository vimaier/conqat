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

import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Exception for parsing errors in the {@link EntitySelectionExpressionParser}.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46221 $
 * @ConQAT.Rating GREEN Hash: 7BEB1AE1316FEC6C334101271938DBEE
 */
public class EntitySelectionExpressionParsingException extends ConQATException {

	/** Version for serialization. */
	private static final long serialVersionUID = 1;

	/** The message identifier. */
	private final EParsingExceptionMessage messageIdentifier;

	/**
	 * Constructor.
	 * 
	 * @param expression
	 *            the expression being parsed.
	 * @param position
	 *            the current parsing position in the message.
	 * 
	 */
	public EntitySelectionExpressionParsingException(
			EParsingExceptionMessage messageIdentifier, String expression,
			int position) {
		super(createMessage(messageIdentifier, expression, position, null));
		this.messageIdentifier = messageIdentifier;
	}

	/**
	 * Constructor.
	 * 
	 * @param expression
	 *            the expression being parsed.
	 * @param position
	 *            the current parsing position in the message.
	 * @param cause
	 *            may not be null.
	 * 
	 */
	public EntitySelectionExpressionParsingException(
			EParsingExceptionMessage messageIdentifier, String expression,
			int position, Throwable cause) {
		super(createMessage(messageIdentifier, expression, position, cause),
				cause);
		this.messageIdentifier = messageIdentifier;
	}

	/** Creates the message for this exception. */
	private static String createMessage(
			EParsingExceptionMessage messageIdentifier, String expression,
			int position, Throwable cause) {

		String message = messageIdentifier.getMessage();
		if (cause != null) {
			message += ": " + cause.getMessage();
		}

		if (!StringUtils.endsWithOneOf(message, ".", "!")) {
			message += ".";
		}

		String details = "The error occurred at or before the position marked with a caret: "
				+ expression.substring(0, position)
				+ "^"
				+ expression.substring(position);
		return message + " " + details;
	}

	/** Returns the message identifier. */
	public EParsingExceptionMessage getMessageIdentifier() {
		return messageIdentifier;
	}

	/**
	 * Enumeration of valid messages for this exception. This is both to reduce
	 * redundancy and to simplify testing.
	 */
	public static enum EParsingExceptionMessage {

		/** Message. */
		EXPECTED_EXPRESSION(
				"Expected an expression before or at this position."),

		/** Message. */
		EXPECTED_BINARY_OPERATOR("Expected a binary operator such as & or |."),

		/** Message. */
		PREDICATE_CONSTRUCTION_FAILED("Failed to construct predicate"),

		/** Message. */
		PARAMETER_MISSING("Must provide parameter for this predicate!"),

		/** Message. */
		UNSUPPORTED_PARAMETER("May not provide parameter for this predicate!"),

		/** Message. */
		PREDICATE_NOT_FOUND("No matching predicate found!"),

		/** Message. */
		MISSING_CLOSING_PARENTHESIS("Missing closing parenthesis!"),

		/** Message. */
		MISPLACED_CLOSING_PARENTHESIS("Misplaced closing parenthesis."),

		/** Message. */
		UNEXPECTED_CHARACTER("Unexpected character.");

		/** The message for the exception. */
		private final String message;

		/** Constructor. */
		private EParsingExceptionMessage(String message) {
			this.message = message;
		}

		/** Returns the message for the exception. */
		public String getMessage() {
			return message;
		}
	}
}
