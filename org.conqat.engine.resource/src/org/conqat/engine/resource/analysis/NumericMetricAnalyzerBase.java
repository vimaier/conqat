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
package org.conqat.engine.resource.analysis;

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.commons.findings.EFindingKeys;
import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.findings.FindingGroup;
import org.conqat.engine.commons.findings.FindingGroupInfo;
import org.conqat.engine.commons.findings.FindingReport;
import org.conqat.engine.commons.findings.location.ElementLocation;
import org.conqat.engine.commons.findings.util.FindingUtils;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.IResource;
import org.conqat.lib.commons.assessment.ETrafficLightColor;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Base class for processors that calculate a numeric metric value, possibly at
 * multiple locations for each file (e.g. each method, each line, etc.). This
 * base class provides aggregation of this value if there are multiple
 * measurements and an optional parameter that allows the creation of findings
 * for violations of a metric threshold.
 * 
 * 
 * @param <R>
 *            type of the resource
 * 
 * @param <E>
 *            type of the element (in the resource tree)
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46045 $
 * @ConQAT.Rating RED Hash: 8ACF2974E5EF7C636E3A8F906540BB0B
 */
public abstract class NumericMetricAnalyzerBase<R extends IResource, E extends IElement>
		extends ElementAnalyzerBase<R, E> {

	/** The default category used. */
	public static final String DEFAULT_CATEGORY = "Metric Violations";

	/** The name of the property used for storing the value. */
	private static final String VALUE_FINDING_PROPERTY = "Value";

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "finding-category", attribute = "name", optional = true, description = ""
			+ "The name of the finding category to store the findings in. If this is not set, the category "
			+ DEFAULT_CATEGORY + " is used.")
	public String findingCategory = DEFAULT_CATEGORY;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "suppress", attribute = "value", optional = true, description = ""
			+ "If this is true, the actual metric value will not be written. This can be useful if only findings shall be created. Default is false.")
	public boolean suppressMetricValue = false;

	/** The thresholds used for producing findings. */
	private final List<ThresholdInfo> thresholds = new ArrayList<ThresholdInfo>();

	/** Stores, whether any metric value was reported for the current element. */
	private boolean hadValue = false;

	/** The current aggregated value for the element. */
	private double currentAggregate = 0;

	/**
	 * The current element from {@link #analyzeElement(IElement)}. This is
	 * protected for the case that sub classes need to create specific
	 * locations.
	 */
	protected E currentElement;

	/** The finding report. */
	private FindingReport findingReport;

	/**
	 * The group to create findings in. Lazily initialized by
	 * {@link #getFindingGroup()}.
	 */
	private FindingGroup findingGroup;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "findings", description = ""
			+ "If this optional parameter is set, findings are created for each violation of the threshold for the metric calculated by this processor. "
			+ "Multiple thresholds can be used, in which case the first violated one will be used. I.e. the worst ones (typically RED) should come first.")
	public void setFindingThreshold(
			@AConQATAttribute(name = "threshold", description = "The (inclusive) threshold for valid ranges of the metric.") double threshold,
			@AConQATAttribute(name = "key", description = "The key under which the findings are stored.") String key,
			@AConQATAttribute(name = "color", description = "The color for the finding (default: RED).", defaultValue = "RED") ETrafficLightColor color,
			@AConQATAttribute(name = "message", defaultValue = "Violation of %n threshold of %t: %v", description = ""
					+ "The message created. The following special strings are replaced: "
					+ "%n is the name of the metric, %t is the chosen threshold, "
					+ "%v is the actual value of the metric") String message) {
		thresholds.add(new ThresholdInfo(threshold, color, key, message));
	}

	/** {@inheritDoc} */
	@Override
	protected void setUp(R root) throws ConQATException {
		super.setUp(root);
		findingReport = NodeUtils.getFindingReport(root);
	}

	/** {@inheritDoc} */
	@Override
	protected final void analyzeElement(E element) throws ConQATException {
		hadValue = false;
		currentElement = element;
		currentAggregate = 0;

		calculateMetrics(element);

		if (!suppressMetricValue) {
			element.setValue(getKey(), currentAggregate);
		}
	}

	/**
	 * Template method for calculating the actual metric values. These values
	 * must be reported via one of the reportMetricValue callbacks.
	 */
	protected abstract void calculateMetrics(E element) throws ConQATException;

	/** Main method for reporting a metric value. */
	protected final void reportMetricValue(double value,
			ElementLocation location) {
		aggregate(value);

		for (ThresholdInfo threshold : thresholds) {
			if (threshold.isViolation(value)) {
				Finding finding = FindingUtils.createAndAttachFinding(
						getFindingGroup(), threshold.formatMessage(value),
						currentElement, location, threshold.findingsKey);
				finding.setValue(EFindingKeys.ASSESSMENT.toString(),
						threshold.color);
				finding.getProperties().put(VALUE_FINDING_PROPERTY, value);
				break;
			}
		}
	}

	/** Aggregates the metric value. */
	private void aggregate(double value) {
		if (hadValue) {
			if (aggregateMax()) {
				currentAggregate = Math.max(value, currentAggregate);
			} else {
				currentAggregate = Math.min(value, currentAggregate);
			}
		} else {
			hadValue = true;
			currentAggregate = value;
		}
	}

	/** Returns the finding group used. Lazy initialization. */
	private FindingGroup getFindingGroup() {
		if (findingGroup == null) {
			findingGroup = findingReport.getOrCreateCategory(findingCategory)
					.getOrCreateFindingGroup(getMetricName());

			FindingGroupInfo info = new FindingGroupInfo(findingGroup,
					getFindingDescription());
			info.setPropertyDescription(VALUE_FINDING_PROPERTY,
					"The value of the violated metric.");
			findingGroup.setGroupInfo(info);
		}
		return findingGroup;
	}

	// TODO (LH) Don't understand your strategy here. Some subclasses override
	// some don't. I would expect every subclass to provide a proper finding
	// description. Why not force subclasses to override by making this
	// abstract?
	/**
	 * Returns the description to be used for the findings group used for
	 * findings created. Default implementation uses empty string.
	 */
	protected String getFindingDescription() {
		return StringUtils.EMPTY_STRING;
	}

	/** Reports a metric value. The location is the entire file. */
	protected void reportMetricValue(double value) {
		reportMetricValue(value,
				new ElementLocation(currentElement.getLocation(),
						currentElement.getUniformPath()));
	}

	/**
	 * Template method that determines whether aggregation is maximizing (true)
	 * or minimizing (false). This also affects the interpretation of the
	 * threshold, that is used as an upper bound of valid values (true) or a
	 * lower bound (false). The default implementation returns true.
	 */
	protected boolean aggregateMax() {
		return true;
	}

	/**
	 * Template method that returns the name of the metric. The default
	 * implementation returns {@link #getKey()}.
	 */
	protected String getMetricName() {
		return getKey();
	}

	/**
	 * Returns the key at which the maximal (or minimal) value of the metric is
	 * stored.
	 */
	protected abstract String getKey();

	/** {@inheritDoc} */
	@Override
	protected String[] getKeys() {
		List<String> keys = new ArrayList<String>();

		if (!suppressMetricValue) {
			keys.add(getKey());
		}

		for (ThresholdInfo threshold : thresholds) {
			keys.add(threshold.findingsKey);
		}

		return CollectionUtils.toArray(keys, String.class);
	}

	/** Class for holding threshold information. */
	private class ThresholdInfo {

		/** The threshold. */
		private final double threshold;

		/** The color used for the finding. */
		private final ETrafficLightColor color;

		/** The key used for storing the findings. */
		private final String findingsKey;

		/**
		 * The message used for the findings. For special characters supported
		 * see the {@link #formatMessage(double)} method and the description of
		 * the parameters of
		 * {@link NumericMetricAnalyzerBase#setFindingThreshold(double, String, ETrafficLightColor, String)}
		 * .
		 */
		private final String findingsMessage;

		/** Constructor. */
		public ThresholdInfo(double threshold, ETrafficLightColor color,
				String findingsKey, String findingsMessage) {
			this.threshold = threshold;
			this.color = color;
			this.findingsKey = findingsKey;
			this.findingsMessage = findingsMessage;
		}

		/**
		 * Formats the message used in the finding. This can be overridden by
		 * subclasses. This implementation is based on the
		 * {@link #findingsMessage} with template replacements as described in
		 * {@link NumericMetricAnalyzerBase#setFindingThreshold(double, String, ETrafficLightColor, String)}
		 * .
		 */
		protected String formatMessage(double actualValue) {
			String message = findingsMessage;
			message = message.replace("%n", getMetricName());
			message = message.replace("%t", Double.toString(threshold));
			message = message.replace("%v", Double.toString(actualValue));
			return message;
		}

		/** Returns whether the given value violates the threshold. */
		private boolean isViolation(double value) {
			if (aggregateMax()) {
				return value > threshold;
			}
			return value < threshold;
		}
	}
}