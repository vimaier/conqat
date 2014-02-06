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
package org.conqat.engine.core.driver.instance;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.core.IConQATProcessor;
import org.conqat.engine.core.driver.ConQATInstrumentation;
import org.conqat.engine.core.driver.declaration.ProcessorDeclaration;
import org.conqat.engine.core.driver.error.DriverException;
import org.conqat.engine.core.driver.specification.ISpecification;
import org.conqat.engine.core.driver.specification.ISpecificationParameter;
import org.conqat.engine.core.driver.specification.ProcessorSpecificationParameter;
import org.conqat.engine.core.logging.ConQATLogger;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.clone.DeepCloneException;
import org.conqat.lib.commons.collections.CounterSet;
import org.conqat.lib.commons.concurrent.InThreadExecutorService;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.commons.system.PerformanceMonitor;

/**
 * The instance of a processor. The main purpose of this class is to construct
 * an {@link IConQATProcessor}, configure it using the correct setters (data
 * taken from parameters) and finally execute it. Thereby all possible errors
 * must be handled and the state adjusted accordingly.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 6FA9C9E760CA46EAAF18805F2D5EC474
 */
public class ProcessorInstance extends InstanceBase<ProcessorDeclaration> {

	/** Logger. */
	/* package */final ConQATLogger logger = new ConQATLogger(this,
			ProcessorInstance.class);

	/** The output of this instance. */
	private final InstanceOutput output;

	/** The time this processor needed to run in milliseconds. */
	private long runtimeMillis = 0;

	/** The state this processor instance is in. */
	private EInstanceState state = EInstanceState.NOT_RUN;

	/**
	 * Create a new processor instance from a given declaration.
	 * 
	 * @param declaration
	 *            the declaration this is based on.
	 * @param parent
	 *            the parent (block) instance.
	 */
	public ProcessorInstance(ProcessorDeclaration declaration,
			BlockInstance parent) {
		super(parent, declaration);

		// activate all attributes (as we want their values later)
		for (InstanceParameter param : getParameters()) {
			for (InstanceAttribute attr : param.getAttributes()) {
				attr.addConsumer();
			}
		}
		output = new InstanceOutput(getDeclaration().getOutputs().get(0), this);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @param instrumentation
	 *            instrumentation of processors is cared for in
	 *            {@link BlockInstance#execute(ExecutionContext, ConQATInstrumentation)}
	 *            as we wouldn't be able to suspend execution at this point.
	 *            This method carries the parameter, however, as it is defined
	 *            by the interface.
	 */
	@Override
	protected void doExecute(ExecutionContext contextInfo,
			ConQATInstrumentation instrumentation) {
		// Check if parameters are there with required multiplicities
		List<InstanceParameter> preparedParameters;
		try {
			preparedParameters = prepareParameters();
		} catch (DeepCloneException e) {
			logger.error("Skipping element " + getName()
					+ " because of an error during cloning: " + e.getMessage(),
					e);
			state = EInstanceState.FAILED_DUE_TO_CLONING_PROBLEM;
			return;
		}
		if (!fulfillsMultiplicities(preparedParameters)) {
			logger.warn("Skipping element: " + getName());
			state = EInstanceState.FAILED_DUE_TO_MISSING_INPUT;
			return;
		}

		// Start collecting statistics
		logger.info("Executing processor: " + getName());
		PerformanceMonitor monitor = PerformanceMonitor.create();

		try {
			// try to actually execute the processor
			execute(contextInfo, instrumentation, preparedParameters);
		} catch (Throwable e) {
			handleExecutionException(e);
		} finally {
			// clear the ConQAT global string pool
			ConQATStringPool.clear();

			// Stop collecting statistics
			monitor.stop();
			runtimeMillis = monitor.getMilliseconds();
			logger.info("Processor '" + getName() + "' used "
					+ StringUtils.format(runtimeMillis) + "ms and "
					+ StringUtils.format(monitor.getDeltaMemUsageInKBs())
					+ "kB.");
		}
	}

	/**
	 * Handles exceptions which occurred during the execution of the processor
	 * or one of its exit hooks. This performs logging and update of the state.
	 */
	/* package */void handleExecutionException(Throwable e) {
		if (e instanceof DriverException) {
			logger.error("Processor '" + getName()
					+ "' failed badly (constructor): " + e.getMessage());
			state = EInstanceState.FAILED_BADLY;
		} else if (e instanceof ConQATException) {
			logger.error("Processor '" + getName() + "' failed gracefully: "
					+ e.getMessage());
			state = EInstanceState.FAILED_GRACEFULLY;
		} else if (e instanceof StackOverflowError) {
			logger.error("Processor '" + getName() + "' had a stack overflow: "
					+ e.getMessage());
			logger.error("You could try to increase the stack size by using the parameter "
					+ "-Xss10m for the java VM. If this does not help, "
					+ "you probably ran into a programming bug.");
			state = EInstanceState.FAILED_BADLY;
		} else if (e instanceof OutOfMemoryError) {
			logger.error("Processor '" + getName() + "' ran out of memory: "
					+ e.getMessage());
			if (e.getMessage() != null
					&& e.getMessage().toLowerCase().contains("perm")) {
				logger.error("It seems the permanent generation space was exhausted, "
						+ "which can happen for large systems with certain processors. "
						+ "To increase it, pass the parameter -XX:MaxPermSize=128m to the "
						+ "java VM Instead of 128 you can of course use any other number, "
						+ "which is memory in MB (256m might be needed in some extreme cases.) ");
			} else {
				logger.error("You can increase the amount of memory available for java "
						+ "by passing the parameter -Xmx1024m to the VM. Instead of 1024 you "
						+ "can of course use any other number, which is memory in MB.");
			}
			state = EInstanceState.FAILED_BADLY;
		} else {
			StringWriter result = new StringWriter();
			e.printStackTrace(new PrintWriter(result));

			logger.error("Processor '" + getName() + "' failed badly: "
					+ e.getMessage() + StringUtils.CR + result);
			state = EInstanceState.FAILED_BADLY;
		}
	}

	/** Performs the actual execution. */
	private void execute(ExecutionContext contextInfo,
			ConQATInstrumentation instrumentation,
			List<InstanceParameter> preparedParameters) throws Throwable {

		// instantiate and initialize
		IConQATProcessor processor = getDeclaration().getSpecification()
				.createProcessorInstance();
		ExecutorService executorService;
		if (getDeclaration().getSpecification().isThreadSafe()) {
			executorService = contextInfo.getSharedExecutorService();
		} else {
			executorService = new InThreadExecutorService();
		}
		processor.init(new ConQATProcessorInfo(this, contextInfo,
				instrumentation, executorService));
		for (InstanceParameter param : preparedParameters) {
			applyParameterToProcessor(param, processor);
		}

		// Execute process method
		Object result = processor.process();
		output.setValue(result);

		// record execution state
		state = EInstanceState.RUN_SUCCESSFULLY;
	}

	/**
	 * Prepares the values of all parameters and returns those that were
	 * prepared "to completion".
	 * 
	 * @return the list of those parameters whose attributes could be prepared.
	 */
	private List<InstanceParameter> prepareParameters()
			throws DeepCloneException {
		List<InstanceParameter> result = new ArrayList<InstanceParameter>();
		for (InstanceParameter param : getNonSyntheticParameters()) {
			if (param.prepareAttributes()) {
				result.add(param);
			}
		}
		return result;
	}

	/**
	 * Returns whether the given parameters fulfill the multiplicities required
	 * by the specification.
	 */
	private boolean fulfillsMultiplicities(List<InstanceParameter> parameters) {
		// Count actual instance parameters
		CounterSet<ISpecificationParameter> counter = new CounterSet<ISpecificationParameter>();
		for (InstanceParameter param : parameters) {
			counter.inc(param.getDeclaration().getSpecificationParameter());
		}

		// Compare specified multiplicities with actual counts
		ISpecification spec = getDeclaration().getSpecification();
		for (ISpecificationParameter specParam : spec
				.getNonSyntheticParameters()) {
			int count = counter.getValue(specParam);
			if (count < specParam.getMultiplicity().getLower()
					|| count > specParam.getMultiplicity().getUpper()) {
				return false;
			}
		}

		return true;
	}

	/** Apply a parameter to the given ConQAT processor using reflection. */
	private static void applyParameterToProcessor(InstanceParameter param,
			IConQATProcessor processor) throws Throwable {

		ISpecificationParameter specificationParameter = param.getDeclaration()
				.getSpecificationParameter();
		CCSMAssert
				.isTrue(specificationParameter instanceof ProcessorSpecificationParameter,
						"In a processor instance this should be true by construction.");

		// we do not inline this variable as exceptions thrown by it should not
		// be handled by the block below
		Object[] arguments = param.getValueArray();

		try {
			((ProcessorSpecificationParameter) specificationParameter)
					.applyParameterToProcessor(processor, arguments);
		} catch (IllegalArgumentException e) {
			throw new ConQATException("error setting parameter '"
					+ param.toString() + "'. [" + e.getMessage() + "]");
		} catch (IllegalAccessException e) {
			throw new ConQATException("error accessing parameter '"
					+ param.toString() + "'. [" + e.getMessage() + "]");
		} catch (InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	/** {@inheritDoc} */
	@Override
	public List<InstanceOutput> getOutputs() {
		ArrayList<InstanceOutput> result = new ArrayList<InstanceOutput>(1);
		result.add(output);
		return result;
	}

	/** Returns the current state of this processor. */
	public EInstanceState getState() {
		return state;
	}

	/** Returns time required to execute this processor in milliseconds. */
	public long getExecutionTime() {
		return runtimeMillis;
	}

	/** Marks this processor as disabled */
	@Override
	public void disable(EInstanceState disablementState) {
		state = disablementState;

		// no removal for synthetic parameters, as these may already have been
		// consumed
		for (InstanceParameter param : getNonSyntheticParameters()) {
			// remove one consumer from referenced value providers to ensure the
			// garbage collector removed intermediates
			for (InstanceAttribute attribute : param.getAttributes()) {
				if (attribute.getValueProvider() != null) {
					attribute.getValueProvider().removeConsumer();
				}
			}
		}
	}

}