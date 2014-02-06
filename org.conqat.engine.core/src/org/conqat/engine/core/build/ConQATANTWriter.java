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
package org.conqat.engine.core.build;

import static org.conqat.engine.core.build.BuildFileConstants.ALL_BLOCKS_DIR;
import static org.conqat.engine.core.build.BuildFileConstants.ALL_SUFFIX;
import static org.conqat.engine.core.build.BuildFileConstants.BINARY_DIST_ROOT_DIR;
import static org.conqat.engine.core.build.BuildFileConstants.BINARY_DIST_ZIP;
import static org.conqat.engine.core.build.BuildFileConstants.BUNDLES_DIR;
import static org.conqat.engine.core.build.BuildFileConstants.CLEAN_ALL_TARGET;
import static org.conqat.engine.core.build.BuildFileConstants.COMPILE_ALL_TARGET;
import static org.conqat.engine.core.build.BuildFileConstants.COMPILE_TARGET;
import static org.conqat.engine.core.build.BuildFileConstants.COMPILE_TESTS_TARGET;
import static org.conqat.engine.core.build.BuildFileConstants.CONQAT;
import static org.conqat.engine.core.build.BuildFileConstants.CONQAT_BUILD_DIR;
import static org.conqat.engine.core.build.BuildFileConstants.CONQAT_JAVADOC_DIR;
import static org.conqat.engine.core.build.BuildFileConstants.CONQAT_LIB_DIR;
import static org.conqat.engine.core.build.BuildFileConstants.CONQAT_PROJ;
import static org.conqat.engine.core.build.BuildFileConstants.DIST_BINARY_DIR;
import static org.conqat.engine.core.build.BuildFileConstants.DIST_BINARY_TARGET;
import static org.conqat.engine.core.build.BuildFileConstants.DIST_MONOLITH_TARGET;
import static org.conqat.engine.core.build.BuildFileConstants.DIST_SOURCE_DIR;
import static org.conqat.engine.core.build.BuildFileConstants.DIST_SOURCE_TARGET;
import static org.conqat.engine.core.build.BuildFileConstants.DIST_UNIFIED_DIR;
import static org.conqat.engine.core.build.BuildFileConstants.DIST_UNIFIED_TARGET;
import static org.conqat.engine.core.build.BuildFileConstants.JAVADOC_TARGET;
import static org.conqat.engine.core.build.BuildFileConstants.MONOLITH_DIST_JAR;
import static org.conqat.engine.core.build.BuildFileConstants.SOURCE_DIST_ROOT_DIR;
import static org.conqat.engine.core.build.BuildFileConstants.SOURCE_DIST_ZIP;
import static org.conqat.engine.core.build.BuildFileConstants.UNIFIED_DIST_ROOT_DIR;
import static org.conqat.engine.core.build.BuildFileConstants.UNIFIED_DIST_ZIP;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.conqat.engine.core.ConQATInfo;
import org.conqat.engine.core.bundle.BundleDependency;
import org.conqat.engine.core.bundle.BundleInfo;
import org.conqat.engine.core.javadoc.ConQATTaglet;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.date.DateUtils;
import org.conqat.lib.commons.filesystem.FileExtensionFilter;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.filesystem.RegularDirectoryFilter;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Writer class for generating ANT build files for ConQAT. These are needed to
 * build bundles depending on each other in the correct order.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 47098 $
 * @ConQAT.Rating GREEN Hash: 625A8E7F9BE85B6618C3205886947CC1
 */
/* package */class ConQATANTWriter extends ANTWriter {

	/** Links to be passed to JavaDoc. */
	private static final String[] JAVADOC_LINKS = {
			"http://jung.sourceforge.net/doc/api/",
			"http://java.sun.com/javase/6/docs/api/",
			"http://commons.apache.org/collections/apidocs/",
			"http://www.jfree.org/jfreechart/api/javadoc/",
			"http://www.jfree.org/jcommon/api/",
			"http://jakarta.apache.org/bcel/apidocs/",
			"http://java.sun.com/j2se/1.5.0/docs/guide/javadoc/doclet/spec/",
			"http://jaxen.org/apidocs/", "http://pmd.sourceforge.net/apidocs/",
			"http://www.beanshell.org/javadoc/",
			"http://svnkit.com/kb/javadoc/",
			"http://conqat.cs.tum.edu/download/lib-javadoc/commons/",
			"http://conqat.cs.tum.edu/download/lib-javadoc/scanner/",
			"http://conqat.cs.tum.edu/download/lib-javadoc/simulink/",
			"http://conqat.cs.tum.edu/download/lib-javadoc/bugzilla/" };

	/** Topologically sorted list of all bundles. */
	private final List<BundleInfo> bundles;

	/**
	 * Set of bundle ids. This set is initialized with the ids of all bundles.
	 * During build file generation the ids of bundles that other bundles depend
	 * on are removed. That leaves a set with the ids of all bundles no one
	 * depends on. It is used to generate the <code>compile-all</code> and
	 * <code>compile-tests-all</code>-targets in
	 * {@link #createAllTarget(String)}
	 */
	private final Set<String> nonDependeeIds = new HashSet<String>();

	/**
	 * Create a new build file writer.
	 * 
	 * @param file
	 *            name of build file
	 * @param bundles
	 *            topologically sorted list of bundles
	 * 
	 * @throws FileNotFoundException
	 *             if the file build file could not be created.
	 */
	public ConQATANTWriter(File file, List<BundleInfo> bundles)
			throws FileNotFoundException {
		super(file);
		this.bundles = bundles;

		for (BundleInfo bundle : bundles) {
			nonDependeeIds.add(bundle.getId());
		}
	}

	/** Write build file. */
	public void writeBuildFile() {
		addHeader(("ConQAT build file was generated by "
				+ getClass().getSimpleName() + " @ " + DateUtils.getNow()));

		startProject("ConQAT");

		createTargets(COMPILE_TARGET);
		createTargets(COMPILE_TESTS_TARGET);
		createTargets(JAVADOC_TARGET);

		createSequentialAllTargets("clean", false);
		addNewLine();

		createSequentialAllTargets("test", true);
		addNewLine();

		createBinaryDistTarget();
		createMonolithDistTarget();
		createSourceDistTarget();
		createUnifiedDistTarget();
		addNewLine();

		createJavaDocTarget();

		closeProject();
	}

	/**
	 * For a given target <code>X</code> this creates the required target for
	 * ConQAT core, the target for all bundles (topologically sorted) and the
	 * <code>X</code>-all target.
	 * 
	 * @param targetName
	 */
	private void createTargets(String targetName) {
		createConQATTarget(targetName);
		for (BundleInfo bundle : bundles) {
			createTarget(bundle, targetName);
		}
		createAllTarget(targetName);
	}

	/**
	 * Create a target for the ConQAT core.
	 * 
	 * @param target
	 *            name of the target
	 */
	private void createConQATTarget(String target) {
		startTarget(target + "-" + CONQAT);
		writeAnt(CONQAT_PROJ, target);
		closeTarget();
		addNewLine();
	}

	/**
	 * Creates a target for a specified bundle and adds the dependency list.
	 * 
	 * @param bundle
	 *            the bundle.
	 * @param target
	 *            name of the target.
	 */
	private void createTarget(BundleInfo bundle, String target) {
		startTarget(target + "-" + bundle.getId(),
				createDependencyList(bundle, target));

		writeAnt(bundle.getId(), target);
		closeTarget();
		addNewLine();
	}

	/**
	 * Create list of dependencies for a bundle. The ConQAT core is always
	 * added, so the dependency list is never empty.
	 */
	private ArrayList<String> createDependencyList(BundleInfo bundle,
			String target) {

		ArrayList<String> dependencyList = new ArrayList<String>();

		dependencyList.add(target + "-" + CONQAT);

		for (BundleDependency dependency : bundle.getDependencies()) {
			nonDependeeIds.remove(dependency.getId());
			dependencyList.add(target + "-" + dependency.getId());
		}

		return dependencyList;
	}

	/**
	 * Creates the <code>x</code>-all that calls the <code>x</code>-target for
	 * all leaves of the dependency tree.
	 * 
	 * @param target
	 *            name of the target
	 */
	private void createAllTarget(String target) {

		ArrayList<String> dependencies = new ArrayList<String>();

		for (String dependency : nonDependeeIds) {
			dependencies.add(target + "-" + dependency);
		}

		startTarget(target + ALL_SUFFIX, dependencies);

		closeTarget();
		addNewLine();
	}

	/**
	 * Create the <code>x</code>-all target that sequentially call target
	 * <code>x</code> for ConQAT core and all bundles.
	 * 
	 * @param target
	 *            name of the target
	 */
	private void createSequentialAllTargets(String target, boolean parallel) {
		startTarget(target + ALL_SUFFIX);

		if (parallel) {
			startParallel();
		}

		writeAnt(CONQAT_PROJ, target);
		for (BundleInfo bundle : bundles) {
			writeAnt(bundle.getId(), target);
		}

		if (parallel) {
			closeParallel();
		}

		closeTarget();
	}

	/** Write target for binary distribution. */
	private void createBinaryDistTarget() {
		startTarget(DIST_BINARY_TARGET, CLEAN_ALL_TARGET, COMPILE_ALL_TARGET);

		writeDeleteDir(BINARY_DIST_ROOT_DIR);
		writeMkDir(BINARY_DIST_ROOT_DIR + "/" + BUNDLES_DIR);
		addNewLine();

		for (BundleInfo bundle : bundles) {
			writeAnt(bundle.getId(), DIST_BINARY_TARGET);

			writeMkDir(BINARY_DIST_ROOT_DIR + "/" + BUNDLES_DIR + "/"
					+ bundle.getId());
			writeCopyDir(
					bundle.getId() + "/" + DIST_BINARY_DIR + "/"
							+ bundle.getId(), BINARY_DIST_ROOT_DIR + "/"
							+ BUNDLES_DIR + "/" + bundle.getId());
			addNewLine();
		}
		addNewLine();

		writeAnt(CONQAT_PROJ, DIST_BINARY_TARGET);
		writeCopyDir(CONQAT_PROJ + "/" + DIST_BINARY_DIR + "/" + CONQAT,
				BINARY_DIST_ROOT_DIR);
		addNewLine();

		writeZip(BINARY_DIST_ZIP, BINARY_DIST_ROOT_DIR);
		closeTarget();
	}

	/**
	 * Write target for monolith (aka "big jar") distribution. This does not
	 * include bundle.xml files and resources currently.
	 */
	private void createMonolithDistTarget() {
		startTarget(DIST_MONOLITH_TARGET, CLEAN_ALL_TARGET, COMPILE_ALL_TARGET);

		writeDeleteDir(ALL_BLOCKS_DIR);
		writeMkDir(ALL_BLOCKS_DIR);
		for (BundleInfo bundle : bundles) {
			if (bundle.getBlocksDirectory().isDirectory()) {
				writeCopyDir(
						bundle.getId() + "/" + BundleInfo.BLOCKS_LOCATION,
						ALL_BLOCKS_DIR + "/" + bundle.getId().replace('.', '/'),
						"**/.svn/**");
			}
		}

		openJar(MONOLITH_DIST_JAR);

		writeFileSet(ALL_BLOCKS_DIR);
		writeFileSet(CONQAT_BUILD_DIR);
		for (File library : FileSystemUtils.listFilesRecursively(new File(
				CONQAT_LIB_DIR), new FileExtensionFilter("jar"))) {
			writeZipFileSet(
					CONQAT_PROJ + "/" + CONQAT_LIB_DIR + "/"
							+ library.getName(), "**/*.class", "**/*.xsd");
		}
		addNewLine();

		List<String> bundleIDs = new ArrayList<String>();
		for (BundleInfo bundle : bundles) {
			bundleIDs.add(bundle.getId());

			writeFileSet(bundle.getId() + "/" + BundleInfo.CLASSES_LOCATION);
			for (File library : bundle.getLibraries()) {
				String path = FileSystemUtils.normalizeSeparators(library
						.getPath());

				// discard everything up to the bundle ID (path returned might
				// be absolute)
				CCSMAssert.isTrue(path.contains(bundle.getId()),
						"Path must contain bundle id");
				path = path.substring(path.indexOf(bundle.getId()));
				writeZipFileSet(path, "**/*.class", "**/*.xsd");
			}

			addNewLine();
		}

		openManifest();
		writeAttribute("ConQAT-Bundles", StringUtils.concat(bundleIDs, ","));
		closeManifest();

		closeJar();
		closeTarget();
	}

	/** Write target for source distribution. */
	private void createSourceDistTarget() {
		startTarget(DIST_SOURCE_TARGET, CLEAN_ALL_TARGET);
		completeDistCopyTarget(DIST_SOURCE_TARGET, DIST_SOURCE_DIR,
				SOURCE_DIST_ROOT_DIR, SOURCE_DIST_ZIP);
	}

	/**
	 * Completes a target that re-calls the target for each bundle and copies
	 * the results into one place.
	 */
	private void completeDistCopyTarget(String target, String distDir,
			String rootDir, String zipfileName) {
		writeDeleteDir(rootDir);
		writeMkDir(rootDir);
		addNewLine();

		for (BundleInfo bundle : bundles) {

			writeAnt(bundle.getId(), target);

			writeMkDir(rootDir + "/" + bundle.getId());
			writeCopyDir(bundle.getId() + "/" + distDir + "/" + bundle.getId(),
					rootDir + "/" + bundle.getId());
			addNewLine();
		}
		addNewLine();

		writeAnt(CONQAT_PROJ, target);
		writeCopyDir(CONQAT_PROJ + "/" + distDir, rootDir);
		addNewLine();

		writeCopy(BuildFileConstants.TOPLEVEL_BUILD_XML, rootDir);
		addNewLine();

		writeZip(zipfileName, rootDir);
		closeTarget();
	}

	/** Write target for unified distribution. */
	private void createUnifiedDistTarget() {
		startTarget(DIST_UNIFIED_TARGET, CLEAN_ALL_TARGET, COMPILE_ALL_TARGET);
		completeDistCopyTarget(DIST_UNIFIED_TARGET, DIST_UNIFIED_DIR,
				UNIFIED_DIST_ROOT_DIR, UNIFIED_DIST_ZIP);
	}

	/**
	 * Create target for the JavaDoc creation for the whole installation. This
	 * uses exec due to a bug in the JavaDoc implementation that prohibits us
	 * from using the ANT target.
	 */
	private void createJavaDocTarget() {
		startTarget(JAVADOC_TARGET, COMPILE_TARGET + "-" + CONQAT);

		writeDeleteDir(CONQAT_JAVADOC_DIR);
		writeMkDir(CONQAT_JAVADOC_DIR);

		ArrayList<String> sourcePathes = createSourcePathes();

		// executable is assumed to be on the path.
		openExec("javadoc");

		writeArg("-d " + CONQAT_JAVADOC_DIR);
		writeArg("-subpackages");
		writeArg(StringUtils.concat(findTopLevelPackages(), ":"));
		writeArg("-sourcepath");
		writeArg(StringUtils.concat(sourcePathes, File.pathSeparator));
		writeArg("-classpath");
		writeArg(StringUtils.concat(createClassPath(), File.pathSeparator));
		writeArg("-tag levd.rating:t:\"Rating: \"");
		writeArg("-tag ConQAT.Rating:t:\"Rating: \"");
		writeArg("-taglet " + ConQATTaglet.class.getName());
		writeArg("-tagletpath " + CONQAT_PROJ + "/build");
		writeArg("-windowtitle   \"ConQAT " + ConQATInfo.DIST_VERSION + "\"");
		writeArg("-doctitle \"ConQAT " + ConQATInfo.DIST_VERSION + "\"");
		writeArg("-header  \"ConQAT " + ConQATInfo.DIST_VERSION + "\"");
		writeArg("-footer  \"ConQAT " + ConQATInfo.DIST_VERSION + "\"");
		writeArg("-bottom  \"&copy; 2005&mdash;"
				+ Calendar.getInstance().get(Calendar.YEAR)
				+ " The ConQAT Project\"");

		for (String link : JAVADOC_LINKS) {
			writeArg("-link " + link);
		}

		closeExec();
		addNewLine();

		for (String sourcePath : sourcePathes) {
			writeCopyDir(sourcePath, CONQAT_JAVADOC_DIR, "**/*.java",
					"**/package.html");
		}

		closeTarget();
	}

	/** Create class path for JavaDoc. */
	private ArrayList<String> createClassPath() {
		ArrayList<String> elements = new ArrayList<String>();

		elements.add(CONQAT_PROJ + "/lib/ccsm-commons.jar");
		elements.add(CONQAT_PROJ + "/lib/log4j.jar");
		elements.add(CONQAT_PROJ + "/lib/ant.jar");
		elements.add(CONQAT_PROJ + "/lib/javadoc.jar");
		elements.add(CONQAT_PROJ + "/lib/junit.jar");
		elements.add(CONQAT_PROJ + "/lib/cqddl.jar");
		elements.add(CONQAT_PROJ + "/lib/antlr-runtime.jar");

		for (BundleInfo bundle : bundles) {
			for (File lib : bundle.getLibraries()) {
				elements.add(lib.getAbsolutePath());
			}
		}
		return elements;
	}

	/** Create source pathes for JavaDoc. */
	private ArrayList<String> createSourcePathes() {
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(CONQAT_PROJ + "/src");
		for (BundleInfo bundle : bundles) {
			File srcDirectory = new File(bundle.getLocation(), "src");
			if (srcDirectory.isDirectory()) {
				paths.add(srcDirectory.getAbsolutePath());
			}
		}
		return paths;
	}

	/** Create set of top level packages for JavaDoc. */
	private HashSet<String> findTopLevelPackages() {
		HashSet<String> packages = new HashSet<String>();
		for (BundleInfo bundle : bundles) {
			File srcDirectory = new File(bundle.getLocation(), "src");
			if (srcDirectory.isDirectory()) {
				for (File file : srcDirectory
						.listFiles(new RegularDirectoryFilter())) {
					packages.add(file.getName());
				}
			}
		}
		return packages;
	}
}