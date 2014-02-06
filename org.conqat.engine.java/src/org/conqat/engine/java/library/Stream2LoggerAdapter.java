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
package org.conqat.engine.java.library;

import java.io.OutputStream;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * This class is an adapter to log information written to an outputstream with
 * log4j. It processes all data written to the stream and logs it line-wise
 * everytime a linebreak (\n, \r or \r\n) is written to the stream.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * 
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: BC46B289E4597AEF11819A69D1DDF150
 */
/* package */class Stream2LoggerAdapter extends OutputStream {

	/** current line */
	private StringBuilder line = new StringBuilder();

	/**
	 * this is or handling \r\n linebreaks. Set to <code>true</code> if last
	 * character written to the stream was an \r. So it can look ahead and check
	 * if the next character is \n
	 */
	private boolean inR;

	/** The logger to write the ouput to. */
	private final Logger logger;

	/** The log level to use. */
	private final Level level;

	/** The line prefix for logging. */
	private final String prefix;

	/**
	 * Create a new adapter.
	 * 
	 * @param logger
	 *            The logger to write output to.
	 * @param level
	 *            The log level to use.
	 */
	public Stream2LoggerAdapter(Logger logger, Level level) {
		this(logger, level, null);
	}

	/**
	 * Create a new adapter.
	 * 
	 * @param logger
	 *            The logger to write output to.
	 * @param level
	 *            The log level to use.
	 * @param prefix
	 *            A prefix for every log message.
	 */
	public Stream2LoggerAdapter(Logger logger, Level level, String prefix) {
		this.logger = logger;
		this.level = level;
		this.prefix = prefix;
	}

	/**
	 * Write a single byte. If the byte is not a line terminator it will be
	 * append to the current line. If it's a line terminator. The current line
	 * will be logged and cleared.
	 */
	@Override
	public void write(int b) {
		char c = (char) b;

		if (c == '\n') {
			printLine();
			inR = false;
			return;
		} else

		if (c == '\r') {
			inR = true;
			return;
		}

		// was a single '\r'
		if (inR) {
			inR = false;
			printLine();
			return;
		}

		line.append(c);
	}

	/** Log the current line and clear it. */
	private void printLine() {
		String message;
		if (prefix == null) {
			message = line.toString();
		} else {
			message = prefix + ": " + line.toString();
		}
		logger.log(level, message);
		line = new StringBuilder();
	}
}