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

// import static org.conqat.lib.commons.string.StringUtils.TWO_SPACES;

import static org.conqat.lib.commons.string.StringUtils.CR;
import static org.conqat.lib.commons.string.StringUtils.TWO_SPACES;

import java.util.HashMap;
import java.util.Map;

import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * This class describes the <code>struct</code> data structure used by
 * Targetlink.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 8EA9631CE4CFE4B7E5392A53C442303A
 */
/* package */class TargetlinkStruct {

	/** This maps from name to child struct. */
	private final HashMap<String, TargetlinkStruct> children = new HashMap<String, TargetlinkStruct>();

	/** Maps from parameter name to value. */
	private final HashMap<String, String> parameters = new HashMap<String, String>();

	/** Add child struct. */
	public void addChild(String name, TargetlinkStruct struct) {
		children.put(name, struct);
	}

	/** Set parameter. */
	public void setParameter(String name, String value) {
		parameters.put(name, value);
	}

	/** Get parameters of this struct and all all child structs. */
	public Map<String, String> getParameters() {
		HashMap<String, String> result = new HashMap<String, String>();
		addParameters(result, "");
		return result;
	}

	/** Recursively add paramters to the map. */
	private void addParameters(Map<String, String> map, String prefix) {

		for (String paramName : parameters.keySet()) {
			map.put(prefix + TargetLinkDataResolver.PARAMETER_SEPARATOR
					+ paramName, parameters.get(paramName));
		}

		for (String childName : children.keySet()) {
			TargetlinkStruct child = children.get(childName);
			child.addParameters(map, prefix
					+ TargetLinkDataResolver.PARAMETER_SEPARATOR + childName);
		}
	}

	/** Returns pretty printed struct. This is helpful for debugging purposes. */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("struct (");
		result.append(CR);

		for (String childName : CollectionUtils.sort(children.keySet())) {
			result.append(TWO_SPACES);
			result.append("'");
			result.append(childName);
			result.append("' -> ");
			result.append(StringUtils.prefixLines(children.get(childName)
					.toString(), TWO_SPACES, false));
			result.append(CR);
		}

		for (String paramName : CollectionUtils.sort(parameters.keySet())) {
			result.append(TWO_SPACES);
			result.append("'");
			result.append(paramName);
			result.append("' -> '");
			result.append(parameters.get(paramName));
			result.append("'");
			result.append(CR);
		}

		result.append(")");
		return result.toString();
	}
}