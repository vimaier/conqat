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
package org.conqat.lib.scanner;

import static org.conqat.lib.commons.collections.CollectionUtils.asHashSet;
import static org.conqat.lib.scanner.ELanguageConstants.STANDARD_COMMENT_DELIMITERS;
import static org.conqat.lib.scanner.ETokenType.COLON;
import static org.conqat.lib.scanner.ETokenType.COMMA;
import static org.conqat.lib.scanner.ETokenType.DOT;
import static org.conqat.lib.scanner.ETokenType.EOL;
import static org.conqat.lib.scanner.ETokenType.EXCLAMATION;
import static org.conqat.lib.scanner.ETokenType.LBRACE;
import static org.conqat.lib.scanner.ETokenType.LEFT_ANGLE_BRACKET;
import static org.conqat.lib.scanner.ETokenType.MULTIPLE_EOL;
import static org.conqat.lib.scanner.ETokenType.QUESTION;
import static org.conqat.lib.scanner.ETokenType.RBRACE;
import static org.conqat.lib.scanner.ETokenType.RIGHT_ANGLE_BRACKET;
import static org.conqat.lib.scanner.ETokenType.SEMICOLON;
import static org.conqat.lib.scanner.ETokenType.SLASH;
import static org.conqat.lib.scanner.ETokenType.THEN;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.ListMap;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Enumeration class for the languages support by the scanner framework.
 * 
 * @author $Author: hummelb $
 * @version $Revision: 45274 $
 * @ConQAT.Rating GREEN Hash: A88DE0E8B4BEEADC5945E17010E9D382
 */
public enum ELanguage {

	// we need to use the ugly workaround with asHashSet here as we cannot use
	// varargs twice in the constructor

	/** Java */
	JAVA(asHashSet(SEMICOLON, RBRACE, LBRACE), STANDARD_COMMENT_DELIMITERS,
			true, "java"),

	/** C/C++ */
	CPP(asHashSet(SEMICOLON, RBRACE, LBRACE), STANDARD_COMMENT_DELIMITERS,
			true, "cpp", "cc", "c", "h", "hh", "hpp", "cxx", "hxx", "inl", "pc"),

	/** Visual Basic */
	VB(asHashSet(COLON, EOL), asHashSet("'"), false, "vb", "frm", "cls", "bas"),

	/** PL/I */
	PL1(asHashSet(SEMICOLON, RBRACE, LBRACE), STANDARD_COMMENT_DELIMITERS,
			false, "pl1", "pli"),

	/** COBOL */
	COBOL(new CobolStatementOracle(), asHashSet("/", "*"), false, "cbl", "cob",
			"cobol", "cpy"),

	/** C# */
	CS(asHashSet(SEMICOLON, RBRACE, LBRACE),
			asHashSet("///", "//", "/*", "*/"), true, "cs"),

	/** ABAP */
	ABAP(asHashSet(DOT), asHashSet("*", "\""), false, "abap"),

	/** Ada */
	ADA(asHashSet(SEMICOLON, THEN), asHashSet("--"), false, "ada", "ads", "adb"),

	/** Natural language text */
	TEXT(asHashSet(DOT, QUESTION, EXCLAMATION, COLON, COMMA, MULTIPLE_EOL),
			Collections.<String> emptySet(), false, "txt"),

	/** XML */
	XML(asHashSet(LEFT_ANGLE_BRACKET, SLASH, RIGHT_ANGLE_BRACKET), asHashSet(
			"<!--", "-->"), true, "xml", "xsl", "xslt"),

	/** PL/SQL */
	PLSQL(asHashSet(SEMICOLON), asHashSet("/*", "*/", "--"), false, "sql",
			"pks", "pkb", "trg", "fnc", "typ", "tyb", "prc"),

	/** Python */
	PYTHON(asHashSet(EOL), asHashSet("#"), false, "py"),

	/** T-SQL aka Transact SQL. */
	TSQL(asHashSet(EOL), asHashSet("/*", "*/", "--"), false, "tsql"),

	/** Matlab */
	MATLAB(asHashSet(EOL, SEMICOLON), asHashSet("%"), true, "m"),

	/** PHP */
	PHP(asHashSet(SEMICOLON, RBRACE, LBRACE), STANDARD_COMMENT_DELIMITERS,
			true, "php", "php3", "php4", "php5"),

	/**
	 * JavaScript.
	 * <p>
	 * Note that the statement oracle only works if semicolons are used
	 * consistently. However, semicolons are optional in JavaScript (rules
	 * described here: http://bclary.com/2004/11/07/#a-7.9), but to determine
	 * end of statement in this case requires a full blown parser (hard to
	 * decide locally in some cases). As most coding guidelines recommend using
	 * semicolons anyway, we stick with this solution.
	 */
	JAVASCRIPT(asHashSet(SEMICOLON, RBRACE, LBRACE),
			STANDARD_COMMENT_DELIMITERS, true, "js", "sj"),

	/** The language used within the M/Text printing system. */
	MTEXT(asHashSet(EOL), asHashSet(".DSC"), true, "mtx"),

	/**
	 * The "Just Your Average Computer Company Procedural Language". A C-like
	 * language being part of the Panther framework developed by the company
	 * Prolifics. The language is used in the archive system d.3 developed by
	 * the company "d.velop".
	 */
	JPL(asHashSet(EOL), STANDARD_COMMENT_DELIMITERS, true, "jpl"),

	/**
	 * Use this for languages for which no dedicated scanner is available.
	 * Creates a token per line (and creates EOL tokens).
	 */
	LINE(asHashSet(EOL), CollectionUtils.<String> emptySet(), false);

	/** This maps from extensions to languages. */
	private static ListMap<String, ELanguage> extension2LanguageMap = new ListMap<String, ELanguage>();

	/** The statement oracle for this language. */
	private final IStatementOracle statementOracle;

	/** Initialize {@link #extension2LanguageMap}. */
	static {
		for (ELanguage language : values()) {
			for (String extension : language.extensions) {
				extension2LanguageMap.add(extension.toLowerCase(), language);
			}
		}
	}

	/** File extensions commonly used for this language. */
	private final String[] extensions;

	/** Delimiters for comments. */
	private final Set<String> commentDelimiters;

	/** Whether the language is case sensitive. */
	private final boolean caseSensitive;

	/** Create language. */
	private ELanguage(Set<ETokenType> statementDelimiters,
			Set<String> commentDelimiters, boolean caseSensitive,
			String... extensions) {
		this(new StatementOracle(statementDelimiters), commentDelimiters,
				caseSensitive, extensions);
	}

	/** Create language. */
	private ELanguage(IStatementOracle oracle, Set<String> commentDelimiters,
			boolean caseSensitive, String... extensions) {
		statementOracle = oracle;
		this.commentDelimiters = commentDelimiters;
		this.caseSensitive = caseSensitive;
		this.extensions = extensions;
	}

	/** Get the file extensions commonly used for this language. */
	public String[] getFileExtensions() {
		return CollectionUtils.copyArray(extensions);
	}

	/** Get statement oracle for this language. */
	public IStatementOracle getStatementOracle() {
		return statementOracle;
	}

	/**
	 * Gets the {@link ELanguage} value corresponding to the file extension of
	 * the path. Returns null if no extension was found. If there are multiple
	 * possible languages, the first one is returned.
	 */
	public static ELanguage fromPath(String path) {
		return fromFile(new File(path));
	}

	/**
	 * Gets the {@link ELanguage} value corresponding to the file extension of
	 * the file. Returns null if no extension was found. If there are multiple
	 * possible languages, the first one is returned.
	 */
	public static ELanguage fromFile(File file) {
		return fromFileExtension(FileSystemUtils.getFileExtension(file));
	}

	/**
	 * Gets the {@link ELanguage} value corresponding to the given file
	 * extension (without a dot). Returns null if no extension was found. If
	 * there are multiple possible languages, the first one is returned.
	 */
	public static ELanguage fromFileExtension(String extension) {
		if (extension == null) {
			return null;
		}

		List<ELanguage> result = extension2LanguageMap.getCollection(extension
				.toLowerCase());

		if (result == null || result.isEmpty()) {
			return null;
		}
		return result.get(0);
	}

	/**
	 * Get the content of a comment, i.e. with the comment delimiters removed.
	 */
	public String getCommentContent(String commentText) {
		StringBuffer content = new StringBuffer();
		String[] lines = StringUtils.splitLines(commentText);
		for (String line : lines) {
			if (content.length() > 0) {
				content.append(StringUtils.CR);
			}
			for (String delimiter : commentDelimiters) {
				line = line.trim();
				line = StringUtils.stripPrefix(delimiter, line);
				line = StringUtils.stripSuffix(delimiter, line);
				line = line.trim();
			}
			content.append(line);
		}
		return content.toString();
	}

	/** Return whether the language is case sensitive. */
	public boolean isCaseSensitive() {
		return caseSensitive;
	}
}