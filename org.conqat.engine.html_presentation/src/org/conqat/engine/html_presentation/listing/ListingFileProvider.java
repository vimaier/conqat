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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.lib.commons.clone.IDeepCloneable;
import org.conqat.lib.commons.filesystem.CanonicalFile;

/**
 * {@ConQAT.Doc}
 * <p>
 * As the full uniform path is typically too long and might even contain
 * characters that are no valid file names, we create unique file names in this
 * class. The generated file name consists of a number and the file name
 * extension. This class contains the parameters that are used for mapping
 * listings to files.
 * <p>
 * For testing it can be important to get stable file names. Thus it is possible
 * to pre-reserve (stable) file names in one large step using a special
 * parameter instead of creating filenames as they are queried from other
 * processors.
 * 
 * @author $Author: goede $
 * @version $Rev: 38084 $
 * @ConQAT.Rating GREEN Hash: 7003B40796EF286752340509B428F833
 */
@AConQATProcessor(description = "This processor provides the mapping of elements to the files to store the listings in.")
public class ListingFileProvider extends ConQATProcessorBase implements
		IDeepCloneable {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "directory", attribute = "name", optional = true, description = "Changes the name of the subdirectory used for storing the listings.")
	public String subdirectoryName = "listings";

	/**
	 * Mapping of uniform path to an index used. The index is used for
	 * generation of the file names.
	 */
	private final Map<String, Integer> uniformPathToIndex = new HashMap<String, Integer>();

	/** {@ConQAT.Doc}. */
	@AConQATParameter(name = "reserve-names", description = "This parameter allows to pre-reserve names for resources. This can be used to make the filenames stable for these elements (e.g. when regression testing).")
	public void reserveNamesForResource(
			@AConQATAttribute(name = "ref", description = "Reference to the resources.") IResource resource) {
		reserveNames(ResourceTraversalUtils.listElements(resource));
	}

	/**
	 * Reserves internal names for the given elements. As the name reservation
	 * sorts the uniform paths, calling this method can ensure the naming to be
	 * stable, which is useful for regression testing.
	 */
	public void reserveNames(Collection<IElement> elements) {
		List<String> uniformPaths = new ArrayList<String>();
		for (IElement element : elements) {
			uniformPaths.add(element.getUniformPath());
		}

		Collections.sort(uniformPaths);
		for (String uniformPath : uniformPaths) {
			determineFilename(uniformPath);
		}
	}

	/** Returns the basic filename for a given uniform path. */
	private String determineFilename(String uniformPath) {
		Integer index = uniformPathToIndex.get(uniformPath);
		if (index == null) {
			index = uniformPathToIndex.size();
			uniformPathToIndex.put(uniformPath, index);
		}
		return String.format("%06d.html", index);
	}

	/** Returns the link for an element relative to another listing file. */
	public String getListingLink(IElement element) {
		return determineFilename(element.getUniformPath());
	}

	/** Returns the link for an element relative to the root of the HTML output. */
	public String getRootLink(IElement element) {
		return subdirectoryName + "/" + getListingLink(element);
	}

	/** Returns the link for a uniform path to the root of the HTML output. */
	public String getRootLink(String uniformPath) {
		return subdirectoryName + "/" + determineFilename(uniformPath);
	}

	/**
	 * Returns the file to place the element's listing into for a given output
	 * directory.
	 * 
	 * @throws IOException
	 *             in case of canonization problems.
	 */
	public CanonicalFile getListingFile(IElement element,
			CanonicalFile outputDir) throws IOException {
		return new CanonicalFile(outputDir, getRootLink(element));
	}

	/** {@inheritDoc} */
	@Override
	public ListingFileProvider process() {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public IDeepCloneable deepClone() {
		return this;
	}
}
