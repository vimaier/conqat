
package cern.lhc.omc.conqat.python;

import org.conqat.engine.commons.node.IConQATNode;

/**
 * @author vimaier
 */
public class Utils {

	@SuppressWarnings("javadoc")
	public static boolean conQatNodeIsPyModule(IConQATNode input) {
		return input.getName().endsWith(".py");
	}
	
}
