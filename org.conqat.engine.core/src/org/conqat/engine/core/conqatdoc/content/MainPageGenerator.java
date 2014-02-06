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
package org.conqat.engine.core.conqatdoc.content;

import static org.conqat.lib.commons.html.EHTMLAttribute.BORDER;
import static org.conqat.lib.commons.html.EHTMLAttribute.HREF;
import static org.conqat.lib.commons.html.EHTMLAttribute.SRC;
import static org.conqat.lib.commons.html.EHTMLAttribute.STYLE;
import static org.conqat.lib.commons.html.EHTMLAttribute.USEMAP;
import static org.conqat.lib.commons.html.EHTMLElement.A;
import static org.conqat.lib.commons.html.EHTMLElement.H2;
import static org.conqat.lib.commons.html.EHTMLElement.IMG;
import static org.conqat.lib.commons.html.EHTMLElement.P;
import static org.conqat.lib.commons.html.EHTMLElement.TABLE;
import static org.conqat.lib.commons.html.EHTMLElement.TD;
import static org.conqat.lib.commons.html.EHTMLElement.TR;
import static org.conqat.lib.commons.string.StringUtils.CR;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.conqat.engine.core.ConQATInfo;
import org.conqat.engine.core.bundle.BundleDependency;
import org.conqat.engine.core.bundle.BundleInfo;
import org.conqat.engine.core.conqatdoc.compare.BundleInfoIdComparator;
import org.conqat.lib.commons.graph.GraphvizException;
import org.conqat.lib.commons.graph.GraphvizGenerator;
import org.conqat.lib.commons.html.EHTMLAttribute;
import org.conqat.lib.commons.html.EHTMLElement;

/**
 * Generator class for the main page of the documentation listing all installed
 * bundles and descriptions. Additionally the bundle dependencies are shown as a
 * graph. This is shown in the main frame.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: AB411BEEFE14EB7EE5DD96EF5ABA7A00
 */
public class MainPageGenerator extends ContentPageGeneratorBase {

	/** The name of the HTML page generated. */
	public static final String PAGE_NAME = "_overview.html";

	/** The name of the graph image created (without extension). */
	private static final String GRAPH_NAME = "dependencies";

	/** The DOT header used. */
	private final static String DOT_HEADER = "digraph " + GRAPH_NAME + " {"
			+ CR + "  edge [  fontname = \"Helvetica\"," + CR
			+ "          color = \"#639CCE\", fontsize = 8 ];" + CR
			+ "  node [  color = \"#639CCE\"," + CR
			+ "          fontname = \"Helvetica\"," + CR
			+ "          shape = \"box\",\n" + "          fontsize    = 9,"
			+ CR + " height=0.25];" + CR;

	/** The list of bundles. */
	private final List<BundleInfo> bundles;

	/** Create a new generator for the main page. */
	public MainPageGenerator(File targetDirectory,
			Collection<BundleInfo> bundles) {
		super(targetDirectory);

		this.bundles = new ArrayList<BundleInfo>(bundles);
		Collections.sort(this.bundles, new BundleInfoIdComparator());
	}

	/** {@inheritDoc} */
	@Override
	protected String getPageName() {
		return PAGE_NAME;
	}

	/** {@inheritDoc} */
	@Override
	protected String getPageTitle() {
		return "ConQAT Installation Overview";
	}

	/** {@inheritDoc} */
	@Override
	protected void appendContents() throws IOException {
		pageWriter.addClosedTextElement(P, "ConQAT " + ConQATInfo.DIST_VERSION
				+ " (core " + ConQATInfo.CORE_VERSION + ")");

		generateInstalledList();
		generateBundleDependencyGraph();
	}

	/** Generate the list of installed bundles. */
	private void generateInstalledList() {
		pageWriter.addClosedTextElement(H2, "Installed bundles");

		pageWriter.openElement(TABLE);
		for (BundleInfo bundle : bundles) {
			pageWriter.openElement(TR);
			pageWriter.openElement(TD);
			pageWriter.addClosedTextElement(A, bundle.getId(), HREF,
					BundleDetailsPageGenerator.getPageName(bundle));
			pageWriter.closeElement(TD);
			pageWriter.addClosedTextElement(TD, bundle.getDescription());
			pageWriter.closeElement(TR);
		}
		pageWriter.closeElement(TABLE);
	}

	/** Generate the (clickable) image of bundle dependencies. */
	private void generateBundleDependencyGraph() throws IOException {
		pageWriter.addClosedTextElement(H2, "Bundle dependencies");

		GraphvizGenerator gig = new GraphvizGenerator();
		String filename = GRAPH_NAME + "." + GRAPH_FORMAT.getFileExtension();
		try {
			String imageMap = gig.generateFileAndImageMap(buildGraph(),
					getFile(filename), GRAPH_FORMAT);
			pageWriter.addClosedElement(IMG, SRC, filename, BORDER, 0, USEMAP,
					"#" + GRAPH_NAME);
			pageWriter.openElement(EHTMLElement.MAP, EHTMLAttribute.ID,
					GRAPH_NAME, EHTMLAttribute.NAME, GRAPH_NAME);
			pageWriter.addRawString(imageMap);
			pageWriter.closeElement(EHTMLElement.MAP);
		} catch (GraphvizException e) {
			pageWriter.addClosedTextElement(P,
					"dot not found! Graphviz not installed?", STYLE,
					"color=red");
		}

	}

	/** Build the actual dependency graph in DOT format. */
	private String buildGraph() {
		StringBuilder sb = new StringBuilder();
		sb.append(DOT_HEADER);

		// nodes
		for (BundleInfo bundle : bundles) {
			sb.append("\"" + bundle.getId() + "\" [label=\"" + bundle.getName()
					+ "\\n" + bundle.getId() + "\",URL=\""
					+ BundleDetailsPageGenerator.getPageName(bundle) + "\"];"
					+ CR);
		}

		// edges
		for (BundleInfo bundle : bundles) {
			for (BundleDependency dep : bundle.getDependencies()) {
				sb.append("\"" + bundle.getId() + "\" -> \"" + dep.getId()
						+ "\" [label=\"" + dep.getVersion() + "\"];" + CR);
			}
		}

		sb.append("}" + CR);
		return sb.toString();
	}

}