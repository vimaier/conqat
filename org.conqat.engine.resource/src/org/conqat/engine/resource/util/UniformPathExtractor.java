/*-----------------------------------------------------------------------+
 | com.teamscale.index
 |                                                                       |
   $Id: codetemplates.xml 18709 2009-03-06 13:31:16Z hummelb $            
 |                                                                       |
 | Copyright (c)  2009-2012 CQSE GmbH                                 |
 +-----------------------------------------------------------------------*/
package org.conqat.engine.resource.util;

import org.conqat.engine.commons.util.ConQATInputProcessorBase;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.resource.IElement;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 18709 $
 * @ConQAT.Rating GREEN Hash: B491BE65BBD473CF5E175303DAFC4DB6
 */
@AConQATProcessor(description = "Returns the uniform path for an element")
public class UniformPathExtractor extends ConQATInputProcessorBase<IElement> {

	/** {@inheritDoc} */
	@Override
	public String process() {
		return input.getUniformPath();
	}
}
