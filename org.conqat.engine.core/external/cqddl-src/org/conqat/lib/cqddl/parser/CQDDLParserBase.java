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
package org.conqat.lib.cqddl.parser;

import java.util.HashMap;
import java.util.Map;

import org.antlr.runtime.Parser;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.TokenStream;
import org.conqat.lib.cqddl.function.CQDDLEvaluationException;
import org.conqat.lib.cqddl.function.ICQDDLFunction;

import org.conqat.lib.commons.collections.PairList;

/**
 * Base class for the CQDDL parser.
 * 
 * @author hummelb
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating YELLOW Hash: 80EFE38F4541B112567F5F8F7E61A957
 */
public abstract class CQDDLParserBase extends Parser {

	/** Stored objects. */
	private final Map<String, Object> storedObjects = new HashMap<String, Object>();

	/** The current set of parameters used for parsing. */
	private CQDDLParsingParameters parsingParameters = new CQDDLParsingParameters();

	/** Stores the error message. */
	private String errorMessage;

	/** Constructor. */
	protected CQDDLParserBase(TokenStream input, RecognizerSharedState state) {
		super(input, state);
	}

	/** Sets/replaced the currently used parsing parameters. */
	public void setParsingParameters(CQDDLParsingParameters parsingParameters) {
		this.parsingParameters = parsingParameters;
	}

	/** {@inheritDoc} */
	@Override
	public void reportError(RecognitionException ex) {
		errorMessage = getErrorMessage(ex, getTokenNames());
	}

	/** Returns the last error message (or null if none occurred). */
	public String getErrorMessage() {
		return errorMessage;
	}

	/** Stores the given object under the name provided. */
	public void storeObject(String name, Object value)
			throws CQDDLParseException {
		if (storedObjects.containsKey(name)) {
			throw new CQDDLParseException("Duplicate stored object name: "
					+ name);
		}
		storedObjects.put(name, value);
	}

	/** Returns a previously stored object. */
	protected Object retrieveObject(String name) throws CQDDLParseException {
		if (!storedObjects.containsKey(name)) {
			throw new CQDDLParseException("Unknown object to retrieve: " + name);
		}
		return storedObjects.get(name);
	}

	/**
	 * Returns the long version of the key if an abbreviation is registered;
	 * otherwise the key itself.
	 */
	protected String resolveKey(String key) {
		if (parsingParameters.keyAbbreviations.containsKey(key)) {
			return parsingParameters.keyAbbreviations.get(key);
		}
		return key;
	}

	/** Evaluates a function and returns the result. */
	protected Object eval(String functionName,
			PairList<String, Object> parameters) throws CQDDLParseException {
		ICQDDLFunction function = parsingParameters.functions.get(functionName);
		if (function == null) {
			throw new CQDDLParseException("No function of name " + functionName
					+ " found!");
		}

		try {
			return function.eval(parameters);
		} catch (CQDDLEvaluationException e) {
			throw new CQDDLParseException("Error while evaluating function "
					+ functionName, e);
		}
	}

	/** Removes quotes from a string. */
	protected static String unquote(String s) {
		return s.substring(1, s.length() - 1);
	}
}