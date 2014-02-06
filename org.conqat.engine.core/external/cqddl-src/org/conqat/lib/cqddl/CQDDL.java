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
package org.conqat.lib.cqddl;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.conqat.lib.cqddl.parser.CQDDLLexer;
import org.conqat.lib.cqddl.parser.CQDDLParseException;
import org.conqat.lib.cqddl.parser.CQDDLParser;
import org.conqat.lib.cqddl.parser.CQDDLParsingParameters;

import org.conqat.lib.commons.collections.PairList;

/**
 * The main/entry class for using CQDDL.
 * 
 * @author hummelb
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating YELLOW Hash: 5CE4C7FDF418FC1712F45FDF48911D30
 */
public class CQDDL {

	/**
	 * Parses the given CQDDL statement (which may be intermixed with arbitrary
	 * objects).
	 * 
	 * @param parameters
	 *            the parameters that provide functions and key abbreviations.
	 * @param statements
	 *            the statements forming the CQDDL expression. All strings used
	 *            here will just be concatenated, while other objects are
	 *            inlined into the CQDDL term using object references. Also see
	 *            the test cases for example uses of this function.
	 * @return the parses object, which may be a {@link PairList} or an
	 *         arbitrary object.
	 */
	public static Object parse(CQDDLParsingParameters parameters,
			Object... statements) throws CQDDLParseException {

		CQDDLLexer lexer = new CQDDLLexer();
		CQDDLParser parser = new CQDDLParser(new CommonTokenStream(lexer));
		parser.setParsingParameters(parameters);

		int internalObjectCounter = 0;
		StringBuilder sb = new StringBuilder();

		for (Object object : statements) {
			if (object instanceof String) {
				sb.append((String) object);
			} else {
				String id = "__" + internalObjectCounter++;
				sb.append("$" + id);
				parser.storeObject(id, object);
			}
		}

		lexer.setCharStream(new ANTLRStringStream(sb.toString()));
		try {
			Object result = parser.objectEOF();
			if (parser.getErrorMessage() != null) {
				throw new CQDDLParseException(parser.getErrorMessage());
			}
			return result;
		} catch (RecognitionException e) {
			throw new CQDDLParseException("Could not parse CQDDL expression!",
					e);
		}
	}
}