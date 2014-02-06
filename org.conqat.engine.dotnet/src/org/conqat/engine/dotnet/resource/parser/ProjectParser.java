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

import java.util.HashSet;
import java.util.Set;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.IConQATLogger;
import org.conqat.engine.dotnet.resource.BuildConfiguration;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.util.TextElementXMLReader;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.commons.xml.IXMLElementProcessor;

/**
 * Base class for parsers of VS.NET project elements.
 * <p>
 * Since different versions of the Visual Studio generate different project
 * formats, different project parsers exist. This class serves as factory to
 * create a {@link ProjectParser} for a specific VS.NET version.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 43447 $
 * @ConQAT.Rating GREEN Hash: 0D831874A2876469C151F9F7DCEFFC5D
 */
public abstract class ProjectParser {

	/** The default warning level used if no level is specified. */
	private static int DEFAULT_WARN_LEVEL = 4;

	/** Logger used to issue log statements */
	protected final IConQATLogger logger;

	/** Constructor */
	public ProjectParser(IConQATLogger logger) {
		this.logger = logger;
	}

	/**
	 * Extracts relative for the source elements that are part of the project.
	 */
	public Set<String> extractSourceElementRelativeNames(
			ITextElement projectElement) throws ConQATException {
		return createReader(projectElement).readRelativeSourceNames();
	}

	/**
	 * Extracts the relative names to the assemblies that are generated when
	 * compiling this project.
	 * 
	 * @param configuration
	 */
	public Set<String> extractAssemblyRelativeNames(
			ITextElement projectElement, BuildConfiguration configuration)
			throws ConQATException {
		ProjectElementReaderBase reader = createReader(projectElement);

		String relativeName = reader.readRelativeAssemblyName(configuration);

		// the relative name is null if the relative output path could not be
		// retrieved. The warning is already set by readRelativeAssemblyName()
		Set<String> relativeNames = new HashSet<String>();
		if (relativeName != null) {
			relativeNames.add(relativeName);
		}
		return relativeNames;
	}

	/**
	 * Template factory name that returns the project version specific XMLReader
	 */
	protected abstract ProjectElementReaderBase createReader(
			ITextElement projectElement) throws ConQATException;

	/**
	 * Returns a {@link Set} of suppressed warning ids for the given
	 * configuration
	 */
	public Set<Integer> getNoWarnIds(ITextElement projectElement,
			BuildConfiguration configuration) throws ConQATException {
		ProjectElementReaderBase reader = createReader(projectElement);

		reader.parseBuildConfiguration(configuration);

		return reader.noWarnList;
	}

	/**
	 * Returns the warning level for the given configuration. May be
	 * {@link #DEFAULT_WARN_LEVEL} if not defined.
	 */
	public Integer getWarnLevel(ITextElement projectElement,
			BuildConfiguration configuration) throws ConQATException {
		ProjectElementReaderBase reader = createReader(projectElement);

		reader.parseBuildConfiguration(configuration);

		if (reader.warnLevel == null) {
			return DEFAULT_WARN_LEVEL;
		}

		return reader.warnLevel;
	}

	/** XML reader that performs actual XML processing. */
	protected abstract class ProjectElementReaderBase
			extends
			TextElementXMLReader<EProjectXmlElement, EProjectXmlAttribute, ConQATException> {

		/** The list of relative source names. */
		protected final Set<String> relativeSourceElementNames = new HashSet<String>();

		/** The name of the assembly */
		protected String assemblyName = null;

		/** The relative path to the assembly */
		protected String outputPath = null;

		/** The output type (can be either Library or Exe) */
		protected String outputType = null;

		/** WarnLevel used for this configuration */
		protected Integer warnLevel = null;

		/** List of Ids that are in the NoWarn liste */
		protected Set<Integer> noWarnList = new HashSet<Integer>();

		/** Constructor */
		public ProjectElementReaderBase(ITextElement element)
				throws ConQATException {
			super(element, EProjectXmlAttribute.class);
		}

		/**
		 * Reads the relative names of the included source elements from the
		 * project.
		 */
		public Set<String> readRelativeSourceNames() throws ConQATException {
			parseAndWrapExceptions();
			processDecendantElements(createProcessor());
			return relativeSourceElementNames;
		}

		/** Reads the build configuration */
		public Set<String> readBuildConfiguration() throws ConQATException {
			parseAndWrapExceptions();
			processDecendantElements(createProcessor());
			return relativeSourceElementNames;
		}

		/** Reads the relative names of the executables created by this project. */
		public String readRelativeAssemblyName(
				BuildConfiguration buildConfiguration) throws ConQATException {
			String extension = "";
			parseAndWrapExceptions();

			// get the name of the assembly
			processDecendantElements(createAssemblyNameProcessor());

			// get the output type (it can only be "Library" or "Exe")
			IXMLElementProcessor<EProjectXmlElement, ConQATException> processor = createOutputTypeProcessor();

			// The createOutputTypeProcessor method in VS2003 projects returns
			// null because the AssemblyNameProcessor already sets the output
			// type for VS2003 projects
			if (processor != null) {
				processDecendantElements(processor);
			}

			if (outputType == null || assemblyName == null) {
				// This happens if .vcproj-files are parsed (CR#2556 requests
				// parsing of .vcproj files).
				logger.warn("No valid assembly name was identified in the project "
						+ getLocation() + ".");
				return null;
			}

			if (outputType.equals("Library")) {
				extension = ".dll";
			} else {
				if (!outputType.equals("Exe") && !outputType.equals("WinExe")) {
					logger.warn("The assembly " + assemblyName
							+ " that was found in project " + getLocation()
							+ " is neither a .dll nor a .exe");
					return null;
				}
				extension = ".exe";
			}

			// get the relative path to the assembly
			processDecendantElements(createRelativeAssemblyPathProcessor(buildConfiguration));

			CCSMAssert.isNotNull(assemblyName, "No assemblyName in project "
					+ getLocation() + " identified");
			if (outputPath == null) {
				logger.warn("No relative output path found for project "
						+ getLocation() + ". Perhaps the build configuration ("
						+ buildConfiguration.getName() + "|"
						+ buildConfiguration.getPlatform()
						+ ") is not valid for the project.");
				return null;
			}

			String separator = StringUtils.EMPTY_STRING;
			if (!StringUtils.endsWithOneOf(outputPath, "\\", "/")) {
				separator = "\\";
			}
			return outputPath + separator + assemblyName + extension;
		}

		/** Reads in a list of suppressed warnings from the NoWarn property */
		protected void readNoWarnIDs(String noWarnIds) {
			for (String id : noWarnIds.split("[,;]")) {
				id = id.trim();
				if (!id.isEmpty()) {
					noWarnList.add(Integer.parseInt(id));
				}
			}
		}

		/** Runs the basic analysis of the property groups */
		protected void parseBuildConfiguration(
				BuildConfiguration buildConfiguration) throws ConQATException {
			parseAndWrapExceptions();
			processChildElements(createBuildConfigurationProcessor(buildConfiguration));
		}

		/**
		 * Template factory name that creates the XMLProcessor that performs the
		 * actual information retrieval
		 */
		protected abstract IXMLElementProcessor<EProjectXmlElement, ConQATException> createProcessor();

		/**
		 * Template factory name that creates the XMLProcessor that performs the
		 * actual information retrieval
		 */
		protected abstract IXMLElementProcessor<EProjectXmlElement, ConQATException> createAssemblyNameProcessor();

		/**
		 * Template factory name that creates the XMLProcessor that performs the
		 * actual information retrieval
		 */
		protected abstract IXMLElementProcessor<EProjectXmlElement, ConQATException> createOutputTypeProcessor();

		/**
		 * Template factory name that creates the XMLProcessor that performs the
		 * actual information retrieval
		 */
		protected abstract IXMLElementProcessor<EProjectXmlElement, ConQATException> createRelativeAssemblyPathProcessor(
				BuildConfiguration buildConfiguration);

		/**
		 * Template factory name that creates the XMLProcessor that performs the
		 * the parsing of a build configuration
		 */
		protected abstract IXMLElementProcessor<EProjectXmlElement, ConQATException> createBuildConfigurationProcessor(
				BuildConfiguration buildConfiguration);

	}
}