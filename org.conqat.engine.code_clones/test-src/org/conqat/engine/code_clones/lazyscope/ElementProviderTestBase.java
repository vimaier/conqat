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
package org.conqat.engine.code_clones.lazyscope;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.conqat.engine.commons.sorting.NodeIdComparator;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.engine.sourcecode.resource.TokenTestCaseBase;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Base class for testcases for subclasses of {@link ElementProviderBase}
 * 
 * @author juergens
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: 152437D419FE22BB8EB43D5E5D9F42DB
 */
public abstract class ElementProviderTestBase extends TokenTestCaseBase {

	/** Root node of the scope on which test runs */
	private ITextResource root;

	/** provider under test */
	private ElementProviderBase<ITextResource, ITextElement> provider;

	/** {@inheritDoc} */
	@Override
	protected void setUp() throws ConQATException {
		root = createTokenResourceHierarchyFor(useTestFile(""));
		provider = createProvider();
		provider.init(root);
	}

	/** Template method: Deriving classes return the provider under test */
	protected abstract ElementProviderBase<ITextResource, ITextElement> createProvider();

	/** Test if provider returns the elements from the scope appropriately */
	public void testGetNextElement() {

		// get elements from scope as expected elements
		List<ITextElement> expectedElements = ResourceTraversalUtils
				.listTextElements(root);
		Collections.sort(expectedElements, new NodeIdComparator());

		// get elements actually returned by provider
		List<ITextElement> actualElements = drainProvider();
		Collections.sort(actualElements, new NodeIdComparator());

		// Make sure that we have test files at all
		assertTrue(expectedElements.size() > 0);

		// compare
		assertEquals(expectedElements.size(), actualElements.size());
		for (int i = 0; i < expectedElements.size(); i++) {
			assertSame(expectedElements.get(i), actualElements.get(i));
		}

		// tell test data manager that we used those files
		String prefix = useTestFile("").getPath() + File.separator;
		for (ITextElement element : actualElements) {
			String nameOffset = StringUtils.stripPrefix(prefix,
					element.getLocation());
			useTestFile(nameOffset);
		}

	}

	/** Utility method that drains the elements from the provider into a list */
	private List<ITextElement> drainProvider() {
		List<ITextElement> elements = new ArrayList<ITextElement>();
		ITextElement element = provider.getNext();
		while (element != null) {
			elements.add(element);
			element = provider.getNext();
		}
		return elements;
	}
}