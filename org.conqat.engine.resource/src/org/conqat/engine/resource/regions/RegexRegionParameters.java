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
package org.conqat.engine.resource.regions;

import java.io.Serializable;

import org.conqat.engine.commons.pattern.PatternList;

/**
 * Parameter object for processors that recognize regions based on regular
 * expressions.
 * 
 * @author juergens
 * @author $Author: hummelb $
 * @version $Rev: 36296 $
 * @ConQAT.Rating GREEN Hash: 3D61743D59946E41ABD96F5C5C2E5F4E
 */
public class RegexRegionParameters implements Serializable {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** List of regular expressions this processor searches for */
	public final PatternList patterns;

	/**
	 * Origin that gets stored in the regions created by this processor. Default
	 * is processor name
	 */
	public final String origin;

	/** Flag that indicates whether match is interpreted from file start. */
	public final boolean startRegionAtElementBegin;

	/** Constructor. */
	public RegexRegionParameters(PatternList patterns, String origin,
			boolean startRegionAtElementBegin) {
		this.patterns = patterns;
		this.origin = origin;
		this.startRegionAtElementBegin = startRegionAtElementBegin;
	}

}