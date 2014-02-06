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
package org.conqat.engine.code_clones.core.report.enums;

/**
 * Enumeration for three-values answers. Useful to perform studies that involve
 * manual developer ratings. Such studies typically contribute their views and
 * columns via a separate plugin that uses the extension points from this
 * plugin. To avoid classloader problems when opening a clone report however,
 * this class resides here and not in one of those plugins.
 * 
 * @author juergens
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: 9DBFA360ABBC5561F78277B20F88E1E7
 */
public enum EAnswer {

	/** Yes value */
	YES,

	/** No value */
	NO,

	/** Don't know */
	DONT_KNOW,

	/** Not yet decided */
	UNRATED
}