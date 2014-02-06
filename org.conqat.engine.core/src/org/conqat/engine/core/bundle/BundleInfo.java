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

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;

import org.conqat.engine.core.bundle.library.LibraryDescriptor;
import org.conqat.engine.core.driver.error.EDriverExceptionType;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableCollection;
import org.conqat.lib.commons.collections.UnmodifiableSet;
import org.conqat.lib.commons.version.Version;

/**
 * This class describes bundles. It provides access to information specified in
 * the bundle descriptor as well as to implicitly defined information like the
 * libraries a bundle uses.
 * <p>
 * Instances of this class are provided to the bundles via the bundle context
 * classes. Therefore, this class carefully distinguishes between information
 * that can be safely given to the clients (bundles) and information that must
 * be kept strictly in the ConQAT core. As a general rule all information
 * available to clients is immutable.
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 46536 $
 * @ConQAT.Rating GREEN Hash: E71751266962739A7240185608B03C4B
 */
public class BundleInfo {

	/** Name of the file containing the bundle descriptor */
	public static final String BUNDLE_DESCRIPTOR_NAME = "bundle.xml";

	/** Location of the folder containing the XML definitions of blocks */
	public static final String BLOCKS_LOCATION = "blocks";

	/** Location of the folder containing the classes of the bundle */
	public static final String CLASSES_LOCATION = "classes";

	/** Pattern a bundle id must comply to: {@value} . */
	private final static String ID_PATTERN_STRING = "[a-zA-Z_][a-zA-Z0-9_]*(\\.[a-zA-Z_][a-zA-Z0-9_]*)*";

	/** Compiled regex pattern a bundle id must comply to. */
	private final static Pattern ID_PATTERN = Pattern
			.compile(ID_PATTERN_STRING);

	/** The location where bundle is stored in the file system. */
	private final File bundleLocation;

	/** The context class. */
	private BundleContextBase context;

	/** Maps from bundle id to dependency */
	private final HashMap<String, BundleDependency> dependencies = new HashMap<String, BundleDependency>();

	/** Bundle description. */
	private String description;

	/** Set of libraries this bundle provides. */
	private final HashSet<File> libraries = new HashSet<File>();

	/** Set of library descriptors this bundle provides. */
	private final HashSet<LibraryDescriptor> libraryDescriptors = new HashSet<LibraryDescriptor>();

	/** Bundle name. */
	private String name;

	/** Bundle provider. */
	private String provider;

	/** The version of ConQAT core this bundle requires. */
	private Version requiredCoreVersion;

	/** Bundle version. */
	private Version version;

	/**
	 * Create new bundle info.
	 * 
	 * @param bundleLocation
	 *            the location of the bundle.
	 * @throws BundleException
	 *             if bundle id does not comply to pattern
	 *             {@value #ID_PATTERN_STRING}.
	 */
	public BundleInfo(File bundleLocation) throws BundleException {
		this.bundleLocation = bundleLocation;

		if (!ID_PATTERN.matcher(getId()).matches()) {
			throw new BundleException(EDriverExceptionType.ILLEGAL_BUNDLE_ID,
					"Illegal bundle id: " + getId(), bundleLocation);
		}
	}

	/** Get blocks directory. */
	public File getBlocksDirectory() {
		return new File(bundleLocation, BLOCKS_LOCATION);
	}

	/** Get classes directory. */
	public File getClassesDirectory() {
		return new File(bundleLocation, CLASSES_LOCATION);
	}

	/**
	 * Get set of dependencies.
	 */
	public UnmodifiableCollection<BundleDependency> getDependencies() {
		return CollectionUtils.asUnmodifiable(dependencies.values());
	}

	/**
	 * Get description specified in the bundle descriptor.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Get bundle id. The bundle id ist the name of the bundle location (without
	 * parent pathes): {@link #getLocation()}<code>.getName()</code>.
	 * <p>
	 * For example, the id of the bundle <code>CloneDetective Bundle</code> at
	 * location <code>D:\svnccsm\edu.tum.cs.conqat.clones</code> is
	 * <code>edu.tum.cs.conqat.clones</code>.
	 */
	public String getId() {
		return bundleLocation.getName();
	}

	/** Get libraries this bundle provides. */
	public UnmodifiableSet<File> getLibraries() {
		return CollectionUtils.asUnmodifiable(libraries);
	}

	/** Get library descriptors this bundle provides. */
	public UnmodifiableSet<LibraryDescriptor> getLibraryDescriptors() {
		return CollectionUtils.asUnmodifiable(libraryDescriptors);
	}

	/**
	 * Get bundle location.
	 */
	public File getLocation() {
		return bundleLocation;
	}

	/**
	 * Get name specified in the bundle descriptor.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get provider specified in the bundle descriptor.
	 */
	public String getProvider() {
		return provider;
	}

	/**
	 * Get required core version specified in the bundle descriptor.
	 */
	public Version getRequiredCoreVersion() {
		return requiredCoreVersion;
	}

	/**
	 * Get bundle version specified in the bundle descriptor.
	 */
	public Version getVersion() {
		return version;
	}

	/** Returns bundle id. */
	@Override
	public String toString() {
		return "bundle '" + getId() + "'";
	}

	/**
	 * Add a dependency from this bundle ot another one.
	 * 
	 * @throws BundleException
	 *             if dependency to the target bundle was already defined
	 *             (thrown, even if target versions differ).
	 */
	/* package */void addDependency(BundleDependency dependency)
			throws BundleException {

		if (dependencies.containsKey(dependency.getId())) {
			throw new BundleException(EDriverExceptionType.DUPLICATE_DEPDENDCY,
					"Duplicate dependency from  '" + this + "' to bundle '"
							+ dependency.getId() + "'.", getDescriptor());
		}

		dependencies.put(dependency.getId(), dependency);

	}

	/** Add a library. */
	/* package */void addLibrary(File lib) {
		libraries.add(lib);
	}

	/** Add a library. */
	/* package */void addLibraryDescriptor(LibraryDescriptor descriptor) {
		libraryDescriptors.add(descriptor);
	}

	/** Get bundle context object. */
	/* package */BundleContextBase getContext() {
		return context;
	}

	/**
	 * Get bundle descriptor.
	 */
	/* package */File getDescriptor() {
		File descriptorFile = new File(bundleLocation.getAbsolutePath()
				+ File.separator + BUNDLE_DESCRIPTOR_NAME);
		return descriptorFile;
	}

	/** Checks if this bundle has a classes directory. */
	/* package */boolean hasClasses() {
		return getClassesDirectory().exists();
	}

	/** Set bundle context object. */
	/* package */void setContext(BundleContextBase bundleContext) {
		context = bundleContext;
	}

	/** Set description. */
	/* package */void setDescription(String description) {
		this.description = description;
	}

	/** Set name. */
	/* package */void setName(String name) {
		this.name = name;
	}

	/** Set provider. */
	/* package */void setProvider(String provider) {
		this.provider = provider;
	}

	/** Set required core version. */
	/* package */void setRequiredCoreVersion(Version requiredCoreVersion) {
		this.requiredCoreVersion = requiredCoreVersion;
	}

	/** Set bundle version. */
	/* package */void setVersion(Version version) {
		this.version = version;
	}
}