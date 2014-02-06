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
package org.conqat.engine.text.language;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * This is a helper class containing a small program for generating language
 * tables (see {@link LetterPairDistribution}) from text files.
 * 
 * @author hummelb
 * @author $Author: juergens $
 * @version $Rev: 35207 $
 * @ConQAT.Rating GREEN Hash: A1CDA60B87D05A5661DBB0FF358ABB8B
 */
public class LangugeTableGenerator {

	/** Calculates a new distribution from a long text file. */
	private static LetterPairDistribution calculateFromString(String text) {
		LetterPairDistribution lpd = new LetterPairDistribution();

		char[] chars = LetterPairDistribution.prepareText(text);
		for (int i = 1; i < chars.length; ++i) {
			if (chars[i - 1] != '.') {
				String s = new String(chars, i - 1, 2);
				lpd.insertPair(s);
			}
		}

		lpd.normalize();
		return lpd;
	}

	/** Simple program for computing a table from one or more files. */
	public static void main(String[] args) throws IOException {
		if (args.length == 0) {
			System.err.println("Usage: provide files on command line!");
			System.exit(1);
		}
		StringBuilder sb = new StringBuilder();
		for (String arg : args) {
			sb.append(FileSystemUtils.readFile(new File(arg)));
			sb.append(StringUtils.CR);
		}
		OutputStreamWriter out = new OutputStreamWriter(System.out,
				FileSystemUtils.UTF8_ENCODING);
		calculateFromString(sb.toString()).writeTable(out);
		out.flush();
	}
}