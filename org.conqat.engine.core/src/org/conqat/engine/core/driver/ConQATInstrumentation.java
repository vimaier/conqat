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
package org.conqat.engine.core.driver;

import org.conqat.engine.core.core.IProgressMonitor;
import org.conqat.engine.core.driver.instance.ExecutionContext;
import org.conqat.engine.core.driver.instance.IInstance;
import org.conqat.engine.core.logging.ConQATLoggingEvent;

/**
 * This is the base class to be extended by ConQAT instrumentation clients. The
 * instrumentation interface can be used to observe and influence the execution
 * of a ConQAT configuration, similar to the Java debugging and profiling
 * interfaces.
 * <p>
 * All method provide empty default implementations, which allows us to extend
 * this class (making instrumentation more versatile) without destroying
 * existing instrumentation clients. This also is used as the default
 * instrumentation when running ConQAT.
 * 
 * @author hummelb
 * @author $Author: juergens $
 * @version $Rev: 35194 $
 * @ConQAT.Rating GREEN Hash: C3628F886BB80D84C02B48B36FF50E77
 */
@SuppressWarnings("unused")
public class ConQATInstrumentation implements IProgressMonitor {

	/**
	 * This method is called directly after the constructor. The parameters
	 * string contains any parameters to this instrumentation as provided on the
	 * command-line. This string may also be empty.
	 */
	public void init(String[] parameters) {
		// empty default implementation
	}

	/**
	 * This method is called before starting the execution.
	 * 
	 * @param executionContext
	 *            the context in which the configuration is executed.
	 * @return <code>true</code> if execution may continue, <code>false</code>
	 *         if execution should be stopped right now.
	 */
	public boolean beginExecution(ExecutionContext executionContext) {
		return true;
	}

	/**
	 * This method is called after the execution of the driver has been
	 * completed.
	 */
	public void endExecution() {
		// empty default implementation
	}

	/**
	 * This method is executed before an unit instance is executed.
	 * 
	 * @param instance
	 *            the unit instance about to be executed.
	 * @return <code>true</code> if execution of this instance should be
	 *         executed. If <code>false</code> is returned, this instance (and
	 *         its children, if this is a block) will be skipped.
	 */
	public boolean beforeExecute(IInstance instance) {
		return true;
	}

	/**
	 * This method may be called from the currently running processor to report
	 * the current progress. It is not guaranteed that a processor will actually
	 * call this, not that the data provided makes sense. Note that calls to
	 * this method may be frequent, so nothing too expensive should happen here.
	 */
	@Override
	public void reportProgress(int workDone, int overallWork) {
		// empty default implementation
	}

	/**
	 * This method is called after an instance has completed execution. This
	 * called even if {@link #beforeExecute(IInstance)} returned
	 * <code>false</code>.
	 */
	public void afterExecute(IInstance instance) {
		// empty default implementation
	}

	/**
	 * This method is called by the logger for any log message that appeared
	 * (both logged from processors or elsewhere). Note that calls to this
	 * method may be frequent, so nothing too expensive should happen here.
	 */
	public void eventLogged(ConQATLoggingEvent conqatLoggingEvent) {
		// empty default implementation
	}

}