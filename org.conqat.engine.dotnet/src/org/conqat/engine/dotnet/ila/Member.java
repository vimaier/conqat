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
package org.conqat.engine.dotnet.ila;

/**
 * Value object that stores IL code member information. This class is immutable.
 * 
 * @author juergens
 * @author $Author: juergens $
 * @version $Rev: 46178 $
 * @ConQAT.Rating GREEN Hash: C1492710F54E4EE5257FE9FF6A7CCDFC
 */
public class Member {

	/** Member name */
	private final String name;

	/** Member type */
	private final String type;

	/** Member visibility */
	private final String visibility;

	/** Abstract modifier of member */
	private final boolean isAbstract;

	/** Member metadata token */
	private final String token;

	/** Number of il statements of the member */
	private final int numberIlStatements;

	/** IL statements of the method body */
	private final String ilStatementSequence;

	/** Flag that determines whether member is synthetic */
	private final boolean isSynthetic;

	/** Constructor */
	public Member(String name, String type, String visibility,
			boolean isAbstract, String token, int numberIlStatements,
			String ilStatementSequence, boolean Synthetic) {
		this.name = name;
		this.type = type;
		this.visibility = visibility;
		this.isAbstract = isAbstract;
		this.token = token;
		this.numberIlStatements = numberIlStatements;
		this.ilStatementSequence = ilStatementSequence;
		isSynthetic = Synthetic;
	}

	/** Returns true, if member is a method. */
	public boolean isMethod() {
		return "Method".equals(type);
	}

	/** Returns true, if member is a constructor. */
	public boolean isConstructor() {
		return "Constructor".equals(type) || "StaticConstructor".equals(type);
	}

	/**
	 * Returns true, if member is callable, i.e. either a method or a
	 * constructor
	 */
	public boolean isCallable() {
		return isMethod() || isConstructor();
	}

	/** Returns name. */
	public String getName() {
		return name;
	}

	/** Returns type. */
	public String getType() {
		return type;
	}

	/** Returns visibility. */
	public String getVisibility() {
		return visibility;
	}

	/** Returns isAbstract */
	public boolean isAbstract() {
		return isAbstract;
	}

	/** Returns token. */
	public String getToken() {
		return token;
	}

	/** Returns numberIlStatements. */
	public int getNumberIlStatements() {
		return numberIlStatements;
	}

	/** Returns name. */
	@Override
	public String toString() {
		return name;
	}

	/** Returns body zip. */
	public String getILStatementSequence() {
		return ilStatementSequence;
	}

	/** Returns isSynthetic. */
	public boolean isSynthetic() {
		return isSynthetic;
	}

}