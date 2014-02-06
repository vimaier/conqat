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
package org.conqat.engine.java.library;

import java.lang.reflect.Modifier;
import java.util.EnumSet;

import org.conqat.lib.commons.enums.EnumUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * This class describes the modifiers that can be used with Java types and
 * members. This class maintains three distinct classes of modifiers:
 * <ul>
 * <li>Visibility: one of <code>private|package|protected|public</code></li>
 * <li>Modifiers: any combination of
 * <code>abstract|final|native|static|strictfp|synchronized|volatile|transient</code>
 * </li>
 * <li>Type: one of <code>class|interface|enum|annotation</code></li>
 * </ul>
 * 
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: F7F0875AC6B85BF1B6202F986C4F16AD
 */
public class Modifiers {

	/** Documentation of the pattern used by {@link #Modifiers(String)}. */
	public static final String MEMBER_PATTERN_DOC = "(private|package|protected|public)? "
			+ "(abstract|final|native|static|strictfp|synchronized|volatile|transient)* ";

	/** Documentation of the pattern used by {@link #Modifiers(String)}. */
	public static final String TYPE_PATTERN_DOC = MEMBER_PATTERN_DOC
			+ "(class|interface|enum|annotation)?";

	/** This is not yet public in {@link Modifier} */
	private static final int SYNTHETIC_FLAG = 0x00001000;

	/** This is not yet public in {@link Modifier} */
	private static final int ANNOTATION_FLAG = 0x00002000;

	/** This is not yet public in {@link Modifier} */
	private static final int ENUM_FLAG = 0x00004000;

	/** Set of modifiers. */
	private final EnumSet<EModifier> modifiers = EnumSet
			.noneOf(EModifier.class);

	/** The visibility. */
	private final EVisibility visibility;

	/** The type. */
	private final EType type;

	/**
	 * Create a modifier from a textual pattern. Pattern syntax is:
	 * {@value #TYPE_PATTERN_DOC}. Please note that a modifier may stay
	 * underspecified, so it is perfectly legal to create a modifier that
	 * describes e.g. abstract class without specifying the visbility:
	 * <code>abstract class</code>.
	 * <p>
	 * <i>Note:</i> If mutliple visibilities or types are specified, only one of
	 * them is chosen.
	 */
	public Modifiers(String pattern) {
		String[] parts = pattern.split("\\s+");
		decodeModifiers(parts);
		type = decode(parts, EType.class);
		visibility = decode(parts, EVisibility.class);
	}

	/**
	 * Create modifiers object from virtual machine defined access flags as
	 * provided by e.g. {@link Class#getModifiers()} or
	 * {@link org.apache.bcel.classfile.AccessFlags#getAccessFlags()}.
	 * 
	 * @param flags
	 *            the access flags
	 * @param isClass
	 *            unfortunately the virtual machine uses the same bit pattern
	 *            for the ACC_SUPER and ACC_SYNCHRONIZED flags. Therefore this
	 *            methods needs to know if it is working a class or a method.
	 *            Set to <code>true</code> for classes.
	 */
	public Modifiers(int flags, boolean isClass) {
		type = decodeType(flags);
		visibility = decodeVisibility(flags);
		decodeModifiers(flags, isClass);
	}

	/** Checks if this modifiers object defines anything at all. */
	public boolean isEmpty() {
		return visibility == null && type == null && modifiers.isEmpty();
	}

	/**
	 * Checks if this modifier is statisfied by another. It is statisfied if
	 * they have the same visibility and type and the other has all modifiers
	 * this one has.
	 */
	public boolean isSatisfied(Modifiers other) {
		return isVisibilitySatisfied(other) && isModifiersSatisfied(other)
				&& isTypeSatisfied(other);
	}

	/** Hashcode includes visibility, type and modifiers. */
	@Override
	public int hashCode() {
		int hashCode = modifiers.hashCode();
		if (visibility != null) {
			hashCode = 37 * hashCode + visibility.hashCode();
		}
		if (type != null) {
			hashCode = 37 * hashCode + type.hashCode();
		}
		return hashCode;
	}

	/** Checks for equality of visibility, type and modifiers. */
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Modifiers)) {
			return false;
		}
		Modifiers otherModifier = (Modifiers) other;
		return modifiers.equals(otherModifier.modifiers)
				&& visibility == otherModifier.visibility
				&& type == otherModifier.type;
	}

	/** String representation . */
	@Override
	public String toString() {

		StringBuilder result = new StringBuilder();

		if (visibility == null) {
			result.append("*");
		} else {
			result.append(visibility.name().toLowerCase());
		}

		result.append(StringUtils.SPACE_CHAR);

		for (EModifier modifier : modifiers) {
			result.append(modifier.name().toLowerCase());
			result.append(StringUtils.SPACE_CHAR);
		}

		if (type == null) {
			result.append("*");
		} else {
			result.append(type.name().toLowerCase());
		}

		return result.toString();

	}

	/**
	 * Decode modifiers. See {@link #Modifiers(int, boolean)} for details.
	 */
	private void decodeModifiers(int flags, boolean isClass) {
		if (Modifier.isAbstract(flags)) {
			modifiers.add(EModifier.ABSTRACT);
		}
		if (Modifier.isFinal(flags)) {
			modifiers.add(EModifier.FINAL);
		}
		if (Modifier.isNative(flags)) {
			modifiers.add(EModifier.NATIVE);
		}
		if (Modifier.isStatic(flags)) {
			modifiers.add(EModifier.STATIC);
		}
		if (Modifier.isStrict(flags)) {
			modifiers.add(EModifier.STRICTFP);
		}
		if (!isClass && Modifier.isSynchronized(flags)) {
			modifiers.add(EModifier.SYNCHRONIZED);
		}
		if (Modifier.isVolatile(flags)) {
			modifiers.add(EModifier.VOLATILE);
		}
		if (Modifier.isTransient(flags)) {
			modifiers.add(EModifier.TRANSIENT);
		}
		if ((flags & SYNTHETIC_FLAG) != 0) {
			modifiers.add(EModifier.SYNTHETIC);
		}
	}

	/** Decode type. */
	private EType decodeType(int flags) {
		// Evaluation order is important to distinguish annotations and
		// interfaces
		if ((flags & ANNOTATION_FLAG) != 0) {
			return EType.ANNOTATION;
		}
		if ((flags & ENUM_FLAG) != 0) {
			return EType.ENUM;
		}
		if (Modifier.isInterface(flags)) {
			return EType.INTERFACE;
		}
		return EType.CLASS;
	}

	/** Decode visibility. */
	private EVisibility decodeVisibility(int flags) {
		if (Modifier.isPrivate(flags)) {
			return EVisibility.PRIVATE;
		}
		if (Modifier.isProtected(flags)) {
			return EVisibility.PROTECTED;
		}
		if (Modifier.isPublic(flags)) {
			return EVisibility.PUBLIC;
		}
		return EVisibility.PACKAGE;
	}

	/** Decode modifiers from strings. */
	private void decodeModifiers(String[] codes) {
		for (String code : codes) {
			EModifier flag = EnumUtils.valueOfIgnoreCase(EModifier.class, code);
			if (flag != null) {
				modifiers.add(flag);
			}
		}
	}

	/**
	 * Find enum element that matches a string in a string array. This returns
	 * the the first element found.
	 */
	private <E extends Enum<E>> E decode(String[] codes, Class<E> enumClass) {
		for (String code : codes) {
			E e = EnumUtils.valueOfIgnoreCase(enumClass, code);
			if (e != null) {
				return e;
			}
		}
		return null;
	}

	/** Are modifiers satisfied? */
	private boolean isModifiersSatisfied(Modifiers other) {
		return other.modifiers.containsAll(modifiers);
	}

	/** Is type satisified? */
	private boolean isTypeSatisfied(Modifiers other) {
		if (type == null) {
			return true;
		}
		return type == other.type;
	}

	/** Is visibility satisfied? */
	private boolean isVisibilitySatisfied(Modifiers other) {
		if (visibility == null) {
			return true;
		}
		return visibility == other.visibility;
	}

	/** This enumeration describes the modifiers. */
	private enum EModifier {
		/** Protected visibility. */
		ABSTRACT,

		/** Protected visibility. */
		FINAL,

		/** Protected visibility. */
		NATIVE,

		/** Protected visibility. */
		STATIC,

		/** Protected visibility. */
		STRICTFP,

		/** Protected visibility. */
		SYNCHRONIZED,

		/** Protected visibility. */
		SYNTHETIC,

		/** Protected visibility. */
		VOLATILE,

		/** Protected visibility. */
		TRANSIENT;
	}

	/** This enumeration describes the types. */
	private enum EType {
		/** Protected visibility. */
		ANNOTATION,

		/** Protected visibility. */
		CLASS,

		/** Protected visibility. */
		ENUM,

		/** Protected visibility. */
		INTERFACE
	}

	/** This enumeration describes the visbilities. */
	private enum EVisibility {

		/** Private visibility. */
		PRIVATE,

		/** Protected visibility. */
		PROTECTED,

		/** Public visibility. */
		PUBLIC,

		/** Package visibility. */
		PACKAGE
	}
}