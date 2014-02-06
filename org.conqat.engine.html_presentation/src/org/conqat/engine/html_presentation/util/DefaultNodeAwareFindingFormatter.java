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
package org.conqat.engine.html_presentation.util;

import org.conqat.engine.commons.findings.location.ElementLocation;
import org.conqat.engine.commons.findings.location.TextRegionLocation;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.html_presentation.links.LinkProviderBase;
import org.conqat.engine.html_presentation.listing.ListingWriter;
import org.conqat.lib.commons.string.StringUtils;

/**
 * A {@link NodeAwareFindingFormatterBase} that relies on the link stored in the
 * node for linking.
 * 
 * @author $Author: goede $
 * @version $Rev: 41731 $
 * @ConQAT.Rating GREEN Hash: BD20AC507C4465350B8FF47473E4D608
 */
public class DefaultNodeAwareFindingFormatter extends
		NodeAwareFindingFormatterBase {

	/** The link stored in the local node (or null). */
	private final String link;

	/** Constructor. */
	public DefaultNodeAwareFindingFormatter(IConQATNode localNode) {
		super(localNode);

		link = LinkProviderBase.obtainLink(localNode);
	}

	/** {@inheritDoc} */
	@Override
	protected String determineUrl(ElementLocation location) {
		if (!uniformPath.equals(location.getUniformPath())) {
			return null;
		}

		if (link == null) {
			return null;
		}

		String suffix = StringUtils.EMPTY_STRING;
		if (location instanceof TextRegionLocation) {
			int line = ((TextRegionLocation) location).getRawStartLine();
			suffix = "#" + ListingWriter.getLineId(line);
		}

		return link + suffix;
	}

}
