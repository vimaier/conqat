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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import org.conqat.lib.commons.test.CCSMTestCaseBase;
import org.conqat.engine.core.driver.error.EDriverExceptionType;

/**
 * Test class for {@link BundleDescriptorReader}.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 4E97F907CA666E847F6D2603464A2FF7
 */
public class BundleDescriptorReaderTest extends CCSMTestCaseBase {

	/**
	 * Test if dependency elements {@link EBundleXMLElement#dependsOn} are read
	 * correctly.
	 */
	public void testDependencies() throws BundleException, IOException {
		BundleInfo bundleInfo = readDescriptor("bundle-descriptor-03.xml");

		Collection<BundleDependency> dependencies = bundleInfo
				.getDependencies();

		assertEquals(3, dependencies.size());

		ArrayList<BundleDependency> dependencyList = new ArrayList<BundleDependency>(
				dependencies);

		Collections.sort(dependencyList, new BundleDependencyComparator());

		for (int i = 0; i < 3; i++) {
			BundleDependency dependency = dependencyList.get(i);
			assertEquals("bundle" + i, dependency.getId());
			assertEquals(1, dependency.getVersion().getMajor());
			assertEquals(i, dependency.getVersion().getMinor());
		}
	}

	/**
	 * Test if the schema matches the legal bundle ids.
	 */
	public void testBundleIdType() throws BundleException, IOException {
		BundleInfo bundleInfo = readDescriptor("bundle-descriptor-08.xml");

		Collection<BundleDependency> dependencies = bundleInfo
				.getDependencies();

		assertEquals(3, dependencies.size());
	}

	/** Test if {@link EBundleXMLElement#description} is read correctly. */
	public void testDescription() throws BundleException, IOException {
		BundleInfo bundleInfo = readDescriptor("bundle-descriptor-03.xml");

		assertEquals("A Test Bundle", bundleInfo.getDescription());
	}

	/** Test if {@link EBundleXMLElement#name} is read correctly. */
	public void testName() throws BundleException, IOException {
		BundleInfo bundleInfo = readDescriptor("bundle-descriptor-03.xml");

		assertEquals("Test Bundle", bundleInfo.getName());
	}

	/** Test if {@link EBundleXMLElement#provider} is read correctly. */
	public void testProvider() throws BundleException, IOException {
		BundleInfo bundleInfo = readDescriptor("bundle-descriptor-03.xml");

		assertEquals("Technische Universitaet Muenchen",
				bundleInfo.getProvider());
	}

	/** Test if {@link EBundleXMLElement#requiresCore} is read correctly. */
	public void testRequiresCoreVersion() throws BundleException, IOException {
		BundleInfo bundleInfo = readDescriptor("bundle-descriptor-03.xml");

		assertEquals(2, bundleInfo.getRequiredCoreVersion().getMajor());
		assertEquals(0, bundleInfo.getRequiredCoreVersion().getMinor());
	}

	/** Test if the reader validates against the schema. */
	public void testSchema() throws IOException {
		checkException("bundle-descriptor-02.xml",
				EDriverExceptionType.XML_PARSING_EXCEPTION);
	}

	/** Test if {@link EBundleXMLElement#version} is read correctly. */
	public void testVersion() throws BundleException, IOException {
		BundleInfo bundleInfo = readDescriptor("bundle-descriptor-03.xml");
		assertEquals(1, bundleInfo.getVersion().getMajor());
		assertEquals(5, bundleInfo.getVersion().getMinor());
	}

	/** Test if well-formed-errors are reported. */
	public void testXMLParsingError() throws IOException {
		checkException("bundle-descriptor-01.xml",
				EDriverExceptionType.XML_PARSING_EXCEPTION);
	}

	/**
	 * Read a file with an expected exception. Checks if the exception has the
	 * expected type.
	 * 
	 * @param filename
	 *            file to read
	 * @param expectedType
	 *            expected exception type.
	 */
	private void checkException(String filename,
			EDriverExceptionType expectedType) throws IOException {
		try {
			readDescriptor(filename);
			fail("expected exception");
		} catch (BundleException e) {
			assertEquals(expectedType, e.getType());
		}
	}

	/**
	 * Read a file and return the bundle info object.
	 */
	private BundleInfo readDescriptor(String filename) throws BundleException,
			IOException {
		File file = useTestFile(filename);
		BundleDescriptorReader reader = new BundleDescriptorReader(file);
		BundleInfo bundleInfo = new BundleInfo(file.getParentFile());
		reader.read(bundleInfo);
		return bundleInfo;
	}

	/** Comparator for comparing bundle dependencies by id. */
	private class BundleDependencyComparator implements
			Comparator<BundleDependency> {

		/** Compare by {@link BundleDependency#getId()}. */
		@Override
		public int compare(BundleDependency dependency1,
				BundleDependency dependency2) {
			return dependency1.getId().compareTo(dependency2.getId());
		}

	}
}