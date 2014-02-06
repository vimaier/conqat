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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.conqat.engine.architecture.assessment.shared.ICodeMapping;
import org.conqat.engine.architecture.assessment.shared.IComponent;
import org.conqat.engine.architecture.format.ECodeMappingType;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.ListMap;
import org.conqat.lib.commons.collections.PairList;
import org.conqat.lib.commons.collections.TwoDimHashMap;
import org.conqat.lib.commons.collections.UnmodifiableList;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;

/**
 * Utility component class used for calculation of regex overlaps.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43813 $
 * @ConQAT.Rating GREEN Hash: 9E4EB025D5A35D26DD0D9E0E400CED31
 */
public class OverlapComponent implements Comparable<OverlapComponent> {

	/** The underlying component. */
	private final IComponent component;

	/** Children of this component. */
	private final List<OverlapComponent> children = new ArrayList<OverlapComponent>();

	/**
	 * Stores the automata for the include pattern with exclude pattern already
	 * removed.
	 */
	private final PairList<String, Automaton> includes = new PairList<String, Automaton>();

	/**
	 * The automaton of the language described by all includes and the includes
	 * of all transitive children. This is used for caching during
	 * {@link #checkForOverlaps(Runnable)}.
	 */
	private Automaton mergedIncludes;

	/** Constructor for empty root component. */
	private OverlapComponent() {
		component = null;
	}

	/** Constructor. */
	private OverlapComponent(IComponent component) {
		this.component = component;

		Automaton excludes = null;
		for (ICodeMapping mapping : component.getCodeMappings()) {
			if (mapping.getType() == ECodeMappingType.EXCLUDE) {
				if (excludes == null) {
					excludes = createAutomatonFromMapping(mapping);
				} else {
					excludes = excludes
							.union(createAutomatonFromMapping(mapping));
					excludes.minimize();
				}
			}
		}

		for (ICodeMapping mapping : component.getCodeMappings()) {
			if (mapping.getType() == ECodeMappingType.INCLUDE) {
				Automaton automaton = createAutomatonFromMapping(mapping);
				if (excludes != null) {
					automaton = automaton.minus(excludes);
					automaton.minimize();
				}
				includes.add(mapping.getRegex(), automaton);
			}
		}
	}

	/** Returns the children of this component. */
	/* package */UnmodifiableList<OverlapComponent> getChildren() {
		return CollectionUtils.asUnmodifiable(children);
	}

	/** Returns the name of the underlying component. */
	/* package */String getName() {
		if (component == null) {
			return "ROOT";
		}
		return component.getName();
	}

	/**
	 * Converts a mapping to an automaton accepting the language of the
	 * mapping's regex.
	 */
	private Automaton createAutomatonFromMapping(ICodeMapping mapping) {
		String regex = RegExConverter
				.translatePatternToAutomatonLibraryRegEx(mapping.getRegex());
		return new RegExp(regex, RegExp.INTERSECTION).toAutomaton();
	}

	/** Adds a child component. */
	private void addChild(OverlapComponent child) {
		children.add(child);
	}

	/**
	 * Checks for overlaps and reports and overlaps found as a component to
	 * error message list map.
	 * 
	 * @param progress
	 *            if this parameter is not null, it is executed after each
	 *            completed component. This can be used to treck progress.
	 */
	public ListMap<IComponent, String> checkForOverlaps(Runnable progress) {
		ListMap<IComponent, String> errors = new ListMap<IComponent, String>();
		checkForOverlaps(errors, progress);
		return errors;
	}

	/**
	 * Checks for overlaps and reports and overlaps found as a component to
	 * error message list map.
	 */
	private void checkForOverlaps(ListMap<IComponent, String> errors,
			Runnable progress) {
		for (OverlapComponent child : children) {
			child.checkForOverlaps(errors, progress);
		}

		checkForOverlapsInChildren(errors);

		updateMergedIncludes();

		if (progress != null) {
			progress.run();
		}
	}

	/** Checks for overlaps between child components. */
	private void checkForOverlapsInChildren(ListMap<IComponent, String> errors) {
		Automaton allChildren = null;
		for (int i = 0; i < children.size(); ++i) {
			Automaton childAutomaton = children.get(i).mergedIncludes;
			if (childAutomaton == null) {
				continue;
			}
			if (allChildren == null) {
				allChildren = childAutomaton;
			} else {
				if (findOverlap(allChildren, childAutomaton) != null) {
					reportOverlap(children.get(i), children.subList(0, i),
							errors);
				}

				allChildren = allChildren.union(childAutomaton);
				allChildren.minimize();
			}
		}
	}

	/** Returns an example string for an overlap (or null if no overlap exists). */
	private static String findOverlap(Automaton a1, Automaton a2) {
		Automaton intersection = a1.intersection(a2);
		if (intersection.isEmpty()) {
			return null;
		}

		String example = intersection.getShortestExample(true);
		// we want to replace the nul character, which does not print nicely,
		// however we have to ensure that the replacement is still a valid
		// string. We prefer X as replacement, but test a wider range.
		for (char replacement = 'X'; replacement >= 'A'; replacement--) {
			String result = example.replace('\u0000', replacement);
			if (intersection.run(result)) {
				return result;
			}
		}

		// fallback is to use kind of quoting
		return example.replaceAll("\u0000", "\\\\0");
	}

	/**
	 * Reports an overlap between a component and multiple other components.
	 * This one s expensive, so we only call it if we are sure there is an
	 * overlap.
	 */
	private static void reportOverlap(OverlapComponent component,
			List<OverlapComponent> comparees, ListMap<IComponent, String> errors) {
		TwoDimHashMap<IComponent, String, Automaton> componentIncludes = new TwoDimHashMap<IComponent, String, Automaton>();
		insertIncludesRecursively(component, componentIncludes);

		TwoDimHashMap<IComponent, String, Automaton> compareeIncludes = new TwoDimHashMap<IComponent, String, Automaton>();
		for (OverlapComponent comparee : comparees) {
			insertIncludesRecursively(comparee, compareeIncludes);
		}

		for (IComponent compareeComponent : compareeIncludes.getFirstKeys()) {
			for (String compareeInclude : compareeIncludes
					.getSecondKeys(compareeComponent)) {
				Automaton compareeAutomaton = compareeIncludes.getValue(
						compareeComponent, compareeInclude);
				for (IComponent mainComponent : componentIncludes
						.getFirstKeys()) {
					for (String mainInclude : componentIncludes
							.getSecondKeys(mainComponent)) {
						checkAndReportOverlap(errors, compareeComponent,
								compareeInclude, componentIncludes,
								compareeAutomaton, mainComponent, mainInclude);
					}
				}
			}
		}
	}

	/**
	 * Inserts the includes of the given component and all its descendants into
	 * the given map.
	 */
	private static void insertIncludesRecursively(OverlapComponent component,
			TwoDimHashMap<IComponent, String, Automaton> includes) {
		component.fillIncludes(includes);
		for (OverlapComponent child : component.children) {
			insertIncludesRecursively(child, includes);
		}
	}

	/**
	 * Checks for overlaps and creates an error message if an overlap was found.
	 * The parameters correspond to the local variables in
	 * {@link #reportOverlap(OverlapComponent, List, ListMap)}.
	 */
	private static void checkAndReportOverlap(
			ListMap<IComponent, String> errors, IComponent compareeComponent,
			String compareeInclude,
			TwoDimHashMap<IComponent, String, Automaton> mainIncludes,
			Automaton compareeAutomaton, IComponent mainComponent,
			String mainInclude) {
		String overlap = findOverlap(compareeAutomaton,
				mainIncludes.getValue(mainComponent, mainInclude));
		if (overlap == null) {
			return;
		}
		String message = "Overlapping pattern between components "
				+ compareeComponent.getName() + " (" + compareeInclude
				+ ") and " + mainComponent.getName() + " (" + mainInclude
				+ "). Example: \"" + overlap + "\"";
		errors.add(mainComponent, message);
		errors.add(compareeComponent, message);
	}

	/** Fill the includes of this component and all children into the given map. */
	private void fillIncludes(
			TwoDimHashMap<IComponent, String, Automaton> detailedIncludes) {
		for (int i = 0; i < includes.size(); ++i) {
			detailedIncludes.putValue(component, includes.getFirst(i),
					includes.getSecond(i));
		}
	}

	/** Updates the {@link #mergedIncludes}. */
	private void updateMergedIncludes() {
		List<Automaton> automata = new ArrayList<Automaton>();
		automata.addAll(includes.extractSecondList());
		for (OverlapComponent child : children) {
			if (child.mergedIncludes != null) {
				automata.add(child.mergedIncludes);
			}
		}

		if (automata.isEmpty()) {
			mergedIncludes = null;
		} else {
			mergedIncludes = Automaton.union(automata);
			mergedIncludes.minimize();
		}
	}

	/**
	 * Converts a list of components to a rooted tree of
	 * {@link OverlapComponent}s. The root of this tree will be artificial.
	 */
	public static OverlapComponent convertFromArchitectureComponents(
			Collection<? extends IComponent> components) {
		Map<IComponent, OverlapComponent> componentToOverlap = new IdentityHashMap<IComponent, OverlapComponent>();
		for (IComponent component : components) {
			for (IComponent descendant : component.getDescendants()) {
				// The descendants include the component itself.
				componentToOverlap.put(descendant, new OverlapComponent(
						descendant));
			}
		}

		OverlapComponent root = new OverlapComponent();
		for (OverlapComponent overlap : componentToOverlap.values()) {
			IComponent parent = overlap.component.getParent();
			if (parent == null) {
				root.addChild(overlap);
			} else {
				componentToOverlap.get(parent).addChild(overlap);
			}
		}

		root.sortRecursively();
		return root;
	}

	/**
	 * Sort the children and recursively its children. This is used to make the
	 * order deterministic and hence allows for unit testing of the error
	 * messages.
	 */
	private void sortRecursively() {
		Collections.sort(children);
		for (OverlapComponent child : children) {
			child.sortRecursively();
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Compare by component name.
	 */
	@Override
	public int compareTo(OverlapComponent other) {
		return component.getName().compareTo(other.component.getName());
	}
}
