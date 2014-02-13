
package cern.lhc.omc.conqat.python;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.analysis.TextMetricAnalyzerBase;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.TextElementUtils;
import org.conqat.lib.commons.string.StringUtils;


/**
 * 
 * @author vimaier
 */
@AConQATProcessor(description = "Calculates the three different functions size of a Python module. All function sizes in "
		+ "the module will be determined. Afterwards they will be divided into good-sized, acceptable-sized and big-sized. "
		+ "The thresholds can be given in the acceptableSizeThreshold and tooBigSizeThreshold. Default is 30/80.")
public class FunctionSizesCounter extends TextMetricAnalyzerBase {
/*
 * textMetricAnalyzerBase is made to calculate only one metric. However, we want three metrics here. So 
 * TOO_BIG_FUNCTIONS_KEY will be the actual metric and the both other we have to add manually.
 * Therefore we insert the Key/Value pairs into the IConQATNodes and add the Keys to the DisplayList by overwriting
 * getKeys() function.
 */

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Number of small-sized functions", type = "java.lang.Number")
	public static final String SMALL_FUNCTIONS_KEY = "SmallFunc";
	
	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Number of small-sized functions", type = "java.lang.Number")
	public static final String ACCEPTABLE_FUNCTIONS_KEY = "AcceptableFunc";
	
	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Number of small-sized functions", type = "java.lang.Number")
	public static final String TOO_BIG_FUNCTIONS_KEY = "TooBigFunc";

	/** {@inheritDoc} */
	@Override
	protected String getKey() {
		// This function is actually not used. It will be called by getKeys, but we override getKeys()
		return "";
	}
	

	/** {@inheritDoc} */
	@Override
	protected String[] getKeys() {
		return new String[]{SMALL_FUNCTIONS_KEY, ACCEPTABLE_FUNCTIONS_KEY, TOO_BIG_FUNCTIONS_KEY};
	}



	/** {@inheritDoc} */
	@Override
	protected void calculateMetrics(ITextElement element)
			throws ConQATException {
		PythonFunctionSizeDeterminer functionSizeDeterminer = new PythonFunctionSizeDeterminer(element);
		element.setValue(SMALL_FUNCTIONS_KEY, functionSizeDeterminer.getNumberOfSmallFunctions());
		element.setValue(ACCEPTABLE_FUNCTIONS_KEY, functionSizeDeterminer.getNumberOfAcceptableFunctions());
		reportMetricValue(functionSizeDeterminer.getNumberOfTooBigFunctions());
	}


	/** {@inheritDoc} */
	@Override
	protected String getFindingDescription() {
		return "<p>Long functions can complicate both locating a specific feature "
				+ "within a file and understanding the consequences of a "
				+ "change. Ideally, a function should only do one thing. "
				+ "Very long functions often indicate that too many features "
				+ "are intermixed in the code.</p> "
				+ "<p>The reduction of function length should not be performed only "
				+ "syntactically (e.g. by folding all lines into a single line or "
				+ "using a preprocessor for splitting the files), but rather by "
				+ "separating the different features or concerns of a function and "
				+ "placing them into separate files, classes or functions.</p> ";
	}
	
	@SuppressWarnings("javadoc")
	class PythonFunctionSizeDeterminer {
		
		private static final int WARNING_THRESHOLD = 30;
		private static final int TOO_BIG_THRESHOLD = 80;
		
		private int smallFunctions = 0;
		private int acceptableFunctions = 0;
		private int tooBigFunctions = 0;
		
		private List<String> allLines;
		
		
		/**
		 * @param element Representing a Python module
		 * @throws ConQATException 
		 */
		public PythonFunctionSizeDeterminer(ITextElement element) throws ConQATException {
			allLines = new ArrayList<>(Arrays.asList(TextElementUtils.getLines(element)));
			trimNewlineSignsAtTheEndOfLines();
			trimUntilFirstLineContainingLiteralDef();
			// For simplicity, we remove all lines without indentation which do not contain the def statement.
			// This makes it easier to count but could deliver wrong results(Multiline comments, multiline 
			// statements connected with '\', blocks on global space..) We accept this errors for simplicity.
			// Well designed modules should not have these errors except of maybe (if __name__=="__main__":) block...
			trimGlobalBlocks();
			deleteAllLinesWithoutIndentationAndLiteralDef();
			
			determineFunctionBlocks();
		}
		


		/**
		 * 
		 */
		private void trimNewlineSignsAtTheEndOfLines() {
			for(int i=0; i < allLines.size() ;++i) {
				String oldLine = allLines.get(i);
				allLines.set(i, oldLine.replace("\n", "").replace("\r", ""));
			}
			
		}

		// TODO:remove
		public PythonFunctionSizeDeterminer(String moduleContent) {
			allLines = new ArrayList<>(Arrays.asList(moduleContent.split("\\n")));
			trimNewlineSignsAtTheEndOfLines();
			trimUntilFirstLineContainingLiteralDef();
			// For simplicity, we remove all lines without indentation which do not contain the def statement.
			// This makes it easier to count but could deliver wrong results(Multiline comments, multiline 
			// statements connected with '\', blocks on global space..) We accept this errors for simplicity.
			// Well designed modules should not have these errors except of maybe (if __name__=="__main__":) block...
			trimGlobalBlocks();
			deleteAllLinesWithoutIndentationAndLiteralDef();

			determineFunctionBlocks();
		}

		private void trimGlobalBlocks() {
			// Removes for, while, if and else blocks from global space
			if(0 == allLines.size())
				return;
			int offset = getLineIndexOfNextGlobalBlockBegin(0);  
			while(-1 != offset) {
				int indexOfNextLineWithoutIndentation = getNextLineWithNoIndentation(offset+1);
				deleteLines(offset, indexOfNextLineWithoutIndentation);
				offset = getLineIndexOfNextGlobalBlockBegin(0); // Since we manipulate the list we start always from beginning
			}
		}



		private int getLineIndexOfNextGlobalBlockBegin(int offset) {
			for(int i=offset; i<allLines.size() ;++i) {
				if(isGlobalBlockBeginLine(allLines.get(i))) {
					return i;
				}
			}
			return -1;
		}
		String[] globalBlockBeginIdentifiers = {"if", "else", "elif", "while", "for", "try", "except"};
		private boolean isGlobalBlockBeginLine(String line) {
			return StringUtils.startsWithOneOf(line, globalBlockBeginIdentifiers);
		}
		
		private int getNextLineWithNoIndentation(int offset) {
			for(int i=offset; i<allLines.size() ;++i) {
				if(isStatementWithNoIndentation(allLines.get(i)))
					return i;
			}
			return allLines.size();
		}
		private boolean isStatementWithNoIndentation(String line) {
			// Should be an reserved word or identifier... all start with a letter or _
 			return line.matches("^[a-zA-Z_].*");
		}

		private void deleteLines(int startIndexIncluding, int endIndexExcluding) {
			for(int i=endIndexExcluding-1; startIndexIncluding <= i ;--i) {
				allLines.remove(i);
			}
		}

		private void trimUntilFirstLineContainingLiteralDef() {
			int indexLineDef = getLineIndexOfNextDefLiteral(0);
			if(-1 == indexLineDef)
				return;
			allLines = allLines.subList(indexLineDef, allLines.size());
		}

		private int getLineIndexOfNextDefLiteral(int offset) {
			
			for(int i=offset; i < allLines.size() ; ++i) {
				if(hasDefLiteral(allLines.get(i))) {
					return i;
				}
			}
			return -1; // No further def in lines
			
		}

		private void deleteAllLinesWithoutIndentationAndLiteralDef() {
			// We iterate backwards since we manipulate the list...
			for(int i=allLines.size()-1; i>=0 ;--i) {
				if(isLineWihtoutIndentationAndLiteralDef(allLines.get(i))){
					allLines.remove(i);
				}
			}			
		}

		private boolean isLineWihtoutIndentationAndLiteralDef(String line) {
			// Empty string does not count
			if ("".equals(line))
				return false;
			return startsNotWithWhitespace(line) && hasNoDefLiteral(line);
		}
		
		private boolean hasNoDefLiteral(String line) {
			return ! hasDefLiteral(line);
		}
		private boolean hasDefLiteral(String line) {
			String regexPattern = "^\\s*def\\s+.*"; 
			// [arbitrary whitespace] + ['def'] + [at least one whitespace] + [arbitrary string]
			return line.matches(regexPattern);
		}


		private boolean startsNotWithWhitespace(String line) {
			return ! startsWithWhitespace(line);
		}
		private boolean startsWithWhitespace(String line) {
			return line.matches("^\\s+.*"); // [arbitrary whitespace] + [arbitrary string]
		}

		
		private void determineFunctionBlocks() {
			// After 'cleaning' the file, we only need to to split lines by def, trim empty lines and count results
			List<List<String>> functionBlocks = splitLinesByLiteralDef();
			for(List<String>funcBlock : functionBlocks)
				trimEmptyLinesAtTheEnd(funcBlock);
			saveFunctionSizes(functionBlocks);			
		}

		private List<List<String>> splitLinesByLiteralDef() {
			List<List<String>> functionBlocks = new ArrayList<>();
			if(0 == allLines.size())
				return functionBlocks;
			int offset = 0;  // Line in index 0 contains a def statement 
			int indexOfNextDefLiteralLine = getLineIndexOfNextDefLiteral(offset+1);
			while(-1 != indexOfNextDefLiteralLine) {
				functionBlocks.add(allLines.subList(offset, indexOfNextDefLiteralLine));
				offset = indexOfNextDefLiteralLine;
				indexOfNextDefLiteralLine = getLineIndexOfNextDefLiteral(offset+1);
			}
			// Extract last block
			functionBlocks.add(allLines.subList(offset, allLines.size()));
			
			return functionBlocks;			
		}

		private void trimEmptyLinesAtTheEnd(List<String> funcBlock) {
			for(int i=funcBlock.size()-1; i >= 0 ;--i){
				if(isEmptyLine(funcBlock.get(i)))
					funcBlock.remove(i);
				else
					return;
			}
			
		}

		private boolean isEmptyLine(String line) {
			return line.matches("^\\s*$"); // Arbitrary whitespace between start and end of line
		}
		
		
		private void saveFunctionSizes(List<List<String>> functionBlocks) {
			// Every function should have only the lines belonging to the function
			for(List<String> funcBlock : functionBlocks) {
				int funcSize = funcBlock.size() -1; // -1 for line containing def
				if(funcSize < WARNING_THRESHOLD) {
					smallFunctions++;
				}else if(funcSize < TOO_BIG_THRESHOLD) {
					acceptableFunctions++;
				}else {
					tooBigFunctions++;
				}
			}				
		}
		
		public int getNumberOfSmallFunctions() {
			return smallFunctions;
		}
		
		public int getNumberOfAcceptableFunctions() {
			return acceptableFunctions;
		}
		
		public int getNumberOfTooBigFunctions() {
			return tooBigFunctions;
		}

		
	}
	
	public static void main(String[] args) throws IOException {
		try {
			FunctionSizesCounter x = new FunctionSizesCounter();
			x.main();
		} catch (ConQATException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println();
	}
	
	private void main() throws IOException, ConQATException {
		String moduleContent = FileUtils.readFileToString(new File("C:\\MyTemp\\tmp\\test.py"));
		PythonFunctionSizeDeterminer pythonFunctionSizeDeterminer = new PythonFunctionSizeDeterminer(moduleContent);
		System.out.println(pythonFunctionSizeDeterminer.getNumberOfSmallFunctions());
		System.out.println(pythonFunctionSizeDeterminer.getNumberOfAcceptableFunctions());
		System.out.println(pythonFunctionSizeDeterminer.getNumberOfTooBigFunctions());
		boolean hasDef = pythonFunctionSizeDeterminer.hasDefLiteral("asfefsdfdefsdf");
		hasDef = pythonFunctionSizeDeterminer.hasDefLiteral("defsdf");
		hasDef = pythonFunctionSizeDeterminer.hasDefLiteral("def main():");
		hasDef = pythonFunctionSizeDeterminer.hasDefLiteral("def main():\r");
		hasDef = pythonFunctionSizeDeterminer.hasDefLiteral("def sdf");
		hasDef = pythonFunctionSizeDeterminer.hasDefLiteral(" def hjfgh");
		
		hasDef = pythonFunctionSizeDeterminer.isLineWihtoutIndentationAndLiteralDef("# main invocation");
		hasDef = pythonFunctionSizeDeterminer.isLineWihtoutIndentationAndLiteralDef("#===================================================================================================");
		hasDef = pythonFunctionSizeDeterminer.isLineWihtoutIndentationAndLiteralDef("#===================================================================================================");
	}

}
