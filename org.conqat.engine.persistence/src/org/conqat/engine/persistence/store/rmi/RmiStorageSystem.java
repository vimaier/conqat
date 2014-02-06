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

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.conqat.engine.persistence.store.IStorageSystem;
import org.conqat.engine.persistence.store.IStore;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.lib.commons.net.LocalhostRMISocketFactory;
import org.conqat.lib.commons.net.SmartRMISocketFactory;

/**
 * A simple storage system based on RMI communication.
 * 
 * @author $Author: heineman $
 * @version $Rev: 37938 $
 * @ConQAT.Rating GREEN Hash: F76A5B7DAFBB974089DE3F273ED6849C
 */
public class RmiStorageSystem implements IStorageSystem {

	/** The remote store. */
	private IRemoteStore remoteStore;

	/** Constructor. */
	public RmiStorageSystem(String host, int port) throws RemoteException,
			NotBoundException {
		SmartRMISocketFactory factory;
		if ("localhost".equals(host) || "127.0.0.1".equals(host)) {
			factory = new LocalhostRMISocketFactory();
		} else {
			factory = new SmartRMISocketFactory();
		}

		Registry registry = LocateRegistry.getRegistry(host, port, factory);
		remoteStore = (IRemoteStore) registry
				.lookup(RmiStorageServer.STORAGE_SERVER_URL);
	}

	/** {@inheritDoc} */
	@Override
	public IStore openStore(String name) throws StorageException {
		if (remoteStore == null) {
			throw new StorageException("May not access store after closing!");
		}

		return new RmiStore(name, remoteStore);
	}

	/** {@inheritDoc} */
	@Override
	public void removeStore(String storeName) throws StorageException {
		throw new StorageException("Removal not supported via RMI.");
	}

	/** {@inheritDoc} */
	@Override
	public void close() {
		// allow garbage collector to kill the connection
		remoteStore = null;
	}
}