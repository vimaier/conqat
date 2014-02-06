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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;

/**
 * This processor displays a prompt and requires the user to enter a string.
 * 
 * @author Daniel Ratiu
 * @author Florian Deissenboeck
 * @author Tilman Seifert
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: BAD0795B34E21B56578166AF40A6EF6A
 */
@AConQATProcessor(description = "Reads a string from standard in.")
public class ConsoleInput extends ConQATProcessorBase {

	/** Prompt to be displayed. */
	private String prompt = "? ";

	/** Set the prompt text. */
	@AConQATParameter(name = "prompt", maxOccurrences = 1, description = ""
			+ "Prompt to display. Default is a single question mark.")
	public void setPrompt(
			@AConQATAttribute(name = "text", description = "Text for the prompt.")
			String prompt) {
		this.prompt = prompt;
	}

	/** {@inheritDoc} */
	@Override
	public String process() throws ConQATException {
		System.out.print(prompt);
		System.out.flush();

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String input;
		try {
			input = in.readLine();
		} catch (IOException e) {
			throw new ConQATException(e);
		}
		return input;
	}

}