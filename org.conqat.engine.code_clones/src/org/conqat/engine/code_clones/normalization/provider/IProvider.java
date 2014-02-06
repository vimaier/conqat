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

import org.conqat.engine.code_clones.lazyscope.IElementProvider;
import org.conqat.engine.code_clones.normalization.token.ITokenProvider;
import org.conqat.engine.core.logging.IConQATLogger;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.lib.commons.collections.ILookahead;

/**
 * Base interface for components that work on {@link ITextResource} hierarchies
 * and provide data objects in a stepwise, lazy fashion.
 * <p>
 * The root of the {@link ITextResource} tree from which data objects are
 * provided is set in the {@link IProvider#init(ITextResource, IConQATLogger)}
 * method. It must be called before the first call to
 * {@link IProvider#getNext()}.
 * <p>
 * See {@link IElementProvider}, {@link ITokenProvider} or {@link IUnitProvider}
 * for examples of provided data elements.
 * 
 * @param <Element>
 *            Element type this processor works on.
 * 
 * @param <Data>
 *            Data type returned by {@link #getNext()}.
 * 
 * @param <X>
 *            Exception that gets thrown by {@link #getNext()} and
 *            {@link #init(ITextResource, IConQATLogger)}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 37237 $
 * @ConQAT.Rating GREEN Hash: 0033478D3AF91982E10654DA4D81C4E7
 */
public interface IProvider<Element extends ITextResource, Data, X extends Exception>
		extends ILookahead<Data, X> {

	/**
	 * Get next data object.
	 * 
	 * @return The next unit or <code>null</code> if all data objects have been
	 *         returned.
	 */
	public Data getNext() throws X;

	/**
	 * Initialize the {@link IProvider} with the root of the
	 * {@link ITextResource} tree it works on.
	 * 
	 * @param root
	 *            root of tree from which data objects are provided.
	 * @param logger
	 *            Logger from the processor that executes this provider
	 */
	public void init(Element root, IConQATLogger logger) throws X;

}