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
package org.conqat.engine.java;

import net.sourceforge.pmd.lang.LanguageVersion;

import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;

/**
 * The Java language specification versions.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 42938 $
 * @ConQAT.Rating GREEN Hash: B6A51E9C1EC4B2BA9CE9B3072BA04362
 */
public enum EJavaVersion {

	/** Version 1.1 */
	// PMD provides only 1.3 and above
	VERSION_1_1(CompilerOptions.VERSION_1_1, LanguageVersion.JAVA_13),

	/** Version 1.2 */
	// PMD provides only 1.3 and above
	VERSION_1_2(CompilerOptions.VERSION_1_2, LanguageVersion.JAVA_13),

	/** Version 1.3 */
	VERSION_1_3(CompilerOptions.VERSION_1_3, LanguageVersion.JAVA_13),

	/** Version 1.4 */
	VERSION_1_4(CompilerOptions.VERSION_1_4, LanguageVersion.JAVA_14),

	/** Version 1.5 */
	VERSION_1_5(CompilerOptions.VERSION_1_5, LanguageVersion.JAVA_15),

	/** Version 1.6 */
	VERSION_1_6(CompilerOptions.VERSION_1_6, LanguageVersion.JAVA_16),

	/** Version 1.7 */
	VERSION_1_7(CompilerOptions.VERSION_1_7, LanguageVersion.JAVA_17);

	/** The version identifier as used by the ECJ API */
	private final String ecjVersion;

	/** The language version used for PMD */
	private final LanguageVersion pmdLanguageVersion;

	/** Constructor */
	private EJavaVersion(String ecjVersion, LanguageVersion pmdLanguageVersion) {
		this.ecjVersion = ecjVersion;
		this.pmdLanguageVersion = pmdLanguageVersion;
	}

	/** Returns the version identifier as used by the ECJ API */
	public String getECJVersion() {
		return ecjVersion;
	}

	/** Returns the corresponding {@link LanguageVersion}. */
	public LanguageVersion getPMDLanguageVersion() {
		return pmdLanguageVersion;
	}
}