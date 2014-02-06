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
package org.conqat.engine.commons.pattern;

import java.util.ArrayList;

import org.conqat.lib.commons.clone.IDeepCloneable;
import org.conqat.lib.commons.string.IRegexReplacement;
import org.conqat.lib.commons.string.RegexReplacementProcessor;

/**
 * A list of RegEx patterns with corresponding substitution strings. This is
 * useful for defining transformations (e.g. abbreviations) for strings, such as
 * labels.
 * 
 * @author Benjamin Hummel
 * @author $Author: hummelb $
 * @version $Rev: 36404 $
 * @ConQAT.Rating GREEN Hash: D12C57A4385A377271B739E94C0D8980
 * 
 * @see PatternTransformationDef
 */
public class PatternTransformationList extends ArrayList<IRegexReplacement>
		implements IDeepCloneable {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/**
	 * Returns the given string after transforming it using all contained
	 * transformation pairs.
	 */
	public String applyTransformation(String string) {
		return new RegexReplacementProcessor(this).process(string);
	}

	/** {@inheritDoc} */
	@Override
	public PatternTransformationList deepClone() {
		PatternTransformationList result = new PatternTransformationList();
		result.addAll(this);
		return result;
	}

}