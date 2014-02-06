/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
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
package org.conqat.engine.architecture.overlap;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.architecture.assessment.shared.IComponent;
import org.conqat.engine.architecture.format.EStereotype;
import org.conqat.engine.architecture.scope.ComponentNode;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.collections.ListMap;
import org.conqat.lib.commons.test.CCSMTestCaseBase;

/**
 * Provides methods for testing the correct detection of overlaps between the
 * include and exclude patterns of individual components.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43816 $
 * @ConQAT.Rating GREEN Hash: 35B010E92092718165FC7D2D80FABDFC
 */
public class OverlapComponentTest extends CCSMTestCaseBase {

	/**
	 * Tests whether the conversion of a tree of architecture components to a
	 * tree of overlap components works correctly.
	 */
	public void testConversion() throws ConQATException {
		OverlapComponent overlapComponent = createSimpleTestArchitecture();
		List<OverlapComponent> children = overlapComponent.getChildren();

		// On the top level there should be two components A and B.
		assertEquals(2, children.size());
		assertEquals("A", children.get(0).getName());
		assertEquals("B", children.get(1).getName());

		// A should not have any children.
		assertEquals(0, children.get(0).getChildren().size());

		// B should have a single child with name C.
		assertEquals(1, children.get(1).getChildren().size());
		assertEquals("C", children.get(1).getChildren().get(0).getName());
	}

	/**
	 * Tests that an overlap between a component and an ancestor of a sibling
	 * component is detected.
	 */
	public void testOverlap() throws ConQATException {
		OverlapComponent overlapComponent = createSimpleTestArchitecture();

		ListMap<IComponent, String> overlaps = overlapComponent
				.checkForOverlaps(null);

		// A and C should conflict as they both include everything. We expect
		// two messages for the same conflict, one for A and one for C.
		assertEquals(2, overlaps.getKeys().size());
	}

	/**
	 * Creates a simple architecture consisting of three components. A and B are
	 * top-level components, C is a child of B. A and C include everything (.*)
	 * while B includes nothing.
	 */
	private OverlapComponent createSimpleTestArchitecture()
	        throws ConQATException {
		List<ComponentNode> components = new ArrayList<ComponentNode>();
		ComponentNode componentA = createAllIncludingComponent("A");
		ComponentNode componentB = new ComponentNode("B", new Point(),
				new Dimension(), EStereotype.NONE);
		ComponentNode componentC = createAllIncludingComponent("C");

		// A and B are top-level components.
		components.add(componentA);
		components.add(componentB);

		// C is a child of B.
		componentB.addChild(componentC);

		return OverlapComponent.convertFromArchitectureComponents(components);
	}

	/** Creates a component with a single pattern that includes everything. */
	private ComponentNode createAllIncludingComponent(String name)
	        throws ConQATException {
		ComponentNode component = new ComponentNode(name, new Point(),
				new Dimension(), EStereotype.NONE);
		component.addIncludeRegex(".*");
		return component;
	}

}
