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
import java.util.HashSet;
import java.util.Set;

import org.apache.tools.ant.BuildException;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * This ANT task constructs a argument string that is passed to the
 * <code>javadoc</code>-task to include inter-bundle links in the JavaDoc
 * documentation. The task expects the bundle collection path relative to the
 * project base dir and the output directory for JavaDoc.
 * <p>
 * The arguments for the <code>javadoc</code>-task are of the form
 * <code>-link &lt;bundle-dir-1/javadoc&gt; -link &lt;bundle-dir-2/javadoc&gt;..</code>
 * . The bundle dirs are relative path names. The arguments string is stored at
 * property {@value #JAVADOC_LINK_ARGS_PROPERTY}.
 * <p>
 * The ANT file <code>conqat-ant-base.xml</code> in directory <code>ant</code>
 * documents the usage of this task.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 97A6C98C0992E50087926259A0D74077
 */
public class BundleJavaDocPathTask extends BundleTaskBase {

	/** Name of the property that carries the link arguments. */
	public static final String JAVADOC_LINK_ARGS_PROPERTY = "javadoc-link-args";

	/** Output directory for javadoc. */
	private File javaDocDir;

	/** Set JavaDoc output directory (relative to base dir). */
	public void setJavaDocPath(String javadocPath) {
		javaDocDir = new File(getProject().getBaseDir(), javadocPath);
	}

	/** Execute the task. */
	@Override
	protected void execute(Set<BundleInfo> bundles) {
		// no point to run if bundle collection is not defined
		if (javaDocDir == null) {
			throw new BuildException("JavaDoc path not set.");
		}
		try {
			FileSystemUtils.ensureDirectoryExists(javaDocDir);
		} catch (IOException e) {
			throw new BuildException("Creating directory " + javaDocDir
					+ " failed.", e);
		}
		Set<File> pathElements = createPathElements(bundles);
		String args = createArgs(pathElements);
		getProject().setProperty(JAVADOC_LINK_ARGS_PROPERTY, args);
	}

	/** Create path as a set of files from a set of bundles. */
	private Set<File> createPathElements(Set<BundleInfo> bundleClosure) {
		HashSet<File> result = new HashSet<File>();
		for (BundleInfo bundleInfo : bundleClosure) {
			// exclude bundle itself
			if (bundleInfo.getLocation().equals(getProject().getBaseDir())) {
				continue;
			}
			File javadocDir = new File(bundleInfo.getLocation(), "javadoc");
			if (javadocDir.isDirectory()) {
				result.add(javadocDir);
			} else {
				System.out.println("Bundle " + bundleInfo.getId()
						+ " does not have a javadoc directory.");
			}
		}
		return result;
	}

	/** Create ANT path object from a set of files. */
	private String createArgs(Set<File> pathElements) {
		StringBuilder args = new StringBuilder();
		for (File element : pathElements) {
			args.append("-link ");
			try {
				args.append(FileSystemUtils.createRelativePath(element,
						javaDocDir));
			} catch (IOException ex) {
				throw new BuildException(ex);
			}
			args.append(StringUtils.SPACE_CHAR);
		}
		return args.toString();
	}
}