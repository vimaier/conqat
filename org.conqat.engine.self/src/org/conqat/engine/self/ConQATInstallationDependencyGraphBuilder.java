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
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.JavaClass;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.commons.node.NodeConstants;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.bundle.BundleDependency;
import org.conqat.engine.core.bundle.BundleInfo;
import org.conqat.engine.core.bundle.BundleUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.driver.BlockFileReader;
import org.conqat.engine.core.driver.error.DriverException;
import org.conqat.engine.core.driver.util.XmlToken;
import org.conqat.engine.graph.nodes.ConQATGraph;
import org.conqat.engine.graph.nodes.ConQATGraphInnerNode;
import org.conqat.engine.graph.nodes.ConQATVertex;
import org.conqat.engine.graph.nodes.DeepCloneCopyAction;
import org.conqat.engine.java.library.JavaLibrary;
import org.conqat.engine.self.scope.ConQATBundleNode;
import org.conqat.engine.self.scope.ConQATInstallationRoot;
import org.conqat.lib.commons.assessment.Assessment;
import org.conqat.lib.commons.assessment.ETrafficLightColor;
import org.conqat.lib.commons.filesystem.FileExtensionFilter;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * This class creates the dependency graph for a given ConQAT installation.
 * 
 * @author $Author: steidl $
 * @version $Rev: 43637 $
 * @ConQAT.Rating GREEN Hash: 40B1E8153CA30C44534918B4AE377BF1
 */
@AConQATProcessor(description = "This processor creates a dependency graph for a given ConQAT "
		+ "installation. This graph has arcs from a block or class to all blocks or classes it uses. "
		+ "The blocks and classes are hierarchically ordered by the bundle they belong to.")
public class ConQATInstallationDependencyGraphBuilder extends
		ConQATProcessorBase {

	/** Key used for edge assessment. */
	@AConQATKey(description = "Key used to append edge assessments", type = "org.conqat.lib.commons.assessment.Assessment")
	public static final String ASSESSMENT_KEY = "assessment";

	/** Key used for edge assessment. */
	@AConQATKey(description = "Key used to append violation lists to nodes.", type = "java.util.List<String>")
	public static final String VIOLATIONS_KEY = "violations";

	/** The ConQAT installation to look at. */
	private ConQATInstallationRoot conqatRoot;

	/** The graph returned. */
	private final ConQATGraph graph = new ConQATGraph("Bundle dependencies",
			"Bundle dependencies");

	/** Dependency infos. */
	private final Map<ConQATVertex, Collection<String>> depInfo = new HashMap<ConQATVertex, Collection<String>>();

	/** The parent bundle for each dependency. */
	private final Map<String, BundleInfo> parentBundle = new HashMap<String, BundleInfo>();

	/** Set the ConQAT installation. */
	@AConQATParameter(name = "conqat", minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "The ConQAT installation being converted.")
	public void setConQATDirectory(
			@AConQATAttribute(name = "root", description = "Reference to the generating processor.") ConQATInstallationRoot conqatRoot) {
		this.conqatRoot = conqatRoot;
	}

	/** {@inheritDoc} */
	@Override
	public ConQATGraph process() throws ConQATException {
		NodeUtils.addToDisplayList(graph, VIOLATIONS_KEY);

		createConQATNodes();
		createBundleNodes();
		insertEdges();

		getLogger().info(
				"Created graph has " + graph.getVertices().size()
						+ " vertices and " + graph.getEdges().size()
						+ " edges.");
		return graph;
	}

	/** Creates the nodes and vertices for ConQAT core. */
	private void createConQATNodes() throws ConQATException {
		ConQATGraphInnerNode conqatNode = graph.createChildNode(
				"ConQAT classes", "ConQAT");
		handleClasses(conqatNode, null,
				new File(conqatRoot.getConQATDirectory(), "build"));
		for (File lib : new File(conqatRoot.getConQATDirectory(), "lib")
				.listFiles(new FileExtensionFilter("jar"))) {
			handleJAR(conqatNode, null, lib);
		}
	}

	/** Creates the nodes and vertices for ConQAT bundles. */
	private void createBundleNodes() throws ConQATException {
		for (ConQATBundleNode bundleNode : conqatRoot.getChildren()) {
			BundleInfo bi = bundleNode.getBundleInfo();

			ConQATGraphInnerNode graphNode = graph.createChildNode(
					bundleNode.getId(), bundleNode.getName());

			ConQATGraphInnerNode blockNode = graphNode.createChildNode(
					graphNode.getId() + " blocks", "blocks");
			for (File xmlFile : BundleUtils
					.getProvidedBlockSpecificationFiles(bi)) {
				handleBlocks(blockNode, bi, xmlFile);
			}
			if (!blockNode.hasChildren()) {
				blockNode.remove();
			}

			ConQATGraphInnerNode classNode = graphNode.createChildNode(
					graphNode.getId() + " classes", "classes");
			handleClasses(classNode, bi, bi.getClassesDirectory());
			for (File lib : bi.getLibraries()) {
				handleJAR(classNode, bi, lib);
			}
			if (!classNode.hasChildren()) {
				classNode.remove();
			}
		}
	}

	/**
	 * Handle all classes in the given directory and its subdirectories and
	 * append corresponding vertices to the given node.
	 */
	private void handleClasses(ConQATGraphInnerNode node, BundleInfo bundle,
			File dir) {
		for (File classFile : FileSystemUtils.listFilesRecursively(dir,
				new FileExtensionFilter("class"))) {
			try {
				JavaClass clazz = new ClassParser(classFile.getPath()).parse();
				makeClassNode(node, bundle, clazz);
			} catch (ClassFormatException e) {
				getLogger().warn("Could not parse " + classFile, e);
			} catch (IOException e) {
				getLogger().warn("Could not read " + classFile, e);
			}
		}
	}

	/**
	 * Handle all classes in the given JAR file and append corresponding
	 * vertices to the given node.
	 */
	private void handleJAR(ConQATGraphInnerNode node, BundleInfo bundle,
			File jarFile) {
		JarFile jf;
		try {
			jf = new JarFile(jarFile);
		} catch (IOException e) {
			getLogger().warn("Could not access JAR " + jarFile, e);
			return;
		}

		Enumeration<JarEntry> it = jf.entries();
		while (it.hasMoreElements()) {
			JarEntry entry = it.nextElement();
			if (entry.getName().endsWith(".class")) {
				try {
					JavaClass clazz = new ClassParser(jarFile.getPath(),
							entry.getName()).parse();
					makeClassNode(node, bundle, clazz);
				} catch (ClassFormatException e) {
					getLogger().warn(
							"Could not parse " + jarFile.getPath() + ":"
									+ entry.getName(), e);
				} catch (IOException e) {
					getLogger().warn(
							"Could not read " + jarFile.getPath() + ":"
									+ entry.getName(), e);
				}
			}
		}
	}

	/** Create the vertex and dependencies for the given class. */
	private void makeClassNode(ConQATGraphInnerNode node, BundleInfo bundle,
			JavaClass clazz) {
		String className = clazz.getClassName();
		Set<String> dependencies = new HashSet<String>();

		ConstantPool cp = clazz.getConstantPool();
		for (Constant c : cp.getConstantPool()) {
			if (c instanceof ConstantClass) {
				String usedClassName = cp.constantToString(c);
				usedClassName = JavaLibrary
						.ignoreArtificialPrefix(usedClassName);
				if (usedClassName.equals("")
						|| usedClassName.startsWith("java.")
						|| usedClassName.startsWith("javax.")
						|| usedClassName.startsWith("org.xml.")
						|| usedClassName.startsWith("org.w3c.")
						|| className.equals(usedClassName)) {
					continue;
				}
				dependencies.add(usedClassName);
			}
		}

		try {
			ConQATVertex v = graph.createVertex(className, className, node);
			depInfo.put(v, dependencies);
			parentBundle.put(className, bundle);
		} catch (ConQATException e) {
			getLogger().warn("Duplicate class: " + className);
		}
	}

	/** Handle all block-specs in the given XML file. */
	private void handleBlocks(ConQATGraphInnerNode node, BundleInfo bundle,
			File xmlFile) {
		Element conqatElement;
		try {
			conqatElement = BlockFileReader.loadConqatElement(xmlFile);
		} catch (DriverException e) {
			getLogger().warn("Could not parse file " + xmlFile, e);
			return;
		}

		for (Element elem : XMLUtils
				.elementNodes(conqatElement
						.getElementsByTagName(XmlToken.XML_ELEMENT_BLOCK_SPECIFICATION))) {
			makeBlockNode(node, bundle, elem);
		}
	}

	/** Create the vertex and dependencies for the given block spec. */
	private void makeBlockNode(ConQATGraphInnerNode node, BundleInfo bundle,
			Element blockSpec) {
		final String PREFIX = "block ";

		String blockName = blockSpec.getAttribute(XmlToken.XML_ATTRIBUTE_NAME);
		Set<String> dependencies = new HashSet<String>();

		for (Element elem : XMLUtils.elementNodes(blockSpec
				.getElementsByTagName(XmlToken.XML_ELEMENT_PROCESSOR))) {
			dependencies.add(elem.getAttribute(XmlToken.XML_ATTRIBUTE_CLASS));
		}
		for (Element elem : XMLUtils.elementNodes(blockSpec
				.getElementsByTagName(XmlToken.XML_ELEMENT_BLOCK))) {
			dependencies.add(PREFIX
					+ elem.getAttribute(XmlToken.XML_ATTRIBUTE_SPEC));
		}

		try {
			ConQATVertex v = graph.createVertex(PREFIX + blockName, PREFIX
					+ blockName, node);
			depInfo.put(v, dependencies);
			parentBundle.put(PREFIX + blockName, bundle);
		} catch (ConQATException e) {
			getLogger().warn("Duplicate block specification: " + blockName);
		}
	}

	/** Insert all edges based on the {@link #depInfo} map. */
	private void insertEdges() throws ConQATException {
		ConQATGraphInnerNode unknownNode = graph.createChildNode("Unknown",
				"Unknown");

		for (Entry<ConQATVertex, Collection<String>> entry : depInfo.entrySet()) {
			ConQATVertex source = entry.getKey();
			for (String dep : entry.getValue()) {
				insertDependency(source, dep, unknownNode);
			}
		}
		if (!unknownNode.hasChildren()) {
			unknownNode.remove();
		}
	}

	/** Inserts a dependency as edge into the graph. */
	private void insertDependency(ConQATVertex sourceVertex, String dependency,
			ConQATGraphInnerNode unknownNode) throws ConQATException {
		BundleInfo sourceBundle = parentBundle.get(sourceVertex.getId());
		ConQATVertex target = graph.getVertexByID(dependency);

		if (target == null) {
			target = graph.createVertex(dependency, dependency, unknownNode);
		}

		ETrafficLightColor color = ETrafficLightColor.GREEN;
		BundleInfo targetBundle = parentBundle.get(dependency);
		if ((target.getParent() == unknownNode)) {
			color = ETrafficLightColor.RED;
			NodeUtils.getOrCreateStringList(sourceVertex, VIOLATIONS_KEY).add(
					"Link to unknown target: " + dependency);
		} else if (!dependsOn(sourceBundle, targetBundle)) {
			color = ETrafficLightColor.RED;
			NodeUtils.getOrCreateStringList(sourceVertex, VIOLATIONS_KEY).add(
					targetBundle.getId() + ": " + dependency);
		}

		NodeUtils.getOrCreateAssessment(graph, NodeConstants.SUMMARY)
				.add(color);
		graph.addEdge(sourceVertex, target).setUserDatum(ASSESSMENT_KEY,
				new Assessment(color), DeepCloneCopyAction.getInstance());
	}

	/** Returns whether the source bundle depends on the target bundle. */
	private boolean dependsOn(BundleInfo sourceBundle, BundleInfo targetBundle) {
		if (targetBundle == null) {
			// allowed
			return true;
		}
		if (sourceBundle == null) {
			// ConQAT may not access other
			return false;
		}
		if (sourceBundle.getId().equals(targetBundle.getId())) {
			return true;
		}
		for (BundleDependency dep : sourceBundle.getDependencies()) {
			if (dep.getId().equals(targetBundle.getId())) {
				return true;
			}
		}
		return false;
	}
}