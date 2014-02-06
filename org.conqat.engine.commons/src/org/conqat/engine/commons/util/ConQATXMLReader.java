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
package org.conqat.engine.commons.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.xml.IXMLResolver;
import org.conqat.lib.commons.xml.XMLReader;
import org.conqat.lib.commons.xml.XMLResolver;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Base class for XML readers in the ConQAT context. This mainly takes care of
 * wrapping exceptions.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: 0D17E57921B137CE08837FD1A8D874C7
 */
public abstract class ConQATXMLReader<E extends Enum<E>, A extends Enum<A>, X extends Exception>
		extends XMLReader<E, A, X> {

	/** Constructor. */
	public ConQATXMLReader(String content, Class<A> attributeClass) {
		this(content, new XMLResolver<E, A>(attributeClass));
	}

	/** Constructor with XMLResolver. */
	public ConQATXMLReader(String content, IXMLResolver<E, A> resolver) {
		super(content, null, resolver);
	}

	/** Constructor with schema and XMLResolver. */
	public ConQATXMLReader(String content, URL schema,
			IXMLResolver<E, A> resolver) {
		super(content, schema, resolver);
	}

	/** Constructor. */
	public ConQATXMLReader(File file, URL schema, IXMLResolver<E, A> resolver)
			throws IOException {
		super(file, schema, resolver);
	}

	/**
	 * Parse the XML file and wrap the exceptions into {@link ConQATException}.
	 */
	protected void parseAndWrapExceptions() throws ConQATException {
		try {
			parseFile();
		} catch (SAXParseException e) {
			throw new ConQATException("XML parsing exception at line "
					+ e.getLineNumber() + ", column " + e.getColumnNumber()
					+ " (" + e.getMessage() + ") in element at "
					+ getLocation(), e);
		} catch (SAXException e) {
			throw new ConQATException("XML parsing exception: "
					+ e.getMessage() + " in element at " + getLocation(), e);
		} catch (IOException e) {
			throw new ConQATException("Element at " + getLocation()
					+ " could not be read: " + e.getMessage(), e);
		}
	}

	/** Template method to obtain location used in error messages. */
	protected abstract String getLocation();
}