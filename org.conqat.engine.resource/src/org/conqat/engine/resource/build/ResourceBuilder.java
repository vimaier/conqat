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
package org.conqat.engine.resource.build;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IContentAccessor;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.base.ContainerBase;
import org.conqat.engine.resource.util.ConQATDirectoryScanner;
import org.conqat.engine.resource.util.UniformPathUtils;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.collections.PairList;
import org.conqat.lib.commons.error.NeverThrownRuntimeException;
import org.conqat.lib.commons.factory.IParameterizedFactory;
import org.conqat.lib.commons.string.StringUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: juergens $
 * @version $Rev: 35198 $
 * @ConQAT.Rating GREEN Hash: 9B652A9E6071E6D7465439F188194C4C
 */
@AConQATProcessor(description = "Processor for constructing resources from content accessors.")
public class ResourceBuilder extends ConQATProcessorBase {

	/** Factory for creating {@link Container}s. */
	private static final IParameterizedFactory<Container, String, NeverThrownRuntimeException> CONTAINER_FACTORY = new IParameterizedFactory<Container, String, NeverThrownRuntimeException>() {
		@Override
		public Container create(String name) {
			return new Container(name);
		}
	};

	/** The content accessors. */
	private final List<IContentAccessor> contentAccessors = new ArrayList<IContentAccessor>();

	/** The factories used in the builder. */
	private final PairList<Pattern, IElementFactory> factories = new PairList<Pattern, IElementFactory>();

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "lenient", attribute = "value", description = ""
			+ "If this is set to true, errors during element creation are ignored (default is false)", optional = true)
	public boolean lenient = false;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "empty", attribute = "allow", description = ""
			+ "If this is set to false, a ConQATException gets thrown, if no content accessor is contained in the scope. Default is false", optional = true)
	public boolean allowEmpty = false;

	/** Flag for storing errors. */
	private boolean hasErrors = false;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "scope", minOccurrences = 1, description = ""
			+ "Reference to the scope defining the resources to be built.")
	public void addContentAccessors(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) IContentAccessor[] contentAccessors) {
		this.contentAccessors.addAll(Arrays.asList(contentAccessors));
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "factory", minOccurrences = 1, description = ""
			+ "Adds a factory to this builder. If multiple factories are defined, "
			+ "the first factory whose pattern matches the uniform path of the content accessor is used.")
	public void addFactory(
			@AConQATAttribute(name = ConQATParamDoc.ANT_PATTERN_NAME, description = ConQATParamDoc.ANT_PATTERN_DESC) String pattern,
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) IElementFactory factory,
			@AConQATAttribute(name = "case-sensitive", defaultValue = "true", description = ""
					+ "Whether pattern matching is performed case sensitively (default: true)") boolean caseSensitive)
			throws ConQATException {
		factories.add(
				ConQATDirectoryScanner.convertPattern(pattern, caseSensitive),
				factory);
	}

	/** {@inheritDoc} */
	@Override
	public IResource process() throws ConQATException {
		if (contentAccessors.isEmpty() && !allowEmpty) {
			throw new ConQATException(
					"Empty scope (no content accessors provided)!");
		}

		Container root = new Container(StringUtils.EMPTY_STRING);
		for (IContentAccessor contentAccessor : contentAccessors) {
			IElement element = buildElement(contentAccessor);
			if (element != null) {
				insert(element, root);
			}
		}

		if (hasErrors) {
			throw new ConQATException(
					"Had errors during resource building. See log for details!");
		}
		return root;
	}

	/** Build the element for an accessor using the factories. */
	private IElement buildElement(IContentAccessor contentAccessor) {
		String path = contentAccessor.getUniformPath();
		for (int i = 0; i < factories.size(); ++i) {
			if (factories.getFirst(i).matcher(path).matches()) {
				try {
					return factories.getSecond(i).create(contentAccessor);
				} catch (ConQATException e) {
					String message = "Could not create element for "
							+ contentAccessor.getUniformPath() + " ["
							+ contentAccessor.getLocation() + "] "
							+ e.getMessage();
					if (lenient) {
						// do not log causing exception in this case. The stack
						// trace looks confusing on the console when this is
						// expected.
						getLogger().error(message);
					} else {
						getLogger().error(message, e);
						hasErrors = true;
					}
					return null;
				}
			}
		}

		getLogger().error(
				"No factory found for element "
						+ contentAccessor.getUniformPath() + " ["
						+ contentAccessor.getLocation() + "]");
		hasErrors = true;
		return null;
	}

	/** Inserts the given element into the hierarchy. */
	private static void insert(IElement element, Container container) {
		insert(element, container, CONTAINER_FACTORY);
	}

	/** Inserts the given element into the hierarchy. */
	@SuppressWarnings("unchecked")
	public static <R extends IResource, E extends IElement, C extends ContainerBase<R>> void insert(
			E element,
			C container,
			IParameterizedFactory<C, String, NeverThrownRuntimeException> containerFactory) {
		String[] segments = UniformPathUtils
				.splitPath(element.getUniformPath());
		int start = 0;
		if (!container.getName().isEmpty()) {
			CCSMAssert.isTrue(container.getName().equals(segments[0]),
					"Root should be named after the only one project!");
			start = 1;
		}

		for (int i = start; i < segments.length - 1; ++i) {
			R child = container.getNamedChild(segments[i]);
			if (!(child instanceof ContainerBase<?>)) {
				child = (R) containerFactory.create(segments[i]);
				container.addChild(child);
			}
			container = (C) child;
		}
		container.addChild((R) element);
	}
}