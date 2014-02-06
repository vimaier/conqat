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
package org.conqat.engine.core.conqatdoc.content;

import static org.conqat.lib.commons.html.EHTMLElement.BODY;
import static org.conqat.lib.commons.html.EHTMLElement.H1;

import java.io.File;
import java.io.IOException;

import org.conqat.engine.core.conqatdoc.PageGeneratorBase;
import org.conqat.engine.core.driver.error.DriverException;

/**
 * This is the page generator class used for content pages, i.e. such pages
 * shown in the central frame of the documentation. In addition to the
 * {@link PageGeneratorBase} it adds body tags and a caption.
 * 
 * @author Benjamin Hummel
 * @author $Author: juergens $
 * @version $Rev: 35194 $
 * @ConQAT.Rating GREEN Hash: 80836257DDA2ACA64DB85AFDBEB48E1E
 */
public abstract class ContentPageGeneratorBase extends PageGeneratorBase {

	/** Constructor. */
	protected ContentPageGeneratorBase(File targetDirectory) {
		super(targetDirectory);
	}

	/** {@inheritDoc} */
	@Override
	protected void appendBody() throws IOException, DriverException {
		pageWriter.openElement(BODY);
		pageWriter.addClosedTextElement(H1, getPageTitle());
		appendContents();
		pageWriter.closeElement(BODY);
	}

	/** Generate the contents of the page. */
	protected abstract void appendContents() throws IOException,
			DriverException;
}