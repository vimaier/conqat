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
package org.conqat.engine.resource.util;

import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.filesystem.AntPatternDirectoryScanner;
import org.conqat.lib.commons.filesystem.AntPatternUtils;

/**
 * ConQAT engine wrapper class for the {@link AntPatternDirectoryScanner}.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: EE51B2936CE80CAD61F11A278DB65C0A
 */
public class ConQATDirectoryScanner {

	/** Converts an ANT pattern to a regex pattern. */
	public static Pattern convertPattern(String antPattern,
			boolean caseSensitive) throws ConQATException {
		try {
			return AntPatternUtils.convertPattern(antPattern, caseSensitive);
		} catch (PatternSyntaxException e) {
			throw new ConQATException(e.getMessage(), e);
		}
	}

	/**
	 * Performs directory scanning.
	 * 
	 * @param baseDir
	 *            the directory to start scanning in. All file names returned
	 *            will be relative to this file.
	 * @param caseSensitive
	 *            whether pattern should be applied case sensitive or not.
	 * @param includePatterns
	 *            the include pattern (use ANT's pattern syntax)
	 * @param excludePatterns
	 *            the exclude pattern (use ANT's pattern syntax)
	 * @throws ConQATException
	 *             in case of invalid pattern provided or IO problems.
	 */
	public static String[] scan(String baseDir, boolean caseSensitive,
			String[] includePatterns, String[] excludePatterns)
			throws ConQATException {
		try {
			return AntPatternDirectoryScanner.scan(baseDir, caseSensitive,
					includePatterns, excludePatterns);
		} catch (IOException e) {
			throw new ConQATException(
					"Error while scanning: " + e.getMessage(), e);
		}
	}
}