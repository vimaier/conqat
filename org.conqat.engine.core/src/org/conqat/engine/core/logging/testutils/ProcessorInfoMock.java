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
package org.conqat.engine.core.logging.testutils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

import org.conqat.engine.core.ConQATInfo;
import org.conqat.engine.core.bundle.BundlesConfiguration;
import org.conqat.engine.core.core.IConQATProcessorInfo;
import org.conqat.engine.core.core.IShutdownHook;
import org.conqat.engine.core.driver.info.BlockInfo;
import org.conqat.engine.core.logging.IConQATLogger;
import org.conqat.engine.core.logging.LogManager;
import org.conqat.lib.commons.concurrent.InThreadExecutorService;
import org.conqat.lib.commons.filesystem.CanonicalFile;
import org.conqat.lib.commons.version.Version;

/**
 * Dummy implementation of {@link IConQATProcessorInfo} for testing purposes.
 * This uses a {@link LoggerMock} for logging to standard out.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 42375 $
 * @ConQAT.Rating GREEN Hash: E0557D745E4CDCFD2C5F3DBF896274D2
 */
public class ProcessorInfoMock implements IConQATProcessorInfo {

	/** Logger. */
	private final IConQATLogger logger;

	/**
	 * Constructs a new {@link ProcessorInfoMock} with a {@link LoggerMock} as
	 * logger.
	 */
	public ProcessorInfoMock() {
		this(new LoggerMock());
	}

	/** Constructs a new {@link ProcessorInfoMock} with the given logger */
	public ProcessorInfoMock(IConQATLogger logger) {
		this.logger = logger;
	}

	/** Throws {@link IllegalStateException} */
	@Override
	public BlockInfo getConfigurationInformation() {
		throw new IllegalStateException("Not supported by "
				+ getClass().getName());
	}

	/** Throws {@link IllegalStateException} */
	@Override
	public LogManager getLogManager() {
		throw new IllegalStateException("Not supported by "
				+ getClass().getName());
	}

	/** Returns a {@link LoggerMock}. */
	@Override
	public IConQATLogger getLogger() {
		return logger;
	}

	/** Returns class name. */
	@Override
	public String getName() {
		return getClass().getSimpleName();
	}

	/** Throws {@link IllegalStateException} */
	@Override
	public BundlesConfiguration getBundlesConfiguration() {
		throw new IllegalStateException("Not supported by "
				+ getClass().getName());
	}

	/** {@inheritDoc} */
	@Override
	public Version getConQATCoreVersion() {
		return ConQATInfo.CORE_VERSION;
	}

	/** Does nothing. */
	@Override
	public void reportProgress(int workDone, int overallWork) {
		// does nothing
	}

	/** Hooks are not supported by the mock */
	@Override
	public void registerShutdownHook(IShutdownHook shutdownHook,
			boolean processesLog) {
		// does nothing
	}

	/** {@inheritDoc} */
	@Override
	public CanonicalFile getTempFile(String prefix, String suffix) {
		try {
			File tempFile = File.createTempFile(prefix, suffix);
			tempFile.deleteOnExit();
			return new CanonicalFile(tempFile);
		} catch (IOException e) {
			// this behavior is ok during testing
			throw new AssertionError("Could not create new temp file.");
		}
	}

	/** {@inheritDoc} */
	@Override
	public ExecutorService getExecutorService() {
		return new InThreadExecutorService();
	}
}