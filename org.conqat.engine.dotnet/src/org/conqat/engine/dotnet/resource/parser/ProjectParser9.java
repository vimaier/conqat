/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2012 The ConQAT Project                                   |
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
import org.conqat.lib.commons.xml.IXMLElementProcessor;

/**
 * Parses VS.NET 2005 and newer projects.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 45827 $
 * @ConQAT.Rating GREEN Hash: 7B3971AD9419C50A0D23A582FEB6A1CA
 */
/* package */class ProjectParser9 extends ProjectParser {

	/** Constructor */
	protected ProjectParser9(IConQATLogger logger) {
		super(logger);
	}

	/** {@inheritDoc} */
	@Override
	protected ProjectReader9 createReader(ITextElement projectElement)
			throws ConQATException {
		return new ProjectReader9(projectElement);
	}

	/** XML reader that performs actual XML processing. */
	/* package */class ProjectReader9 extends ProjectElementReaderBase {

		/** Creates a {@link ProjectParser9} for a project */
		public ProjectReader9(ITextElement element) throws ConQATException {
			super(element);
		}

		/** {@inheritDoc} */
		@Override
		protected IXMLElementProcessor<EProjectXmlElement, ConQATException> createProcessor() {
			return new ItemGroupProcessor();
		}

		/** {@inheritDoc} */
		@Override
		protected IXMLElementProcessor<EProjectXmlElement, ConQATException> createAssemblyNameProcessor() {
			return new AssemblyNameProcessor();
		}

		/** {@inheritDoc} */
		@Override
		protected IXMLElementProcessor<EProjectXmlElement, ConQATException> createRelativeAssemblyPathProcessor(
				BuildConfiguration buildConfiguration) {
			return new PropertyGroupProcessor(buildConfiguration);
		}

		/** {@inheritDoc} */
		@Override
		protected IXMLElementProcessor<EProjectXmlElement, ConQATException> createOutputTypeProcessor() {
			return new OutputTypeProcessor();
		}

		/** {@inheritDoc} */
		@Override
		protected IXMLElementProcessor<EProjectXmlElement, ConQATException> createBuildConfigurationProcessor(
				BuildConfiguration buildConfiguration) {
			return new PropertyGroupProcessor(buildConfiguration);
		}

		/** Processor for ItemGroup elements */
		private class ItemGroupProcessor implements
				IXMLElementProcessor<EProjectXmlElement, ConQATException> {

			/** {@inheritDoc} */
			@Override
			public EProjectXmlElement getTargetElement() {
				return EProjectXmlElement.ItemGroup;
			}

			/** {@inheritDoc} */
			@Override
			public void process() throws ConQATException {
				processChildElements(new CompileProcessor());
			}
		}

		/** Processor for PropertyGroup elements */
		private class PropertyGroupProcessor implements
				IXMLElementProcessor<EProjectXmlElement, ConQATException> {

			/** Build configuration used for parsing */
			private final BuildConfiguration buildConfiguration;

			/** Constructor */
			public PropertyGroupProcessor(BuildConfiguration buildConfiguration) {
				this.buildConfiguration = buildConfiguration;
			}

			/** {@inheritDoc} */
			@Override
			public EProjectXmlElement getTargetElement() {
				return EProjectXmlElement.PropertyGroup;
			}

			/** {@inheritDoc} */
			@Override
			public void process() throws ConQATException {

				// check if the current PropertyGroup is the one used in the
				// build configuration
				String condition = getStringAttribute(EProjectXmlAttribute.Condition);

				// check if we are in the default PropertyGroup
				if (condition == null || condition.equals("")) {

					// find the name and type of the assembly
					processChildElements(new OutputTypeProcessor());
					processChildElements(new AssemblyNameProcessor());

					// the arguments defined here are used if the used build
					// configuration does not override them
					processChildElements(new OutputPathProcessor());
				} else {

					// we are not interested in conditions other than "=="
					String conditionSeparator = "==";
					if (!condition.contains(conditionSeparator)) {
						return;
					}

					String[] expressionParts = condition
							.split(conditionSeparator);
					String currentConfiguration = expressionParts[1].trim();

					// check if the propertyGroup is the one for the used
					// configuration and platform
					String config = "'" + buildConfiguration.getName() + "|"
							+ buildConfiguration.getPlatform() + "'";
					if (currentConfiguration.equals(config)) {
						processChildElements(new OutputPathProcessor());
						processChildElements(new WarnLevelProcessor());
						processChildElements(new NoWarnProcessor());
					}
				}
			}
		}

		/** Processor for OutputPath elements */
		private class OutputPathProcessor implements
				IXMLElementProcessor<EProjectXmlElement, ConQATException> {

			/** {@inheritDoc} */
			@Override
			public EProjectXmlElement getTargetElement() {
				return EProjectXmlElement.OutputPath;
			}

			/** {@inheritDoc} */
			@Override
			public void process() {
				outputPath = getText();
			}
		}

		/** Processor for WarnLevel elements */
		private class WarnLevelProcessor implements
				IXMLElementProcessor<EProjectXmlElement, ConQATException> {

			/** {@inheritDoc} */
			@Override
			public EProjectXmlElement getTargetElement() {
				return EProjectXmlElement.WarningLevel;
			}

			/** {@inheritDoc} */
			@Override
			public void process() {
				warnLevel = Integer.parseInt(getText());
			}
		}

		/** Processor for NoWarn elements */
		private class NoWarnProcessor implements
				IXMLElementProcessor<EProjectXmlElement, ConQATException> {

			/** {@inheritDoc} */
			@Override
			public EProjectXmlElement getTargetElement() {
				return EProjectXmlElement.NoWarn;
			}

			/** {@inheritDoc} */
			@Override
			public void process() {
				String noWarn = getText();
				readNoWarnIDs(noWarn);
			}

		}

		/** Processor for ExecutableGroup elements */
		private class AssemblyNameProcessor implements
				IXMLElementProcessor<EProjectXmlElement, ConQATException> {

			/** {@inheritDoc} */
			@Override
			public EProjectXmlElement getTargetElement() {
				return EProjectXmlElement.AssemblyName;
			}

			/** {@inheritDoc} */
			@Override
			public void process() {
				assemblyName = getText();
			}
		}

		/** Processor for ExecutableGroup elements */
		private class OutputTypeProcessor implements
				IXMLElementProcessor<EProjectXmlElement, ConQATException> {

			/** {@inheritDoc} */
			@Override
			public EProjectXmlElement getTargetElement() {
				return EProjectXmlElement.OutputType;
			}

			/** {@inheritDoc} */
			@Override
			public void process() {
				outputType = getText();
			}
		}

		/** Processor for Compile elements */
		private class CompileProcessor implements
				IXMLElementProcessor<EProjectXmlElement, ConQATException> {

			/** {@inheritDoc} */
			@Override
			public EProjectXmlElement getTargetElement() {
				return EProjectXmlElement.Compile;
			}

			/** {@inheritDoc} */
			@Override
			public void process() {
				relativeSourceElementNames
						.add(getStringAttribute(EProjectXmlAttribute.Include));
			}
		}

	}
}