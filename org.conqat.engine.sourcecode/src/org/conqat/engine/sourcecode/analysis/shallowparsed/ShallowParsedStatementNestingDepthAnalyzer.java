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
import java.util.Stack;

import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.util.ResourceUtils;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.shallowparser.framework.EShallowEntityType;
import org.conqat.engine.sourcecode.shallowparser.framework.IShallowEntityVisitor;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.IdentityHashSet;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 46378 $
 * @ConQAT.Rating GREEN Hash: 75E710B5BEC2D5BAED623869F5CB5306
 */
@AConQATProcessor(description = "Annotates each element with the deepest statement nesting found.")
public class ShallowParsedStatementNestingDepthAnalyzer extends
		ShallowParsedMetricAnalyzerBase {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "The depth of the most deeply nested region.", type = "java.lang.Number")
	public static final String KEY = "Nesting Depth";

	/** {@inheritDoc} */
	@Override
	protected String getKey() {
		return KEY;
	}

	/** {@inheritDoc} */
	@Override
	protected void calculateMetrics(ITokenElement element,
			List<ShallowEntity> entities) {
		ShallowEntity.traverse(entities, new MaximalNestingDepthVisitior());
	}

	/** Visitor for calculating maximal nesting depth. */
	private final class MaximalNestingDepthVisitior implements
			IShallowEntityVisitor {

		/** {@inheritDoc} */
		@Override
		public boolean visit(ShallowEntity entity) {
			if (entity.getType() == EShallowEntityType.STATEMENT) {
				entity.traverse(new NestingDepthVisitor());
				return false;
			}
			return true;
		}

		/** {@inheritDoc} */
		@Override
		public void endVisit(ShallowEntity entity) {
			// not needed
		}
	}

	/** {@inheritDoc} */
	@Override
	protected String getFindingDescription() {
		return "<p>Deep nesting of control structures, such as conditionals and "
				+ "loops, complicate the understanding of the context and conditions holding at "
				+ "the deeply nested statements. Often the end of each nested "
				+ "construct is not easy to spot. </p> "
				+ "<p> To resolve deep nesting, you can extract methods where the "
				+ "context is described in the method name and comment. Often deep "
				+ "nesting can also be reduced by merging conditional statements or "
				+ "by rephrasing positive conditionals to negative ones in "
				+ "combination with a return statement (early exit).</p> ";
	}

	/**
	 * Visitor for calculating maximal nesting depth. The algorithm works as
	 * follows:
	 * <ul>
	 * <li>Values are reported for the first "simple" statement within a block
	 * statement (while, if, etc.)</li>
	 * <li>We perform the reporting during backtracking, so we get called for
	 * the deepest nodes first.</li>
	 * <li>After reporting, all surrounding blocks are marked as already
	 * reported; by checking for reported blocks prior to reporting, we can
	 * avoid reporting the nesting also for the surrounding blocks.</li>
	 * <li>To make this possible, we manage the stack of blocks/scopes we are
	 * currently in; this stack is also used to determine the actual nesting.</li>
	 * </ul>
	 */
	private final class NestingDepthVisitor implements IShallowEntityVisitor {

		/** Current stack of open scopes. */
		private final Stack<ShallowEntity> scopes = new Stack<ShallowEntity>();

		/** The scopes within which we already reported nesting. */
		private final Set<ShallowEntity> reportedScopes = new IdentityHashSet<ShallowEntity>();

		/** Stores the first statement within each scope. */
		private ShallowEntity firstStatement;

		/** {@inheritDoc} */
		@Override
		public boolean visit(ShallowEntity entity) {
			if (entity.getType() != EShallowEntityType.STATEMENT) {
				return false;
			}

			if (!entity.getChildren().isEmpty()) {
				scopes.push(entity);
				firstStatement = null;
			} else if (firstStatement == null) {
				firstStatement = entity;
			}
			return true;
		}

		/** {@inheritDoc} */
		@Override
		public void endVisit(ShallowEntity entity) {
			if (scopes.isEmpty()) {
				return;
			}

			if (scopes.peek() == entity) {
				if (!reportedScopes.contains(entity) && firstStatement != null) {
					int startLine = firstStatement.getStartLine();
					ShallowEntity lastSibling = firstStatement;
					// in some extreme configurations, top-level entities might
					// be deeptly nested
					if (firstStatement.getParent() != null) {
						lastSibling = CollectionUtils.getLast(firstStatement
								.getParent().getChildren());
					}
					int endLine = lastSibling.getEndLine();

					try {
						reportMetricValue(
								scopes.size(),
								ResourceUtils
										.createTextRegionLocationForFilteredLines(
												currentElement, startLine,
												endLine));
						reportedScopes.addAll(scopes);
					} catch (ConQATException e) {
						getLogger().error(
								"Offset conversion failed: " + e.getMessage(),
								e);
					}
				}

				scopes.pop();
			}
		}
	}
}