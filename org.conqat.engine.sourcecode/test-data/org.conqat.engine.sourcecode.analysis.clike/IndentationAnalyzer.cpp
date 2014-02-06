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
	
	// Constructor with correct indentation
	MyClass::MyClass(int a)
			: MySuperClass(a),
			: fMyInt(42) {
		// do nothing
	}

	// Should also be detected as correct
	MyClass::MyClass(int a, float b)
	: MySuperClass(a, b),
	: fMyInt(42) {
		// do nothing
	}

	// Incorrect indentation
	MyClass::MyClass(int a, float b, std::string c)
: MySuperClass(a, b, c),
: fMyInt(42) {
		// do nothing
	}

	// Incorrect indentation
	MyClass::MyClass(int a, float b, std::string c)
			: MySuperClass(),
			: fMyInt(42) {
		
		// Correct
		printf("");
	// Incorrect
	printf("");
printf("");
	}

	int MyClass::test2 (int a, float b, std::string c) {
		if (a > 0) {
			std::map<int, int> m;
			std::map<int, float> m1, &m2, *m3;
		}
		for (int i = 0, e = 15; i < e; ++i) {
			printf ("%d", i);
		}
	}

int MyClass::test3 () {
}
}

