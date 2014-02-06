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
package org.conqat.engine.architecture.overlap;

import java.util.regex.Pattern;

import org.conqat.engine.architecture.assessment.shared.ICodeMapping;
import org.conqat.engine.architecture.format.ECodeMappingType;

/**
 * This class takes a {@link Pattern} and adapts it to represent an
 * {@link ICodeMapping} to the outside.
 * 
 * @author $Author: Moritz Marc Beller$
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 89B0890766FA91D81D9B0BAD05DDD92A
 */
public class PatternToCodeMappingAdaptor implements ICodeMapping {

	/** The pattern that is wrapped */
	private Pattern wrappedPattern;

	/** The type of the code mapping for the pattern */
	private ECodeMappingType type;

	/** constructor */
	public PatternToCodeMappingAdaptor(Pattern wrappedPattern,
			ECodeMappingType type) {
		this.wrappedPattern = wrappedPattern;
		this.type = type;
	}

	/** {@inheritDoc} */
	@Override
	public String getRegex() {
		return wrappedPattern.pattern();
	}

	/** {@inheritDoc} */
	@Override
	public ECodeMappingType getType() {
		return type;
	}

}
