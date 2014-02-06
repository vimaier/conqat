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
package org.conqat.engine.code_clones.core.constraint;

import java.util.HashSet;
import java.util.Set;

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: deissenb $
 * @version $Rev: 36791 $
 * @ConQAT.Rating GREEN Hash: 22F216D6C258B67DD55E8374ECFF67D2
 */
@AConQATProcessor(description = "Constraint that is satisfied for clone classes that span a minimum number of different projects")
public class CrossProjectCloneClassConstraint extends ConstraintBase {

	/** String used to indicate that other project prefixes are matched. */
	public static final String REST = "#REST#";

	/** List of project-specific prefixes */
	private final Set<String> projectPrefixes = new HashSet<String>();

	/** Minimum number of spanned projects */
	@AConQATFieldParameter(parameter = "projects", attribute = "count", optional = true, description = "Number of required projects (default is 2)")
	public int requiredProjectsCount = 2;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "project", minOccurrences = 1, description = ""
			+ "Prefix of all uniform paths of this project (typically project root directory or package)")
	public void addProjectPathPrefix(
			@AConQATAttribute(name = "prefix", description = "Prefix of all uniform paths of this project. Use "
					+ REST
					+ " to denote paths that are not matched by any other prefixes. "
					+ "This helps to focus on clones between a component and the rest of the system.") String projectPathPrefix) {
		projectPrefixes.add(projectPathPrefix);
	}

	/**
	 * Determines the set of projects spanned by a clone class. Projects are
	 * identified by their prefix.
	 */
	private Set<String> computeSpannedProjects(CloneClass cloneClass) {
		Set<String> spannedProjects = new HashSet<String>();
		for (Clone clone : cloneClass.getClones()) {
			boolean matchesAny = false;
			for (String projectPrefix : projectPrefixes) {
				if (clone.getUniformPath().startsWith(projectPrefix)) {
					spannedProjects.add(projectPrefix);
					matchesAny = true;
				}
			}
			if (!matchesAny && projectPrefixes.contains(REST)) {
				spannedProjects.add(REST);
			}
		}
		return spannedProjects;
	}

	/** {@inheritDoc} */
	@Override
	public boolean satisfied(CloneClass cloneClass) {
		return computeSpannedProjects(cloneClass).size() >= requiredProjectsCount;
	}

}
