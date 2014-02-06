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
package org.conqat.engine.html_presentation.portfolio;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.commons.httpclient.methods.PostMethod;
import org.conqat.engine.core.core.ConQATException;

/**
 * Fetches the dashboard statistics JSON data from an HTTP source behind a
 * Jenkins login form.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 40015 $
 * @ConQAT.Rating GREEN Hash: 1A0FE66A09C561C32F41FF146622C4DE
 */
public class PortfolioHttpJenkinsDataFetcher extends PortfolioHttpDataFetcher {

	/** The username to use when logging into Jenkins. */
	private final String username;

	/** The password to use when logging into Jenkins. */
	private final String password;

	/** The base URL of the Jenkins server. */
	private final String jenkinsBaseUrl;

	/** Constructor. */
	public PortfolioHttpJenkinsDataFetcher(String name, String locationUrl,
			String jenkinsBaseUrl, String username, String password) {
		super(name, locationUrl);
		this.jenkinsBaseUrl = jenkinsBaseUrl;
		this.username = username;
		this.password = password;
	}

	/** {@inheritDoc} */
	@Override
	protected String getJsonString(String dataJsonFileLocation)
			throws ConQATException, IOException {
		try {
			PostMethod method = new PostMethod(jenkinsBaseUrl
					+ "/j_acegi_security_check");
			method.setParameter("j_username", username);
			method.setParameter("j_password", password);
			client.executeMethod(method);
			int statusCode = method.getStatusCode();
			if (statusCode != 302) {
				throw new IOException(
						"Jenkins login request was not successful. "
								+ "Server returned status code " + statusCode);
			}
		} catch (MalformedURLException e) {
			throw new ConQATException("Unable to parse URL "
					+ dataJsonFileLocation, e);
		}
		return super.getJsonString(dataJsonFileLocation);
	}

}
