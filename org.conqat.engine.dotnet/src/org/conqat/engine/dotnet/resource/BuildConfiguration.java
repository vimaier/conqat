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
package org.conqat.engine.dotnet.resource;

/**
 * This class represents a build configuration that is used to build visual
 * studio projects.
 * 
 * @author feilkas
 * @author $Author: juergens $
 * @version $Rev: 35167 $
 * @ConQAT.Rating GREEN Hash: AEDD973849C9357C40631D4FFFF7A71E
 */
public class BuildConfiguration {

	/** The name of the configuration used to build the solution. */
	private final String name;

	/** The platform for which the solution is compiled. */
	private final String platform;

	/** Constructor */
	public BuildConfiguration(String name, String platform) {
		this.name = name;
		this.platform = platform;
	}

	/** Returns name of the configuration used to build the solution. */
	public String getName() {
		return name;
	}

	/** Returns platform for which the solution is compiled. */
	public String getPlatform() {
		return platform;
	}
}