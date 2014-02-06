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
package org.conqat.engine.simulink.scope;

import java.nio.charset.Charset;

import org.conqat.engine.resource.IContentAccessor;
import org.conqat.engine.resource.text.TextElement;

import org.conqat.lib.commons.clone.DeepCloneException;
import org.conqat.lib.simulink.model.SimulinkModel;

/**
 * Basic implementation for {@link ISimulinkElement}.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @levd.rating GREEN Hash: 05FFB559C5D3283D3BE4A1B380F21F3A
 */
public class SimulinkElement extends TextElement implements
		ISimulinkElement {

	/** The encapsulated model. */
	private final SimulinkModel model;

	/** Constructor. */
	protected SimulinkElement(IContentAccessor accessor, Charset encoding,
			SimulinkModel model) {
		super(accessor, encoding);
		this.model = model;
	}

	/** Copy constructor. */
	protected SimulinkElement(SimulinkElement element)
			throws DeepCloneException {
		super(element);
		model = element.model.deepClone();
	}

	/** Returns <code>null</code>. Must override this to fulfill interface. */
	@Override
	public ISimulinkElement[] getChildren() {
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public SimulinkElement deepClone() throws DeepCloneException {
		return new SimulinkElement(this);
	}

	/** {@inheritDoc} */
	@Override
	public SimulinkModel getModel() {
		return model;
	}
}