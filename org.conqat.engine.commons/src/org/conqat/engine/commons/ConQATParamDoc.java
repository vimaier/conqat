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
package org.conqat.engine.commons;

import java.text.SimpleDateFormat;

import org.conqat.engine.core.core.IConQATProcessor;

/**
 * This is a class collecting the names and description strings of commonly used
 * parameters and their attributes.
 * 
 * @author $Author: juergens $
 * @version $Rev: 42129 $
 * @ConQAT.Rating YELLOW Hash: A11D326F76B49D341B27DD51BAB4AE60
 */
public abstract class ConQATParamDoc implements IConQATProcessor {

	/** Name of the input parameter. */
	public static final String INPUT_NAME = "input";

	/** Description of the input parameter. */
	public static final String INPUT_DESC = "The input this processor works on.";

	/** Name of the ref attribute for the input parameter. */
	public static final String INPUT_REF_NAME = "ref";

	/** Description of the ref attribute for the input parameter. */
	public static final String INPUT_REF_DESC = "Reference to the generating processor.";

	/** Name of the enable parameter. */
	public static final String ENABLE_NAME = "enable";

	/** Description of the enable parameter. */
	public static final String ENABLE_DESC = "If set to true, processor is enabled. If disabled, no action is performed.";

	/** Name of the processor attribute for the enable parameter. */
	public static final String ENABLE_PROCESSOR_NAME = "processor";

	/** Name of the readkey parameter. */
	public static final String READKEY_NAME = "read";

	/** Description of the readkey parameter. */
	public static final String READKEY_DESC = "The key to read from.";

	/** Name of the key attribute for the readkey parameter. */
	public static final String READKEY_KEY_NAME = "key";

	/** Description of the key attribute for the readkey parameter. */
	public static final String READKEY_KEY_DESC = "The name of the key.";

	/** Name of the writekey parameter. */
	public static final String WRITEKEY_NAME = "write";

	/** Description of the writekey parameter. */
	public static final String WRITEKEY_DESC = "The key to write to.";

	/** Name of the aggregation strategy parameter. */
	public static final String AGG_STRATEGY_NAME = "aggregation";

	/** Description of the aggregation strategy parameter. */
	public static final String AGG_STRATEGY_DESC = "Define aggregation strategy.";

	/** Name of the strategy attribute. */
	public static final String STRATEGY_NAME = "strategy";

	/** Description of the strategy attribute. */
	public static final String STRATEGY_DESC = "Enum value for aggregation strategy.";

	/** Name of the key attribute for the writekey parameter. */
	public static final String WRITEKEY_KEY_NAME = READKEY_KEY_NAME;

	/** Description of the key attribute for the writekey parameter. */
	public static final String WRITEKEY_KEY_DESC = READKEY_KEY_DESC;

	/** Name of the auth parameter. */
	public static final String AUTH_NAME = "auth";

	/** Description of the auth parameter. */
	public static final String AUTH_DESC = "The authentification information.";

	/** Name of the user attribute for the auth parameter. */
	public static final String AUTH_USER_NAME = "user";

	/** Description of the user attribute for the auth parameter. */
	public static final String AUTH_USER_DESC = "The username";

	/** Name of the pass attribute for the auth parameter. */
	public static final String AUTH_PASS_NAME = "pass";

	/** Description of the pass attribute for the auth parameter. */
	public static final String AUTH_PASS_DESC = "The password.";

	/** Name of the user attribute for the auth parameter. */
	public static final String INCLUDE_NAME = "include";

	/** Name of the user attribute for the auth parameter. */
	public static final String EXCLUDE_NAME = "exclude";

	/** Name of an attribute describing an ant pattern. */
	public static final String ANT_PATTERN_NAME = "pattern";

	/** Description of an attribute describing an ant pattern. */
	public static final String ANT_PATTERN_DESC = "A pattern as defined by http://ant.apache.org/manual/dirtasks.html#patterns";

	/** Name of an attribute describing an ant pattern. */
	public static final String HTML_COLOR_NAME = "color";

	/** Description of an attribute describing an ant pattern. */
	public static final String HTML_COLOR_DESC = "The color using the #RRGGBB format known from HTML.";

	/** Description of an attribute accepting a Java RegEx pattern. */
	public static final String REGEX_PATTERN_DESC = "A regular expression as described in the Java API documentation at "
			+ "http://java.sun.com/j2se/1.5.0/docs/api/index.html";

	/**
	 * Description of an attribute accepting a {@link SimpleDateFormat} pattern.
	 */
	public static final String DATE_PATTERN_DESC = "The date pattern as specified by http://java.sun.com/javase/6/docs/api/java/text/SimpleDateFormat.html";

	/** Name of a parameter to specify predecessors */
	public static final String PREDECESSOR_NAME = "predecessor";

	/** Description of a parameter to specify predecessors */
	public static final String PREDECESSOR_NAME_DESC = "Processor that should be executed before this processor gets executed";

	/** Name of an attribute referencing a predecessor */
	public static final String PREDECESSOR_REF_NAME = INPUT_REF_NAME;

	/** Description of an attribute referencing a predecessor */
	public static final String PREDECESSOR_REF_DESC = "Reference to the predecessor";

	/** Name of the encoding parameter. */
	public static final String ENCODING_PARAM_NAME = "encoding";

	/** Description of the encoding parameter. */
	public static final String ENCODING_PARAM_DESC = "Set encoding for files"
			+ " in this scope [default encoding is used if not set].";

	/** Name of the encoding attribute. */
	public static final String ENCODING_ATTR_NAME = "name";

	/** Description of the encoding attribute. */
	public static final String ENCODING_ATTR_DESC = "Name of the encoding";

	/** String that describes the type of a finding list. */
	public static final String FINDING_LIST_TYPE = "java.util.List<org.conqat.engine.commons.findings.Finding>";

	/** Name of repetition min length parameter */
	public static final String REPETITION_MIN_LENGTH_NAME = "length";

	/** Description of repetition min length parameter */
	public static final String REPETITION_MIN_LENGTH_DESC = "Minimal number of statements contained in repetition. Must be > 0.";

	/** Name of repetition min instances parameter */
	public static final String REPETITION_MIN_INSTANCES_NAME = "instances";

	/** Description of repetition min instances parameter */
	public static final String REPETITION_MIN_INSTANCES_DESC = "Minimal required number of motif instances in repetition. Must be >= 2.";

	/** Name of repetition min motif length parameter */
	public static final String REPETITION_MIN_MOTIF_LENGTH_NAME = "min-motif-length";

	/** Description of repetition min motig length parameter */
	public static final String REPETITION_MIN_MOTIF_LENGTH_DESC = "Length of shortest repetition motif being searched for. Must be > 0.";

	/** Name of repetition max motif length parameter */
	public static final String REPETITION_MAX_MOTIF_LENGTH_NAME = "max-motif-length";

	/** Description of repetition max motif length parameter */
	public static final String REPETITION_MAX_MOTIF_LENGTH_DESC = "Length of longest repetition motif being searched for. Must be >= min motig length.";

	/** Name of regex regions */
	public static final String REGEX_REGIONS_NAME = "mark";

	/** Description of regex regions name */
	public static final String REGEX_REGIONS_DESC = "Parameters for region recognition.";

	/** Name of regex regions patterns */
	public static final String REGEX_REGIONS_PATTERNS_NAME = "patterns";

	/** Description of regex regions patterns */
	public static final String REGEX_REGIONS_PATTERNS_DESC = "Reference to the pattern list used.";

	/** Name of regex regions origin */
	public static final String REGEX_REGIONS_ORIGIN_NAME = "origin";

	/** Description of regex regions origin */
	public static final String REGEX_REGIONS_ORIGIN_DESC = "The name used for the origin.";

	/** Name of regex regions start at file begin */
	public static final String REGEX_REGIONS_START_AT_FILE_BEGIN_NAME = "start-at-file-begin";

	/** Description of regex regions start at file begin */
	public static final String REGEX_REGIONS_START_AT_FILE_BEGIN_DESC = "The name used for the origin.";

	/** Name of block marker patterns */
	public static final String BLOCK_MARKER_PATTERNS_NAME = "patterns";

	/** Description of block marker patterns */
	public static final String BLOCK_MARKER_PATTERNS_DESC = "Patterns that match block start. Each pattern must end with '\\{' to make sure "
			+ "that it matches a block start.";

	/** Name of pattern list parameter */
	public static final String PATTERN_LIST = "pattern-list";

	/** Description of pattern list parameter */
	public static final String PATTERN_LIST_DESC = "List of patterns.";

	/** Finding parameter. */
	public static final String FINDING_NAME = "name";

	/** Finding key attribute. */
	public static final String FINDING_KEY_NAME = "key";

	/** Finding key description. */
	public static final String FINDING_KEY_DESC = "The key used for storing the findings in.";

	/** Name of parameter that determines whether to draw legend */
	public static final String DRAW_LEGEND_PARAM = "legend";

	/** Name of attribute that determines whether to draw legend */
	public static final String DRAW_LEGEND_ATTRIBUTE = "draw";

	/** Description of parameter */
	public static final String DRAW_LEGEND_DESC = "Flag that determines whether or not to draw a legend. Default is true.";

	/** Name of finding parameter */
	public static final String FINDING_PARAM_NAME = "finding";

	/** Parameter for finding group */
	public static final String FINDING_GROUP_NAME = "group";

	/** Parameter for finding category */
	public static final String FINDING_CATEGORY_NAME = "category";

	/** Parameter for finding message */
	public static final String FINDING_MESSAGE_NAME = "message";

	/** Parameter name for path transformations. */
	public static final String PATH_TRANSFORMATION_PARAM = "path-transformation";

	/** Attribute name for path transformations. */
	public static final String PATH_TRANSFORMATION_ATTRIBUTE = INPUT_REF_NAME;

	/**
	 * Description for path transformations. Note that this is specific for
	 * diffing processors as this mentions a comparee.
	 */
	public static final String PATH_TRANSFORMATION_DESCRIPTION = "If this parameter is set, the transformation is applied to the uniform paths of the comparee elements before matching elements.";

	/** Parameter doc for finding.key */
	public static final String FINDING_KEYS_PARAM_DOC = "Adds a key under which to search for findings. "
			+ "If no keys are given, all keys from the display list will be searched.";

	/** Parameter name for filter inversion */
	public static final String INVERT_NAME = "invert";

	/** Attribute value for value parameter. */
	public static final String INVERT_VALUE_NAME = "value";

	/** Parameter doc for filter inversion */
	public static final String INVERT_PARAM_DOC = "If set to true, filter is inverted. Default: false.";

	/** The default name of the build configuration */
	public static final String DEFAULT_CONFIGURATION_NAME = "Debug";

	/** The default description for the build configuration name */
	public static final String DEFAULT_CONFIGURATION_NAME_DESC = "Name of the configuration, e.g. 'Debug' or 'Release'";

	/** The platform used by default */
	public static final String DEFAULT_PLATFORM = "AnyCPU";

	/** The default description for the platform used by default */
	public static final String DEFAULT_PLATFORM_DESC = "Name of the platform, e.g. 'AnyCPU'";

	/** Default name for primitive values. */
	public static final String ATTRIBUTE_VALUE_NAME = "value";

	/** Log level parameter. */
	public static final String LOG_LEVEL_NAME = "log-level";

	/** Log level description. */
	public static final String LOG_LEVEL_DESCRIPTION = "This allows to specify the log level used for logging messages. Use OFF to turn off logging.";

	/** Name of the ignore parameter. */
	public static final String IGNORE_NAME = "ignore";

	/** Description of the ignore parameter. */
	public static final String IGNORE_DESC = "Key under which a ignore flag is stored.";

	/** Name of the ignore key. */
	public static final String IGNORE_KEY_NAME = "key";

	/** Description of the ignore key. */
	public static final String IGNORE_KEY_DESC = "If no key is given, no elements are ignored.";

	/** Name of the value key */
	public static final String VALUE_KEY_NAME = "value";

	/** Description of key in which string value of an object is stored */
	public static final String STRING_VALUE_KEY_DESC = "String representation of value, e.g. 5 for an integer";

	/** Name of type key */
	public static final String TYPE_KEY_NAME = "type";

	/** Description of type key */
	public static final String TYPE_KEY_DESC = "Type of value (e.g. java.lang.String)";

	/** Name of inclusion predicate parameter */
	public static final String INCLUSION_PREDICATE_PARAM = "inclusion";

	/** Name of inclusion predicate attribute */
	public static final String INCLUSION_PREDICATE_ATTRIBUTE = "predicate";

	/** Description of inclusion predicate parameter */
	public static final String INCLUSION_PREDICATE_DESC = "If set, only nodes that are contained in the predicate are processed.";

}