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
package org.conqat.engine.simulink.clones.normalize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.conqat.lib.commons.clone.IDeepCloneable;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.simulink.model.SimulinkBlock;
import org.conqat.lib.simulink.model.SimulinkLine;
import org.conqat.lib.simulink.model.SimulinkPortBase;

/**
 * A basic Simulink normalizer implementation which can be customized to some
 * degree.
 * 
 * @author $Author:hummelb $
 * @version $Rev: 35176 $
 * @ConQAT.Rating GREEN Hash: 5702CDEB2A12B4B17BA0F203C6751E2F
 */
@AConQATProcessor(description = "A basic Simulink normalizer.")
public class BasicSimulinkNormalizer extends ConQATProcessorBase implements
		ISimulinkNormalizer {

	/** Value: {@value} */
	private static final String TYPE_NAME = "type";

	/** Value: {@value} */
	private static final String TYPE_DESC = "The block type used. "
			+ "If you want to specify the subtype of a 'Reference' block, "
			+ "append it separated by a dot. So for the TargetLink constant "
			+ "block you would use 'Reference.TL_Constant'.";

	/** Value: {@value} */
	private static final String ATTR_NAME = "attr";

	/** Value: {@value} */
	private static final String ATTR_DESC = "The relevant (i.e. used for "
			+ "normalization) attributes, separated by pipes '|'.";

	/** Value: {@value} */
	private static final String PNORM_NAME = "pnorm";

	/** Value: {@value} */
	private static final String PNORM_DESC = "The normalization used for ports "
			+ "of this block.";

	/** Value: {@value} */
	private static final String WEIGHT_NAME = "weight";

	/** Value: {@value} */
	private static final String WEIGHT_DESC = "The weight used for the block. Must not be negative.";

	/** Separator between attributes used in the normalization. */
	private static final String SEPARATOR = "|#|";

	/** Mapping from block types to settings used. */
	private final Map<String, NormalizationSettings> blockSettings = new HashMap<String, NormalizationSettings>();

	/** Settings used for the default case. */
	private NormalizationSettings fallbackSettings = null;

	/** Add a normalization setting. */
	@AConQATParameter(name = "normalize", description = ""
			+ "Gives the normalization settings for a single block type.")
	public void addNormalizationSettings(
			@AConQATAttribute(name = TYPE_NAME, description = TYPE_DESC) String blockType,
			@AConQATAttribute(name = ATTR_NAME, description = ATTR_DESC) String attributesUsed,
			@AConQATAttribute(name = PNORM_NAME, description = PNORM_DESC) EPortNormalization portNormalization,
			@AConQATAttribute(name = WEIGHT_NAME, defaultValue = "1", description = WEIGHT_DESC) int weight)
			throws ConQATException {
		if (blockSettings.containsKey(blockType)) {
			throw new ConQATException("Block type given twice: " + blockType);
		}

		if (weight < 0) {
			throw new ConQATException("Negative weight not supported!");
		}

		blockSettings.put(blockType, new NormalizationSettings(attributesUsed,
				portNormalization, weight));
	}

	/** Add fallback settings. */
	@AConQATParameter(name = "fallback", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Gives settings used for blocks not explicitly specified. If this is omitted, unknown blocks will cause exceptions.")
	public void setFallbackSettings(
			@AConQATAttribute(name = ATTR_NAME, description = ATTR_DESC) String attributesUsed,
			@AConQATAttribute(name = PNORM_NAME, description = PNORM_DESC) EPortNormalization portNormalization,
			@AConQATAttribute(name = WEIGHT_NAME, defaultValue = "1", description = WEIGHT_DESC) int weight)
			throws ConQATException {
		if (weight < 0) {
			throw new ConQATException("Negative weight not supported!");
		}

		fallbackSettings = new NormalizationSettings(attributesUsed,
				portNormalization, weight);
	}

	/** {@inheritDoc} */
	@Override
	public ISimulinkNormalizer process() {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public int determineWeight(SimulinkBlock block) throws ConQATException {
		return determineSettings(block).weight;
	}

	/** {@inheritDoc} */
	@Override
	public String normalizeBlock(SimulinkBlock block) throws ConQATException {
		StringBuilder sb = new StringBuilder();
		sb.append(block.getResolvedType());
		for (String attributeName : determineSettings(block).includedAttributes) {
			sb.append(SEPARATOR);
			sb.append(attributeName);
			sb.append(SEPARATOR);
			String value = block.getParameter(attributeName);
			if (value == null) {
				throw new ConQATException("The required attribute '"
						+ attributeName + "' does not exist for block '"
						+ block.getName() + "' of type "
						+ block.getResolvedType());
			}
			sb.append(value);
		}

		return sb.toString();
	}

	/** {@inheritDoc} */
	@Override
	public String normalizeLine(SimulinkLine line) throws ConQATException {
		return normalizePort(line.getSrcPort()) + " [->] "
				+ normalizePort(line.getDstPort());
	}

	/** Normalizes the given port. */
	private String normalizePort(SimulinkPortBase port) throws ConQATException {
		SimulinkBlock block = port.getBlock();
		NormalizationSettings settings = determineSettings(block);
		try {
			int index = Integer.parseInt(port.getIndex());
			return settings.portNormalization.normalize(index, block);
		} catch (NumberFormatException e) {
			// Never normalize special ports.
			return port.getIndex();
		}
	}

	/** Returns the settings to be used for the given block. */
	private NormalizationSettings determineSettings(SimulinkBlock block)
			throws ConQATException {
		NormalizationSettings settings = blockSettings.get(block
				.getResolvedType());
		if (settings != null) {
			return settings;
		}

		// retry with plain type
		settings = blockSettings.get(block.getType());
		if (settings != null) {
			return settings;
		}

		if (fallbackSettings != null) {
			return fallbackSettings;
		}

		throw new ConQATException("Found no normalization settings for block "
				+ block.getName() + " of type " + block.getResolvedType());
	}

	/** A class for managing normalization settings. */
	private static class NormalizationSettings {

		/** The attributes included for the normalization. */
		private final List<String> includedAttributes = new ArrayList<String>();

		/** The normalization used for the ports. */
		private final EPortNormalization portNormalization;

		/** The weight used for the block. */
		private final int weight;

		/** Constructor. */
		public NormalizationSettings(String attributesUsed,
				EPortNormalization portNormalization, int weight) {
			this.portNormalization = portNormalization;
			this.weight = weight;
			parseAttributes(attributesUsed);
		}

		/**
		 * Parse the given attribute string and fill {@link #includedAttributes}
		 * .
		 */
		private void parseAttributes(String attributesUsed) {
			for (String attr : attributesUsed.split(Pattern.quote("|"))) {
				if (!StringUtils.isEmpty(attr)) {
					includedAttributes.add(attr.trim());
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Returns <code>this</code>
	 */
	@Override
	public IDeepCloneable deepClone() {
		return this;
	}
}