

package cern.lhc.omc.conqat.python;

import org.conqat.engine.core.bundle.BundleContextBase;
import org.conqat.engine.core.bundle.BundleInfo;

/**
 * Bundle context class.
 * 
 * @author vimaier
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