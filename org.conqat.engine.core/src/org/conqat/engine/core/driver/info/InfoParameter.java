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
package org.conqat.engine.core.driver.info;

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.core.driver.instance.InstanceAttribute;
import org.conqat.engine.core.driver.instance.InstanceParameter;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableList;

/**
 * Info on {@link InstanceParameter}s.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: D3206ECD1594FA3665244A05366DB573
 */
public class InfoParameter {

	/** The info object this belongs to. */
	private final IInfo info;

	/** The underlying instance parameter. */
	private final InstanceParameter instanceParameter;

	/** The list of attributes for this parameter. */
	private final List<InfoAttribute> attributes = new ArrayList<InfoAttribute>();

	/** Creates a new info parameter. */
	/* package */InfoParameter(InstanceParameter instanceParameter, IInfo info) {
		this.instanceParameter = instanceParameter;
		this.info = info;
		initAttributes();
	}

	/** Initialize the list of attributes. */
	private void initAttributes() {
		for (InstanceAttribute attr : instanceParameter.getAttributes()) {
			attributes.add(new InfoAttribute(attr, this));
		}
	}

	/** Returns the info object this belongs to. */
	public IInfo getInfo() {
		return info;
	}

	/** Returns the name of this parameter. */
	public String getName() {
		return instanceParameter.getDeclarationParameter().getName();
	}

	/** Returns whether this parameter is synthetic. */
	public boolean isSynthetic() {
		return instanceParameter.isSynthetic();
	}

	/** Returns the (unmodifiable) list of attributes. */
	public UnmodifiableList<InfoAttribute> getAttributes() {
		return CollectionUtils.asUnmodifiable(attributes);
	}
}