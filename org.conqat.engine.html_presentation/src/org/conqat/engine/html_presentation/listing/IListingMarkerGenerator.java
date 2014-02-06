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
package org.conqat.engine.html_presentation.listing;

import java.util.List;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.ITextElement;

/**
 * Interface for generating markers for listings.
 * 
 * @author $Author: goede $
 * @version $Rev: 41731 $
 * @ConQAT.Rating GREEN Hash: 1EFE72C3C7093EC01683D04B2D8F68AD
 */
public interface IListingMarkerGenerator {

	/** Returns the markers for the given element (may return null). */
	List<ListingMarkerDescriptor> generateMarkers(ITextElement element)
			throws ConQATException;
}
