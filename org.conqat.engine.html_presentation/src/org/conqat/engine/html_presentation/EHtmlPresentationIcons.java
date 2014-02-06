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
package org.conqat.engine.html_presentation;

/**
 * This enum lists the default icons that come with the HTML presentation. Each
 * enum constant is related to an actual file by {@link #getIconName()}.
 * 
 * Before using this enum to check whether an icon is used at all, keep in mind
 * that some icons might be used from JavaScript code or (for historical
 * reasons) directly by icon name.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating YELLOW Hash: AE8030AA50163792EFD496DEB329E71D
 */
public enum EHtmlPresentationIcons {

	/** Icon arrow_down.gif */
	ARROW_DOWN,

	/** Icon arrow_none.gif */
	ARROW_NONE,

	/** Icon arrow_up.gif */
	ARROW_UP,

	/** Icon attribute_icon.gif */
	ATTRIBUTE_ICON,

	/** Icon chart.gif */
	CHART,

	/** Icon collapse_all.gif */
	COLLAPSE_ALL,

	/** Icon collapsed.gif */
	COLLAPSED,

	/** Icon config.gif */
	CONFIG,

	/** Icon conqat_logo.gif */
	CONQAT_LOGO,

	/** Icon execution_time.gif */
	EXECUTION_TIME,

	/** Icon expand_all.gif */
	EXPAND_ALL,

	/** Icon expanded.gif */
	EXPANDED,

	/** Icon expand_subtree.gif */
	EXPAND_SUBTREE,

	/** Icon external.gif */
	EXTERNAL,

	/** Icon file.gif */
	FILE,

	/** Icon graph.gif */
	GRAPH,

	/** Icon green.gif */
	GREEN,

	/** Icon group.gif */
	GROUP,

	/** Icon group_open.gif */
	GROUP_OPEN,

	/** Icon info_group.gif */
	INFO_GROUP,

	/** Icon leaf.gif */
	LEAF,

	/** Icon log_debug.gif */
	LOG_DEBUG,

	/** Icon log_error.gif */
	LOG_ERROR,

	/** Icon log_fatal.gif */
	LOG_FATAL,

	/** Icon log_info.gif */
	LOG_INFO,

	/** Icon logs.gif */
	LOGS,

	/** Icon log_warn.gif */
	LOG_WARN,

	/** Icon merged_page.gif */
	MERGED_PAGE,

	/** Icon overview.gif */
	OVERVIEW,

	/** Icon page.gif */
	PAGE,

	/** Icon parameter_icon.gif */
	PARAMETER_ICON,

	/** Icon red.gif */
	RED,

	/** Icon table.gif */
	TABLE,

	/** Icon tree_map.gif */
	TREE_MAP,

	/** Icon tree_table.gif */
	TREE_TABLE,

	/** Plus icon. */
	PLUS,

	/** Minus icon. */
	MINUS,

	/** Icon unknown.gif */
	UNKNOWN,

	/** Icon yellow.gif */
	YELLOW;

	/** Returns the name of the icon. */
	public String getIconName() {
		return name().toLowerCase() + ".gif";
	}

}