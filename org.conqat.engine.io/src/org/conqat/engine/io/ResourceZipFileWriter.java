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
package org.conqat.engine.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.conqat.engine.commons.traversal.INodeVisitor;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.IResource;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: pfaller $
 * @version $Rev: 45502 $
 * @ConQAT.Rating YELLOW Hash: DB0415C34B3A1C360AFCA6B1EDC7EF5F
 */
@AConQATProcessor(description = "This processor writes all resource elements to a single zip file.  "
		+ "The elements are organized in the zip file by their uniform path.")
public class ResourceZipFileWriter extends InputFileWriterBase<IResource>
		implements INodeVisitor<IResource, ConQATException> {

	/** Output stream where the content is written. */
	private ZipOutputStream zipOutput;

	/** {@inheritDoc} */
	@Override
	protected void writeToFile(IResource input, File file)
			throws ConQATException, IOException {
		FileOutputStream dest = new FileOutputStream(file);
		zipOutput = new ZipOutputStream(new BufferedOutputStream(dest));
		TraversalUtils.visitAllDepthFirst(this, input);
		zipOutput.close();
	}

	/** {@inheritDoc} */
	@Override
	public void visit(IResource node) throws ConQATException {
		if (!(node instanceof IElement)) {
			return;
		}
		IElement element = (IElement) node;
		try {
			zipOutput.putNextEntry(new ZipEntry(element.getUniformPath()));
			zipOutput.write(element.getContent());
		} catch (IOException e) {
			throw new ConQATException("I/O error while adding element "
					+ element + " to zip file.", e);
		}

	}

}
