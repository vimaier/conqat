/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
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
package org.conqat.engine.core.driver.runner;

import org.apache.log4j.Logger;
import org.conqat.engine.core.bundle.BundleException;
import org.conqat.engine.core.bundle.BundlesConfiguration;
import org.conqat.engine.core.driver.BundleCommandLineBase;
import org.conqat.engine.core.driver.DriverUtils;
import org.conqat.lib.commons.options.AOption;

/**
 * This class allows to run a program in a ConQAT context, i.e. the actual
 * program is loaded from a bundle and when the program is started, the class
 * loader has been prepared to include all bundles. This is achieved by
 * instantiating the actual {@link ConQATRunnableBase} via reflection after
 * initializing the bundles. The actual runnable may take further parameters.
 * <p>
 * The reason for this indirection is that running a program directly from a
 * bundle (i.e. a program subclassing {@link BundleCommandLineBase}) causes
 * problems during class loading, as the root class loader will always precede
 * the bundle classloader. While this can be fixed by using a more complicated
 * class path, this is rather tedious.
 * <p>
 * Arguments given on the command line after <code>--</code> are handed over to
 * the {@link ConQATRunnableBase} that is run.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46960 $
 * @ConQAT.Rating GREEN Hash: DB4AC178D591AF2ABAE59F0BB9DE3D11
 */
public class ConQATRunner extends ConQATRunnerBase {

	/** Logger */
	private static final Logger LOGGER = Logger.getLogger(ConQATRunner.class);

	/** The name of the runnable class. */
	private String runnableClassName;

	/** Arguments for the runnable. */
	private String[] runnableArgs;

	/** Entry point for the program. */
	public static void main(String[] args) {
		ConQATRunner runner = new ConQATRunner();
		runner.initFromCommandLine(args);

		DriverUtils.initLogger(runner.loggingConfigFile);
		DriverUtils.initCaches(runner.cacheConfigFile);

		try {
			BundlesConfiguration bundleConfig = runner.loadBundles();
			runner.startRunnable(bundleConfig);
		} catch (BundleException e) {
			LOGGER.fatal("Bundle exception: " + e.getMessage() + " @ "
					+ e.getLocationsAsString());
			System.exit(1);
		} catch (RunnerException e) {
			LOGGER.fatal("Could not run: " + e.getMessage(), e);
			System.exit(1);
		}
	}

	/** Instantiates and starts the runnable. */
	private void startRunnable(BundlesConfiguration bundleConfig)
			throws RunnerException {
		if (runnableClassName == null) {
			throw new RunnerException("No runnable class name provided!");
		}

		Class<?> runnableClass;
		try {
			runnableClass = Thread.currentThread().getContextClassLoader()
					.loadClass(runnableClassName);
		} catch (ClassNotFoundException e1) {
			throw new RunnerException("Could not find runnable class "
					+ runnableClassName + "!");
		}

		Object runnable;
		try {
			runnable = runnableClass.newInstance();
		} catch (InstantiationException e) {
			throw new RunnerException(
					"Provided runnable could not be instantiated! Default constructor missing?",
					e);
		} catch (IllegalAccessException e) {
			throw new RunnerException(
					"Provided runnable could not be instantiated! Class or constructor not visible?",
					e);
		}

		if (!(runnable instanceof ConQATRunnableBase)) {
			throw new RunnerException("Provided runnable " + runnableClassName
					+ " does not subclass "
					+ ConQATRunnableBase.class.getSimpleName() + "!");
		}

		((ConQATRunnableBase) runnable).run(runnableArgs, bundleConfig);
	}

	/** {@inheritDoc} */
	@Override
	protected void handleLeftOvers(String[] leftOvers) {
		runnableArgs = leftOvers;
	}

	/** Set runnable class. */
	@AOption(shortName = 'r', longName = "run", greedy = false, description = "the runnable class")
	public void setRunnable(String runnableClassName) {
		if (this.runnableClassName != null) {
			throw new IllegalArgumentException(
					"May not set runnable class more than once!");
		}
		this.runnableClassName = runnableClassName;
	}

	/** Local exception (only used within this class). */
	private static class RunnerException extends Exception {

		/** Serial version UID. */
		private static final long serialVersionUID = 1;

		/** Constructor. */
		public RunnerException(String message) {
			super(message);
		}

		/** Constructor. */
		public RunnerException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}
