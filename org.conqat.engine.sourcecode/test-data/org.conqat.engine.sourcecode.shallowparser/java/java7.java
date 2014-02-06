// some interesting cases that are new in Java 1.7
// see http://www.rapidprogramming.com/tutorial/List-Java-1-7-Features-418 for a summary

public class Foo {
	
	// Diamond operator for attributes
	private Map<String, String> myMap = new HashMap<>();
	
	
	public void myMethod (String s) {
		// Diamond operator for local declarations
		Map<String, String> myMap = new HashMap<>();
		
		// switch with strings
		switch (s) {
			case "hello": 
					System.out.println("Hi"); 
					break;
			case "world": 
					System.out.println("there"); 
					break;
			default: 
					System.err.println("Unknown"); 
					break;
		}
		
		// multi-catch
		try {
			DoSomething.dangerous ();
		} catch (IOException | ClassCastException | FunnyException e) {
			e.printStackTrace ();
		}
		
		// byte literals
		byte sampleByte = (byte)0b01001101;
		
		// underscores between digits in number literals
		long SSN = 819_44_9789L;
		
		// try with resources
		try (BufferedReader sampleBufferedReader = new BufferedReader(FileReader(samplePath))) {
			return sampleBufferedReader.readLine();
		}

		// more than one variable
		try (OpenDoor door = new OpenDoor();
			 OpenWindow window = new OpenWindow()) {
			door.connect (window);
		} finally {
			window.turn ();
		}
	}
}

