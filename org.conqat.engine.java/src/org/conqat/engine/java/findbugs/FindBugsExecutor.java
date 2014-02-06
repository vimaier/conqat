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
package org.conqat.engine.java.findbugs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.execution.JavaExecutorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.java.BundleContext;
import org.conqat.engine.java.resource.IJavaResource;
import org.conqat.engine.java.resource.JavaContext;
import org.conqat.engine.java.resource.JavaElementUtils;
import org.conqat.lib.commons.filesystem.CanonicalFile;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author deissenb
 * @author $Author: heinemann $
 * @version $Rev: 46407 $
 * @ConQAT.Rating GREEN Hash: 0A40C31366199A7CA70A729D6EAD1F46
 */
@AConQATProcessor(description = "Processor to execute FindBugs on a JavaScope. "
		+ "FindBugs can be obtained from http://findbugs.sourceforge.net/.")
public class FindBugsExecutor extends JavaExecutorBase {

	/** Root element of the scope. */
	private IJavaResource root;

	/** The context that is extracted. */
	private JavaContext context;

	/** The findbugs jar file. */
	private File findBugsJar;

	/** {ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "nested-jars", attribute = "include", optional = true, description = "If true, all classes from nested jar files will be included in the analysis. Default ist false.")
	public boolean inlcudeNestedJars = false;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "output-file", description = "Name of the FindBugs output file", minOccurrences = 1, maxOccurrences = 1)
	public void setOutputFile(
			@AConQATAttribute(name = "value", description = "Ouput file path") String outputFile) {
		this.outputFile = outputFile;
	}

	/** {@ConQAT.Doc} */
	@Override
	@AConQATParameter(name = "arg", description = "Additional arguments passed to FindBugs. "
			+ "This can be used to fine-tune the execution of FindBugs. FindBugs parameters "
			+ "are documented at http://findbugs.sourceforge.net/manual/running.html#commandLineOptions. "
			+ "However, there's a couple of more parameters, e.g. to limit analysis to certain "
			+ "detector, that can be obtained by running FindBugs without any parameters on the "
			+ "command line.")
	public void addArgument(
			@AConQATAttribute(name = "value", description = "Argument value") String argument) {
		super.addArgument(argument);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.INPUT_NAME, description = ConQATParamDoc.INPUT_DESC, minOccurrences = 1, maxOccurrences = 1)
	public void setInput(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) IJavaResource root) {
		this.root = root;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "findbugs-home", description = "The location where FindBugs is installed. "
			+ "If not set, the FindBugs version that comes with ConQAT is used.", minOccurrences = 0, maxOccurrences = 1)
	public void setFindBugsHome(
			@AConQATAttribute(name = "path", description = "FindBugs install path.") String findBugsHome)
			throws ConQATException {
		findBugsJar = FileSystemUtils.newFile(new File(findBugsHome), "lib",
				"findbugs.jar");
		if (!findBugsJar.canRead()) {
			throw new ConQATException("Findbugs could not be located at "
					+ findBugsHome);
		}
	}

	/** {@inheritDoc} */
	@Override
	public String process() throws ConQATException {
		context = JavaElementUtils.getUniqueContext(root);
		if (context.hasMissingFiles()) {
			throw new ConQATException(
					"The following files were not found on the file system: "
							+ StringUtils.CR
							+ StringUtils.concat(context.getMissingFiles(),
									StringUtils.CR));
		}
		super.process();
		return outputFile;
	}

	/** Returns path of findbugs.jar. */
	@Override
	protected String getExecuteeName() {
		if (findBugsJar != null) {
			return findBugsJar.getAbsolutePath();
		}
		return getFindbugsJarDefaultLocation();
	}

	/** Returns the default location of the findbugs.jar file. */
	/* package */static String getFindbugsJarDefaultLocation() {
		return BundleContext.getInstance().getResourceManager()
				.getAbsoluteResourcePath("findbugs/lib/findbugs.jar");
	}

	/** Get arguments required to execute FindBugs. */
	@Override
	protected List<String> getArguments() {
		ArrayList<String> result = new ArrayList<String>();
		result.add("-textui");
		result.add("-output");
		result.add(outputFile);

		if (!inlcudeNestedJars) {
			result.add("-nested:false");
		}

		String auxClasspath = determineAuxClassPath();

		if (!StringUtils.isEmpty(auxClasspath)) {
			result.add("-auxclasspath");
			result.add(auxClasspath);
		}

		result.add("-sourcepath");
		result.add(StringUtils.concat(context.getSourceDirectories(),
				File.pathSeparator));
		result.addAll(arguments);

		for (CanonicalFile byteCodeDir : context.getByteCodeDirectories()) {
			result.add(byteCodeDir.getCanonicalPath());
		}
		return result;
	}

	/**
	 * Determine the auxiliary class path. This method is required as the
	 * {@link JavaContext} includes the byte code directories in the classpath.
	 * Hence, this needs to be removed to determine the <em>auxiliary</em> class
	 * path.
	 */
	private String determineAuxClassPath() {
		ArrayList<String> scopeClassPath = new ArrayList<String>(
				context.getClassPath());
		for (String pathElement : context.getClassPath()) {
			for (CanonicalFile byteCodeDirectory : context
					.getByteCodeDirectories()) {
				if (pathElement.equals(byteCodeDirectory.getCanonicalPath())) {
					scopeClassPath.remove(pathElement);
				}
			}
		}
		return StringUtils.concat(scopeClassPath, File.pathSeparator);
	}

}