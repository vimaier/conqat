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

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.commons.keys.IDependencyListKey;
import org.conqat.engine.commons.logging.IncludeExcludeListLogMessage;
import org.conqat.engine.commons.logging.StructuredLogTags;
import org.conqat.engine.commons.node.ListNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.dotnet.ila.xml.IlaXmlReaderBase;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.engine.resource.util.ResourceTraversalUtils;

/**
 * Base class for processors that imports dependencies from ILA Xml files.
 * 
 * @see ILAnalyzerRunnerProcessor
 * 
 * @author $Author: juergens $
 * @version $Revision: 36634 $
 * @ConQAT.Rating GREEN Hash: 0B8BF21E671A3897E70662DC3E6FE4C4
 */
public abstract class ILImporterProcessorBase extends ConQATProcessorBase implements IDependencyListKey{

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Key under which assembly name is stored.", type = "java.lang.String")
	public static final String ASSEMBLY_NAME = "AssemblyName";

	/**
	 * Root of the text resource tree that contains the XML elements containing
	 * IL information that serve as input for this processor.
	 */
	private ITextResource root;

	/** Root of the result tree containing dependency information */
	protected ListNode outputRoot;

	/** Dependencies that match the ignore pattern are thrown away. */
	protected PatternList excludePatterns = new PatternList();

	/**
	 * If this pattern list is set, only dependencies that match one of these
	 * patterns are included. (Mainly useful for debugging purposes to
	 * temporarily reduce the amount dependencies.)
	 */
	protected PatternList includePatterns = null;

	/** Set that stores all dependencies that have been imported */
	protected final Set<String> includedDependencies = new HashSet<String>();

	/** Set that stores all dependencies that have been excluded */
	protected final Set<String> excludedDependencies = new HashSet<String>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.INPUT_NAME, description = "Text resource tree that contains ILA XML output.", minOccurrences = 1, maxOccurrences = 1)
	public void setRoot(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) ITextResource root) {
		this.root = root;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "exclude", description = "Dependencies that match the ignore patterns are thrown away", minOccurrences = 0, maxOccurrences = 1)
	public void setIgnorePatterns(
			@AConQATAttribute(name = "patterns", description = "Regular expressions") PatternList excludePatterns) {
		this.excludePatterns = excludePatterns;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "include", description = ""
			+ "If this pattern list is set, only dependencies matching one of the patterns are included.", minOccurrences = 0, maxOccurrences = 1)
	public void setIncludePatterns(
			@AConQATAttribute(name = "patterns", description = "Regular expressions") PatternList includePatterns) {
		this.includePatterns = includePatterns;
	}

	/** Read dependencies from all IL-XML */
	@Override
	public ListNode process() throws ConQATException {
		logPatterns(includePatterns, true);
		logPatterns(excludePatterns, false);

		outputRoot = new ListNode();
		List<ITextElement> elements = ResourceTraversalUtils
				.listTextElements(root);
		// Sort to make sure they are imported in the same order every time
		Collections.sort(elements, new Comparator<ITextElement>() {
			@Override
			public int compare(ITextElement o1, ITextElement o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		for (ITextElement element : elements) {
			processElement(element);
		}

		NodeUtils.addToDisplayList(outputRoot, DEPENDENCY_LIST_KEY, ASSEMBLY_NAME);
		NodeUtils.addToDisplayList(outputRoot, getKeys());

		logDependencies(includedDependencies, true);
		logDependencies(excludedDependencies, false);

		return outputRoot;
	}

	/** Log dependency include and exclude patterns. */
	private void logPatterns(PatternList patterns, boolean included) {
		if (patterns == null) {
			return;
		}

		List<String> patternStrings = new ArrayList<String>();
		for (Pattern pattern : patterns) {
			patternStrings.add(pattern.toString());
		}

		getLogger().info(
				new IncludeExcludeListLogMessage("dependencies", included,
						patternStrings, StructuredLogTags.PATTERN));
	}

	/** Log included and excluded dependencies. */
	private void logDependencies(Set<String> dependencies, boolean included) {
		getLogger().info(
				new IncludeExcludeListLogMessage("dependencies", included,
						dependencies, StructuredLogTags.FILES));
	}

	/** Read dependencies from a single IL-XML object. */
	private void processElement(ITextElement element) throws ConQATException {
		try {
			IlaXmlReaderBase<?> xmlReader = createXmlReader(element);
			xmlReader.parse();
		} catch (FileNotFoundException e) {
			throw new ConQATException("File not found: ", e);
		} catch (ConQATException ex) {
			getLogger().error("Could not parse file: " + element.getLocation(),
					ex);
		}
	}

	/**
	 * Create actual XML reader
	 */
	protected abstract IlaXmlReaderBase<?> createXmlReader(ITextElement element)
			throws FileNotFoundException, ConQATException;

	/** Default implementation returns empty key list. Subclasses can override. */
	protected String[] getKeys() {
		return new String[] {};
	}

}