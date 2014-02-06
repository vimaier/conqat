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
package org.conqat.engine.dotnet.util;

import java.io.File;
import java.io.IOException;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.dotnet.resource.ContentAccessorExtractorBase;
import org.conqat.engine.dotnet.resource.ProjectContentExtractorBase;
import org.conqat.engine.resource.IContentAccessor;
import org.conqat.engine.resource.util.ContentAccessorUniformPathComparator;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.ListMap;
import org.conqat.lib.commons.collections.SetMap;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Utility functions for reading and writing mapping files as used in
 * {@link ProjectContentExtractorBase}.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 45824 $
 * @ConQAT.Rating GREEN Hash: D07C4FFC90E8E691DAFF9B2C13A31D26
 */
public class MappingFileUtils {
	/**
	 * Reads a mapping file created by {@link ContentAccessorExtractorBase} into
	 * a list map. The returned list map maps from origin element paths to a
	 * list of uniform paths of its children, e.g. the uniform path of a project
	 * to the list of uniform paths of its contained source files.
	 */
	public static ListMap<String, String> readMapping(String mappingFilePath)
			throws ConQATException {
		try {
			String[] lines = StringUtils.splitLines(FileSystemUtils
					.readFile(new File(mappingFilePath)));
			ListMap<String, String> result = new ListMap<String, String>();

			for (String line : lines) {
				String[] parts = line.split(";");
				CCSMAssert.isTrue(parts.length == 2, "Unexpected line format: "
						+ line);
				result.add(parts[0], parts[1]);
			}

			return result;
		} catch (IOException e) {
			throw new ConQATException(
					"Could not read file: " + mappingFilePath, e);
		}
	}

	/** Write scope descriptor to extracted element mapping into file. */
	public static void writeMapping(String mappingFileName,
			SetMap<String, IContentAccessor> included) throws ConQATException {
		StringBuilder result = new StringBuilder();
		for (String scopeDescriptor : CollectionUtils.sort(included.getKeys())) {
			for (IContentAccessor accessor : CollectionUtils.sort(
					included.getValues(),
					new ContentAccessorUniformPathComparator())) {
				result.append(scopeDescriptor + ";" + accessor.getUniformPath()
						+ StringUtils.CR);
			}
		}

		try {
			FileSystemUtils.writeFile(new File(mappingFileName),
					result.toString());
		} catch (IOException e) {
			throw new ConQATException("Could not write file: "
					+ mappingFileName);
		}
	}
}
