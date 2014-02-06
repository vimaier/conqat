public class ReduntantLiterals {

	public static String FOO = "foo";

	public static void main(String[] args) {
		System.out.println(FOO);
		System.out.println("foo");
		
		System.out.println("bar");
		System.out.println("bar");
		System.out.println("bar");
		
		System.out.println(0);
		System.out.println(0);
		System.out.println(0);
	}

}
