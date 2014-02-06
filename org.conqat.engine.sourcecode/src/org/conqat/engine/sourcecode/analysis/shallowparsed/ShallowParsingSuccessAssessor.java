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

import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.analysis.AnalysisProblemsProcessorBase;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.shallowparser.ShallowParserFactory;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntityTraversalUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: heinemann $
 * @version $Rev: 44267 $
 * @ConQAT.Rating GREEN Hash: 54B5DE21FC5F12C7A1842D1B670641A5
 */
@AConQATProcessor(description = "Annotates each element with an assessment of whether it could be shallow parsed or not. "
		+ "Optionally, findings can be created for parser problems.")
public class ShallowParsingSuccessAssessor extends
		AnalysisProblemsProcessorBase {

	/** Finding group name used. */
	private static final String FINDING_GROUP_NAME = "Parser Problems";

	/** {ConQAT.Doc} */
	@AConQATKey(description = "Green if the element could be parsed successfully, red otherwise.", type = "org.conqat.lib.commons.assessment.Assessment")
	public static final String PARSING_ASSESSMENT_KEY = "parseable";

	/** {ConQAT.Doc} */
	@AConQATKey(description = "Textual description of the parse problem.", type = "java.lang.String")
	public static final String PARSING_ERROR_KEY = "parse error";

	/** {ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "missing-parser", attribute = "report", optional = true, description = ""
			+ "Determines whether to also report if an element is found for which no parser is available. Default is true.")
	public boolean reportMissingParser = true;

	/** Constructor. */
	public ShallowParsingSuccessAssessor() {
		super(PARSING_ASSESSMENT_KEY, PARSING_ERROR_KEY, FINDING_GROUP_NAME);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws ConQATException
	 *             in case of underlying I/O problems.
	 */
	@Override
	protected String getAnalysisErrorMessage(ITokenElement element)
			throws ConQATException {
		if (!ShallowParserFactory.supportsLanguage(element.getLanguage())) {
			if (reportMissingParser) {
				return "No parser available for language "
						+ element.getLanguage();
			}
			return null;
		}

		List<ShallowEntity> entities = ShallowParserFactory.parse(element,
				getLogger());
		ShallowEntity incomplete = ShallowEntityTraversalUtils
				.findIncompleteEntity(entities);
		if (incomplete != null) {
			return "Incompletely parsed node: "
					+ incomplete.toLocalStringUnfiltered(element);
		}
		return null;
	}
}