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
package org.conqat.engine.architecture.assessment.shared;

import java.io.File;
import java.util.Collections;
import java.util.Set;

import org.conqat.engine.architecture.scope.ArchitectureDefinition;
import org.conqat.engine.architecture.scope.ArchitectureDefinitionReader;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.testutils.ProcessorInfoMock;
import org.conqat.lib.commons.collections.SetMap;
import org.conqat.lib.commons.test.CCSMTestCaseBase;

/**
 * This class contains test cases for testing the functionality of the
 * {@link ArchitectureAssessor}.
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 44257 $
 * @ConQAT.Rating GREEN Hash: 028B4424F6CE6029B15411BC7F1F1068
 */
public class ArchitectureAssessorTest extends CCSMTestCaseBase {

    /**
     * Architecture assessor used for testing. The underlying architecture has
     * overlapping regular expressions and some of the types match more than one
     * component.
     */
    private ArchitectureAssessor assessor;

    /** Architecture with overlapping regular expressions used for this test. */
    private ArchitectureDefinition architecture;

    /** {@inheritDoc} */
    @Override
    protected void setUp() throws ConQATException {
        architecture = loadArchitecture();

        assessor =
                new ArchitectureAssessor(architecture,
                        createTypeLevelDependencies());

        // Since architecture and types have been specifically created for this
        // test, there should be no orphans.
        assertTrue(assessor.getOrphans().isEmpty());
    }

    /**
     * Tests that a type is always mapped to the same component in case it
     * matches multiple components. Although the architecture editor should
     * ensure that no overlaps are possible, these may still exist (for example
     * if the architecture is edited by hand with a text editor.)
     */
    public void testDeterministicMapping() {
        assertMapping("A", "a", "d");
        assertMapping("B", "b", "e");
        assertMapping("C", "c");
    }

    /**
     * Tests whether the given types (and only these) are mapped to the
     * component with the specified name.
     */
    private void assertMapping(String componentName, String... types) {
        IComponent component = architecture.getComponentByName(componentName);
        Set<String> mappedTypes = assessor.getMappedTypes(component);
        assertEquals("Wrong number of types mapped to component '"
                + componentName + "': ", types.length, mappedTypes.size());
        for (String type : types) {
            assertTrue("Type " + type
                    + " is expected to be mapped to component " + componentName
                    + " but is not.", mappedTypes.contains(type));
        }
    }

    /**
     * Loads the architecture explicitly specified for testing the functionality
     * of the {@link ArchitectureAssessor}.
     */
    private ArchitectureDefinition loadArchitecture() throws ConQATException {
        File architectureFile = useTestFile("overlaps.architecture");
        ArchitectureDefinitionReader reader =
                new ArchitectureDefinitionReader();
        reader.init(new ProcessorInfoMock());
        reader.setInputFile(architectureFile);
        return reader.process();
    }

    /**
     * Create hypothetical type-level dependencies for testing. Since we need
     * only the types, the list of targets for each source type is empty. In
     * other words, there are no real dependencies. However, this is how the
     * architecture assessor expects the information about types.
     */
    private SetMap<String, String> createTypeLevelDependencies() {
        SetMap<String, String> dependencies = new SetMap<String, String>();

        String[] types = new String[] { "a", "b", "c", "d", "e" };

        for (String type : types) {
            dependencies.addAll(type, Collections.<String> emptyList());
        }
        return dependencies;
    }
}
