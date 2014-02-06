// Here goes a method 1
boolean method1() {
   	return false;
}

namespace xzy {

    class ClassWithMethods {
    
        // Here goes a method 2
        boolean method1() {
        	return false;
        }
    
        // Here fos method 3
		int methodA() {
            return 5;
            
            for (int i=0; i<10; i++) {
				// ...            
            }
            
        }
    
    }


	class ClassWithoutMethods {
		// Here goes method 4
		int methodA() {
            return 5;
        }
	}
	
	class ClassWithoutMethods {
		// this block should not be counted		
	}
	
	class ClassWithoutMethods {
		// this block should not be counted		
	}
	
	class ClassWithMethods {
    
        // Here goes a method 5
        boolean method1() {
        	return false;
        }
        
        // inner class. should be ignored
        class ClassWithoutMethods {
			// this block should not be counted		
		}
    
    }

}

class ClassWithoutMethods {
		// this block should not be counted		
}

class ClassWithoutMethods {
	// Here goes a method 6
	boolean method1() {
    	return false;
	}
}


// Here goes a method 6
boolean method7() {
   	return false;
}