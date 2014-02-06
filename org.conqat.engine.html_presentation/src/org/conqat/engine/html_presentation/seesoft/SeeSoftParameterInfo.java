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
package org.conqat.engine.html_presentation.seesoft;

import java.awt.Color;
import java.awt.Dimension;

import org.conqat.engine.resource.findings.FindingsAnnotatorBase;

/**
 * Parameters that determine appearance of a SeeSoft visualization.
 * 
 * @author $Author: juergens $
 * @version $Rev: 44148 $
 * @ConQAT.Rating GREEN Hash: AD2FD9AA999204EF50D73FC04627DD53
 */
/* package */class SeeSoftParameterInfo {

	/** @see SeeSoftImageCreator#setDimensions(int, int, int, int, int) */
	private int width;

	/** @see SeeSoftImageCreator#setDimensions(int, int, int, int, int) */
	private int height;

	/** @see SeeSoftImageCreator#setDimensions(int, int, int, int, int) */
	private int columnWidth;

	/** @see SeeSoftImageCreator#setDimensions(int, int, int, int, int) */
	private Dimension padding;

	/** @see SeeSoftImageCreator#setFinding(String, String) */
	private String findingsKey = FindingsAnnotatorBase.KEY;

	/** @see SeeSoftImageCreator#setFinding(String, String) */
	private String suppressFindingsKey = SeeSoftImageCreator.SUPPRESS_FINDINGS_KEY;

	/** @see SeeSoftImageCreator#setCompressionFactor(int) */
	private int compressionFactor = 1;

	/** @see SeeSoftImageCreator#setBackgroundColor(Color) */
	private Color backgroundColor = Color.WHITE;

	/** @see SeeSoftImageCreator#setEnableSyntaxHighlighting(boolean) */
	private boolean syntaxHighlightingEnabled = true;

	/** @see SeeSoftImageCreator#setFindingsMinHeight(int) */
	private int findingsMinHeight = 3;

	/** @see SeeSoftImageCreator#setFindingsOpaque(boolean) */
	private boolean findingsOpaque = false;

	/** See this.{@link #width} */
	public int getWidth() {
		return width;
	}

	/** See this.{@link #width} */
	public void setWidth(int width) {
		this.width = width;
	}

	/** See this.{@link #height} */
	public int getHeight() {
		return height;
	}

	/** See this.{@link #height} */
	public void setHeight(int height) {
		this.height = height;
	}

	/** See this.{@link #columnWidth} */
	public int getColumnWidth() {
		return columnWidth;
	}

	/** See this.{@link #columnWidth} */
	public void setColumnWidth(int columnWidth) {
		this.columnWidth = columnWidth;
	}

	/** See this.{@link #padding} */
	public Dimension getPadding() {
		return padding;
	}

	/** See this.{@link #padding} */
	public void setPadding(Dimension padding) {
		this.padding = padding;
	}

	/** See this.{@link #findingsKey} */
	public String getFindingsKey() {
		return findingsKey;
	}

	/** See this.{@link #findingsKey} */
	public String getSuppressFindingsKey() {
		return suppressFindingsKey;
	}

	/** See this.{@link #compressionFactor} */
	public int getCompressionFactor() {
		return compressionFactor;
	}

	/** See this.{@link #backgroundColor} */
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	/** See this.{@link #syntaxHighlightingEnabled} */
	public boolean isSyntaxHighlightingEnabled() {
		return syntaxHighlightingEnabled;
	}

	/** See this.{@link #findingsMinHeight} */
	public int getFindingsMinHeight() {
		return findingsMinHeight;
	}

	/** See this.{@link #isFindingsOpaque()} */
	public boolean isFindingsOpaque() {
		return findingsOpaque;
	}

	/** See this.{@link #findingsKey} */
	public void setFindingsKey(String findingsKey) {
		this.findingsKey = findingsKey;
	}

	/** See this.{@link #suppressFindingsKey} */
	public void setSuppressFindingsKey(String suppressFindingsKey) {
		this.suppressFindingsKey = suppressFindingsKey;
	}

	/** See this.{@link #compressionFactor} */
	public void setCompressionFactor(int compressionFactor) {
		this.compressionFactor = compressionFactor;
	}

	/** See this.{@link #backgroundColor} */
	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	/** See this.{@link #syntaxHighlightingEnabled} */
	public void setSyntaxHighlightingEnabled(boolean syntaxHighlightingEnabled) {
		this.syntaxHighlightingEnabled = syntaxHighlightingEnabled;
	}

	/** See this.{@link #findingsMinHeight} */
	public void setFindingsMinHeight(int findingsMinHeight) {
		this.findingsMinHeight = findingsMinHeight;
	}

	/** See this.{@link #findingsOpaque} */
	public void setFindingsOpaque(boolean findingsOpaque) {
		this.findingsOpaque = findingsOpaque;
	}

}
