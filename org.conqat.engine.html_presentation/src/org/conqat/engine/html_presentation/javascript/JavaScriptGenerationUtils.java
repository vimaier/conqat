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
package org.conqat.engine.html_presentation.javascript;

import static org.conqat.lib.commons.string.StringUtils.CR;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.html_presentation.util.JsonUtils;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.reflect.ReflectionUtils;

/**
 * Utility methods that can be used to generate JavaScript from Java constructs.
 * This is used to keep both Java and JavaScript synchronized without manual
 * interaction.
 * <p>
 * Note that this is not a general purpose JavaScript generator, but is limited
 * to support those cases that are actually used in ConQAT.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 47143 $
 * @ConQAT.Rating GREEN Hash: BCBFC2F3A58D189DBC1A884AA8A50255
 */
public class JavaScriptGenerationUtils {

	/** The enum field used for storing the name of the enum literal. */
	private static final String NAME_FIELD = "name";

	/**
	 * Generates and returns a JavaScript enumeration from a map.
	 * 
	 * @param values
	 *            the values of the enum as a name to value mapping. This may
	 *            not be empty and all values should be of same type.
	 * @param fullName
	 *            the fully qualified name of the enum.
	 */
	public static String generateEnumJS(Map<String, ?> values, String fullName) {

		CCSMAssert.isFalse(values.isEmpty(), "May not call with empty values!");

		StringBuilder builder = new StringBuilder();
		String type = getJSType(CollectionUtils.getAny(values.values())
				.getClass());
		insertEnumHead(fullName, type, builder);

		int count = 0;
		for (Entry<String, ?> entry : values.entrySet()) {
			builder.append("    " + entry.getKey() + ": "
					+ JsonUtils.serializeToJSON(entry.getValue()));
			if (++count < values.size()) {
				builder.append(",");
			}
			builder.append(CR);
		}
		builder.append("}" + CR + CR);

		appendValuesArray(fullName, values.keySet(), builder);

		return builder.toString();
	}

	/**
	 * Generates the JavaScript for an enum class. Each enum literal is mapped
	 * to a JavaScript object with the same fields as the enum type.
	 * Additionally, the fields 'name' and 'ordinal' are provided.
	 */
	public static String generateEnumJS(Class<? extends Enum<?>> enumClass,
			String targetPackage) throws ConQATException {

		String fullName = targetPackage + "." + enumClass.getSimpleName();

		StringBuilder builder = new StringBuilder();

		List<Field> fields = getRelevantEnumFields(enumClass);
		Enum<?>[] enumConstants = enumClass.getEnumConstants();

		insertEnumHead(fullName, generateEnumValueType(fields), builder);
		int count = 0;
		for (Enum<?> value : enumConstants) {
			builder.append("    '" + value.name() + "': "
					+ generateEnumValue(value, fields));
			if (++count < enumConstants.length) {
				builder.append(",");
			}
			builder.append(CR);
		}
		builder.append("}" + CR + CR);

		appendValuesArray(fullName, enumConstants, builder);

		return builder.toString();
	}

	/** Inserts the enum head to the given {@link StringBuilder}. */
	private static void insertEnumHead(String fullName, String valueType,
			StringBuilder builder) {
		builder.append("goog.provide('" + fullName + "');" + CR + CR);
		builder.append("/** @enum {" + valueType + "} */" + CR);
		builder.append(fullName + " = {" + CR);
	}

	/**
	 * Returns the fields of the class relevant for conversion to a JavaScript
	 * enumeration. These are all non-static non-final fields for which a type
	 * mapping to JavaScript is defined.
	 */
	private static List<Field> getRelevantEnumFields(
			Class<? extends Enum<?>> enumClass) {
		List<Field> fields = new ArrayList<Field>();
		for (Field field : enumClass.getDeclaredFields()) {
			if (isRelevantEnumField(field)) {
				// we also want private fields and thus have to modify
				// accessibility
				field.setAccessible(true);
				fields.add(field);
			}
		}
		return fields;
	}

	/** Returns whether the given field is relevant for enum conversion. */
	private static boolean isRelevantEnumField(Field field) {
		return !Modifier.isStatic(field.getModifiers())
				&& Modifier.isFinal(field.getModifiers())
				&& getJSType(field) != null;
	}

	/**
	 * Returns the type used for an enum value described by the given fields.
	 * This additionally includes fields for the ordinal and the name.
	 */
	private static String generateEnumValueType(List<Field> fields) {
		StringBuilder type = new StringBuilder();
		type.append("{ ordinal: number");
		if (!hasNameField(fields)) {
			type.append(", " + NAME_FIELD + ": string");
		}
		for (Field field : fields) {
			type.append(", ");
			type.append(field.getName() + ": " + getJSType(field));
		}
		type.append(" }");
		return type.toString();
	}

	/**
	 * Returns whether there is one field called
	 * {@link JavaScriptGenerationUtils#NAME_FIELD}.
	 */
	private static boolean hasNameField(List<Field> fields) {
		for (Field field : fields) {
			if (field.getName().equals(NAME_FIELD)) {
				return true;
			}
		}
		return false;
	}

	/** Returns the JavaScript for a single enum value. */
	private static String generateEnumValue(Enum<?> value, List<Field> fields)
			throws ConQATException {
		StringBuilder valueBuilder = new StringBuilder();
		valueBuilder.append("{ ordinal: " + value.ordinal());

		if (!hasNameField(fields)) {
			valueBuilder.append(", " + NAME_FIELD + ": '" + value.name() + "'");
		}

		for (Field field : fields) {
			valueBuilder.append(", ");
			try {
				valueBuilder.append(field.getName() + ": "
						+ JsonUtils.serializeToJSON(field.get(value)));
			} catch (IllegalAccessException e) {
				throw new ConQATException(
						"Unexpected access problems. Is there a security manager present?",
						e);
			}
		}
		valueBuilder.append("}");
		return valueBuilder.toString();
	}

	/** Appends the values array for an enum to the given {@link StringBuilder}. */
	private static void appendValuesArray(String fullName,
			Enum<?>[] enumConstants, StringBuilder builder) {
		List<String> names = new ArrayList<String>();
		for (Enum<?> value : enumConstants) {
			names.add(value.name());
		}
		appendValuesArray(fullName, names, builder);
	}

	/** Appends the values array for an enum to the given {@link StringBuilder}. */
	private static void appendValuesArray(String fullName,
			Collection<String> names, StringBuilder builder) {
		int count;
		builder.append("/** @type {Array.<" + fullName + ">} */" + CR);
		builder.append(fullName + ".values = [" + CR);
		count = 0;
		for (String name : names) {
			builder.append("    " + fullName + "['" + name + "']");
			if (++count < names.size()) {
				builder.append(",");
			}
			builder.append(CR);
		}
		builder.append("];" + CR);
	}

	/** Returns the JavaScript type for the given field. */
	private static String getJSType(Field field) {
		return getJSType(field.getType());
	}

	/** Returns the JavaScript type for the given type. */
	private static String getJSType(Class<?> type) {
		type = ReflectionUtils.resolvePrimitiveClass(type);

		if (Number.class.isAssignableFrom(type)) {
			return "number";
		}
		if (String.class == type) {
			return "string";
		}
		if (Boolean.class == type) {
			return "boolean";
		}
		if (Enum.class.isAssignableFrom(type)) {
			return "string";
		}
		if (type.isArray()) {
			return "Array.<" + getJSType(type.getComponentType()) + ">";
		}
		if (Collection.class.isAssignableFrom(type)) {
			return "Array";
		}

		return "Object";
	}
}
