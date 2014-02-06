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
package org.conqat.engine.sourcecode.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IContentAccessor;
import org.conqat.engine.resource.base.ContainerBase;
import org.conqat.engine.resource.scope.memory.InMemoryContentAccessor;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.engine.resource.text.TextElement;
import org.conqat.engine.resource.text.filter.base.Deletion;
import org.conqat.engine.resource.text.filter.base.ITextFilter;
import org.conqat.engine.resource.text.filter.base.TextFilterBase;
import org.conqat.engine.resource.text.filter.util.TextFilterChain;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.lib.commons.region.Region;
import org.conqat.lib.scanner.ELanguage;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 45313 $
 * @ConQAT.Rating GREEN Hash: 8729AE9E854C3A68BF67534E25DF03CA
 */
@AConQATProcessor(description = "Each PL/SQL element that is found in the "
		+ "provided scope and contains stored Java code, is replaced by a copy "
		+ "that has a text filter to remove the Java code. In addition, a Java "
		+ "token element is created for the embedded Java code. This processor "
		+ "does not copy any key-value pairs stored for the original element "
		+ "since the text content changes when a filter is added. If you want "
		+ "to preserve any key value pairs, you have to copy them manually. In"
		+ "addition, any previous text filters are deleted.")
public class PLSQLJavaExtractor extends
		ConQATPipelineProcessorBase<ITextResource> {
	/**
	 * Expression to identify the start of an embedded Java block. The heuristic
	 * requires the entire expression to be in one line.
	 */
	private final Pattern startPattern = Pattern
			.compile("(?m)(?i)^CREATE.+JAVA.+SOURCE.+AS$\n");

	/** Expression to identify the end of an embedded Java block. */
	private final Pattern endPattern = Pattern.compile("(?m)^\\s*/\\s*$");

	/**
	 * Filter that removes Java code embedded in PL/SQL code. There is only a
	 * single instance of this filter shared by all elements.
	 */
	private final EmbeddedJavaFilter javaFilter = new EmbeddedJavaFilter();

	/**
	 * Filter that removes all PL/SQL code that surrounds the embedded Java
	 * code. In fact, it is an inversion of the <code>javaFilter</code>. There
	 * is only a single instance of this filter shared by all elements.
	 */
	private final SurroundingPLSQLFilter plsqlFilter = new SurroundingPLSQLFilter();

	/** {@inheritDoc} */
	@Override
	protected void processInput(ITextResource input) throws ConQATException {
		List<TokenElement> elements = ResourceTraversalUtils.listElements(
				input, TokenElement.class);
		for (TokenElement element : elements) {
			if (element.getLanguage() == ELanguage.PLSQL
					&& containsJavaCode(element)) {
				processPLSQLElementWithJavaCode(element);
			}
		}
	}

	/**
	 * Replaces the given element with a copy that has an additional filter to
	 * remove the Java code. Furthermore, a second token element is created that
	 * represents the Java code. The PL/SQL code is removed by an appropriate
	 * filter.
	 */
	private void processPLSQLElementWithJavaCode(TokenElement element)
			throws ConQATException {
		@SuppressWarnings("unchecked")
		ContainerBase<ITextResource> container = (ContainerBase<ITextResource>) element
				.getParent();
		element.remove();

		RegionFilter existingRegionsFilter = new RegionFilter(
				element.getFilteredRegions());

		// Create a copy of the PL/SQL element with an appropriate filter to
		// remove the embedded Java code.
		TextFilterChain javaFilterChain = new TextFilterChain(
				existingRegionsFilter, javaFilter);
		TokenElement plsqlElement = new TokenElement(element.getAccessor(),
				element.getEncoding(), element.getLanguage(), javaFilterChain);
		container.addChild(plsqlElement);

		// Create a new token element for the Java code.
		TextFilterChain plsqlFilterChain = new TextFilterChain(
				existingRegionsFilter, plsqlFilter);
		container.addChild(createJavaElement(plsqlElement, plsqlFilterChain));
	}

	/**
	 * Returns <code>true</code> if the element contains at least one block of
	 * Java code.
	 */
	private boolean containsJavaCode(TokenElement element)
			throws ConQATException {
		return startPattern.matcher(element.getTextContent()).find();
	}

	/**
	 * Creates a Java token element for the filtered regions of the given PL/SQL
	 * element. The filters for the content are effectively inverted. The method
	 * returns <code>null</code> if there is no filtered region with Java code
	 * in the given element.
	 */
	private TokenElement createJavaElement(TextElement plsqlElement,
			ITextFilter filter) throws ConQATException {
		IContentAccessor javaAccessor = new InMemoryContentAccessor(
				plsqlElement.getUniformPath() + ".java",
				plsqlElement.getContent());
		return new TokenElement(javaAccessor, plsqlElement.getEncoding(),
				ELanguage.JAVA, filter);
	}

	/**
	 * Filters a predefined list of regions. The idea is to create an individual
	 * instance of this filter for each element to which the filtered regions
	 * belong.
	 */
	private class RegionFilter extends TextFilterBase {
		/** List of regions that are filtered. */
		private List<Region> regions;

		/** Creates a new instance for the given list of regions. */
		public RegionFilter(List<Region> regions) {
			this.regions = regions;
			if (this.regions == null) {
				this.regions = new ArrayList<Region>();
			}
		}

		/** {@inheritDoc} */
		@Override
		public List<Deletion> getDeletions(String content,
				String elementUniformPath) {
			List<Deletion> deletions = new ArrayList<Deletion>();
			for (Region region : regions) {
				deletions.add(new Deletion(region.getStart(),
						region.getEnd() + 1, true));
			}
			return deletions;
		}
	}

	/**
	 * Base class for filters that operate using the boundaries of Java code
	 * blocks embedded in PL/SQL code.
	 */
	private abstract class PLSQLJavaFilterBase extends TextFilterBase {

		/**
		 * List of code blocks deleted by this filter. Deriving classes are
		 * expected to add their deletions to this list.
		 */
		protected List<Deletion> deletions;

		/** {@inheritDoc} */
		@Override
		public List<Deletion> getDeletions(String content,
				String elementUniformPath) {
			deletions = new ArrayList<Deletion>();

			Matcher startMatcher = startPattern.matcher(content);
			Matcher endMatcher = endPattern.matcher(content);
			while (startMatcher.find()) {
				if (!endMatcher.find(startMatcher.end())) {
					getLogger().warn(
							"Malformed embedded Java code in "
									+ elementUniformPath
									+ ": Cannot find terminating slash.");
					return deletions;
				}
				handleJavaBlock(
						new Region(startMatcher.start(), startMatcher.end()),
						new Region(endMatcher.start(), endMatcher.end()));
			}

			finish(content.length());

			return deletions;
		}

		/**
		 * Handles a block of Java code between the given two regions
		 * (exclusive). The regions refer to the introducing and terminating
		 * part of the PL/SQL statement that contains the Java code.
		 */
		protected abstract void handleJavaBlock(Region start, Region end);

		/**
		 * Finishes filtering for the respective content. Deriving classes can
		 * override this method to perform post-processing operations. The
		 * parameter indicates the total length of the content that has been
		 * processed.
		 */
		protected void finish(@SuppressWarnings("unused") int contentLength) {
			// The default implementation is empty.
		}

	}

	/**
	 * This filter removes all blocks of Java code and the corresponding PL/SQL
	 * statements that contain the Java code.
	 */
	private class EmbeddedJavaFilter extends PLSQLJavaFilterBase {
		/** {@inheritDoc} */
		@Override
		protected void handleJavaBlock(Region start, Region end) {
			deletions.add(new Deletion(start.getStart(), end.getEnd(), true));
		}
	}

	/** This filter removes everything except for blocks of Java code. */
	private class SurroundingPLSQLFilter extends PLSQLJavaFilterBase {

		/** The current offset inside the content. */
		int offset = 0;

		/** {@inheritDoc} */
		@Override
		protected void handleJavaBlock(Region start, Region end) {
			if (start.getEnd() > offset) {
				deletions.add(new Deletion(offset, start.getEnd(), true));
			}
			offset = end.getStart();
		}

		/** {@inheritDoc} */
		@Override
		protected void finish(int contentLength) {
			if (offset < contentLength) {
				deletions.add(new Deletion(offset, contentLength, true));
			}
		}
	}

}
