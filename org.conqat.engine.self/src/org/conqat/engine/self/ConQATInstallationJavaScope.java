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
package org.conqat.engine.self;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.commons.util.ConQATInputProcessorBase;
import org.conqat.engine.core.bundle.BundleInfo;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.java.resource.IJavaElement;
import org.conqat.engine.java.resource.IJavaResource;
import org.conqat.engine.java.resource.JavaContainer;
import org.conqat.engine.java.resource.JavaElementFactory;
import org.conqat.engine.resource.IContentAccessor;
import org.conqat.engine.resource.scope.filesystem.FileContentAccessor;
import org.conqat.engine.resource.util.ConQATDirectoryScanner;
import org.conqat.engine.resource.util.ConQATFileUtils;
import org.conqat.engine.self.scope.ConQATBundleNode;
import org.conqat.engine.self.scope.ConQATInstallationRoot;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.filesystem.CanonicalFile;
import org.conqat.lib.commons.filesystem.FileExtensionFilter;
import org.conqat.lib.commons.string.StringUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: deissenb $
 * @version $Rev: 39848 $
 * @ConQAT.Rating GREEN Hash: EFE430C9F4AD1172049DDDEEAC7E51F9
 */
@AConQATProcessor(description = "This processor creates a JavaRootElement from the "
		+ "given ConQATInstallationRoot including ConQAT and all its bundles.")
public class ConQATInstallationJavaScope extends
		ConQATInputProcessorBase<ConQATInstallationRoot> {

	/** The name of the ConQAT project itself. */
	private static final String CONQAT = "ConQAT";

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "test-src", attribute = "include", optional = true, description = ""
			+ "Decide whether to also include the test sources. Default is false.")
	public boolean includeTests = false;

	/** Factory used for creating byte-code. */
	private final JavaElementFactory factory = new JavaElementFactory();

	/** The accessors used for Java files. */
	private final List<IContentAccessor> javaAccessors = new ArrayList<IContentAccessor>();

	/** The accessors used for class files. */
	private final List<IContentAccessor> classAccessors = new ArrayList<IContentAccessor>();

	/** {@inheritDoc} */
	@Override
	public IJavaResource process() throws ConQATException {
		factory.init(getProcessorInfo());

		addConQAT();
		for (ConQATBundleNode bundleNode : input.getChildren()) {
			addBundle(bundleNode);
		}

		factory.addByteCodeAccessors(classAccessors
				.toArray(new IContentAccessor[classAccessors.size()]));
		factory.process();

		JavaContainer rootPackage = new JavaContainer(StringUtils.EMPTY_STRING);
		for (IContentAccessor javaAccessor : javaAccessors) {
			ITokenElement element = factory.create(javaAccessor);
			CCSMAssert
					.isTrue(element instanceof IJavaElement,
							"As the factory is not lenient, we should get an exception if the java element is not created.");
			insert((IJavaElement) element, rootPackage);
		}
		return rootPackage;
	}

	/** Inserts the given java element into the hierarchy. */
	private void insert(IJavaElement javaElement, JavaContainer container) {
		String[] segments = javaElement.getClassName().split("[.]");
		for (int i = 0; i < segments.length - 1; ++i) {
			IJavaResource child = container.getNamedChild(segments[i]);
			if (!(child instanceof JavaContainer)) {
				child = new JavaContainer(segments[i]);
				container.addChild(child);
			}
			container = (JavaContainer) child;
		}
		container.addChild(javaElement);
	}

	/** Adds the ConQAT directory. */
	private void addConQAT() throws ConQATException {
		scanForJava(new File(input.getConQATDirectory(), "src"), CONQAT);
		if (includeTests) {
			scanForJava(new File(input.getConQATDirectory(), "test-src"),
					CONQAT);
		}

		scanForClass(new File(input.getConQATDirectory(), "build"), CONQAT);

		for (File lib : new File(input.getConQATDirectory(), "lib")
				.listFiles(new FileExtensionFilter("jar"))) {
			getLogger().info("Added class path element " + lib.getPath());
			factory.addClasspathElement(lib.getPath());
		}
	}

	/** Adds a bundle. */
	private void addBundle(ConQATBundleNode bundleNode) throws ConQATException {
		BundleInfo bundleInfo = bundleNode.getBundleInfo();
		File srcDir = new File(bundleInfo.getLocation(), "src");
		File testsrcDir = new File(bundleInfo.getLocation(), "test-src");
		File classesDir = bundleInfo.getClassesDirectory();

		if (srcDir.isDirectory()) {
			scanForJava(srcDir, bundleNode.getId());
		}
		if (includeTests && testsrcDir.isDirectory()) {
			scanForJava(testsrcDir, bundleNode.getId());
		}
		if (classesDir.isDirectory()) {
			scanForClass(classesDir, bundleNode.getId());
		}
		for (File lib : bundleInfo.getLibraries()) {
			getLogger().info("Added class path element " + lib.getPath());
			factory.addClasspathElement(lib.getPath());
		}
	}

	/** Scans for Java files in the given directory. */
	private void scanForJava(File dir, String projectName)
			throws ConQATException {
		scanForFiles(dir, projectName, "source code", "java", javaAccessors);
	}

	/** Scans for class files in the given directory. */
	private void scanForClass(File dir, String projectName)
			throws ConQATException {
		scanForFiles(dir, projectName, "byte code", "class", classAccessors);
	}

	/** Scans for files with given extensions and adds them to the target list. */
	private void scanForFiles(File dir, String projectName, String fileType,
			String extension, List<IContentAccessor> target)
			throws ConQATException {
		if (!dir.isDirectory()) {
			throw new ConQATException("Expected " + dir + " to be a directory!");
		}

		getLogger().info("Added " + fileType + " directory " + dir);

		String[] filenames = ConQATDirectoryScanner.scan(dir.getAbsolutePath(),
				true, new String[] { "**/*." + extension }, new String[] {
						"**/.svn/**", "**/*$*" });

		CanonicalFile root = ConQATFileUtils.createCanonicalFile(dir
				.getParentFile());

		for (int i = 0; i < filenames.length; ++i) {
			target.add(new FileContentAccessor(ConQATFileUtils
					.createCanonicalFile(new File(dir, filenames[i])), root,
					projectName));
		}
	}
}