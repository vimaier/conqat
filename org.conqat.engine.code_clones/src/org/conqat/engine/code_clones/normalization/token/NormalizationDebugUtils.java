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
package org.conqat.engine.code_clones.normalization.token;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.conqat.engine.code_clones.core.TokenUnit;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.IConQATLogger;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.lib.commons.filesystem.CanonicalFile;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.scanner.IToken;

/**
 * Utility functions for writing debug files.
 * 
 * @author $Author: juergens $
 * @version $Rev: 34670 $ 16369
 * @ConQAT.Rating GREEN Hash: 068250948F8630EC18AEA896C63DCB48
 */
/* package */class NormalizationDebugUtils {

	/**
	 * Copies all whitespace between a start and an end index (both inclusive)
	 * from a string into a StringBuilder. Encountered non-whitespace characters
	 * are replaced with spaces. This way, the tabulation and line breaks from
	 * the source string are preserved in the target {@link StringBuilder}.
	 */
	/* package */static void copyWhitespace(int startOffset, int endOffset,
			String source, StringBuilder target) {
		if (startOffset > endOffset) {
			return;
		}

		for (int pos = startOffset; pos <= endOffset; pos++) {
			char c = source.charAt(pos);
			if (Character.isWhitespace(c)) {
				target.append(c);
			} else {
				target.append(" ");
			}
		}
	}

	/** Writes a debug file */
	/* package */static void writeDebugFile(CanonicalFile originalFile,
			ITokenElement element, Map<IToken, TokenUnit> normalization,
			IConQATLogger logger, String debugFileExtension) {

		try {
			String originalContent = element.getUnfilteredTextContent();
			File debugFile = new File(originalFile.getCanonicalPath()
					+ debugFileExtension);
			FileSystemUtils.writeFile(debugFile,
					createDebugFileContent(originalContent, normalization));
		} catch (IOException e) {
			logDebugFileException(originalFile, e, logger);
		} catch (ConQATException e) {
			logDebugFileException(originalFile, e, logger);
		}
	}

	/** Write exception to log file */
	private static void logDebugFileException(File originalFile, Throwable e,
			IConQATLogger logger) {
		logger.warn("Could not write debug file for : " + originalFile + ": "
				+ e.getMessage());
	}

	/**
	 * Creates the debug file content for an input file and its token list. This
	 * method attempts to replace each token or the original file with its
	 * normalized counterpart while preserving line and column numbers as good
	 * as possible.
	 */
	private static String createDebugFileContent(String originalContent,
			Map<IToken, TokenUnit> normalization) {
		StringBuilder content = new StringBuilder();
		int lastOffset = 0;

		// Since normalization is a LinkedHashMap, the tokens are in the
		// insertion sequence
		for (IToken originalToken : normalization.keySet()) {
			TokenUnit normalizedToken = normalization.get(originalToken);

			if (normalizedToken == null) {
				continue;
			}

			NormalizationDebugUtils.copyWhitespace(lastOffset,
					originalToken.getOffset() - 1, originalContent, content);
			content.append(normalizedToken.getContent());
			lastOffset = originalToken.getOffset()
					+ normalizedToken.getContent().length();
		}
		return content.toString();
	}

}