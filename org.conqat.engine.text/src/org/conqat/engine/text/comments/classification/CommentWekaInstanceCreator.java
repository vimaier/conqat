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

package org.conqat.engine.text.comments.classification;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.conqat.engine.commons.machine_learning.BooleanFeatureBase;
import org.conqat.engine.commons.machine_learning.IFeature;
import org.conqat.engine.commons.machine_learning.InstanceCreatorBase;
import org.conqat.engine.commons.machine_learning.NamedFeatureBase;
import org.conqat.engine.sourcecode.shallowparser.TokenStreamUtils;
import org.conqat.engine.text.comments.Comment;
import org.conqat.engine.text.comments.ECommentCategory;
import org.conqat.engine.text.comments.classification.features.CodeRecognizer;
import org.conqat.engine.text.comments.classification.features.CoherenceUtils;
import org.conqat.engine.text.comments.utils.CommentUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.ETokenType.ETokenClass;
import org.conqat.lib.scanner.IToken;

/**
 * Creates a weka instance for a given comment.
 * 
 * @author $Author: steidl $
 * @version $Rev: 46304 $
 * @ConQAT.Rating GREEN Hash: 479AF38802BA98F89B12E9FFA07A45BC
 */
public class CommentWekaInstanceCreator extends
		InstanceCreatorBase<Comment, ECommentCategory> {

	/** Pattern used for identifying task tags. */
	private static final Pattern TASK_TAG_PATTERN = Pattern.compile(
			"\\b(todo|fixme|hack)\\b", Pattern.CASE_INSENSITIVE);

	/** Set of token types representing a class, interface, enum, or struct. */
	private static final ETokenType[] TYPE_KEYWORD = { ETokenType.CLASS,
			ETokenType.INTERFACE, ETokenType.ENUM, ETokenType.STRUCT };

	/** Constructor. */
	public CommentWekaInstanceCreator() {
		super(ECommentCategory.class);

	}

	/** {@inheritDoc} */
	@Override
	public ECommentCategory getDefaultLabel() {
		return ECommentCategory.CODE;
	}

	/** {@inheritDoc} */
	@Override
	protected List<IFeature<Comment>> getFeatures() {
		List<IFeature<Comment>> features = new ArrayList<IFeature<Comment>>();

		features.add(new BooleanFeatureBase<Comment>("contains copyright") {
			@Override
			public boolean getBooleanValue(Comment classificationObject) {
				return CommentUtils.isCopyrightComment(classificationObject
						.getCommentString());
			}
		});

		features.add(new BooleanFeatureBase<Comment>("contains author") {
			@Override
			public boolean getBooleanValue(Comment classificationObject) {
				String commentString = classificationObject.getCommentString()
						.toLowerCase();
				return commentString.contains("author");
			}
		});

		features.add(new BooleanFeatureBase<Comment>("followed by class") {
			@Override
			public boolean getBooleanValue(Comment classificationObject) {
				List<IToken> nextTokens = classificationObject.getNextTokens(
						classificationObject.getPosition(), 10);
				return TokenStreamUtils.containsAny(nextTokens, 0,
						nextTokens.size(), TYPE_KEYWORD);
			}
		});

		features.add(new BooleanFeatureBase<Comment>("doc comment") {
			@Override
			public boolean getBooleanValue(Comment classificationObject) {
				String commentString = classificationObject.getCommentString()
						.toLowerCase();
				return StringUtils.containsOneOf(commentString, "@param",
						"@return", "@throws", "@link", "{@inheritDoc}",
						"\\param", "\\return");
			}
		});

		features.add(new NamedFeatureBase<Comment>("ast location") {
			@Override
			public double getValue(Comment classificationObject) {
				return classificationObject.getASTLocation();
			}
		});

		features.add(new NamedFeatureBase<Comment>("decl. distance") {
			@Override
			public double getValue(Comment classificationObject) {
				return classificationObject.getMethodFinder()
						.getDistanceToNextDefinition(
								classificationObject.getPosition());
			}
		});

		features.add(new BooleanFeatureBase<Comment>("code indicators") {
			@Override
			public boolean getBooleanValue(Comment classificationObject) {
				return CodeRecognizer.isCommentedOutCode(classificationObject
						.getCommentString());
			}
		});

		features.add(new NamedFeatureBase<Comment>("special characters") {
			@Override
			public double getValue(Comment classificationObject) {
				String comment = classificationObject.getCommentString();
				double allCharacters = comment.length();
				double specialCharacters = allCharacters
						- comment.replaceAll(";|=|\\(|\\)|\\{|\\}|\\+|\\-|/",
								"").length();
				return specialCharacters / allCharacters;
			}
		});

		features.add(new BooleanFeatureBase<Comment>("context") {
			@Override
			public boolean getBooleanValue(Comment classificationObject) {
				return CoherenceUtils
						.hasContextCorrelation(classificationObject);
			}
		});

		features.add(new BooleanFeatureBase<Comment>("section") {
			@Override
			public boolean getBooleanValue(Comment classificationObject) {
				String commentString = classificationObject.getCommentString();
				return StringUtils.containsOneOf(commentString, "***", "---",
						"///", "\\\\\\", "{{{");
			}
		});

		features.add(new BooleanFeatureBase<Comment>("inside method") {
			@Override
			public boolean getBooleanValue(Comment classificationObject) {
				return classificationObject.getMethodFinder()
						.getShallowParser()
						.isInsideMethod(classificationObject.getPosition());
			}
		});

		features.add(new NamedFeatureBase<Comment>("length") {
			@Override
			public double getValue(Comment classificationObject) {
				return StringUtils.countLines(classificationObject
						.getCommentString());
			}
		});

		features.add(new BooleanFeatureBase<Comment>("task") {
			@Override
			public boolean getBooleanValue(Comment classificationObject) {
				return TASK_TAG_PATTERN.matcher(
						classificationObject.getCommentString()).find();

			}
		});

		features.add(new BooleanFeatureBase<Comment>("followed by comment") {
			@Override
			public boolean getBooleanValue(Comment classificationObject) {
				List<IToken> nextTokens = classificationObject.getNextTokens(
						classificationObject.getPosition(), 3);
				for (IToken token : nextTokens) {
					if (token.getType().getTokenClass()
							.equals(ETokenClass.COMMENT)) {
						return true;
					}
				}
				return false;

			}
		});

		features.add(new BooleanFeatureBase<Comment>("first") {
			@Override
			public boolean getBooleanValue(Comment classificationObject) {
				for (int i = 0; i < classificationObject.getPosition(); i++) {
					if (classificationObject.getTokens().get(i).getType()
							.getTokenClass().equals(ETokenClass.COMMENT)) {
						return false;
					}
				}
				return true;
			}
		});

		return features;
	}
}
