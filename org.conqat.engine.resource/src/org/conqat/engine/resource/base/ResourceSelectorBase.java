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
package org.conqat.engine.resource.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.commons.findings.FindingReport;
import org.conqat.engine.commons.findings.FindingsList;
import org.conqat.engine.commons.node.DisplayList;
import org.conqat.engine.commons.node.NodeConstants;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IContainer;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.lib.commons.clone.DeepCloneException;

/**
 * Base class for resource selection processors.
 * 
 * @param <R>
 *            the kind of resource handled.
 * 
 * @param <C>
 *            the container implementation which has to match R.
 * 
 * @author $Author: goede $
 * @version $Rev: 44122 $
 * @ConQAT.Rating GREEN Hash: 3F1622BBD8FC4B1743735EFB008CB92A
 */
public abstract class ResourceSelectorBase<R extends IResource, C extends IContainer>
        extends ConQATProcessorBase {

    /** The list of input roots. */
    private final List<IResource> roots = new ArrayList<IResource>();

    /** The comparator used for containers (if any). */
    private Comparator<?> comparator;

    /** {@ConQAT.Doc} */
    @AConQATParameter(name = ConQATParamDoc.INPUT_NAME, minOccurrences = 1, description = ConQATParamDoc.INPUT_DESC)
    public void addRoot(
            @AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) IResource root) {
        roots.add(root);
    }

    /** {@ConQAT.Doc} */
    @AConQATFieldParameter(parameter = "empty", attribute = "allow", description = ""
            + "If this is set to false, a ConQATException gets thrown, if selection resulted in an empty container. Default is false", optional = true)
    public boolean allowEmpty = false;

    /** {@inheritDoc}. */
    @SuppressWarnings("unchecked")
    @Override
    public R process() throws ConQATException {

        comparator = findFirstComparator();

        C result = createRootContainer();
        result.setValue(NodeConstants.COMPARATOR, comparator);
        NodeUtils.addToDisplayList(result, determineDisplayList());
        mergeFindingReports(result);

        Map<String, R> pathToElements = new HashMap<String, R>();
        for (IResource root : roots) {
            DisplayList displayList = NodeUtils.getDisplayList(root);

            for (IElement element : ResourceTraversalUtils.listElements(root)) {
                if (keepElement(element)) {
                    R existing = pathToElements.get(element.getUniformPath());
                    if (existing == null) {
                        pathToElements.put(element.getUniformPath(),
                                (R) element);
                    } else {
                        mergeKeys(existing, (R) element, displayList);
                    }
                }
            }
        }

        if (pathToElements.isEmpty() && !allowEmpty) {
            throw new ConQATException(
                    "Resource selection resulted in empty container!");
        }

        for (R element : pathToElements.values()) {
            insertElement(result, element);
        }

        return (R) result;
    }

    /**
     * Returns the first comparator found at any of the roots or null if none
     * was found.
     */
    private Comparator<?> findFirstComparator() {
        for (IResource root : roots) {
            Comparator<?> c = NodeUtils.getComparator(root);
            if (c != null) {
                return c;
            }
        }
        return null;
    }

    /** Calculates the unified display list. */
    private DisplayList determineDisplayList() {
        DisplayList displayList = new DisplayList();
        for (IResource root : roots) {
            displayList.addAll(NodeUtils.getDisplayList(root));
        }
        return displayList;
    }

    /** Merges all finding reports into the one from the result. */
    private void mergeFindingReports(C result) throws ConQATException {
        FindingReport findingReport = NodeUtils.getFindingReport(result);
        for (IResource root : roots) {
            try {
                FindingReport.copyAll(NodeUtils.getFindingReport(root),
                        findingReport);
            } catch (DeepCloneException e) {
                throw new ConQATException(e);
            }
        }
    }

    /**
     * This method merges keys from the mergee to the existing element. The
     * method is protected to allow for adjustments in sub classes. Only keys
     * from the mergee's display list will be considered during merging.
     */
    @SuppressWarnings("unchecked")
    protected void mergeKeys(R existing, R mergee, DisplayList displayList) {
        for (String key : displayList) {

            // nothing to do if no value set
            Object value = mergee.getValue(key);
            if (value == null) {
                continue;
            }

            // if existing has no value set, just copy over
            Object existingValue = existing.getValue(key);
            if (existingValue == null) {
                if (value instanceof FindingsList) {
                    existing.setValue(key, new FindingsList(
                            (FindingsList) value, existing));
                } else {
                    existing.setValue(key, value);
                }
                continue;
            }

            // ignore equal values
            if (value.equals(existingValue)) {
                continue;
            }

            // specific handling of FindingsLists
            if (value instanceof FindingsList
                    && existingValue instanceof FindingsList) {
                ((FindingsList) existingValue).mergeIn((FindingsList) value);
                continue;
            }

            // be smart about lists
            if (value instanceof Collection<?>
                    && existingValue instanceof Collection<?>) {
                mergeCollections((Collection<Object>) existingValue,
                        (Collection<Object>) value);
                continue;
            }

            getLogger().warn(
                    "Merging of values failed for element "
                            + ((IElement) existing).getUniformPath()
                            + " and key " + key);
        }
    }

    /**
     * Merges all values from the source to the target, unless they already
     * exist in the target.
     */
    private void mergeCollections(Collection<Object> target,
            Collection<Object> source) {
        for (Object value : source) {
            if (!target.contains(value)) {
                target.add(value);
            }
        }
    }

    /**
     * Template method for creating the root element of the container hierarchy.
     */
    protected abstract C createRootContainer();

    /** Creates a new container and sets some defaults. */
    protected C createContainer(String name) {
        C container = createRawContainer(name);
        if (comparator != null) {
            container.setValue(NodeConstants.COMPARATOR, comparator);
        }
        return container;
    }

    /** Template method for creating a raw container (no keys, etc.). */
    protected abstract C createRawContainer(String name);

    /**
     * Template method for inserting an element into the container hierarchy.
     * While the type is a subclass of {@link IResource}, this is called for
     * elements only.
     */
    protected abstract void insertElement(C rootContainer, R element);

    /**
     * Template method that decides for an element whether is should be kept. It
     * is important that this element also is an instance of R. While the type
     * is a subclass of {@link IResource}, this is called for elements only.
     */
    protected abstract boolean keepElement(IResource element);
}