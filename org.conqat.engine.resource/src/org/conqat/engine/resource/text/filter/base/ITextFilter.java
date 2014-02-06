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
package org.conqat.engine.resource.text.filter.base;

import java.util.List;

import org.conqat.engine.core.core.ConQATException;

/**
 * Interface for filters that work on strings. The filters do not actually
 * modify the string, but rather calculate intervals that should be removed.
 * 
 * @author $Author: juergens $
 * @version $Rev: 40976 $
 * @ConQAT.Rating GREEN Hash: 1A1CBA42CF2D57FF51C871D88260B987
 */
public interface ITextFilter {

	/**
	 * Returns the deletions that the filter would perform on a given string.
	 * 
	 * @param content
	 *            String that gets filtered
	 * @param elementUniformPath
	 *            Uniform path of element that contains content. Required e.g.
	 *            to create sensible logging messages in filters.
	 */
	List<Deletion> getDeletions(String content, String elementUniformPath)
			throws ConQATException;
}