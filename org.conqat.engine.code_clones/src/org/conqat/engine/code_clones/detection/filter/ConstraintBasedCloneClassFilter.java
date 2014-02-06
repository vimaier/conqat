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
package org.conqat.engine.code_clones.detection.filter;

import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.code_clones.core.constraint.ConstraintList;
import org.conqat.engine.code_clones.core.constraint.ICloneClassConstraint;
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
 * @ConQAT.Rating GREEN Hash: E5E385416EC860106FA3E1EAC758D878
 */
@AConQATProcessor(description = ""
		+ "Filters out clone classes based on clone class constraints. If you intend to "
		+ "apply this filter on the result of a detection processor, consider to add the "
		+ "constraints directly to the detector, since the clone classes are then "
		+ "filtered out immediately and do not unnecessarily consume memory. However, "
		+ "this processor can still be useful to filter results read from a clone "
		+ "report.")
public class ConstraintBasedCloneClassFilter extends CloneClassFilterBase {

	/** List of constraints */
	private final ConstraintList constraints = new ConstraintList();

	/** If this flag is set, filter is inverted */
	private boolean invert = false;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "constraint", minOccurrences = 1, maxOccurrences = -1, description = ""
			+ "Adds a constraint that each detected clone class must satisfy")
	public void addConstraint(
			@AConQATAttribute(name = "type", description = "Clone classes that do not match the constraint are filtered") ICloneClassConstraint constraint) {
		constraints.add(constraint);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "invert", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "If this flag is set, filter is inverted")
	public void setInvert(
			@AConQATAttribute(name = "value", description = "Default value is false") boolean invert) {
		this.invert = invert;
	}

	/** {@inheritDoc} */
	@Override
	protected boolean filteredOut(CloneClass cloneClass) throws ConQATException {
		boolean filteredOut = !constraints.allSatisfied(cloneClass);
		if (invert) {
			filteredOut = !filteredOut;
		}
		return filteredOut;
	}

}