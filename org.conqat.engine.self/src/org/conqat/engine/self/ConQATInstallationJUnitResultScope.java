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
package org.conqat.engine.self;

import java.io.File;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.bundle.BundleInfo;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.java.junit.JUnitResultNode;
import org.conqat.engine.java.junit.JUnitResultScope;
import org.conqat.engine.self.scope.ConQATBundleNode;
import org.conqat.engine.self.scope.ConQATInstallationRoot;

/**
 * A scope that acts like the JUnitResultScope but uses the
 * ConQATInstallationRoot as input.
 * 
 * @author deissenb
 * @author $Author: juergens $
 * @version $Rev: 35201 $
 * @ConQAT.Rating GREEN Hash: 37C492B20CC2F811D28B9F4D79681F0D
 */
@AConQATProcessor(description = "This processor creates a JUnitResultNode from the "
		+ "given ConQATInstallationRoot including ConQAT and all its bundles.")
public class ConQATInstallationJUnitResultScope extends ConQATProcessorBase {

	/** The ConQAT installation to look at. */
	private ConQATInstallationRoot conqatRoot;

	/** Set the ConQAT installation. */
	@AConQATParameter(name = "conqat", minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "The ConQAT installation being used as source for JUnit results.")
	public void setConQATInstallationRoot(
			@AConQATAttribute(name = "root", description = "Reference to the generating processor.") ConQATInstallationRoot conqatRoot) {
		this.conqatRoot = conqatRoot;
	}

	/** {@inheritDoc} */
	@Override
	public JUnitResultNode process() throws ConQATException {
		JUnitResultScope scope = new JUnitResultScope();
		scope.init(getProcessorInfo());

		addConQAT(scope);
		addBundles(scope);

		return scope.process();
	}

	/** Adds the ConQAT directory. */
	private void addConQAT(JUnitResultScope scope) {
		scope.addFilename(new File(conqatRoot.getConQATDirectory(),
				"log/junit/TESTS-TestSuites.xml").toString());
	}

	/** Adds all bundle directories. */
	private void addBundles(JUnitResultScope scope) {
		for (ConQATBundleNode bundleNode : conqatRoot.getChildren()) {
			BundleInfo bundle = bundleNode.getBundleInfo();

			scope.addFilename(new File(bundle.getLocation(),
					"log/junit/TESTS-TestSuites.xml").toString());
		}
	}
}