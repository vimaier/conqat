
package cern.lhc.omc.conqat.python;

import java.util.SortedSet;
import java.util.TreeSet;

import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.traversal.ConQATNodePredicateBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;


/**
 * 
 * @author $Author: $
 * @version $Rev: $
 * @ConQAT.Rating RED Hash:
 */
@AConQATProcessor(description = "Puts all IConQATNodes which names end with .py into this NodePredicateBase")
@SuppressWarnings("javadoc")
public class PythonModulePredicate extends ConQATNodePredicateBase {
	
	private IConQATNode rootNode;
	private SortedSet<String> pythonModuleNodeNames = new TreeSet<>(); 

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "ConQATRootNode", minOccurrences = 1, maxOccurrences=1, 
			description = "Adds the root node of the project")
	public void addPyLintReport(
			@AConQATAttribute(name = "root", description = "The the root node of the project")
			IConQATNode rootNode) {
		this.rootNode = rootNode;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isContained(IConQATNode node) {
		return pythonModuleNodeNames.contains(node.getName());
	}

	/** {@inheritDoc} */
	@Override
	public ConQATNodePredicateBase process() {
		saveAllModuleNamesInMap();
		return super.process();
	}

	/**
	 * 
	 */
	private void saveAllModuleNamesInMap() {
		saveModuleNameInMap(rootNode);		
	}

	private void saveModuleNameInMap(IConQATNode node) {
		if(Utils.conQatNodeIsPyModule(node)) {
			pythonModuleNodeNames.add(node.getName());
		}
		if( ! node.hasChildren())
			return;
		for(IConQATNode children : node.getChildren()) {
			saveModuleNameInMap(children);
		}
	}
	
	

}
