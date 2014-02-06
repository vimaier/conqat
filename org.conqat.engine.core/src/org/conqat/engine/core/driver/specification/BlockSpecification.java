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
package org.conqat.engine.core.driver.specification;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.conqat.engine.core.driver.declaration.IDeclaration;
import org.conqat.engine.core.driver.error.BlockFileException;
import org.conqat.engine.core.driver.error.DriverException;
import org.conqat.engine.core.driver.error.EDriverExceptionType;
import org.conqat.engine.core.driver.error.ErrorLocation;
import org.conqat.engine.core.driver.util.IDocumentable;
import org.w3c.dom.Element;

/**
 * This is the specification for a block (corresponds to block-spec in the XML
 * file). The initialization of this class does not happen completely in the
 * constructor but is delayed using the {@link #initialize()} method, because
 * this step is potentially expensive, as all contained blocks and processors
 * also have to be resolved. Furthermore, the class is usually configured using
 * the add* methods before initialization.
 * <p>
 * The lazy initialization is handled transparently the the
 * {@link org.conqat.engine.core.driver.specification.SpecificationLoader} in
 * conjuction with the
 * {@link org.conqat.engine.core.driver.specification.BlockSpecificationInitializer}
 * class.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 1DB938936A66B4F6968807DEEFFF011F
 */
public class BlockSpecification extends
		SpecificationBase<BlockSpecificationParameter> implements IDocumentable {

	/** The file this specification was read from. */
	private final File origin;

	/** The location used for error reporting. */
	private final ErrorLocation errorLocation;

	/**
	 * Mapping from output names to outputs. The order is kept the same as found
	 * in the XML file. This order is important when resolving the dot-star
	 * operator.
	 */
	private final Map<String, SpecificationOutput> outputs = new LinkedHashMap<String, SpecificationOutput>();

	/**
	 * The list of declarations of this processor in the order in which they
	 * should be executed. A suitable order for this list is determined via
	 * topological sorting. Note that during early compilation stages the order
	 * of this list is still undefined (but this should not matter too much
	 * then).
	 */
	private List<IDeclaration> executionList = new ArrayList<IDeclaration>();

	/** Marker, indicating whether initialization has already happened. */
	private boolean initialized = false;

	/** Documentation for this element. */
	private String doc;

	/** Storage for meta data. */
	private final Map<String, Element> meta = new HashMap<String, Element>();

	/**
	 * Create a new block specification.
	 * 
	 * @param name
	 *            the name the block specification should have.
	 * @param origin
	 *            the file the specification was read from.
	 */
	public BlockSpecification(String name, File origin)
			throws BlockFileException {
		this(name, origin, new ErrorLocation(origin));
	}

	/**
	 * Create a new block specification with explicit error location.
	 * 
	 * @param name
	 *            the name the block specification should have.
	 * @param origin
	 *            the file the specification was read from.
	 * @param errorLocation
	 *            the error location to be reported.
	 */
	public BlockSpecification(String name, File origin,
			ErrorLocation errorLocation) throws BlockFileException {
		super(name);
		this.origin = origin;
		this.errorLocation = errorLocation;

		addParam(new ConditionalBlockSpecificationParameter(this));
	}

	/** {@inheritDoc} */
	@Override
	// public, as this is called from the ConfigFileReader
	public void addParam(BlockSpecificationParameter param)
			throws BlockFileException {
		if (initialized) {
			throw new IllegalStateException(
					"May only modify the specification before initialization.");
		}
		super.addParam(param);
	}

	/** Add an inner declaration to this block specification. */
	public void addDeclaration(IDeclaration declaration) {
		if (initialized) {
			throw new IllegalStateException(
					"May only modify the specification before initialization.");
		}
		executionList.add(declaration);
	}

	/** Add an output for this block-spec. */
	public void addOutput(BlockSpecificationOutput output)
			throws BlockFileException {
		if (initialized) {
			throw new IllegalStateException(
					"May only modify the specification before initialization.");
		}

		if (outputs.containsKey(output.getName())) {
			throw new BlockFileException(
					EDriverExceptionType.DUPLICATE_OUTPUT_NAME,
					"Duplicate output " + output.getName(), output);
		}

		outputs.put(output.getName(), output);
	}

	/**
	 * This is usually called automatically for specifications received via the
	 * specification loader and should only be called for specifications created
	 * by yourself!
	 * <p>
	 * Perform the initialization of this block specification. This consists of
	 * the following steps:
	 * <ul>
	 * <li>Loading and preparing all contained declarations (blocks and
	 * processors).</li>
	 * <li>Resolving references involving the dot-star operator.</li>
	 * <li>Resolving all remaining references.</li>
	 * <li>Ordering the execution list using topological sort on the declaration
	 * graph.</li>
	 * <li>Inferring multiplicities of input parameters.</li>
	 * <li>Inferring types of input attributes (including pipeline information).
	 * </li>
	 * <li>Inferring types and pipeline information for the outputs.</li>
	 * <li>Performing type checking on all internal connections.</li>
	 * <li>Performing internal consistency checks.</li>
	 * </ul>
	 * <img src="blockspecification_initialization_order.png"/>
	 */
	public void initialize() throws DriverException {
		if (initialized) {
			throw new IllegalStateException(
					"This already has been initialized!");
		}
		initialized = true;

		for (IDeclaration declaration : executionList) {
			declaration.referenceSpecification();
		}

		new StarReferenceResolver(this).resolveAll();
		new ReferenceResolver(this).resolve();
		executionList = new TopSorter(this).sort();
		new PipelineFreezer(this).freeze();
		new ParameterMultiplicityInferer(this).infer();
		new AttributeTypeInferer(this).infer();
		new OutputTypeInferer(this).infer();
		new DeclarationTypeChecker(this).check();
		new ConQATDocTagletProcessor(this).process();
	}

	/** Get a topologically sorted list of the configuration. */
	public List<IDeclaration> getDeclarationList() {
		return executionList;
	}

	/** {@inheritDoc} */
	@Override
	public BlockSpecificationOutput[] getOutputs() {
		return outputs.values().toArray(
				new BlockSpecificationOutput[outputs.size()]);
	}

	/** Returns the file this specification was read from. This may be null. */
	public File getOrigin() {
		return origin;
	}

	/** {@inheritDoc} */
	@Override
	public String getDoc() {
		return doc;
	}

	/** {@inheritDoc} */
	@Override
	public void setDoc(String doc) {
		this.doc = doc;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "Block specification '" + getName() + "'";
	}

	/** {@inheritDoc} */
	@Override
	protected BlockSpecificationParameter[] newParameterArray(int size) {
		return new BlockSpecificationParameter[size];
	}

	/** {@inheritDoc} */
	@Override
	public ErrorLocation getErrorLocation() {
		return errorLocation;
	}

	/**
	 * Adds meta data to this block specification. If meta data of the same type
	 * already exists, it will be replaced.
	 */
	public void addMeta(String type, Element metaElement) {
		meta.put(type, metaElement);
	}

	/**
	 * Returns meta data of the given type if it exists. If not, null is
	 * returned. This returns a clone, so this should be cached in a local
	 * variable instead of calling over and over again.
	 */
	public Element getMeta(String type) {
		Element result = meta.get(type);
		if (result != null) {
			result = (Element) result.cloneNode(true);
		}
		return result;
	}
}