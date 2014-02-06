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
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.conqat.engine.core.bundle.BundleException;
import org.conqat.engine.core.bundle.BundleInfo;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.core.IConQATProcessor;
import org.conqat.engine.core.driver.cqddl.CQDDLExecutionException;
import org.conqat.engine.core.driver.cqddl.CQDDLUtils;
import org.conqat.engine.core.driver.specification.SpecificationLoader;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.collections.PairList;
import org.conqat.lib.commons.filesystem.ClassPathUtils;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.commons.test.CCSMTestCaseBase;
import org.conqat.lib.cqddl.CQDDL;
import org.conqat.lib.cqddl.function.CQDDLCheck;
import org.conqat.lib.cqddl.function.CQDDLEvaluationException;
import org.conqat.lib.cqddl.function.ICQDDLFunction;
import org.conqat.lib.cqddl.parser.CQDDLParseException;
import org.conqat.lib.cqddl.parser.CQDDLParsingParameters;

/**
 * Base class for tests for ConQAT processors. This class uses CQDDL to allow a
 * compact test description.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 43071 $
 * @ConQAT.Rating GREEN Hash: 6E58499138EB234DCE750F09058C6150
 */
public abstract class ConQATProcessorTestCaseBase extends CCSMTestCaseBase {

	/**
	 * The parsing parameters used. These may be updated in the constructor to
	 * provide additional functions and key abbreviations.
	 */
	protected final CQDDLParsingParameters parsingParameters = new CQDDLParsingParameters();

	/** Constructor (used to register custom functions). */
	protected ConQATProcessorTestCaseBase() {

		parsingParameters.registerFunction("file", new ICQDDLFunction() {
			@Override
			public File eval(PairList<String, Object> list)
					throws CQDDLEvaluationException {
				CQDDLCheck.parameters(list, String.class);
				return useTestFile((String) list.getSecond(0));
			}
		});

		parsingParameters.registerFunction("list", new ICQDDLFunction() {
			@Override
			public List<?> eval(PairList<String, Object> parms)
					throws CQDDLEvaluationException {
				return CQDDLCheck.asList(parms);
			}
		});

		parsingParameters.registerFunction("nan", new ICQDDLFunction() {
			@Override
			public Double eval(PairList<String, Object> parms) {
				return Double.NaN;
			}
		});

	}

	/** Parses CQDDL expression(s) using the {@link #parsingParameters}. */
	protected Object parseCQDDL(Object... args) throws CQDDLParseException {
		return CQDDL.parse(parsingParameters, args);
	}

	/**
	 * Executes the given ConQAT processor using a CQDDL specification for its
	 * parameters.
	 * <p>
	 * The error handling strategy is to wrap all exceptions as
	 * {@link RuntimeException}s. The only exception is the
	 * {@link ConQATException}, as this might refer to errors the user is
	 * interested in.
	 * 
	 * @param processorClass
	 *            identifies the processor being executed.
	 * @param args
	 *            the arguments of the processor which are passed to the CQDDL
	 *            parser to return the processor parameters. The
	 *            {@link #parsingParameters} are used during parsing. The
	 *            arguments must be parsable to a pair list. The top-level keys
	 *            are parameter names, the top-level values are pair lists again
	 *            which map attributes to values. See users of this method for
	 *            examples.
	 * 
	 * @return the result of the processor's run.
	 */
	protected Object executeProcessor(
			Class<? extends IConQATProcessor> processorClass, Object... args)
			throws ConQATException {

		try {
			return CQDDLUtils.executeProcessor(processorClass.getName(),
					parsingParameters, args);
		} catch (CQDDLExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	/** Make sure that the tmp directory exists and is empty. */
	protected void ensureEmptyTmpDir() throws IOException {
		deleteTmpDirectory();
		FileSystemUtils.ensureDirectoryExists(getTmpDirectory());
	}

	/**
	 * Returns a new {@link SpecificationLoader} with the bundles recovered from
	 * the class path.
	 */
	protected SpecificationLoader createTestSpecificationLoader()
			throws BundleException {
		return new SpecificationLoader(null, determineBundlesFromClasspath());
	}

	/**
	 * Attempts to recover bundle information from the class path. This uses the
	 * class path of the class loader used for loading the test class. There are
	 * lots of things that could go wrong, but most of them should not happen in
	 * our test environment.
	 */
	private Collection<BundleInfo> determineBundlesFromClasspath()
			throws BundleException {
		URLClassLoader loader = CCSMAssert.checkedCast(getClass()
				.getClassLoader(), URLClassLoader.class);
		Collection<BundleInfo> bundles = new ArrayList<BundleInfo>();
		for (URL url : loader.getURLs()) {
			if (!"file".equals(url.getProtocol())) {
				continue;
			}

			try {
				File bundleDir = new File(url.toURI()).getParentFile();
				File bundleDescriptor = new File(bundleDir,
						BundleInfo.BUNDLE_DESCRIPTOR_NAME);
				if (bundleDescriptor.canRead()) {
					bundles.add(new BundleInfo(bundleDir));
				}
			} catch (URISyntaxException e) {
				// skip this one
			}
		}
		return bundles;
	}
	
	/** Resolves the bundle directory of the given class */
	protected static File getBundleDir(Class<?> clazz) {
		String classFile = ClassPathUtils.obtainClassFileURL(clazz).getFile();
		String classesDir = StringUtils.stripSuffix(
				clazz.getName().replace('.', '/') + ".class", classFile);
		String bundleDir = StringUtils.stripSuffix("classes/", classesDir);
		return new File(bundleDir);
	}
}