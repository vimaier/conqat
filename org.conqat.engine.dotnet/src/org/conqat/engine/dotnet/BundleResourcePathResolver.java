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
package org.conqat.engine.dotnet;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * Resolves a bundle-relative resource path to an absolute path.
 * <p>
 * If no relative path (or the empty string) is given, the absolute path to the
 * resources directory of the bundle is returned.
 * 
 * @author juergens
 * @author $Author: juergens $
 * @version $Rev: 35167 $
 * @ConQAT.Rating GREEN Hash: 861F3A4164D0E1EC179FE75AFCC54A8D
 */
@AConQATProcessor(description = "Resolves a bundle-relative path to an absolute path")
public class BundleResourcePathResolver extends ConQATProcessorBase {

	/** Relative path that gets resolved to an absolute path */
	private String relativePath = "";

	/** ConQAT Parameter */
	@AConQATParameter(name = "bundle-relative", description = "Bundle relative path", minOccurrences = 0, maxOccurrences = 1)
	public void setRelativePath(
			@AConQATAttribute(name = "path", description = "Bundle relative path") String relativePath) {
		this.relativePath = relativePath;
	}

	/** {@inheritDoc} */
	@Override
	public String process() {
		return BundleContext.getInstance().getResourceManager()
				.getAbsoluteResourcePath(relativePath);
	}

}