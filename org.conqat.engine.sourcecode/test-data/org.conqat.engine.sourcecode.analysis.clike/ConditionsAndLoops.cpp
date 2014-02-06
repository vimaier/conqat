#include <stdio.h>

namespace my {

	class MyClass {
		void conditions () {
			if (foo)
				printf("bar");
			else
				print("baz");
			
			if (bar) {
				printf("foo");
				if (foo && bar)
					printf("wombat");
			}
			
			if (true)
				while (true) {}
		}
		
	};

	int MyClass::loops () {
		for (int i = 0, e = 15; i < e; ++i) {
			printf ("%d", i);
			while(foo) {
				if(bar);
			}
		}
		
		while(true)
			printf("");
			
		do {
			printf("");
		} while (false);
		
	}

	void testSwitch () {
		printf ("");
		switch (MyClass::loops ()) {
		case Other::SomeOtherConst2: printf ("Hiho");
		case SomeConst: break;
		case Other::SomeOtherConst: printf ("Hiho");
		default: 
		}
	}
}

