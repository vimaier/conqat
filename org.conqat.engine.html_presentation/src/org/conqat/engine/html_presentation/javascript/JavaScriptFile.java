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

import java.util.ArrayList;
import java.util.List;

import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableList;

/**
 * Represents a single JavaScript file. This class is immutable.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 38039 $
 * @ConQAT.Rating GREEN Hash: 4408897DFBE8B1A68718A45EAF2CFB8F
 */
public class JavaScriptFile implements Comparable<JavaScriptFile> {

	/** The type of JavaScript contained. */
	public static enum EType {

		/**
		 * A file that contains JavaScript code that is required and thus
		 * included in the compiled JavaScript in any case. This is typically
		 * used for all non-third-party code.
		 */
		CODE_REQUIRED,

		/**
		 * A file that contains code that is from a library and might be
		 * referenced from other files. This is only included if an actual
		 * reference is found. Furthermore, compiler warnings are not reported
		 * for these files.
		 */
		CODE_LIBRARY,

		/**
		 * A file containing a closure template (a.k.a. Soy). Technically, this
		 * is no JavaScript, but it is converted to JavaScript during
		 * compilation. Template files are always required, as there are no
		 * third-party templates.
		 */
		CLOSURE_TEMPLATE,

		/**
		 * A file containing extern descriptions for Google's closure compiler.
		 * This is always included as externs description to the compiler.
		 */
		CLOSURE_EXTERN
	}

	/** The type of this file. */
	private final EType type;

	/** A unique name used to identify this file. */
	private final String name;

	/** The content of this file. */
	private final String content;

	/**
	 * The list of namespaces provided by this file. The namespaces are
	 * basically just a list of arbitrary strings and are not related to
	 * {@link #name}. The provided namespaces are inspected to determine if an
	 * optional code file should be included during compilation.
	 */
	private final List<String> providedNamespaces = new ArrayList<String>();

	/**
	 * The list of namespaces required by this file. The namespaces are
	 * basically just a list of arbitrary strings and are not related to
	 * {@link #name}. The required namespaces are inspected to resolve
	 * additional code files required for building the script.
	 */
	private final List<String> requiredNamespaces = new ArrayList<String>();

	/** Constructor. */
	/* package */JavaScriptFile(EType type, String name, String content,
			List<String> providedNamespaces, List<String> requiredNamespaces) {
		this.type = type;
		this.name = name;
		this.content = content;
		this.providedNamespaces.addAll(providedNamespaces);
		this.requiredNamespaces.addAll(requiredNamespaces);
	}

	/** Returns the type. */
	public EType getType() {
		return type;
	}

	/** Returns the name. */
	public String getName() {
		return name;
	}

	/** Returns the content. */
	public String getContent() {
		return content;
	}

	/** Returns the provided namespaces. */
	public UnmodifiableList<String> getProvidedNamespaces() {
		return CollectionUtils.asUnmodifiable(providedNamespaces);
	}

	/** Returns the required namespaces. */
	public UnmodifiableList<String> getRequiredNamespaces() {
		return CollectionUtils.asUnmodifiable(requiredNamespaces);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Compares by name.
	 */
	@Override
	public int compareTo(JavaScriptFile other) {
		return name.compareTo(other.name);
	}
}
