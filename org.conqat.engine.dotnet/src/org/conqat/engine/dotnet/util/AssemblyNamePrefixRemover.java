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
package org.conqat.engine.dotnet.util;

import org.conqat.engine.commons.string.ConQATStringResolverBase;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: $
 * @version $Rev: $
 * @ConQAT.Rating YELLOW Hash: 755F68A7EFC36AFBBB4087C7501DFB4A
 */
@AConQATProcessor(description="Delegate for removing assembly prefixes.")
public class AssemblyNamePrefixRemover extends
		ConQATStringResolverBase {

	/** {@inheritDoc} */
	@Override
	public String resolve(String name) {
		if (!name.contains(AssemblyNamePrefixer.SEPARATOR)) {
			return name;
		}
		return name.split(AssemblyNamePrefixer.SEPARATOR,2)[1];
	}

}
