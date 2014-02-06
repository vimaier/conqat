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
package org.conqat.engine.core.driver.instance;

import java.io.File;
import java.io.IOException;
import java.util.Stack;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;
import org.conqat.engine.core.bundle.BundlesConfiguration;
import org.conqat.engine.core.core.IConQATProcessorInfo;
import org.conqat.engine.core.core.IShutdownHook;
import org.conqat.engine.core.driver.ConQATInstrumentation;
import org.conqat.engine.core.driver.error.EDriverExceptionType;
import org.conqat.engine.core.driver.error.EnvironmentException;
import org.conqat.engine.core.driver.error.ErrorLocation;
import org.conqat.engine.core.driver.info.BlockInfo;
import org.conqat.engine.core.logging.LogManager;
import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.collections.Pair;
import org.conqat.lib.commons.filesystem.CanonicalFile;
import org.conqat.lib.commons.filesystem.FileSystemUtils;

/**
 * This class contains the non-processor specific part of the
 * {@link IConQATProcessorInfo} and is passed to the
 * {@link IInstance#execute(ExecutionContext,ConQATInstrumentation)} method, so
 * the information on the current context of ConQAT can be provided to the
 * processors. This is created exactly once in the driver and passed between
 * processor instances.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 44669 $
 * @ConQAT.Rating GREEN Hash: 5B8BB521F38648719262E031C9384DC7
 */
public class ExecutionContext {

	/** Information on the configuration. */
	private final BlockInfo configurationInfo;

	/** The log manager used. */
	private final LogManager logManager;

	/**
	 * The primary shutdown hooks are those that will be executed first. The
	 * pairs contain the processor instance name and the hook.
	 */
	private final Stack<Pair<ProcessorInstance, IShutdownHook>> primaryShutdownHooks = new Stack<Pair<ProcessorInstance, IShutdownHook>>();

	/**
	 * The secondary shutdown hooks are those that will be executed after those
	 * from the {@link #primaryShutdownHooks} have been finished. The pairs
	 * contain the processor instance name and the hook.
	 */
	private final Stack<Pair<ProcessorInstance, IShutdownHook>> secondaryShutdownHooks = new Stack<Pair<ProcessorInstance, IShutdownHook>>();

	/** Bundle configuration. */
	private final BundlesConfiguration bundlesConfig;

	/** The directory used for storing temporary files in. */
	private final CanonicalFile tmpBaseDir;

	/** Counter used to create unique temporary file names. */
	private int tempFileCounter = 0;

	/** The executor. */
	private final ExecutorService executorService;

	/**
	 * Create a new context information object.
	 * 
	 * @param tempDir
	 *            a directory that can be used to store temporary information.
	 *            This must exists and be writable.
	 * 
	 * @throws EnvironmentException
	 *             if the given tempDir is not a writable directory or the
	 *             required subdirectory can not be created.
	 */
	public ExecutionContext(BlockInfo configurationInfo,
			BundlesConfiguration bundlesConfig,
			ConQATInstrumentation instrumentation, File tempDir,
			ExecutorService executorService) throws EnvironmentException {
		if (!tempDir.isDirectory() || !tempDir.canWrite()) {
			throw new EnvironmentException(EDriverExceptionType.TEMP_DIR,
					"Temporary directory " + tempDir
							+ " must be a writable directory!",
					new ErrorLocation(tempDir));
		}

		this.executorService = executorService;

		this.configurationInfo = configurationInfo;
		this.bundlesConfig = bundlesConfig;
		logManager = new LogManager(instrumentation);
		Logger.getRootLogger().addAppender(logManager);

		try {
			tmpBaseDir = createUniqueFile(tempDir, "cq_", "_tmp");
			FileSystemUtils.ensureDirectoryExists(tmpBaseDir);
		} catch (IOException e) {
			throw new EnvironmentException(EDriverExceptionType.TEMP_DIR,
					"Could not create subdirectory for temporary files: "
							+ e.getMessage(), new ErrorLocation(tempDir));
		}
	}

	/**
	 * Creates a new unique filename in a given directory with given
	 * prefix/suffix.
	 */
	private CanonicalFile createUniqueFile(File baseDir, String prefix,
			String suffix) throws IOException {
		File result;

		// We use a loop to determine the filename, as we never know of a
		// processor creates additional files with colliding names. While this
		// is not allowed, we cannot really prevent it.
		do {
			result = new File(baseDir, prefix + tempFileCounter++ + suffix);
		} while (result.exists());

		return new CanonicalFile(result);
	}

	/** Returns the configuration information. */
	public BlockInfo getConfigurationInfo() {
		return configurationInfo;
	}

	/** Returns the bundle configuration for this ConQAT run. */
	public BundlesConfiguration getBundlesConfiguration() {
		return bundlesConfig;
	}

	/** Returns the log manager. */
	public LogManager getLogManager() {
		return logManager;
	}

	/** Executes the shutdown hooks and removes the temporary files. */
	public void performShutdown() {
		executeHooks(primaryShutdownHooks);
		executeHooks(secondaryShutdownHooks);

		FileSystemUtils.deleteRecursively(tmpBaseDir);
	}

	/** Executes the given stack of hooks. */
	private void executeHooks(
			Stack<Pair<ProcessorInstance, IShutdownHook>> shutdownHooks) {
		while (!shutdownHooks.isEmpty()) {
			ProcessorInstance instance = shutdownHooks.peek().getFirst();
			IShutdownHook hook = shutdownHooks.peek().getSecond();
			shutdownHooks.pop();

			if (instance.getState() == EInstanceState.RUN_SUCCESSFULLY) {
				executeHook(instance, hook);
			}
		}
	}

	/** Executes a single hook. */
	private void executeHook(ProcessorInstance instance, IShutdownHook hook) {
		instance.logger.info("Executing shutdown hook for processor: "
				+ instance.getName());
		try {
			hook.performShutdown();
		} catch (Throwable t) {
			instance.handleExecutionException(t);
		}
		instance.logger.info("Execution of shutdown hook completed for "
				+ instance.getName());
	}

	/**
	 * Registers a shutdown hook for a processor instance.
	 * 
	 * @see IConQATProcessorInfo#registerShutdownHook(IShutdownHook, boolean)
	 */
	/* package */void registerShutdownHook(IShutdownHook shutdownHook,
			boolean processesLog, ProcessorInstance processorInstance) {
		CCSMPre.isNotNull(shutdownHook);
		Pair<ProcessorInstance, IShutdownHook> pair = new Pair<ProcessorInstance, IShutdownHook>(
				processorInstance, shutdownHook);
		if (processesLog) {
			secondaryShutdownHooks.push(pair);
		} else {
			primaryShutdownHooks.push(pair);

		}
	}

	/**
	 * Returns a new temporary file.
	 * 
	 * @see IConQATProcessorInfo#getTempFile(String, String)
	 */
	public CanonicalFile getTempFile(String prefix, String suffix) {
		try {
			return createUniqueFile(tmpBaseDir, prefix, suffix);
		} catch (IOException e) {
			// the exception can only occur if the canonization fails, which is
			// rather unlikely, so
			// we deal with it here using an assertion.
			throw new AssertionError("Could not create temp file: "
					+ e.getMessage());
		}
	}

	/**
	 * Returns the executor service that is shared by all ConQAT processor
	 * instances.
	 */
	public ExecutorService getSharedExecutorService() {
		return executorService;
	}
}