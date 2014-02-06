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
package org.conqat.engine.commons.config;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.commons.logging.StructuredLogMessage;
import org.conqat.engine.commons.logging.StructuredLogTags;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.reflect.ReflectionUtils;
import org.conqat.lib.commons.reflect.TypeConversionException;

/**
 * Base class for processors that access and return a value of a keyed config.
 * <p>
 * This extensively uses user-level logging to allow transparent documentation
 * in the dashboard.
 * 
 * @param <T>
 *            the type of value provided by this processor.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 38382 $
 * @ConQAT.Rating GREEN Hash: 108B3CE9BBAD9DF2D90B48FB72FB86BE
 */
public abstract class KeyedConfigValueBase<T> extends ConQATProcessorBase {

	/** Doc string. */
	public static final String CONFIG_PARAM_NAME = "config";

	/** Doc string. */
	public static final String CONFIG_DESCRIPTION = "The keyed config to read from.";

	/** Doc string. */
	public static final String KEY_DESCRIPTION = "The name of the key to read from.";

	/** Doc string. */
	public static final String KEY_ATTR_NAME = "name";

	/** Doc string. */
	public static final String KEY_PARAM_NAME = "key";

	/** Doc string. */
	public static final String USAGE_DESCRIPTION = "A comment describing what the key is used for in the configuration.";

	/** Doc string. */
	public static final String USAGE_ATTR_NAME = "comment";

	/** Doc string. */
	public static final String USAGE_PARAM_NAME = "usage";

	/** Prefix used for the processor description. */
	public static final String DESCRIPTION_PREFIX = "Allows to access a ";

	/** Suffix used for the processor description. */
	public static final String DESCRIPTION_SUFFIX = " value from a keyed config.";

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = CONFIG_PARAM_NAME, attribute = ConQATParamDoc.INPUT_REF_NAME, description = CONFIG_DESCRIPTION)
	public KeyedConfig config;

	/**
	 * {@ConQAT.Doc}
	 * <p>
	 * This is not called "read.key", as it is a different kind of key (not
	 * ConQAT node related).
	 */
	@AConQATFieldParameter(parameter = KEY_PARAM_NAME, attribute = KEY_ATTR_NAME, description = KEY_DESCRIPTION)
	public String keyName;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = USAGE_PARAM_NAME, attribute = USAGE_ATTR_NAME, description = USAGE_DESCRIPTION)
	public String comment;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "default", attribute = "value", optional = true, description = "The default value used if the key is not found in the config.")
	public T defaultValue = null;

	/** {@inheritDoc} */
	@Override
	public T process() throws ConQATException {

		logUsedKey();

		String value = config.get(keyName);
		if (value == null) {
			return useDefaultValue();
		}

		logUsedValue(value);
		return convert(value);
	}

	/** Logs the key used and the comment via user-level logging. */
	private void logUsedKey() {
		String message = keyName;
		if (defaultValue != null) {
			message += " [default: " + defaultValue + "]";
		}
		message += ": " + getValueClass().getSimpleName() + " (" + comment
				+ ")";

		getLogger()
				.info(new StructuredLogMessage(message,
						StructuredLogTags.CONFIG_KEY));
	}

	/**
	 * Uses the default value is possible, logs and returns its value. Reports
	 * the error otherwise.
	 */
	private T useDefaultValue() throws ConQATException {
		if (defaultValue == null) {
			throw new ConQATException("No value provided for key " + keyName
					+ " and not default value configured.");
		}
		logUsedValue(defaultValue + " (default)");
		return defaultValue;
	}

	/** Logs the value used via user-level logging. */
	private void logUsedValue(String value) {
		getLogger().info(
				new StructuredLogMessage("For key '" + keyName
						+ "' using value '" + value + "'",
						StructuredLogTags.CONFIG_VALUE));
	}

	/**
	 * Converts a string to the expected result type. The input string is never
	 * null, so the result should never be null as well; instead an exception
	 * should be thrown in case of problems.
	 * <p>
	 * The default implementation is based on reflection using
	 * {@link ReflectionUtils#convertString(String, Class)}, which is the same
	 * mechanism used by ConQAT for immediate parameters. However, it can be
	 * overridden if this is not suitable for the value type used.
	 */
	protected T convert(String string) throws ConQATException {
		try {
			return ReflectionUtils.convertString(string, getValueClass());
		} catch (TypeConversionException e) {
			throw new ConQATException(string + " is no valid "
					+ getValueClass().getSimpleName() + "!", e);
		}
	}

	/** Returns the class the value should be converted to. */
	protected abstract Class<T> getValueClass();
}
