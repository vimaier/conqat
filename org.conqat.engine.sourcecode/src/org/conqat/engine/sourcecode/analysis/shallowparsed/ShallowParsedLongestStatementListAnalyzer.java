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
package org.conqat.engine.sourcecode.analysis.shallowparsed;

import java.util.List;
import java.util.Set;

import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.analysis.LongestStatementListAnalyzerBase;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.engine.sourcecode.shallowparser.ShallowParserFactory;
import org.conqat.engine.sourcecode.shallowparser.framework.EShallowEntityType;
import org.conqat.engine.sourcecode.shallowparser.framework.IShallowEntityVisitor;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;
import org.conqat.lib.commons.collections.CollectionUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 46378 $
 * @ConQAT.Rating GREEN Hash: 848D9F537D9FA2A4DDFC5B14022D794E
 */
@AConQATProcessor(description = "Annotates each element with the length of the longest statement list found.")
public class ShallowParsedLongestStatementListAnalyzer extends
		LongestStatementListAnalyzerBase<ITokenResource, ITokenElement> {

	/** {@inheritDoc} */
	@Override
	protected Class<ITokenElement> getElementClass() {
		return ITokenElement.class;
	}

	/** {@inheritDoc} */
	@Override
	protected void calculateStatementListLocations(ITokenElement element,
			Set<Integer> ignoredLines) {
		try {
			ShallowEntity.traverse(
					ShallowParserFactory.parse(element, getLogger()),
					new StatementListLengthVisitior(ignoredLines));
		} catch (ConQATException e) {
			getLogger().warn(
					"Ignoring element " + element.getLocation() + ": "
							+ e.getMessage());
		}
	}

	/** {@inheritDoc} */
	@Override
	protected String getFindingDescription() {
		return "<p>Long lists of statements in functions, methods, static "
				+ "initializers, or as top-level code complicate both code "
				+ "understanding and modification, as often the entire statement "
				+ "sequence has to be understood before introducing changes. Shorter "
				+ "functions and methods and help when browsing the source code, as "
				+ "their names can act as a summary of the functionality and release "
				+ "the developer from reading the low-level implementation.  </p> "
				+ "<p>The best way to reduce the length is typically the extraction "
				+ "of smaller methods. Often, inline comments already hint at good "
				+ "points for splitting a sequence of statements.  </p> ";
	}

	/** Visitor for calculating maximal statement list length. */
	private final class StatementListLengthVisitior implements
			IShallowEntityVisitor {

		/** The set of ignored lines (lines are 1-based here). */
		private final Set<Integer> ignoredLines;

		/** Constructor. */
		public StatementListLengthVisitior(Set<Integer> ignoredLines) {
			this.ignoredLines = ignoredLines;
		}

		/** {@inheritDoc} */
		@Override
		public boolean visit(ShallowEntity entity) {
			if (entity.getType() == EShallowEntityType.STATEMENT) {
				// we are not interested in sub statements, as this has been
				// handled already by the parent
				return false;
			}

			List<ShallowEntity> statements = entity
					.getChildrenOfType(EShallowEntityType.STATEMENT);
			if (statements.isEmpty()) {
				return true;
			}

			int startOffset = statements.get(0).getStartOffset();
			int endOffset = CollectionUtils.getLast(statements).getEndOffset();

			try {
				reportStatementListForOffsets(startOffset, endOffset,
						ignoredLines);
			} catch (ConQATException e) {
				getLogger().error(
						"Offset conversion failed: " + e.getMessage(), e);
			}
			return true;
		}

		/** {@inheritDoc} */
		@Override
		public void endVisit(ShallowEntity entity) {
			// not needed
		}
	}
}