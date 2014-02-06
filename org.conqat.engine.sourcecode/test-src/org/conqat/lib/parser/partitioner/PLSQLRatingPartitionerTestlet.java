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
package org.conqat.lib.parser.partitioner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.conqat.lib.commons.assessment.partition.PartitioningException;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.region.Region;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.commons.test.TestletBase;
import org.junit.Ignore;

/**
 * Testlet for {@link PLSQLRatingPartitionerTest}.
 * 
 * @author $Author: goede $
 * @version $Rev: 40495 $
 * @ConQAT.Rating GREEN Hash: F9ABD7BBA756A26051B351C6CD636960
 */
@Ignore
public class PLSQLRatingPartitionerTestlet extends TestletBase {

	/** File under test. */
	private final File codeFile;

	/** File used as reference. */
	private final File refFile;

	/** Constructor. */
	public PLSQLRatingPartitionerTestlet(File codeFile, File refFile) {
		this.codeFile = codeFile;
		this.refFile = refFile;
	}

	/** Parse whole file and check against reference. */
	@Override
	public void test() throws IOException, PartitioningException {
		List<Region> partitions = new PLSQLRatingPartitioner()
				.partition(StringUtils.splitLines(FileSystemUtils
						.readFileUTF8(codeFile)));

		List<String> partitionStrings = new ArrayList<String>();
		for (Region region : partitions) {
			partitionStrings.add(region.getOrigin() + ": " + region.getStart()
					+ "-" + region.getEnd());
		}
		String actual = StringUtils.concat(partitionStrings, StringUtils.CR);
		String expected = StringUtils.normalizeLineBreaks(FileSystemUtils
				.readFileUTF8(refFile));
		assertEquals(expected, actual);
	}

	/** Name of the test case is the name of the smoke test file. */
	@Override
	public String getName() {
		return codeFile.getPath();
	}
}