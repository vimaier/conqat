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
package org.conqat.engine.resource.scope;


import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IContentAccessor;

/**
 * Base class for scopes that support include/exclude pattern.
 * 
 * @author $Author: feilkas $
 * @version $Rev: 41250 $
 * @ConQAT.Rating GREEN Hash: EED7CC8812355E51FA485A5D6997C6A7
 */
public abstract class PatternSupportingScopeBase extends CompiledPatternProcessorBase {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "project", attribute = "name", description = "The logical name of the project containing the resources. This is used to build the uniform names.")
	public String projectName;

	/** {@inheritDoc} */
	@Override
	public final IContentAccessor[] process() throws ConQATException {
		compilePatterns();
		return createAccessors();
	}

	/** Template method used for creation of the content accessors. */
	protected abstract IContentAccessor[] createAccessors()
			throws ConQATException;
}