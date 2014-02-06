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
package org.conqat.engine.abap.analyzer;

import java.util.Iterator;
import java.util.List;

import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.scanner.ELanguage;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;

/**
 * A comment that has a type
 * 
 * @author herrmama
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating GREEN Hash: CAE59DC204104260077D318678B5A8E5
 */
public class Comment {

	/** Line number in which the comment starts (one based) */
	private final int startLineNumber;

	/** Line number in which the comment ends (one based) */
	private final int endLineNumber;

	/** Character offset at which the comment starts (zero based) */
	private final int startOffset;

	/** Character offset at which the comment ends (zero based) */
	private final int endOffset;

	/** Comment text */
	private final String text;

	/** The type of the comment */
	private ECommentType type;

	/** The kind of the comment */
	private final ETokenType kind;

	/** The language in which this comment is written. */
	private final ELanguage language;

	/** Escape character for line breaks in comments */
	public static final String ESCAPE_LINE_BREAK = "|";

	/**
	 * Constant that determines after how many characters the toString of the
	 * comment is truncated
	 */
	private static final int TRUNCATE_AFTER = 80;

	/**
	 * Constructor
	 */
	public Comment(int startLineNumber, int endLineNumber, int startOffset,
			int endOffset, String text, ECommentType type, ETokenType kind,
			ELanguage language) {
		this.startLineNumber = startLineNumber;
		this.endLineNumber = endLineNumber;
		this.startOffset = startOffset;
		this.endOffset = endOffset;
		this.text = text;

		this.type = type;
		this.kind = kind;
		this.language = language;
	}

	/**
	 * Constructor to create a comment with unknown type
	 */
	Comment(List<IToken> tokens) {
		this(tokens, ECommentType.UNKNOWN);
	}

	/** Constructor. */
	Comment(List<IToken> tokens, ECommentType type) {
		IToken firstToken = tokens.get(0);
		IToken lastToken = tokens.get(tokens.size() - 1);
		int lastTokenLines = StringUtils.splitLines(lastToken.getText().trim()).length;

		// line numbers are one based, whereas token line numbers are zero based
		startLineNumber = firstToken.getLineNumber() + 1;
		endLineNumber = lastToken.getLineNumber() + lastTokenLines;
		startOffset = firstToken.getOffset();
		endOffset = lastToken.getOffset() + lastToken.getText().length() - 1;
		text = extractText(tokens);

		this.type = type;
		kind = firstToken.getType();
		language = firstToken.getLanguage();
	}

	/**
	 * Extract the text from the tokens into a single string
	 */
	private String extractText(List<IToken> tokens) {
		StringBuilder content = new StringBuilder();

		Iterator<IToken> i = tokens.iterator();
		while (i.hasNext()) {
			content.append(i.next().getText().trim());
			if (i.hasNext()) {
				content.append(StringUtils.CR);
			}
		}
		return content.toString();
	}

	/** Get the type */
	public ECommentType getType() {
		return type;
	}

	/** Set the type */
	public void setType(ECommentType type) {
		this.type = type;
	}

	/** Get the kind */
	public ETokenType getKind() {
		return kind;
	}

	/** Get the start line number */
	public int getStartLineNumber() {
		return startLineNumber;
	}

	/** Get the end line number */
	public int getEndLineNumber() {
		return endLineNumber;
	}

	/** Get the number of lines */
	public int getNumberOfLines() {
		return endLineNumber - startLineNumber + 1;
	}

	/** Get the start offset */
	public int getStartOffset() {
		return startOffset;
	}

	/** Get the end offset */
	public int getEndOffset() {
		return endOffset;
	}

	/** Get the number of characters */
	public int getNumberOfCharacters() {
		return endOffset - startOffset + 1;
	}

	/** Get the comment text */
	public String getText() {
		return text;
	}

	/** Get the content of a comment, i.e. with the comment delimiters removed. */
	public String getContent() {
		return language.getCommentContent(text);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		String escapedText = text.replaceAll(StringUtils.CR,
				Comment.ESCAPE_LINE_BREAK);
		if (escapedText.length() > TRUNCATE_AFTER) {
			escapedText = escapedText.substring(0, TRUNCATE_AFTER) + "...";
		}
		return "Lines " + getStartLineNumber() + "-" + getEndLineNumber()
				+ ": " + escapedText;
	}
}