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

import org.conqat.engine.core.ConQATInfo;

/**
 * Constant definitions for {@link ConQATANTWriter}.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46088 $
 * @ConQAT.Rating GREEN Hash: CDE51ACE8B4276C0F58B7A101F0244A3
 */
/* package */class BuildFileConstants {

	/** ConQAT-string, used for targets and as directory. : {@value} */
	public final static String CONQAT = "conqat";

	/** Name of the project directory of the ConQAT core. */
	public final static String CONQAT_PROJ = ConQATInfo.class.getPackage()
			.getName();

	/** Suffix to be attached to targets that operate on all bundles. */
	public final static String ALL_SUFFIX = "-all";

	/** Name of the clean-all target: {@value} */
	public final static String CLEAN_ALL_TARGET = "clean" + ALL_SUFFIX;

	/**
	 * Prefix for all compile targets and name of the target called in bundle
	 * build files: {@value}
	 */
	public final static String COMPILE_TARGET = "compile";

	/**
	 * Prefix for all javadoc targets and name of the target called in bundle
	 * build files: {@value}
	 */
	public final static String JAVADOC_TARGET = "javadoc";

	/**
	 * Prefix for all compile test targets and name of the target called in
	 * bundle build files: {@value}
	 */
	public final static String COMPILE_TESTS_TARGET = "compile-tests";

	/** Name of the compile-all target: {@value} */
	public final static String COMPILE_ALL_TARGET = COMPILE_TARGET + ALL_SUFFIX;

	/** Name of the binary distribution target: {@value} */
	public final static String DIST_BINARY_TARGET = "dist-binary";

	/** Name of the monolith distribution target: {@value} */
	public final static String DIST_MONOLITH_TARGET = "dist-monolith";

	/** Name of the source distribution target: {@value} */
	public final static String DIST_SOURCE_TARGET = "dist-source";

	/** Name of the unified distribution target: {@value} */
	public final static String DIST_UNIFIED_TARGET = "dist-unified";

	/** Binary distribution root directory. */
	public final static String BINARY_DIST_ROOT_DIR = "conqat-"
			+ ConQATInfo.DIST_VERSION;

	/** Source distribution root directory. */
	public final static String SOURCE_DIST_ROOT_DIR = "conqat-src-"
			+ ConQATInfo.DIST_VERSION;

	/** Unified distribution root directory. */
	public final static String UNIFIED_DIST_ROOT_DIR = "conqat-unified-"
			+ ConQATInfo.DIST_VERSION;

	/** Directory for ConQAT's build directory (including ConQAT prefix). */
	public final static String CONQAT_BUILD_DIR = CONQAT_PROJ + "/build";

	/** Directory for ConQAT's lib directory (without prefix). */
	public final static String CONQAT_LIB_DIR = "lib";

	/** Directory for the full javadoc documentation. */
	public final static String CONQAT_JAVADOC_DIR = "conqat-javadoc";

	/** Bundles subdirectory: {@value} */
	public final static String BUNDLES_DIR = "bundles";

	/** Binary distribution subdirectory: {@value} */
	public final static String DIST_BINARY_DIR = "dist/binary";

	/** Source distribution subdirectory: {@value} */
	public final static String DIST_SOURCE_DIR = "dist/source";

	/** Unified distribution subdirectory: {@value} */
	public final static String DIST_UNIFIED_DIR = "dist/unified";

	/** Binary dist zip file. */
	public static final String BINARY_DIST_ZIP = CONQAT + "-binary-"
			+ ConQATInfo.DIST_VERSION + ".zip";

	/** Monolith dist jar file. */
	public static final String MONOLITH_DIST_JAR = CONQAT + "-monolith-"
			+ ConQATInfo.DIST_VERSION + ".jar";

	/** Temporary directory for putting all blocks into. */
	public static final String ALL_BLOCKS_DIR = "all-blocks";

	/** Source dist zip file. */
	public static final String SOURCE_DIST_ZIP = CONQAT + "-source-"
			+ ConQATInfo.DIST_VERSION + ".zip";

	/** Unified dist zip file. */
	public static final String UNIFIED_DIST_ZIP = CONQAT + "-unified-"
			+ ConQATInfo.DIST_VERSION + ".zip";

	/** Name of the top-level build.xml. */
	public static final String TOPLEVEL_BUILD_XML = "build.xml";

}