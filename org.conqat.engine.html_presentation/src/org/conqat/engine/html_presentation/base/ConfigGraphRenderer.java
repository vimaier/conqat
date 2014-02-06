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
package org.conqat.engine.html_presentation.base;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.core.conqatdoc.layout.BlockRendererBase;
import org.conqat.engine.core.conqatdoc.layout.CQEditMetaData;
import org.conqat.engine.core.driver.info.BlockInfo;
import org.conqat.engine.core.driver.info.IInfo;
import org.conqat.engine.core.driver.info.InfoAttribute;
import org.conqat.engine.core.driver.info.InfoOutput;
import org.conqat.engine.core.driver.info.InfoParameter;
import org.conqat.engine.core.driver.info.InfoRefNode;
import org.conqat.engine.core.driver.info.ProcessorInfo;
import org.conqat.lib.commons.string.StringUtils;

/**
 * This class provides a static method for creating the dot description of a
 * ConQAT block.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 018BA1038315596E557745A510849F06
 */
/* package */class ConfigGraphRenderer extends BlockRendererBase {

	/** The block info rendered. */
	private final BlockInfo blockInfo;

	/** Constructor. */
	public ConfigGraphRenderer(BlockInfo blockInfo) {
		super(blockInfo.getMeta(CQEditMetaData.CQEDIT_META_DATA_TYPE));
		this.blockInfo = blockInfo;
	}

	/** {@inheritDoc} */
	@Override
	protected List<Object> getChildren() {
		List<Object> result = new ArrayList<Object>();
		result.addAll(blockInfo.getChildren());
		result.addAll(blockInfo.getParameters());
		result.addAll(blockInfo.getOutputs());
		return result;
	}

	/** {@inheritDoc} */
	@Override
	protected String getName(Object child) {
		if (child instanceof IInfo) {
			return StringUtils.getLastPart(((IInfo) child).getInstanceName(),
					'.');
		}
		if (child instanceof InfoParameter) {
			return ((InfoParameter) child).getName();
		}
		if (child instanceof InfoOutput) {
			return ((InfoOutput) child).getName();
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	protected String getImageMapURL(Object child) {
		if (child instanceof BlockInfo) {
			return BaseUtils.getConfigGraphFilename(((BlockInfo) child)
					.getInstanceName());
		}
		if (child instanceof ProcessorInfo) {
			return BaseUtils.getProcessorLogFilename((ProcessorInfo) child);
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	protected EChildType getType(Object child) {
		if (child instanceof ProcessorInfo) {
			return EChildType.PROCESSOR;
		}
		if (child instanceof BlockInfo) {
			return EChildType.BLOCK;
		}
		if (child instanceof InfoParameter) {
			return EChildType.PARAMETER;
		}
		if (child instanceof InfoOutput) {
			return EChildType.OUTPUT;
		}
		throw new AssertionError(
				"Not possible as we do not provide children of type "
						+ child.getClass());
	}

	/** {@inheritDoc} */
	@Override
	protected String getTypeName(Object child) {
		if (child instanceof IInfo) {
			return StringUtils.getLastPart(
					((IInfo) child).getSpecificationName(), '.');
		}
		return StringUtils.EMPTY_STRING;
	}

	/** {@inheritDoc} */
	@Override
	protected void paintEdges(Graphics2D graphics) {
		for (IInfo info : blockInfo.getChildren()) {
			for (InfoParameter param : info.getParameters()) {
				for (InfoAttribute attr : param.getAttributes()) {
					resolveRefAndPaint(graphics, attr.getReferenced(), info);
				}
			}
		}
		for (InfoOutput output : blockInfo.getOutputs()) {
			resolveRefAndPaint(graphics, output.getReferenced(), output);
		}
	}

	/** Override so {@link ConfigJSONWriter} can access it. */
	@Override
	protected boolean getSourceEdgesInvisible(Object source) {
		return super.getSourceEdgesInvisible(source);
	}

	/** Resolves the given source reference and draws the edge. */
	private void resolveRefAndPaint(Graphics2D graphics, InfoRefNode source,
			Object target) {
		if (source instanceof InfoOutput) {
			paintEdge(graphics, ((InfoOutput) source).getInfo(), target);
		} else if (source instanceof InfoAttribute) {
			paintEdge(graphics, ((InfoAttribute) source).getParameter(), target);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected Color getBackgroundColor(Object child) {
		if (child instanceof IInfo) {
			return ((IInfo) child).getState().getColor();
		}
		return super.getBackgroundColor(child);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Make public.
	 */
	@Override
	public Rectangle getBounds(Object child) {
		return super.getBounds(child);
	}
}