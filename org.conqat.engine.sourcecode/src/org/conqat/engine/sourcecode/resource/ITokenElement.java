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
package org.conqat.engine.sourcecode.resource;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.IConQATLogger;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.lib.commons.collections.UnmodifiableList;
import org.conqat.lib.scanner.ELanguage;
import org.conqat.lib.scanner.IToken;

/**
 * This interface describes elements that provide tokens.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 725CB123625CEEF0EBCBD12A3AC23300
 */
public interface ITokenElement extends ITokenResource, ITextElement {

	/** Get language of this element. */
	ELanguage getLanguage();

	/**
	 * Get tokens. This uses the scanner specified via the language to tokenize
	 * the text content. If the scanner encounters errors they are logged via
	 * the provided logger (warning level). If the number of error tokens
	 * exceeds a threshold, an error is logged and an empty list returned.
	 * <p>
	 * This works on the result of {@link #getTextContent()} (and <b>not</b>
	 * {@link #getUnfilteredTextContent()}).
	 * 
	 * @throws ConQATException
	 *             in case of I/O problems.
	 */
	UnmodifiableList<IToken> getTokens(IConQATLogger logger)
			throws ConQATException;
}