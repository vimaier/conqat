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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.DataFlowHandler;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.JavaDataFlowHandler;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.dfa.DataFlowFacade;
import net.sourceforge.pmd.lang.rule.properties.StringProperty;

import org.conqat.engine.commons.findings.FindingCategory;
import org.conqat.engine.commons.findings.FindingGroup;
import org.conqat.engine.commons.findings.util.FindingUtils;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.IConQATLogger;
import org.conqat.engine.java.library.JavaLibrary;
import org.conqat.engine.java.resource.IJavaElement;
import org.conqat.engine.resource.util.ResourceUtils;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Delegate for PMD analysis. This is to enable {@link PMDAnalyzer} to operate
 * on {@link IJavaElement} and {@link TokenElementPMDAnalyzer} to operate on
 * {@link ITokenElement}.
 * 
 * @author $Author: $
 * @version $Rev: $
 * @ConQAT.Rating GREEN Hash: 9FBD86F1A334DDCF1564123BD3868D47
 */
public class PMDAnalyzerDelegate {

	/** The processor description ot be used for PMD processors. */
	public final static String PROCESSOR_DESCRIPTION = "This is an assessor using PMD. It runs "
			+ "the specified PMD rules on all nodes and adds findings for rule violations. "
			+ "One may define PMD checks to run by either specifying the rule class directly "
			+ "or using one of the configuration files that come with PMD. These are stored "
			+ "within pmd.jar and can be simply referenced by rulesets/<name of the file>, "
			+ "e.g. rulesets/naming.xml. This mechanism also runs rules that are not defined as "
			+ "classes but with PMD's XML syntax.";

	/** Dummy default value to recognize non-existent values. */
	public final static String DUMMY_DEFAULT_VALUE = "<none>";

	/** The rule set to use. */
	private final RuleSet ruleSet = new RuleSet();

	/** Additional rule sets loaded from files. */
	private final List<RuleSets> ruleSetsList = new ArrayList<RuleSets>();

	/** The rule context. */
	private final RuleContext context = new RuleContext();

	/** The finding category */
	private FindingCategory category;

	/** The finding group name */
	private String groupName;

	/** The key to write the findings to */
	private String writeKey;

	/** Initialization */
	public void init(ITokenResource root, String categoryName,
			String groupName, String writeKey) {
		category = NodeUtils.getFindingReport(root).getOrCreateCategory(
				categoryName);
		this.groupName = groupName;
		this.writeKey = writeKey;
	}

	/** Adds the given rule */
	public void addRule(String className, String message, String values)
			throws ConQATException {

		Properties properties = null;
		if (!values.equals(DUMMY_DEFAULT_VALUE)) {
			HashMap<String, String> keyValuePairs = StringUtils
					.getKeyValuePairs(values);
			properties = new Properties();
			properties.putAll(keyValuePairs);
		}
		ruleSet.addRule(loadRule(className, message, properties));
	}

	/** Adds the rule from the given config in pmd.jar */
	public void addRulesFromConfig(String configFileName)
			throws ConQATException {

		try {
			RuleSetFactory ruleSetFactory = new RuleSetFactory();
			ruleSetFactory.setClassLoader(getClass().getClassLoader());
			RuleSets ruleSets = ruleSetFactory.createRuleSets(configFileName);
			ruleSetsList.add(ruleSets);
		} catch (RuleSetNotFoundException e) {
			throw new ConQATException("Couldn't find RuleSet: "
					+ configFileName, e);
		}
	}

	/** Adds the rule from the given external config */
	public void addRulesFromExtConfig(String configFileName)
			throws ConQATException {

		try {
			RuleSetFactory ruleSetFactory = new RuleSetFactory();
			ruleSetFactory.setClassLoader(new ClassLoader(getClass()
					.getClassLoader()) {
				/** {@inheritDoc} */
				@Override
				public InputStream getResourceAsStream(String name) {
					InputStream in = super.getResourceAsStream(name);
					if (in == null) {
						// if not found on class path, locate via file system
						File file = new File(name);
						if (file.canRead()) {
							try {
								return new FileInputStream(file);
							} catch (FileNotFoundException e) {
								// give up
							}
						}
					}
					return in;
				}
			});
			RuleSet ruleSet = ruleSetFactory.createRuleSet(configFileName);
			ruleSetsList.add(new RuleSets(ruleSet));
		} catch (RuleSetNotFoundException e) {
			throw new ConQATException("Error adding rules from config: "
					+ e.getMessage(), e);
		}
	}

	/** Run PMD on a class and add messages about violations. */
	public void analyze(ITokenElement element, LanguageVersion languageVersion,
			IConQATLogger logger) throws ConQATException {

		ASTCompilationUnit unit;
		try {
			unit = JavaLibrary.getInstance().getAST(element);
		} catch (ConQATException e) {
			logger.warn("A parsing problem occured for class "
					+ element.getId());
			return;
		}

		initDFA(unit);
		List<Node> units = new ArrayList<Node>();
		units.add(unit);

		context.setReport(new Report());
		context.setSourceCodeFilename(element.getLocation());
		context.setLanguageVersion(languageVersion);

		ruleSet.apply(units, context);
		try {
			for (RuleSets rs : ruleSetsList) {
				rs.apply(units, context, Language.JAVA);
			}
		} catch (RuntimeException e) {
			// fix for CR 1247
			logger.warn("PMD caused an error for '" + element + "': "
					+ e.getMessage());
		}
		evaluateReport(element, context.getReport());
	}

	/** Evaluates the PMD report an labels the class correspondingly. */
	private void evaluateReport(ITokenElement element, Report report)
			throws ConQATException {
		Iterator<RuleViolation> it = report.iterator();
		while (it.hasNext()) {
			RuleViolation violation = it.next();

			String ruleIdentifier = violation.getRule().getName();

			FindingGroup findingGroup;
			if (groupName != null) {
				findingGroup = category.getOrCreateFindingGroup(groupName);
			} else {
				findingGroup = FindingUtils
						.getOrCreateFindingGroupAndSetRuleId(category,
								ruleIdentifier, ruleIdentifier);
			}

			ResourceUtils.createAndAttachFindingForFilteredLine(findingGroup,
					violation.getDescription(), element,
					violation.getBeginLine(), writeKey);
		}
	}

	/**
	 * Load a rule using reflection.
	 * 
	 * @throws ConQATException
	 *             if loading failed.
	 */
	private Rule loadRule(String className, String message,
			Properties properties) throws ConQATException {
		try {
			Class<?> ruleClass = Class.forName(className);
			Rule rule = (Rule) ruleClass.newInstance();
			rule.setMessage(message);
			if (properties != null) {
				for (Object key : properties.keySet()) {
					String value = properties.getProperty(key.toString());
					rule.setProperty(new StringProperty(key.toString(), "", "",
							0), value);
				}
			}
			return rule;
		} catch (ClassNotFoundException e) {
			throw new ConQATException("Couldn't load rule: " + className, e);
		} catch (InstantiationException e) {
			throw new ConQATException("Couldn't load rule: " + className, e);
		} catch (IllegalAccessException e) {
			throw new ConQATException("Couldn't load rule: " + className, e);
		}
	}

	/** Initialized data flow analysis if needed. */
	private void initDFA(ASTCompilationUnit unit) {
		boolean needsDFA = ruleSet.usesDFA(Language.JAVA);
		for (RuleSets rs : ruleSetsList) {
			if (rs.usesDFA(Language.JAVA)) {
				needsDFA = true;
			}
		}
		if (needsDFA) {
			DataFlowHandler dataFlowHandler = new JavaDataFlowHandler();
			new DataFlowFacade().initializeWith(dataFlowHandler, unit);
		}
	}

}
