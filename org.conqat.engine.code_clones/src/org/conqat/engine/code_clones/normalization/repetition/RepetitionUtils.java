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
package org.conqat.engine.code_clones.normalization.repetition;

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.code_clones.core.CloneDetectionException;
import org.conqat.engine.code_clones.core.StatementUnit;
import org.conqat.engine.code_clones.core.TokenUnit;
import org.conqat.engine.code_clones.core.Unit;
import org.conqat.engine.code_clones.normalization.UnitProviderBase;
import org.conqat.engine.code_clones.normalization.provider.ListBasedTokenProvider;
import org.conqat.engine.code_clones.normalization.statement.StatementNormalization;
import org.conqat.engine.code_clones.normalization.token.FilteringTokenProvider;
import org.conqat.engine.code_clones.normalization.token.ITokenProvider;
import org.conqat.engine.code_clones.normalization.token.TokenNormalization;
import org.conqat.engine.code_clones.normalization.token.configuration.ITokenConfiguration;
import org.conqat.engine.code_clones.normalization.token.configuration.TokenConfigurationDef;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.IConQATLogger;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.lib.commons.region.Region;
import org.conqat.lib.scanner.IToken;

/**
 * Utility classes for repetition detection.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43764 $
 * @ConQAT.Rating GREEN Hash: CC2FC2362C4BA731A519B7AB91FC4680
 */
public class RepetitionUtils {

	/**
	 * Creates an array of the {@link StatementUnit}s of an element.
	 */
	public static StatementUnit[] getStatements(ITokenElement element,
			IConQATLogger logger) throws ConQATException,
			CloneDetectionException {

		TokenNormalization normalization = createFullNormalizationFor(element,
				logger);
		StatementNormalization sequencer = new StatementNormalization(
				normalization, true);
		sequencer.init(element, logger);

		return drainStatementsOnly(sequencer).toArray(new StatementUnit[] {});
	}

	/**
	 * Creates an array of the {@link TokenUnit}s of an element.
	 */
	public static TokenUnit[] getTokens(ITokenElement element,
			IConQATLogger logger) throws ConQATException,
			CloneDetectionException {

		TokenNormalization normalization = createFullNormalizationFor(element,
				logger);
		normalization.init(element, logger);

		return drainTokensOnly(normalization).toArray(new TokenUnit[] {});
	}

	/** Creates a region for a repetition */
	public static Region regionFor(Repetition<? extends Unit> repetition) {
		int start = repetition.getStart().getFilteredStartOffset();
		int end = repetition.getEnd().getFilteredEndOffset();
		String origin = "repetition: motif length "
				+ repetition.getMotifLength();

		return new Region(start, end, origin);
	}

	/**
	 * Gets a provider for fully normalized {@link TokenUnit}s for an
	 * {@link ITokenElement}
	 */
	public static TokenNormalization createFullNormalizationFor(
			ITokenElement element, IConQATLogger logger) throws ConQATException {
		// set up token provider chain
		List<IToken> tokens = element.getTokens(logger);
		ITokenProvider rawTokenProvider = new ListBasedTokenProvider(tokens);
		ITokenProvider ignoreFilteredTokenProvider = new FilteringTokenProvider(
				rawTokenProvider);

		// set up and return normalization
		TokenNormalization normalization = new TokenNormalization(
				ignoreFilteredTokenProvider,
				new ArrayList<ITokenConfiguration>(),
				fullNormalizationConfiguration());
		normalization.init(element, logger);
		return normalization;
	}

	/** Creates an {@link ITokenConfiguration} with full normalization */
	private static ITokenConfiguration fullNormalizationConfiguration() {
		TokenConfigurationDef configuration = new TokenConfigurationDef();
		configuration.setAll();
		return configuration.process();
	}

	/**
	 * Drains the list of statements from a {@link StatementNormalization}. All
	 * non-statement units (i.e. sentinels) contained in the normalization are
	 * dropped.
	 */
	/* package */static List<StatementUnit> drainStatementsOnly(
			StatementNormalization normalization)
			throws CloneDetectionException {
		return drainUnitsOfType(normalization, StatementUnit.class);
	}

	/**
	 * Drains the list of token units from a {@link StatementNormalization}. All
	 * non-statement units (i.e. sentinels) contained in the normalization are
	 * dropped.
	 */
	/* package */static List<TokenUnit> drainTokensOnly(
			TokenNormalization normalization) throws CloneDetectionException {
		return drainUnitsOfType(normalization, TokenUnit.class);
	}

	/**
	 * Drains the list of units from a normalization. All units that are not of
	 * the specified type are dropped.
	 */
	@SuppressWarnings("unchecked")
	private static <U extends Unit> List<U> drainUnitsOfType(
			UnitProviderBase<? extends ITextResource, ? extends Unit> normalization,
			Class<U> unitType) throws CloneDetectionException {
		List<U> statements = new ArrayList<U>();
		Unit unit = normalization.getNext();
		while (unit != null) {
			if (unitType.isAssignableFrom(unit.getClass())) {
				statements.add((U) unit);
			}
			unit = normalization.getNext();
		}
		return statements;
	}

}