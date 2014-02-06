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

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;

/**
 * Fetches the dashboard statistics JSON data from an HTTP source using basic
 * authentication.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 40011 $
 * @ConQAT.Rating GREEN Hash: E888B800661B87E9E34D1C207F63F210
 */
public class PortfolioHttpAuthenticatedDataFetcher extends
		PortfolioHttpDataFetcher {

	/** Constructor. */
	public PortfolioHttpAuthenticatedDataFetcher(String name,
			String locationUrl, String username, String password) {
		super(name, locationUrl);
		client.getParams().setAuthenticationPreemptive(true);
		Credentials credentials = new UsernamePasswordCredentials(username,
				password);
		client.getState().setCredentials(AuthScope.ANY, credentials);
	}
}
