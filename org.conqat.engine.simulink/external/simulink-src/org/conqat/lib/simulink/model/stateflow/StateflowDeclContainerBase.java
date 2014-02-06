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
package org.conqat.lib.simulink.model.stateflow;

import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.IdentityHashSet;
import org.conqat.lib.commons.collections.UnmodifiableSet;

/**
 * Base class for classes that contain Stateflow declarations.
 * 
 * @param
 * <P>
 * Type of the parent of this node.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: C9477DB7EA25FFA7312E15230D23F73E
 */
public abstract class StateflowDeclContainerBase<P extends IStateflowElement<?>>
		extends StateflowElementBase<P> {

	/** Set of Stateflow dates. */
	private final IdentityHashSet<StateflowData> dates = new IdentityHashSet<StateflowData>();

	/** Set of Stateflow events. */
	private final IdentityHashSet<StateflowEvent> events = new IdentityHashSet<StateflowEvent>();

	/** Create new declaration container. */
	/* package */StateflowDeclContainerBase() {
		super();
	}

	/** Copy constructor for deep cloning. */
	/* package */StateflowDeclContainerBase(
			StateflowDeclContainerBase<? extends P> orig) {
		super(orig);

		for (StateflowData data : orig.dates) {
			addData(data.deepClone());
		}

		for (StateflowEvent event : orig.events) {
			addEvent(event.deepClone());
		}
	}

	/** Add Stateflow data. */
	public void addData(StateflowData data) {
		dates.add(data);
		data.setParent(this);
	}

	/** Add Stateflow event. */
	public void addEvent(StateflowEvent event) {
		events.add(event);
		event.setParent(this);
	}

	/** Get Stateflow data objects. */
	public UnmodifiableSet<StateflowData> getData() {
		return CollectionUtils.asUnmodifiable(dates);
	}

	/** Get Stateflow events objects. */
	public UnmodifiableSet<StateflowEvent> getEvents() {
		return CollectionUtils.asUnmodifiable(events);
	}

	/** Remove Stateflow data object. */
	/* package */void removeData(StateflowData data) {
		CCSMPre.isTrue(data.getParent() == this,
				"Data object must belong to container to be removed.");
		dates.remove(data);
		data.setParent(null);
	}

	/** Remove Stateflow event object. */
	/* package */void removeEvent(StateflowEvent event) {
		CCSMPre.isTrue(event.getParent() == this,
				"Event must belong to container to be removed.");
		events.remove(event);
		event.setParent(null);
	}
}