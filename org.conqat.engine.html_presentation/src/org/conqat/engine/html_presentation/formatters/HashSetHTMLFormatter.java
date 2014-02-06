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
package org.conqat.engine.html_presentation.formatters;

import static org.conqat.lib.commons.html.EHTMLElement.BR;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import org.conqat.engine.html_presentation.util.PresentationUtils;
import org.conqat.lib.commons.html.HTMLWriter;

/**
 * A HTML formatter for {@link HashSet}s. The formatter sorts the elements in
 * the set if they are of type {@link Comparable}. In case the type is a
 * subclass of {@link HashSet} whose elements already have a defined order (for
 * example {@link LinkedHashSet}), the elements are not sorted.
 * 
 * @author $Author: goede $
 * @version $Rev: 41357 $
 * @ConQAT.Rating YELLOW Hash: 06157DFC811BE686B93E9CD0E32D7AD2
 */
@SuppressWarnings({ "rawtypes" })
public class HashSetHTMLFormatter implements IHTMLFormatter<HashSet> {

    /** {@inheritDoc} */
    @Override
    public void formatObject(HashSet t, HTMLWriter writer) {
        if (t instanceof LinkedHashSet<?> || hasUncomparableElement(t)) {
            formatValues(t, writer);
        } else {
            @SuppressWarnings("unchecked")
            List<Comparable<Object>> values =
                    new ArrayList<Comparable<Object>>(t);
            Collections.sort(values);
            formatValues(values, writer);
        }
    }
    
    /**
     * Checks whether there is an item in the set not implementing
     * {@link Comparable}.
     */
    private boolean hasUncomparableElement(HashSet<?> t) {
        for (Object item : t) {
            if (!(item instanceof Comparable<?>)) {
                return true;
            }
        }
        return false;
    }

    /** Formats all values in the given collection. */
    private void formatValues(Collection<?> values, HTMLWriter writer) {
        for (Object value : values) {
            PresentationUtils.appendValue(value, writer);
            writer.addClosedElement(BR);
        }
    }
}
