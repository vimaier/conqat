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
package org.conqat.engine.code_clones.result;

import java.util.Collections;
import java.util.List;

import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.code_clones.core.utils.CloneUtils;
import org.conqat.engine.code_clones.core.utils.ECloneClassComparator;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Base class for processors that create clone lists
 * 
 * @author juergens
 * @author $Author: juergens $
 * @version $Rev: 43751 $
 * @ConQAT.Rating GREEN Hash: 2269967303ED92519ACB9B9CF0DECDD1
 */
public abstract class CloneListBuilderBase extends ConQATProcessorBase {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Key used to store length of clone in lines", type = "java.lang.Integer")
	public static final String CLONE_LENGTH_IN_LINES = "Length in Lines";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Key used to store start line of clone", type = "java.lang.Integer")
	public static final String CLONE_START_LINE = "Line";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Key used to store number of clones in clone class", type = "java.lang.Integer")
	public static final String VOLUME = "Volume";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Key used to store number of clones in clone class", type = "java.lang.Integer")
	public static final String CARDINALITY = "#Instances";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Key used to store normalized length of clones in clone class", type = "java.lang.Integer")
	public static final String NORMALIZED_LENGTH = "Normalized length";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Key used to store the number of gaps in the clone", type = "java.lang.Integer")
	public static final String GAP_COUNT = "#Gaps";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Key used to store the covered flag", type = "java.lang.Boolean")
	public static final String COVERED = "Covered";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Key used to store birth of clone", type = "java.lang.Date")
	public static final String BIRTH = "Birth";

	/** Number that determines after how many clones the result is truncated. */
	private int maxCloneCount = CloneUtils.UNLIMITED;

	/** Comparator used to sort clone classes */
	private ECloneClassComparator comparator = ECloneClassComparator.NORMALIZED_LENGTH;

	/** ConQAT Parameter */
	@AConQATParameter(name = "sort", description = "Metric according to which clone classes are sorted", minOccurrences = 0, maxOccurrences = 1)
	public void setComparator(
			@AConQATAttribute(name = "dimension", description = "Valid dimensions are NORMALIZED_LENGTH, VOLUME and CARDINALITY, default value is NORMALIZED_LENGTH") ECloneClassComparator comparator) {
		this.comparator = comparator;
	}

	/** ConQAT Parameter */
	@AConQATParameter(name = "max", description = "Maximal number of clones that gets included. All clones exceeding this number are truncated", minOccurrences = 0, maxOccurrences = 1)
	public void setMaxCloneCount(
			@AConQATAttribute(name = "clones", description = "Use "
					+ CloneUtils.UNLIMITED
					+ " to include all clones. Default is "
					+ CloneUtils.UNLIMITED + ".") int maxCloneCount) {
		this.maxCloneCount = maxCloneCount;
	}

	/** {@inheritDoc} */
	@Override
	public DetectionResultRootNode process() {
		// sort clone classes
		List<CloneClass> allCloneClasses = getCloneClasses();
		Collections.sort(allCloneClasses, comparator);

		// truncate list after maxCloneCount
		List<CloneClass> keptCloneClasses = CloneUtils
				.cloneClassesForFirstNClones(allCloneClasses, maxCloneCount);

		// create root
		DetectionResultRootNode root = new DetectionResultRootNode(comparator);
		String rootPath = getRootPath();

		// add clone classes
		for (CloneClass cloneClass : keptCloneClasses) {
			root.addChild(new CloneClassNode(cloneClass, rootPath));
		}

		return root;
	}

	/** Return root path that is to be cut off from clones */
	protected String getRootPath() {
		return StringUtils.EMPTY_STRING;
	}

	/** Return list of clone classes */
	protected abstract List<CloneClass> getCloneClasses();

}