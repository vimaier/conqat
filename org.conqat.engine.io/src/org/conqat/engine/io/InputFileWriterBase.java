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
package org.conqat.engine.io;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;

/**
 * Base class for file writers depending on a single input object.
 * 
 * @param <T>
 *            the type used for input.
 * 
 * @author Florian Deissenboeck
 * @author hummelb
 * @author $Author: juergens $
 * @version $Rev: 35195 $
 * @ConQAT.Rating GREEN Hash: 42EA597A29F4D10F4B6E9953DA6D7746
 */
public abstract class InputFileWriterBase<T> extends FileWriterBase {

	/** The input to work on. */
	private T input;

	/** Number formatter instance. */
	protected static final NumberFormat numberFormatter = NumberFormat
			.getInstance();

	/** Set input. */
	@AConQATParameter(name = ConQATParamDoc.INPUT_NAME, minOccurrences = 1, maxOccurrences = 1, description = ConQATParamDoc.INPUT_DESC)
	public void setInput(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) T input) {

		this.input = input;
	}

	/** {@inheritDoc} */
	@Override
	protected final void writeFile(File file) throws ConQATException,
			IOException {
		writeToFile(input, file);
	}

	/** Write the given input to the file. */
	protected abstract void writeToFile(T input, File file)
			throws ConQATException, IOException;

}