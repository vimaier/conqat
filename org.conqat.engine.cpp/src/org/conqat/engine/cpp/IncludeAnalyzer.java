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
package org.conqat.engine.cpp;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;


import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.engine.sourcecode.resource.TokenElementProcessorBase;
import org.conqat.lib.scanner.ELanguage;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @levd.rating GREEN Hash: 2A7400F7703F2010B192B0176D2420D6
 */
@AConQATProcessor(description = "This processor extracts all include targets "
		+ "from a C/C++ file and stores them as a string list at the "
		+ "including node. This processor additionally checks if the "
		+ "included file can be located either in the directory of "
		+ "the including file or in a separately specified include "
		+ "directory. If the file can't be found a warning is logged. "
		+ "This processor only works on code in the file system due to "
		+ "the complex lookup mechanism. Works only on files in the "
		+ "local file system")
public class IncludeAnalyzer extends TokenElementProcessorBase {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Include target list", type = "java.util.List<String>")
	public static final String KEY_TARGETS = "Include targets";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Resolved targets", type = "java.util.List<String>")
	public static final String KEY_RESOLVED = "Resolved targets";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Unresolved targets", type = "java.util.List<String>")
	public static final String KEY_UNRESOLVED = "Unresolved targets";

	/** List of include directories. */
	private HashSet<File> includeDirectories = new HashSet<File>();

	/** Flag for excluding angle bracket includes. */
	private boolean excludeAngleBracketIncludes = true;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "include", description = "Include directory")
	public void addResult(
			@AConQATAttribute(name = "dir", description = "directory name") String directoryName) {
		includeDirectories.add(new File(directoryName));
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "excludeAngleBracketIncludes", minOccurrences = 0, maxOccurrences = 1, description = "If true angle bracket includes "
			+ "(as opposed to quoted ones) are excluded from resolution.")
	public void setExcludeAngleBracketIncludes(
			@AConQATAttribute(name = "value", description = "true/false (true by default)") boolean exclude) {
		this.excludeAngleBracketIncludes = exclude;
	}

	/** {@inheritDoc} */
	@Override
	protected void setUp(ITokenResource root) throws ConQATException {
		super.setUp(root);

		NodeUtils.addToDisplayList(root, KEY_TARGETS, KEY_RESOLVED,
				KEY_UNRESOLVED);
		checkIncludeDirectories();
	}

	/**
	 * Check if the specified include directories are directories and if they
	 * exist. Trims include directory list accordingly.
	 */
	private void checkIncludeDirectories() {
		HashSet<File> existingDirectories = new HashSet<File>();

		for (File directory : includeDirectories) {
			if (!directory.exists()) {
				getLogger().warn(
						"Include directory " + directory + " does not exist.");
				continue;
			}
			if (!directory.isDirectory()) {
				getLogger().warn(
						"Include directory " + directory
								+ " is not a directory.");
				continue;
			}
			existingDirectories.add(directory);
		}
		includeDirectories = existingDirectories;
	}

	/** {@inheritDoc} */
	@Override
	protected void processElement(ITokenElement node) throws ConQATException {

		if (node.getLanguage() != ELanguage.CPP) {
			throw new ConQATException("This class only works with C/C++ code!");
		}

		List<IToken> tokens = null;
		try {
			tokens = node.getTokens(getLogger());
		} catch (ConQATException e) {
			getLogger().warn(
					"Couldn't read " + node.getLocation() + ": "
							+ e.getMessage());
			return;
		}

		HashSet<String> targets = new HashSet<String>();
		HashSet<String> resolvedTargets = new HashSet<String>();
		HashSet<String> unresolvedTargets = new HashSet<String>();

		for (int i = 0; i < tokens.size(); i++) {
			IToken token = tokens.get(i);
			if (token.getType() == ETokenType.PREPROCESSOR_DIRECTIVE) {
				if (token.getText().indexOf("include") >= 0) {
					analyzeIncludeDirective(node, tokens, targets,
							resolvedTargets, unresolvedTargets, i);
				}
			}
		}

		node.setValue(KEY_TARGETS, CollectionUtils.sort(targets));
		node.setValue(KEY_RESOLVED, CollectionUtils.sort(resolvedTargets));
		node.setValue(KEY_UNRESOLVED, CollectionUtils.sort(unresolvedTargets));
	}

	/**
	 * Analyze an include directive.
	 * 
	 * @param targets
	 *            set to store the bare targets
	 * @param resolvedTargets
	 *            set to store resolved targets
	 * @param unresolvedTargets
	 *            set to store unresolved targets
	 * @param currenTokenPosition
	 *            postion of the include token within the token list
	 */
	private void analyzeIncludeDirective(ITokenElement node,
			List<IToken> tokens, HashSet<String> targets,
			HashSet<String> resolvedTargets, HashSet<String> unresolvedTargets,
			int currenTokenPosition) {

		String includeTarget = extractIncludeTarget(tokens, currenTokenPosition);

		if (StringUtils.isEmpty(includeTarget)) {
			getLogger()
					.warn(
							"Could not extract include target in "
									+ node.getLocation());
			return;
		}

		targets.add(includeTarget);

		// do not resolve angle bracket includes if excluded
		if (includeTarget.startsWith("<") && excludeAngleBracketIncludes) {
			return;
		}

		// remove quotes or brackets
		String bareIncludeTarget = includeTarget.substring(1, includeTarget
				.length() - 1);

		String resolvedTarget = resolveIncludeTarget(node, bareIncludeTarget);
		if (resolvedTarget == null) {
			unresolvedTargets.add(bareIncludeTarget);
			getLogger().warn(
					"Element " + node.getLocation()
							+ " references non-existing file " + includeTarget);
		} else {
			resolvedTargets.add(resolvedTarget);
		}

	}

	/**
	 * Resolve included target by searching in the directory of the including
	 * element or in one of the specified include directories.
	 * 
	 * @return the full path of the included target or <code>null</code> if
	 *         could not be resolved.
	 */
	private String resolveIncludeTarget(ITokenElement node, String includeTarget) {

		// check if it is in same path as including file
		File parentDir = new File(node.getLocation()).getParentFile();
		File targetFile = new File(parentDir, includeTarget);
		if (targetFile.exists()) {
			try {
				return targetFile.getCanonicalPath();
			} catch (IOException e) {
				getLogger().warn(
						"Couldn't resolve canonical path for: " + targetFile);
				return targetFile.getAbsolutePath();
			}
		}

		// check if one of the include directories contains it
		for (File directory : includeDirectories) {
			targetFile = new File(directory, includeTarget);
			if (targetFile.exists()) {
				try {
					return targetFile.getCanonicalPath();
				} catch (IOException e) {
					getLogger().warn(
							"Couldn't resolve canonical path for: "
									+ targetFile);
					return targetFile.getAbsolutePath();
				}
			}
		}

		return null;
	}

	/**
	 * Extracts the include target from an include statement.
	 * 
	 * @param tokens
	 *            list of tokens.
	 * @param i
	 *            position of the #include preprocessor directive token within
	 *            the token list
	 * @return the include target or <code>null</code> if it could not be
	 *         extracted. Currently the targets specified with double quotes are
	 *         returned without the quotes, target specified by angle brackets
	 *         are returned with brackets.
	 */
	private String extractIncludeTarget(List<IToken> tokens, int i) {

		// check boundaries
		if (i >= tokens.size() - 1) {
			return null;
		}

		i++;
		IToken nextToken = tokens.get(i);
		if (nextToken.getType() == ETokenType.STRING_LITERAL) {
			return nextToken.getText();

		}

		if (nextToken.getType() == ETokenType.LT) {
			StringBuilder target = new StringBuilder();
			target.append(nextToken.getText());

			// check boundaries
			if (i >= tokens.size() - 1) {
				return null;
			}

			i++;
			nextToken = tokens.get(i);

			do {
				target.append(nextToken.getText());

				// check boundaries
				if (i >= tokens.size() - 1) {
					return null;
				}

				i++;
				nextToken = tokens.get(i);
			} while (nextToken.getType() != ETokenType.GT && i < tokens.size());

			target.append(nextToken.getText());

			return target.toString();
		}

		return null;
	}

}