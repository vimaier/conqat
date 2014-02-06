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
package org.conqat.engine.sourcecode.analysis.cpp;

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.shallowparser.TokenStreamUtils;
import org.conqat.engine.sourcecode.shallowparser.framework.EShallowEntityType;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;
import org.conqat.lib.scanner.ETokenType;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43899 $
 * @ConQAT.Rating GREEN Hash: 72FD3A852A8129F5A2F3A1C0AA7D8E4E
 */
@AConQATProcessor(description = "This processor reports findings for classes "
		+ "that do not have a virtual destructor or any destructor at all.")
public class VirtualDestructorsAnalyzer extends CppFindingAnalyzerBase {

	/** {@inheritDoc} */
	@Override
	protected void analyzeShallowEntities(final ITokenElement element,
			List<ShallowEntity> entities) throws ConQATException {
		for (ShallowEntity clazz : CppUtils.listRealClasses(entities)) {

			List<ShallowEntity> destructors = listDestructors(clazz);

			if (destructors.size() == 0) {
				createFindingForEntityStart("Class without destructor",
						element, clazz);
			}

			for (ShallowEntity destructor : destructors) {
				if (!destructorIsVirtual(destructor)) {
					createFindingForEntityStart("Non-virtual destructor",
							element, destructor);
				}

			}
		}
	}

	/** Lists declared destructors of a given class entity. */
	private List<ShallowEntity> listDestructors(ShallowEntity clazz) {
		List<ShallowEntity> destructors = new ArrayList<ShallowEntity>();
		for (ShallowEntity method : clazz
				.getChildrenOfType(EShallowEntityType.METHOD)) {
			if (CppUtils.isDestructor(method)) {
				destructors.add(method);
			}
		}
		return destructors;
	}

	/**
	 * Returns true if the given ShallowEntity (which should be a destructor
	 * declaration) is preceded by a 'virtual' keyword.
	 */
	private static boolean destructorIsVirtual(ShallowEntity entity) {
		return TokenStreamUtils.tokenStreamContains(entity.includedTokens(),
				ETokenType.VIRTUAL);
	}

	/** {@inheritDoc} */
	@Override
	protected String getFindingGroupName() {
		return "Missing virtual destructor";
	}
}
