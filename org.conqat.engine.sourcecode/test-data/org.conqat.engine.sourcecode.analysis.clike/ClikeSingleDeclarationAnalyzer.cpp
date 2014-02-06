#include "foo.h"
#include <bar>

namespace my {

	class MyClass {
		void test () {
			int a, b, c;
			a = b;
			b = c;
		}
		
		int test2 (int a, float b, std::string c);
	};

	int MyClass::test2 (int a, float b, std::string c) {
		if (a > 0) {
			std::map<int, int> m;
			std::map<int, float> m1, &m2, *m3;
		}
		for (int i = 0, e = 15; i < e; ++i) {
			printf ("%d", i);
		}
	}
}

