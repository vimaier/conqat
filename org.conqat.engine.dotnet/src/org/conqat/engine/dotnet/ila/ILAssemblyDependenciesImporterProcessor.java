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
package org.conqat.engine.dotnet.ila;

import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.dotnet.ila.xml.IlaAssembliesXmlReader;
import org.conqat.engine.dotnet.ila.xml.IlaXmlReaderBase;
import org.conqat.engine.resource.text.ITextElement;

/**
 * {@ConQAT.Doc}
 * 
 * @see ILAnalyzerRunnerProcessor
 * 
 * @author $Author: juergens $
 * @version $Revision: 35167 $
 * @ConQAT.Rating GREEN Hash: 2A0B26BF845B5172AC384E6149339281
 */
@AConQATProcessor(description = "Reads dependency information stored in XML produced by the Intermediate "
		+ "Language Analyzer files into a representation that can be used to create an dependency graph of the "
		+ "assemblies contained in a solution.")
public class ILAssemblyDependenciesImporterProcessor extends
		ILImporterProcessorBase {

	/** {@inheritDoc} */
	@Override
	protected IlaXmlReaderBase<?> createXmlReader(ITextElement element)
			throws ConQATException {
		return new IlaAssembliesXmlReader(element, outputRoot, excludePatterns,
				includePatterns, includedDependencies, excludedDependencies);
	}
}