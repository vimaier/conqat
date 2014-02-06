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

import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.html_presentation.listing.ListingFileProvider;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.IResource;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: deissenb $
 * @version $Rev: 36232 $
 * @ConQAT.Rating GREEN Hash: 0B36258578AE7876E17E01668F8CEC9B
 */
@AConQATProcessor(description = "Provides links to the elements listing.")
public class ListingLinkProvider extends LinkProviderBase<IResource> {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "file-provider", attribute = "ref", description = "The file provider used for mapping elements to storage files.")
	public ListingFileProvider fileProvider;

	/** {@inheritDoc} */
	@Override
	protected String determineLink(IResource resource) {
		if (resource instanceof IElement) {
			return fileProvider.getRootLink((IElement) resource);
		}
		return null;
	}
}
