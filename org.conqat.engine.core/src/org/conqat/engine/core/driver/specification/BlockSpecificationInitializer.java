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
package org.conqat.engine.core.driver.specification;

import org.conqat.engine.core.driver.error.CyclicBlockSpecDependencyException;
import org.conqat.engine.core.driver.error.DriverException;

/**
 * This class is a wrapper for the
 * {@link org.conqat.engine.core.driver.specification.BlockSpecification} which
 * assists in its lazy initialization.
 * <p>
 * It is only used from the
 * {@link org.conqat.engine.core.driver.specification.SpecificationLoader}.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 48C6D8D477699E3591A3E7D9DBE19C3F
 */
/* package */class BlockSpecificationInitializer {

	/** The block specification being wrapped. */
	private final BlockSpecification blockSpecification;

	/** The state of initialization the wrapped block specification is in. */
	private EInitializationState initializationState;

	/** Create a new wrapper. */
	public BlockSpecificationInitializer(BlockSpecification blockSpecification) {
		this.blockSpecification = blockSpecification;
		initializationState = EInitializationState.UNINITIALIZED;
	}

	/**
	 * Returns the wrapped block specification and makes sure that it is
	 * initialized before. If the initialization has failed before,
	 * <code>null</code> is returned.
	 */
	public BlockSpecification accessBlockSpecification() throws DriverException {

		switch (initializationState) {

		case INITIALIZING:
			throw new CyclicBlockSpecDependencyException(blockSpecification);

		case ERROR:
			return null;

		case UNINITIALIZED:
			initialize();
			return blockSpecification;

		case INITIALIZED:
			return blockSpecification;

		default:
			throw new IllegalStateException("There should be not alternative!");
		}
	}

	/**
	 * Initialize the contained block specification and manage the state
	 * accordingly.
	 */
	private void initialize() throws DriverException {
		try {
			initializationState = EInitializationState.INITIALIZING;
			blockSpecification.initialize();
			initializationState = EInitializationState.INITIALIZED;
		} catch (CyclicBlockSpecDependencyException e) {
			e.unwindDependencyStack(blockSpecification);
			initializationState = EInitializationState.ERROR;
			throw e;
		} catch (DriverException e) {
			initializationState = EInitializationState.ERROR;
			throw e;
		}
	}
}