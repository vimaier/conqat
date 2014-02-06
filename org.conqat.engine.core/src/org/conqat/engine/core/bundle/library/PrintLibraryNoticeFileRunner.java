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
package org.conqat.engine.core.bundle.library;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Comparator;

import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableList;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.options.AOption;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Prints notice information about the 3rd party libraries in the loaded
 * bundles.
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 46538 $
 * @ConQAT.Rating YELLOW Hash: C12FBFE78B75C81C175D1F19749A1E09
 */
public class PrintLibraryNoticeFileRunner extends LibraryDescriptorRunnerBase {

	/**
	 * Compares library descriptors based on their lowercase name, i.e. sorts
	 * them alphabetically.
	 */
	private static final Comparator<LibraryDescriptor> LIBRARY_DESRIPTOR_COMPARATOR = new Comparator<LibraryDescriptor>() {

		@Override
		public int compare(LibraryDescriptor first, LibraryDescriptor second) {
			if (first.getName() == null && second.getName() == null) {
				return 0;
			}

			if (first.getName() == null) {
				return -1;
			}

			if (second.getName() == null) {
				return 1;
			}

			return first.getName().toLowerCase()
					.compareTo(second.getName().toLowerCase());
		}
	};
	/** An optional preamble to be added to the notice information. */
	private String preamble = null;

	/**
	 * Adds a preamble to the notice information, which is specified in the
	 * given file.
	 */
	@AOption(shortName = 'p', longName = "preamble", description = "Adds the preamble in the specified file to the notice information.")
	public void setPreamble(String fileName) throws IOException {
		String content = FileSystemUtils.readFile(new File(fileName));
		preamble = StringUtils.normalizeLineBreaks(content);
	}

	/** {@inheritDoc} */
	@Override
	protected void doRun(PrintStream out) {
		if (preamble != null) {
			out.println(preamble);
		}

		UnmodifiableList<LibraryDescriptor> libraryDescriptors = CollectionUtils
				.asSortedUnmodifiableList(getLibraryDescriptors(),
						LIBRARY_DESRIPTOR_COMPARATOR);
		for (LibraryDescriptor descriptor : libraryDescriptors) {
			printNoticeForDescriptor(out, descriptor);
		}
	}

	/** Prints notice information for the given library descriptor. */
	private void printNoticeForDescriptor(PrintStream out,
			LibraryDescriptor descriptor) {

		out.println(StringUtils.fillString(80, '-'));

		// TODO (BH): What is the benefit of format() method here over simple
		// string concatenation. It seems you were confused yourself, juding
		// from the positional comments.
		// TODO (MP) Personally I prefer format() over concatenation, just the
		// eclipse formatter confused me ;) now directly printing.

		out.println("The " + descriptor.getBundle().getName() + " bundle uses "
				+ descriptor.getName());
		out.println(" (" + descriptor.getWebsite() + "),");
		out.println("licensed under " + descriptor.getLicense().getName());
		out.println(" (" + descriptor.getLicense().getWebsite() + ").");

		String notice = descriptor.getLicense().getNotice();
		if (!StringUtils.isEmpty(notice)) {
			out.println();
			out.println(notice);
		}

	}
}
