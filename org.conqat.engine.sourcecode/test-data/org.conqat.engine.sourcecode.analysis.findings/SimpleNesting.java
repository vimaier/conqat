package foo;

public class SimpleNesting {

	static boolean a;

	public static void main(String[] args) {
		for (int i = 0; i < 10; ++i) {
			if (a) {
				if (i > 3) {
					if (args.length > i) {
						if (args[i] != null) {
							System.err.println("foo");
						}
					}
				}
			}
		}
	}
}
