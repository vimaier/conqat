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
package org.conqat.lib.commons.io;

import java.io.InputStream;

/**
 * A thread to drain an input stream. Read content is discarded.
 * 
 * @deprecated Use {@link StreamReaderThread} with storeContent=false instead.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: EDD6FA05A47808D637179C6B2CFA1315
 */
@Deprecated
public class StreamDrainerThread extends StreamReaderThread {

	/**
	 * Create a new reader that immediately starts to drain the content of this
	 * stream. This call is non-blocking.
	 * 
	 * @param input
	 *            Stream to read from. This stream is not automatically closed,
	 *            but must be closed by the caller (if this is intended).
	 */
	public StreamDrainerThread(InputStream input) {
		super(input, false);
	}
}