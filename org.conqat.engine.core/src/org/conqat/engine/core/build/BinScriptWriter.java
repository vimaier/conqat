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
package org.conqat.engine.core.build;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.conqat.engine.core.ConQATInfo;
import org.conqat.engine.core.conqatdoc.ConQATDoc;
import org.conqat.engine.core.driver.Driver;
import org.conqat.engine.core.driver.runner.ConQATRunner;
import org.conqat.lib.commons.filesystem.ClassPathUtils;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.options.AOption;
import org.conqat.lib.commons.options.CommandLine;
import org.conqat.lib.commons.options.OptionException;
import org.conqat.lib.commons.options.OptionRegistry;
import org.conqat.lib.commons.string.StringUtils;

/**
 * This is a small program used for creating the "bin" directory and the various
 * start scripts.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 37513 $
 * @ConQAT.Rating GREEN Hash: 78D0A928AA2BCD7FC4B2688959CE2EA0
 */
public class BinScriptWriter {

	/**
	 * The templates. This is not static and filled in the constructor, as the
	 * {@link BinScriptTemplate} class refers to the current instance of
	 * {@link BinScriptWriter} as well.
	 */
	private final List<BinScriptTemplate> templates = new ArrayList<BinScriptTemplate>();

	/** The command line parser. */
	private final CommandLine commandLine = new CommandLine(new OptionRegistry(
			this));

	/** Name of the target directory. */
	private String targetDirectory = "bin";

	/**
	 * If this is true, we generate the files in the style used for the unified
	 * dist.
	 */
	private boolean unifiedStyle = false;

	/** Constructor. */
	private BinScriptWriter(String[] args) {
		try {
			String[] leftOvers = commandLine.parse(args);
			if (leftOvers.length > 0) {
				System.err.println("Unsupported parameter: " + leftOvers[0]);
				printUsageAndExit();
			}
		} catch (OptionException e) {
			System.err.println("Unsupported parameter: " + e.getMessage());
			printUsageAndExit();
		}

		initTemplates();
	}

	/** Prepares the {@link #templates}. */
	private void initTemplates() {
		// windows:
		templates.add(new BinScriptTemplate("bat", "binscript.bat.template",
				";", "%CONQAT_HOME%", "\r\n"));

		// linux and mac
		templates.add(new BinScriptTemplate("sh", "binscript.sh.template", ":",
				"$CONQAT_HOME", "\n"));
	}

	/** Entry point. */
	public static void main(String[] args) {
		try {
			new BinScriptWriter(args).write();
		} catch (IOException e) {
			System.err.println("Could not create bin scripts: "
					+ e.getMessage());
			System.exit(1);
		}
	}

	/** Writes the script files to disk. */
	private void write() throws IOException {
		String bundlesDir = "bundles";
		if (unifiedStyle) {
			bundlesDir = ".";
		}

		for (BinScriptTemplate template : templates) {
			template.applyAndWrite("conqat", Driver.class,
					determineRawClasspath(), Arrays.asList("-c", bundlesDir));
			template.applyAndWrite("conqatdoc", ConQATDoc.class,
					determineRawClasspath(),
					Arrays.asList("-c", bundlesDir, "-o", "conqatdoc"));
			template.applyAndWrite("conqatrun", ConQATRunner.class,
					determineRawClasspath(), Arrays.asList("-c", bundlesDir));
		}
	}

	/**
	 * Determines the classpath used to execute ConQAT. This is "raw" as it does
	 * not contain the CONQAT_HOME prefix for its entries.
	 */
	private Set<String> determineRawClasspath() throws IOException {
		Set<String> classpath = new HashSet<String>();
		if (unifiedStyle) {
			// in the unified style, the JARs are found in the lib directory of
			// the core bundle and the ConQAT code is in its "build" directory
			String coreBundle = ConQATInfo.class.getPackage().getName();
			for (String jar : determineJars()) {
				classpath.add(coreBundle + "/lib/" + jar);
			}
			classpath.add(coreBundle + "/build");
		} else {
			// in the classic setup the JARs are in the top-level "lib"
			// directory and there is a separate conqat.jar
			for (String jar : determineJars()) {
				classpath.add("lib/" + jar);
			}
			classpath.add("lib/conqat.jar");
		}
		return classpath;
	}

	/**
	 * Returns the names of JAR files required by ConQAT (for the startup file).
	 */
	private Set<String> determineJars() throws IOException {
		Set<String> jarNames = new HashSet<String>();
		for (String element : ClassPathUtils.createClassPathAsSet(null,
				ConQATInfo.CLASSPATH_CLASSES)) {
			File file = new File(element);
			if ("jar".equalsIgnoreCase(FileSystemUtils.getFileExtension(file))) {
				jarNames.add(file.getName());
			}
		}
		return jarNames;
	}

	/** Help option: print usage and exit. */
	@AOption(shortName = 'h', longName = "help", description = "print this usage message")
	public void printUsageAndExit() {
		commandLine.printUsage(new PrintWriter(System.err));
		System.exit(1);
	}

	/** Sets the target directory. */
	@AOption(shortName = 'd', longName = "directory", description = "Sets the name of the output directory. Default is \"bin\".")
	public void setTargetDirectory(String targetDirectory) {
		this.targetDirectory = targetDirectory;
	}

	/** Sets unified style to true. */
	@AOption(shortName = 'u', longName = "unified", description = "Create scripts usable with the unified distribution.")
	public void setUnifiedStyle() {
		unifiedStyle = true;
	}

	/** Encapsulation of a template for a bat or shell script. */
	private final class BinScriptTemplate {

		/** The file extension used. */
		private final String fileExtension;

		/** The template. */
		private final String template;

		/** The separator used for the platform. */
		private final String classPathSeparator;

		/** The expression used to reference CONQAT_HOME variable. */
		private final String conqatHome;

		/** Line ending used. */
		private final String lineEnding;

		/** Constructor. */
		public BinScriptTemplate(String fileExtension, String templateName,
				String classPathSeparator, String conqatHome, String lineEnding) {
			this.fileExtension = fileExtension;
			this.classPathSeparator = classPathSeparator;
			this.conqatHome = conqatHome;
			this.lineEnding = lineEnding;

			InputStream stream = BinScriptWriter.class
					.getResourceAsStream(templateName);
			if (stream == null) {
				throw new AssertionError("Template file " + templateName
						+ " not found! Is the code not compiled correctly?");
			}

			try {
				template = FileSystemUtils.readStreamUTF8(stream);
			} catch (IOException e) {
				// I actually can not think of a situation where this happens
				throw new AssertionError("Template file " + templateName
						+ " not readable: " + e.getMessage());
			} finally {
				FileSystemUtils.close(stream);
			}
		}

		/**
		 * Applies the given parameters to the template and writes the result to
		 * a file.
		 */
		public void applyAndWrite(String fileNameBase, Class<?> mainClass,
				Collection<String> rawClassPath, List<String> rawArguments)
				throws IOException {
			String content = template
					.replace("%%MAINCLASS%%", mainClass.getName())
					.replace(
							"%%CLASSPATH%%",
							StringUtils.concat(expandClassPath(rawClassPath),
									classPathSeparator))
					.replace("%%MAINARGS%%",
							StringUtils.concat(expandArguments(rawArguments)));

			content = StringUtils.replaceLineBreaks(content, lineEnding);

			File outputFile = new File(targetDirectory, fileNameBase + "."
					+ fileExtension);
			FileSystemUtils.writeFile(outputFile, content);
			System.out.println("Written " + outputFile);
		}

		/** Expands the "raw" classpath by appending CONQAT_HOME to its entries. */
		private List<String> expandClassPath(Collection<String> rawClassPath) {
			List<String> classPath = new ArrayList<String>();
			for (String entry : rawClassPath) {
				classPath.add(conqatHome + "/" + entry);
			}
			return classPath;
		}

		/**
		 * Expands the "raw" input arguments by prefixing non-option arguments
		 * with CONQAT_HOME.
		 */
		private List<String> expandArguments(List<String> rawArguments) {
			List<String> arguments = new ArrayList<String>();
			for (String arg : rawArguments) {
				if (arg.startsWith("-")) {
					arguments.add(arg);
				} else {
					arguments.add("\"" + conqatHome + "/" + arg + "\"");
				}
			}
			return arguments;
		}

	}
}
