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
package org.conqat.engine.dotnet;

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.commons.execution.ProcessExecutorBase;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.util.ConQATFileUtils;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.filesystem.CanonicalFile;

/**
 * Common functionality for processors that execute .NET applications. The .NET
 * executable can be run multiple times. The processor falls back to using Mono
 * when it is run on a non-Windows system.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46336 $
 * @ConQAT.Rating GREEN Hash: 0319AD2E283F78C2352224318A22655D
 */
public abstract class DotnetExecutorBase extends ProcessExecutorBase {

	/** The .NET executable file. */
	private CanonicalFile executable;

	/** The command executed next. */
	private List<String> command = null;

	/**
	 * Set the path of the external tool's executable. This method has to be
	 * called before {@link #execute} can be called.
	 */
	protected void setExecutable(String path) throws ConQATException {
		executable = ConQATFileUtils.createCanonicalFile(path);
		if (!executable.exists()) {
			throw new ConQATException("The file "
					+ executable.getCanonicalPath() + " does not exist.");
		}
	}

	/**
	 * Executes the external tool with the given arguments. Make sure to call
	 * {@link #setExecutable} prior to the first call to this method.
	 */
	protected void execute(List<String> arguments) throws ConQATException {
		CCSMAssert.isNotNull(executable,
				"Please call 'setExecutable' before calling 'execute'");
		command = new ArrayList<String>();
		if (useMono()) {
			command.add("mono");
		}
		command.add(executable.getCanonicalPath());
		command.addAll(arguments);
		super.process();
	}

	/**
	 * Determines whether to use mono. Returns <code>false</code> if the
	 * processor is executed on a Windows machine and <code>true</code>
	 * otherwise.
	 */
	private static boolean useMono() {
		return !System.getProperty("os.name").startsWith("Windows");
	}

	/** {@inheritDoc} */
	@Override
	protected List<String> getCommand() {
		return CollectionUtils.asUnmodifiable(command);
	}
}
