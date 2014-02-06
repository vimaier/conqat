package foo;

public class ComplexNesting01 {

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
		
		for (int i = 0; i < 10; ++i) {
			if (a) {
				if (i > 3) {
					if (args.length > i) {
						if (args[i] != null) {
							if (3 > 5) {
								System.err.println("foo");
							}
						}
					}
				}
			}
		}
	}
}
