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
 * Test class for {@link BundlesDependencyVerifier}. As the verifier does not
 * produce any information but only checks for conformity this test merely
 * checks if exceptions are thrown or not.
 * 
 * @author Florian Deissenboeck
 * @author Elmar Juergens
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @levd.rating GREEN Hash: 12D9BE863738E85A4992D578CC592976
 */
public class BundlesDependencyVerifierTest extends BundleTestBase {

	/** Ensure that dependency to a compatible version is resolved correctly. */
	public void testCompatibleVersionDependency() throws BundleException {
		loadConfiguration("dependencyResolver01", "dependencyResolver04");
	}

	/** Ensure that dependency to an exact version is resolved correctly. */
	public void testExactVersionDependency() throws BundleException {
		loadConfiguration("dependencyResolver01", "dependencyResolver03");
	}

	/** Ensure that multiple depndencies are resolved correctly. */
	public void testMultipleDependencies() throws BundleException {
		loadConfiguration("dependencyResolver01", "dependencyResolver04",
				"dependencyResolver07");
	}

	/** Ensure that everything works for bundles with no dependencies. */
	public void testNoDependencies() throws BundleException {
		loadConfiguration("dependencyResolver01");
	}

	/** Test if an exception is thrown for self dependencies. */
	public void testSelfDependency() {
		checkException(EDriverExceptionType.SELF_DEPENDENCY,
				"dependencyResolver08");
	}

	/** Test if an exception is thrown for an unresolved dependency. */
	public void testUnresolvedDependency() {
		checkException(EDriverExceptionType.BUNDLE_NOT_FOUND,
				"dependencyResolver02");
	}

	/**
	 * Enables the {@link BundlesLoader} and the
	 * {@link BundlesDependencyVerifier}. All other buildlets are not required
	 * for the test.
	 */
	@Override
	protected Set<Class<?>> getEnabledBuildlets() {
		return createSet(BundlesLoader.class, BundlesDependencyVerifier.class);
	}

}