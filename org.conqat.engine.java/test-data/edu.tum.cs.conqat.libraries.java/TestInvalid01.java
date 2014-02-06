/*-----------------------------------------------------------------------+
 | ConQAT                                                                |
 |                                                                       |
 $Id: TestInvalid01.java 8002 2007-02-20 12:37:40Z hummelb $            
 |                                                                       |
 | Copyright (c)  2004-2005 Technische Universitaet Muenchen             |
 |                                                                       |
 | Technische Universitaet Muenchen               #########  ##########  |
 | Institut fuer Informatik - Lehrstuhl IV           ##  ##  ##  ##  ##  |
 | Prof. Dr. Manfred Broy                            ##  ##  ##  ##  ##  |
 | Boltzmannstr. 3                                   ##  ##  ##  ##  ##  |
 | 85748 Garching bei Muenchen                       ##  ##  ##  ##  ##  |
 | Germany                                           ##  ######  ##  ##  |
 +-----------------------------------------------------------------------*/
package edu-tum;

import java.util.List;

import edu.tum.cs.conqat.driver.IConQATNode;
import edu.tum.cs.conqat.driver.IConQATProcessor;
import edu.tum.cs.conqat.driver.config.AConfigAttribute;
import edu.tum.cs.conqat.driver.config.AConfigElement;
import edu.tum.cs.conqat.driver.config.AConfigProcessor;
import edu.tum.cs.conqat.driver.config.APipelineSource;
import edu.tum.cs.conqat.util.KeyValueUtils;

/**
 * A simple processor that counts the number of child nodes and aggregates this
 * value up to the root node.
 * 
 * @author Benjamin Hummel
 * @author $Author: hummelb $
 * @version $Rev: 8002 $
 * @levd.rating GREEN Rev: 4867
 */
@AConfigProcessor(description = "A simple processor that counts the number of "
		+ " child nodes and aggregates this value up to the root node.")
public class ChildCounter implements IConQATProcessor {

}
