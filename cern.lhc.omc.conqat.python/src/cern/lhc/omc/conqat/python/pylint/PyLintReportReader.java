
package cern.lhc.omc.conqat.python.pylint;

import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.ELogLevel;
import org.conqat.engine.resource.base.ReportReaderBase;
import org.conqat.engine.resource.text.ITextElement;

/**
 * 
 * @author vimaier
 */
@SuppressWarnings("javadoc")
@AConQATProcessor(description = "Reads a FindBugs report and attaches the findings "
		+ "to the provided resource tree. "
		+ ReportReaderBase.DOC
		+ " A finding type is e.g. WMI_WRONG_MAP_ITERATOR. "
		+ "See http://findbugs.sourceforge.net/bugDescriptions.html for"
		+ "details.")
public class PyLintReportReader extends ReportReaderBase {
	
	
	private static final String START_KEY = "::MSG::";
	private static final String END_KEY = "::KEY::";
	
	/**
	 * MESSAGE_TEMPLATE will be passed to PyLint as argument. This argument describes the ouput of the warnings.
	 * If the template style will be changed, the parsing has to be adapted. See http://docs.pylint.org/output.html
	 * 
	 * Since several messages spread over multiple lines we insert the keys START_KEY and END_KEY to emphasise that a 
	 * new message will start. 
	 * 
	 * Hint: Avoid spaces in the template string. python_runner.py(or the Python interpreter) will remove them.
	 */
	static final String MESSAGE_TEMPLATE = "--msg-template=\""+
							START_KEY+"{abspath}::{msg_id}::{line},{column}::{obj}::{msg}"+END_KEY+"\"";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Key for findings", type = "org.conqat.engine.commons.findings.FindingsList")
	public static final String PY_LINT = "PyLint";


	/** {@inheritDoc} */
	@Override
	protected ELogLevel getDefaultLogLevel() {
		return ELogLevel.WARN;
	}

	/** {@inheritDoc} */
	@Override
	protected String obtainRuleDescription(String ruleId)
			throws ConQATException {
		return PyLintMessageManager.getInstance().getLongDescription(ruleId);
	}

	/** {@inheritDoc} */
	@Override
	protected String obtainDetailedDescription(String ruleId)
			throws ConQATException {
		return PyLintMessageManager.getInstance().getDetailedDescription(
				ruleId);
	}

	/** {@inheritDoc} */
	@Override
	protected String getFindingGroupName(String ruleId, String ruleDescription) {
		return ruleDescription;
	}

	/**
	 * Loads a single findbugs report.
	 */
	@Override
	protected void loadReport(ITextElement report) throws ConQATException {
		new BugCollectionReader(report).load();
	}

	/** Class used for reading the output of a PyLint execution. */
	private final class BugCollectionReader {
		
		private int currTextPointer = 0;
		private String pylintOutput;

		/** Constructor. */
		private BugCollectionReader(ITextElement report) throws ConQATException {
			pylintOutput = report.getTextContent();
		}



		/** Reads the report and loads its contents into the findings report. 
		 * @throws ConQATException */
		
		public void load() throws ConQATException {
			
			String messageContent = getNextMessageContent();
			while( ! "".equals(messageContent) ) {
				parseMsgContentAndCreateFindings(messageContent);
				messageContent = getNextMessageContent();
			}
		}



		/**
		 * Returns x from String START_KEY+x+END_KEY.
		 * @return The message string between START_KEY and END_KEY if available else an empty String
		 */
		private String getNextMessageContent() {
			int startMsgIndex = pylintOutput.indexOf(START_KEY, currTextPointer);
			if( -1 == startMsgIndex)
				return "";
			startMsgIndex += START_KEY.length(); // We do not want to include the keys
			currTextPointer += START_KEY.length();
			
			int endMsgIndex = pylintOutput.indexOf(END_KEY, currTextPointer);
			if( -1 == endMsgIndex) {
				getLogger().warn( String.format(
						"Error in parsing PyLint-output. "+
						"Found start key '%s' at index %d but no end key '%s' in the subsequent string.",
						START_KEY, currTextPointer-START_KEY.length(), END_KEY)
						 		);
			}
			
			currTextPointer += END_KEY.length(); // Exclude END_KEY from next search
			
			return pylintOutput.substring(startMsgIndex, endMsgIndex);
		}		



		/**
		 * @param messageContent should be in the style of MESSAGE_TEMPLATE without START- and END_KEY
		 * @throws ConQATException 
		 */
		private void parseMsgContentAndCreateFindings(String messageContent) throws ConQATException {
			// Currently we have this format: {path}::{msg_id}::{line},{column}:: {obj}:: {msg}
			String[] messageParts = messageContent.split("::");
			String absPath = messageParts[0];
			String msgType = messageParts[1];
			int startLine = Integer.parseInt(messageParts[2]);
			// String columnNumber = messageParts[3];
			String function_name = messageParts[4];
			String msg = messageParts[5];
			
			createLineRegionFinding(msgType, getMessage(function_name, msg), absPath, startLine, startLine );
			
		}


		private String getMessage(String function_name, String msg) {
			if( "".equals(function_name) )
				return msg;
			return String.format("%s:%s", function_name, msg);
		}

	}
	
}
