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
package org.conqat.engine.bugzilla.lib;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Set;

import org.conqat.lib.commons.assertion.CCSMAssert;

/**
 * Represents a query to Bugzilla. Only a subset of the Bugzilla fields are
 * currently supported.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 46826 $
 * @ConQAT.Rating GREEN Hash: 718BB91A1BE78A045DC5DF0DA97054AB
 */
public class BugzillaQuery {

	/** Denotes that the bug ID was not set */
	private static final int NOT_SET = -1;

	/** The URL of the server */
	private final String server;

	/** Set of products */
	private Set<String> products;

	/** Set of components */
	private Set<String> components;

	/** Set of bug statuses */
	private Set<EBugzillaStatus> bugStatuses;

	/** Bug ID */
	private int bugId = NOT_SET;

	/** The bug's "changed since" timestamp. */
	private String changedSince;

	/** QA contact */
	private String qaContact;

	/**
	 * Constructs an empty query for the given server. Use setters to set query
	 * parameters.
	 */
	public BugzillaQuery(String server) {
		this.server = server;
	}

	/** Sets the products */
	public void setProducts(Set<String> products) {
		this.products = products;
	}

	/** Sets the components */
	public void setComponents(Set<String> components) {
		this.components = components;
	}

	/** Sets the bug statuses */
	public void setBugStatuses(Set<EBugzillaStatus> bugStatuses) {
		this.bugStatuses = bugStatuses;
	}

	/** Sets the bug ID */
	public void setBugId(int bugId) {
		this.bugId = bugId;
	}
	
	/** Sets the changed since timestamp. */
	public void setChangedSince(String changedSince) {
		this.changedSince = changedSince;
	}

	/** Sets the QA contact */
	public void setQaContact(String qaContact) {
		this.qaContact = qaContact;
	}

	/** Creates a URL for the HTTP query. */
	public String createURL() {
		StringBuilder url = new StringBuilder();
		url.append(server);
		url.append("/buglist.cgi?query_format=advanced&");

		if (products != null) {
			for (String product : products) {
				url.append("product=" + encodeUTF8(product) + "&");
			}
		}

		if (components != null) {
			for (String component : components) {
				url.append("component=" + encodeUTF8(component) + "&");
			}
		}

		if (bugStatuses != null) {
			for (EBugzillaStatus status : bugStatuses) {
				url.append("bug_status=" + encodeUTF8(status.name()) + "&");
			}
		}
		
		if (changedSince != null) {
			url.append("chfieldto=Now&");
			url.append("chfieldfrom=" + changedSince + "&");
		}

		if (qaContact != null) {
			url.append("emailqa_contact1=1&");
			url.append("email1=" + encodeUTF8(qaContact) + "&");
			url.append("emailtype1=exact&");
		}

		if (bugId != NOT_SET) {
			url.append("bug_id=" + bugId + "&");
		}

		url.append("columnlist=all&");
		url.append("ctype=rdf");
		return url.toString();
	}

	/** Encode URL. */
	private static String encodeUTF8(String url) {
		try {
			return URLEncoder.encode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			CCSMAssert.fail("Unknown encoding");
			return null;
		}
	}

}
