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
package org.conqat.engine.text.comments.classification;

import java.io.File;

import org.conqat.engine.commons.machine_learning.BaseWekaClassifier;
import org.conqat.engine.commons.machine_learning.DataSetCreator;
import org.conqat.engine.text.BundleContext;
import org.conqat.engine.text.comments.Comment;
import org.conqat.engine.text.comments.ECommentCategory;
import org.conqat.lib.scanner.ELanguage;

/**
 * A machine learning classifier for code comments.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 46288 $
 * @ConQAT.Rating GREEN Hash: 3DA187E094128547CB0692B8BB72F3F3
 */
public class CommentClassifier extends
		BaseWekaClassifier<Comment, ECommentCategory> {

	/** Name of the model file of this classifier. */
	private static String modelFileName = "Classifier.model";

	/** Name of the data file for this classifier. */
	private static String dataFileName = "Comments.arff";

	/**
	 * Prefix of the directory where model and data file are located depending
	 * on the language.
	 */
	private static String directoryPrefix = "classification-";

	/**
	 * Constructor for a comment classifier of the given language. If the given
	 * language is not Java or C/CPP, an exception is thrown.
	 */
	public CommentClassifier(ELanguage language) {
		super(new DataSetCreator<Comment, ECommentCategory>(
				new CommentWekaInstanceCreator()), getModelFile(language),
				getDataFile(language));
	}

	/**
	 * Returns the model file depending on the given language. If the given
	 * language is not Java or C/CPP, an exception is thrown.
	 */
	private static File getModelFile(ELanguage language) {
		return BundleContext
				.getInstance()
				.getResourceManager()
				.getResourceAsFile(
						directoryPrefix + language.name().toLowerCase() + "/"
								+ modelFileName);
	}

	/**
	 * Returns the data file depending on the given language. As default, the
	 * model file for CPP is returned.
	 */
	private static File getDataFile(ELanguage language) {
		return BundleContext
				.getInstance()
				.getResourceManager()
				.getResourceAsFile(
						directoryPrefix + language.name().toLowerCase() + "/"
								+ dataFileName);
	}
}
