#include <stdio.h>

namespace my {

	class MyClass {
		void conditions () {
			#if (foo)
				if (a)
			#else
				if (b)
			#endif
			 {
				doLoop;
			 }

			#if (foo)
				printf("bar");
			#endif
			
		}
		
	};

}

