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
package org.conqat.engine.resource.test;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.commons.test.ConQATCommonsProcessorTestCaseBase;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IContainer;
import org.conqat.engine.resource.IContentAccessor;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.base.ContainerBase;
import org.conqat.engine.resource.binary.BinaryElementFactory;
import org.conqat.engine.resource.build.IElementFactory;
import org.conqat.engine.resource.build.ResourceBuilder;
import org.conqat.engine.resource.scope.filesystem.FileContentAccessor;
import org.conqat.engine.resource.scope.filesystem.FileSystemScope;
import org.conqat.engine.resource.scope.memory.InMemoryContentAccessor;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.engine.resource.text.TextElement;
import org.conqat.engine.resource.text.TextElementFactory;
import org.conqat.engine.resource.text.TextResourceSelector;
import org.conqat.engine.resource.text.filter.RegexTextFilter;
import org.conqat.engine.resource.text.filter.base.ITextFilter;
import org.conqat.lib.commons.collections.PairList;
import org.conqat.lib.commons.filesystem.CanonicalFile;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.cqddl.function.CQDDLEvaluationException;
import org.conqat.lib.cqddl.function.ICQDDLFunction;

/**
 * Base class for tests that work on processors dealing with resources.
 * <p>
 * Registers the following CQDDL functions:
 * <ul>
 * <li>memScope: creates and array of content accessors from arguments, where
 * the element's key is used as uniform path and the value is the content (may
 * be either String or byte[])</li>
 * <li>binFactory: takes no parameters and returns a
 * {@link BinaryElementFactory}</li>
 * <li>textFactory: takes no parameters and returns a {@link TextElementFactory}
 * </li>
 * </ul>
 * 
 * @author $Author: juergens $
 * @version $Rev: 40963 $
 * @ConQAT.Rating GREEN Hash: FF98E7E048499DAAA625A0E5247B14A7
 */
public abstract class ResourceProcessorTestCaseBase extends
		ConQATCommonsProcessorTestCaseBase {

	/** Constructor (used to register CQDDL functions). */
	protected ResourceProcessorTestCaseBase() {

		parsingParameters.registerFunction("memScope", new ICQDDLFunction() {
			@Override
			public IContentAccessor[] eval(PairList<String, Object> params)
					throws CQDDLEvaluationException {
				IContentAccessor[] result = new IContentAccessor[params.size()];
				for (int i = 0; i < result.length; ++i) {
					if (params.getSecond(i) instanceof byte[]) {
						result[i] = new InMemoryContentAccessor(params
								.getFirst(i), (byte[]) params.getSecond(i));
					} else if (params.getSecond(i) instanceof String) {
						result[i] = new InMemoryContentAccessor(params
								.getFirst(i), ((String) params.getSecond(i))
								.getBytes());
					} else {
						throw new CQDDLEvaluationException(
								"Unsupported value type: "
										+ params.getSecond(i).getClass());
					}
				}
				return result;
			}
		});

		parsingParameters.registerFunction("binFactory", new ICQDDLFunction() {
			@Override
			public Object eval(PairList<String, Object> arg0)
					throws CQDDLEvaluationException {
				try {
					return executeProcessor(BinaryElementFactory.class, "()");
				} catch (Exception e) {
					throw new CQDDLEvaluationException(
							"Failed to construct factory!", e);
				}
			}
		});

		parsingParameters.registerFunction("textFactory", new ICQDDLFunction() {
			@Override
			public Object eval(PairList<String, Object> arg0)
					throws CQDDLEvaluationException {
				try {
					return executeProcessor(TextElementFactory.class, "()");
				} catch (Exception e) {
					throw new CQDDLEvaluationException(
							"Failed to construct factory!", e);
				}
			}
		});
	}

	/**
	 * Creates a binary scope from a directory, i.e. a hierarchy of binary
	 * elements/containers. The pattern arrays may be null.
	 */
	protected IResource createBinaryScope(File rootDirectory,
			String[] includePattern, String[] excludePattern) throws Exception {
		return createScope(rootDirectory, includePattern, excludePattern,
				"binFactory()");
	}

	/**
	 * Creates a basic scope from a directory, i.e. a hierarchy of
	 * elements/containers. The pattern arrays may be null.
	 * 
	 * @param factory
	 *            this may either be an instance of {@link IElementFactory} or a
	 *            CQDDL-String describing the factory.
	 */
	protected IResource createScope(File rootDirectory,
			String[] includePattern, String[] excludePattern, Object factory)
			throws ConQATException {
		StringBuilder additionalArgs = new StringBuilder();
		if (includePattern != null) {
			for (String pattern : includePattern) {
				additionalArgs.append(",include=(pattern='" + pattern + "')");
			}
		}

		if (excludePattern != null) {
			for (String pattern : excludePattern) {
				additionalArgs.append(",exclude=(pattern='" + pattern + "')");
			}
		}

		IContentAccessor[] accessors = (IContentAccessor[]) executeProcessor(
				FileSystemScope.class, "(root=(dir='",
				rootDirectory.getAbsolutePath(), "'), project=(name=TEST)",
				additionalArgs.toString(), ")");

		IResource resource = (IResource) executeProcessor(
				ResourceBuilder.class, "(scope=(ref=", accessors,
				"), factory=(pattern='**', ref=", factory, "))");
		return resource;
	}

	/**
	 * Creates a text scope from a directory, i.e. a hierarchy of text
	 * elements/containers. The pattern arrays may be null.
	 */
	protected ITextResource createTextScope(File rootDirectory,
			String[] includePattern, String[] excludePattern)
			throws ConQATException {
		IResource resource = createScope(rootDirectory, includePattern,
				excludePattern, "textFactory()");
		return (ITextResource) executeProcessor(TextResourceSelector.class,
				"(input=(ref=", resource, "))");
	}

	/** Creates a dummy {@link ITextElement}. */
	protected ITextElement dummyTextElement() {
		return dummyTextElement("CONTENT");
	}

	/** Creates a dummy {@link ITextElement} with specified content. */
	protected ITextElement dummyTextElement(String content) {
		IContentAccessor accessor = new InMemoryContentAccessor("path",
				content.getBytes());
		return new TextElement(accessor, Charset.defaultCharset());
	}

	/**
	 * Checks that no empty containers are found and all containers are
	 * instances of the given class.
	 */
	protected void assertNoEmptyContainer(IResource root,
			Class<? extends IContainer> containerClass) {
		if (root instanceof IContainer) {
			assertTrue(root.hasChildren());
			assertEquals(containerClass, root.getClass());
			for (IResource child : root.getChildren()) {
				assertNoEmptyContainer(child, containerClass);
			}
		}
	}

	/**
	 * Ensures that a slash separated path can be traversed and ends in the
	 * given target class.
	 */
	@SuppressWarnings("unchecked")
	protected <T extends ContainerBase<?>> void assertValidPath(T container,
			String path, Class<T> containerClass,
			Class<? extends IElement> targetClass) {
		if (path.contains("/")) {
			String[] parts = path.split("/", 2);
			IResource child = container.getNamedChild(parts[0]);
			assertNotNull(child);
			assertEquals(containerClass, child.getClass());
			assertValidPath((T) child, parts[1], containerClass, targetClass);
		} else {
			assertEquals(targetClass, container.getNamedChild(path).getClass());
		}
	}

	/** Creates a regex filter. */
	protected ITextFilter regexFilter(String regex) throws ConQATException {
		return regexFilter(regex, false);
	}

	/** Creates a regex filter. */
	protected ITextFilter regexFilter(String regex, boolean gap)
			throws ConQATException {
		return (ITextFilter) executeProcessor(RegexTextFilter.class,
				"(patterns=(ref=",
				new PatternList(Arrays.asList(Pattern.compile(regex))),
				"), 'create-gap'=(value=", gap, "))");
	}

	/**
	 * Creates a {@link TextElement} from a file. Use this method in conjunction
	 * with {@link #useCanonicalTestFile(String)} to create {@link TextElement}s
	 * for files in the test-data folder. (since
	 * {@link #useCanonicalTestFile(String)} is an instance method, it cannot be
	 * moved into this method).
	 */
	public static TextElement useTextElement(CanonicalFile report) {
		return new TextElement(new FileContentAccessor(report,
				report.getCanonicalPath()),
				Charset.forName(FileSystemUtils.UTF8_ENCODING));
	}
}