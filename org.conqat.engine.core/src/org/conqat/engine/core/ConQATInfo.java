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
package org.conqat.engine.core;

import junit.framework.TestCase;

import org.antlr.runtime.Parser;
import org.apache.log4j.Logger;
import org.apache.tools.ant.DirectoryScanner;
import org.conqat.engine.core.driver.Driver;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.version.Version;
import org.conqat.lib.cqddl.CQDDL;

import com.sun.javadoc.Doc;

/**
 * This class is meant to store ConQAT-global information.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 47160 $
 * @ConQAT.Rating GREEN Hash: 3E4FDA440405BD593CF9AE216574B174
 */
public class ConQATInfo {

	/** The ConQAT core version. */
	public final static Version CORE_VERSION = new Version(2013, 12);

	/** Version of the distribution. */
	public final static Version DIST_VERSION = new Version(2013, 12);

	/** Extension for block files. */
	public final static String BLOCK_FILE_EXTENSION = "cqb";

	/** Extension for run config files. */
	public final static String RUNCONFIG_FILE_EXTENSION = "cqr";

	/**
	 * List of classes which are required by the ConQAT driver. The list should
	 * be complete, such that all required JARs are covered by these classes.
	 * These are not the only classes required by the driver, but just pointers
	 * to jars.
	 */
	public final static Class<?>[] CLASSPATH_CLASSES = { CCSMAssert.class /*
																		 * CCSM
																		 * commons
																		 */,
			Logger.class /* Log4J */, Driver.class /* conqat.jar */,
			Doc.class /* JavaDoc lib */, TestCase.class /* JUnit */,
			DirectoryScanner.class /* ANT */, CQDDL.class /* CQDDL */,
			Parser.class /* ANTLR runtime */};
}