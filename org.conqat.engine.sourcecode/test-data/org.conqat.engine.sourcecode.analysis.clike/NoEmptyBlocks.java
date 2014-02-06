package org.conqat.engine.sourcecode.analysis;

/** Comment. */
public class NoEmptyBlocks {
	
	private String[] emptyArray = {};

    /** Comment. */
    protected void method() {
        methodWithOnlyAComment(new Object[] {});
    }

    /** Comment. */
    protected void methodWithOnlyAComment(Object[] data) {
        data[0] = data[1];
	}
}
