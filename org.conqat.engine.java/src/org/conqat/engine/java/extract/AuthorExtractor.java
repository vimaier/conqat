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
package org.conqat.engine.java.extract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.bcel.classfile.JavaClass;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.java.base.JavaAnalyzerBase;
import org.conqat.engine.java.resource.IJavaElement;
import org.conqat.lib.commons.string.StringUtils;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Tag;

/**
 * This processor extracts the authors specified by the JavaDoc authors tags
 * from a class.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: E9A9E1FDAC38B2F775099769D3961572
 */
@AConQATProcessor(description = "This processor extracts the authors specified by "
		+ "the JavaDoc authors tags from a class")
public class AuthorExtractor extends JavaAnalyzerBase {

	/** The key to use for saving. */
	@AConQATKey(description = "Author", type = "java.lang.String")
	public static final String AUTHOR_KEY = "Author";

	/** {@inheritDoc} */
	@Override
	protected void analyze(IJavaElement classElement, JavaClass clazz) {
		try {
			ClassDoc classDoc = javaLibrary.getDoc(classElement);
			classElement.setValue(AUTHOR_KEY, getAuthors(classDoc));
		} catch (ConQATException ex) {
			getLogger().warn("Could not determine author for: " + classElement);
		}

	}

	/** Get authors from a class documentaton. */
	private String getAuthors(ClassDoc doc) {
		List<String> authors = getAuthorList(doc);
		Collections.sort(authors);
		return StringUtils.concat(authors, ", ");
	}

	/** Get authors list from class documentation. */
	private List<String> getAuthorList(ClassDoc doc) {
		ArrayList<String> authors = new ArrayList<String>();
		Tag[] authorTags = doc.tags("author");
		for (Tag authorTag : authorTags) {
			String author = authorTag.text();

			// exclude SVN or CVS keywords
			if (author != null && !author.contains("$")) {
				authors.add(author);
			}
		}
		return authors;
	}

	/** {@inheritDoc} */
	@Override
	protected String[] getKeys() {
		return new String[] { AUTHOR_KEY };
	}
}