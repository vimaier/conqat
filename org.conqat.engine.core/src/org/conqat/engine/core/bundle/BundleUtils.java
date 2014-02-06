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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.conqat.engine.core.ConQATInfo;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.IConQATProcessor;
import org.conqat.engine.core.driver.error.EDriverExceptionType;
import org.conqat.engine.core.driver.error.EnvironmentException;
import org.conqat.engine.core.driver.error.ErrorLocation;
import org.conqat.lib.commons.error.IExceptionHandler;
import org.conqat.lib.commons.error.StderrExceptionHandler;
import org.conqat.lib.commons.filesystem.FileExtensionFilter;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.filesystem.PlainClassFileFilter;

/**
 * This class provides some utility methods which are useful when dealing with
 * bundles (especially {@link BundleInfo}s).
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: DED123D73C91D95A6F76D0BB41221BBD
 */
public class BundleUtils {

	/**
	 * Returns the names of all ConQAT processors provided by a single bundle.
	 * This is done by scanning the classes directory of the bundle and
	 * returning all classes that are annotated with {@link AConQATProcessor}
	 * and implement {@link IConQATProcessor}. Any errors are reported to
	 * stderr.
	 * 
	 * @param bundleInfo
	 *            the bundle whose processors to return.
	 * @return the names of all processors found.
	 * @throws IOException
	 *             in case of path resolution problems.
	 */
	public static List<String> getProvidedProcessors(BundleInfo bundleInfo)
			throws IOException {
		return getProvidedProcessors(bundleInfo,
				new StderrExceptionHandler<EnvironmentException>());
	}

	/**
	 * Returns the names of all ConQAT processors provided by a single bundle.
	 * This is done by scanning the classes directory of the bundle and
	 * returning all classes that are annotated with {@link AConQATProcessor}
	 * and implement {@link IConQATProcessor}.
	 * 
	 * @param bundleInfo
	 *            the bundle whose processors to return.
	 * @param exceptionHandler
	 *            The exception handler used for dealing with
	 *            {@link ClassNotFoundException} occurring during the search. If
	 *            this is <code>null</code> exceptions are simply swallowed.
	 * @return the names of all processors found.
	 * @throws IOException
	 *             in case of path resolution problems.
	 */
	public static <X extends Exception> List<String> getProvidedProcessors(
			BundleInfo bundleInfo,
			IExceptionHandler<EnvironmentException, X> exceptionHandler)
			throws IOException, X {

		List<String> processorNames = new ArrayList<String>();
		if (!bundleInfo.hasClasses()) {
			return processorNames;
		}

		File dir = bundleInfo.getClassesDirectory();
		List<File> classFiles = FileSystemUtils.listFilesRecursively(dir,
				new PlainClassFileFilter());

		int dirLength = dir.getCanonicalPath().length() + 1;

		for (File classFile : classFiles) {
			String className = obtainClassnameFromFilename(classFile
					.getCanonicalPath().substring(dirLength));
			Class<?> clazz;
			try {
				clazz = Class.forName(className, true, Thread.currentThread()
						.getContextClassLoader());
			} catch (ClassNotFoundException e) {
				handleClassLoadingException(exceptionHandler,
						EDriverExceptionType.CLASS_NOT_FOUND, e, className,
						bundleInfo);

				continue;
			} catch (NoClassDefFoundError e) {
				handleClassLoadingException(exceptionHandler,
						EDriverExceptionType.CLASS_DEF_NOT_FOUND, e, className,
						bundleInfo);
				continue;
			}

			if (clazz.isAnnotationPresent(AConQATProcessor.class)
					&& IConQATProcessor.class.isAssignableFrom(clazz)) {
				processorNames.add(className);
			}
		}

		return processorNames;
	}

	/**
	 * Handle class loading exception.
	 * 
	 * @param exceptionHandler
	 *            If this is <code>null</code>, method does nothing.
	 */
	private static <X extends Exception> void handleClassLoadingException(
			IExceptionHandler<EnvironmentException, X> exceptionHandler,
			EDriverExceptionType type, Throwable e, String className,
			BundleInfo bundleInfo) throws X {
		if (exceptionHandler == null) {
			return;
		}

		exceptionHandler.handleException(new EnvironmentException(type,
				"Could not load class " + e.getMessage().replace('/', '.')
						+ " when loading class " + className + " in bundle "
						+ bundleInfo.getId() + ".", new ErrorLocation(
						bundleInfo.getLocation())));
	}

	/** Determines the classname from the class filename. */
	private static String obtainClassnameFromFilename(String filename) {
		String withoutExtension = filename.substring(0, filename.length()
				- ".class".length());
		return withoutExtension.replaceAll(Pattern.quote(File.separator), ".");
	}

	/**
	 * Returns the all block specification files provided by a single bundle.
	 * This is done by scanning the blocks directory of the bundle and returning
	 * all files with the correct extension.
	 * 
	 * @param bundleInfo
	 *            the bundle whose block specification files to return.
	 * @return all files found.
	 */
	public static List<File> getProvidedBlockSpecificationFiles(
			BundleInfo bundleInfo) {

		File blocksDir = bundleInfo.getBlocksDirectory();
		if (!blocksDir.isDirectory()) {
			return new ArrayList<File>();
		}

		return FileSystemUtils.listFilesRecursively(blocksDir,
				new FileExtensionFilter(ConQATInfo.BLOCK_FILE_EXTENSION));
	}

	/**
	 * Returns the names of all block specifications provided by a single
	 * bundle. This is done by scanning the blocks directory of the bundle and
	 * converting the filenames to block names.
	 * 
	 * @param bundleInfo
	 *            the bundle whose block specification files to return.
	 * @return all block names found.
	 */
	public static List<String> getProvidedBlockSpecifications(
			BundleInfo bundleInfo) {

		int prefixLength = bundleInfo.getBlocksDirectory().getPath().length() + 1;
		int suffixLength = ConQATInfo.BLOCK_FILE_EXTENSION.length() + 1;
		List<String> result = new ArrayList<String>();
		for (File blockFile : getProvidedBlockSpecificationFiles(bundleInfo)) {
			String path = blockFile.getPath();
			String strippedPath = path.substring(prefixLength, path.length()
					- suffixLength);
			result.add(bundleInfo.getId() + '.'
					+ strippedPath.replace(File.separatorChar, '.'));
		}
		return result;
	}

	/**
	 * Returns the names of all block specification files (XML) all bundles in a
	 * given configuration.
	 * 
	 * @param bundlesConfiguration
	 *            the configuration whose block specification files to return.
	 * @return all files found.
	 */
	public static List<File> getProvidedBlockSpecificationFiles(
			BundlesConfiguration bundlesConfiguration) {
		List<File> result = new ArrayList<File>();
		if (bundlesConfiguration != null) {
			for (BundleInfo bundleInfo : bundlesConfiguration.getBundles()) {
				result.addAll(getProvidedBlockSpecificationFiles(bundleInfo));
			}
		}
		return result;
	}

	/** Get all bundle locations within this bundle collection. */
	public static Set<File> getBundleLocations(File bundleCollection) {
		HashSet<File> bundleLocations = new HashSet<File>();
		for (File bundleLocation : bundleCollection.listFiles()) {
			// if the descriptor exist but is not a file, this will be detected
			// later.
			if ((new File(bundleLocation, BundleInfo.BUNDLE_DESCRIPTOR_NAME)
					.exists())) {
				bundleLocations.add(bundleLocation);
			}
		}
		return bundleLocations;
	}
}