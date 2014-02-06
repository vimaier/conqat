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
 * Class for comment findings.
 * @author $Author: hummelb $
 * @version $Rev: 46284 $
 * @ConQAT.Rating RED Hash:
 */
public class CommentFindings {

	
		
		/** inner attribute comment */
		private int a1;
		
		/** Inner Constructor */
		public InnerClass{
			
		}
		
		/** copy */
		public void test(){
		}
		
		/** toString */
		public String toString(){
			//this should not happen!
			return "";
		}
		
		/** Returns number of methods */
		public void getNumberOfMethods(){
			//is this right?
			return 2;
		}
		
		public void short(){
			//that's useless
		}
		
		public void long(){
			//this is a very long comment that should contain more than 30 words to create a very long inline comment finding in the test case. aha aha aha aha aha aha aha. hopefully 30 now.
		}
		
		public void commentedOutCode(){
//			String result = removeTags(comment);
//			result = removeCommentIdentifiers(result);
//			result = removeJavaDocElements(result);
//			result = removeLineBreaks(result);
//			result = result.replaceAll("_", " ");
//			result = result.replaceAll("}", "");
//			return result;
		}
			
	}
}
