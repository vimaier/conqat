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
package org.conqat.engine.dotnet.resource.parser;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.IConQATLogger;
import org.conqat.engine.dotnet.resource.BuildConfiguration;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.commons.xml.IXMLElementProcessor;

/**
 * Parses VS.NET 2003 projects.
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 45758 $
 * @ConQAT.Rating GREEN Hash: 3CF3B7BB2EE45714151CC542D8B66247
 */
/* package */class ProjectParser8 extends ProjectParser {

	/** Constructor */
	protected ProjectParser8(IConQATLogger logger) {
		super(logger);
	}

	/** {@inheritDoc} */
	@Override
	protected ProjectReader8 createReader(ITextElement projectElement)
			throws ConQATException {
		return new ProjectReader8(projectElement);
	}

	/** XML reader that performs actual XML processing. */
	private class ProjectReader8 extends ProjectElementReaderBase {

		/** Creates a {@link ProjectReader8} for a project */
		public ProjectReader8(ITextElement element) throws ConQATException {
			super(element);
		}

		/** {@inheritDoc} */
		@Override
		protected IXMLElementProcessor<EProjectXmlElement, ConQATException> createProcessor() {
			return new IncludeProcessor();
		}

		/** {@inheritDoc} */
		@Override
		protected IXMLElementProcessor<EProjectXmlElement, ConQATException> createAssemblyNameProcessor() {
			return new AssemblyProcessor();
		}

		/** {@inheritDoc} */
		@Override
		protected IXMLElementProcessor<EProjectXmlElement, ConQATException> createOutputTypeProcessor() {
			// No outputTypeProcessor needed in VS2003 projects because both
			// AssemblyName and OutputType
			// are an attribute of Settings. These are extracted by the
			// AssemblyProcessor.
			return null;
		}

		/** {@inheritDoc} */
		@Override
		protected IXMLElementProcessor<EProjectXmlElement, ConQATException> createRelativeAssemblyPathProcessor(
				BuildConfiguration buildConfiguration) {
			return new ConfigProcessor(buildConfiguration);
		}

		/** {@inheritDoc} */
		@Override
		protected IXMLElementProcessor<EProjectXmlElement, ConQATException> createBuildConfigurationProcessor(
				BuildConfiguration buildConfiguration) {
			return createRelativeAssemblyPathProcessor(buildConfiguration);
		}

		/** Processor for AssemblyName elements */
		private class AssemblyProcessor implements
				IXMLElementProcessor<EProjectXmlElement, ConQATException> {

			/** {@inheritDoc} */
			@Override
			public EProjectXmlElement getTargetElement() {
				return EProjectXmlElement.Settings;
			}

			/** {@inheritDoc} */
			@Override
			public void process() {
				assemblyName = getStringAttribute(EProjectXmlAttribute.AssemblyName);
				outputType = getStringAttribute(EProjectXmlAttribute.OutputType);
			}
		}

		/** Processor for Config elements */
		private class ConfigProcessor implements
				IXMLElementProcessor<EProjectXmlElement, ConQATException> {

			/** Employed configuration */
			private final BuildConfiguration buildConfiguration;

			/** Constructor */
			public ConfigProcessor(BuildConfiguration buildConfiguration) {
				this.buildConfiguration = buildConfiguration;
			}

			/** {@inheritDoc} */
			@Override
			public EProjectXmlElement getTargetElement() {
				return EProjectXmlElement.Config;
			}

			/** {@inheritDoc} */
			@Override
			public void process() {
				if (buildConfiguration.getName().equals(
						getStringAttribute(EProjectXmlAttribute.Name))) {
					outputPath = getStringAttribute(EProjectXmlAttribute.OutputPath);
					warnLevel = Integer
							.parseInt(getStringAttribute(EProjectXmlAttribute.WarningLevel));
					readNoWarnIDs(getStringAttribute(EProjectXmlAttribute.NoWarn));
				}
			}
		}

		/** Processor for Include elements */
		private class IncludeProcessor implements
				IXMLElementProcessor<EProjectXmlElement, ConQATException> {

			/** {@inheritDoc} */
			@Override
			public EProjectXmlElement getTargetElement() {
				return EProjectXmlElement.Include;
			}

			/** {@inheritDoc} */
			@Override
			public void process() throws ConQATException {
				processChildElements(new FileProcessor());
			}
		}

		/** Processor for File elements */
		private class FileProcessor implements
				IXMLElementProcessor<EProjectXmlElement, ConQATException> {

			/** {@inheritDoc} */
			@Override
			public EProjectXmlElement getTargetElement() {
				return EProjectXmlElement.File;
			}

			/** {@inheritDoc} */
			@Override
			public void process() {
				String relativeSourceElementName = getStringAttribute(EProjectXmlAttribute.RelPath);
				String buildAction = getStringAttribute(EProjectXmlAttribute.BuildAction);
				if (buildAction != null && buildAction.equals("Compile")) {
					String link = getStringAttribute(EProjectXmlAttribute.Link);
					if (!StringUtils.isEmpty(link)) {
						relativeSourceElementName = link;
					}
					relativeSourceElementNames.add(relativeSourceElementName);
				}
			}

		}

	}

}