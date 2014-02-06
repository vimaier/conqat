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
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.conqat.engine.core.driver.instance.BlockInstance;
import org.conqat.engine.core.driver.instance.EInstanceState;
import org.conqat.engine.core.driver.instance.IInstance;
import org.conqat.engine.core.driver.instance.IValueProvider;
import org.conqat.engine.core.driver.instance.ProcessorInstance;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableList;
import org.conqat.lib.commons.collections.UnmodifiableMap;
import org.w3c.dom.Element;

/**
 * Information on {@link BlockInstance}s.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 87DD3773C61235D4ED41670FD77FFCD7
 */
public class BlockInfo extends InfoBase {

	/** The underlying {@link BlockInstance}. */
	private final BlockInstance instance;

	/** The list of child nodes. */
	private final List<IInfo> children = new ArrayList<IInfo>();

	/** Create a ne block info. */
	public BlockInfo(BlockInstance instance) {
		this(instance, null);
	}

	/** Create a new block info. */
	/* package */BlockInfo(BlockInstance instance, BlockInfo parent) {
		super(instance, parent);
		this.instance = instance;

		initChildren();
		wireUp();
	}

	/** Initialize the list of child infos. */
	private void initChildren() {
		for (IInstance child : instance.getExecutionList()) {
			if (child instanceof ProcessorInstance) {
				children.add(new ProcessorInfo((ProcessorInstance) child, this));
			} else if (child instanceof BlockInstance) {
				children.add(new BlockInfo((BlockInstance) child, this));
			} else {
				throw new IllegalStateException("No other subclass known!");
			}
		}
	}

	/** Setup the references. */
	private void wireUp() {
		Map<IValueProvider, InfoRefNode> lookup = new HashMap<IValueProvider, InfoRefNode>();

		// insert all relevant targets
		for (InfoParameter param : getParameters()) {
			for (InfoAttribute attr : param.getAttributes()) {
				lookup.put(attr.getInstanceAttribute(), attr);
			}
		}
		for (IInfo child : children) {
			for (InfoOutput output : child.getOutputs()) {
				lookup.put(output.getInstanceOutput(), output);
			}
		}

		wireUpOwnOutputs(lookup);
		wireUpChildAttributes(lookup);
	}

	/** Wires up the outputs of this block. */
	private void wireUpOwnOutputs(Map<IValueProvider, InfoRefNode> lookup) {
		for (InfoOutput output : getOutputs()) {
			IValueProvider ref = output.getInstanceOutput().getValueProvider();
			if (ref != null) {
				if (!lookup.containsKey(ref)) {
					throw new IllegalStateException(
							"This should not be possible!");
				}
				output.setReferenced(lookup.get(ref));
			}
		}
	}

	/** Wires up the attributes of child infos. */
	private void wireUpChildAttributes(Map<IValueProvider, InfoRefNode> lookup) {
		for (IInfo child : children) {
			for (InfoParameter param : child.getParameters()) {
				for (InfoAttribute attr : param.getAttributes()) {
					IValueProvider ref = attr.getInstanceAttribute()
							.getValueProvider();
					if (ref != null) {
						if (!lookup.containsKey(ref)) {
							throw new IllegalStateException(
									"This should not be possible!");
						}
						attr.setReferenced(lookup.get(ref));
					}
				}
			}
		}
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public UnmodifiableMap<EInstanceState, Integer> getProcessorStateDistribution() {
		EnumMap<EInstanceState, Integer> result = new EnumMap<EInstanceState, Integer>(
				EInstanceState.class);
		for (IInfo child : children) {
			for (Map.Entry<EInstanceState, Integer> entry : child
					.getProcessorStateDistribution().entrySet()) {

				if (result.containsKey(entry.getKey())) {
					result.put(entry.getKey(),
							entry.getValue() + result.get(entry.getKey()));
				} else {
					result.put(entry.getKey(), entry.getValue());
				}
			}
		}
		return CollectionUtils.asUnmodifiable(result);
	}

	/** Returns the (unmodifiable) list of children of this block. */
	public UnmodifiableList<IInfo> getChildren() {
		return CollectionUtils.asUnmodifiable(children);
	}

	/** {@inheritDoc} */
	@Override
	public EInstanceState getState() {
		EInstanceState result = EInstanceState.UNDEFINED;
		for (IInfo child : children) {
			result = EInstanceState.merge(result, child.getState());
		}
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public long getExecutionTime() {
		long result = 0;
		for (IInfo child : children) {
			result += child.getExecutionTime();
		}
		return result;
	}

	/**
	 * Returns meta data of the given type if it exists. If not, null is
	 * returned. Note that this returns a copy of the stored XML, so the result
	 * should be stored and not queried too often.
	 */
	public Element getMeta(String type) {
		return instance.getDeclaration().getSpecification().getMeta(type);
	}
}