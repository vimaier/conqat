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
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.commons.xml.XMLResolver;
import org.conqat.lib.commons.xml.XMLWriter;

/**
 * Base class for writing ant files.
 * <p>
 * <i>Note: </i> Creating an object of this class opens a stream that is
 * automatically closed by method {@link #closeProject()}.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 47069 $
 * @ConQAT.Rating GREEN Hash: 4DBE4DD26BBEE9468EEAAE808451A3C7
 */
public class ANTWriter {

	/** The XML writer used to write the ANT file. */
	private final XMLWriter<EANTElement, EANTAttribute> xmlWriter;

	/**
	 * Create a new build file writer.
	 * <p>
	 * <i>Note: </i> Creating an object of this class opens a stream that is
	 * automatically closed by method {@link #closeProject()}.
	 * 
	 * @param file
	 *            file to write to
	 * @throws FileNotFoundException
	 *             if stream could not be created for the the specified file.
	 */
	public ANTWriter(File file) throws FileNotFoundException {
		try {
			xmlWriter = new XMLWriter<EANTElement, EANTAttribute>(
					new PrintStream(file, FileSystemUtils.UTF8_ENCODING),
					new XMLResolver<EANTElement, EANTAttribute>(
							EANTAttribute.class));
		} catch (UnsupportedEncodingException e) {
			throw new AssertionError("UTF-8 should be supported!");
		}
	}

	/**
	 * Create a the ANT task that calls a build file in a specified directory.
	 * 
	 * @param directoryName
	 *            name of the directory where the build file (
	 *            <code>build.xml</code>) resides.
	 * @param target
	 *            name of the target to call
	 */
	public void writeAnt(String directoryName, String target) {
		xmlWriter.openElement(EANTElement.ant);
		xmlWriter.addAttribute(EANTAttribute.dir, directoryName);
		xmlWriter.addAttribute(EANTAttribute.target, target);
		xmlWriter.addAttribute(EANTAttribute.inheritAll, false);
		xmlWriter.closeElement(EANTElement.ant);
	}

	/**
	 * Write ant-xml for directory copy.
	 * 
	 * @param source
	 *            source directory
	 * @param target
	 *            target directory
	 */
	public void writeCopyDir(String source, String target,
			String... excludePatterns) {
		xmlWriter.openElement(EANTElement.copy);
		xmlWriter.addAttribute(EANTAttribute.todir, target);

		// add folders like src and test-src even if they are empty to ensure
		// correct settings of Eclipse projects
		xmlWriter.addAttribute(EANTAttribute.includeEmptyDirs, true);
		writeFileSet(source, excludePatterns);
		xmlWriter.closeElement(EANTElement.copy);
	}

	/** Write ant-xml for single file copy. */
	public void writeCopy(String sourceFile, String targetDir) {
		xmlWriter.openElement(EANTElement.copy);
		xmlWriter.addAttribute(EANTAttribute.file, sourceFile);
		xmlWriter.addAttribute(EANTAttribute.todir, targetDir);
		xmlWriter.closeElement(EANTElement.copy);
	}

	/**
	 * Write a fileset-element.
	 * 
	 * @param source
	 *            source directory
	 */
	public void writeFileSet(String source, String... excludePatterns) {
		xmlWriter.openElement(EANTElement.fileset);
		xmlWriter.addAttribute(EANTAttribute.dir, source);
		if (excludePatterns.length > 0) {
			xmlWriter.addClosedElement(EANTElement.include, EANTAttribute.name,
					"**/*");

			for (String exludePattern : excludePatterns) {
				xmlWriter.addClosedElement(EANTElement.exclude,
						EANTAttribute.name, exludePattern);
			}
		}
		xmlWriter.closeElement(EANTElement.fileset);
	}

	/**
	 * Write a zipfileset-element.
	 * 
	 * @param source
	 *            source zip file
	 */
	public void writeZipFileSet(String source, String... includePatterns) {
		xmlWriter.openElement(EANTElement.zipfileset);
		xmlWriter.addAttribute(EANTAttribute.src, source);
		if (includePatterns.length > 0) {
			xmlWriter.addAttribute(EANTAttribute.includes,
					StringUtils.concat(includePatterns, ","));
		}
		xmlWriter.closeElement(EANTElement.zipfileset);
	}

	/** Write ant-xml for deletion of directory. */
	public void writeDeleteDir(String directoryName) {
		xmlWriter.openElement(EANTElement.delete);
		xmlWriter.addAttribute(EANTAttribute.dir, directoryName);
		xmlWriter.closeElement(EANTElement.delete);
	}

	/**
	 * Write ant-xml for deletion of directory.
	 * 
	 * @param directoryName
	 *            directory to delete.
	 * @param includePatterns
	 *            patterns to include.
	 */
	public void writeDeleteDir(String directoryName, String... includePatterns) {
		xmlWriter.openElement(EANTElement.delete);
		xmlWriter.openElement(EANTElement.fileset);
		xmlWriter.addAttribute(EANTAttribute.dir, directoryName);

		for (String includePattern : includePatterns) {
			xmlWriter.addClosedElement(EANTElement.include, EANTAttribute.name,
					includePattern);
		}

		xmlWriter.closeElement(EANTElement.fileset);
		xmlWriter.closeElement(EANTElement.delete);
	}

	/** Write ant-xml for mkdir */
	public void writeMkDir(String directoryName) {
		xmlWriter.openElement(EANTElement.mkdir);
		xmlWriter.addAttribute(EANTAttribute.dir, directoryName);
		xmlWriter.closeElement(EANTElement.mkdir);
	}

	/**
	 * Open the exec-tag
	 * 
	 * @param executable
	 *            name of the executable.
	 */
	public void openExec(String executable) {
		xmlWriter.openElement(EANTElement.exec);
		xmlWriter.addAttribute(EANTAttribute.executable, executable);

	}

	/** Close the exec tag. */
	public void closeExec() {
		xmlWriter.closeElement(EANTElement.exec);

	}

	/** Write an arg-tag with line attribute. */
	public void writeArg(String arg) {
		xmlWriter.addClosedElement(EANTElement.arg, EANTAttribute.line, arg);
	}

	/**
	 * Create ant-xml for zip file creation.
	 * 
	 * @param zipFilename
	 *            name of the zip file
	 * @param directoryName
	 *            directory to zip.
	 */
	public void writeZip(String zipFilename, String directoryName) {
		xmlWriter.openElement(EANTElement.zip);
		xmlWriter.addAttribute(EANTAttribute.destfile, zipFilename);
		xmlWriter.openElement(EANTElement.fileset);
		xmlWriter.addAttribute(EANTAttribute.dir, ".");
		xmlWriter.openElement(EANTElement.include);
		xmlWriter.addAttribute(EANTAttribute.name, directoryName + "/**");
		xmlWriter.closeElement(EANTElement.include);
		xmlWriter.closeElement(EANTElement.fileset);
		xmlWriter.closeElement(EANTElement.zip);
	}

	/**
	 * Open the jar-tag
	 * 
	 * @param jarDest
	 *            name of the destination JAR file.
	 */
	public void openJar(String jarDest) {
		xmlWriter.openElement(EANTElement.jar);
		xmlWriter.addAttribute(EANTAttribute.destfile, jarDest);

	}

	/** Close the jar tag. */
	public void closeJar() {
		xmlWriter.closeElement(EANTElement.jar);
	}

	/** Open manifest-tag. */
	public void openManifest() {
		xmlWriter.openElement(EANTElement.manifest);
	}

	/** Writes an attribute-tag as (for example) used in manifests. */
	public void writeAttribute(String name, String value) {
		xmlWriter.addClosedElement(EANTElement.attribute, EANTAttribute.name,
				name, EANTAttribute.value, value);
	}

	/** Close manifest-tag. */
	public void closeManifest() {
		xmlWriter.closeElement(EANTElement.manifest);
	}

	/**
	 * Create ant-xml for new target.
	 * 
	 * @param name
	 *            name of the target
	 * @param dependencies
	 *            list of targets this target depends on
	 */
	public void startTarget(String name, String... dependencies) {
		startTarget(name, Arrays.asList(dependencies));
	}

	/**
	 * Create ant-xml for new target.
	 * 
	 * @param name
	 *            name of the target
	 * @param dependencies
	 *            list of targets this target depends on
	 */
	public void startTarget(String name, List<String> dependencies) {
		xmlWriter.openElement(EANTElement.target);
		xmlWriter.addAttribute(EANTAttribute.name, name);

		if (dependencies.isEmpty()) {
			return;
		}

		xmlWriter.addAttribute(EANTAttribute.depends,
				StringUtils.concat(dependencies, ", "));
	}

	/** Close current ant target. */
	public void closeTarget() {
		xmlWriter.closeElement(EANTElement.target);
	}

	/**
	 * Start new ant project
	 * 
	 * @param name
	 *            project name.
	 */
	public void startProject(String name) {
		xmlWriter.openElement(EANTElement.project);
		xmlWriter.addAttribute(EANTAttribute.name, name);

		xmlWriter.addNewLine();

		xmlWriter.addClosedElement(EANTElement.property,
				EANTAttribute.environment, "env");

		xmlWriter.addNewLine();
	}

	/** Close current ant project and close underlying stream. */
	public void closeProject() {
		xmlWriter.closeElement(EANTElement.project);
		xmlWriter.close();
	}

	/** Starts a parallel execution block. */
	public void startParallel() {
		xmlWriter.openElement(EANTElement.parallel);
		xmlWriter.addAttribute(EANTAttribute.threadCount, "4");
		xmlWriter.addNewLine();
	}

	/** Close parallel execution block. */
	public void closeParallel() {
		xmlWriter.closeElement(EANTElement.parallel);
	}

	/** Add new line. */
	public void addNewLine() {
		xmlWriter.addNewLine();
	}

	/**
	 * Add xml header.
	 * 
	 * @param comment
	 *            comment to be added after the header.
	 */
	public void addHeader(String comment) {
		xmlWriter.addHeader("1.0", FileSystemUtils.UTF8_ENCODING);
		xmlWriter.addNewLine();
		xmlWriter.addComment(comment);
		xmlWriter.addNewLine();
	}
}