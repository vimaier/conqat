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
import java.util.HashSet;
import java.util.Set;

import org.apache.tools.ant.types.Path;

/**
 * This ANT task constructs a classpath based on a bundle descriptor to allow
 * convenient building of ConQAT bundles. The task expects the bundle collection
 * path relative to the project base dir as parameter and creates a classpath to
 * be referenced from ANT. This class path contains all classes provided by the
 * current ConQAT constellation. This includes all classes provided by bundles
 * themselves and classes in the libraries that bundles use. The classpath can
 * be referenced with {@value #ID_CLASS_PATH}.
 * <p>
 * The ANT file <code>conqat-ant-base.xml</code> in directory <code>ant</code>
 * documents the usage of this task.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 39C7245E05C7D77D905F9C2B34A4BAA6
 */
public class BundleClasspathTask extends BundleTaskBase {

	/** ANT id for the class path. */
	public final static String ID_CLASS_PATH = "bundle.classpath";

	/** Execute the task. */
	@Override
	protected void execute(Set<BundleInfo> bundleClosure) {
		Set<File> pathElements = createPathElements(bundleClosure);
		Path classpath = createPath(pathElements);
		System.out.println("Bundle classpath: " + classpath);
		getProject().addReference(ID_CLASS_PATH, classpath);
	}

	/** Create ANT path object from a set of files. */
	private Path createPath(Set<File> pathElements) {
		Path path = new Path(getProject());
		for (File element : pathElements) {
			path.createPathElement().setLocation(element);
		}
		return path;
	}

	/** Create path as a set of files from a set of bundles. */
	private Set<File> createPathElements(Set<BundleInfo> bundleClosure) {
		HashSet<File> result = new HashSet<File>();
		for (BundleInfo bundleInfo : bundleClosure) {
			addPathElements(bundleInfo, result);
		}
		return result;
	}

	/** Add libraries and classes directory for a bundle to the set of files. */
	private void addPathElements(BundleInfo bundleInfo, HashSet<File> result) {
		for (File library : bundleInfo.getLibraries()) {
			result.add(library);
		}
		if (bundleInfo.hasClasses()) {
			result.add(bundleInfo.getClassesDirectory());
		}
	}
}