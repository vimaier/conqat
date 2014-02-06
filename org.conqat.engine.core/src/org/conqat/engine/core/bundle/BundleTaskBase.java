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
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * This is a base class for ANT tasks that operate on the so called "bundle
 * closure", i.e. tasks that somehow reference all bundles a bundle depends on.
 * The task expects the bundle collection path relative to the project base dir.
 * Currently this can operate on a single bundle collection path only.
 * Individual bundles cannot be specified.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: F2E1EE7C90516703EC51FC6CD881757A
 */
public abstract class BundleTaskBase extends Task {

	/** Path to the bundle collection. */
	private String bundleCollectionPath;

	/** Set bundle collection path. */
	public void setCollection(String bundleCollectionPath) {
		this.bundleCollectionPath = bundleCollectionPath;
	}

	/**
	 * Execute task. This issues the definition of the classpath and the
	 * filesets.
	 */
	@Override
	public void execute() throws BuildException {
		// no point to run if bundle collection is not defined
		if (bundleCollectionPath == null) {
			throw new BuildException("Bundle collection undefined!");
		}

		try {
			BasicConfigurator.configure();
			Logger.getRootLogger().setLevel(Level.OFF);
			execute(getBundleClosure());
		} catch (BundleException e) {
			throw new BuildException(e);
		}
	}

	/**
	 * Template method for executing the task.
	 * 
	 * @param bundleClosure
	 *            set of alle bundles the bundle depends on + itself
	 * @throws BuildException
	 *             Exceptions should be signaled via {@link BuildException}s.
	 */
	protected abstract void execute(Set<BundleInfo> bundleClosure)
			throws BuildException;

	/**
	 * Get a set of bundles this bundle depends on. This includes the bundle
	 * itself.
	 */
	private Set<BundleInfo> getBundleClosure() throws BundleException {

		File baseDir = getProject().getBaseDir();

		BundlesManager manager = new BundlesManager();

		manager.addBundleCollection(new File(baseDir, bundleCollectionPath)
				.getPath());

		// add the current bundle
		manager.addBundleLocation(baseDir.getAbsolutePath());

		BundlesConfiguration config = manager.loadAndSortBundles();
		Set<BundleInfo> bundleClosure = config.getBundleClosure(baseDir
				.getName());

		if (bundleClosure == null) {
			throw new BuildException("Bundle " + baseDir.getName()
					+ " not found");
		}
		return bundleClosure;
	}
}