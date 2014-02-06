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

import static org.conqat.engine.bugzilla.lib.EBugzillaField.COMMENT;
import static org.conqat.engine.bugzilla.lib.EBugzillaField.COMPONENT;
import static org.conqat.engine.bugzilla.lib.EBugzillaField.FILE_LOC;
import static org.conqat.engine.bugzilla.lib.EBugzillaField.ID;
import static org.conqat.engine.bugzilla.lib.EBugzillaField.LONG_DESCRIPTION_LENGTH;
import static org.conqat.engine.bugzilla.lib.EBugzillaField.OS;
import static org.conqat.engine.bugzilla.lib.EBugzillaField.PLATFORM;
import static org.conqat.engine.bugzilla.lib.EBugzillaField.PRIORITY;
import static org.conqat.engine.bugzilla.lib.EBugzillaField.PRODUCT;
import static org.conqat.engine.bugzilla.lib.EBugzillaField.SEVERITY;
import static org.conqat.engine.bugzilla.lib.EBugzillaField.SHORT_DESCRIPTION;
import static org.conqat.engine.bugzilla.lib.EBugzillaField.VERSION;

import java.io.IOException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.protocol.Protocol;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * A simple read-only client for the Bugzilla web interface.
 * 
 * This is known to work for Bugzilla 3.0.2.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 46826 $
 * @ConQAT.Rating GREEN Hash: C1ECF6F24B50281A7480A4284FD3B265
 */
public class BugzillaWebClient {

	/**
	 * Pattern for matching the part of the response that contains the error
	 * message.
	 */
	private final static Pattern ERROR_MESSAGE_PATTERN = Pattern
			.compile("(?s)<td\\s+bgcolor=\"#ff0000\">\\s*<font\\s+size=\"\\+2\">\\s*(.+)\\s*</font>\\s*</td>");

	/** Pattern for matching the bug id after bug creation. */
	private final static Pattern BUG_ID_PATTERN = Pattern
			.compile("Bug (\\d{1,}) Submitted");

	/** Strings used in Bugzilla responses that signal errors. */
	private final static String[] ERROR_MARKERS = { "Internal Error",
			"Summary Needed", "Illegal Date",
			"Specified Component Does Not Exist" };

	/** Socket timeout, set to 10 seconds */
	private static final int TIMEOUT = 10 * 1000;

	/** The Bugzilla server. */
	private final String server;

	/** The HTTP client used to access Bugzilla. */
	private final HttpClient client = new HttpClient();

	/**
	 * Create new Bugzilla client.
	 * 
	 * @param server
	 *            address of the server, e.g. http://bugzilla.mydomain.com
	 * 
	 * @throws IllegalStateException
	 *             if SSL library could not be accessed.
	 */
	public BugzillaWebClient(String server) {
		this(server, null, null);
	}

	/**
	 * Create new Bugzilla client.
	 * 
	 * @param server
	 *            address of the server, e.g. http://bugzilla.mydomain.com
	 * 
	 * @param httpAuthUser
	 *            the username for HTTP basic authentification. If this is null,
	 *            no authentification is performed. Note that this is the
	 *            username/password asked by the browser, not by the bugzilla
	 *            page.
	 * 
	 * @param httpAuthPassword
	 *            the password for HTTP basic authentification. If this is null,
	 *            no authentification is performed. Note that this is the
	 *            username/password asked by the browser, not by the bugzilla
	 *            page.
	 * 
	 * @throws IllegalStateException
	 *             if SSL library could not be accessed.
	 */
	public BugzillaWebClient(String server, String httpAuthUser,
			String httpAuthPassword) {
		this.server = server;

		// make sure we can deal with https-based servers
		Protocol.registerProtocol("https", new Protocol("https",
				new SimpleSSLSocketFactory(), 443));

		client.getHttpConnectionManager().getParams()
				.setConnectionTimeout(TIMEOUT);

		if (httpAuthUser != null && httpAuthPassword != null) {
			client.getState().setCredentials(
					AuthScope.ANY,
					new UsernamePasswordCredentials(httpAuthUser,
							httpAuthPassword));
		}
	}

	/**
	 * Add a comment to an existing Bug.
	 * 
	 * @param id
	 *            id of the bug.
	 * @param comment
	 *            comment to add
	 * @throws BugzillaException
	 *             if Bugzilla responded with an exception to the bug was not
	 *             found.
	 * @throws IOException
	 *             if an I/O problem occurs while obtaining the response from
	 *             Bugzilla
	 */
	public void addComment(int id, String comment) throws BugzillaException,
			IOException {
		updateBug(id, new BugzillaFields(COMMENT, comment));
	}

	/**
	 * Update a bug. This method cannot be used to change the status of a bug.
	 * 
	 * @param id
	 *            bug id
	 * @param fields
	 *            fields to set.
	 * @throws BugzillaException
	 *             if Bugzilla responded with an exception to the bug was not
	 *             found.
	 * @throws IOException
	 *             if an I/O problem occurs while obtaining the response from
	 *             Bugzilla
	 */
	public void updateBug(int id, BugzillaFields fields)
			throws BugzillaException, IOException {
		Bug bug = getBug(id);
		if (bug == null) {
			throw new BugzillaException("Bug with id " + id + " not found");
		}

		fields.setValue(ID, String.valueOf(id));
		fields.copyAllNonExistingFields(bug);

		// these two are required by Bugzilla
		fields.setNonExistingValue(FILE_LOC, StringUtils.EMPTY_STRING);
		fields.setNonExistingValue(LONG_DESCRIPTION_LENGTH, "1");

		executePostMethod("process_bug.cgi", fields);
	}

	/**
	 * Authenticate with Bugzilla server. Note that Bugzilla servers can often
	 * be accessed without credentials in a read-only manner. Hence, credentials
	 * are not necessarily required although your Bugzilla server requires them
	 * to edit bugs. Depending on the permissions set on the Bugzilla server,
	 * calls to {@link #query(Set, Set, Set)} may return different results
	 * whether you are authenticated or not.
	 * 
	 * @throws BugzillaException
	 *             If Bugzilla server responded with an error.
	 * @throws IOException
	 *             if an I/O problem occurs while obtaining the response from
	 *             Bugzilla
	 * @throws HttpException
	 *             if a protocol exception occurs
	 */
	public void authenticate(String username, String password)
			throws BugzillaException, HttpException, IOException {
		NameValuePair[] formData = new NameValuePair[2];
		formData[0] = new NameValuePair("Bugzilla_login", username);
		formData[1] = new NameValuePair("Bugzilla_password", password);

		PostMethod postMethod = new PostMethod(server + "/index.cgi");

		postMethod.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded");
		postMethod.setRequestBody(formData);
		postMethod.setDoAuthentication(true);
		postMethod.setFollowRedirects(false);
		client.getState().clearCookies();

		try {
			int code = client.executeMethod(postMethod);
			if (code != HttpStatus.SC_OK) {
				throw new BugzillaException("Authentication failed: "
						+ HttpStatus.getStatusText(code));
			}

			// Bugzilla assigns cookies if everything went ok.
			Cookie[] cookies = client.getState().getCookies();
			if (cookies.length == 0) {
				throw new BugzillaException("Authentication failed!");
			}

			// the following loop fixes CR#2801. For some reason, Bugzilla only
			// accepts the cookies if they are not limited to secure
			// connections.
			for (Cookie c : cookies) {
				c.setSecure(false);
			}
		} finally {
			postMethod.releaseConnection();
		}
	}

	/**
	 * Create a new bug.
	 * 
	 * @param fields
	 *            fields to set. Required are {@link EBugzillaField#PRODUCT},
	 *            {@link EBugzillaField#COMPONENT},
	 *            {@link EBugzillaField#VERSION} and
	 *            {@link EBugzillaField#SHORT_DESCRIPTION}.
	 * @return the id of the newly created bug.
	 * @throws BugzillaException
	 *             If Bugzilla server responded with an error.
	 * @throws IOException
	 *             if an I/O problem occurs while obtaining the response from
	 *             Bugzilla
	 * @throws HttpException
	 *             if a protocol exception occurs
	 */
	public int createBug(BugzillaFields fields) throws BugzillaException,
			HttpException, IOException {

		fields.checkFields(PRODUCT, COMPONENT, VERSION, SHORT_DESCRIPTION);

		fields.setNonExistingValue(OS, "All");
		fields.setNonExistingValue(PLATFORM, "All");
		fields.setNonExistingValue(PRIORITY, "P5");
		fields.setNonExistingValue(SEVERITY, "normal");

		String response = executePostMethod("post_bug.cgi", fields);

		Matcher bodyMatcher = BUG_ID_PATTERN.matcher(response);
		if (!bodyMatcher.find()) {
			throw new BugzillaException(
					"Could not determine id of new bug. Most probably something went wrong.");
		}

		String idString = bodyMatcher.group(1);
		try {
			return Integer.parseInt(idString);
		} catch (NumberFormatException ex) {
			throw new BugzillaException(
					"Could not determine id of new bug. Most probably something went wrong.");
		}
	}

	/**
	 * Get bug with specific ID. This returns <code>null</code> if the bug was
	 * not found.
	 * 
	 * @throws BugzillaException
	 *             If Bugzilla server responded with an error.
	 * @throws IOException
	 *             if an I/O problem occurs while obtaining the response from
	 *             Bugzilla
	 */
	public Bug getBug(int id) throws BugzillaException, IOException {
		BugzillaQuery query = new BugzillaQuery(server);
		query.setBugId(id);
		Set<Bug> bugs = query(query);
		CCSMAssert.isFalse(bugs.size() > 1, "Cannot return more then one bug.");
		if (bugs.isEmpty()) {
			return null;
		}
		return CollectionUtils.getAny(bugs);
	}

	/**
	 * Issues the given query by executing an HTTP query and returning the
	 * resulting set of Bugs.
	 */
	public Set<Bug> query(BugzillaQuery query) throws BugzillaException,
			HttpException, IOException {
		GetMethod get = new GetMethod(query.createURL());
		try {
			int result = client.executeMethod(get);

			if (result != HttpStatus.SC_OK) {
				throw new BugzillaException(
						"Could not retrieve bugs from server " + server + ": "
								+ get.getStatusText());
			}
			return BugzillaRdfParser.parseRDF(get.getResponseBodyAsStream());
		} finally {
			get.releaseConnection();
		}
	}

	/**
	 * Retrieve set of bugs.
	 * 
	 * @deprecated Use {@link #query(BugzillaQuery)} instead.
	 * 
	 * @param products
	 *            the products the bugs belong to (may be <code>null</code>).
	 * @param components
	 *            the components the bugs belong to (may be <code>null</code>).
	 * @param statuses
	 *            the statuses of the bugs (may be <code>null</code>).
	 * 
	 * @throws BugzillaException
	 *             If Bugzilla server responded with an error.
	 * @throws IOException
	 *             if an I/O problem occurs while obtaining the response from
	 *             Bugzilla
	 * @throws HttpException
	 *             if a protocol exception occurs
	 */
	@Deprecated
	public Set<Bug> query(Set<String> products, Set<String> components,
			Set<EBugzillaStatus> statuses) throws BugzillaException,
			HttpException, IOException {
		BugzillaQuery query = new BugzillaQuery(server);
		query.setProducts(products);
		query.setComponents(components);
		query.setBugStatuses(statuses);
		return query(query);
	}

	/**
	 * Returns the review backlog for the given user, i.e. all bugs where the
	 * user is in the role QA contact that are in the resolved state.
	 */
	public Set<Bug> getReviewBacklog(String user) throws BugzillaException,
			HttpException, IOException {
		BugzillaQuery query = new BugzillaQuery(server);
		query.setQaContact(user);
		query.setBugStatuses(CollectionUtils
				.asHashSet(EBugzillaStatus.RESOLVED));
		return query(query);
	}

	/**
	 * This executes an HTTP post on a Bugzilla URL and deals with the handling
	 * of error messages.
	 * 
	 * @return the body of the HTTP response.
	 * @throws BugzillaException
	 *             If Bugzilla server responded with an error.
	 * @throws IOException
	 *             if an I/O problem occurs while obtaining the response from
	 *             Bugzilla
	 * @throws HttpException
	 *             if a protocol exception occurs
	 */
	private String executePostMethod(String url, BugzillaFields fields)
			throws HttpException, IOException, BugzillaException {
		PostMethod postMethod = new PostMethod(server + "/" + url);

		postMethod.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded; charset=UTF-8");
		postMethod.setRequestBody(fields.createFormData());
		postMethod.setFollowRedirects(false);
		try {
			int result = client.executeMethod(postMethod);
			if (result != HttpStatus.SC_OK) {
				throw new BugzillaException(
						"Error occured when adding comment.");
			}

			String response = postMethod.getResponseBodyAsString();
			if (StringUtils.containsOneOf(response, ERROR_MARKERS)) {
				throw new BugzillaException(extractErrorMessage(response));
			}
			return response;
		} finally {
			postMethod.releaseConnection();
		}
	}

	/**
	 * Extract error message from the HTML response send by Bugzilla. This
	 * returns an unknown error if the error message could not be extracted.
	 */
	private String extractErrorMessage(String response) {
		Matcher matcher = ERROR_MESSAGE_PATTERN.matcher(response);
		if (!matcher.find()) {
			return "Unknown error";
		}

		String message = matcher.group(1).trim();
		message = message.replaceAll("\\s+", StringUtils.SPACE);
		return message;
	}

}