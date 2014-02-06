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
package org.conqat.engine.resource.text;

import java.nio.charset.Charset;

import org.conqat.engine.commons.CommonUtils;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IContentAccessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author deissenb
 * @author $Author: juergens $
 * @version $Rev: 35198 $
 * @ConQAT.Rating GREEN Hash: DA5D407E4761059066E54E851B4FAD32
 */
@AConQATProcessor(description = "Factory for text elements.")
public class TextElementFactory extends TextFilterAwareElementFactoryBase {

	/** The encoding used. */
	protected Charset encoding = Charset.defaultCharset();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.ENCODING_PARAM_NAME, maxOccurrences = 1, description = ConQATParamDoc.ENCODING_PARAM_DESC)
	public void setEncoding(
			@AConQATAttribute(name = ConQATParamDoc.ENCODING_ATTR_NAME, description = ConQATParamDoc.ENCODING_ATTR_DESC) String encodingName)
			throws ConQATException {
		encoding = CommonUtils.obtainEncoding(encodingName);
	}

	/** {@inheritDoc} */
	// keep exception for subclasses
	@SuppressWarnings("unused")
	@Override
	public ITextElement create(IContentAccessor accessor)
			throws ConQATException {
		return new TextElement(accessor, encoding, getFilters());
	}
}