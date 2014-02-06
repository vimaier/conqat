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

import java.util.HashMap;

/**
 * Enumeration of fields defined by Bugzilla. The lists of fields was determined
 * querying Bugzilla like
 * <code>https://bugzilla.informatik.tu-muenchen.de/buglist.cgi?query_format=advanced&product=ConQAT&columnlist=all&ctype=rdf</code>
 * .
 * 
 * @author $Author:deissenb $
 * @version $Rev: 46826 $
 * @ConQAT.Rating GREEN Hash: 37E4A231772C0798E6183521527FD22D
 */
public enum EBugzillaField {

	/** Summary of a bug. */
	SUMMARY("short_short_desc", "Summary"),

	/**
	 * Short description. For some strange reason the HTTP functions for
	 * creating or modifying bugs use this tag while the RDF uses the tag
	 * defined by {@link #SUMMARY}.
	 */
	SHORT_DESCRIPTION("short_desc", "Summary"),

	/** Bug id. */
	ID("id", "Id"),

	/** Product name. */
	PRODUCT("product", "Product"),

	/** Component name. */
	COMPONENT("component", "Component"),

	/** Comment. */
	COMMENT("comment", "Comment"),

	/** Bug status. */
	STATUS("bug_status", "Status"),

	/** Bug resolution. */
	RESOLUTION("resolution", "Resolution"),

	/** E-mail address of bug assignee. */
	ASSIGNEE_MAIL("assigned_to", "Assignee Mail"),

	/** Name of bug assignee. */
	ASSIGNEE_NAME("assigned_to_realname", "Assignee Name"),

	/** E-mail address of bug reporter. */
	REPORTER("reporter", "Reporter Mail"),

	/** Name of bug reporter. */
	REPORTER_NAME("reporter_realname", "Reporter Name"),

	/** Keywords */
	KEYWORDS("keywords", "Keywords"),

	/** Milestone */
	MILESTONE("target_milestone", "Milestone"),

	/** Date the bug was opened. */
	OPEN_DATE("opendate", "Creation Date"),

	/** Date the bug was changed. */
	CHANGED_DATE("changeddate", "Modification Date"),

	/** Priority. */
	PRIORITY("priority", "Priority"),

	/** Severity. */
	SEVERITY("bug_severity", "Severity"),

	/** Classification. */
	CLASSIFICATION("classification", "Classification"),

	/** Platform. */
	PLATFORM("rep_platform", "Platform"),

	/** E-Mail address of the QA contact. */
	QA_CONTACT("qa_contact", "QA Contact Mail"),

	/** Name of the QA contact. */
	QA_CONTACT_NAME("qa_contact_realname", "QA Contact Name"),

	/** Operating System */
	OS("op_sys", "OS"),

	/** Whiteboard */
	WHITEBOARD("status_whiteboard", "Whiteboard"),

	/** Vote count */
	VOTES("votes", "Votes"),

	/** Bug alias */
	ALIAS("alias", "Alias"),

	/** Bug alias */
	DEADLINE("deadline", "Deadline"),

	/** Long description length */
	LONG_DESCRIPTION_LENGTH("longdesclength", "Long description length"),

	/** File loc (this is the URL field). */
	FILE_LOC("bug_file_loc", "bug_file_loc"),

	/** Version. */
	VERSION("version", "Version"),

	/** The estimated time. */
	ESTIMATED_TIME("estimated_time", "Estimated time"),

	/** The remaining time. */
	REMAINING_TIME("remaining_time", "Remaining time"),

	/** Percentage completed. */
	PERCENTAGE_COMPLETED("percentage_complete", "Percentage complete"),

	/** The actual time. */
	ACTUAL_TIME("actual_time", "Actual time");

	/** This maps from XML tag to field. */
	private final static HashMap<String, EBugzillaField> fieldMap = new HashMap<String, EBugzillaField>();

	/** Initialize {@link #fieldMap}. */
	static {
		for (EBugzillaField field : values()) {
			fieldMap.put(field.xmlTag, field);
		}
	}

	/** The tag used in Bugzilla RDF format to store the field. */
	public final String xmlTag;

	/** Display name of the field. */
	public final String displayName;

	/**
	 * Create new field.
	 * 
	 * @param xmlTag
	 *            tag used in Bugzilla RDF format to store the field.
	 * @param displayName
	 *            display name of the field.
	 */
	private EBugzillaField(String xmlTag, String displayName) {
		this.xmlTag = xmlTag;
		this.displayName = displayName;
	}

	/**
	 * Get field by XML tag
	 * 
	 * @param xmlTag
	 *            tag used in Bugzilla RDF format to store the field.
	 * @return the field or <code>null</code> if field was not found.
	 */
	public static EBugzillaField getField(String xmlTag) {
		return fieldMap.get(xmlTag);
	}

}