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
package org.conqat.engine.html_presentation.chart;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeSet;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.node.DisplayList;
import org.conqat.engine.commons.statistics.DateValueSeries;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.html_presentation.chart.annotation.AnnotationList;
import org.conqat.engine.html_presentation.chart.annotation.EventAnnotationBase;
import org.conqat.engine.html_presentation.image.IImageDescriptor;
import org.conqat.engine.html_presentation.image.ITooltipDescriptor;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.date.DateUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.Timeline;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.DateRange;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.xy.XYDataset;

/**
 * Base class for series creators. Series creators create line charts from
 * {@link org.conqat.engine.commons.statistics.DateValueSeries}-objects.
 * <p>
 * JFreeChart line charts can work on different time resolutions
 * (millisecond,...,year). This class allows specification of desired
 * resolution. If the value series to display contains more than one value for
 * the chosen time period the last value within the period overwrites the other
 * values.
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 41871 $
 * @ConQAT.Rating GREEN Hash: 1B2CBD9762A7D0F0A70FA16F540BFE82
 */
public abstract class SeriesCreatorBase extends ChartCreatorBase {

	/** Processor comment valid for all sub classes. */
	protected static final String SERIES_LAYOUTER_COMMENT = "JFreeChart line charts "
			+ "can work on different time resolutions  (millisecond,...,year). "
			+ "This class allows specification of desired  resolution. If the "
			+ "value series to display contains more than one value for the "
			+ "chosen time period the last value within the period overwrites "
			+ "the other  values.";

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "domain", attribute = "resolution", optional = true, description = ""
			+ "Set time resolution of the domain. Default resolution is day.")
	public ETimeResolution resolution = ETimeResolution.DAY;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "scale", attribute = "log", optional = true, description = ""
			+ "This parameter allows to scale the date (domain) axis logarithmically to obtain higher resolution "
			+ "for more recent dates. Default is to not scale logarithmically.")
	public boolean useLogDateAxis = false;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "show-axes", attribute = "value", optional = true, description = ""
			+ "Whether the axes should be shown (default is true).")
	public boolean axesVisible = true;

	/** List of {@link AnnotationList}s to be displayed */
	private final List<AnnotationList> annotationLists = new ArrayList<AnnotationList>();

	/** List of visible annotations */
	private List<EventAnnotationBase> visibleAnnotations;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "chart-title", attribute = "value", description = "Title of the chart "
			+ "(no title is used if not set", optional = true)
	public String chartTitle;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "annotations", description = "Chart annotations", minOccurrences = 0, maxOccurrences = -1)
	public void setAnnotations(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) AnnotationList annotationList) {
		this.annotationLists.add(annotationList);
	}

	/** {@inheritDoc} */
	@Override
	protected void setUp() {
		visibleAnnotations = getVisibleAnnotations();
	}

	/** Computes the list of visible event annotations */
	private List<EventAnnotationBase> getVisibleAnnotations() {
		DateRange visibleRange = getDateRange();
		List<EventAnnotationBase> visibleAnnotations = new ArrayList<EventAnnotationBase>();
		for (AnnotationList annotations : annotationLists) {
			visibleAnnotations.addAll(annotations.projectTo(visibleRange));
		}
		return visibleAnnotations;
	}

	/**
	 * Create a time series.
	 * 
	 * @param description
	 *            Description for the series.
	 * @param series
	 *            values of the series.
	 */
	protected TimeSeries createTimeSeries(String description,
			DateValueSeries series) {
		TimeSeries timeSeries = new TimeSeries(description);
		Map<Date, Double> values = series.getValues();

		for (Date date : CollectionUtils.sort(values.keySet())) {
			RegularTimePeriod period = resolution.createTimePeriod(date);
			double value = values.get(date);
			timeSeries.addOrUpdate(period, value);
		}

		return timeSeries;
	}

	/** Get time resolution. */
	protected ETimeResolution getTimeResolution() {
		return resolution;
	}

	/**
	 * Basic setup of a chart. This creates a time series chart and performs
	 * {@link #setupXYPlot(XYPlot, boolean)}.
	 * 
	 * @param dataset
	 *            dataset for the chart
	 * @param axisLabel
	 *            range axis label
	 * @param includeZero
	 *            should zero be included in range
	 */
	protected JFreeChart setupChart(XYDataset dataset, String axisLabel,
			boolean includeZero) {
		JFreeChart chart = ChartFactory.createTimeSeriesChart(chartTitle, // title
				null, // x-axis label
				axisLabel, // y-axis label
				dataset, // data
				drawLegend, // create legend?
				true, // generate tooltips?
				false // generate URLs?
				);

		XYPlot plot = chart.getXYPlot();

		// the following settings are specific to line diagrams and thus not set
		// in setupXYPlot().
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot
				.getRenderer();
		double size = 4.0;
		renderer.setSeriesShape(0, new Ellipse2D.Double(-(size / 2),
				-(size / 2), size, size));
		renderer.setBaseShapesVisible(true);
		renderer.setBaseShapesFilled(true);

		setupXYPlot(plot, includeZero);
		return chart;
	}

	/**
	 * Basic setup of an XY plot. This sets up details for the appearance of the
	 * plot and affects the layout of the axes.
	 * 
	 * @param includeZero
	 *            should zero be included in range
	 */
	protected void setupXYPlot(XYPlot plot, boolean includeZero) {
		DateAxis domainAxis = (DateAxis) plot.getDomainAxis();
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();

		domainAxis.setVisible(axesVisible);
		rangeAxis.setVisible(axesVisible);

		if (axesVisible) {
			configureDomainAxis(domainAxis);
			configureRangeAxis(rangeAxis, includeZero);
		}

		// draw annotations
		for (EventAnnotationBase annotation : visibleAnnotations) {
			annotation.createMarker(plot);
		}
	}

	/** Configures the domain axis. */
	private void configureDomainAxis(DateAxis domainAxis) {
		domainAxis.setDateFormatOverride(new SimpleDateFormat(resolution
				.getDomainFormat()));

		if (useLogDateAxis) {
			domainAxis.setTimeline(new LogTimeLine());
			domainAxis.setTickUnit(new FixedDateTickUnit());
			domainAxis.setLowerMargin(0.0);
			domainAxis.setUpperMargin(0.0);
		}
	}

	/** Configures the range axis. */
	private void configureRangeAxis(NumberAxis rangeAxis, boolean includeZero) {
		rangeAxis.setAutoRangeIncludesZero(includeZero);

		// enforce integer format unless fractional values can appear.
		if (!containsFractionalEntries()) {
			rangeAxis
					.setNumberFormatOverride(NumberFormat.getIntegerInstance());
		} else if (allIn01()) {
			rangeAxis
					.setNumberFormatOverride(NumberFormat.getPercentInstance());
		}
	}

	/** Returns whether all data points are between 0 and 1. */
	private boolean allIn01() {
		for (DateValueSeries series : getSeries()) {
			for (double value : series.getValues().values()) {
				if (value < 0 || value > 1) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Returns whether one of the data points in {@link #getSeries()} is
	 * fractional.
	 */
	private boolean containsFractionalEntries() {
		for (DateValueSeries series : getSeries()) {
			for (double value : series.getValues().values()) {
				if (isFractional(value)) {
					return true;
				}
			}
		}
		return false;
	}

	/** Returns whether the number is fractional. */
	private static boolean isFractional(double value) {
		return Math.abs(value - Math.round(value)) > 1e-9;
	}

	/** Computes the date range of the values of the chart */
	private DateRange getDateRange() {
		ArrayList<DateValueSeries> seriesList = getSeries();

		if (seriesList.isEmpty()) {
			return new DateRange(DateUtils.getNow(), DateUtils.getNow());
		}

		Date lower = seriesList.get(0).getEarliestDate();
		Date upper = seriesList.get(0).getLatestDate();

		for (DateValueSeries series : seriesList) {
			lower = DateUtils.min(lower, series.getEarliestDate());
			upper = DateUtils.min(upper, series.getLatestDate());
		}

		return new DateRange(lower, upper);
	}

	/** {@inheritDoc} */
	@Override
	protected IImageDescriptor createImageDescriptor(JFreeChart chart) {
		return new ImageDescriptor(chart);
	}

	/** Image descriptor class. */
	private class ImageDescriptor extends ChartImageDescriptor {

		/** Constructor. */
		public ImageDescriptor(JFreeChart chart) {
			super(chart, ChartCreatorBase.DEFAULT_PREFERRED_SIZE);
		}

		/** {@inheritDoc} */
		@Override
		public ITooltipDescriptor<Object> getTooltipDescriptor(int width,
				int height) {
			if (visibleAnnotations.isEmpty()) {
				return null;
			}
			return new TooltipDescriptor(getDescriptions(visibleAnnotations),
					width, height);
		}

	}

	/** Template method that deriving classes override to return the data series */
	protected abstract ArrayList<DateValueSeries> getSeries();

	/** This class implements the logarithmic scaling of the axis. */
	private static class LogTimeLine implements Timeline {

		/**
		 * Scale factor applied prior to integer conversation to preserve some
		 * of the double precision. Needed as log scaled value often only differ
		 * in fractional values.
		 */
		private static final double SPREAD = 1000000.;

		/** The reference time with respect to which the scaling occurs. */
		private final long referenceTime = DateUtils.getNow().getTime();

		/** {@inheritDoc} */
		@Override
		public boolean containsDomainRange(long fromMillisecond,
				long toMillisecond) {
			return true;
		}

		/** {@inheritDoc} */
		@Override
		public boolean containsDomainRange(Date fromDate, Date toDate) {
			return true;
		}

		/** {@inheritDoc} */
		@Override
		public boolean containsDomainValue(long millisecond) {
			return true;
		}

		/** {@inheritDoc} */
		@Override
		public boolean containsDomainValue(Date date) {
			return true;
		}

		/** {@inheritDoc} */
		@Override
		public long toMillisecond(long timelineValue) {
			return referenceTime
					+ (long) (1 - Math.exp(timelineValue / -SPREAD));
		}

		/** {@inheritDoc} */
		@Override
		public long toTimelineValue(long millisecond) {
			if (millisecond > referenceTime) {
				millisecond = referenceTime;
			}
			return (long) (-SPREAD * Math.log(referenceTime - millisecond + 1));
		}

		/** {@inheritDoc} */
		@Override
		public long toTimelineValue(Date date) {
			return toTimelineValue(date.getTime());
		}

	}

	/**
	 * Tick unit which ensures only selected dates are shown. This class marks
	 * ticks for today, two days ago, onw week ago, and one month ago.
	 */
	private final class FixedDateTickUnit extends DateTickUnit {

		/** Serial version UID. */
		private static final long serialVersionUID = 1;

		/** The dates used for ticks. */
		private final TreeSet<Date> tickDates = new TreeSet<Date>();

		/** Constructor. */
		private FixedDateTickUnit() {
			super(DateTickUnitType.DAY, 1);

			Calendar c = Calendar.getInstance();
			Date today = DateUtils.getNow();

			c.setTime(today);
			tickDates.add(c.getTime());

			c.setTime(today);
			c.add(Calendar.DAY_OF_MONTH, -2);
			tickDates.add(c.getTime());

			c.setTime(today);
			c.add(Calendar.DAY_OF_MONTH, -7);
			tickDates.add(c.getTime());

			c.setTime(today);
			c.add(Calendar.MONTH, -1);
			tickDates.add(c.getTime());
		}

		/** {@inheritDoc} */
		@Override
		public Date addToDate(Date base, TimeZone zone) {
			Date result = tickDates.ceiling(new Date(base.getTime() + 1));
			if (result != null) {
				return result;
			}
			return super.addToDate(base, zone);
		}
	}

	/** Compute list of descriptions */
	private static DisplayList getDescriptions(
			List<EventAnnotationBase> annotations) {
		DisplayList descriptions = new DisplayList();
		for (EventAnnotationBase annotation : annotations) {
			descriptions.addKey(annotation.toString(), null);
		}
		return descriptions;
	}

	/** Tool tip descriptor is used to show annotations. */
	private static class TooltipDescriptor implements
			ITooltipDescriptor<Object> {

		/** The width. */
		private final int width;

		/** The height. */
		private final int height;

		/** Annotation descriptions. */
		private final DisplayList descriptions;

		/** Constructor. */
		public TooltipDescriptor(DisplayList descriptions, int width, int height) {
			this.descriptions = descriptions;
			this.width = width;
			this.height = height;
		}

		/** {@inheritDoc} */
		@Override
		public Rectangle2D obtainBounds(Object node) {
			return new Rectangle2D.Double(0, 0, width, height);
		}

		/** {@inheritDoc} */
		@Override
		public String obtainId(Object node) {
			return "Annotations";
		}

		/** {@inheritDoc} */
		@Override
		public Object obtainValue(Object node, String key) {
			return StringUtils.EMPTY_STRING;
		}

		/** {@inheritDoc} */
		@Override
		public Object getRoot() {
			return "dummy";
		}

		/** {@inheritDoc} */
		@Override
		public DisplayList getDisplayList() {
			return descriptions;
		}

		/** {@inheritDoc} */
		@Override
		public boolean hasChildren(Object node) {
			return false;
		}

		/** {@inheritDoc} */
		@Override
		public boolean isTooltipsForInnerNodes() {
			return false;
		}

		/** {@inheritDoc} */
		@Override
		public List<Object> obtainChildren(Object node) {
			return null;
		}
	}
}