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
package org.conqat.engine.code_clones.normalization.provider;

import java.io.Serializable;
import java.util.LinkedList;

import org.conqat.engine.core.core.IConQATProcessor;
import org.conqat.engine.core.logging.IConQATLogger;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.lib.commons.assertion.CCSMAssert;

/**
 * Base class for providers that work in a lazy fashion.
 * 
 * It provides lazy processors (called providers in this bundle) with a logger.
 * <p>
 * ConQAT processors typically work in an eager fashion: They perform their
 * entire task, when their {@link IConQATProcessor#process()} method is called.
 * This implementation potentially requires a lot of memory, since a processor's
 * input and output has to be present in main memory at the same time. This
 * approach is prohibitively expensive for the normalization steps, since
 * storage of both token and unit information requires excessive amounts of main
 * memory.
 * <p>
 * The alternative solution is to perform computation in a lazy fashion: a
 * processor performs its task in small steps, each time some
 * <code>getNext()</code> method is called.
 * 
 * @param <Element>
 *            Element type this provider works on
 * @param <Data>
 *            Type of data object provided by this provider
 * @param <X>
 *            Exception that gets thrown by {@link #getNext()} and
 *            {@link #init(ITextResource, IConQATLogger)}
 * 
 * @author $Author: hummelb $
 * @version $Revision: 43156 $
 * @ConQAT.Rating GREEN Hash: 297767D936D5A1B698B076213D601C44
 */
public abstract class ProviderBase<Element extends ITextResource, Data, X extends Exception>
		implements IProvider<Element, Data, X>, Serializable {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/**
	 * Logger that can be used by this provider. Gets set during
	 * {@link #init(ITextResource, IConQATLogger)}.
	 */
	protected transient IConQATLogger logger;

	/** Returns the stored processor info. */
	protected IConQATLogger getLogger() {
		CCSMAssert.isNotNull(logger,
				"Provider must be initialized before this method gets called.");
		return logger;
	}

	/**
	 * This list stores tokens that have been accessed by
	 * {@link #lookahead(int)} but have not yet been retrieved using
	 * {@link #getNext()}
	 */
	private final LinkedList<Data> lookaheadBuffer = new LinkedList<Data>();

	/**
	 * Initializes the lazy processor. Must be called before other methods on
	 * the class are called.
	 * 
	 * @param root
	 *            {@link ITextResource} this processor works on.
	 * @param logger
	 *            Logger from the processor that executes this provider.
	 */
	@Override
	public void init(Element root, IConQATLogger logger) throws X {
		this.logger = logger;
		lookaheadBuffer.clear();
		init(root);
	}

	/**
	 * Template method that allows deriving classes to perform their
	 * initialization
	 */
	protected abstract void init(Element root) throws X;

	/**
	 * Returns a token ahead of the current position, without actually
	 * retrieving it. The first token to be looked ahead at has index 1.
	 */
	@Override
	public Data lookahead(int index) throws X {
		while (index > lookaheadBuffer.size()) {
			Data data = provideNext();
			if (data == null) {
				return null;
			}
			lookaheadBuffer.add(data);
		}

		return lookaheadBuffer.get(index - 1);
	}

	/** {@inheritDoc} */
	@Override
	public Data getNext() throws X {
		if (lookaheadBuffer.size() > 0) {
			return lookaheadBuffer.poll();
		}
		return provideNext();
	}

	/** Template method that providers implement to yield elements */
	protected abstract Data provideNext() throws X;

}