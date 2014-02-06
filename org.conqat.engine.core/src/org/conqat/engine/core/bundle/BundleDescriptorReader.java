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
package org.conqat.engine.core.bundle;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.conqat.engine.core.ConQATInfo;
import org.conqat.engine.core.driver.error.EDriverExceptionType;
import org.conqat.engine.core.driver.error.ErrorLocation;
import org.conqat.lib.commons.version.Version;
import org.conqat.lib.commons.xml.IXMLElementProcessor;
import org.conqat.lib.commons.xml.XMLReader;
import org.conqat.lib.commons.xml.XMLResolver;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * A reader for the XML bundle descriptor. This class is implemented with
 * {@link XMLReader} from the commons project. Please read documentation on
 * {@link XMLReader} to understand its basic principles.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 91D7D8B2A7CC992EBA29EC45697153B1
 */
public class BundleDescriptorReader extends
		XMLReader<EBundleXMLElement, EBundleXMLAttribute, BundleException> {

	/** Logger */
	private final Logger logger = Logger
			.getLogger(BundleDescriptorReader.class);

	/** Name of the schema used to validate the bundle descriptor. */
	private static final String SCHEMA_FILE_NAME = "bundle.xsd";

	/** Name of the XML bundle descriptor file */
	private final String filename;

	/**
	 * Create new reader.
	 * 
	 * @param file
	 *            file to read
	 */
	protected BundleDescriptorReader(File file) throws IOException {
		super(file, BundleDescriptorReader.class.getResource(SCHEMA_FILE_NAME),
				new XMLResolver<EBundleXMLElement, EBundleXMLAttribute>(
						EBundleXMLAttribute.class));
		filename = file.getAbsolutePath();
	}

	/**
	 * Read bundle descriptor.
	 * 
	 * @param bundleInfo
	 *            the bundle info where the information is stored.
	 * @throws BundleException
	 *             if any exception occurs.
	 */
	/* package */void read(BundleInfo bundleInfo) throws BundleException {
		parseDescriptor(bundleInfo);

		// name, provider and descriptions
		bundleInfo.setName(getChildText(EBundleXMLElement.name));
		bundleInfo.setProvider(getChildText(EBundleXMLElement.provider));
		bundleInfo.setDescription(getChildText(EBundleXMLElement.description));

		// versions
		processVersions(bundleInfo);

		// core requirement
		processCoreRequirement(bundleInfo);

		// and dependencies.
		processChildElements(new DependenciesProcessor(bundleInfo));
	}

	/**
	 * Parse descriptor and convert possibly thrown exceptions to
	 * {@link BundleException}s.
	 * 
	 */
	private void parseDescriptor(BundleInfo bundleInfo) throws BundleException {
		try {
			parseFile();
		} catch (SAXParseException e) {
			throw new BundleException(
					EDriverExceptionType.XML_PARSING_EXCEPTION,
					"XML parsing exception at line " + e.getLineNumber()
							+ ", colum " + e.getColumnNumber() + " ("
							+ e.getMessage() + ") in " + bundleInfo, e,
					new File(filename));
		} catch (SAXException e) {
			throw new BundleException(
					EDriverExceptionType.XML_PARSING_EXCEPTION,
					"XML parsing exception in " + bundleInfo, e, new File(
							filename));
		} catch (IOException e) {
			throw new BundleException(EDriverExceptionType.IO_ERROR, "File "
					+ filename + " could not be read in " + bundleInfo, e,
					ErrorLocation.UNKNOWN);
		}
	}

	/**
	 * Read core version requirement and check if the core satifies the
	 * requirement.
	 * 
	 * @throws BundleException
	 *             if the core does not satifisfy the requirement.
	 */
	private void processCoreRequirement(BundleInfo bundleInfo)
			throws BundleException {
		VersionProcessor versionProcessor = new VersionProcessor(
				EBundleXMLElement.requiresCore);
		processChildElements(versionProcessor);
		Version requiredCoreVersion = versionProcessor.version;

		Version currentCoreVersion = ConQATInfo.CORE_VERSION;

		if (!requiredCoreVersion.equals(currentCoreVersion)) {
			logger.warn("Bundle '" + bundleInfo + "' requires core version "
					+ requiredCoreVersion + ", but core provides version "
					+ currentCoreVersion + ".");
		}

		bundleInfo.setRequiredCoreVersion(requiredCoreVersion);

	}

	/**
	 * Load current version..
	 */
	private void processVersions(BundleInfo bundleInfo) throws BundleException {
		VersionProcessor versionProcessor = new VersionProcessor(
				EBundleXMLElement.version);
		processChildElements(versionProcessor);
		Version currentVersion = versionProcessor.version;
		bundleInfo.setVersion(currentVersion);
	}

	/** Base class for element processors. */
	private abstract class BundleInfoProcessorBase implements
			IXMLElementProcessor<EBundleXMLElement, BundleException> {

		/** The bundle to write info to. */
		protected final BundleInfo bundleInfo;

		/** Create processor. */
		private BundleInfoProcessorBase(BundleInfo bundleInfo) {
			this.bundleInfo = bundleInfo;
		}
	}

	/**
	 * Processor for dependencies ( {@link EBundleXMLElement#dependsOn}
	 * element).
	 */
	private class DependenciesProcessor extends BundleInfoProcessorBase {

		/** Create processor. */
		private DependenciesProcessor(BundleInfo bundleInfo) {
			super(bundleInfo);
		}

		/** Returns {@link EBundleXMLElement#dependsOn}. */
		@Override
		public EBundleXMLElement getTargetElement() {
			return EBundleXMLElement.dependsOn;
		}

		/** Read dependency. */
		@Override
		public void process() throws BundleException {

			String bundleId = getStringAttribute(EBundleXMLAttribute.bundleId);

			int major = getIntAttribute(EBundleXMLAttribute.major);
			int minor = getIntAttribute(EBundleXMLAttribute.minor);

			Version version = new Version(major, minor);

			BundleDependency dependency = new BundleDependency(bundleId,
					version);

			bundleInfo.addDependency(dependency);
		}
	}

	/** This processor is used to read version information for multiple tags. */
	private class VersionProcessor implements
			IXMLElementProcessor<EBundleXMLElement, BundleException> {

		/** The target element of this processor. */
		private final EBundleXMLElement targetElement;

		/** The version read. */
		private Version version;

		/**
		 * Create new processor for specific target element.
		 */
		private VersionProcessor(EBundleXMLElement targetElement) {
			this.targetElement = targetElement;
		}

		/** Returns target element specified with constructor. */
		@Override
		public EBundleXMLElement getTargetElement() {
			return targetElement;
		}

		/** Read version and store it in {@link #version}. */
		@Override
		public void process() {
			int minor = getIntAttribute(EBundleXMLAttribute.minor);
			int major = getIntAttribute(EBundleXMLAttribute.major);

			version = new Version(major, minor);
		}
	}

}