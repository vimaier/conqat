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
package org.conqat.engine.commons.scope;

import junit.framework.TestCase;
import org.conqat.engine.commons.input.PropertiesFileReader;
import org.conqat.engine.core.core.ConQATException;

/**
 * Test for <code>PropertiesFileReader</code>-processor.
 * 
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @levd.rating GREEN Hash: 151CD6FDAFA538B15CC67A005BEFC91A
 */
public class PropertiesFileReaderTest extends TestCase {
    /** Processor under test. */
    private final static PropertiesFileReader processor = new PropertiesFileReader();

    /** Test with non-existent file. */
    public void testFileNotFound() {
        processor.setKey("xyz");
        processor.setFilename("xzy");

        try {
            processor.process();
            fail();
        } catch (ConQATException e) {
            // this is expected
        }
    }

    /** Test with non-present key. */
    public void testKeyNotPresent() {
        processor.setKey("xyz");
        processor
                .setFilename("test-data/edu.tum.cs.conqat.scope.value/test.properties");

        try {
            processor.process();
            fail();
        } catch (ConQATException e) {
            // this is expected
        }
    }

    /** Test normal behavior. */
    public void testOk() throws ConQATException {
        processor.setKey("key1");
        processor
                .setFilename("test-data/edu.tum.cs.conqat.scope.value/test.properties");

        String value = processor.process();

        assertEquals("value1", value);
    }
}