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
package org.conqat.engine.persistence.store.rmi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import org.apache.log4j.Logger;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.driver.cqddl.CQDDLExecutionException;
import org.conqat.engine.core.driver.cqddl.CQDDLUtils;
import org.conqat.engine.core.driver.runner.ConQATRunnableBase;
import org.conqat.engine.persistence.store.IStorageSystem;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.engine.persistence.store.mem.InMemoryStorageSystem;
import org.conqat.lib.commons.net.LocalhostRMISocketFactory;
import org.conqat.lib.commons.net.SmartRMISocketFactory;
import org.conqat.lib.commons.options.AOption;
import org.conqat.lib.cqddl.parser.CQDDLParsingParameters;

/**
 * A ConQAT runnable that can be used for starting a storage server. The
 * underlying (local) storage system is created by executing a factory (i.e. a
 * ConQAT processor) which may be parameterized using CQDDL.
 * <p>
 * For example to start a RMI server with an {@link InMemoryStorageSystem} that
 * start parameters should be:
 * 
 * <pre>
 * -f org.conqat.engine.persistence.store.mem.InMemoryStorageFactory
 * </pre>
 * 
 * If you also want to specify the base directory (i.e. parameter
 * "storage.dir"), you can use the following parameter in addition
 * 
 * <pre>
 * -o 'storage=(dir="/path/to/storage/dir")'
 * </pre>
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46960 $
 * @ConQAT.Rating GREEN Hash: 14EE112997B8E4D39EC2CE2EA3AE2D02
 */
public class RmiStorageServer extends ConQATRunnableBase {

	/** Logger. */
	private static final Logger LOGGER = Logger
			.getLogger(RmiStorageServer.class);

	/**
	 * URL used by the server to bind the {@link IRemoteStore} remote interface.
	 */
	public static final String STORAGE_SERVER_URL = IRemoteStore.class
			.getSimpleName();

	/** The default port used for publishing the RMI interface. */
	public static final int DEFAULT_PORT = 17336;

	/** The port actually used. */
	private int port = DEFAULT_PORT;

	/**
	 * The name of the ConQAT processor/class used for creating the storage
	 * system.
	 */
	private String storageFactoryClass;

	/**
	 * The parameters to the storage factory using CQDDL syntax (see header
	 * comment for an example).
	 */
	private String storageFactoryParameters = "()";

	/** The storage system used. */
	private IStorageSystem storageSystem;

	/** The actual remote store. */
	private RemoteStore remoteStore;

	/**
	 * Holds a reference to the stub. This is required that the distributed GC
	 * does not kill the object.
	 */
	@SuppressWarnings("unused")
	private Remote stub;

	/** {@inheritDoc} */
	@Override
	protected void doRun() {
		storageSystem = createStorageSystem();
		remoteStore = new RemoteStore(storageSystem);
		publish();
		waitForExit();
		shutDown();
	}

	/** Creates the storage system. */
	private IStorageSystem createStorageSystem() {
		if (storageFactoryClass == null) {
			System.err.println("Factory to use was not provided.");
			printUsageAndExit();
		}

		try {
			Object storeObject = CQDDLUtils.executeProcessor(
					storageFactoryClass, new CQDDLParsingParameters(),
					storageFactoryParameters);
			if (storeObject instanceof IStorageSystem) {
				return (IStorageSystem) storeObject;
			}

			String returned = "null";
			if (storeObject != null) {
				returned = storeObject.getClass().getName();
			}
			LOGGER.fatal("Factory did not return a storage system but "
					+ returned);
		} catch (ConQATException e) {
			LOGGER.fatal("Factory did not complete normally!", e);
		} catch (CQDDLExecutionException e) {
			LOGGER.fatal("Factory seems to be invalid or misconfigured!", e);
		}

		System.exit(1);
		return null;
	}

	/** Publishes the RMI interface. */
	private void publish() {
		SmartRMISocketFactory factory = new LocalhostRMISocketFactory();
		try {
			stub = UnicastRemoteObject.exportObject(remoteStore, 0, factory,
					factory);
			LocateRegistry.createRegistry(port).rebind(STORAGE_SERVER_URL,
					remoteStore);
		} catch (RemoteException e) {
			LOGGER.fatal("Could not publish remote interface!", e);
			System.exit(1);
		}
		LOGGER.info("Storage server ready; type 'exit' to stop server.");
	}

	/**
	 * Waits until the system should exit. Typically, thisi s performed
	 * interactively by the user typing "exit". However, there are cases where
	 * the system is started in a non-interactive context (i.e. an empty console
	 * input stream). In this case, we switch to an infinite loop and wait for
	 * process termination via Ctrl-C or similar.
	 */
	private void waitForExit() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in));

		boolean hadAnyInput = false;
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				hadAnyInput = true;
				if (line.trim().equalsIgnoreCase("exit")) {
					return;
				}
			}
		} catch (IOException e) {
			LOGGER.error("Had problems with user input!", e);
		}

		if (!hadAnyInput) {
			// non-interactive mode

			while (true) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// can be ignored
				}
			}
		}
	}

	/** Closes all stores and terminates the server. */
	private void shutDown() {
		LOGGER.info("Closing storage system.");
		try {
			storageSystem.close();
		} catch (StorageException e) {
			LOGGER.error("Had problems closing storage system", e);
		}

		LOGGER.info("Terminated successfully.");
		System.exit(0);
	}

	/** Set the port. */
	@AOption(shortName = 'p', longName = "port", description = "Set the port to bind to (default: "
			+ DEFAULT_PORT + ").")
	public void setPort(int port) {
		this.port = port;
	}

	/** Set the port. */
	@AOption(shortName = 'f', longName = "factory", description = "Set the factory used. "
			+ "Must be the class name of a ConQAT processor returning a storage system.")
	public void setFactoryClass(String factoryClassName) {
		this.storageFactoryClass = factoryClassName;
	}

	/** Set the port. */
	@AOption(shortName = 'o', longName = "options", description = "Sets the parameters/options for the factory. "
			+ "The options must be given as a CQDDL string.")
	public void setFactoryParameters(String parameters) {
		if (!parameters.startsWith("(")) {
			parameters = "(" + parameters + ")";
		}
		this.storageFactoryParameters = parameters;
	}
}