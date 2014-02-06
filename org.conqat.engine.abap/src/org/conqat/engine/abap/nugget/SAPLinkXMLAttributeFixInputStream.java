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
package org.conqat.engine.abap.nugget;

import java.io.InputStream;

/**
 * Special {@link XMLAttributeFixInputStream} for XML documents exported with
 * SAP Link which have an inconsistent line ending style in attribute values.
 * More specifically, the line ending style is "\r\r\n" which is however
 * consistently followed. This stream replaces the newline ("\n"), and ignores
 * the carriage returns ("\r").
 * 
 * @author herrmama
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: E4C2EFCA4EC1390160092385166EBE4A
 */
public class SAPLinkXMLAttributeFixInputStream extends
		XMLAttributeFixInputStream {

	/**
	 * Constructor
	 */
	public SAPLinkXMLAttributeFixInputStream(InputStream in) {
		super(in);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void replaceLineBreak(int c) {
		if (c == '\n') {
			produceLineBreakEscape();
		} else if (c != '\r') {
			produce(c);
		}
	}
}