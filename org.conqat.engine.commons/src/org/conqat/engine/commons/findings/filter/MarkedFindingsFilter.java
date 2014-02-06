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
package org.conqat.engine.commons.findings.filter;

import java.util.HashSet;
import java.util.Set;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: juergens $
 * @version $Rev: 37108 $
 * @ConQAT.Rating GREEN Hash: 4B0311BDAB602D9261C65C0D79C23E70
 */
@AConQATProcessor(description = "This processor filters all findings that were marked with a specific value."
		+ FindingsFilterBase.PROCESSOR_DOC_SUFFIX)
public class MarkedFindingsFilter extends FindingsFilterBase {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.READKEY_NAME, attribute = ConQATParamDoc.READKEY_KEY_NAME, description = "The key in the findings where to read the marker values.")
	public String key;

	/** The values to be filtered. */
	private final Set<String> values = new HashSet<String>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "filter", minOccurrences = 1, description = "Adds a value to the filter list. A finding will be removed if the value stored at the given key is one of the mark values.")
	public void addFilterValue(
			@AConQATAttribute(name = "value", description = "The value.") String value) {
		values.add(value);
	}

	/** {@inheritDoc} */
	@Override
	protected boolean isFiltered(IConQATNode node, Finding finding) {
		return values.contains(finding.getValue(key));
	}

}
