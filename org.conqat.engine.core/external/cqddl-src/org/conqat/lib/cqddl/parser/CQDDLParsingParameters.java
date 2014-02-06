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

import org.conqat.lib.cqddl.function.ICQDDLFunction;

import org.conqat.lib.commons.assertion.CCSMPre;

/**
 * Parameter object for storing all relevant option needed for parsing a CQDDL
 * term. This includes key abbreviations and registered functions.
 * 
 * @author hummelb
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating YELLOW Hash: B41886DD4739EFA1C11D66E153ADF3BB
 */
public class CQDDLParsingParameters {

	/** Registered functions. */
	/* package */final Map<String, ICQDDLFunction> functions = new HashMap<String, ICQDDLFunction>();

	/** Abbreviations used for keys. */
	/* package */final Map<String, String> keyAbbreviations = new HashMap<String, String>();

	/**
	 * Registers a function with the parser. The function name may not have been
	 * already used.
	 */
	public void registerFunction(String name, ICQDDLFunction function) {
		CCSMPre.isNotNull(function);
		CCSMPre.isTrue(!functions.containsKey(name), "Name " + name
				+ " alread in use!");
		functions.put(name, function);
	}

	/** Adds a key abbreviation. */
	public void addKeyAbbreviation(String shortName, String longName) {
		CCSMPre.isNotNull(shortName);
		CCSMPre.isNotNull(longName);
		CCSMPre.isTrue(!keyAbbreviations.containsKey(shortName),
				"Abbreviation for " + shortName + " alread in use!");
		keyAbbreviations.put(shortName, longName);
	}

}