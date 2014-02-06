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
package org.conqat.engine.core.build;

/**
 * This enumeration describes the XML attributes used in ANT file generation.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 47069 $
 * @ConQAT.Rating GREEN Hash: F3133E4F068FC94EB4C0254263AE61B9
 */
/* package */enum EANTAttribute {
	/** attribute {@linkplain #depends} of element {@link EANTElement#target}. */
	depends,

	/** attribute {@linkplain #destfile} of element {@link EANTElement#zip}. */
	destfile,

	/** attribute {@linkplain #dir} of element {@link EANTElement#ant}. */
	dir,

	/** attribute {@linkplain #inheritAll} of element {@link EANTElement#ant}. */
	inheritAll,

	/**
	 * attribute {@linkplain #name} of element {@link EANTElement#target} and
	 * {@link EANTElement#project}.
	 */
	name,

	/**
	 * attribute {@linkplain #value} of element {@link EANTElement#attribute}.
	 */
	value,

	/** attribute {@linkplain #src} of element {@link EANTElement#zipfileset}. */
	src,

	/**
	 * attribute {@linkplain #includes} of element
	 * {@link EANTElement#zipfileset}.
	 */
	includes,

	/** attribute {@linkplain #target} of element {@link EANTElement#ant}. */
	target,

	/** attribute {@linkplain #todir} of element {@link EANTElement#copy}. */
	todir,

	/**
	 * attribute {@linkplain #includeEmptyDirs} of element
	 * {@link EANTElement#copy}.
	 */
	includeEmptyDirs,

	/**
	 * attribute {@linkplain #environment} of element
	 * {@link EANTElement#property}.
	 */
	environment,

	/** attribute {@linkplain #executable} of element {@link EANTElement#exec}. */
	executable,

	/** attribute {@linkplain #line} of element {@link EANTElement#arg}. */
	line,

	/** attribute {@linkplain #file} of element {@link EANTElement#copy}. */
	file,

	/**
	 * attribute {@linkplain #threadCount} of element
	 * {@link EANTElement#parallel}.
	 */
	threadCount
}