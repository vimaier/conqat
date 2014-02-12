

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
public class PyLintIssuePointExtractor extends
		ConQATPipelineProcessorBase<ITextResource> {
	
	@AConQATKey(description="Key for storing issue points from PyLint(4*num_of_errors + all_other_issues)", type="java.lang.Integer")
	public static final String PYLINT_ISSUE_POINTS = "PyLintIssuePoints";
	
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
	
	
	private PyLintIssueCounter issueCounter;
	/** {@inheritDoc} 
	 * @throws ConQATException */
	@Override
	protected void processInput(ITextResource input) throws ConQATException {
		FindingReport findingReport = NodeUtils.getFindingReport(this.report);
		issueCounter = new PyLintIssueCounter(findingReport);
		insertIssueNumbersIntoInputTree(input);
		// To display the values in the HTML table we need to add out key to the DisplayList
		NodeUtils.addToDisplayList(input, PYLINT_ISSUE_POINTS);
	}


	private void insertIssueNumbersIntoInputTree(IConQATNode input) {
		if(Utils.conQatNodeIsPyModule(input))
			insertIssueNumberIntoModule(input);
		if( ! input.hasChildren())
			return;
		for(IConQATNode children : input.getChildren()) {
			insertIssueNumbersIntoInputTree(children);
		}
	}


	private void insertIssueNumberIntoModule(IConQATNode input) {
		assert Utils.conQatNodeIsPyModule(input) : "ConQATNode is not a Python module: " + input.getName();
		String relativeModulePath = createPathFromParentNodesUntilProjectName(input);
		try{
			int pylintIssues = issueCounter.getNumberOfPyLintIssuesForRelativePyModule(relativeModulePath);
			input.setValue(PYLINT_ISSUE_POINTS, new Integer(pylintIssues));
		}catch(ConQATException e){
			getLogger().warn(e.getMessage());
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


	class PyLintIssueCounter {
		
		FindingReport findingReport = null;
		/** Stores K:Absolute path to py module and V number of issues */
		private Map<String, IncrementableInteger> findingsCounterMap = new HashMap<>();
		
		public PyLintIssueCounter(FindingReport report) {
			findingReport = report;
			countIssuesForEveryModule();
		}

		private void countIssuesForEveryModule() {
			for (FindingCategory category : findingReport.getChildren()) {
				for (FindingGroup group : category.getChildren()) {
					getLogger().info("ID: " + group.getId());
					getLogger().info("Name: " + group.getGroupInfo().getGroupName());
					for (Finding finding : group.getChildren()) {
						putLocationIntoMapAndIncrement(finding);
					}
				}
			}
		}
	
	
		private void putLocationIntoMapAndIncrement(Finding finding) {
			ElementLocation location = finding.getLocation();
			String pythonModule = location.getLocation();
			if(findingsCounterMap.containsKey(pythonModule)) {
				findingsCounterMap.get(pythonModule).incrementByOne();
			}else {
				findingsCounterMap.put(pythonModule, new IncrementableInteger(1));
			}
			if(isErrorType(finding))
				// An error counts for 4 issue points (1 we already incremented)
				findingsCounterMap.get(pythonModule).incrementBy(3); 
			
			
		}
		
		private boolean isErrorType(Finding finding) {
			String msgType = (String) finding.getValue(PyLintReportReader.ISSUE_TYPE_KEY_IN_FINDINGS); // E.g. W0101, E0011 ...
			return 'E' == msgType.charAt(0);
		}

		
		/**
		 * @throws ConQATException if relativeModulePath will not be found
		 */
		public int getNumberOfPyLintIssuesForRelativePyModule(String relativeModulePath) throws ConQATException {
			for(String absPath : findingsCounterMap.keySet()) {
				if(absPath.endsWith(relativeModulePath))
					return findingsCounterMap.get(absPath).getValue();
			}
			throw new ConQATException("Could not find absolute path for relative py module: " + relativeModulePath);
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
