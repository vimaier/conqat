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
package org.conqat.engine.core.bundle;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.conqat.engine.core.driver.error.EDriverExceptionType;
import org.conqat.engine.core.driver.error.ErrorLocation;

/**
 * This class loads the bundle context class and initializes it (if present).
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @author Elmar Juergens
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: A9833BD53FEC2D7BD8366B29A22A998D
 */
public class BundlesContextLoader {

	/** Name of the BundleContext class */
	private static final String CONTEXT_CLASS_NAME = "BundleContext";

	/** The configuration the class works on. */
	private final BundlesConfiguration config;

	/** Create new context loader. */
	/* package */BundlesContextLoader(BundlesConfiguration config) {
		this.config = config;
	}

	/**
	 * Iterates over all bundles in the configuration and calls
	 * {@link #process(BundleInfo)} for each bundle.
	 */
	/* package */void process() throws BundleException {
		// make sure we initialize bundle contexts in topological order, as they
		// might depend on each other.
		for (BundleInfo bundleInfo : new BundlesTopSorter(config).sort()) {
			process(bundleInfo);
		}
	}

	/**
	 * Load bundle context (if present). If present, the context can be obtained
	 * via {@link BundleInfo#getContext()}. If not present, it is left to
	 * <code>null</code>.
	 */
	protected void process(BundleInfo bundleInfo) throws BundleException {

		// no classes -> no context
		if (!bundleInfo.hasClasses()) {
			return;
		}

		// load context class
		Class<BundleContextBase> bundleContextClass = getContextClass(bundleInfo);

		// bundles does not define a context class
		if (bundleContextClass == null) {
			return;
		}

		// obtain constructor
		Constructor<BundleContextBase> constructor = getContextConstructor(
				bundleContextClass, bundleInfo);

		// obtain instance
		BundleContextBase bundleContext = getContext(constructor, bundleInfo);

		// store instance
		bundleInfo.setContext(bundleContext);
	}

	/**
	 * Get context class for a bundle.
	 * 
	 * @return the context class or <code>null</code> if the bundle does not
	 *         define a context.
	 * @throws BundleException
	 *             if the class is not a subclass of {@link BundleContextBase}.
	 */
	private Class<BundleContextBase> getContextClass(BundleInfo bundleInfo)
			throws BundleException {
		String bundleContextClassName = bundleInfo.getId() + "."
				+ CONTEXT_CLASS_NAME;

		Class<?> clazz;

		try {
			// this uses the previously initialized bundle class loader
			clazz = Class.forName(bundleContextClassName, true, Thread
					.currentThread().getContextClassLoader());

		} catch (ClassNotFoundException e) {
			return null;
		}

		if (!BundleContextBase.class.isAssignableFrom(clazz)) {
			throw new BundleException(
					EDriverExceptionType.CONTEXT_CLASS_NOT_SUBCLASS,
					"Bundle context class of " + bundleInfo
							+ " does not extend "
							+ BundleContextBase.class.getSimpleName() + ".",
					new ErrorLocation(clazz));
		}

		// this is safe, since we checked it above
		@SuppressWarnings("unchecked")
		Class<BundleContextBase> bundleContextClass = (Class<BundleContextBase>) clazz;

		return bundleContextClass;
	}

	/**
	 * Obtain context constructor of context class.
	 * 
	 * @return the constructor
	 * @throws BundleException
	 *             if the class has no context constructor or a security
	 *             exception occurred.
	 */
	private Constructor<BundleContextBase> getContextConstructor(
			Class<BundleContextBase> bundleContextClass, BundleInfo bundleInfo)
			throws BundleException {
		Constructor<BundleContextBase> constructor;

		try {
			constructor = bundleContextClass.getConstructor(BundleInfo.class);
			return constructor;
		} catch (SecurityException e) {
			throw new BundleException(
					EDriverExceptionType.CONTEXT_CONSTRUCTOR_SECURITY_EXCEPTION,
					"Context constructor of bundle '" + bundleInfo
							+ "' can not be called due to security reasons.",
					new ErrorLocation(bundleContextClass));
		} catch (NoSuchMethodException e) {
			throw new BundleException(
					EDriverExceptionType.MISSING_CONTEXT_CONSTRUCTOR,
					"Context of bundle '" + bundleInfo
							+ "' has no bundle constructor.",
					new ErrorLocation(bundleContextClass));
		}
	}

	/**
	 * Get context instance.
	 * 
	 * @param constructor
	 *            the context constructor.
	 * @return the instance
	 * @throws BundleException
	 *             if the class could no be instantiated.
	 */
	private BundleContextBase getContext(
			Constructor<BundleContextBase> constructor, BundleInfo bundleInfo)
			throws BundleException {
		try {
			BundleContextBase bundleContext = constructor
					.newInstance(bundleInfo);

			return bundleContext;

		} catch (InstantiationException e) {
			throw new BundleException(
					EDriverExceptionType.ABSTRACT_CONTEXT_CLASS,
					"Context class of bundle '" + bundleInfo + "' is abstract.",
					new ErrorLocation(constructor.getDeclaringClass()));
		} catch (IllegalAccessException e) {
			throw new BundleException(
					EDriverExceptionType.NON_ACCESSIBLE_CONTEXT_CONSTRUCTOR,
					"Bundle context of " + bundleInfo
							+ " has no accessible context constructor.",
					new ErrorLocation(constructor.getDeclaringClass()));
		} catch (InvocationTargetException e) {
			throw new BundleException(
					EDriverExceptionType.CONTEXT_CONSTRUCTOR_THREW_EXCEPTION,
					"Context constructor of " + bundleInfo
							+ " threw exception: " + e.getCause().getMessage(),
					new ErrorLocation(constructor.getDeclaringClass()));
		}
	}
}