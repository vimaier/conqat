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
package org.conqat.engine.commons;

import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.reflect.ReflectionUtils;
import org.conqat.lib.commons.reflect.TypeConversionException;

/**
 * Collection of utility methods.
 * 
 * @author deissenb
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: FBF9436738A7484B30DBB1C37A863F8C
 */
public class CommonUtils {

	/** Default date format. */
	public final static String DEFAULT_DATE_FORMAT_PATTERN = "yyyy-MM-dd";

	/**
	 * Create {@link SimpleDateFormat} from pattern string. In contrast to the
	 * constructor of {@link SimpleDateFormat} this raises a
	 * {@link ConQATException} for invalid patterns.
	 */
	public static SimpleDateFormat createDateFormat(String pattern)
			throws ConQATException {
		try {
			return new SimpleDateFormat(pattern);
		} catch (IllegalArgumentException ex) {
			throw new ConQATException("Illegal date pattern: " + pattern);
		}
	}

	/**
	 * This method parses a date string using the specified format pattern. See
	 * {@link SimpleDateFormat} for pattern syntax.
	 * 
	 * @throws ConQATException
	 *             if an illegal format string was supplied or the date string
	 *             does not match the given format.
	 */
	public static Date parseDate(String dateString, String formatString)
			throws ConQATException {
		SimpleDateFormat format = createDateFormat(formatString);
		try {
			return format.parse(dateString);
		} catch (ParseException e) {
			throw new ConQATException("Illegal date format for '" + dateString
					+ "'.");
		}
	}

	/**
	 * Wraps {@link Pattern#compile(String)} to produce {@link ConQATException}s
	 * instead of {@link PatternSyntaxException}s.
	 */
	public static Pattern compilePattern(String regex) throws ConQATException {
		try {
			return Pattern.compile(regex);
		} catch (PatternSyntaxException e) {
			throw wrapPatternSyntaxException(e);
		}
	}

	/**
	 * Wraps {@link Pattern#compile(String)} to produce {@link ConQATException}s
	 * instead of {@link PatternSyntaxException}s.
	 */
	public static Pattern compilePattern(String regex, String message)
			throws ConQATException {
		try {
			return Pattern.compile(regex);
		} catch (PatternSyntaxException e) {
			throw new ConQATException(message, e);
		}
	}

	/**
	 * Wraps {@link Pattern#compile(String, int)} to produce
	 * {@link ConQATException}s instead of {@link PatternSyntaxException}s.
	 */
	public static Pattern compilePattern(String regex, int flags)
			throws ConQATException {
		try {
			return Pattern.compile(regex, flags);
		} catch (PatternSyntaxException e) {
			throw wrapPatternSyntaxException(e);
		}
	}

	/** Wraps a {@link PatternSyntaxException} into a {@link ConQATException} */
	public static ConQATException wrapPatternSyntaxException(
			PatternSyntaxException e) {
		return new ConQATException("Illegal regular expression: ", e);
	}

	/**
	 * Get an encoding for the specified name. This throws a
	 * {@link ConQATException} if the encoding is not supported.
	 */
	public static Charset obtainEncoding(String encodingName)
			throws ConQATException {
		if (!Charset.isSupported(encodingName)) {
			throw new ConQATException("Unsupported encoding: " + encodingName);
		}
		return Charset.forName(encodingName);
	}

	/**
	 * Converts a String value to an object of the specified type and wraps the
	 * Exceptions into a {@link ConQATException}
	 */
	public static Object convertTo(String valueString, String typeName) throws ConQATException {
		try {
			return ReflectionUtils.convertTo(valueString, typeName);
		} catch (ClassNotFoundException e) {
			throw new ConQATException("Could not resolve type: " + typeName, e);
		} catch (TypeConversionException e) {
			throw new ConQATException(
					"Could not convert value: " + valueString, e);
		}
	}
}