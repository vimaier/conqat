package org.conqat.engine.core.build;

import static org.conqat.lib.commons.filesystem.ClassPathUtils.CLASS_FILE_SUFFIX;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.filesystem.PathBasedContentProviderBase;
import org.conqat.lib.commons.string.StringUtils;

/**
 * This class describes an entry in a jar file. While the jar files actually
 * only handles files as entries, this also models directories.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 44651 $
 * @ConQAT.Rating GREEN Hash: ADF27FACA71D70748D6D12AA0B5AA899
 */
/* package */class JarEntry {

	/** Parent entry. */
	private final JarEntry parent;

	/** Name of the entry. */
	private final String name;

	/** Maps from child name to child. */
	private final Map<String, JarEntry> children = new HashMap<String, JarEntry>();

	/** Prefixes to preserve. */
	private final String[] preservePrefixes;

	/** Flag for directories. This is set by {@link #addChild(String)}. */
	private boolean isDirectory = false;

	/** Constructor */
	public JarEntry(String name, JarEntry parent, String[] preservePrefixes) {
		this.name = name;
		this.parent = parent;
		this.preservePrefixes = preservePrefixes;
	}

	/**
	 * Recursively copy entries from source to target jar.
	 */
	public void writeToJar(JarOutputStream jarOutputStream,
			PathBasedContentProviderBase provider) throws IOException {

		for (JarEntry child : children.values()) {
			child.writeToJar(jarOutputStream, provider);
		}

		if (isDirectory) {
			return;
		}

		InputStream in = null;
		try {
			String fullName = getFullName();
			jarOutputStream.putNextEntry(new ZipEntry(fullName));
			in = provider.openStream(fullName);
			FileSystemUtils.copy(in, jarOutputStream);
			jarOutputStream.closeEntry();
		} finally {
			FileSystemUtils.close(in);
		}
	}

	/**
	 * Get full name.
	 */
	private String getFullName() {
		if (parent.isRoot()) {
			return name;
		}
		return parent.getFullName() + "/" + name;
	}

	/**
	 * Get class name of this entry.
	 */
	private String getClassName() {
		String localName = name;
		if (isClass()) {
			localName = StringUtils.stripSuffix(CLASS_FILE_SUFFIX, localName);
		}

		if (parent.isRoot()) {
			return localName;
		}

		return parent.getClassName() + "." + localName;
	}

	/**
	 * Recursively filter non-class resources if they reside in a directory that
	 * has no classes at all. We do not prune empty directories as we anyway
	 * write only file entries to the jar file later.
	 */
	public void filterNonClassResources(Set<String> deletedEntries) {

		for (JarEntry child : new ArrayList<JarEntry>(children.values())) {
			child.filterNonClassResources(deletedEntries);
		}

		if (!isDirectory && !isClass() && !hasSiblingClasses()) {
			delete(deletedEntries);
		}
	}

	/** Count the number of sibling classes this entry has. */
	private boolean hasSiblingClasses() {
		if (parent == null) {
			return false;
		}

		for (JarEntry sibling : parent.children.values()) {
			if (sibling.isClass()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Recursively deletes class entries that are already on the ConQAT class
	 * path.
	 */
	public void filterKnownClassFiles(Set<String> deletedEntries) {
		for (JarEntry child : new ArrayList<JarEntry>(children.values())) {
			child.filterKnownClassFiles(deletedEntries);
		}
		if (isClass() && isKnownClass(getClassName())) {
			delete(deletedEntries);
		}
	}

	/** Checks if an entry describes a class. */
	private boolean isClass() {
		return !isDirectory && name.endsWith(CLASS_FILE_SUFFIX);
	}

	/** Checks if the entry is the root. */
	private boolean isRoot() {
		return parent == null;
	}

	/**
	 * Delete this entry. Full names of all descendant files are stored in the
	 * provided set.
	 */
	public void delete(Set<String> deletedEntries) {
		if (StringUtils.startsWithOneOf(getFullName(), preservePrefixes)) {
			return;
		}
		parent.deleteChild(this);
		addFullNames(deletedEntries);
	}

	/** Add full names of deleted files to the provided set. */
	private void addFullNames(Set<String> deletedEntries) {
		for (JarEntry child : children.values()) {
			child.addFullNames(deletedEntries);
		}
		if (!isDirectory) {
			deletedEntries.add(getFullName());
		}
	}

	/**
	 * Delete a child entry.
	 */
	private void deleteChild(JarEntry child) {
		children.remove(child.name);
	}

	/**
	 * Obtain entry by '/'-separated path.
	 */
	public JarEntry getEntryByPath(String path) {
		return getEntryByPath(Arrays.asList(path.split("/")));
	}

	/** Obtain entry by path. */
	private JarEntry getEntryByPath(List<String> elements) {
		if (elements.isEmpty()) {
			return this;
		}
		String childName = elements.get(0);
		JarEntry child = getChild(childName);
		if (child == null) {
			return null;
		}
		return child.getEntryByPath(CollectionUtils.getRest(elements));
	}

	/**
	 * Get the number of descendant files with the specifed suffix. Calling this
	 * with the empty string determines all files.
	 */
	public int getFileCount(String suffix) {
		if (!isDirectory && name.endsWith(suffix)) {
			return 1;
		}

		int count = 0;
		for (JarEntry child : children.values()) {
			count += child.getFileCount(suffix);
		}
		return count;
	}

	/**
	 * Get child with specified name.
	 */
	public JarEntry getChild(String name) {
		return children.get(name);
	}

	/**
	 * Add and return child.
	 */
	public JarEntry addChild(String childName) {
		CCSMAssert.isFalse(children.containsKey(childName),
				"Child already exists.");
		JarEntry child = new JarEntry(childName, this, preservePrefixes);
		children.put(childName, child);
		isDirectory = true;
		return child;
	}

	/**
	 * Checks if a class is already on the classpath.
	 */
	private boolean isKnownClass(String className) {

		try {
			Class.forName(className, false, Thread.currentThread()
					.getContextClassLoader());
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		} catch (Throwable e) {
			System.err.println("When looking for class " + className + " an "
					+ e.getClass().getSimpleName() + " occured: "
					+ e.getMessage());

			// we assume that the class itself is there but there were
			// dependencies missing
			return true;
		}
	}
}