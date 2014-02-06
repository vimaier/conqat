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
package org.conqat.engine.java.library;

import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.java.resource.IJavaElement;
import org.conqat.engine.java.resource.IJavaResource;
import org.conqat.engine.java.resource.JavaElementUtils;
import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.io.NullOutputStream;
import org.conqat.lib.commons.string.StringUtils;

import com.sun.javadoc.ClassDoc;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javadoc.JavadocTool;
import com.sun.tools.javadoc.Messager;
import com.sun.tools.javadoc.ModifierFilter;
import com.sun.tools.javadoc.RootDocImpl;

/**
 * Cache implementation for JavaDoc documentation. On the first documentation
 * request (via {@link #getDoc(IJavaElement)} the root element of the element
 * tree the requested element belongs to is determined. JavaDoc facility is
 * called for all class elements that are descendants of this root element. All
 * results are cached. On subsequent calls the documentation is returned
 * straight from the cache.
 * <p>
 * JavaDoc logging information is redirected to log4j. JavaDoc errors are logged
 * as warnings.
 * <p>
 * The current implementation has a number of drawbacks: <br/>
 * <br/>
 * 
 * <ul>
 * <li>It is not memory-sensitive. Caching may cause an out of memory error.</li>
 * <li>It doesn't deal with package documentation.</li>
 * <li>It is very inefficient in the following situation:<br/>
 * <ol>
 * <li>Class documentation is request for a specific element.</li>
 * <li>The documentation is not cached yet so JavaDoc facility is run for the
 * whole tree.</li>
 * <li>Another element is added to tree.</li>
 * <li>A request for that specific (new) element triggers a rerun of the JavaDoc
 * facility for the whole tree.</li>
 * <li>If this should happen inside some kind of iteration performance is
 * doomed.</li>
 * </ol>
 * </li>
 * <li>Due to the crude JavaDoc interface the implementation isn't very elegant.
 * </li>
 * </ul>
 * 
 * @author Florian Deissenboeck
 * @author $Author: heinemann $
 * 
 * @version $Rev: 46960 $
 * @ConQAT.Rating GREEN Hash: ADF5EAE8C1612C92956F27ABC8181C68
 */
/* package */class JavaDocCache {

	/** Logger. */
	private static final Logger LOGGER = Logger.getLogger(JavaDocCache.class);

	/** Cache. This uses the uniform path as key. */
	private final HashMap<String, ClassDoc> cache = new HashMap<String, ClassDoc>();

	/**
	 * Get JavaDoc documentation for a class. If the documentation ist not
	 * cached this starts java doc processing for the whole class and package
	 * tree this class belongs to and caches all documentation nodes.
	 * 
	 * @return the JavaDoc documentation or <code>null</code> if documentation
	 *         couldn't be derived.
	 * @throws ConQATException
	 * 
	 */
	public ClassDoc getDoc(IJavaElement element) throws ConQATException {

		ClassDoc classDoc = cache.get(element.getUniformPath());

		// already cached
		if (classDoc != null) {
			return classDoc;
		}

		// this tree hasn't been cached before, so process and cache it
		cache(element);

		// if it's not in cache now it couldn't be processed
		classDoc = cache.get(element.getUniformPath());
		if (classDoc == null) {
			throw new ConQATException("Couldn't find JavaDoc for element: "
					+ element.getId());
		}

		return classDoc;
	}

	/**
	 * For a given elment find the root of the tree, start JavaDoc processing
	 * for the whole tree and cache all results.
	 * 
	 * @param element
	 *            any Java element of an tree
	 */
	private void cache(IJavaElement element) throws ConQATException {

		IResource rootNode = ResourceTraversalUtils.returnRoot(element);

		Context context = new Context();

		PrintWriter errorWriter = new PrintWriter(new Stream2LoggerAdapter(
				LOGGER, Level.DEBUG, "JavaDoc Error"));
		PrintWriter warningWriter = new PrintWriter(new Stream2LoggerAdapter(
				LOGGER, Level.DEBUG, "JavaDoc Warning"));

		// do not store info messages
		PrintWriter infoWriter = new PrintWriter(new NullOutputStream());

		// This is correct, as the messager attaches itself to the context.
		new SimpleMessager(context, errorWriter, warningWriter, infoWriter);

		JavadocTool tool = JavadocTool.make0(context);

		ModifierFilter showAccess = new ModifierFilter(
				ModifierFilter.ALL_ACCESS);
		String encoding = determineEncoding(rootNode);
		String docLocale = StringUtils.EMPTY_STRING;
		boolean breakiterator = false;
		ListBuffer<String[]> options = new ListBuffer<String[]>();
		ListBuffer<String> includedElements = addAllChildren(rootNode);
		boolean docClasses = false;
		ListBuffer<String> subPackages = new ListBuffer<String>();
		ListBuffer<String> excludedPackages = new ListBuffer<String>();
		boolean quiet = false;

		try {
			RootDocImpl rootDoc = tool.getRootDocImpl(docLocale, encoding,
					showAccess, includedElements.toList(), options.toList(),
					breakiterator, subPackages.toList(),
					excludedPackages.toList(), docClasses, false, quiet);

			if (rootDoc == null) {
				throw new ConQATException("Could not analyze JavaDoc for "
						+ rootNode);
			}

			Map<String, IJavaResource> classLookup = TraversalUtils
					.createIdToNodeMap((IJavaResource) rootNode);
			ClassDoc[] classes = rootDoc.classes();
			for (ClassDoc doc : classes) {
				IJavaResource tmpElement = classLookup.get(doc.qualifiedName());
				if (tmpElement instanceof IJavaElement) {
					cache.put(((IJavaElement) tmpElement).getUniformPath(), doc);
				}
			}
		} catch (Throwable ex) {
			// The dreaded JavaDoc implementation may throw all kinds of stuff,
			// including Errors. Hence, we catch throwable here. Additionally,
			// we minimally support debugging by extracting a somewhat
			// reasonable message.

			String message = ex.getMessage();
			if (message == null) {
				message = ex.getClass().getName();
				message += StringUtils.obtainStackTrace(ex);
			}

			throw new ConQATException(message, ex);
		} finally {
			errorWriter.close();
			warningWriter.close();
			infoWriter.close();
		}

	}

	/**
	 * Determines the encoding to use. Throws an exception if it can not be
	 * determined.
	 */
	private String determineEncoding(IResource rootNode) throws ConQATException {
		Set<Charset> encodings = new HashSet<Charset>();
		for (IJavaElement element : JavaElementUtils.listJavaElements(rootNode)) {
			encodings.add(element.getEncoding());
		}
		if (encodings.size() != 1) {
			throw new ConQATException("Inconsistent encodings!");
		}

		return CollectionUtils.getAny(encodings).name();
	}

	/** Returns a list of all names of source files in the tree. */
	private ListBuffer<String> addAllChildren(IResource root) {
		ListBuffer<String> result = new ListBuffer<String>();
		for (IJavaElement element : JavaElementUtils.listJavaElements(root)) {
			result.append(element.getLocation());
		}
		return result;
	}

	/**
	 * As the constructor of {@link Messager} is protected, we need this class
	 * to create a messager instance.
	 */
	private class SimpleMessager extends Messager {
		/** Create new Messager. */
		protected SimpleMessager(Context context, PrintWriter errWriter,
				PrintWriter warnWriter, PrintWriter noticeWriter) {
			super(context, StringUtils.EMPTY_STRING, errWriter, warnWriter,
					noticeWriter);
		}

		/** Return 0 to make sure JavaDoc continues even if errors were found. */
		@Override
		public int nerrors() {
			return 0;
		}

		/** Return 0 to make sure JavaDoc continues even if warnings were found. */
		@Override
		public int nwarnings() {
			return 0;
		}
	}
}