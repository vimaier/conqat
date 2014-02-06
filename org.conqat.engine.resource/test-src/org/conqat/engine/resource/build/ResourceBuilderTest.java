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

import org.conqat.engine.resource.IContentAccessor;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.binary.BinaryElement;
import org.conqat.engine.resource.binary.BinaryElementFactory;
import org.conqat.engine.resource.test.ResourceProcessorTestCaseBase;
import org.conqat.engine.resource.text.TextElement;
import org.conqat.engine.resource.text.TextElementFactory;
import org.conqat.engine.resource.util.ResourceTraversalUtils;

import org.conqat.engine.core.core.ConQATException;

/**
 * Tests the {@link ResourceBuilder}.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: FE282B2438A8081C6498518E68DA4630
 */
public class ResourceBuilderTest extends ResourceProcessorTestCaseBase {

	/**
	 * Tests the very basic behavior using a single factory and a single
	 * project.
	 */
	public void testBasic() throws ConQATException {
		IResource result = (IResource) executeProcessor(ResourceBuilder.class,
				"(scope=(ref=memScope('TEST/a/b'=B, 'TEST/a/c'=C, 'TEST/d'=D)), "
						+ "factory=(pattern='**', ref=", executeProcessor(
						BinaryElementFactory.class, "()"), "))");

		assertEquals("", result.getName());
		assertNoEmptyContainer(result, Container.class);
		assertEquals(3, ResourceTraversalUtils.countNonContainers(result));
		assertValidPath((Container) result, "TEST/a/b", Container.class,
				BinaryElement.class);
		assertValidPath((Container) result, "TEST/a/c", Container.class,
				BinaryElement.class);
		assertValidPath((Container) result, "TEST/d", Container.class,
				BinaryElement.class);
	}

	/** Tests the case of multiple projects. */
	public void testMultipleProjects() throws ConQATException {
		IResource result = (IResource) executeProcessor(ResourceBuilder.class,
				"(scope=(ref=memScope('TEST1/a/b'=B, 'TEST2/a/c'=C, 'TEST1/d'=D)), "
						+ "factory=(pattern='**', ref=", executeProcessor(
						BinaryElementFactory.class, "()"), "))");

		assertEquals("", result.getName());
		assertNoEmptyContainer(result, Container.class);
		assertEquals(3, ResourceTraversalUtils.countNonContainers(result));
		assertValidPath((Container) result, "TEST1/a/b", Container.class,
				BinaryElement.class);
		assertValidPath((Container) result, "TEST2/a/c", Container.class,
				BinaryElement.class);
		assertValidPath((Container) result, "TEST1/d", Container.class,
				BinaryElement.class);
	}

	/** Tests the case of multiple factories. */
	public void testMultipleFactories() throws ConQATException {
		Container result = (Container) executeProcessor(ResourceBuilder.class,
				"(scope=(ref=memScope('TEST/a.txt'=A, 'TEST/b.bin'=B)), ",
				"factory=(pattern='**/*.txt', ref=", executeProcessor(
						TextElementFactory.class, "()"), "), ",
				"factory=(pattern='**', ref=", executeProcessor(
						BinaryElementFactory.class, "()"), "))");

		assertEquals(2, ResourceTraversalUtils.countNonContainers(result));
		Container project = (Container) result.getNamedChild("TEST");
		assertTrue(project.getNamedChild("a.txt") instanceof TextElement);
		assertTrue(project.getNamedChild("b.bin") instanceof BinaryElement);
	}

	/**
	 * Tests that uncaptured accessors (no matching factory found) lead to an
	 * error.
	 */
	public void testFallThroughError() {
		try {
			executeProcessor(ResourceBuilder.class,
					"(scope=(ref=memScope('TEST1/a/b.txt'=B)), "
							+ "factory=(pattern='**/*.java', ref=",
					executeProcessor(BinaryElementFactory.class, "()"),
					"), lenient=(value=true))");
			fail("Expected exception (even in lenient mode)!");
		} catch (ConQATException e) {
			// expected
		}
	}

	/** Tests case insensitive patterns. */
	public void testCaseInsensitive() throws ConQATException {
		Container result = (Container) executeProcessor(ResourceBuilder.class,
				"(scope=(ref=memScope('TEST/a.txt'=A, 'TEST/b.TXT'=B)), ",
				"factory=(pattern='**/*.txt', ref=", executeProcessor(
						TextElementFactory.class, "()"), "), ",
				"factory=(pattern='**/*.txt', 'case-sensitive'=false, ref=",
				executeProcessor(BinaryElementFactory.class, "()"), "))");
		// we know that last pattern works, as otherwise we would have an
		// exception

		assertEquals(2, ResourceTraversalUtils.countNonContainers(result));
		Container project = (Container) result.getNamedChild("TEST");
		assertTrue(project.getNamedChild("a.txt") instanceof TextElement);
		assertTrue(project.getNamedChild("b.TXT") instanceof BinaryElement);
	}

	/** Tests the lenient mode. */
	public void testLenientMode() throws ConQATException {
		try {
			executeProcessor(ResourceBuilder.class,
					"(scope=(ref=memScope('TEST1/a/b.txt'=B)), "
							+ "factory=(pattern='**', ref=",
					new FailingFactory(), "), lenient=(value=false))");
			fail("Expected exception!");
		} catch (ConQATException e) {
			// expected
		}

		// no exception should be thrown in lenient mode
		executeProcessor(ResourceBuilder.class,
				"(scope=(ref=memScope('TEST1/a/b.txt'=B)), "
						+ "factory=(pattern='**', ref=", new FailingFactory(),
				"), lenient=(value=true))");
	}

	/** Tests error behavior in the case of empty accessor list. */
	public void testEmptyError() {
		try {
			executeProcessor(ResourceBuilder.class, "(scope=(ref=memScope()), "
					+ "factory=(pattern='**', ref=", new FailingFactory(), "))");
			fail("Expected exception!");
		} catch (ConQATException e) {
			// expected
		}
	}

	/** An element factory that always throws. */
	public static class FailingFactory implements IElementFactory {
		/** {@inheritDoc} */
		@Override
		public IElement create(IContentAccessor accessor)
				throws ConQATException {
			throw new ConQATException("test");
		}

	}
}