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
package org.conqat.engine.sourcecode.shallowparser.framework;

import static org.conqat.lib.scanner.ETokenType.IDENTIFIER;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.conqat.engine.sourcecode.shallowparser.framework.SequenceRecognizer.ITokenMatcher;
import org.conqat.lib.scanner.IToken;

/**
 * Matcher for identifiers by name.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 44176 $
 * @ConQAT.Rating GREEN Hash: 3B8D92380E755861424218F6DF894074
 */
public class ExactIdentifierMatcher implements ITokenMatcher {

	/** The matched identifiers. */
	private final Set<String> identifiers = new HashSet<String>();

	/** Constructor. */
	public ExactIdentifierMatcher(String... identifiers) {
		this.identifiers.addAll(Arrays.asList(identifiers));
	}

	/** {@inheritDoc} */
	@Override
	public boolean matches(IToken token) {
		return token.getType() == IDENTIFIER
				&& identifiers.contains(token.getText());
	}

}