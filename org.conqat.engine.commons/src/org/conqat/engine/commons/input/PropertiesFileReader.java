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
package org.conqat.engine.commons.input;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;

/**
 * This processor reads a set of key-value pairs from a Java properties file
 * (see {@link java.util.Properties#load(java.io.InputStream)} and extracts a
 * value identified by a key.
 * 
 * @author Daniel Ratiu
 * @author Florian Deissenboeck
 * @author Tilman Seifert
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: 83F78F90EFE970D83D216230FA42397F
 */
@AConQATProcessor(description = "This processor reads a set of key-value "
		+ "pairs from a Java properties file "
		+ "and extracts a value identified by a key. "
		+ "This processor fails if the file can't be read or the key "
		+ "isn't present.")
public class PropertiesFileReader extends ConQATProcessorBase {

	/** name of the properties file */
	private String filename;

	/** key to read */
	private String key;

	/** Set name of the properties file. */
	@AConQATParameter(name = "file", minOccurrences = 1, maxOccurrences = 1, description = "Properties file to read value from")
	public void setFilename(
			@AConQATAttribute(name = "name", description = "Filename")
			String filename) {
		this.filename = filename;
	}

	/** Set the key to read */
	@AConQATParameter(name = "key", minOccurrences = 1, maxOccurrences = 1, description = "Key to read.")
	public void setKey(
			@AConQATAttribute(name = "name", description = "Name of the key.")
			String key) {
		this.key = key;
	}

	/**
	 * Reads properties file and extracts value.
	 * 
	 * @throws ConQATException
	 *             if the file isn't found or the key isn't present
	 */
	@Override
	public String process() throws ConQATException {
		Properties properties = new Properties();

		try {
			InputStream inputStream = new FileInputStream(filename);
			properties.load(inputStream);
			inputStream.close();
		} catch (IOException e) {
			throw new ConQATException("Can't read file: " + filename + "!", e);
		}

		String value = properties.getProperty(key);
		if (value == null) {
			throw new ConQATException("Key '" + key + "' not found.");
		}
		return value;
	}
}