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
package org.conqat.lib.parser.partitioner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.shallowparser.ShallowParserFactory;
import org.conqat.engine.sourcecode.shallowparser.framework.EShallowEntityType;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntityTraversalUtils.ShallowEntityVisitorBase;
import org.conqat.lib.commons.assessment.partition.IRatingPartitioner;
import org.conqat.lib.commons.assessment.partition.PartitioningException;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableList;
import org.conqat.lib.commons.region.Region;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.scanner.ELanguage;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;
import org.conqat.lib.scanner.ScannerUtils;

/**
 * A partitioner for PL/SQL based on the shallow parser framework.
 * <p>
 * This is meant to be compatible with the old (ANTLR parser based) PL/SQL
 * partitioner, hence the name and package are the same as for the old one and
 * can not be changed (the rating mechanism performs lookup by class name).
 * 
 * @author $Author: hummelb $
 * @version $Rev: 45404 $
 * @ConQAT.Rating GREEN Hash: DF97AF7799B3BE5AEC44583617B510E8
 */
public class PLSQLRatingPartitioner implements IRatingPartitioner {

	/** The types that indicate (reverse) continuation of a type. */
	private static final Set<ETokenType> CONTINUATION_TYPES = EnumSet.of(
			ETokenType.ATTRIBUTE_INDICATOR, ETokenType.DOT);

	/** {@inheritDoc} */
	@Override
	public List<Region> partition(String[] lines) throws PartitioningException {
		List<IToken> tokens = ScannerUtils.getTokens(
				StringUtils.concat(lines, "\n"), ELanguage.PLSQL);
		List<ShallowEntity> entities;
		try {
			entities = ShallowParserFactory.createParser(ELanguage.PLSQL)
					.parseTopLevel(tokens);
		} catch (ConQATException e) {
			throw new PartitioningException(
					"PLSQL support missing in parser library!");
		}

		MethodVisitor visitor = new MethodVisitor();
		ShallowEntity.traverse(entities, visitor);

		if (visitor.regions.isEmpty()) {
			return new ArrayList<Region>(Collections.singletonList(new Region(
					0, lines.length - 1, "ALL")));
		}
		return visitor.regions;
	}

	/**
	 * Extracts a description string for the given method. This consists of the
	 * method's name followed by the signature, i.e. the types of parameters in
	 * parentheses (not return type).
	 */
	private static String extractDescription(ShallowEntity method) {
		StringBuilder builder = new StringBuilder();
		builder.append(method.getName() + "(");

		if ("trigger".equalsIgnoreCase(method.getSubtype())) {
			// no parameters for triggers
			builder.append(")");
			return builder.toString();
		}

		List<IToken> tokens = method.includedTokens();
		int end = tokens.size() - 1;
		if (!method.getChildren().isEmpty()) {
			end = method.getChildren().get(0).getRelativeStartTokenIndex();
		}

		int startIndex = -1;
		int nesting = 0;

		for (int index = 0; index < end; ++index) {
			switch (tokens.get(index).getType()) {
			case LPAREN:
				if (startIndex < 0) {
					startIndex = index + 1;
				} else {
					nesting += 1;
				}
				break;
			case RPAREN:
				if (nesting > 0) {
					nesting -= 1;
					break;
				}
				// fall through intended
			case COMMA:
				if (startIndex > 0 && nesting == 0) {
					insertType(builder, tokens.subList(startIndex, index));
					startIndex = index + 1;
				}
				break;
			}
		}

		builder.append(")");
		return builder.toString();
	}

	/** Inserts a type token into the builder, adding a comma as needed. */
	private static void insertType(StringBuilder builder, List<IToken> tokens) {
		if (tokens.isEmpty()) {
			return;
		}

		if (builder.charAt(builder.length() - 1) != '(') {
			builder.append(',');
		}

		int end = tokens.size();
		for (int i = 1; i < tokens.size(); ++i) {
			ETokenType tokenType = tokens.get(i).getType();
			if (tokenType == ETokenType.DEFAULT
					|| tokenType == ETokenType.ASSIGNMENT) {
				end = i;
				break;
			}
		}

		int start = end - 1;
		while (start > 1
				&& CONTINUATION_TYPES.contains(tokens.get(start - 1).getType())) {
			start -= 2;
		}

		for (int i = start; i < end; ++i) {
			builder.append(tokens.get(i).getText());
		}
	}

	/** Visitor used to extract regions from methods. */
	private final class MethodVisitor extends ShallowEntityVisitorBase {

		/** The regions. */
		private final List<Region> regions = new ArrayList<Region>();

		/** {@inheritDoc} */
		@Override
		public boolean visit(ShallowEntity entity) {
			if (entity.getType() != EShallowEntityType.METHOD) {
				return true;
			}

			if (regions.isEmpty()) {
				// we need -1 to convert from 1-based to 0-based lines, and
				// another -1 as we use the start line of the next function
				int last = entity.getStartLine() - 2;
				if (last > 0) {
					regions.add(new Region(0, last, "Head"));
				}
			}

			// region lines are 0-based
			int endLine = entity.getEndLine() - 1;
			// correct end line in case of trailing ')'
			UnmodifiableList<IToken> includedTokens = entity.includedTokens();
			ETokenType lastTokenType = CollectionUtils.getLast(includedTokens)
					.getType();
			if (lastTokenType == ETokenType.RPAREN
					|| lastTokenType == ETokenType.COMMA) {
				endLine = Math.min(endLine,
						includedTokens.get(includedTokens.size() - 2)
								.getLineNumber());
			}

			regions.add(new Region(entity.getStartLine() - 1, endLine,
					extractDescription(entity)));
			return false;
		}
	}
}
