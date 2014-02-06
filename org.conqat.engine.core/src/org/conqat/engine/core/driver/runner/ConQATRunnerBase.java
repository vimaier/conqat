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

import java.io.File;

import org.conqat.engine.core.driver.BundleCommandLineBase;
import org.conqat.lib.commons.options.AOption;

/**
 * Base class that provides parameters for setting the configuration files for
 * caching and logging.
 * 
 * @author $Author: goede $
 * @version $Rev: 45258 $
 * @ConQAT.Rating GREEN Hash: 274B400DAFF3D3B6180248A2A5FA4983
 */
public abstract class ConQATRunnerBase extends BundleCommandLineBase {

	/**
	 * The logging configuration file. Null indicates to use the default
	 * location.
	 */
	protected File loggingConfigFile = null;

	/**
	 * The cache configuration file. Null indicates to use the default location.
	 */
	protected File cacheConfigFile = null;

	/** Set configuration file for caches. */
	@AOption(longName = "cache", description = "set cache config file")
	public void setCacheConfig(String cacheConfig) {
		cacheConfigFile = new File(cacheConfig);
		if (!cacheConfigFile.canRead()) {
			System.err.println("Cache config file not readable: "
					+ cacheConfigFile.getAbsolutePath());
			System.exit(4);
		}
	}

	/** Set configuration file for logger. */
	@AOption(shortName = 'l', longName = "log", description = "set logger config file")
	public void setLoggingConfig(String loggingConfig) {
		loggingConfigFile = new File(loggingConfig);
		if (!loggingConfigFile.canRead()) {
			System.err.println("Logging config file not readable: "
					+ loggingConfigFile.getAbsolutePath());
			System.exit(3);
		}
	}
}
