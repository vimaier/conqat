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
package org.conqat.engine.core.driver.util;

/**
 * This class holds all constants related to parsing the XML configuration
 * (mostly token names).
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 50B3CF4F59D025F85EAF72621BB66F20
 */
public final class XmlToken {

	/** XML element {@value} */
	public static final String XML_ELEMENT_CONQAT = "conqat";

	/** XML element {@value} */
	public static final String XML_ELEMENT_PROPERTY = "property";

	/** XML element {@value} */
	public static final String XML_ELEMENT_PROCESSOR = "processor";

	/** XML element {@value} */
	public static final String XML_ELEMENT_BLOCK = "block";

	/** XML element {@value} */
	public static final String XML_ELEMENT_BLOCK_SPECIFICATION = "block-spec";

	/** XML element {@value} */
	public static final String XML_ELEMENT_META = "meta";

	/** XML element {@value} */
	public static final String XML_ELEMENT_DOC = "doc";

	/** XML element {@value} */
	public static final String XML_ELEMENT_PARAM = "param";

	/** XML element {@value} */
	public static final String XML_ELEMENT_ATTR = "attr";

	/** XML element {@value} ' */
	public static final String XML_ELEMENT_OUTPUT = "out";

	/** XML attribute {@value} */
	public static final String XML_ATTRIBUTE_VALUE = "value";

	/** XML attribute {@value} */
	public static final String XML_ATTRIBUTE_NAME = "name";

	/** XML attribute {@value} */
	public static final String XML_ATTRIBUTE_TYPE = "type";

	/** XML attribute {@value} */
	public static final String XML_ATTRIBUTE_CLASS = "class";

	/** XML attribute {@value} */
	public static final String XML_ATTRIBUTE_SPEC = "spec";

	/** XML attribute {@value} */
	public static final String XML_ATTRIBUTE_CONDITION = "condition";

	/** XML attribute {@value} */
	public static final String XML_ATTRIBUTE_REF = "ref";

	/** Location of the schema used to validate the config file. */
	public static final String SCHEMA_NAME = "conqat.xsd";

	/** The namespace used in the block files. */
	public static final String BLOCK_FILE_NAMESPACE = "http://conqat.cs.tum.edu/ns/config";
}