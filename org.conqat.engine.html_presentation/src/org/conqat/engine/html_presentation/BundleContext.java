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

import java.util.HashSet;

import org.apache.log4j.Logger;
import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.core.bundle.BundleContextBase;
import org.conqat.engine.core.bundle.BundleInfo;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.html_presentation.color.ColoredStringList;
import org.conqat.engine.html_presentation.formatters.AssessmentHTMLFormatter;
import org.conqat.engine.html_presentation.formatters.ColoredStringListHTMLFormatter;
import org.conqat.engine.html_presentation.formatters.CounterSetHTMLFormatter;
import org.conqat.engine.html_presentation.formatters.FindingHTMLFormatter;
import org.conqat.engine.html_presentation.formatters.HashSetHTMLFormatter;
import org.conqat.engine.html_presentation.formatters.HashedListMapHTMLFormatter;
import org.conqat.engine.html_presentation.formatters.IterableHTMLFormatter;
import org.conqat.engine.html_presentation.formatters.LinkHTMLFormatter;
import org.conqat.engine.html_presentation.formatters.NumberHTMLFormatter;
import org.conqat.engine.html_presentation.formatters.ObjectHTMLFormatter;
import org.conqat.engine.html_presentation.formatters.StringHTMLFormatter;
import org.conqat.engine.html_presentation.javascript.JavaScriptManager;
import org.conqat.engine.html_presentation.javascript.JavaScriptModuleBase;
import org.conqat.engine.html_presentation.javascript.base.BaseJSModule;
import org.conqat.engine.html_presentation.javascript.config.ConfigJSModule;
import org.conqat.engine.html_presentation.javascript.third_party.ClosureLibraryModule;
import org.conqat.engine.html_presentation.javascript.third_party.FlotModule;
import org.conqat.engine.html_presentation.javascript.third_party.JQueryModule;
import org.conqat.engine.html_presentation.javascript.third_party.RaphaelModule;
import org.conqat.engine.html_presentation.util.HTMLLink;
import org.conqat.lib.commons.assessment.Assessment;
import org.conqat.lib.commons.collections.CounterSet;
import org.conqat.lib.commons.collections.HashedListMap;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Bundle context class. This is a singleton that provides access to the
 * {@link HTMLPresentationManager}.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46960 $
 * @ConQAT.Rating GREEN Hash: 4BF03AB8C622DAE922C1FE5AE0001E48
 */
public class BundleContext extends BundleContextBase {

	/** Logger */
	private static final Logger LOGGER = Logger.getLogger(BundleContext.class);

	/** Singleton instance. */
	private static BundleContext instance;

	/** The HTML presentation manager used. */
	private final HTMLPresentationManager htmlPresentationManager;

	/** Create bundle context. This is called by ConQAT core. */
	public BundleContext(BundleInfo bundleInfo) {
		super(bundleInfo);

		// prevent multiple instantiation (e.g. when running multiple times in
		// TestDriver during testing)
		if (instance != null) {
			htmlPresentationManager = null;
			return;
		}

		instance = this;

		htmlPresentationManager = new HTMLPresentationManager();

		initFormatters();
		try {
			getHtmlPresentationManager().addResourcePath(
					getResourceManager().getResourceAsFile(
							StringUtils.EMPTY_STRING));
		} catch (ConQATException e) {
			LOGGER.error(e);
		}

		registerJavaScripts();
	}

	/** Setup the formatters provided by the HTML presentation. */
	private void initFormatters() {
		getHtmlPresentationManager().addHtmlFormatter(Object.class,
				new ObjectHTMLFormatter());
		getHtmlPresentationManager().addHtmlFormatter(String.class,
				new StringHTMLFormatter());
		getHtmlPresentationManager().addHtmlFormatter(Number.class,
				new NumberHTMLFormatter());
		getHtmlPresentationManager().addHtmlFormatter(Iterable.class,
				new IterableHTMLFormatter());
		getHtmlPresentationManager().addHtmlFormatter(Assessment.class,
				new AssessmentHTMLFormatter());
		getHtmlPresentationManager().addHtmlFormatter(HTMLLink.class,
				new LinkHTMLFormatter());

		// we need the formatter as long as some bundle still uses the
		// deprecated HashedListMap
		getHtmlPresentationManager().addHtmlFormatter(HashedListMap.class,
				new HashedListMapHTMLFormatter());
		getHtmlPresentationManager().addHtmlFormatter(HashSet.class,
				new HashSetHTMLFormatter());
		getHtmlPresentationManager().addHtmlFormatter(CounterSet.class,
				new CounterSetHTMLFormatter());
		getHtmlPresentationManager().addHtmlFormatter(Finding.class,
				new FindingHTMLFormatter());
		getHtmlPresentationManager().addHtmlFormatter(ColoredStringList.class,
				new ColoredStringListHTMLFormatter());
	}

	/** Registers the JavaScript descriptors used. */
	private void registerJavaScripts() {

		@SuppressWarnings("unchecked")
		Class<? extends JavaScriptModuleBase>[] moduleClasses = new Class[] {
				// custom (non-third-party)
				BaseJSModule.class, ConfigJSModule.class,

				// third-party
				ClosureLibraryModule.class, RaphaelModule.class,
				JQueryModule.class, FlotModule.class };

		for (Class<? extends JavaScriptModuleBase> moduleClass : moduleClasses) {
			JavaScriptManager.getInstance().registerModule(moduleClass);
		}
	}

	/** Get single instance. */
	public static BundleContext getInstance() {
		return instance;
	}

	/** Returns the HTML presentation manager. */
	public HTMLPresentationManager getHtmlPresentationManager() {
		return htmlPresentationManager;
	}
}