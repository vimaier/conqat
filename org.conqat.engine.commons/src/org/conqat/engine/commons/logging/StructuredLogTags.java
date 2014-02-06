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
package org.conqat.engine.commons.logging;

import org.conqat.engine.commons.config.KeyedConfig;

/**
 * A list of common tags used for structured logging. We do not use an
 * enumeration here, as we want to allow extension of the set of tags by third
 * party bundles.
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 42122 $
 * @ConQAT.Rating YELLOW Hash: A1F5880D5B623059F5C7E6E46FD07474
 */
public class StructuredLogTags {

	/** Marker for scopes. */
	public static final String SCOPE = "scope";

	/** Marker for pattern. */
	public static final String PATTERN = "pattern";

	/** Marker for file/element lists. */
	public static final String FILES = "files";

	/** Marker for describing keys of a {@link KeyedConfig}. */
	public static final String CONFIG_KEY = "config-key";

	/** Marker for describing the actual value used from a {@link KeyedConfig}. */
	public static final String CONFIG_VALUE = "config-value";

	/** Marker for errors leveraging from execution of 3rd party tools. */
	public static final String THIRD_PARTY_TOOL = "3rd-party-tool";
}