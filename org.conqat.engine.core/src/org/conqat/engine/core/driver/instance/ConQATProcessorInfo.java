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

import java.util.concurrent.ExecutorService;

import org.conqat.engine.core.ConQATInfo;
import org.conqat.engine.core.bundle.BundlesConfiguration;
import org.conqat.engine.core.core.IConQATProcessorInfo;
import org.conqat.engine.core.core.IShutdownHook;
import org.conqat.engine.core.driver.ConQATInstrumentation;
import org.conqat.engine.core.driver.info.BlockInfo;
import org.conqat.engine.core.logging.ConQATLogger;
import org.conqat.engine.core.logging.LogManager;
import org.conqat.lib.commons.filesystem.CanonicalFile;
import org.conqat.lib.commons.version.Version;

/**
 * This is the implementation of {@link IConQATProcessorInfo} used for running
 * processors.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: A532AA7A4349026414F0FEB29AE21F41
 */
/* package */class ConQATProcessorInfo implements IConQATProcessorInfo {

	/** The local information on the processor. */
	private final ProcessorInstance processorInstance;

	/** The available context information. */
	private final ExecutionContext contextInfo;

	/** Instrumentation used. */
	private final ConQATInstrumentation instrumentation;

	/** The executor used. */
	private final ExecutorService executorService;

	/** The logger. */
	private ConQATLogger logger;

	/** Create new instance of processor info. */
	/* package */ConQATProcessorInfo(ProcessorInstance processorInstance,
			ExecutionContext contextInfo,
			ConQATInstrumentation instrumentation,
			ExecutorService executorService) {
		this.processorInstance = processorInstance;
		this.contextInfo = contextInfo;
		this.instrumentation = instrumentation;
		this.executorService = executorService;
	}

	/** {@inheritDoc} */
	@Override
	public synchronized BlockInfo getConfigurationInformation() {
		return contextInfo.getConfigurationInfo();
	}

	/** {@inheritDoc} */
	@Override
	public synchronized String getName() {
		return processorInstance.getName();
	}

	/** Returns the logger. */
	@Override
	public synchronized ConQATLogger getLogger() {
		if (logger == null) {
			logger = new ConQATLogger(processorInstance);
		}
		return logger;
	}

	/** Returns the log manager. */
	@Override
	public synchronized LogManager getLogManager() {
		return contextInfo.getLogManager();
	}

	/** Returns name. */
	@Override
	public String toString() {
		return getName();
	}

	/** {@inheritDoc} */
	@Override
	public synchronized BundlesConfiguration getBundlesConfiguration() {
		return contextInfo.getBundlesConfiguration();
	}

	/** {@inheritDoc} */
	@Override
	public Version getConQATCoreVersion() {
		return ConQATInfo.CORE_VERSION;
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void reportProgress(int workDone, int overallWork) {
		instrumentation.reportProgress(workDone, overallWork);
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void registerShutdownHook(IShutdownHook shutdownHook,
			boolean processesLog) {
		contextInfo.registerShutdownHook(shutdownHook, processesLog,
				processorInstance);
	}

	/** {@inheritDoc} */
	@Override
	public synchronized CanonicalFile getTempFile(String prefix, String suffix) {
		return contextInfo.getTempFile(prefix, suffix);
	}

	/** {@inheritDoc} */
	@Override
	public ExecutorService getExecutorService() {
		return executorService;
	}
}