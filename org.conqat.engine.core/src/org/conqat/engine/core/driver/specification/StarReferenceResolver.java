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
package org.conqat.engine.core.driver.specification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.conqat.engine.core.driver.declaration.DeclarationAttribute;
import org.conqat.engine.core.driver.declaration.DeclarationOutput;
import org.conqat.engine.core.driver.declaration.DeclarationParameter;
import org.conqat.engine.core.driver.declaration.IDeclaration;
import org.conqat.engine.core.driver.error.BlockFileException;
import org.conqat.engine.core.driver.error.EDriverExceptionType;

/**
 * This class is responsible for resolving references using the star syntax. It
 * simply replaces parameters using star syntax by multiple explicit parameters
 * without star syntax.
 * <p>
 * <b>Example</b><br />
 * <i>Block-Spec</i>
 * 
 * <pre>
 *        &lt;block-spec name=&quot;demo-spec&quot;&gt;
 *        ...    
 *        &lt;out name=&quot;html-junit-table&quot; ref=&quot;@standard-analysis.junit-result&quot; /&gt;
 *        &lt;out name=&quot;html-testcov-treemap&quot; ref=&quot;@standard-analysis.testcov-treemap&quot; /&gt;
 *        &lt;out name=&quot;data-warn-freq&quot; ref=&quot;@standard-analysis.warn-freq&quot; /&gt;
 *        &lt;/block-spec&gt;
 * </pre>
 * 
 * <i>Config-File</i>
 * 
 * <pre>
 *      &lt;block name=&quot;demo&quot; spec=&quot;demo-spec&quot;&gt;
 *      ...
 *      &lt;/block&gt;
 * </pre>
 * 
 * <i>StarResolver...</i>
 * 
 * <pre>
 *     &lt;processor name=&quot;output&quot; class=&quot;Consumer&quot;&gt;
 *      
 *     &lt;result ref=&quot;@demo.html*&quot;  /&gt;
 *     	
 *     &lt;/processor&gt;
 * </pre>
 * 
 * <i>... resolves to:</i>
 * 
 * <pre>
 *     &lt;processor name=&quot;output&quot; class=&quot;Consumer&quot;&gt;
 *      
 *     &lt;result ref=&quot;@demo.html-junit-table&quot;  /&gt;
 *     &lt;result ref=&quot;@demo.html-testcov-treemap&quot;  /&gt;
 *     	
 *     &lt;/processor&gt;
 * </pre>
 * 
 * <p>
 * As this is only used for the initialization phase of block specifications it
 * has package visibility.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 891800EEF29ECA3BE1AD1755BA3BB52B
 */
/* package */class StarReferenceResolver {

	/** The block specification to work on. */
	private final BlockSpecification blockSpecification;

	/** A lookup map from declaration names to actual declarations. */
	private final Map<String, IDeclaration> declarationLookup = new HashMap<String, IDeclaration>();

	/** Create new reference resolver. */
	/* package */StarReferenceResolver(BlockSpecification blockSpecification) {
		this.blockSpecification = blockSpecification;

		// fill lookup map
		for (IDeclaration declaration : blockSpecification.getDeclarationList()) {
			declarationLookup.put(declaration.getName(), declaration);
		}
	}

	/** Perform the actual resolution step. */
	public void resolveAll() throws BlockFileException {
		for (IDeclaration declaration : blockSpecification.getDeclarationList()) {
			resolveStarReferences(declaration);
		}
	}

	/** Performs star reference resolution on a single declaration. */
	private void resolveStarReferences(IDeclaration declaration)
			throws BlockFileException {

		ArrayList<DeclarationParameter> newParameters = new ArrayList<DeclarationParameter>();

		for (DeclarationParameter parameter : declaration.getParameters()) {
			String starReferenceName = getStarReferenceName(parameter);

			// no star reference used
			if (starReferenceName == null) {
				newParameters.add(parameter);
				continue;
			}

			// find parameters a star reference resolves to
			findReplacements(parameter, starReferenceName, newParameters);
		}

		declaration.setParameters(newParameters);
	}

	/**
	 * Returns the name of the declaration refenced by the star or null if no
	 * star operator was used. This is done by searching through all attributes
	 * of the given parameter.
	 */
	private static String getStarReferenceName(DeclarationParameter parameter)
			throws BlockFileException {
		String result = null;

		for (DeclarationAttribute attribute : parameter.getAttributes()) {
			String value = attribute.getValueText();
			if (value.startsWith("@") && value.endsWith("*")) {
				if (result != null && !result.equals(value)) {
					throw new BlockFileException(
							EDriverExceptionType.MULTIPLE_STARS_IN_PARAMETER,
							parameter
									+ " has multiple attributes containing the star operator.",
							parameter);
				}
				result = value;
			}
		}

		return result;
	}

	/**
	 * Finds the parameters a star reference resolves to and stores them in the
	 * newParameters list
	 * 
	 * @param parameter
	 *            the parameter being replaced.
	 * @param starReferenceName
	 *            the value of the reference containing the star operator.
	 * @param newParameters
	 *            a list to append the newly generated parameters to.
	 */
	private void findReplacements(DeclarationParameter parameter,
			String starReferenceName,
			ArrayList<DeclarationParameter> newParameters)
			throws BlockFileException {

		String innerRef = starReferenceName.substring(1,
				starReferenceName.length() - 1);
		String[] parts = innerRef.split("\\.", -1);
		if (parts.length != 2) {
			throw new BlockFileException(
					EDriverExceptionType.ILLEGAL_STAR_EXPRESSION,
					starReferenceName, parameter);
		}
		String declarationName = parts[0];
		String prefix = parts[1];

		IDeclaration referenced = declarationLookup.get(declarationName);
		if (referenced == null) {
			throw new BlockFileException(
					EDriverExceptionType.UNDEFINED_REFERENCE,
					starReferenceName, parameter);
		}

		for (DeclarationOutput output : referenced.getOutputs()) {
			if (output.getName().startsWith(prefix)) {
				String newName = "@" + declarationName;
				if (output.getName().length() > 0) {
					newName += "." + output.getName();
				}
				DeclarationParameter newParam = parameter
						.cloneWithAttributeSubstitution(starReferenceName,
								newName);
				newParameters.add(newParam);
			}
		}
	}

}