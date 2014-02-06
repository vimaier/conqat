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
package org.conqat.engine.self.scope;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.bundle.BundleException;
import org.conqat.engine.core.bundle.BundleInfo;
import org.conqat.engine.core.bundle.BundleUtils;
import org.conqat.engine.core.bundle.BundlesConfiguration;
import org.conqat.engine.core.bundle.BundlesManager;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.core.IConQATProcessor;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.filesystem.PlainClassFileFilter;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * A scope that creates a {@link ConQATInstallationRoot} node.
 * 
 * @author Benjamin Hummel
 * @author $Author: heineman $
 * @version $Rev: 40893 $
 * @ConQAT.Rating GREEN Hash: 909B82C505761297940D6D87F201DA23
 */
@AConQATProcessor(description = "This processor reads the given ConQAT installation and bundles. "
		+ "By default also the processors and blocks will be contained in the scope, but this can be turned off.")
public class ConQATInstallationScope extends ConQATProcessorBase {

	/**
	 * Possible value for {@link #TYPE_KEY}; this is not an enum to allow
	 * referencing from annotation.
	 */
	public static final String BLOCK_VALUE = "block";

	/**
	 * Possible value for {@link #TYPE_KEY}; this is not an enum to allow
	 * referencing from annotation.
	 */
	public static final String PROCESSOR_VALUE = "processor";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "The type of unit (" + BLOCK_VALUE + " or "
			+ PROCESSOR_VALUE + ")", type = "java.lang.String")
	public static final String TYPE_KEY = "UnitType";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "The pretty name of the bundle", type = "java.lang.String")
	public static final String NAME_KEY = "Name";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "The version of the bundle", type = "org.conqat.lib.commons.version.Version")
	public static final String VERSION_KEY = "Version";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "The provider of the bundle", type = "java.lang.String")
	public static final String PROVIDER_KEY = "Provider";

	/** ConQAT installation directory. */
	private File conqatDir;

	/** Whether units should be included in this scope. */
	private boolean includeUnits = true;

	/** The bundles manager used for loading the bundles. */
	private final BundlesManager bundlesManager = new BundlesManager();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "conqat", minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "The directory ConQAT is installed into.")
	public void setConQATDirectory(
			@AConQATAttribute(name = "dir", description = "The name of the directory.") String dir) {
		conqatDir = new File(dir);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "include-units", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "This flag determines whether units will be shown in this scope. Default is true.")
	public void setIncludeUnits(
			@AConQATAttribute(name = "value", description = "The name of the directory.") boolean includeUnits) {
		this.includeUnits = includeUnits;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "bundle", minOccurrences = 0, description = ""
			+ "Add a single bundle.")
	public void addBundleLocation(
			@AConQATAttribute(name = "dir", description = "The directory name of the bundle.") String dir)
			throws ConQATException {
		try {
			bundlesManager.addBundleLocation(dir);
		} catch (BundleException e) {
			throw new ConQATException(e);
		}
	}

	/** Add a bundle collection. */
	@AConQATParameter(name = "bundle-collection", minOccurrences = 0, description = ""
			+ "Add a collection of bundles.")
	public void addBundleCollection(
			@AConQATAttribute(name = "dir", description = "The name of the directory the bundles are in.") String dir)
			throws ConQATException {
		try {
			bundlesManager.addBundleCollection(dir);
		} catch (BundleException e) {
			throw new ConQATException(e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public ConQATInstallationRoot process() throws ConQATException {
		ConQATInstallationRoot root = new ConQATInstallationRoot(conqatDir);
		if (includeUnits) {
			NodeUtils.addToDisplayList(root, TYPE_KEY);
		}
		NodeUtils.addToDisplayList(root, NAME_KEY, VERSION_KEY, PROVIDER_KEY);

		BundlesConfiguration config;
		try {
			config = bundlesManager.loadAndSortBundles();
		} catch (BundleException e) {
			throw new ConQATException(e);
		}
		for (BundleInfo bundle : config.getBundles()) {
			getLogger().info("Added bundle " + bundle.getId());
			ConQATBundleNode node = new ConQATBundleNode(bundle);
			node.setValue(NAME_KEY, bundle.getName());
			node.setValue(VERSION_KEY, bundle.getVersion());
			node.setValue(PROVIDER_KEY, bundle.getProvider());
			root.addChild(node);
			if (includeUnits) {
				addProcessorNodes(node);
				addBlockNodes(node);
			}
		}
		return root;
	}

	/** Adds all processors to the given bundle node. */
	private void addProcessorNodes(ConQATBundleNode bundleNode) {
		File dir = bundleNode.getBundleInfo().getClassesDirectory();

		for (File classFile : FileSystemUtils.listFilesRecursively(dir,
				new PlainClassFileFilter())) {
			String name = getProcessorName(classFile);
			if (name == null) {
				continue;
			}

			new ConQATUnitNode(name, PROCESSOR_VALUE, bundleNode);
		}
	}

	/** Adds all blocks to the given bundle node. */
	private void addBlockNodes(ConQATBundleNode bundleNode) {
		for (String blockName : BundleUtils
				.getProvidedBlockSpecifications(bundleNode.getBundleInfo())) {
			new ConQATUnitNode(blockName, BLOCK_VALUE, bundleNode);
		}
	}

	/**
	 * Determines for a Java byte-code file whether it is a processor class. If
	 * so, the name of the processor (i.e. class) is returned, otherwise null.
	 * This method is conservative, i.e. in case of I/O problems null is
	 * returned (no advanced error handling). Uses the
	 * {@link ProcessorCheckVisitor} to find whether a class is a processor.
	 */
	private String getProcessorName(File classFile) {
		FileInputStream in = null;
		try {
			in = new FileInputStream(classFile);
			ClassReader reader = new ClassReader(in);
			ProcessorCheckVisitor visitor = new ProcessorCheckVisitor();
			reader.accept(visitor, ClassReader.SKIP_DEBUG
					| ClassReader.SKIP_CODE | ClassReader.SKIP_FRAMES);
			return visitor.getProcessorName();
		} catch (IOException e) {
			getLogger().warn(
					"Could not inspect " + classFile
							+ " while searching for processors!");
			return null;
		} finally {
			FileSystemUtils.close(in);
		}
	}

	/**
	 * ASM visitor which is used to determine whether a class is a processor.
	 * This only checks whether the class is annotated with
	 * {@link AConQATProcessor}, as an instanceof check for
	 * {@link IConQATProcessor} would require to traverse the full hierarchy,
	 * which is expensive.
	 * <p>
	 * Similar functionality is realized in the driver and ConQATDoc, but there
	 * reflection is used. Reflection is better, as the code is more compact and
	 * we can perform more advanced checks there. However, we can not use
	 * reflection here as we are usually analyzing an external ConQAT. Using
	 * reflection in this case will pollute the class loaders and potentially
	 * lead to situations where the wrong class is loaded.
	 */
	private static final class ProcessorCheckVisitor extends ClassVisitor {

		/** Constructor. */
		public ProcessorCheckVisitor() {
			super(Opcodes.ASM4);
		}

		/**
		 * String representation of the {@link AConQATProcessor} annotation
		 * following the Java byte-code encoding for class names.
		 */
		private static final String PROCESSOR_ANNOTATION = "L"
				+ AConQATProcessor.class.getName().replace('.', '/') + ";";

		/** Flag for storing whether the annotation was found. */
		private boolean hasProcessorAnnotation = false;

		/** The name of the visited class. */
		private String className = null;

		/** Returns whether the visited class is a processor. */
		public String getProcessorName() {
			if (hasProcessorAnnotation) {
				return className;
			}
			return null;
		}

		/** {@inheritDoc} */
		@Override
		public void visit(int version, int access, String name,
				String signature, String superName, String[] interfaces) {
			// conversion from Java's byte-code format
			className = name.replace('/', '.');
		}

		/** {@inheritDoc} */
		@Override
		public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
			if (PROCESSOR_ANNOTATION.equals(desc)) {
				hasProcessorAnnotation = true;
			}
			return null;
		}

		/** {@inheritDoc} */
		@Override
		public MethodVisitor visitMethod(int access, String name, String desc,
				String signature, String[] exceptions) {
			// we do not visit methods (and their annotations)
			return null;
		}

		/** {@inheritDoc} */
		@Override
		public FieldVisitor visitField(int access, String name, String desc,
				String signature, Object value) {
			// we do not visit fields (and their annotations)
			return null;
		}
	}
}