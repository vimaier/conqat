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

/**
 * Enumeration of well-known software licenses.
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 46536 $
 * @ConQAT.Rating YELLOW Hash: 5CF4C5FD88C2772047F00DDA2B039C9C
 */
public enum ELicense implements ILicense {

	/** Apache License, Version 1.0 */
	APACHE_LICENSE_v1_0("Apache License 1.0", true, true,
			"http://www.apache.org/licenses/LICENSE-1.0"),

	/** Apache License, Version 1.1 */
	APACHE_LICENSE_v1_1("Apache License 1.1", true, true,
			"http://www.apache.org/licenses/LICENSE-1.1"),

	/** Apache License, Version 2.0 */
	APACHE_LICENSE_v2_0("Apache License 2.0", true, true,
			"http://www.apache.org/licenses/LICENSE-2.0"),

	/** GNU Public License v3 */
	GPL_v3("GNU General Public License Version 3", false, false,
			"http://www.gnu.org/licenses/gpl.html", "GPLv3"),

	/** GNU Public License v2 */
	GPL_v2("GNU General Public License Version 2", false, false,
			"http://www.gnu.org/licenses/old-licenses/gpl-2.0.html", "GPLv2"),

	/** GNU Lesser General Public License v3 */
	LGPL_v3("GNU Lesser General Public License Version 3", false, true,
			"http://www.gnu.org/copyleft/lesser.html", "LGPLv3"),

	/** Eclipse Distribution License */
	EDL_v1_0("Eclipse Distribution License Version 1.0", true, true,
			"http://www.eclipse.org/org/documents/edl-v10.php", "EDLv1"),

	/** Eclipse Public License */
	EPL_v1_0("Eclipse Public License Version 1.0", true, true,
			"http://www.eclipse.org/org/documents/epl-v10.php", "EPLv1"),

	/** Common Public License */
	CPL_v1_0("Common Public License Version 1.0", true, true,
			"http://www.opensource.org/licenses/cpl1.0.txt", "CPLv1"),

	/** MIT License */
	MIT("MIT License", true, true,
			"http://opensource.org/licenses/mit-license.php", "MIT"),

	/** BSD 3-Clause License */
	BSD_3_CLAUSE("BSD 3-Clause", true, true,
			"http://opensource.org/licenses/BSD-3-Clause", "BSD New",
			"BSD Simplified"),

	/** BSD 2-Clause License */
	BSD_2_CLAUSE("BSD 2-Clause", true, true,
			"http://opensource.org/licenses/BSD-2-Clause", "BSD");

	/** The name of the license. */
	private final String name;

	/** Aliases for the license name. */
	private final String[] aliases;

	/** The website of the license. */
	private final String website;

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
	private ELicense(String name, boolean isApacheCompatible,
			boolean isCommercialUseAllowed, String website, String... aliases) {
		this.name = name;
		this.isApacheCompatible = isApacheCompatible;
		this.isCommercialUseAllowed = isCommercialUseAllowed;
		this.website = website;
		this.aliases = aliases;
	}

	/** {@inheritDoc} */
	@Override
	public String getName() {
		return this.name;
	}

	/** {@inheritDoc} */
	@Override
	public String getWebsite() {
		return this.website;
	}

	/** {@inheritDoc} */
	@Override
	public String getNotice() {
		return null;
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
	 * @return A license object created from the license name, or
	 *         <code>null</code> if no license with the given name is found.
	 */
	public static ILicense fromName(String licenseName) {
		for (ELicense license : values()) {
			if (license.name.equals(licenseName)) {
				return license;
			}
			for (String alias : license.aliases) {
				if (alias.equals(licenseName)) {
					return license;
				}
			}
		}
		return null;
	}
}
