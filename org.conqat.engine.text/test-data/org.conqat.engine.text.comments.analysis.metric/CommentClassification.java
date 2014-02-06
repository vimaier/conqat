/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
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

/**
 * This is a header comment.
 * @author $Author: hummelb $
 * @version $Rev: 46284 $
 * @ConQAT.Rating RED Hash:
 */
public class CommentClassification {
	
	/** This is a interface comment */
	public int a;
	
	/*************************************
	 * This is a section comment.
	 **************************************/
	
	/** This is an interface comment */
	public int a(){
		//this is an inline comment
		return 1;
	}
	
//	private static String normalizeComment(String comment) {
//		String result = removeTags(comment);
//		result = removeCommentIdentifiers(result);
//		result = removeJavaDocElements(result);
//		result = removeLineBreaks(result);
//		result = result.replaceAll("_", " ");
//		result = result.replaceAll("}", "");
//		return result;
//	}
	
	public int todo(){
		//TODO(DS): remove this commented out code
	}
	
	
	
	
}
