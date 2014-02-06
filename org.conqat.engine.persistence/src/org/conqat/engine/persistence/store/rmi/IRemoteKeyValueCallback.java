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

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.conqat.engine.persistence.store.IKeyValueCallback;

/**
 * This is a remote enabled version of {@link IKeyValueCallback}.
 * 
 * @author $Author: heineman $
 * @version $Rev: 37925 $
 * @ConQAT.Rating GREEN Hash: 217EB3AEAC1215F38A887F12E956250C
 */
public interface IRemoteKeyValueCallback extends Remote {

	/** The callback function. */
	void callback(byte[] key, byte[] value) throws RemoteException;
}