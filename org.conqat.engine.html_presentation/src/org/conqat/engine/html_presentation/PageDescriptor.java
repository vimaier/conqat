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
package org.conqat.engine.html_presentation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.conqat.lib.commons.clone.DeepCloneException;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.html.HTMLWriter;

/**
 * Default implementation of {@link IPageDescriptor}. This class provides a HTML
 * writer ({@link #getWriter()}) to directly create HTML content.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 91FA8FB2DD9DAAFE917D66E35774A68C
 */
public class PageDescriptor implements IPageDescriptor {

	/** The summary. */
	private final Object summary;

	/** The description. */
	private final String description;

	/** The filename. */
	private final String filename;

	/** Group ID. */
	private final String groupId;

	/** Name of the icon */
	private final String iconName;

	/** Result name */
	private final String name;

	/** Stream the HTML writer writes to. */
	private final ByteArrayOutputStream stream;

	/** HTML writer. */
	private final HTMLWriter writer;

	/** Flag that determines whether page is external or written by ConQAT */
	private final boolean external;

	/**
	 * Page descriptor constructor.
	 * 
	 * @param description
	 *            page description (long)
	 * @param name
	 *            page name (short)
	 * @param groupId
	 *            group id
	 * @param iconName
	 *            name of icon file
	 * @param filename
	 *            name of target file
	 * @param external
	 *            whether page content is external to ConQAT
	 */
	public PageDescriptor(String description, String name, String groupId,
			String iconName, String filename, boolean external) {
		this(description, name, groupId, null, iconName, filename, external);
	}

	/**
	 * Page descriptor constructor.
	 * 
	 * @param description
	 *            page description (long)
	 * @param name
	 *            page name (short)
	 * @param groupId
	 *            group id
	 * @param iconName
	 *            name of icon file
	 * @param filename
	 *            name of target file
	 */
	public PageDescriptor(String description, String name, String groupId,
			String iconName, String filename) {
		this(description, name, groupId, null, iconName, filename, false);
	}

	/**
	 * Full constructor for page descriptor.
	 * 
	 * @param description
	 *            page description (long)
	 * @param name
	 *            page name (short)
	 * @param groupId
	 *            group id
	 * @param summary
	 *            assessement
	 * @param iconName
	 *            name of icon file
	 * @param filename
	 *            name of target file
	 */
	public PageDescriptor(String description, String name, String groupId,
			Object summary, String iconName, String filename) {
		this(description, name, groupId, summary, iconName, filename, false);
	}

	/**
	 * Full constructor for page descriptor.
	 * 
	 * @param description
	 *            page description (long)
	 * @param name
	 *            page name (short)
	 * @param groupId
	 *            group id
	 * @param summary
	 *            assessement
	 * @param iconName
	 *            name of icon file
	 * @param filename
	 *            name of target file
	 */
	public PageDescriptor(String description, String name, String groupId,
			Object summary, String iconName, String filename, boolean external) {
		this.summary = summary;
		this.iconName = iconName;
		this.description = description;
		this.name = name;
		this.groupId = groupId;
		this.filename = filename;
		this.external = external;

		stream = new ByteArrayOutputStream();
		writer = new HTMLWriter(stream, CSSMananger.getInstance());
	}

	/** Copy constructor. */
	private PageDescriptor(PageDescriptor descriptor) throws IOException {
		summary = descriptor.summary;
		iconName = descriptor.iconName;
		description = descriptor.description;
		name = descriptor.name;
		groupId = descriptor.groupId;
		filename = descriptor.filename;
		external = descriptor.external;

		descriptor.writer.flush();

		stream = new ByteArrayOutputStream();
		stream.write(descriptor.stream.toByteArray());
		writer = new HTMLWriter(stream, CSSMananger.getInstance());
	}

	/** {@inheritDoc} */
	@Override
	public Object getSummary() {
		return summary;
	}

	/** {@inheritDoc} */
	@Override
	public String getContent() {
		writer.flush();
		try {
			return stream.toString(FileSystemUtils.UTF8_ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new AssertionError("UTF-8 should be supported!");
		}
	}

	/** {@inheritDoc} */
	@Override
	public String getDescription() {
		return description;
	}

	/** {@inheritDoc} */
	@Override
	public String getFilename() {
		return filename;
	}

	/** {@inheritDoc} */
	@Override
	public String getGroupId() {
		return groupId;
	}

	/** {@inheritDoc} */
	@Override
	public String getIconName() {
		return iconName;
	}

	/** {@inheritDoc} */
	@Override
	public String getName() {
		return name;
	}

	/** Returns {@link #getName()}. */
	@Override
	public String toString() {
		return getName();
	}

	/** Get writer. */
	public HTMLWriter getWriter() {
		return writer;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isExternal() {
		return external;
	}

	/** {@inheritDoc} */
	@Override
	public PageDescriptor deepClone() throws DeepCloneException {
		try {
			return new PageDescriptor(this);
		} catch (IOException ex) {
			throw new DeepCloneException("Could not clone page descriptor: "
					+ ex);
		}
	}

}