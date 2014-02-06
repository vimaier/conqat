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
package org.conqat.engine.architecture.format;

import java.net.URL;

/**
 * Provides access to the various schemas for architecture related xml files.
 * 
 * @author heineman
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: E8188889885ADF0ABA4DFF0EF41E3169
 */
public class ArchitectureFormats {

	/** Extension of architecture specification files. */
	public static final String ARCHITECTURE_FILE_EXTENSION = "architecture";

	/**
	 * Location of the schema used to validate the architecture definition file.
	 */
	private static final String ARCHITECTURE_DEFINITION_SCHEMA_NAME = "architecture.xsd";

	/**
	 * Location of the schema used to validate the architecture assessment file.
	 */
	private static final String ARCHITECTURE_ASSESSMENT_SCHEMA_NAME = "architecture_assessment.xsd";

	/**
	 * The xml namespace of the assessment result xml.
	 */
	public static final String ASSESSMENT_RESULT_XML_NAMESPACE = "http://conqat.cs.tum.edu/ns/architecture-assessment";

	/**
	 * @return the architecture definition file schema.
	 */
	public static URL getArchitectureDefinitionSchema() {
		return ArchitectureFormats.class
				.getResource(ARCHITECTURE_DEFINITION_SCHEMA_NAME);
	}

	/**
	 * @return the architecture assessment file schema.
	 */
	public static URL getArchitectureAssessmentSchema() {
		return ArchitectureFormats.class
				.getResource(ARCHITECTURE_ASSESSMENT_SCHEMA_NAME);
	}

}