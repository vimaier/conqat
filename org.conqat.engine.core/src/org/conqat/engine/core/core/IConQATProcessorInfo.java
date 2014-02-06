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
package org.conqat.engine.core.core;

import java.util.concurrent.ExecutorService;

import org.conqat.engine.core.bundle.BundlesConfiguration;
import org.conqat.engine.core.driver.info.BlockInfo;
import org.conqat.engine.core.logging.IConQATLogger;
import org.conqat.engine.core.logging.LogManager;
import org.conqat.lib.commons.filesystem.CanonicalFile;
import org.conqat.lib.commons.version.Version;

/**
 * The interface for runtime context information provided to a running ConQAT
 * processor.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 39DDFA33BB61E478B8327DE64C3AF6B7
 */
public interface IConQATProcessorInfo extends IProgressMonitor {

	/** Returns the name of this processor. */
	String getName();

	/**
	 * Returns information on the complete configuration currently running. This
	 * will usually be used for presentation purposes to display statistics or
	 * generate a graphical representation of the configuration.
	 */
	BlockInfo getConfigurationInformation();

	/** Obtain processor-instance-specific logger. */
	IConQATLogger getLogger();

	/** Returns the log manager. */
	LogManager getLogManager();

	/** Get bundles configuration of this ConQAT run. */
	BundlesConfiguration getBundlesConfiguration();

	/** Get ConQAT core version. */
	Version getConQATCoreVersion();

	/**
	 * Registers a shutdown hook which is executed after all processors have
	 * been finished. The exit hooks are managed in a stack-wise fashion, i.e.
	 * hooks which have been registered early will be executed last. Note that a
	 * shutdown hook will only be executed if the processor executed
	 * successfully (did not throw an exception).
	 * 
	 * @param processesLog
	 *            This flag should be <code>true</code> if the hook will process
	 *            log messages generated during the ConQAT execution. It will
	 *            cause the hook to be placed in a secondary stack which is
	 *            executed later than hook for which this parameter is
	 *            <code>false</code>. The rationale is the following: Exit hooks
	 *            may generate log messages. If an exit hooks wants to process
	 *            log messages, e.g. to visualize them, it should run later than
	 *            other exit hooks. This parameter allows this. For most exit
	 *            hooks, that e.g. only close open database connections, this
	 *            parameter should be false.
	 */
	void registerShutdownHook(IShutdownHook shutdownHook, boolean processesLog);

	/**
	 * Returns a new temporary file whose name starts/ends with the given
	 * prefix/suffix. The returned file will start with the given prefix and end
	 * with the given suffix. However, the part between prefix and suffix is
	 * arbitrary to ensure file uniqueness. The file returned is guaranteed to
	 * not exist and be in a writable location. The parent directory will always
	 * exist.
	 * 
	 * @param prefix
	 *            the non-empty prefix (only alpha-numerical characters are
	 *            allowed)
	 * @param suffix
	 *            the non-empty suffix (only alpha-numerical characters and the
	 *            dot are allowed); it may not end in a dot.
	 */
	CanonicalFile getTempFile(String prefix, String suffix);

	/**
	 * Returns an executor service that can be used to implement processor level
	 * parallelism. However, the returned executor only uses separate threads,
	 * if ConQAT was started with multi-thread support and the processor is
	 * marked with {@link AThreadSafeProcessor}, i.e. it supports
	 * multi-threading. Otherwise, the executor will simply execute within the
	 * same thread.
	 */
	ExecutorService getExecutorService();
}