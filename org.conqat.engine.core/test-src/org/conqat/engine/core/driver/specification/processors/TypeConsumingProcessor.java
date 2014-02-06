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
package org.conqat.engine.core.driver.specification.processors;

import java.util.Collection;
import java.util.List;

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.core.IConQATProcessor;
import org.conqat.engine.core.core.IConQATProcessorInfo;

/**
 * This processor consumes all kinds of inputs so we can easily construct
 * interesting cases for input type inference.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 26C3442D93046B4581E1A14BDED2B12B
 */
@AConQATProcessor(description = "desc")
@SuppressWarnings("unused")
public class TypeConsumingProcessor implements IConQATProcessor {

	/** annotation test method */
	@AConQATParameter(description = "", name = "String")
	public void setString(
			@AConQATAttribute(description = "", name = "ref") String value) {
		// nothing to do here
	}

	/** annotation test method */
	@AConQATParameter(description = "", name = "int")
	public void setInt(
			@AConQATAttribute(description = "", name = "ref") int value) {
		// nothing to do here
	}

	/** annotation test method */
	@AConQATParameter(description = "", name = "Integer")
	public void setInteger(
			@AConQATAttribute(description = "", name = "ref") Integer value) {
		// nothing to do here
	}

	/** annotation test method */
	@AConQATParameter(description = "", name = "Number")
	public void setNumber(
			@AConQATAttribute(description = "", name = "ref") Number value) {
		// nothing to do here
	}

	/** annotation test method */
	@AConQATParameter(description = "", name = "Object")
	public void setObject(
			@AConQATAttribute(description = "", name = "ref") Object value) {
		// nothing to do here
	}

	/** annotation test method */
	@SuppressWarnings("rawtypes")
	@AConQATParameter(description = "", name = "Collection")
	public void setCollection(
			@AConQATAttribute(description = "", name = "ref") Collection value) {
		// nothing to do here
	}

	/** annotation test method */
	@SuppressWarnings("rawtypes")
	@AConQATParameter(description = "", name = "List")
	public void setList(
			@AConQATAttribute(description = "", name = "ref") List value) {
		// nothing to do here
	}

	/** {@inheritDoc} */
	@Override
	public void init(IConQATProcessorInfo processorInfo) {
		// nothing to do here
	}

	/** {@inheritDoc} */
	@Override
	public String process() throws ConQATException {
		return null;
	}

}