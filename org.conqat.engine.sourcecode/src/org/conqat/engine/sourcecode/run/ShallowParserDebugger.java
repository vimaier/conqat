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
package org.conqat.engine.sourcecode.run;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.driver.runner.ConQATRunnableBase;
import org.conqat.engine.sourcecode.shallowparser.IShallowParser;
import org.conqat.engine.sourcecode.shallowparser.ShallowParserFactory;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;
import org.conqat.lib.commons.enums.EnumUtils;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.options.AOption;
import org.conqat.lib.scanner.ELanguage;
import org.conqat.lib.scanner.IToken;
import org.conqat.lib.scanner.ScannerUtils;

/**
 * A {@link ConQATRunnableBase} that dumps the serialized shallow parsed AST to
 * stdout. This is used to debug parsing issues for cases where no IDE can be
 * installed and the source code can not be fetched.
 * 
 * @author $Author: goede $
 * @version $Rev: 44968 $
 * @ConQAT.Rating GREEN Hash: 516C6B5721AAD115362D162BAEB8FBAD
 */
public class ShallowParserDebugger extends ConQATRunnableBase {

	/** The file to parse. */
	private String filename;

	/** The language used for parsing. */
	private ELanguage language;

	/** {@inheritDoc} */
	@Override
	protected void doRun() {
		if (filename == null || language == null) {
			System.err.println("Must provide both filename and language!");
			printUsageAndExit();
		}

		try {
			String content = FileSystemUtils.readFileUTF8(new File(filename));
			List<IToken> tokens = ScannerUtils.getTokens(content, language);
			IShallowParser parser = ShallowParserFactory.createParser(language);
			List<ShallowEntity> entities = parser.parseTopLevel(tokens);

			for (ShallowEntity entity : entities) {
				System.out.println(entity);
			}
		} catch (IOException e) {
			System.err.println("Failed to read file " + filename + ": "
					+ e.getMessage());
		} catch (ConQATException e) {
			System.err.println("Failed to parse file: " + e.getMessage());
		}
	}

	/** Sets the filename. */
	@AOption(shortName = 'f', description = "The name of the file to parse")
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/** Sets the language. */
	@AOption(shortName = 'l', description = "The language used for parsing")
	public void setLanguage(String language) {
		this.language = EnumUtils.valueOfIgnoreCase(ELanguage.class, language);
		if (this.language == null) {
			throw new IllegalArgumentException("Language " + language
					+ " not found!");
		}
	}
}
