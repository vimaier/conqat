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
package org.conqat.engine.java.analyzer;

import net.sourceforge.pmd.lang.LanguageVersion;

import org.apache.bcel.classfile.JavaClass;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.java.base.JavaAnalyzerBase;
import org.conqat.engine.java.resource.IJavaElement;
import org.conqat.engine.java.resource.IJavaResource;

/**
 * PMD analyzer operating on {@link IJavaElement}. The main functionality is
 * implemented in {@link PMDAnalyzerDelegate}. The redundancy with
 * {@link TokenElementPMDAnalyzer} is thereby reduced to the interface (i.e.
 * parameters and their documentation).
 * 
 * @author $Author: hummelb $
 * @version $Rev: 47044 $
 * @ConQAT.Rating GREEN Hash: 3E742A04B51476D8A2A19DDF884A9919
 */
@AConQATProcessor(description = PMDAnalyzerDelegate.PROCESSOR_DESCRIPTION)
public class PMDAnalyzer extends JavaAnalyzerBase {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "PMD Findings", type = "java.util.List<org.conqat.engine.commons.findings.Finding>")
	public static final String KEY = "PMD Findings";

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "category", attribute = "name", optional = true, description = "The name of the finding category. Default is PMD.")
	public String categoryName = "PMD";

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "group", attribute = "name", optional = true, description = ""
			+ "The name of the finding group. Default is to use a group based on the PMD rule that was violated.")
	public String groupName = null;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.WRITEKEY_NAME, attribute = ConQATParamDoc.WRITEKEY_KEY_NAME, optional = true, description = ""
			+ "The name of the key to write findings into. Default is to use "
			+ KEY + ".")
	public String writeKey = KEY;

	/** The delegate */
	private final PMDAnalyzerDelegate delegate = new PMDAnalyzerDelegate();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "rule", description = "Add a PMD rule to be checked.")
	public void addRule(
			@AConQATAttribute(name = "class", description = "Class name for the PMD rule class.") String className,
			@AConQATAttribute(name = "message", description = "Message text to be printed for violations.") String message,
			@AConQATAttribute(name = "values", description = "Additional values to parametrize the rules."
					+ " Must have following format: key=value [;key=value]*", defaultValue = PMDAnalyzerDelegate.DUMMY_DEFAULT_VALUE) String values)
			throws ConQATException {

		delegate.addRule(className, message, values);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "config", description = "Load rules from the given PMD config file "
			+ "that comes with PMD (stored inside pmd.jar).")
	public void addRulesFromConfig(
			@AConQATAttribute(name = "file", description = "Name of config file to load rules from, "
					+ "e.g. rulesets/naming.xml or rulesets/unusedcode.xml") String configFileName)
			throws ConQATException {

		delegate.addRulesFromConfig(configFileName);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "ext-config", description = "Load rules from the given PMD config file.")
	public void addRulesFromExtConfig(
			@AConQATAttribute(name = "file", description = "Name of config file to load rules from") String configFileName)
			throws ConQATException {

		delegate.addRulesFromExtConfig(configFileName);
	}

	/** {@inheritDoc} */
	@Override
	protected void setUp(IJavaResource root) {
		delegate.init(root, categoryName, groupName, writeKey);
	}

	/** {@inheritDoc} */
	@Override
	protected String[] getKeys() {
		return new String[] { writeKey };
	}

	/** Run PMD on a class and add messages about violations. */
	@Override
	protected void analyze(IJavaElement element, JavaClass clazz)
			throws ConQATException {
		delegate.analyze(element, getSourceType(clazz), getLogger());
	}

	/**
	 * Retrieves the Java source version of the given class as it is understood
	 * by PMD.
	 */
	private LanguageVersion getSourceType(JavaClass clazz) {
		switch (clazz.getMajor()) {
		case 45: // There is no constant for Java version 1.1 in PMD, use 1.3.
		case 46: // There is no constant for Java version 1.2 in PMD, use 1.3.
		case 47:
			return LanguageVersion.JAVA_13;
		case 48:
			return LanguageVersion.JAVA_14;
		case 49:
			return LanguageVersion.JAVA_15;
		case 50:
			return LanguageVersion.JAVA_16;
		default:
			return LanguageVersion.JAVA_17;
		}
	}

}