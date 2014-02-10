

package cern.lhc.omc.conqat.python.pylint;

import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.ITextResource;

/**
 * 
 * @author $Author: $
 * @version $Rev: $
 * @ConQAT.Rating RED Hash:
 */
@AConQATProcessor(description = "Reads a PyLint report and attaches the found errors and other warnings to the module "
		+ "in the provided resource tree.")
@SuppressWarnings("javadoc")
public class PyLintReportMessagesExtractor extends
		ConQATPipelineProcessorBase<ITextResource> {
	
	@AConQATKey(description="Key for storing errors from PyLint", type="java.lang.Integer")
	public static final String PYLINT_ERROR = "PyLintError";
	@AConQATKey(description="Key for storing warnings from PyLint", type="java.lang.Integer")
	public static final String PYLINT_WARNING = "PyLintWarning";
	@AConQATKey(description="Key for storing refactor issues from PyLint", type="java.lang.Integer")
	public static final String PYLINT_REFACTOR = "PyLintRefactor";
	@AConQATKey(description="Key for storing convention issues from PyLint", type="java.lang.Integer")
	public static final String PYLINT_CONVENTION = "PyLintConvention";
	
	
	
	/** {@inheritDoc} */
	@Override
	protected void processInput(ITextResource input) throws ConQATException {
		printAllNamesOf(input);		
	}



	private void printAllNamesOf(ITextResource input) {
		System.out.println(input.getName());
		if( ! input.hasChildren())
			return;
		for(ITextResource children : input.getChildren()) {
			printAllNamesOf(children);
		}
		
	}

}
