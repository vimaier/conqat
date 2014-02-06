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
package org.conqat.engine.commons.string;

import org.conqat.engine.commons.util.ConQATInputProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 42175 $
 * @ConQAT.Rating GREEN Hash: A5EACB91676A3FE150545B380C845BA1
 */
@AConQATProcessor(description = "Splits a string and returns a part of this string. This uses Java's String.split() method and thus has the same behavior.")
public class StringSplitter extends ConQATInputProcessorBase<String> {

	/** Regex used for splitting. */
	private String splitRegex;

	/** Split limit. */
	private int splitLimit;

	/** Index of the returned part. */
	private int index;

	/** {ConQAT.Doc} */
	@AConQATParameter(name = "split", minOccurrences = 1, maxOccurrences = 1, description = "Sets the parameters used for splitting.")
	public void setSplitParameters(
			@AConQATAttribute(name = "regex", description = "The regular expression used for splitting.") String regex,
			@AConQATAttribute(name = "index", description = "The index of the part to be returned. Use negative indexes to access parts from last to first. Index -1 returns the last part, -2 the second to last part, and so on.") int index,
			/*
			 * Note: the description of the last parameter is copied from the
			 * JavaDoc of String.split()
			 */
			@AConQATAttribute(name = "limit", defaultValue = "0", description = "If the limit n is greater than zero then the pattern will be applied at most n - 1 times, the array's length will be no greater than n, and the array's last entry will contain all input beyond the last matched delimiter. If n is non-positive then the pattern will be applied as many times as possible and the array can have any length. If n is zero then the pattern will be applied as many times as possible, the array can have any length, and trailing empty strings will be discarded.") int splitLimit) {

		splitRegex = regex;
		this.index = index;
		this.splitLimit = splitLimit;
	}

	/** {@inheritDoc} */
	@Override
	public String process() throws ConQATException {
		String[] parts = input.split(splitRegex, splitLimit);

		if (parts.length <= index || Math.abs(index) > parts.length) {
			throw new ConQATException("Splitting only created " + parts.length
					+ " parts, thus index " + index + " is not available.");
		}

		if (index < 0) {
			return parts[parts.length + index];
		}

		return parts[index];
	}
}
