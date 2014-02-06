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
package org.conqat.engine.core.driver.cqddl;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.conqat.engine.core.bundle.BundleInfo;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.core.IConQATProcessor;
import org.conqat.engine.core.driver.error.DriverException;
import org.conqat.engine.core.driver.specification.ProcessorSpecification;
import org.conqat.engine.core.driver.specification.ProcessorSpecificationAttribute;
import org.conqat.engine.core.driver.specification.ProcessorSpecificationParameter;
import org.conqat.engine.core.driver.specification.SpecificationLoader;
import org.conqat.engine.core.logging.testutils.ProcessorInfoMock;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.PairList;
import org.conqat.lib.commons.reflect.ClassType;
import org.conqat.lib.commons.reflect.ReflectionUtils;
import org.conqat.lib.commons.reflect.TypeConversionException;
import org.conqat.lib.cqddl.CQDDL;
import org.conqat.lib.cqddl.function.CQDDLCheck;
import org.conqat.lib.cqddl.function.CQDDLEvaluationException;
import org.conqat.lib.cqddl.parser.CQDDLParsingParameters;

/**
 * Utility code for using CQDDL with ConQAT processors.
 * 
 * @author $Author: heineman $
 * @version $Rev: 38023 $
 * @ConQAT.Rating GREEN Hash: EEA4B8613FB242A2DEF1F2A890D6DB7D
 */
public class CQDDLUtils {

	/**
	 * Executes the given ConQAT processor using a CQDDL specification for its
	 * parameters.
	 * <p>
	 * The error handling strategy is to wrap all exceptions as
	 * {@link CQDDLExecutionException}s. The only exception is the
	 * {@link ConQATException}, as this might refer to errors the user is
	 * interested in.
	 * 
	 * @param processorClassName
	 *            identifies the processor being executed.
	 * @param parsingParameters
	 *            the parsing parameters used.
	 * @param args
	 *            the arguments of the processor which are passed to the CQDDL
	 *            parser to return the processor parameters. The
	 *            parsingParameters are used during parsing. The arguments must
	 *            be parsable to a pair list. The top-level keys are parameter
	 *            names, the top-level values are pair lists again which map
	 *            attributes to values. See users of this method for examples.
	 * 
	 * @return the result of the processor's run.
	 * 
	 * @throws ConQATException
	 *             if the actual execution of the processor cause any problems
	 *             (but execution could be started).
	 * @throws CQDDLExecutionException
	 *             if the processor could not be started for some reason
	 *             (missing parameters, class not found, etc.).
	 */
	public static Object executeProcessor(String processorClassName,
			CQDDLParsingParameters parsingParameters, Object... args)
			throws ConQATException, CQDDLExecutionException {

		try {
			ProcessorSpecification processorSpecification = new SpecificationLoader(
					null, CollectionUtils.<BundleInfo> emptyList())
					.getProcessorSpecification(processorClassName);

			IConQATProcessor processor = processorSpecification
					.createProcessorInstance();
			processor.init(new ProcessorInfoMock());

			Object parsingResult = CQDDL.parse(parsingParameters, args);
			if (!(parsingResult instanceof PairList<?, ?>)) {
				throw new CQDDLExecutionException(
						"CQDDL term did not parse to a PairList!");
			}
			@SuppressWarnings("unchecked")
			PairList<String, Object> params = (PairList<String, Object>) parsingResult;

			applyParameters(processor, processorSpecification, params);

			return processor.process();
		} catch (DriverException e) {
			throw new CQDDLExecutionException(e);
		} catch (CQDDLEvaluationException e) {
			throw new CQDDLExecutionException(e);
		} catch (IllegalAccessException e) {
			throw new CQDDLExecutionException(e);
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof ConQATException) {
				throw (ConQATException) e.getCause();
			}
			throw new CQDDLExecutionException(e.getCause());
		} catch (TypeConversionException e) {
			throw new CQDDLExecutionException(e);
		}
	}

	/**
	 * Applies parameters encoded as a {@link PairList} to the given processor.
	 * 
	 * @param params
	 *            as described in
	 *            {@link #executeProcessor(String, CQDDLParsingParameters, Object...)}
	 */
	private static void applyParameters(IConQATProcessor processor,
			ProcessorSpecification processorSpecification,
			PairList<String, Object> params) throws CQDDLEvaluationException,
			DriverException, IllegalAccessException, InvocationTargetException,
			TypeConversionException, CQDDLExecutionException {

		for (int i = 0; i < params.size(); ++i) {
			String parameterName = params.getFirst(i);
			ProcessorSpecificationParameter parameter = processorSpecification
					.getParameter(parameterName);
			if (parameter == null) {
				throw new CQDDLExecutionException("Invalid name for parameter "
						+ i + ": " + parameterName);
			}
			Object parameterArgument = params.getSecond(i);
			if (!(parameterArgument instanceof PairList<?, ?>)) {
				throw new CQDDLExecutionException("Argument for parameter " + i
						+ " must be PairList but was "
						+ parameterArgument.getClass());
			}

			@SuppressWarnings("unchecked")
			Map<String, Object> attributeMap = CQDDLCheck
					.asMap((PairList<String, Object>) parameterArgument);

			applyParameter(processor, parameter, i, attributeMap);
		}
	}

	/**
	 * Applies a single parameter. The attributes for this parameter are
	 * provided as a map.
	 */
	private static void applyParameter(IConQATProcessor processor,
			ProcessorSpecificationParameter parameter, int parameterIndex,
			Map<String, Object> attributeMap) throws DriverException,
			IllegalAccessException, InvocationTargetException,
			TypeConversionException, CQDDLExecutionException {

		ProcessorSpecificationAttribute[] attributes = parameter
				.getAttributes();
		Object[] attributeValues = new Object[attributes.length];

		for (int j = 0; j < attributes.length; ++j) {
			attributeValues[j] = determineAttributeValue(parameter,
					parameterIndex, attributes[j], attributeMap);
		}
		parameter.applyParameterToProcessor(processor, attributeValues);
	}

	/** Determines the value to be used for a given attribute. */
	private static Object determineAttributeValue(
			ProcessorSpecificationParameter parameter, int parameterIndex,
			ProcessorSpecificationAttribute attribute,
			Map<String, Object> attributeMap) throws DriverException,
			TypeConversionException, CQDDLExecutionException {

		if (attributeMap.containsKey(attribute.getName())) {
			Object value = attributeMap.get(attribute.getName());
			if (value == null) {
				throw new CQDDLExecutionException("Attribute "
						+ attribute.getName()
						+ " may not have null value for parameter "
						+ parameterIndex + " (" + parameter.getName() + ")");
			}

			if (value instanceof String) {
				value = ReflectionUtils.convertString((String) value, attribute
						.getType().getBaseClass());
			}

			if (!attribute.getType().isAssignableFrom(
					new ClassType(value.getClass()))) {
				throw new CQDDLExecutionException("Attribute "
						+ attribute.getName() + " must be of type "
						+ attribute.getType() + " for parameter "
						+ parameterIndex + " (" + parameter.getName() + ")");
			}
			return value;
		}

		Object value = attribute.getDefaultValue();
		if (value == null) {
			throw new CQDDLExecutionException("Attribute "
					+ attribute.getName() + " is missing for paramter "
					+ parameterIndex + " (" + parameter.getName()
					+ ") and has no default value!");
		}
		return value;
	}
}
