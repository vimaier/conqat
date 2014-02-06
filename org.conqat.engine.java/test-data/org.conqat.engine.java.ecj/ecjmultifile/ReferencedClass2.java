public class ReferencedClass2 {
	public static void print() {
		System.out.println("Class 2");
		InnerClass.print();
		InnerClass.InnerClass2ndLevel.print();
	}

	private static class InnerClass {
		public static void print() {
			System.out.println("Inner Class of Class 2");
		}

		private static class InnerClass2ndLevel {
			public static void print() {
				System.out.println("Inner Class of Inner Class of Class 2");
			}
		}
	}
}