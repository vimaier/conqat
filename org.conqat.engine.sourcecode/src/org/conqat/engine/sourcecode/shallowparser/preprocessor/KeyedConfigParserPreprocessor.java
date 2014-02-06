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
package org.conqat.engine.sourcecode.shallowparser.preprocessor;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.config.KeyedConfig;
import org.conqat.engine.commons.config.KeyedConfigValueBase;
import org.conqat.engine.commons.logging.StructuredLogMessage;
import org.conqat.engine.commons.logging.StructuredLogTags;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IResource;
import org.conqat.lib.commons.enums.EnumUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.scanner.ETokenType;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 45115 $
 * @ConQAT.Rating GREEN Hash: AE11FF3E363CFC0F4FBD1A1A242FC119
 */
@AConQATProcessor(description = "A preprocessor that can be configured using the keyed configuration mechanism. "
		+ "Each preprocessing rule is provided as a separate key that starts with a common prefix. "
		+ "The remainder of the key (after the prefix) determines the type of rule. "
		+ "Valid rules are discard-identifier, discard-macro, collapse-macro, true-condition, false-condition, and mapped-identifier. "
		+ "The value is the text of the identifier resp. expression, or (in the last case) a colon separated list of "
		+ "old identifier text, new text, and new token type. "
		+ "Each key may have an arbitrary suffix, to allow differentiation (keys must be unique) and documentation.")
public class KeyedConfigParserPreprocessor extends
		MacroSupportingPreprocessorBase {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = KeyedConfigValueBase.CONFIG_PARAM_NAME, attribute = ConQATParamDoc.INPUT_REF_NAME, description = KeyedConfigValueBase.CONFIG_DESCRIPTION)
	public KeyedConfig config;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = KeyedConfigValueBase.KEY_PARAM_NAME, attribute = "prefix", description = "The prefix of the keys used to configure this preprocessor.")
	public String keyPrefix;

	/** The number of rules parsed. */
	private int ruleCount = 0;

	/** {@inheritDoc} */
	@Override
	protected void setUp(IResource root) throws ConQATException {
		if (!keyPrefix.endsWith(".")) {
			keyPrefix += ".";
		}

		for (String key : config.getKeysWithPrefix(keyPrefix)) {
			ruleCount += 1;
			addRule(StringUtils.stripPrefix(keyPrefix, key), config.get(key));
		}

		getLogger().info(
				new StructuredLogMessage(
						"Parsing preprocessor configuration from keys with prefix "
								+ keyPrefix + " (loaded " + keyPrefix
								+ " keys)", StructuredLogTags.CONFIG_KEY));
	}

	/** Parses a single rule from a key/value pair. */
	private void addRule(String key, String value) throws ConQATException {
		if (key.startsWith("discard-identifier")) {
			addFilteredIdentifier(value);
		} else if (key.startsWith("discard-macro")) {
			addDiscardMacro(value);
		} else if (key.startsWith("collapse-macro")) {
			addCollapseMacro(value);
		} else if (key.startsWith("true-condition")) {
			addTrueCondition(value);
		} else if (key.startsWith("false-condition")) {
			addFalseCondition(value);
		} else if (key.startsWith("mapped-identifier")) {
			String[] parts = value.split(":", 3);
			if (parts.length == 2) {
				addMappedIdentifier(parts[0], parts[1]);
			} else if (parts.length == 3) {
				ETokenType type = EnumUtils.valueOfIgnoreCase(ETokenType.class,
						parts[2]);
				if (type == null) {
					throw new ConQATException("Invalid token type: " + parts[2]);
				}
				addMappedIdentifier(parts[0], parts[1], type);
			} else {
				throw new ConQATException("Invalid mapping specification.");
			}
		} else {
			throw new ConQATException("Unsupported start of key: " + keyPrefix
					+ key);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void visit(IResource node) {
		if (ruleCount == 0) {
			return;
		}

		super.visit(node);
	}
}
