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
package org.conqat.engine.svn;

import java.util.HashSet;
import java.util.Set;

import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.base.ElementTraversingProcessorBase;
import org.conqat.engine.resource.util.ResourceUtils;
import org.conqat.lib.commons.filesystem.CanonicalFile;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNPropertyData;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCClient;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: juergens $
 * @version $Rev: 35205 $
 * @ConQAT.Rating GREEN Hash: C37BB5073839FA117D69FAAA49BB624B
 */
@AConQATProcessor(description = "Extracts svn-properties from resource elements. The property"
		+ "values are annotated at the corresponding elements using the property name as"
		+ "key. The processor issues warning messages for elements that are not under version"
		+ "control. This only works for elements resisiding in the file system.")
public class SVNPropertiesExtractor extends
		ElementTraversingProcessorBase<IResource, IElement> {

	/** Working Copy client used to extract property information */
	private final SVNWCClient wcClient = SVNClientManager.newInstance()
			.getWCClient();

	/** Set of property names that are extracted */
	private final Set<String> propertyNames = new HashSet<String>();

	/** Adds a property name */
	@AConQATParameter(name = "property", description = "Adds a property name", minOccurrences = 1)
	public void addPropertyName(
			@AConQATAttribute(name = "name", description = "Property name") String propertyName) {
		propertyNames.add(propertyName);
	}

	/** {@inheritDoc} */
	@Override
	protected void setUp(IResource root) {
		NodeUtils.addToDisplayList(root, propertyNames);
	}

	/** {@inheritDoc} */
	@Override
	protected void processElement(IElement node) throws ConQATException {

		CanonicalFile file = ResourceUtils.getFile(node);
		if (file == null) {
			throw new ConQATException("Could not convert " + node.getLocation()
					+ " to file!");
		}

		try {
			for (String propertyName : propertyNames) {
				SVNPropertyData property = wcClient.doGetProperty(file,
						propertyName, SVNRevision.WORKING, SVNRevision.WORKING);

				if (property != null) {
					node.setValue(propertyName, property.getValue());
				}
			}
		} catch (SVNException e) {
			getLogger().warn(
					"Couldn't determine properties for " + node.getLocation()
							+ ": " + e.getMessage());
		}
	}

}