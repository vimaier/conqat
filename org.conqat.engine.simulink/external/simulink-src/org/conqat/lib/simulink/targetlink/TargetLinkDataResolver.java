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
package org.conqat.lib.simulink.targetlink;

import java.io.StringReader;
import java.util.Map;

import java_cup.runtime.Symbol;
import org.conqat.lib.commons.visitor.IVisitor;
import org.conqat.lib.simulink.builder.SimulinkModelBuildingException;
import org.conqat.lib.simulink.model.SimulinkBlock;
import org.conqat.lib.simulink.model.SimulinkConstants;
import org.conqat.lib.simulink.util.SimulinkUtils;

/**
 * This visitor identifies Targetlink blocks, parses their data, unfolds it and
 * stores it as normal parameters at the block. The parameter names of nestes
 * Targetlink structs are separated by {@value #PARAMETER_SEPARATOR}.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 5B769BAE43B43B278CC2E44221F0DC56
 */
public class TargetLinkDataResolver implements
		IVisitor<SimulinkBlock, SimulinkModelBuildingException> {

	/** Separator for Targetlink parameter names. */
	public static final String PARAMETER_SEPARATOR = "/";

	/**
	 * If this is a Targetlink block, parse Targetlink data, resolve the structs
	 * and stores parameters at the block.
	 */
	@Override
	public void visit(SimulinkBlock block)
			throws SimulinkModelBuildingException {
		if (SimulinkUtils.isTargetlinkBlock(block)) {
			unfoldTargetlinkData(block);
		}
	}

	/**
	 * Parse Targetlink data, resolve the structs and store parameters at the
	 * block. Currently this only analyzes Targetlink data stored at parameter
	 * {@link SimulinkConstants#PARAM_TARGETLINK_DATA}.
	 */
	private void unfoldTargetlinkData(SimulinkBlock block)
			throws SimulinkModelBuildingException {
		String data = block
				.getParameter(SimulinkConstants.PARAM_TARGETLINK_DATA);
		if (data == null) {
			return;
		}

		TargetlinkStruct struct = parseTargetlinkdata(block, data);
		Map<String, String> values = struct.getParameters();
		for (String key : values.keySet()) {
			block.setParameter(SimulinkConstants.PARAM_TARGETLINK_DATA + key,
					values.get(key));
		}
	}

	/** Parse Targetlink data. */
	private TargetlinkStruct parseTargetlinkdata(SimulinkBlock block,
			String data) throws SimulinkModelBuildingException {
		TargetlinkDataScanner scanner = new TargetlinkDataScanner(
				new StringReader(data));
		TargetlinkDataParser parser = new TargetlinkDataParser(scanner);
		Symbol sym;
		try {
			sym = parser.parse();
		} catch (Exception ex) {
			throw new SimulinkModelBuildingException(ex + " in block "
					+ block.getId());
		}

		TargetlinkStruct struct = (TargetlinkStruct) sym.value;
		return struct;
	}
}