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
package org.conqat.engine.sourcecode.resource;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Iterator;

import org.conqat.lib.cqddl.function.CQDDLEvaluationException;
import org.conqat.lib.cqddl.function.ICQDDLFunction;
import org.conqat.engine.resource.IContentAccessor;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.build.IElementFactory;
import org.conqat.engine.resource.build.ResourceBuilder;
import org.conqat.engine.resource.scope.filesystem.FileContentAccessor;
import org.conqat.engine.resource.scope.filesystem.FileSystemScope;
import org.conqat.engine.resource.test.ResourceProcessorTestCaseBase;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.engine.sourcecode.resource.TokenElement;
import org.conqat.engine.sourcecode.resource.TokenElementFactory;
import org.conqat.engine.sourcecode.resource.TokenResourceSelector;

import org.conqat.lib.commons.collections.PairList;
import org.conqat.lib.commons.filesystem.CanonicalFile;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.ELogLevel;
import org.conqat.engine.core.logging.testutils.CollectingLogger;
import org.conqat.lib.scanner.ELanguage;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;

/**
 * Base classes for token tests.
 * 
 * @author $Author: goede $
 * @version $Rev: 43226 $
 * @ConQAT.Rating GREEN Hash: 4AD5A57DFCA651B7316957237532C603
 */
public abstract class TokenTestCaseBase extends ResourceProcessorTestCaseBase {

	/** Register new functions. */
	protected TokenTestCaseBase() {
		parsingParameters.registerFunction("tokenFactory",
				new ICQDDLFunction() {
					@Override
					public Object eval(PairList<String, Object> arg0)
							throws CQDDLEvaluationException {
						try {
							return executeProcessor(TokenElementFactory.class,
									"()");
						} catch (Exception e) {
							throw new CQDDLEvaluationException(
									"Failed to construct factory!", e);
						}
					}
				});
	}

	/**
	 * Create a new token element with the specified content. The language is
	 * {@link ELanguage#JAVA}. The uniform path is "foo".
	 */
	protected ITokenElement createTokenElement(String content) throws Exception {
		return createTokenElement(content, ELanguage.JAVA);
	}

	/**
	 * Create a new token element with the specified content and language. The
	 * uniform path is "foo".
	 */
	protected ITokenElement createTokenElement(String content,
			ELanguage language) throws Exception {
		IElementFactory factory = (IElementFactory) executeProcessor(
				TokenElementFactory.class, "(language=(name=", language, "))");
		IContentAccessor[] accessors = (IContentAccessor[]) parseCQDDL("memScope(foo='"
				+ content + "')");
		IElement element = factory.create(accessors[0]);

		assertTrue(element instanceof ITokenElement);
		return (ITokenElement) element;
	}

	/**
	 * Creates a new token element with the specified content.
	 * 
	 * @param testFile
	 *            File that contains content of token element
	 * @param language
	 *            Language of file
	 */
	protected ITokenElement createTokenElement(CanonicalFile testFile,
			ELanguage language) {
		IContentAccessor accessor = new FileContentAccessor(testFile,
				testFile.getParentFile(), "TEST");
		return new TokenElement(accessor, Charset.defaultCharset(), language);
	}

	/**
	 * Retrieves tokens from an element and checks if the returned tokens have
	 * the expected types. It is also checked if the correct number of log
	 * messages is generated at the specified log level.
	 * */
	protected void assertTokens(ITokenElement element, ELogLevel minLogLevel,
			int expectedMessages, ETokenType... types) throws ConQATException {
		CollectingLogger logger = new CollectingLogger(minLogLevel);
		Iterator<IToken> it = element.getTokens(logger).iterator();

		for (ETokenType type : types) {
			if (!it.hasNext()) {
				fail("Not enough tokens");
			}
			IToken token = it.next();
			assertEquals("Type mismatch", type, token.getType());
		}

		// make sure that there are no tokens left in the scanner
		assertFalse("More tokens found than expected token types specified",
				it.hasNext());

		int messageCount = logger.getMessages().size();
		assertEquals("Had " + messageCount + " messages. " + expectedMessages
				+ " are expected.", expectedMessages, messageCount);
	}

	/**
	 * Creates a token scope from a directory, i.e. a hierarchy of text
	 * elements/containers. The pattern arrays may be null.
	 */
	protected ITokenResource createTokenScope(File rootDirectory,
			ELanguage language, String[] includePattern, String[] excludePattern)
			throws Exception {
		Object factory = executeProcessor(TokenElementFactory.class,
				"(language=(name=", language.name(), "))");
		IResource resource = createScope(rootDirectory, includePattern,
				excludePattern, factory);
		return (ITokenResource) executeProcessor(TokenResourceSelector.class,
				"(input=(ref=", resource, "))");
	}

	/** Creates a token resource hierarchy for the given directory. */
	protected ITokenResource createTokenResourceHierarchyFor(File rootDir)
			throws ConQATException {
		try {

			IContentAccessor[] accessors = (IContentAccessor[]) executeProcessor(
					FileSystemScope.class, "(root=(dir='", rootDir.getPath(),
					"'),include=(pattern='**/*.java'))");
			Object factory = executeProcessor(TokenElementFactory.class,
					"(language=(name=JAVA))");
			IResource resource = (IResource) executeProcessor(
					ResourceBuilder.class, "(scope=(ref=", accessors,
					"), factory=(pattern='**', ref=", factory, "))");
			return (ITokenResource) executeProcessor(
					TokenResourceSelector.class, "(input=(ref=", resource, "))");
		} catch (Exception e) {
			throw new ConQATException(e);
		}
	}
}