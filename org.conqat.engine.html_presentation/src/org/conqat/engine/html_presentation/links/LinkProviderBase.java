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
package org.conqat.engine.html_presentation.links;

import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.commons.traversal.NodeTraversingProcessorBase;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.ConQATException;

/**
 * Base class for link providers.
 * 
 * @author $Author: juergens $
 * @version $Rev: 42174 $
 * @ConQAT.Rating GREEN Hash: FE20C0892E2C32280537A3D1AF24DCD3
 */
public abstract class LinkProviderBase<E extends IConQATNode> extends
		NodeTraversingProcessorBase<E> {

	/** The key used for storing links. */
	@AConQATKey(description = "Stores an HTML link.", type = "java.lang.String")
	public static final String LINK_KEY = "link";

	/** The key used for storing link targets. */
	@AConQATKey(description = "Stores an HTML link target.", type = "org.conqat.engine.html_presentation.links.ELinkTarget")
	public static final String LINK_KEY_TARGET = "link_target";

	/** {@inheritDoc} */
	@Override
	public void visit(E node) throws ConQATException {
		String link = determineLink(node);
		if (link != null) {
			node.setValue(LINK_KEY, link);
		}
	}

	/**
	 * Template method for providing the link for a node. May return null if no
	 * link can be determined. The link is a string that can be placed into a
	 * href-attribute of an a-element in HTML. Typically this is just a relative
	 * link, but absolute links (e.g. starting with http://) are valid as well.
	 */
	protected abstract String determineLink(E node) throws ConQATException;

	/** {@inheritDoc} */
	@Override
	protected ETargetNodes getTargetNodes() {
		return ETargetNodes.ALL;
	}

	/**
	 * Utility method that returns the link for the given node or null.
	 */
	public static String obtainLink(IConQATNode node) {
		return NodeUtils.getValue(node, LINK_KEY, String.class, null);
	}

	/**
	 * Utility method that returns the link target for the given node or null.
	 */
	public static ELinkTarget obtainLinkTarget(IConQATNode node) {
		return NodeUtils.getValue(node, LINK_KEY_TARGET, ELinkTarget.class,
				null);
	}

}
