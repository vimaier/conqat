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
package org.conqat.engine.java.findbugs;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.ELogLevel;
import org.conqat.engine.resource.base.ReportReaderBase;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.util.TextElementXMLReader;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.commons.xml.IXMLElementProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 46815 $
 * @ConQAT.Rating GREEN Hash: D674613FDB252F5C7615CC79FF348792
 */
@AConQATProcessor(description = "Reads a FindBugs report and attaches the findings "
		+ "to the provided resource tree. "
		+ ReportReaderBase.DOC
		+ " A finding type is e.g. WMI_WRONG_MAP_ITERATOR. "
		+ "See http://findbugs.sourceforge.net/bugDescriptions.html for"
		+ "details.")
public class FindBugsReportReader extends ReportReaderBase {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "exclude-cross-file-location-findings", attribute = "enabled", optional = true, description = ""
			+ "Whether to exclude findings with locations referencing multiple different files. Defaults to false.")
	public boolean excludeCrossFileLocationFindings = false;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "use-rule-description-only", attribute = "enabled", optional = true, description = ""
			+ "Whether to use only the rule description for message and finding group. Defaults to false.")
	public boolean useRuleDescriptionOnly = false;

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Key for findings", type = "org.conqat.engine.commons.findings.FindingsList")
	public static final String FIND_BUGS = "FindBugs";

	/** The categories included. */
	private final Set<String> categories = new HashSet<String>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "category", minOccurrences = 0, description = "Adds a category to include when loading the report. "
			+ "If no categories are specified, all categories will be allowed.")
	public void addCategory(
			@AConQATAttribute(name = "name", description = "Known categories: "
					+ "CORRECTNESS, NOISE, SECURITY, BAD_PRACTICE, STYLE, PERFORMANCE, MALICIOUS_CODE, "
					+ "MT_CORRECTNESS, I18N, EXPERIMENTAL") String category) {
		categories.add(category);
	}

	/** {@inheritDoc} */
	@Override
	protected ELogLevel getDefaultLogLevel() {
		return ELogLevel.WARN;
	}

	/** {@inheritDoc} */
	@Override
	protected String obtainRuleDescription(String ruleId)
			throws ConQATException {
		return FindbugsMessageManager.getInstance().getShortDescription(ruleId);
	}

	/** {@inheritDoc} */
	@Override
	protected String obtainDetailedDescription(String ruleId)
			throws ConQATException {
		return FindbugsMessageManager.getInstance().getDetailedDescription(
				ruleId);
	}

	/** {@inheritDoc} */
	@Override
	protected String getFindingGroupName(String ruleId, String ruleDescription) {
		if (useRuleDescriptionOnly) {
			return ruleDescription;
		}
		return super.getFindingGroupName(ruleId, ruleDescription);
	}

	/**
	 * Loads a single findbugs report.
	 */
	@Override
	protected void loadReport(ITextElement report) throws ConQATException {
		new BugCollectionReader(report).load();
	}

	/** Class used for reading FindBUgs bug collection result files. */
	private final class BugCollectionReader
			extends
			TextElementXMLReader<EBugCollectionElements, EBugCollectionAttributes, ConQATException> {

		/** List of source directories. */
		private final List<String> srcDirs = new ArrayList<String>();

		/** Map used to cache relative to absolute lookups. */
		private final Map<String, String> relativeToLocation = new HashMap<String, String>();

		/** Stores during parsing whether a findings has been created. */
		private boolean findingCreated = false;

		/** Constructor. */
		private BugCollectionReader(ITextElement report) throws ConQATException {
			super(report, EBugCollectionAttributes.class);
		}

		/**
		 * Returns the absolute filename. This is resolved by trying all
		 * {@link #srcDirs} and cached in {@link #relativeToLocation}.
		 * Theoretically, we could handle the attempts to find right directory
		 * via the functionality provided by the base class. However, this would
		 * result in many confusing log messages. Hence, we perform it here.
		 */
		private String getLocation(String relativeFile) throws ConQATException {
			String result = relativeToLocation.get(relativeFile);
			if (result == null) {
				for (String srcDir : srcDirs) {
					String path = new File(srcDir, relativeFile).getPath();
					if (isValidLocation(path)) {
						result = path;
						break;
					}
				}

				if (result == null) {
					String message = "Could not resolve relative file "
							+ relativeFile
							+ "! Probably your FindBugs report misses srcDir information or the files have moved.";
					handleError(message);
				} else {
					relativeToLocation.put(relativeFile, result);
				}
			}
			return result;
		}

		/** Reads the report and loads its contents into the findings report. */
		public void load() throws ConQATException {
			parseAndWrapExceptions();

			processChildElements(new ProjectProcessor());
			processChildElements(new BugInstanceProcessor());
		}

		/** Processor for the project element. Delegates to src dirs. */
		private final class ProjectProcessor implements
				IXMLElementProcessor<EBugCollectionElements, ConQATException> {

			/** {@inheritDoc} */
			@Override
			public EBugCollectionElements getTargetElement() {
				return EBugCollectionElements.Project;
			}

			/** {@inheritDoc} */
			@Override
			public void process() throws ConQATException {
				processChildElements(new SrcDirProcessor());
			}
		}

		/** Processor for the src dir elements; stores the src dirs in the */
		private final class SrcDirProcessor implements
				IXMLElementProcessor<EBugCollectionElements, ConQATException> {

			/** {@inheritDoc} */
			@Override
			public EBugCollectionElements getTargetElement() {
				return EBugCollectionElements.SrcDir;
			}

			/** {@inheritDoc} */
			@Override
			public void process() {
				srcDirs.add(getText());
			}
		}

		/** Processor for bug instances. */
		private final class BugInstanceProcessor implements
				IXMLElementProcessor<EBugCollectionElements, ConQATException> {

			/** {@inheritDoc} */
			@Override
			public EBugCollectionElements getTargetElement() {
				return EBugCollectionElements.BugInstance;
			}

			/** {@inheritDoc} */
			@Override
			public void process() throws ConQATException {
				String category = getStringAttribute(EBugCollectionAttributes.category);
				if (!(categories.isEmpty() || categories.contains(category))) {
					return;
				}

				String type = getStringAttribute(EBugCollectionAttributes.type);
				if (ignoreFindingType(type)) {
					return;
				}

				Set<String> classNames = new HashSet<String>();
				List<String> methodAndFieldNames = new ArrayList<String>();
				ClassProcessor cp = new ClassProcessor(null, null);
				processChildElements(cp);
				classNames.add(cp.className);

				processChildElements(new MethodAndFieldProcessor(
						EBugCollectionElements.Method, classNames,
						methodAndFieldNames, null, null));
				processChildElements(new MethodAndFieldProcessor(
						EBugCollectionElements.Field, classNames,
						methodAndFieldNames, null, null));

				if (excludeCrossFileLocationFindings
						&& countNonLibraryClasses(classNames) > 1) {
					return;
				}

				findingCreated = false;
				createFinding(type,
						getFindingMessage(type, methodAndFieldNames, cp),
						cp.sourcePath);

				if (!findingCreated) {
					getLogger()
							.log(logLevel,
									"Could not create finding as no locations were found (see messages above)");
				}
			}

			/** Constructs the finding message. */
			String getFindingMessage(String type,
					List<String> methodAndFieldNames, ClassProcessor cp)
					throws ConQATException {
				String methodAndFieldsString = StringUtils.concat(
						methodAndFieldNames, "|");
				if (useRuleDescriptionOnly) {
					return obtainRuleDescription(type);
				}
				return cp.className + ":" + methodAndFieldsString + ":" + type;
			}

			/**
			 * Counts the number of classes that are not in the Java API
			 * (java.*, javax.*).
			 */
			private int countNonLibraryClasses(Set<String> classNames) {
				int nonLibraryClassCount = 0;
				for (String name : classNames) {
					if (name != null && !name.startsWith("java")) {
						nonLibraryClassCount += 1;
					}
				}
				return nonLibraryClassCount;
			}
		}

		/**
		 * Creates a finding for a findbugs bug. This method handles all logging
		 * of problems regarding the locations.
		 */
		private void createFinding(String findbugsRule, String message,
				String sourcePath) throws ConQATException {

			processChildElements(new SourceLineProcessor(findbugsRule, message));

			// if there are no SourceLineLocations, then fall back and
			// search for _field_ locations
			if (!findingCreated) {
				processChildElements(new MethodAndFieldProcessor(
						EBugCollectionElements.Field, null, null, findbugsRule,
						message));
			}

			// if there are no SourceLineLocations, then fall back and
			// search for _method_ locations
			if (!findingCreated) {
				processChildElements(new MethodAndFieldProcessor(
						EBugCollectionElements.Method, null, null,
						findbugsRule, message));
			}

			// if there are no SourceLineLocations, then fall back and
			// search for _class_ locations
			if (!findingCreated) {
				processChildElements(new ClassProcessor(findbugsRule, message));
			}

			if (!findingCreated) {
				getLogger().log(
						logLevel,
						"No specific locations found for '" + sourcePath
								+ "'. Adding plain file location.");

				String location = getLocation(sourcePath);
				if (location != null) {
					createFindingForFileLocation(findbugsRule, message,
							location);
				}
			}
		}

		/**
		 * Processor for the class elements; this is used to collect the class
		 * name and the name of the source file.
		 */
		private final class ClassProcessor implements
				IXMLElementProcessor<EBugCollectionElements, ConQATException> {

			/** The class name. */
			private String className;

			/** The source path. */
			private String sourcePath;

			/** The findbugs rule. */
			private final String findbugsRule;

			/** The message used for findings. */
			private final String message;

			/** Constructor. */
			public ClassProcessor(String findbugsRule, String message) {
				this.findbugsRule = findbugsRule;
				this.message = message;
			}

			/** {@inheritDoc} */
			@Override
			public EBugCollectionElements getTargetElement() {
				return EBugCollectionElements.Class;
			}

			/** {@inheritDoc} */
			@Override
			public void process() throws ConQATException {
				if (StringUtils
						.isEmpty(getStringAttribute(EBugCollectionAttributes.role))) {
					className = getStringAttribute(EBugCollectionAttributes.classname);

					SourceLineProcessor slp = new SourceLineProcessor(
							findbugsRule, message);
					processChildElements(slp);
					if (sourcePath == null) {
						sourcePath = slp.sourcePath;
					}
				}
			}
		}

		/**
		 * Processor for method and field elements; this is used to collect the
		 * method name.
		 */
		private final class MethodAndFieldProcessor implements
				IXMLElementProcessor<EBugCollectionElements, ConQATException> {

			/** The target element. */
			private final EBugCollectionElements targetElement;

			/**
			 * Class names encountered. If this is null, no names are collected.
			 */
			private final Set<String> classNames;

			/** Method and field names. If this is null, no names are collected. */
			private final List<String> methodAndFieldNames;

			/** The findbugs rule. */
			private final String findbugsRule;

			/** The message used for findings. */
			private final String message;

			/** Constructor. */
			public MethodAndFieldProcessor(
					EBugCollectionElements targetElement,
					Set<String> classNames, List<String> methodAndFieldNames,
					String findbugsRule, String message) {
				this.targetElement = targetElement;
				this.classNames = classNames;
				this.methodAndFieldNames = methodAndFieldNames;
				this.findbugsRule = findbugsRule;
				this.message = message;
			}

			/** {@inheritDoc} */
			@Override
			public EBugCollectionElements getTargetElement() {
				return targetElement;
			}

			/** {@inheritDoc} */
			@Override
			public void process() throws ConQATException {
				if (classNames != null) {
					classNames
							.add(getStringAttribute(EBugCollectionAttributes.classname));
				}

				if (StringUtils
						.isEmpty(getStringAttribute(EBugCollectionAttributes.role))) {
					if (methodAndFieldNames != null) {
						methodAndFieldNames
								.add(getStringAttribute(EBugCollectionAttributes.name));
					}

					if (findbugsRule != null) {
						processChildElements(new SourceLineProcessor(
								findbugsRule, message));
					}
				}
			}

		}

		/**
		 * Processor for source line elements; this is used to collect the
		 * source file location and the actual locations.
		 */
		private final class SourceLineProcessor implements
				IXMLElementProcessor<EBugCollectionElements, ConQATException> {

			/** The source path. */
			public String sourcePath;

			/** The findbugs rule. */
			private final String findbugsRule;

			/** The message used for findings. */
			private final String message;

			/** Constructor. */
			public SourceLineProcessor(String findbugsRule, String message) {
				this.findbugsRule = findbugsRule;
				this.message = message;
			}

			/** {@inheritDoc} */
			@Override
			public EBugCollectionElements getTargetElement() {
				return EBugCollectionElements.SourceLine;
			}

			/** {@inheritDoc} */
			@Override
			public void process() throws ConQATException {
				sourcePath = getStringAttribute(EBugCollectionAttributes.sourcepath);

				if (findbugsRule == null || findingCreated) {
					return;
				}

				Integer startLine = obtainLineNumber(EBugCollectionAttributes.start);
				Integer endLine = obtainLineNumber(EBugCollectionAttributes.end);

				if (startLine != null && endLine != null) {
					// there are cases, where Findbugs reports bogus line
					// numbers (i.e. end line before start line). Also see
					// CR#4668. Compensate for this.
					endLine = Math.max(startLine, endLine);

					String location = getLocation(sourcePath);
					if (location != null) {
						createLineRegionFinding(findbugsRule, message,
								location, startLine, endLine);
						findingCreated = true;
					}
				}

			}

			/**
			 * Obtain line number specified by the attribute. In case no valid
			 * line number can be parsed, <code>null</code> is returned.
			 */
			private Integer obtainLineNumber(EBugCollectionAttributes attribute) {
				String lineAsString = getStringAttribute(attribute);
				if (StringUtils.isEmpty(lineAsString)) {
					return null;
				}
				try {
					return Integer.parseInt(lineAsString);
				} catch (NumberFormatException ex) {
					getLogger().log(
							logLevel,
							"Invalid line number information for " + sourcePath
									+ ": '" + lineAsString + "'");
					return null;
				}
			}

		}

	}

}