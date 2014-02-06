/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
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
package org.conqat.engine.java.analyzer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;

import org.conqat.lib.commons.assertion.CCSMAssert;

/**
 * Provides access to the PMD rules including their description and category.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46820 $
 * @ConQAT.Rating YELLOW Hash: 381549E6FFA2246C7061CCC373F233ED
 */
public class PMDRuleManager {

	/** THe singleton instance */
	private static PMDRuleManager instance;

	/** Maps from rule name to description */
	private Map<String, String> descriptions = new HashMap<String, String>();

	/** Maps from rule name to category */
	private Map<String, String> categories = new HashMap<String, String>();

	/** Constructor */
	private PMDRuleManager() {
		try {
			Iterator<RuleSet> it = new RuleSetFactory().getRegisteredRuleSets();
			while (it.hasNext()) {
				RuleSet ruleSet = it.next();
				for (Rule rule : ruleSet.getRules()) {
					descriptions.put(rule.getName(), rule.getDescription());
					categories.put(rule.getName(), ruleSet.getName());
				}
			}
		} catch (RuleSetNotFoundException e) {
			CCSMAssert.fail(e.getMessage());
		}
	}

	/** Returns the singleton instance */
	public static PMDRuleManager getInstance() {
		if (instance == null) {
			instance = new PMDRuleManager();
		}
		return instance;
	}

	/** Returns all rules */
	public Collection<String> getAllRules() {
		return descriptions.keySet();
	}

	/** Returns the description for the given rule */
	public String getDescription(String rule) {
		return descriptions.get(rule);
	}

	/** Returns the category for the given rule */
	public String getCategory(String rule) {
		return categories.get(rule);
	}

}
