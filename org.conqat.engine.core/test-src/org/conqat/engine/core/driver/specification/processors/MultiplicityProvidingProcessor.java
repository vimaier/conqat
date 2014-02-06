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
package org.conqat.engine.core.driver.specification.processors;

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.IConQATProcessor;
import org.conqat.engine.core.core.IConQATProcessorInfo;

/**
 * This processor provides parameters of many interesting multiplicities.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 81EB438C2DE8D13326A7EE9FCCCB6244
 */
@SuppressWarnings("unused")
@AConQATProcessor(description = "desc")
public class MultiplicityProvidingProcessor implements IConQATProcessor {

	/** test method */
	@AConQATParameter(description = "", name = "multAny", minOccurrences = 0, maxOccurrences = -1)
	public void multAny(@AConQATAttribute(description = "", name = "ref")
	int a) {// nothing to do here
	}

	/** test method */
	@AConQATParameter(description = "", name = "mult01", minOccurrences = 0, maxOccurrences = 1)
	public void mult01(@AConQATAttribute(description = "", name = "ref")
	int a) {// nothing to do here
	}

	/** test method */
	@AConQATParameter(description = "", name = "mult1", minOccurrences = 1, maxOccurrences = -1)
	public void mult1(@AConQATAttribute(description = "", name = "ref")
	int a) {// nothing to do here
	}

	/** test method */
	@AConQATParameter(description = "", name = "mult07", minOccurrences = 0, maxOccurrences = 7)
	public void mult07(@AConQATAttribute(description = "", name = "ref")
	int a) {// nothing to do here
	}

	/** test method */
	@AConQATParameter(description = "", name = "mult4", minOccurrences = 4, maxOccurrences = -1)
	public void mult4(@AConQATAttribute(description = "", name = "ref")
	int a) {// nothing to do here
	}

	/** test method */
	@AConQATParameter(description = "", name = "mult38", minOccurrences = 3, maxOccurrences = 8)
	public void mult38(@AConQATAttribute(description = "", name = "ref")
	int a) {// nothing to do here
	}

	/** test method */
	@AConQATParameter(description = "", name = "two38", minOccurrences = 3, maxOccurrences = 8)
	public void two(@AConQATAttribute(description = "", name = "refA")
	int a, @AConQATAttribute(description = "", name = "refB")
	int b) {// nothing to do here
	}

	/** test method */
	@AConQATParameter(description = "", name = "twoFixed", minOccurrences = 2, maxOccurrences = 2)
	public void twoFixed(@AConQATAttribute(description = "", name = "refA")
	int a, @AConQATAttribute(description = "", name = "refB")
	int b) {// nothing to do here
	}

	/** {@inheritDoc} */
	@Override
	public void init(IConQATProcessorInfo processorInfo) {
		// nothing to do here
	}

	/** {@inheritDoc} */
	@Override
	public Object process() {
		return null;
	}

}