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

import org.conqat.engine.core.bundle.BundleContextBase;
import org.conqat.engine.core.bundle.BundleInfo;
import org.conqat.engine.core.bundle.BundlesClassLoader;
import org.conqat.engine.core.bundle.BundlesConfiguration;
import org.conqat.engine.core.bundle.BundlesContextLoader;
import org.conqat.engine.core.bundle.BundlesDependencyVerifier;
import org.conqat.engine.core.bundle.BundlesLoader;
import org.conqat.engine.core.bundle.BundlesManager;
import org.conqat.engine.core.bundle.BundlesTopSorter;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.IConQATProcessor;

/**
 * Enumeration to describe exception types.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 979D94ECFEACF4DFFB31FD52720634FE
 */
public enum EDriverExceptionType {

	/** An attribute is defined as pipeline source but has a default value. */
	PIPELINE_ATTRIBUTE_HAS_DEFAULT_VALUE,

	/** A processor defines unsupported parameters. */
	UNSUPPORTED_PARAMETER,

	/** Config file could not be read. */
	IO_ERROR,

	/**
	 * An XML parsing exception occurred. The config file is either not
	 * well-formed or not valid.
	 */
	XML_PARSING_EXCEPTION,

	/** Problems occurred while setting up the XML parsing facilities. */
	XML_PARSER_CONFIGURATION_ERROR,

	/** Failure during attempt to read property file */
	PROPERTY_FILE_READ_ERROR,

	/** Config file defines cyclic dependencies. */
	CYCLIC_DEPENDENCIES,

	/** Reference to an unknown processor. */
	UNDEFINED_REFERENCE,

	/** Processor class is not annotated with {@link AConQATProcessor}. */
	PROCESSOR_CLASS_NOT_ANNOTATED,

	/** Processor class could not be found. */
	PROCESSOR_CLASS_NOT_FOUND,

	/** Processor class does not implement interface {@link IConQATProcessor}. */
	PROCESSOR_CLASS_NOT_IMPLEMENTS_INTERFACE,

	/** Processor class has no parameterless constructor. */
	PROCESSOR_CLASS_WITHOUT_PARAMETERLESS_CONSTRUCTOR,

	/** Processor class has no public constructor. */
	PROCESSOR_CLASS_WITHOUT_PUBLIC_CONSTRUCTOR,

	/** A parameter defines unsupported attributes. */
	UNSUPPORTED_ATTRIBUTE,

	/** A static field or method was annotated. */
	STATIC_PARAMETER,

	/** A final field was annotated as a parameter. */
	FINAL_FIELD_PARAMETER,

	/**
	 * A method that is annotated as {@link AConQATParameter} has a parameter
	 * that is not annotated as {@link AConQATAttribute}.
	 */
	FORMAL_PARAMETER_NOT_ANNOTATED,

	/** A parameter misses an attribute. */
	MISSING_ATTRIBUTE,

	/**
	 * A default value is defined for an attribute but is has an incompatible
	 * type.
	 */
	ILLEGAL_DEFAULT_VALUE,

	/**
	 * Config file specifies an immediate value (not a reference) with
	 * incompatible type.
	 */
	ILLEGAL_IMMEDIATE_VALUE,

	/**
	 * The type provided by a processor (directly or via pipeline) is
	 * incompatible with the expected type of an input parameter of another
	 * processor.
	 */
	TYPE_MISMATCH,

	/** The name used for a parameter of a block specification was used twice. */
	DUPLICATE_PARAM_NAME,

	/**
	 * The name used for an parameter attribute of a block specification was
	 * used twice.
	 */
	DUPLICATE_ATTRIBUTE_NAME,

	/** The name used for an output of a block specification was used twice. */
	DUPLICATE_OUTPUT_NAME,

	/** A tag which is not allowed for XML read from a block file was found. */
	ILLEGAL_TAG_IN_BLOCK_FILE,

	/** The specification of a block was not declared before. */
	UNKNOWN_BLOCK_SPECIFICATION,

	/** There are block specifications depending cyclic on each other. */
	CYCLIC_BLOCK_DEPENDENCY,

	/** The multiplicity for a parameter is empty. */
	EMPTY_PARAMETER_INTERVAL,

	/** The same name was used twice in the same scope (e.g. block). */
	DUPLICATE_NAME,

	/** Multiple star operators in same parameter referencing different objects! */
	MULTIPLE_STARS_IN_PARAMETER,

	/**
	 * An immediate value is not allowed as no class for the given type is
	 * known!
	 */
	INCONSTRUCTIBLE_CLASS,

	/** Inference rules resulted in an empty parameter multiplicity. */
	EMPTY_INFERED_PARAMETER_INTERVAL,

	/** Inference rules resulted in an inconstructible type. */
	INFERED_INCONSISTENT_TYPE,

	/**
	 * Multiple input references in same parameter referencing different inputs!
	 */
	MULTIPLE_INPUT_REFERENCES,

	/** A parameter occurred too often. */
	PARAMETER_OCCURS_TOO_OFTEN,

	/** A parameter occurred too seldom. */
	PARAMETER_OCCURS_NOT_OFTEN_ENOUGH,

	/** The type of pipeline attribute and pipeline output did not match. */
	INCOMPATIBLE_PIPELINE_TYPES,

	/** Generic processor classes are not supported. */
	GENERIC_PROCESSOR_CLASS,

	/** Field annotated with {@link AConQATKey} must be static final! */
	KEY_NOT_PUBLIC_STATIC_FINAL,

	/** Field annotated with {@link AConQATKey} must be of type string! */
	KEY_NOT_STRING,

	/** There must be exactly one dot in a star reference. */
	ILLEGAL_STAR_EXPRESSION,

	/** The given default value for the attribute could not be cloned. */
	DEFAULT_VALUE_COULD_NOT_BE_CLONED,

	/** The parameter has child elements although they are not allowed there. */
	PARAMETER_HAS_CHILDELEMENTS,

	/** The parameter has text contents. */
	PARAMETER_HAS_TEXT_CONTENT,

	/** The block specification is missing for the block file. */
	MISSING_BLOCK_SPECIFICATION,

	/**
	 * The block can be found both in a bundle and local to the current
	 * configuration.
	 */
	AMBIGUOUS_BLOCK_LOOKUP,

	/** The name of a block must match its block file. */
	BLOCK_FILE_NAME_MUST_MATCH,

	/** Each block specification must be specified in a file of its own. */
	MULTIPLE_BLOCKSPEC,

	/** Property names must contain exactly one dot (parameter.attribute)! */
	INVALID_PROPERTY_NAME,

	/** Class not found. */
	CLASS_NOT_FOUND,

	/** Class definition not found. */
	CLASS_DEF_NOT_FOUND,

	/**
	 * Path could not be converted to an URL.
	 * <p>
	 * Thrown by {@link BundlesClassLoader}
	 */
	ILLEGAL_URL_FROM_PATH,

	/**
	 * Bundle location not found.
	 * <p>
	 * Thrown by {@link BundlesLoader}, {@link BundlesManager}
	 */
	BUNDLE_LOCATION_NOT_FOUND,

	/**
	 * Bundle collection not found.
	 * <p>
	 * Thrown by {@link BundlesManager}
	 */
	BUNDLE_COLLECTION_NOT_FOUND,

	/**
	 * No bundles have been configured. This is a bug, as ConQAT does nothing
	 * sensible without bundles.
	 * <p>
	 * Thrown by {@link BundlesManager}
	 */
	NO_BUNDLES_CONFIGURED,

	/**
	 * Bundle context class is not subclass of {@link BundleContextBase}.
	 * <p>
	 * Thrown by {@link BundlesContextLoader}
	 */
	CONTEXT_CLASS_NOT_SUBCLASS,

	/**
	 * Two bundles have the same id.
	 * <p>
	 * Thrown by {@link BundlesConfiguration}
	 */
	DUPLICATE_BUNDLE_ID,

	/**
	 * Creating the normalized (canonical) from of a bundle location raised an
	 * error.
	 * <p>
	 * Thrown by {@link BundlesManager}
	 */
	BUNDLE_LOCATION_COULD_NOT_BE_NORMALIZED,

	/**
	 * Bundle context class has no constructor overriding the constructor of
	 * {@link BundleContextBase}.
	 * <p>
	 * Thrown by {@link BundlesContextLoader}
	 */
	MISSING_CONTEXT_CONSTRUCTOR,

	/**
	 * Constructor of bundle context class could not be accessed due a security
	 * problem.
	 * <p>
	 * Thrown by {@link BundlesContextLoader}
	 */
	CONTEXT_CONSTRUCTOR_SECURITY_EXCEPTION,

	/**
	 * Bundle context class is abstract.
	 * <p>
	 * Thrown by {@link BundlesContextLoader}
	 */
	ABSTRACT_CONTEXT_CLASS,

	/**
	 * Constructor of bundle context class threw an exception.
	 * <p>
	 * Thrown by {@link BundlesContextLoader}
	 */
	CONTEXT_CONSTRUCTOR_THREW_EXCEPTION,

	/**
	 * Constructor of bundle context is not accessible (caused by an
	 * {@link IllegalAccessException}).
	 * <p>
	 * Thrown by {@link BundlesContextLoader}
	 */
	NON_ACCESSIBLE_CONTEXT_CONSTRUCTOR,

	/**
	 * Bundle has no descriptor.
	 * <p>
	 * Thrown by {@link BundlesLoader}
	 */
	MISSING_BUNDLE_DESCRIPTOR,

	/**
	 * Bundle has an illegal id.
	 * <p>
	 * Thrown by {@link BundleInfo}
	 */
	ILLEGAL_BUNDLE_ID,

	/**
	 * Bundle defines duplicate dependency to another bundle.
	 * <p>
	 * Thrown by {@link BundleInfo}
	 */
	DUPLICATE_DEPDENDCY,

	/**
	 * Bundle defines dependency to a missing bundle.
	 * <p>
	 * Thrown by {@link BundlesDependencyVerifier}
	 */
	BUNDLE_NOT_FOUND,

	/**
	 * Bundle defines a self dependency.
	 * <p>
	 * Thrown by {@link BundlesDependencyVerifier}
	 */
	SELF_DEPENDENCY,

	/**
	 * A bundle has a library directory without any libraries.
	 * <p>
	 * Thrown by {@link BundlesLoader}
	 */
	EMPTY_LIBRARY_DIRECTORY,

	/**
	 * A cycle was detected in the bundle dependency graph.
	 * <p>
	 * Thrown by {@link BundlesTopSorter}
	 */
	CYCLIC_BUNDLE_DEPENDENCY,

	/** Indicates problems with the temporary directory. */
	TEMP_DIR,

	/** Indicates that an invalid immediate object type was injected. */
	INVALID_IMMEDIATE_OBJECT_TYPE
}