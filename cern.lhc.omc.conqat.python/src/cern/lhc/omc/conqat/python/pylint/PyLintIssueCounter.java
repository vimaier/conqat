

package cern.lhc.omc.conqat.python.pylint;

import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.Map;

import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.findings.FindingCategory;
import org.conqat.engine.commons.findings.FindingGroup;
import org.conqat.engine.commons.findings.FindingReport;
import org.conqat.engine.commons.findings.location.ElementLocation;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.ITextResource;

import cern.lhc.omc.conqat.python.Utils;

/**
 * 
 * @author $Author: $
 * @version $Rev: $
 * @ConQAT.Rating RED Hash:
 */
@AConQATProcessor(description = "Reads a PyLint report and attaches the found errors and other warnings to the module "
		+ "in the provided resource tree.")
@SuppressWarnings("javadoc")
public class PyLintIssueCounter extends
		ConQATPipelineProcessorBase<ITextResource> {
	
	@AConQATKey(description="Key for storing the number of all issues except of errors from PyLint", type="java.lang.Integer")
	public static final String PYLINT_WARNINGS = "PyLintWarnings";
	
	@AConQATKey(description="Key for storing the number of all errors from PyLint", type="java.lang.Integer")
	public static final String PYLINT_ERRORS = "PyLintErrors";
	
	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "project", attribute = "name", description = "The logical name of the project containing the resources. This is used to build the uniform names.")
	public String projectName;
	
	private ITextResource report = null;
	
	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "pyLintReport", minOccurrences = 1, maxOccurrences=1, description = "Adds a category to include when loading the report. "
			+ "If no categories are specified, all categories will be allowed.")
	public void addPyLintReport(
			@AConQATAttribute(name = "report", description = "The PyLint report(output of std.out)")
			ITextResource report) {
		this.report = report;
	}
	
	
	private PyLintIssueExtractor issueCounter;
	/** {@inheritDoc} 
	 * @throws ConQATException */
	@Override
	protected void processInput(ITextResource input) throws ConQATException {
		FindingReport findingReport = NodeUtils.getFindingReport(this.report);
		issueCounter = new PyLintIssueExtractor(findingReport);
		insertIssueNumbersIntoInputTree(input);
		// To display the values in the HTML table we need to add out key to the DisplayList
		NodeUtils.addToDisplayList(input, PYLINT_WARNINGS);
		NodeUtils.addToDisplayList(input, PYLINT_ERRORS);
	}


	private void insertIssueNumbersIntoInputTree(ITextResource input) {
		if(Utils.conQatNodeIsPyModule(input))
			insertIssueNumbersIntoModule(input);
		if( ! input.hasChildren())
			return;
		for(ITextResource children : input.getChildren()) {
			insertIssueNumbersIntoInputTree(children);
		}
	}


	private void insertIssueNumbersIntoModule(ITextResource input) {
		assert Utils.conQatNodeIsPyModule(input) : "ConQATNode is not a Python module: " + input.getName();
		String relativeModulePath = createPathFromParentNodesUntilProjectName(input);
		try{
			int pylintWarnings = issueCounter.getNumberOfPyLintWarningsForRelativePyModule(relativeModulePath);
			input.setValue(PYLINT_WARNINGS, new Integer(pylintWarnings));
			int pylintErrors = issueCounter.getNumberOfPyLintErrorsForRelativePyModule(relativeModulePath);
			input.setValue(PYLINT_ERRORS, new Integer(pylintErrors));
		}catch(ConQATException e){
			getLogger().warn(e.getMessage() + "\nIConQATNode will be removed.");
			input.remove();
		}
	}
	
	
	private static final String EMPTY_STRING = "";
	private static final String OS_SEPARATOR = FileSystems.getDefault().getSeparator();

	/**
	 * @param input
	 * @return
	 */
	private String createPathFromParentNodesUntilProjectName(IConQATNode input) {
		assert null != input : "input node is null. Should not happen.";
 		String currentName = input.getName();
		if(projectName.equals(currentName))
			return EMPTY_STRING;
		String parentPath = createPathFromParentNodesUntilProjectName(input.getParent());
		if(EMPTY_STRING.equals(parentPath))
			return currentName;
		return parentPath + OS_SEPARATOR + currentName;
	}

	/**
	 * Extracts the found issues and errors from the finding report
	 * 
	 * @author $Author: $
	 * @version $Rev: $
	 * @ConQAT.Rating RED Hash:
	 */
	class PyLintIssueExtractor {
		
		FindingReport findingReport = null;
		/** Stores K:Absolute path to py module and V number of warnings */
		private Map<String, IncrementableInteger> warningsCounterMap = new HashMap<String, IncrementableInteger>();
		/** Stores K:Absolute path to py module and V number of errors */
		private Map<String, IncrementableInteger> errorsCounterMap = new HashMap<String, IncrementableInteger>();
		
		public PyLintIssueExtractor(FindingReport report) {
			findingReport = report;
			countIssuesForEveryModule();
		}

		private void countIssuesForEveryModule() {
			for (FindingCategory category : findingReport.getChildren()) {
				for (FindingGroup group : category.getChildren()) {
					for (Finding finding : group.getChildren()) {
						putLocationIntoMapAndIncrement(finding);
					}
				}
			}
		}
	
	
		private void putLocationIntoMapAndIncrement(Finding finding) {
			ElementLocation location = finding.getLocation();
			String pythonModule = location.getLocation();
			if(isErrorType(finding)){
				incrementMapWith(pythonModule, errorsCounterMap);
			}else {
				incrementMapWith(pythonModule, warningsCounterMap);
			}
			
			
		}
		private boolean isErrorType(Finding finding) {
			String msgType = (String) finding.getValue(PyLintReportReader.ISSUE_TYPE_KEY_IN_FINDINGS); // E.g. W0101, E0011 ...
			return 'E' == msgType.charAt(0);
		}
		private void incrementMapWith(String pythonModule, Map<String, IncrementableInteger> counterMap) {
			if(counterMap.containsKey(pythonModule)) {
				counterMap.get(pythonModule).incrementByOne();
			}else {
				counterMap.put(pythonModule, new IncrementableInteger(1));
			}
		}


		
		/**
		 * @throws ConQATException if relativeModulePath will not be found
		 */
		public int getNumberOfPyLintWarningsForRelativePyModule(String relativeModulePath) throws ConQATException {
			return getPyLintNumberForFirstMapFrom(relativeModulePath, warningsCounterMap, errorsCounterMap);
		}
		
		/**
		 * @throws ConQATException if relativeModulePath will not be found
		 */
		public int getNumberOfPyLintErrorsForRelativePyModule(String relativeModulePath) throws ConQATException {
			return getPyLintNumberForFirstMapFrom(relativeModulePath, errorsCounterMap, warningsCounterMap);
		}

		/**
		 * Returns the number(warnings or errors) for relativeModulePath in the first Map. The second Map is used to
		 * determine if this module was included in the PyLint analysis. If relativeModulePath will not be found in both
		 * maps then it was not included in PyLint and a ConQATException will be raised.
		 * @throws ConQATException 
		 */
		private int getPyLintNumberForFirstMapFrom(String relativeModulePath, Map<String, IncrementableInteger> firstMap,
				Map<String, IncrementableInteger> secondMap) throws ConQATException {
			String absPath = getAbsPathFromMapFor(relativeModulePath, firstMap);
			if(null == absPath) {
				if(null == getAbsPathFromMapFor(relativeModulePath, secondMap))
					throw new ConQATException("Could not find absolute path for relative py module: " + relativeModulePath);
				return 0;
			}
			return firstMap.get(absPath).getValue();
		}
		private String getAbsPathFromMapFor(String relativeModulePath, Map<String, IncrementableInteger> counterMap) {
			for(String absPath : counterMap.keySet()) {
				if(absPath.endsWith(relativeModulePath))
					return absPath;
			}
			return null;
		}
		
	}

	
	class IncrementableInteger {
		
		private int value;
		
		public IncrementableInteger() {
			this.value = 0;
		}
		
		public void incrementBy(int i) {
			
			value += i;
		}

		public IncrementableInteger(int value) {
			this.value = value;
		}
		
		public void incrementByOne() {
			value++;
		}
		
		public int getValue() {
			return value;
		}
		
		@Override
		public String toString() {
			return Integer.toString(value);
		}
	}



}
