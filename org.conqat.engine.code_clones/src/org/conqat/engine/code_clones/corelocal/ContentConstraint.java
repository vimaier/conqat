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
package org.conqat.engine.code_clones.corelocal;

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.code_clones.core.constraint.ConstraintBase;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;

/**
 * {@ConQAT.Doc}
 * 
 * @author juergens
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: 62DD58BC48A375833F58F0F31B5B2261
 */
@AConQATProcessor(description = "Constraint that is satisfied if at least one pattern matches in at least one clone.")
public class ContentConstraint extends ConstraintBase {

	/** Patterns against which clones are matched */
	protected PatternList patterns;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "patterns", description = "Patterns against which cloned content is matched.", minOccurrences = 1, maxOccurrences = 1)
	public void setPatterns(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) PatternList patterns) {
		this.patterns = patterns;
	}

	/** {@inheritDoc} */
	@Override
	public boolean satisfied(CloneClass cloneClass) throws ConQATException {
		for (Clone clone : cloneClass.getClones()) {
			String code = CloneUtils.getCloneContentFromLocalFileSystem(clone);
			if (patterns.findsAnyIn(code)) {
				return true;
			}
		}

		return false;
	}

}