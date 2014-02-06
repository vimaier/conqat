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
package org.conqat.engine.core.conqatdoc;

import org.conqat.engine.core.conqatdoc.content.BlockSpecificationPageGenerator;
import org.conqat.engine.core.conqatdoc.content.ProcessorSpecificationPageGenerator;
import org.conqat.engine.core.driver.specification.BlockSpecification;
import org.conqat.engine.core.driver.specification.ISpecification;
import org.conqat.engine.core.driver.specification.ProcessorSpecification;

/**
 * Class containing support methods for handling ISpecifications.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 3AE1882F33BA252F3EE813479F1E07D1
 */
public class SpecUtils {

	/**
	 * Returns the last part of a specification's namem where parts are
	 * separated by dots. For processors this corresponds to the class name.
	 */
	public static String getShortName(ISpecification spec) {
		int dotPos = spec.getName().lastIndexOf('.');
		if (dotPos < 0) {
			return spec.getName();
		}
		return spec.getName().substring(dotPos + 1);
	}

	/**
	 * Returns the name of a of the HTML page for a specification (used for
	 * links) including the file extension (.html).
	 */
	public static String getLinkName(ISpecification spec) {
		if (spec instanceof ProcessorSpecification) {
			return spec.getName()
					+ ProcessorSpecificationPageGenerator.PAGE_SUFFIX;
		}
		if (spec instanceof BlockSpecification) {
			return spec.getName() + BlockSpecificationPageGenerator.PAGE_SUFFIX;
		}
		throw new IllegalStateException("Unknown subclass of ISpecification");
	}

}