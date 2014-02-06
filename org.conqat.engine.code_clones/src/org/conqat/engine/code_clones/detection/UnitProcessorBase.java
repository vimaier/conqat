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
package org.conqat.engine.code_clones.detection;

import java.util.List;

import org.conqat.engine.code_clones.core.Unit;
import org.conqat.engine.code_clones.normalization.provider.IUnitProvider;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.util.ConQATInputProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Base class for processors that work on units.
 * 
 * @author ladmin
 * @author $Author: juergens $
 * @version $Rev: 41413 $
 * @ConQAT.Rating GREEN Hash: F9D41A839BEBB45DD91281D8A82363FE
 */
public abstract class UnitProcessorBase extends
		ConQATInputProcessorBase<ITextResource> {

	/** Key that is used to store the number of units in an element */
	public static final String UNITS_KEY = "Units";

	/**
	 * {@link IUnitProvider} that provides units on which the clone detection is
	 * performed. Serves as normalization strategy.
	 */
	protected IUnitProvider<ITextResource, Unit> iUnitProvider;

	/**
	 * If this string is set to a non-empty value, a debug file (containing the
	 * normalized units) is written for each input file.
	 */
	protected String debugFileExtension = null;

	/**
	 * Key that contains flag that determines whether elements get ignored.
	 * Influences log message generation, but not unit draining.
	 */
	protected String ignoreKey;

	/** Flag that determines whether the units are stored in clones */
	protected boolean storeUnits = false;

	/** ConQAT Parameter */
	@AConQATParameter(name = "debug", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "If this string is set to a non-empty value, a debug file is written for each input element")
	public void setDebugFileExtension(
			@AConQATAttribute(name = "extension", description = "File extension") String debugFileExtension) {
		if (StringUtils.isEmpty(debugFileExtension)) {
			throw new IllegalArgumentException(
					"Empty debug file extension not allowed, since it would overwrite existing files.");
		}
		if (!debugFileExtension.startsWith(".")) {
			debugFileExtension = "." + debugFileExtension;
		}
		this.debugFileExtension = debugFileExtension;
	}

	/**
	 * Sets normalization strategy.
	 * <p>
	 * In general, ConQAT load time type checking does not check generic
	 * parameters. This is no problem in this case, as {@link ITextResource} and
	 * {@link Unit} are the most general instantiations of the generic
	 * parameters for {@link IUnitProvider}
	 */
	@AConQATParameter(name = "normalization", description = ConQATParamDoc.INPUT_DESC, minOccurrences = 1, maxOccurrences = 1)
	public void setNormalization(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) IUnitProvider<ITextResource, Unit> unitProvider) {
		iUnitProvider = unitProvider;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.IGNORE_NAME, minOccurrences = 0, maxOccurrences = 1, description = ConQATParamDoc.IGNORE_DESC)
	public void setIgnoreKey(
			@AConQATAttribute(name = ConQATParamDoc.IGNORE_KEY_NAME, description = ConQATParamDoc.IGNORE_KEY_DESC) String ignoreKey) {
		this.ignoreKey = ignoreKey;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "store", description = "Flag that determines whether units are stored in clones.", minOccurrences = 0, maxOccurrences = 1)
	public void setStoreUnits(
			@AConQATAttribute(name = "units", description = "Increases memory requirements. Default is false.") boolean storeUnits) {
		this.storeUnits = storeUnits;
	}

	/** Create list of units */
	protected void drainUnits(List<Unit> units,
			boolean clearStringPoolAfterElement) throws ConQATException {
		long start = System.currentTimeMillis();
		UnitDrain drain = new UnitDrain(getLogger(), debugFileExtension,
				ignoreKey, clearStringPoolAfterElement);
		drain.drainUnits(input, iUnitProvider, units);
		long duration = System.currentTimeMillis() - start;
		getLogger().info("Unit drain: " + duration + " ms");
	}

	/** Create list of units */
	protected void drainUnits(List<Unit> units) throws ConQATException {
		drainUnits(units, false);
	}

}