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
package org.conqat.engine.resource;

import org.conqat.engine.core.core.ConQATException;

/**
 * This interface describes content-providing resources. There are cases, where
 * an element also has children (which should be indicated by also implementing
 * {@link IContainer}).
 * 
 * @author $Author: juergens $
 * @version $Rev: 35198 $
 * @ConQAT.Rating GREEN Hash: E6C4A001694663DC1B4AF8C29E507FEC
 */
public interface IElement extends IResource {

	/** Get element content. */
	byte[] getContent() throws ConQATException;

	/**
	 * Get a string that identifies the location of the element, e.g. a file
	 * system path. This location is specific to the running analysis, i.e.
	 * depends on time and the concrete machine ConQAT is running on.
	 */
	String getLocation();

	/**
	 * Returns the uniform path. This is an artificial path that uniquely
	 * defines a resource across machine boundaries. This should be used for
	 * persisted information.
	 */
	String getUniformPath();

	/**
	 * Creates a new {@link IContentAccessor} relative to this element's
	 * accessor. Never returns <code>null</code>.
	 * 
	 * @param relativePath
	 *            a relative path understandable by the accessor.
	 * @throws ConQATException
	 *             if the relative path could not be resolved or does not point
	 *             to an existing element.
	 */
	IContentAccessor createRelativeAccessor(String relativePath)
			throws ConQATException;

	/**
	 * Creates an uniform path relative to this element's accessor. Never
	 * returns <code>null</code>. In contrast to
	 * {@link #createRelativeAccessor(String)}, this method does not throw an
	 * exception if the target element does not exist.
	 * 
	 * @param relativePath
	 *            a relative path understandable by the accessor.
	 * @throws ConQATException
	 *             if the relative path could not be resolved.
	 */
	String createRelativeUniformPath(String relativePath)
			throws ConQATException;
}