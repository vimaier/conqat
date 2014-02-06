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
package org.conqat.engine.core.driver.info;

import org.conqat.engine.core.driver.instance.InstanceOutput;
import org.conqat.lib.commons.reflect.ClassType;

/**
 * Info on {@link InstanceOutput}s.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: C2FCA7C67FE0EA779115AFA0EBCA68A0
 */
public class InfoOutput extends InfoRefNode {

	/** The info object this belongs to. */
	private final IInfo info;

	/** The underlying instance output. */
	private final InstanceOutput instanceOutput;

	/** Create a new info output. */
	/* package */InfoOutput(InstanceOutput instanceOutput, IInfo info) {
		this.instanceOutput = instanceOutput;
		this.info = info;
	}

	/** Returns the underlying instance output. */
	/* package */InstanceOutput getInstanceOutput() {
		return instanceOutput;
	}

	/** Returns the info object this belongs to. */
	public IInfo getInfo() {
		return info;
	}

	/**
	 * Returns the name of the output. The default output has an empty string as
	 * name.
	 */
	public String getName() {
		return instanceOutput.getDeclaration().getName();
	}

	/** Returns the type this output produces according to its specification. */
	public ClassType getSpecifiedType() {
		return instanceOutput.getDeclaration().getSpecificationOutput()
				.getType();
	}
}