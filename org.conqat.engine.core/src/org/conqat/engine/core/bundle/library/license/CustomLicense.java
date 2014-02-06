/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
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
package org.conqat.engine.core.bundle.library.license;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Non-standard library license with custom information.
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 46536 $
 * @ConQAT.Rating YELLOW Hash: 59E2F9412943C5B62605E3334BD561BE
 */
public class CustomLicense implements ILicense {

	/** The name of the library. */
	private final String name;

	/** The website url with information about the library. */
	private final String website;

	/** Additional license notice. */
	private final String notice;

	/**
	 * Flag indicating whether the license is compatible with the Apache
	 * license.
	 */
	private final boolean isApacheCompatible;

	/**
	 * Flag indicating whether the license allows commercial use of the library.
	 */
	private final boolean isCommercialUseAllowed;

	/** Constructor. */
	private CustomLicense(String name, boolean isApacheCompatible,
			boolean isCommercialUseAllowed, String website, String notice) {
		this.name = name;
		this.isApacheCompatible = isApacheCompatible;
		this.isCommercialUseAllowed = isCommercialUseAllowed;
		this.website = website;
		this.notice = notice;
	}

	/** {@inheritDoc} */
	@Override
	public String getName() {
		return name;
	}

	/** {@inheritDoc} */
	@Override
	public String getWebsite() {
		return website;
	}

	/** {@inheritDoc} */
	@Override
	public String getNotice() {
		return notice;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isApacheCompatible() {
		return isApacheCompatible;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isCommercialUseAllowed() {
		return isCommercialUseAllowed;
	}

	/**
	 * Creates a new license information by parsing a properties object and
	 * optionally reading a file containing notice information.
	 */
	public static CustomLicense fromProperties(Properties props, File noticeFile)
			throws IOException {
		String name = props.getProperty("license");
		String website = props.getProperty("license.website");
		String notice = props.getProperty("license.notice");

		// if the property is not set (i.e. null) parseBoolean returns false
		boolean apacheCompatible = Boolean.parseBoolean(props
				.getProperty("license.apacheCompatible"));
		boolean commercialUse = Boolean.parseBoolean(props
				.getProperty("license.commercialUse"));

		if (noticeFile != null && noticeFile.exists()) {
			notice = StringUtils.normalizeLineBreaks(FileSystemUtils
					.readFile(noticeFile));
		}

		return new CustomLicense(name, apacheCompatible, commercialUse,
				website, notice);
	}

}
