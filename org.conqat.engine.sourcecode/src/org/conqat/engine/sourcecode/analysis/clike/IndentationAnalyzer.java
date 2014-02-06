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
package org.conqat.engine.sourcecode.analysis.clike;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.TextElementUtils;
import org.conqat.engine.sourcecode.analysis.shallowparsed.ShallowParsedFindingAnalyzerBase;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;

/**
 * {@ConQATDoc}
 * 
 * @author $Author: goede $
 * @version $Rev: 41726 $
 * @ConQAT.Rating GREEN Hash: FCDD34BE04BBA20B67A600D433065AD3
 */
@AConQATProcessor(description = "Analyzes source code for correct indentation.")
public class IndentationAnalyzer extends ShallowParsedFindingAnalyzerBase {

	/** @{ConQATDoc */
	@AConQATFieldParameter(parameter = "indentation", description = "The string used for indentation. Default is one tab (\\t).", attribute = "string", optional = true)
	public String indentation = "\t";

	/** {@inheritDoc} */
	@Override
	protected void analyzeShallowEntities(ITokenElement element,
			List<ShallowEntity> entities) throws ConQATException {
		String[] lines = TextElementUtils.getLines(element);
		Set<Integer> findingLines = new HashSet<Integer>();
		for (ShallowEntity entity : entities) {
			analyzeShallowEntity(element, lines, entity, 0, findingLines);
		}
	}

	/** Analyzes a single statement for rule violation. */
	private void analyzeShallowEntity(ITokenElement element, String[] lines,
			ShallowEntity entity, int depth, Set<Integer> findingLines)
			throws ConQATException {
		for (int i = entity.getStartLine(); i <= entity.getEndLine(); i++) {
			if (lineViolatesIndentation(lines[i - 1], depth)
					&& findingLines.add(i)) {
				createFindingForFilteredLine("Incorrect indentation", element,
						i);
			}
		}

		for (ShallowEntity child : entity.getChildren()) {
			analyzeShallowEntity(element, lines, child, depth + 1, findingLines);
		}
	}

	/**
	 * Returns <code>true</code> if the given line does not start with
	 * <code>depth * indentation</code>.
	 */
	private boolean lineViolatesIndentation(String line, int depth) {
		for (int i = 0; i < depth; i++) {
			if (!line.startsWith(indentation, i * indentation.length())) {
				return true;
			}
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	protected String getFindingGroupName() {
		return "Incorrect indentation";
	}

	/** {@inheritDoc} */
	@Override
	protected String getFindingCategoryName() {
		return "Formatting";
	}
}
