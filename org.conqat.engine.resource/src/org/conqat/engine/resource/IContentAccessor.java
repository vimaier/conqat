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

import java.util.Arrays;

import org.conqat.engine.core.core.ConQATException;

/**
 * This interface describes classes that can access content. Content accessors
 * should be immutable.
 * 
 * Content accessors can create new accessors relative to themselves. Method
 * {@link #createRelative(String)} creates an accessor. Method
 * {@link #createRelativeUniformPath(String)} only creates a uniform path. The
 * following contract must hold:
 * <code>X.createRelative(Y).getUniformPath() equals X.createRelativeUniformPath()</code>
 * .
 * 
 * Content accessors are considered equal if their uniform paths are equal. The
 * {@link Object#equals(Object)} and {@link Object#hashCode()} implementations
 * must be done accordingly.
 * 
 * @author deissenb
 * @author $Author: juergens $
 * @version $Rev: 35198 $
 * @ConQAT.Rating GREEN Hash: 2E41E0C34BA7DFDF8E42759406C24B74
 */
public interface IContentAccessor {

	/**
	 * Get content. Implementing class must return the same byte array on each
	 * call to this method, i.e. {@link Arrays#equals(byte[], byte[])} must be
	 * <code>true</code> for all pairs of byte arrays returned by this method.
	 */
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
	 * Creates a new {@link IContentAccessor} relative to this one. Never
	 * returns <code>null</code>.
	 * 
	 * @param relativePath
	 *            a relative path understandable by this accessor.
	 * @throws ConQATException
	 *             if the relative path could not be resolved or does not point
	 *             to an existing element.
	 */
	IContentAccessor createRelative(String relativePath) throws ConQATException;

	/**
	 * Creates a new uniform path relative to this one. Never returns
	 * <code>null</code>. In contrast to {@link #createRelative(String)}, this
	 * method does not throw an exception if the target element does not exist.
	 * 
	 * @param relativePath
	 *            a relative path understandable by this accessor.
	 * @throws ConQATException
	 *             if the relative path could not be resolved.
	 */
	String createRelativeUniformPath(String relativePath)
			throws ConQATException;
}