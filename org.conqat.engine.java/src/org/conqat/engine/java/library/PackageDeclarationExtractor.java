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
package org.conqat.engine.java.library;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.scanner.ELanguage;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IScanner;
import org.conqat.lib.scanner.IToken;
import org.conqat.lib.scanner.ScannerException;
import org.conqat.lib.scanner.ScannerFactory;

/**
 * This class extracts the package name from Java source files using a scanner.
 * This is package visible as it is only accessed using the {@link JavaLibrary}.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 8A53360F729D9134FD39A2F876763049
 */
public class PackageDeclarationExtractor {

	/** The scanner. */
	private final IScanner scanner = ScannerFactory.newScanner(ELanguage.JAVA,
			new StringReader(""), null);

	/**
	 * Get package name of the class that can be read from reader.
	 * 
	 * @param path
	 *            path used for error messages in exceptions.
	 * @return the package name or <code>null</code> if class is in the default
	 *         package
	 * @throws IOException
	 *             in case of an IO error.
	 * @throws ConQATException
	 *             in case of a scanner error.
	 */
	public String getPackageNameFromReader(String path, Reader reader)
			throws IOException, ConQATException {
		scanner.reset(reader, null);

		IToken token;

		StringBuilder packageDeclaration = new StringBuilder();

		try {
			while ((token = scanner.getNextToken()).getType() != ETokenType.EOF) {

				// loop until package keyword
				if (token.getType() == ETokenType.PACKAGE) {

					// loop from from package keyword to semicolon
					while ((token = scanner.getNextToken()).getType() != ETokenType.SEMICOLON) {

						/*
						 * A package name must be qualified identifier which is
						 * defined as (JLS 18.1)
						 * 
						 * QualifiedIdentifier: Identifier { . Identifier }
						 * 
						 * Therefore an Exception is raised if a token other
						 * then identifier or dot is encountered. However, it is
						 * not ensured that the name starts and ends with an
						 * identifier
						 */
						// we additionally allow the enum keyword to parse Java
						// 4 code (CR 3043)
						if (token.getType() != ETokenType.IDENTIFIER
								&& token.getType() != ETokenType.DOT
								&& token.getType() != ETokenType.ENUM) {
							throw new ConQATException("Illegal token '"
									+ token.getText() + "' (" + token.getType()
									+ ") in package statement in " + path);
						}

						// append to declaration
						packageDeclaration.append(token.getText());
					}

					// done after the first package declaration
					break;
				}
			}
		} catch (ScannerException e) {
			throw new ConQATException("Error while scanning " + path, e);
		} finally {
			reader.close();
		}

		// no package declaration found
		if (packageDeclaration.length() == 0) {
			return null;
		}

		return packageDeclaration.toString();
	}
}