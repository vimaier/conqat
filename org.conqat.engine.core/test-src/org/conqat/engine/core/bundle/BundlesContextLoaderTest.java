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
package org.conqat.engine.core.bundle;

import java.util.Set;

import org.conqat.engine.core.driver.error.EDriverExceptionType;

/**
 * Test for {@link BundlesContextLoader}.
 * 
 * @author Florian Deissenboeck
 * @author Elmar Juergens
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @levd.rating GREEN Hash: C36975D3D00D62134AD6DA80903EDDB9
 */
public class BundlesContextLoaderTest extends BundleTestBase {

	/** Test if exception is thrown for abstract context class. */
	public void testAbstractContextClass() {
		checkException(EDriverExceptionType.ABSTRACT_CONTEXT_CLASS,
				"bundleContextLoader05");
	}

	/**
	 * Test if exception is thrown if the context contstructor threw an
	 * exception.
	 */
	public void testConstructorThrewException() {
		checkException(
				EDriverExceptionType.CONTEXT_CONSTRUCTOR_THREW_EXCEPTION,
				"bundleContextLoader07");
	}

	/** Test if a proper constructor is loaded. */
	public void testContext() throws BundleException {
		BundleInfo bundleInfo = loadInfo("bundleContextLoader02");
		assertNotNull(bundleInfo.getContext());
	}

	/** Test if multiple proper constructors are loaded. */
	public void testMultipleContexts() throws BundleException {
		BundlesConfiguration config = loadConfiguration(
				"bundleContextLoader02", "bundleContextLoader08");

		assertEquals(2, config.getBundles().size());
		assertNotNull(config.getBundle("bundleContextLoader02").getContext());
		assertNotNull(config.getBundle("bundleContextLoader08").getContext());
	}

	/** Test if a bundle without a context is handle properly. */
	public void testNoContext() throws BundleException {
		BundleInfo bundleInfo = loadInfo("bundleContextLoader01");
		assertNull(bundleInfo.getContext());
	}

	/**
	 * Test if exception is thrown if the context class does not have the
	 * {@link BundleContextBase#BundleContextBase(BundleInfo)} constructor.
	 */
	public void testNoContextConstructor() {
		checkException(EDriverExceptionType.MISSING_CONTEXT_CONSTRUCTOR,
				"bundleContextLoader03");
	}

	/**
	 * Test if exception is thrown if the context class does have a non-public
	 * {@link BundleContextBase#BundleContextBase(BundleInfo)} constructor.
	 */
	public void testNonPublicConstructor() {
		checkException(EDriverExceptionType.MISSING_CONTEXT_CONSTRUCTOR,
				"bundleContextLoader06");
	}

	/**
	 * Test if exception is thrown if the context class is not a subclass of
	 * {@link BundleContextBase}.
	 */
	public void testNotSubclass() {
		checkException(EDriverExceptionType.CONTEXT_CLASS_NOT_SUBCLASS,
				"bundleContextLoader04");
	}

	/**
	 * Enables the {@link BundlesLoader}, {@link BundlesClassLoaderInitializer}
	 * and the {@link BundlesContextLoader}. The
	 * {@link BundlesDependencyVerifier} is not enabled, since it is not
	 * required for the context loading tests.
	 */
	@Override
	protected Set<Class<?>> getEnabledBuildlets() {
		return createSet(BundlesLoader.class, BundlesDependencyVerifier.class,
				BundlesClassLoaderInitializer.class, BundlesContextLoader.class);
	}

}