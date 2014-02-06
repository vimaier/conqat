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

import java.io.IOException;
import java.io.InputStream;

/**
 * An {@link InputStream} that fixes line breaks in attribute values of XML
 * documents. In order to do so, it replaces line breaks in attribute values by
 * the appropriate escape character "&#xA;".
 * 
 * @author herrmama
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: F5B78117A513C2F91B49C0FD37CA7DD0
 */
public class XMLAttributeFixInputStream extends InputStream {

	/**
	 * Length of buffer. It needs to be one element longer than the longest
	 * character sequence by which a single character is replace.
	 */
	private static final int LENGTH = 6;

	/** Input stream to be wrapped */
	private final InputStream in;

	/** Buffer array to accommodate replacement character sequences */
	private final int[] buffer;

	/** Start of buffer */
	private int start;

	/** End of buffer */
	private int end;

	/** true if current character is inside an element */
	private boolean insideElement;

	/** true if current character is inside an attribute value */
	private boolean insideAttributeValue;

	/** true if last character is a carriage return */
	private boolean lastCharIsCarriageReturn;

	/**
	 * Constructor
	 */
	public XMLAttributeFixInputStream(InputStream in) {
		this.in = in;

		// initialize state variables
		insideElement = false;
		insideAttributeValue = false;
		lastCharIsCarriageReturn = false;

		// initialize buffer
		buffer = new int[7];
		start = end = 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int read() throws IOException {

		while (bufferEmpty()) {
			int c = in.read();
			checkInsideElement(c);
			checkInsideAttributeValue(c);
			checkLineBreak(c);
		}
		return consume();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long skip(long n) {
		return 0;
	}

	/**
	 * Check whether there is an element in the buffer
	 */
	private boolean bufferEmpty() {
		return start == end;
	}

	/**
	 * Remove an element from the buffer
	 */
	private int consume() {
		int c = buffer[start];
		start = (start + 1) % LENGTH;
		return c;
	}

	/**
	 * Write an element to the buffer
	 */
	protected void produce(int c) {
		buffer[end] = c;
		end = (end + 1) % LENGTH;
	}

	/**
	 * Change element state based on the current character
	 */
	private void checkInsideElement(int c) {
		if (c == '<') {
			insideElement = true;
		} else if (c == '>') {
			insideElement = false;
		}
	}

	/**
	 * Change attribute value state based on the current character
	 */
	private void checkInsideAttributeValue(int c) {
		if (insideElement && c == '"') {
			insideAttributeValue = !insideAttributeValue;
			lastCharIsCarriageReturn = false;
		}
	}

	/**
	 * Search for line breaks in attribute values and replace them
	 */
	protected void checkLineBreak(int c) {
		if (insideAttributeValue) {
			replaceLineBreak(c);
		} else {
			produce(c);
		}
	}

	/**
	 * If the current character is part of a line break, replace it.
	 */
	protected void replaceLineBreak(int c) {
		if (c == '\r') {
			produceLineBreakEscape();
			lastCharIsCarriageReturn = true;
		} else if (c == '\n') {
			if (!lastCharIsCarriageReturn) {
				produceLineBreakEscape();
			}
			lastCharIsCarriageReturn = false;
		} else {
			produce(c);
		}
	}

	/**
	 * Produce an escape sequence for a line break
	 */
	protected void produceLineBreakEscape() {
		produce('&');
		produce('#');
		produce('x');
		produce('A');
		produce(';');
	}
}