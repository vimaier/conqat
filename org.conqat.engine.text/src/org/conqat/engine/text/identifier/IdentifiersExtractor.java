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
package org.conqat.engine.text.identifier;

import java.util.HashSet;
import java.util.Set;

import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: E661F64C5708DBB1CAEED117AD3B5617
 */
@AConQATProcessor(description = "This processor extracts the set of "
		+ "identifiers from a source code tree.")
public class IdentifiersExtractor extends IdentifierProcessorBase<Set<String>> {

	/** Set of identifiers. */
	private final HashSet<String> identifiers = new HashSet<String>();

	/** {@inheritDoc} */
	@Override
	protected Set<String> obtainResult() {
		return identifiers;
	}

	/** {@inheritDoc} */
	@Override
	protected void processIdentifier(String identifier) {
		identifiers.add(identifier);
	}
}