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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.core.conqatdoc.layout.CQEditMetaData;
import org.conqat.engine.core.driver.instance.EInstanceState;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.ImmutablePair;
import org.conqat.lib.commons.collections.UnmodifiableList;
import org.w3c.dom.Element;

/**
 * Serializable transport object for the running configuration. This
 * encapsulates information from the {@link IInfo} objects (which are not
 * serializable themselves because of the underlying instances).
 * <p>
 * This object is mostly immutable, but the execution state is updated over
 * time.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 36645 $
 * @ConQAT.Rating GREEN Hash: D7EEB650C894873520399BD05E4C50C7
 */
public class ConfigurationTreeTransport implements Serializable {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** The name of the instance. */
	private final String instanceName;

	/** The name of the specification used. */
	private final String specificationName;

	/** The state of the block or processor. */
	private EInstanceState state;

	/** Boolean indicating whether this is a block. */
	private final boolean block;

	/** Child elements. */
	private final List<ConfigurationTreeTransport> children = new ArrayList<ConfigurationTreeTransport>();

	/**
	 * The edges of this configuration stored as pairs of names of the affected
	 * elements (source and target). The edges are mostly stored for
	 * visualization purposes. Edges are between child of this unit and thus are
	 * only meaningful for blocks (as processors have no children).
	 */
	private final List<ImmutablePair<String, String>> edges = new ArrayList<ImmutablePair<String, String>>();

	/**
	 * Decoration elements (parameters and outputs). We use a single list for
	 * both, as they are only used for rendering (decoration) and treated the
	 * same there.
	 */
	private final List<NamedObjectTransportBase> decorations = new ArrayList<NamedObjectTransportBase>();

	/** The XML meta-data stored in the cq.edit key. */
	private final Element cqEditMeta;

	/** Constructor. */
	public ConfigurationTreeTransport(IInfo info) {
		instanceName = info.getInstanceName();
		specificationName = info.getSpecificationName();
		state = info.getState();

		if (info instanceof BlockInfo) {
			block = true;
			cqEditMeta = ((BlockInfo) info)
					.getMeta(CQEditMetaData.CQEDIT_META_DATA_TYPE);
			completeBlock((BlockInfo) info);
		} else {
			block = false;
			cqEditMeta = null;
		}
	}

	/** Completes the block construction by appending child elements. */
	private void completeBlock(BlockInfo block) {
		for (InfoParameter parameter : block.getParameters()) {
			decorations.add(new ParameterTransport(parameter.getName()));
		}

		for (InfoOutput output : block.getOutputs()) {
			decorations.add(new OutputTransport(output.getName()));
			resolveAndInsertEdge(output.getReferenced(), output.getName());
		}

		for (IInfo child : block.getChildren()) {
			children.add(new ConfigurationTreeTransport(child));
			for (InfoParameter param : child.getParameters()) {
				for (InfoAttribute attr : param.getAttributes()) {
					resolveAndInsertEdge(attr.getReferenced(),
							child.getInstanceName());
				}
			}
		}
	}

	/** Resolves the given source reference and inserts the edge. */
	private void resolveAndInsertEdge(InfoRefNode source, String targetName) {
		if (source == null) {
			// this happens for attributes with an immediate value
			return;
		}

		String sourceName = null;
		if (source instanceof InfoOutput) {
			sourceName = ((InfoOutput) source).getInfo().getInstanceName();
		} else if (source instanceof InfoAttribute) {
			sourceName = ((InfoAttribute) source).getParameter().getName();
		} else {
			CCSMAssert.fail("Unknown subclass of " + InfoRefNode.class + ": "
					+ source.getClass());
		}

		edges.add(new ImmutablePair<String, String>(sourceName, targetName));
	}

	/** Returns the instance name. */
	public String getInstanceName() {
		return instanceName;
	}

	/** Returns the specification name. */
	public String getSpecificationName() {
		return specificationName;
	}

	/**
	 * Returns the (dominant) state. If this element has children, the state is
	 * computed from them.
	 */
	public EInstanceState getState() {
		if (children.isEmpty()) {
			return state;
		}

		EInstanceState result = EInstanceState.UNDEFINED;
		for (ConfigurationTreeTransport child : children) {
			result = EInstanceState.merge(result, child.getState());
		}
		return result;
	}

	/** Updates the state stored for this unit. */
	public void updateState(EInstanceState state) {
		this.state = state;
	}

	/** Returns whether this represents a block. */
	public boolean isBlock() {
		return block;
	}

	/** Returns the children. */
	public UnmodifiableList<ConfigurationTreeTransport> getChildren() {
		return CollectionUtils.asUnmodifiable(children);
	}

	/** Returns the edges. */
	public UnmodifiableList<ImmutablePair<String, String>> getEdges() {
		return CollectionUtils.asUnmodifiable(edges);
	}

	/** Returns the decorations. */
	public UnmodifiableList<NamedObjectTransportBase> getDecorations() {
		return CollectionUtils.asUnmodifiable(decorations);
	}

	/** Returns the XML element for the CQ.edit meta data (may be null). */
	public Element getCQEditMeta() {
		return cqEditMeta;
	}

	/** Class for exchanging a parameter or output. */
	public static class NamedObjectTransportBase implements Serializable {

		/** Serial version UID. */
		private static final long serialVersionUID = 1;

		/** The name. */
		private final String name;

		/** Constructor. */
		protected NamedObjectTransportBase(String name) {
			this.name = name;
		}

		/** Returns the name. */
		public String getName() {
			return name;
		}
	}

	/** Transport object for block parameters. */
	public static class ParameterTransport extends NamedObjectTransportBase {

		/** Serial version UID. */
		private static final long serialVersionUID = 1;

		/** Constructor. */
		private ParameterTransport(String name) {
			super(name);
		}
	}

	/** Transport object for block parameters. */
	public static class OutputTransport extends NamedObjectTransportBase {

		/** Serial version UID. */
		private static final long serialVersionUID = 1;

		/** Constructor. */
		private OutputTransport(String name) {
			super(name);
		}
	}
}