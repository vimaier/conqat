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
package org.conqat.engine.code_clones.core.report;

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.engine.code_clones.core.CloneClass;

/**
 * Attributes for the XML result report.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43764 $
 * @ConQAT.Rating GREEN Hash: AE688279491BCDDFE7A2056A0F1A2701
 */
public enum ECloneReportAttribute {

	/** Id of clone class */
	id,

	/** Clone class rating */
	rating,

	/** Length of source file in LOC */
	length,

	/** See {@link CloneClass#getNormalizedLength()} */
	normalizedLength,

	/**
	 * Fingerprint of source file or clone class. (See
	 * {@link CloneClass#getFingerprint()})
	 */
	fingerprint,

	/** 1-based start line (inclusive). */
	startLine,

	/** 1-based end line (inclusive). */
	endLine,

	/** 0-based start offset (inclusive). */
	startOffset,

	/** 0-based end offset (inclusive). */
	endOffset,

	/** Gap information in gapped clone */
	gaps,

	/**
	 * Location of the source file. This is optional. If it is not set, we use
	 * the content stored for {@link #path}.
	 */
	location,

	/** Uniform path of source file. */
	path,

	/** Id of source file */
	sourceFileId,

	/** See {@link Clone#getStartUnitIndexInElement()} */
	startUnitIndexInFile,

	/** See {@link Clone#getLengthInUnits()} */
	lengthInUnits,

	/** See {@link Clone#getDeltaInUnits()} */
	deltaInUnits,

	/** Namespace attribute */
	xmlns,

	/** Clone class value mechanism key attribute */
	key,

	/** Clone class value mechanism value attribute */
	value,

	/** Clone class value mechanism type attribute */
	type,

	/** Date denoting the system version on which clone detection was performed */
	systemdate,

	/** Globally unique id attribute */
	uniqueId;

}