package org.conqat.engine.sourcecode.analysis;

public class EmptyBlocks {

	protected void emptyMethod() {
	}

	protected void methodWithOnlyAComment() {
		// Meaningful comment here
	}
	
	public void methodWithEmptyComment(String foobar) {
		//
	}

	private void anotherEmptyMethod() {

	     
		
	
	}

	public String methodWithEmptyLoop() {
		for (int i = 0; i < 10; i++) {}
	}
}
