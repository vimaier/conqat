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
package org.conqat.engine.core.driver.error;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableList;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Base class for all configuration exceptions. Precise exception type is
 * specified by the type provided by {@link #getType()}.
 * 
 * The locations (files or classes) from which the exception originates is
 * provided by {@link #getLocations()}.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 5BEF3BA4EF0213421291A9E7A3414084
 */
public abstract class DriverException extends Exception {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** Exception type. */
	private final EDriverExceptionType type;

	/** Origin of this exception */
	protected final List<ErrorLocation> locations = new ArrayList<ErrorLocation>();

	/**
	 * Create exception with a causing exception.
	 */
	public DriverException(EDriverExceptionType type, String message,
			Throwable cause, ErrorLocation... locations) {
		super(message, cause);
		this.type = type;
		CCSMAssert.isFalse(locations.length == 0,
				"Must provide at least one location");
		this.locations.addAll(Arrays.asList(locations));
	}

	/** Create exception. */
	public DriverException(EDriverExceptionType type, String message,
			ErrorLocation... locations) {
		this(type, message, null, locations);
	}

	/**
	 * Create exception with a causing exception.
	 */
	public DriverException(EDriverExceptionType type, String message,
			Throwable cause, IErrorLocatable locatable) {
		this(type, message, cause, locatable.getErrorLocation());
	}

	/**
	 * Create exception with a causing exception.
	 */
	public DriverException(EDriverExceptionType type, String message,
			IErrorLocatable locatable) {
		this(type, message, locatable.getErrorLocation());
	}

	/** Create exception with multiple files as error location. */
	public DriverException(EDriverExceptionType type, String message,
			File... files) {
		this(type, message, createLocations(files));
	}

	/** Create error locations from files. */
	private static ErrorLocation[] createLocations(File[] files) {
		ErrorLocation[] locations = new ErrorLocation[files.length];
		for (int i = 0; i < files.length; i++) {
			locations[i] = new ErrorLocation(files[i]);
		}
		return locations;
	}

	/** Get exception type. The type contains the main message of the exception. */
	public EDriverExceptionType getType() {
		return type;
	}

	/** Returns the locations. */
	public UnmodifiableList<ErrorLocation> getLocations() {
		return CollectionUtils.asUnmodifiable(locations);
	}

	/**
	 * Override this message to construct the message from the exception type
	 * and the context.
	 */
	@Override
	public String getMessage() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.getMessage());
		if (getCause() != null) {
			sb.append(" (");
			sb.append(getCause().getMessage());
			sb.append(")");
		}

		return sb.toString();
	}

	/** Get locations as string. */
	public String getLocationsAsString() {
		return StringUtils.concat(locations, ", ");
	}

	/**
	 * Set the error location of the exception. If the exception has multiple
	 * locations, these are substituted by the one provided location.
	 */
	public void relocate(ErrorLocation location) {
		locations.clear();
		locations.add(location);
	}
}