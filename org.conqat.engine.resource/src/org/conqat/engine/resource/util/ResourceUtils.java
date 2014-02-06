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
package org.conqat.engine.resource.util;

import java.io.FileOutputStream;
import java.io.IOException;

import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.findings.FindingGroup;
import org.conqat.engine.commons.findings.location.TextRegionLocation;
import org.conqat.engine.commons.findings.util.FindingUtils;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.core.IConQATProcessorInfo;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.filesystem.CanonicalFile;
import org.conqat.lib.commons.filesystem.FileSystemUtils;

/**
 * Utility methods for using the resource framework.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating GREEN Hash: 0ECFA2B17AFB53536724B923A9F43AF1
 */
public class ResourceUtils {

	/**
	 * Returns a file for the given element. If the element already resides on
	 * disk, the element is returned. Otherwise, a temporary file with the
	 * content is created and returned.
	 * 
	 * @throws ConQATException
	 *             is the content of the element could not be accessed or the
	 *             temporary file could not be created
	 */
	public static CanonicalFile ensureFile(IElement element,
			IConQATProcessorInfo processorInfo) throws ConQATException {
		CanonicalFile file = getFileFromLocation(element.getLocation());
		if (file == null) {
			file = processorInfo.getTempFile("TEMP",
					UniformPathUtils.getElementName(element.getUniformPath()));
			FileOutputStream out = null;
			try {
				out = new FileOutputStream(file);
				out.write(element.getContent());
			} catch (IOException e) {
				throw new ConQATException("Could not write temporary file "
						+ file, e);
			} finally {
				FileSystemUtils.close(out);
			}
		}
		return file;
	}

	/**
	 * If the given element corresponds to a file on disk, the file is returned.
	 * Otherwise null is returned. To determine the file, the method tries to
	 * convert the location to a file and check whether it is canonizable and
	 * exists.
	 */
	public static CanonicalFile getFile(IElement element) {
		return getFileFromLocation(element.getLocation());
	}

	/**
	 * If the given location corresponds to a file on disk, the file is
	 * returned. Otherwise null is returned. To determine the file, the method
	 * tries to convert the location to a file and check whether it is
	 * canonizable and exists.
	 */
	public static CanonicalFile getFileFromLocation(String location) {
		CanonicalFile file;
		try {
			file = new CanonicalFile(location);
		} catch (IOException e) {
			return null;
		}

		if (file.isReadableFile()) {
			return file;
		}
		return null;
	}

	/**
	 * Create a finding for a (filtered) line and attach it to an element.
	 * 
	 * @param group
	 *            the finding group the finding belongs to
	 * @param filteredLineNumber
	 *            the line number of the finding (one based, inclusive,
	 *            filtered)
	 * @param key
	 *            the key used to store the finding
	 */
	public static Finding createAndAttachFindingForFilteredLine(
			FindingGroup group, String message, ITextElement element,
			int filteredLineNumber, String key) throws ConQATException {
		return createAndAttachFindingForFilteredLineRegion(group, message,
				element, filteredLineNumber, filteredLineNumber, key);
	}

	/**
	 * Creates a {@link TextRegionLocation} from filtered start and end lines
	 * (both inclusive and one based).
	 */
	public static TextRegionLocation createTextRegionLocationForFilteredLines(
			ITextElement element, int filteredStartLineNumber,
			int filteredEndLineNumber) throws ConQATException {
		CCSMAssert.isTrue(filteredStartLineNumber <= filteredEndLineNumber,
				"Start line may not be after end line.");

		int filteredStartOffset = element
				.convertFilteredLineToOffset(filteredStartLineNumber);

		// our line/offset converter supports also the
		// one-after-the-last line
		int filteredEndOffset = element
				.convertFilteredLineToOffset(filteredEndLineNumber + 1) - 1;

		return createTextRegionLocationForFilteredOffsets(element,
				filteredStartOffset, filteredEndOffset);
	}

	/**
	 * Creates a {@link TextRegionLocation} from filtered start and end offsets
	 * (both inclusive and zero based).
	 */
	public static TextRegionLocation createTextRegionLocationForFilteredOffsets(
			ITextElement element, int filteredStartOffset, int filteredEndOffset)
			throws ConQATException {
		CCSMAssert.isTrue(filteredStartOffset <= filteredEndOffset,
				"Start offset may not be after end offset.");

		int unfilteredStartOffset = element
				.getUnfilteredOffset(filteredStartOffset);
		int unfilteredEndOffset = element
				.getUnfilteredOffset(filteredEndOffset);

		return new TextRegionLocation(element.getLocation(),
				element.getUniformPath(), unfilteredStartOffset,
				unfilteredEndOffset,
				element.convertUnfilteredOffsetToLine(unfilteredStartOffset),
				element.convertUnfilteredOffsetToLine(unfilteredEndOffset));
	}

	/**
	 * Create a finding for a region of (filtered) lines and attach it to an
	 * element.
	 * 
	 * @param group
	 *            the finding group the finding belongs to
	 * @param filteredStartLineNumber
	 *            the line number in which the finding starts (one based,
	 *            inclusive, filtered)
	 * @param filteredEndLineNumber
	 *            the line number in which the finding ends (one based,
	 *            inclusive, filtered).
	 * @param key
	 *            the key used to store the finding
	 */
	public static Finding createAndAttachFindingForFilteredLineRegion(
			FindingGroup group, String message, ITextElement element,
			int filteredStartLineNumber, int filteredEndLineNumber, String key)
			throws ConQATException {
		TextRegionLocation location = createTextRegionLocationForFilteredLines(
				element, filteredStartLineNumber, filteredEndLineNumber);
		return FindingUtils.createAndAttachFinding(group, message, element,
				location, key);
	}

	/**
	 * Create a finding based on filtered offsets and attach it to an element.
	 * 
	 * @param group
	 *            the finding group the finding belongs to
	 * @param filteredStartOffset
	 *            the offset in which the finding starts (relative to the
	 *            beginning of the file, 0-based, filtered)
	 * @param filteredEndOffset
	 *            the offset in which the finding ends (relative to the
	 *            beginning of the file, 0-based, filtered)
	 * @param key
	 *            the key used to store the finding
	 */
	public static Finding createAndAttachFindingForFilteredRegion(
			FindingGroup group, String message, ITextElement element,
			int filteredStartOffset, int filteredEndOffset, String key)
			throws ConQATException {
		TextRegionLocation location = createTextRegionLocationForFilteredOffsets(
				element, filteredStartOffset, filteredEndOffset);
		return FindingUtils.createAndAttachFinding(group, message, element,
				location, key);
	}
}
