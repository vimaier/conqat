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

import java.io.File;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 41093 $
 * @ConQAT.Rating GREEN Hash: 7ECD2CFDC5A44D86C5E3D9D8DBCE9597
 */
@AConQATProcessor(description = "Determines the boot classpath as a String array. IMPORTANT: Works only on Sun VMs.")
public class JavaBootClasspathDeterminer extends ConQATProcessorBase {

	/** Boot class path property */
	private static final String BOOT_CP_PROPERTY = "sun.boot.class.path";

	/** {@inheritDoc} */
	@Override
	public String[] process() throws ConQATException {
		String property = System.getProperty(BOOT_CP_PROPERTY);
		if (property == null) {
			throw new ConQATException("Property " + BOOT_CP_PROPERTY
					+ " not set");
		}
		return property.split(File.pathSeparator);
	}
}
