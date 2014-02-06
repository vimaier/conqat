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

import java.io.PrintWriter;

import org.conqat.engine.core.bundle.BundlesConfiguration;
import org.conqat.lib.commons.options.AOption;
import org.conqat.lib.commons.options.CommandLine;
import org.conqat.lib.commons.options.OptionException;
import org.conqat.lib.commons.options.OptionRegistry;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Base class for runners that can be started by {@link ConQATRunner}.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 37513 $
 * @ConQAT.Rating GREEN Hash: 8F609B8001179335E37258B9A4F536AB
 */
public abstract class ConQATRunnableBase {

	/** Command line parser */
	private final CommandLine cmdLine = new CommandLine(
			new OptionRegistry(this));

	/** The bundles config. */
	protected BundlesConfiguration bundleConfig;

	/** The entry point for the runnable. */
	public void run(String[] args, BundlesConfiguration bundleConfig) {
		this.bundleConfig = bundleConfig;

		try {
			String[] leftOvers = cmdLine.parse(args);
			if (leftOvers.length > 0) {
				System.err.println("Unsupported trailing options: "
						+ StringUtils.concat(leftOvers, " "));
				printUsageAndExit();
			}
		} catch (OptionException e) {
			System.err.println("Incorrect options: " + e.getMessage());
			System.exit(1);
		}

		doRun();
	}

	/** Performs the actual execution. */
	protected abstract void doRun();

	/** Help option: print usage and exit. */
	@AOption(shortName = 'h', longName = "help", description = "print this usage message")
	public void printUsageAndExit() {
		cmdLine.printUsage(new PrintWriter(System.err));
		System.exit(1);
	}

}
