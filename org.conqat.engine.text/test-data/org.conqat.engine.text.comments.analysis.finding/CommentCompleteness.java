/** Class comment */
public class CommentChecker {

	@MyAnnotation1
	// inline comment
	@MyAnnotation2
	public void a() {
		int b = 12;
	}

	/* simple comment */
	public void b() {
	}

	/** doc comment */
	@MyAnnotation
	public void c() {
	}

	public int getSimpleGetter() {
		return 13;
	}
	
	public void d() {
		int x = 11;
	}

	/* package */int someField;
}
