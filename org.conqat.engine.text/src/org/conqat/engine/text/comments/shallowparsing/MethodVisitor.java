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

package org.conqat.engine.text.comments.shallowparsing;

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.sourcecode.analysis.shallowparsed.ShallowParsedMetricAnalyzerBase;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.shallowparser.framework.EShallowEntityType;
import org.conqat.engine.sourcecode.shallowparser.framework.IShallowEntityVisitor;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;
import org.conqat.lib.scanner.IToken;

/**
 * Extracts all methods and fields during parsing
 * 
 * @author $Author: hummelb $
 * @version $Rev: 46378 $
 * @ConQAT.Rating YELLOW Hash: A6FA2E6AC34F3ED91D6BD6AB2199485D
 */

public class MethodVisitor extends ShallowParsedMetricAnalyzerBase {

	/**
	 * Methods found during parsing.
	 */
	private List<Method> methods = new ArrayList<Method>();

	/**
	 * Names of fields found during parsing.
	 */
	private List<String> fields = new ArrayList<String>();

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "", type = "java.lang.Number")
	public static final String KEY = "methods";

	/** {@inheritDoc} */
	@Override
	protected String getKey() {
		return KEY;
	}

	/**
	 * returns a list of methods
	 */
	public List<Method> getMethods() {
		return methods;
	}

	/**
	 * returns a list of fields
	 */
	public List<String> getFields() {
		return fields;
	}

	/** {@inheritDoc} */
	@Override
	public void calculateMetrics(ITokenElement element,
			List<ShallowEntity> entities) {
		ShallowEntity.traverse(entities, new MemberExtractor());
	}

	/** Visitor for calculating maximal nesting depth. */
	private final class MemberExtractor implements IShallowEntityVisitor {

		/** {@inheritDoc} */
		@Override
		public boolean visit(ShallowEntity entity) {
			if (entity.getType() == EShallowEntityType.ATTRIBUTE) {
				fields.add(entity.getName());

			} else if (entity.getType() == EShallowEntityType.METHOD) {
				IToken startToken = entity.includedTokens().get(0);
				IToken endToken = entity.includedTokens().get(
						entity.includedTokens().size() - 1);
				Method method = new Method(entity.getName(), startToken,
						endToken);
				methods.add(method);
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
}