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
package org.conqat.engine.text;

import org.conqat.engine.core.bundle.BundleContextBase;
import org.conqat.engine.core.bundle.BundleInfo;

/**
 * Bundle context class. T
 * 
 * @author Florian Deissenboeck
 * @author $Author: heinemann $
 * @version $Rev: 45857 $
 * @ConQAT.Rating GREEN Hash: DF547F3BCC9E280DB7FB9F252B319F7C
 */
public class BundleContext extends BundleContextBase {

	/** Singleton instance. */
	private static BundleContext instance;

	/** Create bundle context. This is called by ConQAT core. */
	public BundleContext(BundleInfo bundleInfo) {
		super(bundleInfo);
		instance = this;
	}

	/** Get single instance. */
	public static BundleContext getInstance() {
		return instance;
	}
}