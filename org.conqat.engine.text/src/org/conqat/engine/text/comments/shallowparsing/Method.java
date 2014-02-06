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

package org.conqat.engine.text.comments.shallowparsing;

import org.conqat.lib.scanner.IToken;

/**
 * Class to store information about a method
 * 
 * @author $Author: steidl $
 * @version $Rev: 46304 $
 * @ConQAT.Rating YELLOW Hash: D9BA7717BF317A7D69106288D835A23D
 */
public class Method {

	/** The method identifier. */
	private String methodName;

	/** Start token of this method. */
	private IToken startToken;

	/** End token of this method. */
	private IToken endToken;

	/** Constructor with name, start and end token. */
	public Method(String name, IToken startToken, IToken endToken) {
		this.methodName = name;
		this.startToken = startToken;
		this.endToken = endToken;
	}

	/** returns the start token */
	public IToken getStartToken() {
		return startToken;
	}

	/** returns end token */
	public IToken getEndToken() {
		return endToken;
	}

	/** returns the method name */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * returns a unique id of the method which includes method identifier and
	 * token offsets. (the identifier is not unique as methods with different
	 * parameters can have the same method identifier)
	 */
	public String getId() {
		return methodName + "." + startToken.getOffset() + "-"
				+ endToken.getOffset();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Method)) {
			return false;
		}
		return ((Method) o).getId().equals(getId());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + getId().hashCode();
		return result;
	}

}
