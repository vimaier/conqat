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
package org.conqat.engine.commons.statistics;



import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.util.ConQATInputProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 39856 $
 * @ConQAT.Rating GREEN Hash: 75746340AA0B3A333708D0F4EBEEFB41
 */
@AConQATProcessor(description = "Extracts the string representation of a value "
        + "stored at the root of the tree. If there is no value with the "
        + "specified key, or the value cannot be formatted using the provided "
        + "string, an exception is thrown.")
public class RootValueExtractor extends ConQATInputProcessorBase<IConQATNode> {

    /** Key for the value. */
    private String key;
    
    /** Format string used to format the value before it is returned. */
    private String formatString;
    

    /** {@ConQAT.Doc} */
    @AConQATParameter(name = ConQATParamDoc.READKEY_NAME, minOccurrences = 1, maxOccurrences = 1, description = ""
            + "Key under which the desired value is stored.")
    public void setKey(
            @AConQATAttribute(name = ConQATParamDoc.READKEY_KEY_NAME, description = ConQATParamDoc.READKEY_KEY_DESC) String key) {
        this.key = key;
    }
    
    /** {@ConQAT.Doc} */
    @AConQATParameter(name = "format", minOccurrences = 0, maxOccurrences = 1, description = ""
            + "An optional format string used to format the value before it is returned.")
    public void setFormatString(
            @AConQATAttribute(name = "format", description = "String used to format the value.") String formatString) {
        this.formatString = formatString;
    }
    
    /** {@inheritDoc} */
    @Override
    public String process() throws ConQATException {
        Object value = input.getValue(key);
        
        if (value == null) {
            throw new ConQATException("There is no value with key '" + key +
                    "' stored for the root node.");
        }
        
        if (formatString == null) {
            return value.toString();
        }
        
        try {
            return String.format(formatString, value);
        } catch (IllegalArgumentException e) {
            throw new ConQATException("Value '" + value + "' cannot be " +
                    "formatted using the format string '" + formatString +
                    "'.", e);
        }
    }
}