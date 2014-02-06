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
package org.conqat.engine.sourcecode.analysis.shallowparsed;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.TextElementUtils;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.shallowparser.framework.EShallowEntityType;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntityTraversalUtils.ShallowEntityVisitorBase;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 47155 $
 * @ConQAT.Rating GREEN Hash: 518B6ACD0858440E4A60886ADE87C72D
 */
@AConQATProcessor(description = "Created findings for variables that have the same name as a variable in an outer scope, i.e. shadow this value. "
		+ "This does not handle shadowing of attributes by variables (can be separated using this) or shadowing of attributes in a super class.")
public class VariableShadowingAnalyzer extends ShallowParsedFindingAnalyzerBase {

	/** {@inheritDoc} */
	@Override
	protected void analyzeShallowEntities(ITokenElement element,
			List<ShallowEntity> entities) {
		new ShadowingVisitor(element).apply(entities);
	}

	/** {@inheritDoc} */
	@Override
	protected String getFindingGroupName() {
		return "Variable shadowing";
	}

	/** {@inheritDoc} */
	@Override
	protected String getFindingCategoryName() {
		return "Language features";
	}

	/** Visitor for checking name shadowing. */
	private class ShadowingVisitor extends ShallowEntityVisitorBase {

		/** The element processed. */
		private final ITokenElement element;

		/**
		 * Stores variable sets for outer scopes. Each scope is described by a
		 * map from names of variables in the current scope to their earliest
		 * declaration.
		 */
		private final Stack<Map<String, Integer>> scopes = new Stack<Map<String, Integer>>();

		/** Constructor. */
		public ShadowingVisitor(ITokenElement element) {
			this.element = element;
		}

		/** Applies this visitor to the given entities. */
		public void apply(List<ShallowEntity> entities) {
			scopes.push(new HashMap<String, Integer>());
			try {
				constructScope(null, entities);
			} catch (ConQATException e) {
				getLogger().error(e.getMessage(), e);
			}
			ShallowEntity.traverse(entities, this);
		}

		/** {@inheritDoc} */
		@Override
		public boolean visit(ShallowEntity entity) {
			try {
				constructScope(entity, entity.getChildren());
			} catch (ConQATException e) {
				getLogger().error(e.getMessage(), e);
			}
			return true;
		}

		/**
		 * Constructs the current scope for the entities based on the top of the
		 * {@link #scopes} stack.
		 */
		private void constructScope(ShallowEntity parent,
				List<ShallowEntity> entities) throws ConQATException {
			Map<String, Integer> currentScope = new HashMap<String, Integer>(
					scopes.peek());

			if (parent != null) {
				handleEntityLocalScope(parent, currentScope);
			}

			for (ShallowEntity entity : entities) {
				if (ShallowParsingUtils.isGlobalVariable(entity)
						|| ShallowParsingUtils.isLocalVariable(entity)) {
					collectVariables(
							ShallowParsingUtils.extractVariableNameTokens(entity
									.ownStartTokens()), currentScope);
				}
			}

			scopes.push(currentScope);
		}

		/**
		 * Handles with scopes that are specific to the given entity. This
		 * includes parameter names for a method and the initialization area for
		 * for-loops.
		 */
		private void handleEntityLocalScope(ShallowEntity parent,
				Map<String, Integer> currentScope) throws ConQATException {
			// deal with variables declared in for loop
			if (parent.getType() == EShallowEntityType.STATEMENT
					&& parent.getSubtype().equalsIgnoreCase(
							ETokenType.FOR.name())) {
				collectVariables(
						ShallowParsingUtils
								.extractVariablesDeclaredInFor(parent),
						currentScope);
			}

			// deal with method parameters
			if (parent.getType() == EShallowEntityType.METHOD) {
				collectVariables(
						ShallowParsingUtils.extractParameterNameTokens(parent),
						currentScope);
			}
		}

		/**
		 * Collects the variables from the given tokens corresponding to
		 * variable names. If a name already exists in the given current scope,
		 * it gets reported as finding. Otherwise, the name is added to the
		 * scope.
		 */
		private void collectVariables(List<IToken> variableNameToken,
				Map<String, Integer> currentScope) throws ConQATException {
			for (IToken token : variableNameToken) {
				String variableName = token.getText();
				// if the variable already was known at an earlier point, this
				// is shadowing
				if (currentScope.containsKey(variableName)
						&& currentScope.get(variableName) < token.getOffset()) {
					int line = TextElementUtils
							.convertFilteredOffsetToUnfilteredLine(element,
									currentScope.get(variableName));
					String message = "Variable " + variableName
							+ " hides a variable of same name in same "
							+ "or outer scope declared at line " + line + "!";
					createFindingForFilteredOffsets(message, element,
							token.getOffset(), token.getEndOffset());
				} else {
					currentScope.put(variableName, token.getOffset());
				}
			}
		}

		/** {@inheritDoc} */
		@Override
		public void endVisit(ShallowEntity entity) {
			scopes.pop();
		}
	}
}
