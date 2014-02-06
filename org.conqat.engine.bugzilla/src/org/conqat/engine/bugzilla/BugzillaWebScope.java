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
package org.conqat.engine.bugzilla;

import java.io.IOException;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.httpclient.HttpException;
import org.conqat.engine.bugzilla.lib.Bug;
import org.conqat.engine.bugzilla.lib.BugzillaException;
import org.conqat.engine.bugzilla.lib.BugzillaWebClient;
import org.conqat.engine.bugzilla.lib.EBugzillaField;
import org.conqat.engine.bugzilla.lib.EBugzillaStatus;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeConstants;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.node.StringSetNode;
import org.conqat.engine.commons.sorting.NumericIdSorter;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.html_presentation.links.LinkProviderBase;
import org.conqat.lib.commons.collections.InvertingComparator;
import org.conqat.lib.commons.collections.PairList;
import org.conqat.lib.commons.string.StringUtils;

/**
 * This processor reads bug information from a Bugzilla web server.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 46826 $
 * @ConQAT.Rating GREEN Hash: B62FEE99E465E722E871CCABF2EDAA0D
 */
@AConQATProcessor(description = "This processor reads bug information from a "
		+ "Bugzilla web server. Note that Bugzilla servers can often be accessed "
		+ "without credentials in a read-only manner. Hence, credentials are not "
		+ "necessarily required although your Bugzilla server requires them to "
		+ "edit bugs. Depending on the permissions set on the Bugzilla server, "
		+ "this processor may return different results whether you authenticated or not. "
		+ "This is know to work for Bugzilla 3.0.2.")
public class BugzillaWebScope extends ConQATProcessorBase {

	/** Default value for the custom field "key" attribute. */
	private static final String CUSTOM_FIELD_KEY_DEFAULT = "SAME_AS_FIELD";

	/** The URL of the Bugzilla server. */
	private String serverURL;

	/** Products to obtain bugs for. */
	private final Set<String> products = new HashSet<String>();

	/** Component to obtain bugs for. */
	private final Set<String> components = new HashSet<String>();

	/** User name for HTTP authentification. */
	private String httpAuthUsername;

	/** Password for HTTP authentification. */
	private String httpAuthPassword;

	/** User name for Bugzilla server. */
	private String username;

	/** Password for Bugzilla server. */
	private String password;

	/** States of bugs to include. */
	private final EnumSet<EBugzillaStatus> statuses = EnumSet
			.noneOf(EBugzillaStatus.class);

	/** Fields to include. */
	private final EnumSet<EBugzillaField> fields = EnumSet
			.noneOf(EBugzillaField.class);

	/**
	 * Custom fields to include (first is internal value, second is key/display
	 * value)
	 */
	private final PairList<String, String> customFields = new PairList<String, String>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "server", minOccurrences = 1, maxOccurrences = 1, description = "Bugzilla server.")
	public void setServer(
			@AConQATAttribute(name = "url", description = "URL of the server.") String serverURL) {
		this.serverURL = serverURL;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "credentials", minOccurrences = 0, maxOccurrences = 1, description = "Credentials for the Bugzilla server if the Bugzilla server requires them.")
	public void setCredentials(
			@AConQATAttribute(name = "username", description = "User name") String username,
			@AConQATAttribute(name = "password", description = "Password") String password) {
		this.username = username;
		this.password = password;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "http-authentification", minOccurrences = 0, maxOccurrences = 1, description = "Credentials for HTTP basic authentification, i.e. the password asked by the browser.")
	public void setHttpCredentials(
			@AConQATAttribute(name = "username", description = "User name") String username,
			@AConQATAttribute(name = "password", description = "Password") String password) {
		this.httpAuthUsername = username;
		this.httpAuthPassword = password;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "product", description = "Add a bugzilla product to obtain bugs for.")
	public void addProductName(
			@AConQATAttribute(name = "name", description = "Product name.") String productName) {
		products.add(productName);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "component", description = "Add a bugzilla component to obtain bugs for.")
	public void addComponentName(
			@AConQATAttribute(name = "name", description = "Component name.") String componentName) {
		components.add(componentName);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "status", description = "Add bug states to include.")
	public void addStatus(
			@AConQATAttribute(name = "name", description = "Name of the status to include.") EBugzillaStatus status) {
		statuses.add(status);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "field", description = "Add bugzilla field to include.")
	public void addField(
			@AConQATAttribute(name = "name", description = "Name of the field to include.") EBugzillaField field) {
		fields.add(field);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "custom-field", description = "Add custom bugzilla field to include.")
	public void addCustomField(
			@AConQATAttribute(name = "name", description = "Name of the field to include.") String field,
			@AConQATAttribute(name = "key", defaultValue = CUSTOM_FIELD_KEY_DEFAULT, description = "The key used for storing the field's value.") String key) {

		if (CUSTOM_FIELD_KEY_DEFAULT.equals(key)) {
			key = field;
		}
		customFields.add(field, key);
	}

	/** {@inheritDoc} */
	@Override
	public StringSetNode process() throws ConQATException {
		StringSetNode root = new StringSetNode("Bugzilla CRs");
		root.setValue(NodeConstants.HIDE_ROOT, true);
		root.setValue(NodeConstants.COMPARATOR,
				new InvertingComparator<IConQATNode>(
						NumericIdSorter.NumericIdComparator.getInstance()));

		// if no fields were defined explicitly, use all fields
		// we don't have to do this for statuses as BugzillaWebClient.query()
		// returns Bugs with all statuses if an empty set is supplied
		if (fields.isEmpty()) {
			fields.addAll(EnumSet.allOf(EBugzillaField.class));
		}

		for (EBugzillaField field : fields) {
			NodeUtils.addToDisplayList(root, field.displayName);
		}
		NodeUtils.addToDisplayList(root, customFields.extractSecondList());

		Set<Bug> bugs = obtainBugs();
		getLogger().info("Found " + bugs.size() + " bugs.");

		String baseLinkUrl = StringUtils.stripSuffix("/", serverURL) + "/"
				+ "show_bug.cgi?id=";

		for (Bug bug : bugs) {
			StringSetNode bugNode = new StringSetNode(String.valueOf(bug
					.getId()));
			root.addChild(bugNode);
			bugNode.setValue(LinkProviderBase.LINK_KEY,
					baseLinkUrl + bug.getId());
			copyFields(bug, bugNode);
		}

		return root;
	}

	/** Copies the selected fields from a bug to the bug node. */
	private void copyFields(Bug bug, StringSetNode bugNode) {
		for (EBugzillaField field : fields) {
			String value = bug.getValue(field);
			if (value != null) {
				bugNode.setValue(field.displayName, value);
			}
		}

		for (int i = 0; i < customFields.size(); ++i) {
			String value = bug.getCustomFieldValue(customFields.getFirst(i));
			if (value != null) {
				bugNode.setValue(customFields.getSecond(i), value);
			}
		}
	}

	/** Read bugs from the Bugzilla server. */
	protected Set<Bug> obtainBugs() throws ConQATException {
		// this constructor also works with null values for username/password
		BugzillaWebClient client = new BugzillaWebClient(serverURL,
				httpAuthUsername, httpAuthPassword);
		try {
			if (username != null && password != null) {
				client.authenticate(username, password);
			}
			return client.query(products, components, statuses);
		} catch (HttpException e) {
			throw new ConQATException("Error accessing Bugzilla: "
					+ e.getMessage(), e);
		} catch (BugzillaException e) {
			throw new ConQATException("Error accessing Bugzilla: "
					+ e.getMessage(), e);
		} catch (IOException e) {
			throw new ConQATException("Error accessing Bugzilla: "
					+ e.getMessage(), e);
		}
	}
}