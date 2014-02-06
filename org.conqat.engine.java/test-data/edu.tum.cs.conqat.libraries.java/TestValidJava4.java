/*-----------------------------------------------------------------------+
 | ConQAT                                                                |
 |                                                                       |
 $Id: TestValidJava4.java 29389 2010-07-27 12:17:11Z deissenb $            
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
package edu.enum.test;

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
 * @author $Author: deissenb $
 * @version $Rev: 29389 $
 * @levd.rating GREEN Rev: 4867
 */
@AConfigProcessor(description = "A simple processor that counts the number of "
		+ " child nodes and aggregates this value up to the root node.")
public class ChildCounter implements IConQATProcessor {

}
