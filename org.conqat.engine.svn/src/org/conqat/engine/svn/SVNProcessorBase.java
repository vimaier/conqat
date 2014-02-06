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
package org.conqat.engine.svn;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * Base class for processors working with SVN.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 39948 $
 * @ConQAT.Rating GREEN Hash: AC2CF7C8E7F1C9BDDE671161A9F9D1D3
 */
public abstract class SVNProcessorBase extends ConQATProcessorBase {

	/** Repository URL. */
	private String url;

	/** User name. */
	private String userName;

	/** The password. */
	private String password;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "root", minOccurrences = 1, maxOccurrences = 1, description = "Repository root URL")
	public void setRootDirectory(
			@AConQATAttribute(name = "url", description = "URL") String url) {
		this.url = url;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.AUTH_NAME, maxOccurrences = 1, description = ConQATParamDoc.AUTH_DESC)
	public void setAuthenticationInformation(
			@AConQATAttribute(name = ConQATParamDoc.AUTH_USER_NAME, description = ConQATParamDoc.AUTH_USER_DESC) String userName,
			@AConQATAttribute(name = ConQATParamDoc.AUTH_PASS_NAME, description = ConQATParamDoc.AUTH_PASS_DESC) String password) {
		this.userName = userName;
		this.password = password;
	}

	/**
	 * Set up repository. This method also determines the head revision of the
	 * repository.
	 * 
	 * @throws ConQATException
	 *             if setup fails.
	 */
	protected SVNRepository createRepository() throws ConQATException {
		DAVRepositoryFactory.setup();
		FSRepositoryFactory.setup();
		
		try {
			SVNRepository repository = SVNRepositoryFactory.create(SVNURL
					.parseURIEncoded(url));

			ISVNAuthenticationManager authManager;
			if (userName != null) {
				authManager = SVNWCUtil.createDefaultAuthenticationManager(
						userName, password);
			} else {
				authManager = SVNWCUtil.createDefaultAuthenticationManager();
			}
			repository.setAuthenticationManager(authManager);

			return repository;

		} catch (SVNException e) {
			throw new ConQATException(e);
		}
	}

	/** Returns the url. */
	protected String getUrl() {
		return url;
	}
}