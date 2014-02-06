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
package org.conqat.engine.resource.base;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.commons.findings.EFindingKeys;
import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.findings.FindingCategory;
import org.conqat.engine.commons.findings.FindingGroup;
import org.conqat.engine.commons.findings.FindingGroupInfo;
import org.conqat.engine.commons.findings.location.ElementLocation;
import org.conqat.engine.commons.findings.location.TextRegionLocation;
import org.conqat.engine.commons.findings.util.FindingUtils;
import org.conqat.engine.commons.logging.ListStructuredLogMessage;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.commons.util.SlimmingLogger;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.ELogLevel;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.engine.resource.util.UniformPathUtils;
import org.conqat.lib.commons.collections.CounterSet;
import org.conqat.lib.commons.collections.PairList;
import org.conqat.lib.commons.string.StringUtils;

/**
 * This is a base class for processors that create findings. It is especially
 * useful for processors that import findings from other tools.
 * 
 * This class can serve as base class both for classes that analyze elements in
 * the input scope, and for classes that analyze elements that are not in the
 * input scope.
 * 
 * This class handles two methods of mapping the paths found in reports to the
 * resources in the scope: If no prefixes are defined via
 * {@link #addPrefixToProject(String, String)}, it attempts to match the path
 * against the location string of the resources. If prefixes are defined, it
 * creates uniform paths from the paths in the report by replacing the prefix.
 * The identification of resources is then performed via the uniform paths. This
 * allows us to work with reports generated on the same machine without
 * specifying prefixes.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46849 $
 * @ConQAT.Rating GREEN Hash: F0E70A0632671E3B3C150B65B7F523BF
 */
public abstract class FindingCreatorBase<E extends ITextResource> extends
		ConQATPipelineProcessorBase<E> {

	/** Logging tag for structured log messages. */
	public final static String FINDING_CREATION_LOGGING_TAG = "finding-creation";

	/** The finding category used. */
	protected FindingCategory findingCategory;

	/**
	 * Creates a relation between location prefixes (usually directories) and
	 * projects. If this is empty, matching is performed via uniform paths.
	 * 
	 * @see #resolveViaUniformPath()
	 */
	private final PairList<String, String> prefixToProject = new PairList<String, String>();

	/** List of included bug types. */
	protected PatternList includeTypes;

	/** List of excluded bug types. */
	protected PatternList excludeTypes;

	/**
	 * Key in which ignore flags are stored. See
	 * {@link #setFilterIgnored(String)}
	 */
	protected final Set<String> ignoreKeys = new HashSet<String>();

	/**
	 * If {@link #resolveViaUniformPath()} is true, this maps from uniform path
	 * to element. If false, this maps from location to element.
	 */
	protected Map<String, ITextElement> elementLocationMap;

	/** Slimming logger prevents log message flood for unknown files. */
	private SlimmingLogger logger;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "category-name", attribute = "value", description = "The name of the finding category.")
	public String categoryName;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "findings-key", attribute = "key", optional = true, description = "The key used for storing the findings. "
			+ "If none is given, the category name is used.")
	public String findingsKey = null;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "case-insensitive", attribute = "value", description = "Defines whether the path names in the report "
			+ "should be treated in a case-insensitive manner.", optional = true)
	public boolean caseInsensitive = false;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "lenient", attribute = "mode", description = "If in lenient mode, missing source files are ignored and logged. "
			+ "Otherwise an exception is thrown [false].", optional = true)
	public boolean lenient = false;

	/** Mapping from ruleIds to replacement ruleIds */
	private final HashMap<String, String> replacementRuleMapping = new HashMap<String, String>();

	/** Mapping from replacement ruleIds to their replacement messages */
	private final HashMap<String, String> replacmentRuleMessageMapping = new HashMap<String, String>();

	/** Locations processed */
	private int locationsProcessed = 0;

	/** Unknown locations */
	private final Set<String> unknownLocations = new HashSet<String>();

	/** Number of findings created */
	private int findingsCreated = 0;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.LOG_LEVEL_NAME, attribute = ConQATParamDoc.ATTRIBUTE_VALUE_NAME, description = "In lenient mode, missing source files are logged. "
			+ ConQATParamDoc.LOG_LEVEL_DESCRIPTION
			+ " [default depends on processor]", optional = true)
	public ELogLevel logLevel = getDefaultLogLevel();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "rule-mapping", description = "A mapping from the read ruleId to a custom ruleId and (optional) custom description.", minOccurrences = 0)
	public void setRuleMapping(
			@AConQATAttribute(name = "oldrule", description = "The old ruleId that should be replaced.") String oldRule,
			@AConQATAttribute(name = "newrule", description = "The new ruleId replacing the old one.") String newRule,
			@AConQATAttribute(name = "newdescription", defaultValue = StringUtils.EMPTY_STRING, description = "The new description replacing the old rule description. Leave empty to use the original message.") String newMessage) {
		replacementRuleMapping.put(oldRule, newRule);
		if (!StringUtils.isEmpty(newMessage)) {
			replacmentRuleMessageMapping.put(newRule, newMessage);
		}
	}

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "extended-mapping", attribute = "mode", description = "If extended-mapping is used, all path suffixes of each element are added to the element map. Defaults to false, but the default may be overwritten by subclasses. Warning: Enabling this can consume a lot of memory!", optional = true)
	public boolean extendedMappingEnabled = getExtendetMappingEnabledDefault();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "map", minOccurrences = 0, description = "Defines a mapping from a filename prefix "
			+ "(usually a directory) to a project name. This is used to create a uniform path.")
	public void addPrefixToProject(
			@AConQATAttribute(name = "prefix", description = "The directory/prefix") String prefix,
			@AConQATAttribute(name = "project", description = "The project name") String project)
			throws ConQATException {

		// we introduced this after we had a very hard to debug error
		prefix = prefix.trim();
		prefix = UniformPathUtils.normalizeAllSeparators(prefix);

		for (int i = 0; i < prefixToProject.size(); ++i) {
			if (prefix.startsWith(prefixToProject.getFirst(i))
					|| prefixToProject.getFirst(i).startsWith(prefix)) {
				throw new ConQATException(
						"Directory prefixes in map may not be prefixes from each other!");
			}
		}

		prefixToProject.add(prefix, project);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "include-types", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Sets the list of finding types to include.")
	public void setIncludeTypes(
			@AConQATAttribute(name = ConQATParamDoc.PATTERN_LIST, description = ConQATParamDoc.PATTERN_LIST_DESC) PatternList patternList)
			throws ConQATException {
		PatternList.checkIfEmpty(patternList);
		includeTypes = patternList;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "exclude-types", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Sets the list of bug types to exclude.")
	public void setExcludeTypes(
			@AConQATAttribute(name = ConQATParamDoc.PATTERN_LIST, description = ConQATParamDoc.PATTERN_LIST_DESC) PatternList patternList)
			throws ConQATException {
		PatternList.checkIfEmpty(patternList);
		excludeTypes = patternList;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "filter", description = "Determines whether elements that are marked as ignored, "
			+ "e.g. because they are in generated code, are ignored.", minOccurrences = 0, maxOccurrences = -1)
	public void setFilterIgnored(
			@AConQATAttribute(name = "key", description = "Key that contains ignore flags.", defaultValue = "ignore") String ignoreKey)
			throws ConQATException {

		if (StringUtils.isEmpty(ignoreKey)) {
			throw new ConQATException("Ignore key must not be empty");
		}

		ignoreKeys.add(ignoreKey);
	}

	/** Determines if resolution is performed via uniform paths or locations. */
	protected boolean resolveViaUniformPath() {
		return !prefixToProject.isEmpty();
	}

	/** Returns the finding group for a given rule id. */
	private FindingGroup getFindingGroup(String ruleId) throws ConQATException {

		String ruleDescription = obtainRuleDescription(ruleId);

		// In case we need to replace the ruleId, do so now.
		if (replacementRuleMapping.containsKey(ruleId)) {
			ruleId = replacementRuleMapping.get(ruleId);
			if (replacmentRuleMessageMapping.containsKey(ruleId)) {
				ruleDescription = replacmentRuleMessageMapping.get(ruleId);
			}
		}

		FindingGroup findingGroup = FindingUtils
				.getOrCreateFindingGroupAndSetRuleId(findingCategory,
						getFindingGroupName(ruleId, ruleDescription), ruleId);
		if (findingGroup.getGroupInfo() == null) {
			String detailedDescription = obtainDetailedDescription(ruleId);
			if (detailedDescription != null) {
				findingGroup.setGroupInfo(new FindingGroupInfo(findingGroup,
						detailedDescription));
			}
		}

		return findingGroup;
	}

	/**
	 * Constructs the finding group name from the rule ID and rule description
	 * (may be null)
	 */
	protected String getFindingGroupName(String ruleId, String ruleDescription) {
		if (!StringUtils.isEmpty(ruleDescription)) {
			return ruleId + ": " + ruleDescription;
		}
		return ruleId;
	}

	/**
	 * Template method to obtain a human readable description for the specified
	 * rule. If this returns null or an empty string, only the rule is used for
	 * finding groups' names.
	 */
	protected abstract String obtainRuleDescription(String ruleId)
			throws ConQATException;

	/**
	 * Returns the detailed description for a rule. May return null if no
	 * description is available. Default implementation returns null.
	 */
	@SuppressWarnings("unused")
	protected String obtainDetailedDescription(String ruleId)
			throws ConQATException {
		return null;
	}

	/**
	 * Creates a uniform path from a location. This is performed by checking the
	 * {@link #prefixToProject} table for prefixes.
	 */
	private String resolveUniformPath(String location) {
		location = UniformPathUtils.normalizeAllSeparators(location);
		if (caseInsensitive) {
			location = location.toLowerCase();
		}

		for (int i = 0; i < prefixToProject.size(); ++i) {
			String prefix = prefixToProject.getFirst(i);
			if (caseInsensitive) {
				prefix = prefix.toLowerCase();
			}

			if (location.startsWith(prefix)) {
				location = StringUtils.stripPrefix(prefix, location);
				location = StringUtils.stripPrefix(UniformPathUtils.SEPARATOR,
						location);
				return prefixToProject.getSecond(i)
						+ UniformPathUtils.SEPARATOR + location;
			}
		}
		return location;
	}

	/**
	 * Creates a finding with a file location. This ensures that the path can be
	 * resolved. Returns null if no finding was created.
	 */
	protected Finding createFindingForFileLocation(String ruleId,
			String message, String locationInReport) throws ConQATException {

		ITextElement element = obtainElementAndReportProblem(locationInReport);
		if (element == null) {
			return null;
		}

		ElementLocation location = new ElementLocation(element.getLocation(),
				element.getUniformPath());
		return createFinding(ruleId, message, location, element);
	}

	/**
	 * Creates and returns a finding for a single code line. This ensures that
	 * the path can be resolved. Returns null if no finding was created.
	 * 
	 * @param rawLineNumber
	 *            the 1-based line number in the unfiltered text content.
	 */
	protected Finding createLineFinding(String ruleId, String message,
			String locationInReport, int rawLineNumber) throws ConQATException {
		return createLineRegionFinding(ruleId, message, locationInReport,
				rawLineNumber, rawLineNumber);
	}

	/**
	 * Creates and returns a finding for a region of code lines. This ensures
	 * that the path can be resolved. Returns null if no finding was created.
	 * 
	 * @param rawFirstLine
	 *            the 1-based inclusive start line for the finding in the
	 *            unfiltered text content.
	 * @param rawLastLine
	 *            the 1-based inclusive end line for the finding in the
	 *            unfiltered text content.
	 * 
	 */
	protected Finding createLineRegionFinding(String ruleId, String message,
			String locationInReport, int rawFirstLine, int rawLastLine)
			throws ConQATException {

		ITextElement element = obtainElementAndReportProblem(locationInReport);
		if (element == null) {
			return null;
		}

		int unfilteredStartOffset = element
				.convertUnfilteredLineToOffset(rawFirstLine);
		int unfilteredEndOffset = element
				.convertUnfilteredLineToOffset(rawLastLine + 1) - 1;

		if (findingFiltered(unfilteredStartOffset, unfilteredEndOffset, element)) {
			return null;
		}

		TextRegionLocation location = new TextRegionLocation(
				obtainElementAndReportProblem(locationInReport).getLocation(),
				obtainElementAndReportProblem(locationInReport)
						.getUniformPath(), unfilteredStartOffset,
				unfilteredEndOffset, rawFirstLine, rawLastLine);
		return createFinding(ruleId, message, location,
				obtainElementAndReportProblem(locationInReport));
	}

	/**
	 * Create finding for specified rule and message. The method also checks if
	 * the element is ignored. If the element is ignored no finding will be
	 * created and <code>null</code> will be returned.
	 */
	protected Finding createFinding(String ruleId, String message,
			ElementLocation location, ITextElement element)
			throws ConQATException {
		if (ResourceTraversalUtils.isIgnored(element, ignoreKeys)) {
			return null;
		}
		Finding finding = getFindingGroup(ruleId).createFinding(location);
		finding.setValue(EFindingKeys.MESSAGE.toString(), message);
		NodeUtils.getOrCreateFindingsList(element, findingsKey).add(finding);
		findingsCreated += 1;
		return finding;
	}

	/**
	 * We consider a finding filtered, if all of its content is filtered
	 */
	private static boolean findingFiltered(int unfilteredStartOffset,
			int unfilteredEndOffset, ITextElement textElement)
			throws ConQATException {

		int filteredStartOffset = textElement
				.getFilteredOffset(unfilteredStartOffset);
		int filteredEndOffset = textElement
				.getFilteredOffset(unfilteredEndOffset);

		boolean findingContentEmpty = filteredEndOffset == filteredStartOffset;

		// If the finding is for some reason on an empty line, we get the same
		// start and end offset, independent of whether actual filtering
		// occurred. We thus also need to check if at least one character is
		// filtered
		return findingContentEmpty
				&& textElement.isFilteredOffset(unfilteredStartOffset);
	}

	/** This handles error cause by an unresolvable path. */
	private void handleUnknownElement(String locationInReport)
			throws ConQATException {
		unknownLocations.add(locationInReport);
		String message = "Could not match location found in report ("
				+ locationInReport + ") to an element in the scope";
		handleError(message);
	}

	/**
	 * This handles an error according to the behavior specified by the
	 * {@link #lenient} flag.
	 */
	protected void handleError(String message) throws ConQATException {
		if (lenient) {
			getLogger().log(logLevel, message);
		} else {
			throw new ConQATException(message);
		}
	}

	/**
	 * Obtains and element for the given path. If no element was found, the
	 * error is handled and null is returned.
	 */
	protected ITextElement obtainElementAndReportProblem(String locationInReport)
			throws ConQATException {
		ITextElement element = obtainElement(locationInReport);
		locationsProcessed++;
		if (element == null) {
			handleUnknownElement(locationInReport);
		}
		return element;
	}

	/**
	 * Obtains and element for the given path. If no element was found, null is
	 * returned.
	 */
	public ITextElement obtainElement(String locationInReport) {
		String lookUpString;

		if (resolveViaUniformPath()) {
			lookUpString = resolveUniformPath(locationInReport);
		} else {
			lookUpString = ResourceTraversalUtils
					.normalizeLocation(locationInReport);
		}

		if (caseInsensitive) {
			lookUpString = lookUpString.toLowerCase();
		}

		return elementLocationMap.get(lookUpString);
	}

	/**
	 * Checks if the location in the report can be resolved, i.e. is present in
	 * the scope.
	 */
	protected boolean isValidLocation(String locationInReport) {
		return obtainElement(locationInReport) != null;
	}

	/** Determines whether a finding type is included or not */
	protected boolean ignoreFindingType(String type) {
		boolean included = includeTypes == null
				|| includeTypes.matchesAny(type);
		boolean excluded = excludeTypes != null
				&& excludeTypes.matchesAny(type);
		return !included || excluded;
	}

	/**
	 * Returns a {@link SlimmingLogger}. We overwrite this method as most
	 * programmers are used to access the logger via method
	 * <code>getLogger()</code> and would, hence, be prone to access the
	 * non-slimming logger.
	 */
	@Override
	protected SlimmingLogger getLogger() {
		if (logger == null) {
			logger = new SlimmingLogger(super.getLogger());
		}
		return logger;
	}

	/** {@inheritDoc} */
	@Override
	protected void processInput(E input) throws ConQATException {
		if (findingsKey == null) {
			findingsKey = categoryName;
		}

		setUp(input);

		NodeUtils.addToDisplayList(input, findingsKey);
		findingCategory = NodeUtils.getFindingReport(input)
				.getOrCreateCategory(categoryName);

		if (resolveViaUniformPath()) {
			elementLocationMap = ResourceTraversalUtils
					.createUniformPathToElementMap(input, ITextElement.class);
		} else {
			elementLocationMap = ResourceTraversalUtils
					.createLocationToElementMap(input, ITextElement.class);
		}

		if (caseInsensitive) {
			elementLocationMap = ResourceTraversalUtils
					.toLowercase(elementLocationMap);
		}

		if (extendedMappingEnabled) {
			elementLocationMap = extendPathMapBySuffixes(elementLocationMap);
		}

		createFindings(input);

		getLogger().info(
				"Processed " + locationsProcessed + " locations in report");
		getLogger().info(
				new ListStructuredLogMessage(unknownLocations.size()
						+ " locations could not be resolved", unknownLocations,
						FINDING_CREATION_LOGGING_TAG));
		getLogger().info(findingsCreated + " findings created.");
	}

	/**
	 * Add all suffixes of existing paths in the elementMap to the map. Doesn't
	 * add suffixes that are not unique. Newly added suffixes are added using
	 * their normalized location.
	 */
	public static Map<String, ITextElement> extendPathMapBySuffixes(
			Map<String, ITextElement> elementMap) {
		CounterSet<String> pathCounter = new CounterSet<String>();
		Map<String, ITextElement> copyMap = new HashMap<String, ITextElement>(
				elementMap);
		for (Entry<String, ITextElement> entry : copyMap.entrySet()) {
			String path = entry.getKey();
			ITextElement pathElement = entry.getValue();

			path = UniformPathUtils.createSystemIndependentPath(path);

			if (!elementMap.containsKey(path)) {
				elementMap.put(ResourceTraversalUtils.normalizeLocation(path),
						pathElement);
				pathCounter.inc(path);
			}

			// The paths are normalized at this step so we can safely split by
			// forward slash
			while (path.contains(UniformPathUtils.SEPARATOR)) {
				int nextSlash = path.indexOf(UniformPathUtils.SEPARATOR);
				path = path.substring(nextSlash + 1);
				elementMap.put(path, pathElement);
				pathCounter.inc(path);
			}
		}

		return removeUnuniqueEntries(elementMap, pathCounter);
	}

	/**
	 * Removes elements from the elementMap that have a count > 1 in the
	 * pathCounter
	 */
	private static Map<String, ITextElement> removeUnuniqueEntries(
			Map<String, ITextElement> elementMap, CounterSet<String> pathCounter) {
		for (String path : pathCounter.getKeysByValueDescending()) {
			if (pathCounter.getValue(path) > 1) {
				elementMap.remove(path);
			} else {
				// No longer have values > 1
				break;
			}
		}
		return elementMap;
	}

	/** Returns the default value for the extendedMappingEnabled parameter */
	protected boolean getExtendetMappingEnabledDefault() {
		return false;
	}

	/**
	 * Returns the default log-level that is used if no log-level is explicitly
	 * set by the user. By default this is to log with level <code>ERROR</code>.
	 * If deriving classes want to use a different default log-level they should
	 * overwrite this method.
	 */
	protected ELogLevel getDefaultLogLevel() {
		return ELogLevel.ERROR;
	}

	/** Set up processor. Default implementation is empty. */
	@SuppressWarnings("unused")
	protected void setUp(E input) throws ConQATException {
		// nothing to do
	}

	/** The method can be implemented to create findings by subclasses. */
	protected abstract void createFindings(E input) throws ConQATException;
}