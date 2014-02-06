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
package org.conqat.engine.java.ecj;

import java.util.HashMap;

import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;

/**
 * This class captures options for the ECJ. Currently, this is just a wrapper
 * around {@link CompilerOptions}. However, we introduced this class as we plan
 * to add more sophisticated options support later on.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 3DB2562B2ED1EB4428C86DE74F02EC6C
 */
public class EcjCompilerOptions {

	/** Stores options. */
	private final HashMap<String, Object> options = new HashMap<String, Object>();

	/**
	 * Create empty options object.
	 */
	public EcjCompilerOptions() {
		// nothing to do
	}

	/**
	 * Create options object.
	 * 
	 * @param version
	 *            specifies source and target version. Use constants prefixed
	 *            with <code>VERSION</code> from {@link CompilerOptions} here.
	 */
	public EcjCompilerOptions(String version) {
		options.put(CompilerOptions.OPTION_Source, version);
		options.put(CompilerOptions.OPTION_TargetPlatform, version);
	}

	/** Add an option. See class {@link CompilerOptions} for legal values. */
	public void put(String key, Object value) {
		options.put(key, value);
	}

	/** Obtain an ECJ option object. */
	public CompilerOptions obtainOptions() {
		return new CompilerOptions(options);
	}
}