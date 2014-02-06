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
package org.conqat.engine.java.listbuilder;

import java.util.regex.Pattern;

import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.java.base.JavaAnalyzerBase;
import org.conqat.engine.java.resource.IJavaResource;

/**
 * This class contains code shared between different list builders. This is not
 * necessarily _the_ base class to be used for a new list builder, but it might
 * save some work.
 * 
 * @author hummelb
 * @author $Author: juergens $
 * @version $Rev: 35196 $
 * @ConQAT.Rating GREEN Hash: 45268DD0BC8CFF1FCD075A490A72CF78
 */
public abstract class ListBuilderBase extends JavaAnalyzerBase {

	/** List used to ignore some classes. */
	private PatternList blacklist = new PatternList();

	/** ignore or include internal classes */
	private boolean ignoreInternalClasses = true;

	/** Set the ignore list. */
	@AConQATParameter(name = "ignore-list", description = "The black list used to filter out unwanted classes. "
			+ "All classes matching this list are not included in the lists created.", minOccurrences = 0, maxOccurrences = 1)
	public void setBlackList(
			@AConQATAttribute(name = "ref", description = "The black list to be used.") PatternList blacklist) {
		this.blacklist = blacklist;
	}

	/** Choose whether to ignore internal classes. */
	@AConQATParameter(name = "ignore-internals", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "If this is set to true, internal classes are ignored when building the lists. Default is true.")
	public void setIgnoreInternals(
			@AConQATAttribute(name = "value", description = "true or false") boolean ignore) {
		ignoreInternalClasses = ignore;
	}

	/** Ignore inner classes? */
	@Override
	protected void setUp(IJavaResource root) throws ConQATException {
		super.setUp(root);
		if (ignoreInternalClasses) {
			blacklist.add(Pattern.compile("\\$"));
		}
	}

	/** Returns whether the class of given name is matched by the blacklist. */
	protected boolean isBlacklisted(String className) {
		return blacklist.findsAnyIn(className);
	}
}