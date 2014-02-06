
import java.util.*;

public class Extend {
	
	public void method1 () {
		for (int i = 0; i < 15; ++i) {
			int j = i*i;
			method2();
			if (i < 15) {
				method1();
			} 
			// formatting needed for testing
			else {
				System.out.println("else case 1");
			}
			System.err.println("end");
		}
	}

	public void method2 () {
		for (int i = 0; i < 3; ++i) {
			int j = i*i;
			method1();
			if (i < 13) {
				method2();
			} 
			// formatting needed for testing
			else {
				System.out.println("else case 2");
			}
			System.err.println("end");
		}
	}
	
}
