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
package org.conqat.engine.cpp.dependency;

import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.conqat.engine.commons.findings.EFindingKeys;
import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.findings.FindingCategory;
import org.conqat.engine.commons.findings.FindingGroup;
import org.conqat.engine.commons.findings.location.TextRegionLocation;
import org.conqat.engine.commons.findings.util.FindingUtils;
import org.conqat.engine.commons.logging.ListStructuredLogMessage;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.IConQATLogger;
import org.conqat.engine.resource.util.UniformPathUtils;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.engine.sourcecode.resource.TokenElementProcessorBase;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.SetMap;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: goede $
 * @version $Rev: 46346 $
 * @ConQAT.Rating YELLOW Hash: ACA2E20C25E0D9603DAC5C38F8CC4FA8
 */
@AConQATProcessor(description = "Annotates a tree of C++ files with dependencies. "
        + "This check for both include dependencies and declare/implement dependencies. "
        + "Additional dependencies can be injected via a processor provided as a parameter.")
public class CppDependencyBuilder extends TokenElementProcessorBase {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Lists the dependencies as findings", type = "org.conqat.engine.commons.findings.FindingsList")
	public static final String KEY = "Depends on";

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "ignore-header-casing", attribute = "value", optional = true, description = ""
	        + "Whether to ignore casing when comparing header names (this is especially relevant on Windows). Default: true.")
	public boolean ignoreFilenameCasing = true;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "transitive-includes", attribute = "value", optional = true, description = ""
	        + "Whether also transitive includes should be resolved. Default: true.")
	public boolean useTransitiveIncludes = true;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "preserve-external-headers", attribute = "value", optional = true, description = ""
	        + "Whether headers that are not found in this scope shall be listed as dependencies. Default: true.")
	public boolean preserveExternalHeaders = true;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "dependency-injector", attribute = "ref", optional = true, description = ""
	        + "Optional class for dealing with implicit dependencies, e.g. for asychronous calls.")
	public IDependencyInjector dependencyInjector = null;

	/**
	 * Stores the log messages to allow filtering and aggregation of the log.
	 * Otherwise we can be flooded by log messages in some cases.
	 */
	/* package */final SetMap<String, String> logMessages =
	        new SetMap<String, String>();

	/** Maps header name prefixes to actual elements. */
	/* package */final Map<String, ITokenElement> headers =
	        new HashMap<String, ITokenElement>();

	/**
	 * Maps names of function/method implementations to the elements where they
	 * are implemented.
	 */
	/* package */final Map<String, ITokenElement> functionImplementations =
	        new HashMap<String, ITokenElement>();

	/** File infos by element. */
	/* package */final Map<ITokenElement, CppDependencyBuilderFileDependencyInfo> fileInfos =
	        new IdentityHashMap<ITokenElement, CppDependencyBuilderFileDependencyInfo>();

	/**
	 * Maps a normalized target method name to the set of
	 * {@link CppDependencyBuilderFileDependencyInfo}s that contain the call.
	 */
	/* package */final SetMap<String, CppDependencyBuilderFileDependencyInfo> methodTargetCalls =
	        new SetMap<String, CppDependencyBuilderFileDependencyInfo>();

	/** The finding category. */
	private FindingCategory category;

	/** {@inheritDoc} */
	@Override
	protected void setUp(ITokenResource root) {
		NodeUtils.addToDisplayList(root, KEY);
		category =
		        NodeUtils.getFindingReport(root).getOrCreateCategory(
		                "Dependencies");
	}

	/** {@inheritDoc} */
	@Override
	protected void processElement(ITokenElement element) throws ConQATException {
		String extension =
		        UniformPathUtils.getExtension(element.getUniformPath());
		boolean isHeader =
		        extension != null && extension.toLowerCase().startsWith("h");
		if (isHeader) {
			registerHeader(element);
		}
		fileInfos.put(element, new CppDependencyBuilderFileDependencyInfo(this,
		        element));
	}

	/** Adds an element to the {@link #headers} map. */
	private void registerHeader(ITokenElement element) {
		String path = element.getUniformPath();
		if (ignoreFilenameCasing) {
			path = path.toLowerCase();
		}

		while (path != null) {
			if (headers.put(path, element) != null) {
				logMessages
				        .add("Found multiple headers with same name. This probably leads to dependency errors.",
				                path);
			}

			String[] parts = path.split(UniformPathUtils.SEPARATOR, 2);
			if (parts.length == 2) {
				path = parts[1];
			} else {
				path = null;
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void finish(ITokenResource root) {
		for (CppDependencyBuilderFileDependencyInfo fileInfo : fileInfos
		        .values()) {
			fileInfo.storeDependencies();
		}

		// perform actual logging
		for (String key : logMessages.getKeys()) {
			List<String> messages =
			        CollectionUtils.sort(logMessages.getCollection(key));
			getLogger().warn(
			        new ListStructuredLogMessage(key + "( " + messages.size()
			                + " times)", messages, CppDependencyBuilder.class
			                .getSimpleName()));
		}
	}

	/** Inserts a dependency. */
	/* package */void insertDependency(String dependencyType, String message,
	        ITokenElement source, String target,
	        TextRegionLocation sourceLocation) {
		FindingGroup group = category.getOrCreateFindingGroup(dependencyType);
		Finding finding =
		        FindingUtils.createAndAttachFinding(group, message, source,
		                sourceLocation, KEY);
		finding.setValue(EFindingKeys.DEPENDENCY_SOURCE.toString(),
		        source.getUniformPath());
		// We normalize the separators for the target, because includes may
		// potentially have inconsistent uses of '/' and '\' that leads to
		// problems in the architecture editor which can handle only a single
		// separator to understand the hierarchy.
		finding.setValue(EFindingKeys.DEPENDENCY_TARGET.toString(),
		        UniformPathUtils.normalizeAllSeparators(target));
	}

	/** Inserts a dependency. */
	// TODO (CP) Define constants for the used dependency types (maybe a
	// specific enum?)
	// (contributes to documentation on dependency types)
	// TODO (BH): Constants are declared now. Can not use enum, as dependency
	// type can also be provided by dependencyInjector
	/* package */void insertDependencies(String dependencyType, String message,
	        ITokenElement source, String target,
	        Collection<TextRegionLocation> sourceLocations) {
		for (TextRegionLocation sourceLocation : sourceLocations) {
			insertDependency(dependencyType, message, source, target,
			        sourceLocation);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected IConQATLogger getLogger() {
		// redeclared to make visible to CppDependencyBuilderFileDependencyInfo
		return super.getLogger();
	}
}
