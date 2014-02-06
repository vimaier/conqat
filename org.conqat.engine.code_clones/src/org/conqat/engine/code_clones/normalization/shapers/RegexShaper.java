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
package org.conqat.engine.code_clones.normalization.shapers;

import java.io.Serializable;

import org.conqat.engine.code_clones.core.CloneDetectionException;
import org.conqat.engine.code_clones.core.Unit;
import org.conqat.engine.code_clones.detection.SentinelUnit;
import org.conqat.engine.code_clones.normalization.UnitProviderBase;
import org.conqat.engine.code_clones.normalization.provider.IUnitProvider;
import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.IConQATProcessor;
import org.conqat.engine.core.core.IConQATProcessorInfo;
import org.conqat.engine.resource.text.ITextResource;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 36296 $
 * @ConQAT.Rating GREEN Hash: 1B84CE23A4F3838574082B990308358B
 */
@AConQATProcessor(description = ""
		+ "Inserts sentinels before units that match one of a set of regular expressions. "
		+ "Matching is performed on the textual representation of the units.")
public class RegexShaper extends UnitProviderBase<ITextResource, Unit>
		implements IConQATProcessor, Serializable {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** Underlying provider. */
	private IUnitProvider<ITextResource, Unit> provider;

	/** Patterns. Initialize to empty list in case it does not get set. */
	private final PatternList patterns = new PatternList();

	/** Flag that indicates that the last returned unit was a sentinel */
	private boolean lastWasSentinel = false;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "unit", minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "Unit provider")
	public void setUnitProvider(
			@AConQATAttribute(name = "provider", description = "Unit provider") IUnitProvider<ITextResource, Unit> provider) {
		this.provider = provider;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "patterns", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Patterns matching units before which sentinels are inserted")
	public void setPatterns(
			@AConQATAttribute(name = "ref", description = "Reference to pattern producer") PatternList patterns) {
		this.patterns.addAll(patterns);
	}

	/** {@inheritDoc} */
	@Override
	protected void init(ITextResource root) throws CloneDetectionException {
		provider.init(root, getLogger());
	}

	/** {@inheritDoc} */
	@Override
	protected Unit provideNext() throws CloneDetectionException {
		Unit nextUnit = provider.lookahead(1);
		if (nextUnit == null) {
			return null;
		}

		if (!lastWasSentinel && patterns.matchesAny(nextUnit.getContent())) {
			lastWasSentinel = true;
			return new SentinelUnit(nextUnit.getElementUniformPath());
		}

		lastWasSentinel = false;
		return provider.getNext();
	}

	/** {@inheritDoc} */
	@Override
	public void init(IConQATProcessorInfo processorInfo) {
		// nothing to do
	}

	/** {@inheritDoc} */
	@Override
	public IUnitProvider<ITextResource, Unit> process() {
		return this;
	}

}