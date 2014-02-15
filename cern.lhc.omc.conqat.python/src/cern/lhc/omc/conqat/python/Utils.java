
package cern.lhc.omc.conqat.python;

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.commons.node.IConQATNode;

/**
 * @author vimaier
 */
@SuppressWarnings("javadoc")
public class Utils {

	public static boolean conQatNodeIsPyModule(IConQATNode input) {
		return input.getName().endsWith(".py");
	}
	
	public static List<String> getClonedSublistOf(List<String> list, int startIndexInclusive, int endIndexExclusive) {
		List<String> subList = new ArrayList<String>();
		for(int i=startIndexInclusive; i < endIndexExclusive ;++i)
			subList.add(list.get(i));
		return subList;
	}
	
}
