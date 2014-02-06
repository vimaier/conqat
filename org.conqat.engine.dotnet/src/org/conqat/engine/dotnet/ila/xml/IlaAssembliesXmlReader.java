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
package org.conqat.engine.dotnet.ila.xml;

import java.util.HashSet;
import java.util.Set;

import org.conqat.engine.commons.keys.IDependencyListKey;
import org.conqat.engine.commons.node.ListNode;
import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.dotnet.ila.ILImporterProcessorBase;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.lib.commons.xml.IXMLElementProcessor;

/**
 * Reads members from Intermediate Language Analyzer XML files into a
 * representation that can be used to draw dependency graphs between the
 * assemblies.
 * 
 * @author $Author: juergens $
 * @version $Revision: 36636 $
 * @ConQAT.Rating GREEN Hash: D5D68B041082B4046D73927EFA9C2F71
 */
public class IlaAssembliesXmlReader extends IlaXmlReaderBase<ConQATException> {

	/**
	 * Constructor.
	 * 
	 * @param element
	 *            XML that gets parsed.
	 * @param root
	 *            Root under which the nodes representing types found during
	 *            parse are attached.
	 * @param ignorePatterns
	 *            List of patterns for members that are ignored during import.
	 * @param includePatterns
	 *            If not null, only members matching one of these patterns are
	 *            included.
	 */
	public IlaAssembliesXmlReader(ITextElement element, ListNode root,
			PatternList ignorePatterns, PatternList includePatterns,
			Set<String> includedDependencies, Set<String> excludedDependencies)
			throws ConQATException {
		super(element, root, ignorePatterns, includePatterns,
				includedDependencies, excludedDependencies);
	}

	/** {@inheritDoc} */
	@Override
	protected void doParse() throws ConQATException {
		String assemblyName = getStringAttribute(EIlaXmlAttribute.Name);
		ListNode assemblyNode = new ListNode(assemblyName);
		root.addChild(assemblyNode);
		assemblyNode.setValue(ILImporterProcessorBase.ASSEMBLY_NAME,
				assemblyName);

		Set<String> dependencies = new HashSet<String>();
		processDependenciesList(assemblyName, dependencies);
		assemblyNode.setValue(IDependencyListKey.DEPENDENCY_LIST_KEY,
				dependencies);
	}

	/**
	 * Parse list of all dependencies of an assembly
	 * 
	 * @param dependencies
	 */
	private void processDependenciesList(String assemblyName,
			Set<String> dependencies) throws ConQATException {
		XmlDependsReader processor = new XmlDependsReader(assemblyName,
				dependencies);
		processChildElements(processor);
	}

	/** Processor for &lt;TypeElement&gt; nodes */
	private class XmlDependsReader implements
			IXMLElementProcessor<EIlaXmlElement, ConQATException> {

		/** Name of assembly the processor is working on */
		private final String assemblyName;

		/** Set with assembly names this assembly depends on */
		private final Set<String> dependencies;

		/** Constructor */
		public XmlDependsReader(String assemblyName, Set<String> dependencies) {
			this.assemblyName = assemblyName;
			this.dependencies = dependencies;
		}

		/** {@inheritDoc} */
		@Override
		public void process() throws ConQATException {
			String targetAssemblyString = getStringAttribute(EIlaXmlAttribute.Assembly);

			if (!targetAssemblyString.contains(",")) {
				throw new ConQATException(
						"TargetAssemblyString format unexpected: \""
								+ targetAssemblyString + "\"");
			}
			String targetAssemblyName = targetAssemblyString.substring(0,
					targetAssemblyString.indexOf(','));

			boolean included = processDependency(assemblyName,
					targetAssemblyName);
			if (included) {
				dependencies.add(targetAssemblyName);
			}
		}

		/** {@inheritDoc} */
		@Override
		public EIlaXmlElement getTargetElement() {
			return EIlaXmlElement.Depends;
		}
	}
}