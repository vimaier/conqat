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
package org.conqat.engine.sourcecode.resource;

import java.util.EnumSet;

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.base.UniformPathHierarchyResourceSelectorBase;
import org.conqat.lib.scanner.ELanguage;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 1CDF3BBAC41D920146C2D14012C7D579
 */
@AConQATProcessor(description = "This processor selects all token resources. "
		+ "The selection may be limited to certain languages.")
public class TokenResourceSelector
		extends
		UniformPathHierarchyResourceSelectorBase<ITokenResource, TokenContainer> {

	/** The languages to include. */
	private final EnumSet<ELanguage> languages = EnumSet
			.noneOf(ELanguage.class);

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "language", description = ""
			+ "Adds lanuages to be included. If not set all languages are included.")
	public void addContentAccessors(
			@AConQATAttribute(name = "name", description = "Name of the language") ELanguage language) {
		languages.add(language);
	}

	/** {@inheritDoc} */
	@Override
	protected TokenContainer createRawContainer(String name) {
		return new TokenContainer(name);
	}

	/** {@inheritDoc} */
	@Override
	protected boolean keepElement(IResource element) {
		if (!(element instanceof ITokenElement)) {
			return false;
		}

		if (languages.isEmpty()) {
			return true;
		}

		ELanguage language = ((ITokenElement) element).getLanguage();
		return languages.contains(language);
	}
}