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
package org.conqat.engine.java.library;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.symboltable.SymbolFacade;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.factory.IParameterizedFactory;

/**
 * Factory for creating Java ASTs from elements described via
 * {@link JavaASTCacheKey}.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 47044 $
 * @ConQAT.Rating GREEN Hash: D898C74D29973DBC951AC8A2C0C03A4F
 */
/* package */class JavaASTCacheFactory
		implements
		IParameterizedFactory<ASTCompilationUnit, JavaASTCacheKey, ConQATException> {

	/** {@inheritDoc} */
	@Override
	public ASTCompilationUnit create(JavaASTCacheKey key)
			throws ConQATException {
		ITokenElement element = key.getElement();
		CCSMAssert.isNotNull(element, "The element should not be null, "
				+ "as creation is only issued from the cache via a live "
				+ "TextElement, which in turn means that the reference must "
				+ "still be valid.");

		Reader reader = new StringReader(element.getTextContent());
		Parser parser = LanguageVersion.JAVA_17.getLanguageVersionHandler()
				.getParser(new ParserOptions());
		ASTCompilationUnit compilationUnit = null;
		try {
			compilationUnit = (ASTCompilationUnit) parser.parse(null, reader);
			SymbolFacade sf = new SymbolFacade();
			sf.initializeWith(compilationUnit);
		} catch (RuntimeException ex) {
			// we deliberately catch everything here to be
			// robust against PMD bugs
			throw new ConQATException("Parsing error for " + element + ": "
					+ ex.getMessage(), ex);
		}
		try {
			reader.close();
		} catch (IOException e) {
			CCSMAssert.fail("Can't happen, reading from string");
		}
		return compilationUnit;
	}
}