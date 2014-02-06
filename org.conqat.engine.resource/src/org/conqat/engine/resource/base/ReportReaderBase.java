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
package org.conqat.engine.resource.base;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.engine.resource.util.ResourceTraversalUtils;

/**
 * Base class for processors that read one or many reports and produce findings.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 46811 $
 * @ConQAT.Rating GREEN Hash: C9289C3FDCD1B98A1BE759855EF735A0
 */
public abstract class ReportReaderBase extends
		FindingCreatorBase<ITextResource> {

	/** Processor documentation. */
	public static final String DOC = "If the paths stored in the report "
				+ "refer to the same location the ConQAT analysis is run for, no further "
				+ "configuration is required. If however, the report was generated on another "
				+ "machine, prefixes must supplied to convert pathes in the report to"
				+ "uniform pathes that are used to indentify resources in the resource tree. "
				+ "This processor supports the inclusion and exclusion of finding types.";
	
	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "report-files", attribute = ConQATParamDoc.INPUT_REF_NAME, description = "Scope with report files")
	public ITextResource reports;
	
	
	/** {@inheritDoc} 
	 * @throws ConQATException */
	@Override
	protected void createFindings(ITextResource input) throws ConQATException {
		for (ITextElement report : ResourceTraversalUtils
				.listTextElements(reports)) {
			loadReport(report);
		}
	}
	
	/** Template method that deriving classes override to load report. */
	protected abstract void loadReport(ITextElement report)
			throws ConQATException;

}