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
package org.conqat.engine.dotnet.resource;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.logging.IncludeExcludeListLogMessage;
import org.conqat.engine.commons.logging.StructuredLogTags;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.dotnet.util.MappingFileUtils;
import org.conqat.engine.resource.IContentAccessor;
import org.conqat.engine.resource.scope.PatternSupportingScopeBase;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.SetMap;

/**
 * Base class for processors that extract {@link IContentAccessor}s from scope
 * descriptors (given as {@link ITextResource}s). A scope descriptor is a file
 * which contains a list of file paths that define the actual scope. An example
 * are the source files defined in a Visual Studio Project file.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 45819 $
 * @ConQAT.Rating GREEN Hash: 3ACBF44A984EF05BF63EB509057FCFB6
 */
public abstract class ContentAccessorExtractorBase extends
		PatternSupportingScopeBase {

	/** Root node of input */
	protected ITextResource input;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "mapping-file", attribute = "path", optional = true, description = "If set, mapping from scope descriptor elements to extracted content accessors is written to this file.")
	public String mappingFile;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.INPUT_NAME, description = ConQATParamDoc.INPUT_DESC, minOccurrences = 1, maxOccurrences = 1)
	public void setInput(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) ITextResource input) {
		this.input = input;
	}

	/** {@inheritDoc} */
	@Override
	public IContentAccessor[] createAccessors() throws ConQATException {

		// Maps from uniform path of scope descriptor to content accessors.
		SetMap<String, IContentAccessor> scopeDescriptorsToAccessors = new SetMap<String, IContentAccessor>();

		Set<String> excludedPaths = new HashSet<String>();

		for (ITextElement scopeDescriptor : ResourceTraversalUtils
				.listTextElements(input)) {
			Set<String> relativePaths = extractRelativePaths(scopeDescriptor);
			for (String relativePath : relativePaths) {
				processExtractedPath(scopeDescriptor, relativePath,
						scopeDescriptorsToAccessors, excludedPaths);
			}
		}

		Set<IContentAccessor> includedAccessors = scopeDescriptorsToAccessors
				.getValues();

		logIncludedElements(includedAccessors);
		if (excludedPaths.size() > 0) {
			logExcludedElements(excludedPaths);
		}

		if (mappingFile != null) {
			MappingFileUtils.writeMapping(mappingFile,
					scopeDescriptorsToAccessors);
		}

		return CollectionUtils.toArray(includedAccessors,
				IContentAccessor.class);
	}

	/**
	 * Template method that extracts a set of paths from the scope descriptor
	 * text element. The paths are interpreted relative to the scope
	 * descriptor's location.
	 */
	protected abstract Set<String> extractRelativePaths(
			ITextElement scopeDescriptor) throws ConQATException;

	/** Check if element is included and add to corresponding set. */
	private void processExtractedPath(ITextElement scopeDescriptor,
			String relativePath,
			SetMap<String, IContentAccessor> scopeDescriptorsToAccessors,
			Set<String> excludedElements) throws ConQATException {
		String uniformPath = scopeDescriptor
				.createRelativeUniformPath(relativePath);
		if (isIncluded(uniformPath)) {
			try {
				IContentAccessor contentAccessor = scopeDescriptor
						.createRelativeAccessor(relativePath);
				scopeDescriptorsToAccessors.add(
						scopeDescriptor.getUniformPath(), contentAccessor);
			} catch (ConQATException e) {
				getLogger().error(
						"Could not access: " + uniformPath + ": "
								+ e.getMessage());
			}
		} else {
			excludedElements.add(uniformPath);
		}
	}

	/** Log included accessors */
	private void logIncludedElements(
			Collection<IContentAccessor> includedElements) {
		Set<String> uniformPaths = new HashSet<String>();
		for (IContentAccessor accessor : includedElements) {
			uniformPaths.add(accessor.getUniformPath());
		}

		logElements(uniformPaths, true);
	}

	/** Log excluded paths */
	private void logExcludedElements(Collection<String> excludedElements) {
		logElements(excludedElements, false);
	}

	/** Logs a collection of paths either as included or excluded log message. */
	private void logElements(Collection<String> elements, boolean included) {
		getLogger().info(
				new IncludeExcludeListLogMessage("files", included, elements,
						StructuredLogTags.SCOPE, StructuredLogTags.FILES));
	}

}