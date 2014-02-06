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
package org.conqat.engine.core.conqatdoc.layout;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.conqat.engine.core.conqatdoc.content.BlockSpecificationPageGenerator;
import org.conqat.engine.core.conqatdoc.content.ProcessorSpecificationPageGenerator;
import org.conqat.engine.core.driver.declaration.BlockDeclaration;
import org.conqat.engine.core.driver.declaration.DeclarationAttribute;
import org.conqat.engine.core.driver.declaration.DeclarationOutput;
import org.conqat.engine.core.driver.declaration.DeclarationParameter;
import org.conqat.engine.core.driver.declaration.IDeclaration;
import org.conqat.engine.core.driver.declaration.ProcessorDeclaration;
import org.conqat.engine.core.driver.specification.BlockSpecification;
import org.conqat.engine.core.driver.specification.BlockSpecificationAttribute;
import org.conqat.engine.core.driver.specification.BlockSpecificationOutput;
import org.conqat.engine.core.driver.specification.BlockSpecificationParameter;
import org.conqat.engine.core.driver.util.IInputReferencable;
import org.conqat.lib.commons.string.StringUtils;

/**
 * This class is used for generating the graph and image map for a block
 * specification.
 * 
 * @author Benjamin Hummel
 * @author $Author: juergens $
 * @version $Rev: 35194 $
 * @ConQAT.Rating GREEN Hash: B62312C2321875CA5C132D1A0F1AB49C
 */
public class BlockSpecificationRenderer extends BlockRendererBase {

	/** The block handled here. */
	private final BlockSpecification blockSpecification;

	/** Create a new generator for a bundle details page. */
	public BlockSpecificationRenderer(BlockSpecification blockSpecification) {
		super(blockSpecification.getMeta(CQEditMetaData.CQEDIT_META_DATA_TYPE));
		this.blockSpecification = blockSpecification;
	}

	/** {@inheritDoc} */
	@Override
	protected List<Object> getChildren() {
		List<Object> result = new ArrayList<Object>();
		result.addAll(blockSpecification.getDeclarationList());
		result.addAll(Arrays.asList(blockSpecification.getOutputs()));
		result.addAll(Arrays.asList(blockSpecification.getParameters()));
		return result;
	}

	/** {@inheritDoc} */
	@Override
	protected String getImageMapURL(Object child) {
		if (child instanceof ProcessorDeclaration) {
			return ((ProcessorDeclaration) child).getSpecification().getName()
					+ ProcessorSpecificationPageGenerator.PAGE_SUFFIX;
		}
		if (child instanceof BlockDeclaration) {
			return ((BlockDeclaration) child).getSpecification().getName()
					+ BlockSpecificationPageGenerator.PAGE_SUFFIX;
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	protected String getName(Object child) {
		if (child instanceof IDeclaration) {
			return ((IDeclaration) child).getName();
		}
		if (child instanceof BlockSpecificationParameter) {
			return ((BlockSpecificationParameter) child).getName();
		}
		if (child instanceof BlockSpecificationOutput) {
			return ((BlockSpecificationOutput) child).getName();
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	protected EChildType getType(Object child) {
		if (child instanceof ProcessorDeclaration) {
			return EChildType.PROCESSOR;
		}
		if (child instanceof BlockDeclaration) {
			return EChildType.BLOCK;
		}
		if (child instanceof BlockSpecificationParameter) {
			return EChildType.PARAMETER;
		}
		if (child instanceof BlockSpecificationOutput) {
			return EChildType.OUTPUT;
		}
		throw new AssertionError(
				"Not possible as we do not provide children of type "
						+ child.getClass());
	}

	/** {@inheritDoc} */
	@Override
	protected String getTypeName(Object child) {
		if (child instanceof IDeclaration) {
			return StringUtils.getLastPart(
					((IDeclaration) child).getSpecificationName(), '.');
		}
		return StringUtils.EMPTY_STRING;
	}

	/** {@inheritDoc} */
	@Override
	protected void paintEdges(Graphics2D graphics) {
		for (IDeclaration declaration : blockSpecification.getDeclarationList()) {
			for (DeclarationParameter parameter : declaration.getParameters()) {
				for (DeclarationAttribute attribute : parameter.getAttributes()) {
					resolveRefAndPaint(graphics, attribute.getReference(),
							declaration);
				}
			}
		}

		for (BlockSpecificationOutput output : blockSpecification.getOutputs()) {
			output.getReference();
			resolveRefAndPaint(graphics, output.getReference(), output);
		}
	}

	/** Resolves the given source reference and draws the edge. */
	private void resolveRefAndPaint(Graphics2D graphics,
			IInputReferencable source, Object target) {
		if (source instanceof DeclarationOutput) {
			paintEdge(graphics, ((DeclarationOutput) source).getDeclaration(),
					target);
		} else if (source instanceof BlockSpecificationAttribute) {
			paintEdge(graphics,
					((BlockSpecificationAttribute) source).getParameter(),
					target);
		}
	}

}