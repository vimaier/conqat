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
package org.conqat.engine.html_presentation.portfolio;

import java.io.File;
import java.io.IOException;

import org.conqat.lib.commons.filesystem.FileSystemUtils;

/**
 * Retrieves JSON data files from the local filesystem.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 39890 $
 * @ConQAT.Rating GREEN Hash: F3679BC67274DA23282FE02A6BEF8D18
 */
public class PortfolioFileDataFetcher extends PortfolioDataFetcherBase {

	/** Constructor. */
	public PortfolioFileDataFetcher(String name, String path) {
		super(name, path);
	}

	/** {@inheritDoc} */
	@Override
	protected String getJsonString(String dataJsonFileLocation)
			throws IOException {
		File jsonDataFile = new File(dataJsonFileLocation);
		return FileSystemUtils.readFileUTF8(jsonDataFile);
	}
}
