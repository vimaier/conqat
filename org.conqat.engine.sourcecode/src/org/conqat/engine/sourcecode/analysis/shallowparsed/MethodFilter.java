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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.filter.base.Deletion;
import org.conqat.engine.resource.text.filter.base.TextFilterBase;
import org.conqat.engine.sourcecode.shallowparser.ShallowParserFactory;
import org.conqat.engine.sourcecode.shallowparser.framework.EShallowEntityType;
import org.conqat.engine.sourcecode.shallowparser.framework.IShallowEntityVisitor;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntityTraversalUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.scanner.ELanguage;
import org.conqat.lib.scanner.IToken;
import org.conqat.lib.scanner.ScannerException;
import org.conqat.lib.scanner.ScannerFactory;
import org.conqat.lib.scanner.ScannerUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 46378 $
 * @ConQAT.Rating GREEN Hash: 040F0463818EFAE2411F9A0A9A7C44C2
 */
@AConQATProcessor(description = "Filters out methods whose name matches against the specified regular expressions. This filter creates filter gaps.")
public class MethodFilter extends TextFilterBase {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "method-name-patterns", attribute = "ref", description = "Methods whose names match against one of these patterns are removed.", optional = false)
	public PatternList methodNamePatterns;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "language", attribute = "name", description = "Programming language on which this filter works.", optional = false)
	public ELanguage language;

	/** {@inheritDoc} */
	@Override
	public List<Deletion> getDeletions(String rawContent,
			String elementUniformPath) throws ConQATException {
		List<IToken> tokens = createTokens(rawContent, elementUniformPath);
		List<ShallowEntity> entities = createShallowEntities(
				elementUniformPath, tokens);

		MethodFilterVisitor visitor = new MethodFilterVisitor(
				elementUniformPath);

		ShallowEntity.traverse(entities, visitor);

		return visitor.deletions;
	}

	/** Creates tokens for the raw content and logs scanner problems */
	private List<IToken> createTokens(String rawContent,
			String elementUniformPath) throws AssertionError {
		List<IToken> tokens = new ArrayList<IToken>();
		List<ScannerException> exceptions = new ArrayList<ScannerException>();
		try {
			ScannerUtils.readTokens(ScannerFactory.newScanner(language,
					rawContent, elementUniformPath), tokens, exceptions);
		} catch (IOException e) {
			throw new AssertionError(
					"We are reading from a String. An IOException should not occurr here.");
		}

		if (exceptions.size() > 0) {
			getLogger().debug(
					"Found " + exceptions.size()
							+ " scanner problems in element"
							+ elementUniformPath);
		}

		return tokens;
	}

	/** Creates ShallowEntities from the tokens and logs parser problems */
	private List<ShallowEntity> createShallowEntities(
			String elementUniformPath, List<IToken> tokens)
			throws ConQATException {
		List<ShallowEntity> entities = ShallowParserFactory.createParser(
				language).parseTopLevel(tokens);

		ShallowEntity incomplete = ShallowEntityTraversalUtils
				.findIncompleteEntity(entities);
		if (incomplete != null) {
			getLogger()
					.debug("Incompletely parsed node: "
							+ incomplete.toLocalString()
							+ " in element: "
							+ elementUniformPath
							+ " (element could contain further parsing errors).");
		}

		return entities;
	}

	/** Visitor that filters methods by name. */
	public class MethodFilterVisitor implements IShallowEntityVisitor {

		/** List of Deletions. Constructed during traversal. */
		private final List<Deletion> deletions = new ArrayList<Deletion>();

		/** Uniform path of element that gets visited */
		private final String elementUniformPath;

		/** Constructor */
		public MethodFilterVisitor(String elementUniformPath) {
			this.elementUniformPath = elementUniformPath;
		}

		/** {@inheritDoc} */
		@Override
		public boolean visit(ShallowEntity entity) {
			if (entity.getType() == EShallowEntityType.METHOD) {

				String methodName = entity.getName();
				if (!StringUtils.isEmpty(methodName)
						&& methodNamePatterns.matchesAny(methodName)) {
					filterRegionFor(entity);
					return false;
				}
			}

			return true;
		}

		/** Create a region for a filtered entity */
		private void filterRegionFor(ShallowEntity entity) {
			int startOffset = entity.getStartOffset();
			int endOffset = entity.getEndOffset();

			// log and skip incompletely parsed methods
			if (entity.getEndTokenIndex() < 0) {
				getLogger().warn(
						"Could not filter method in line "
								+ entity.getStartLine() + " in file "
								+ elementUniformPath
								+ " since it was incompletely parsed.");
				return;
			}

			// increment to convert inclusive endOffset to exclusive. not
			// required for startOffset
			Deletion deletion = new Deletion(startOffset, endOffset + 1, true);
			deletions.add(deletion);

			logDeletion(deletion, elementUniformPath);
		}

		/** {@inheritDoc} */
		@Override
		public void endVisit(ShallowEntity entity) {
			// Nothing to do
		}
	}
}
