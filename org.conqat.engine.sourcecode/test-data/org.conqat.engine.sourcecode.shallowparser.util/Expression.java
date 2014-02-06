// test class for the expression parser
public class Expression {

	private static final int CONST = 1;

	public String foo;

	protected int bar;

	/* package */
	double baz;

	public void setX(int x) {
		// does nothing
	}

	@Override
	// comments in between
	@SomeOtherAnnotation("foo")
	public void setComplex(int x) {
		int a = x * x;
		int b = a * a;
		baz = a + b + x;
	}

	private String getFoo() {
		return foo;
	}

	@Override
	protected int getBarUpdated() {
		int calls = 17;
		return bar + calls;
	}

}
