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
package org.conqat.engine.resource.assessment;

import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IContentAccessor;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.engine.resource.text.TextElementProcessorBase;
import org.conqat.lib.commons.assessment.Assessment;
import org.conqat.lib.commons.assessment.ETrafficLightColor;
import org.conqat.lib.commons.assessment.PartitionedRating;
import org.conqat.lib.commons.assessment.Rating;
import org.conqat.lib.commons.assessment.RatingPartition;
import org.conqat.lib.commons.assessment.external.ExternalRatingTableException;
import org.conqat.lib.commons.assessment.external.IRatingTableFileAccessor;
import org.conqat.lib.commons.assessment.partition.PartitioningException;
import org.conqat.lib.commons.string.StringUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: deissenb $
 * @version $Rev: 36788 $
 * @ConQAT.Rating GREEN Hash: 2F2199521C8FE0BD83EA23FF9DC75937
 */
@AConQATProcessor(description = "Determines the ConQAT rating for all elements in the scope. "
		+ "This is calculated on the unfiltered content and is based on the ConQAT. Rating tag in the files.")
public class ConQATRatingAssessor extends TextElementProcessorBase {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "key used for rating", type = "org.conqat.lib.commons.assessment.Assessment")
	public static final String ASSESSMENT_KEY = "rating";

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "line-ratings", attribute = "value", optional = true, description = ""
			+ "If this is enabled, the assessment is performed on a per-line basis (useful for partitioned ratings), "
			+ "otherwise only one color per file is stored in the assessment. Default is false.")
	public boolean useLineRatings = false;

	/** {@inheritDoc} */
	@Override
	protected void setUp(ITextResource root) {
		NodeUtils.addToDisplayList(root, ASSESSMENT_KEY);
	}

	/** {@inheritDoc} */
	@Override
	protected void processElement(ITextElement element) throws ConQATException {
		element.setValue(
				ASSESSMENT_KEY,
				determineAssessment(element.getUnfilteredTextContent(), element));
	}

	/** Determines the assessment based on the content. */
	private Assessment determineAssessment(String content, ITextElement element) {
		Rating rating = new Rating(content);
		if (rating.getStoredRating() != null) {
			return singleFileAssessment(content, rating.getRating());
		}

		try {
			// no old rating found, so maybe this a partitioned rating
			return partitionedAssessment(content, element);
		} catch (PartitioningException e) {
			// no rating means RED
			return singleFileAssessment(content, ETrafficLightColor.RED);
		} catch (ExternalRatingTableException e) {
			// problems with external ratings means RED
			return singleFileAssessment(content, ETrafficLightColor.RED);
		}
	}

	/** Calculates the assessment based on a partitioned rating. */
	private Assessment partitionedAssessment(String content,
			ITextElement element) throws PartitioningException,
			ExternalRatingTableException {
		Assessment assessment = new Assessment();
		PartitionedRating partitionedRating = new PartitionedRating(content,
				new ElementResolver(element));
		for (RatingPartition partition : partitionedRating.getPartitions()) {
			int length = partition.getEndLine() - partition.getStartLine() + 1;
			assessment.add(partition.getRating(), length);
		}
		return assessment;
	}

	/** Returns the assessment if the same color applies to the entire file. */
	private Assessment singleFileAssessment(String content,
			ETrafficLightColor color) {
		if (useLineRatings) {
			Assessment assessment = new Assessment();
			assessment.add(color, StringUtils.splitLinesAsList(content).size());
			return assessment;
		}
		return new Assessment(color);
	}

	/**
	 * Rating table resolver supporting ConQAT resource model and uniform paths.
	 */
	private static class ElementResolver implements IRatingTableFileAccessor {

		/** The underlying element. */
		private final ITextElement element;

		/** Constructor. */
		public ElementResolver(ITextElement element) {
			this.element = element;
		}

		/**
		 * {@inheritDoc}
		 * <p>
		 * Returns the uniform path.
		 */
		@Override
		public String getFilePath() {
			return element.getUniformPath();
		}

		/**
		 * {@inheritDoc}
		 * <p>
		 * Resolves the file using a relative accessor and reads the content as
		 * UTF-8.
		 */
		@Override
		public String getRelativeFileContent(String relativePath) {
			try {
				IContentAccessor accessor = element
						.createRelativeAccessor(relativePath);
				return StringUtils.bytesToString(accessor.getContent());
			} catch (ConQATException e) {
				// suppress problems according to contract.
				return null;
			}
		}

		/** {@inheritDoc} */
		@Override
		public void setRelativeFileContent(String relativePath, String content) {
			// ignore changing of content (we are only reading here)
		}

	}

}
