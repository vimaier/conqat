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
package org.conqat.engine.bugzilla.lib;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.conqat.lib.commons.net.TrustAllCertificatesManager;

/**
 * A simple socket factory for SSL that uses a trust manager that accepts
 * <i>all</i> certificates.
 * <p>
 * Please note that this is only tested in the context of the
 * {@link BugzillaWebClient} and is not meant to be reused elsewhere.
 * 
 * @see TrustAllCertificatesManager
 * 
 * @author $Author: hummelb $
 * @version $Rev: 46826 $
 * @ConQAT.Rating GREEN Hash: 0B716B09DB9E27D73E88C99783B56EE1
 */
/* package */class SimpleSSLSocketFactory implements ProtocolSocketFactory {
	/** The SSL context used to create sockets. */
	private final SSLContext sc;

	/**
	 * Create new factory.
	 * 
	 * @throws IllegalStateException
	 *             if there is something wrong with SSL support.
	 */
	public SimpleSSLSocketFactory() {

		try {
			sc = SSLContext.getInstance("SSL");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(
					"Could not find support for SSL. Check your Java installation. "
							+ e.getMessage(), e);
		}

		try {
			TrustManager[] trustAllCerts = new TrustManager[] { new TrustAllCertificatesManager() };
			sc.init(null, trustAllCerts, null);
		} catch (KeyManagementException e) {
			// should not happen in this context
			throw new IllegalStateException("Key Management Exception: "
					+ e.getMessage(), e);
		}

	}

	/** {@inheritDoc} */
	@Override
	public Socket createSocket(String host, int port) throws IOException,
			UnknownHostException {
		// work-around for bug in Java 7 (see
		// http://stackoverflow.com/questions/7615645/ssl-handshake-alert-unrecognized-name-error-since-upgrade-to-java-1-7-0
		// and http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=7127374 for
		// details)
		Socket socket = sc.getSocketFactory().createSocket();
		socket.connect(new InetSocketAddress(host, port));
		return socket;
	}

	/** {@inheritDoc} */
	@Override
	public Socket createSocket(String host, int port, InetAddress localAddress,
			int localPort) throws IOException, UnknownHostException {
		// work-around for bug in Java 7 (see
		// http://stackoverflow.com/questions/7615645/ssl-handshake-alert-unrecognized-name-error-since-upgrade-to-java-1-7-0
		// and http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=7127374 for
		// details)
		Socket socket = sc.getSocketFactory().createSocket();
		socket.bind(new InetSocketAddress(localAddress, localPort));
		socket.connect(new InetSocketAddress(host, port));
		return socket;
	}

	/**
	 * This just forwards to
	 * {@link #createSocket(String, int, InetAddress, int)}.
	 */
	@Override
	public Socket createSocket(String host, int port, InetAddress localAdress,
			int localPort, HttpConnectionParams params) throws IOException,
			UnknownHostException, ConnectTimeoutException {
		return createSocket(host, port, localAdress, localPort);
	}
}