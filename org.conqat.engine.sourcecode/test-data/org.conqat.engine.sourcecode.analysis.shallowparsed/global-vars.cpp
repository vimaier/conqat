#include <cstdio>

int globalVar = 5;

const int noGlobal = 3;

int main () {
	int alsoNoGlobal = 4;
}

namespace foo {

	int againAGlobal = 5;

}

class C {

	const int noConstant;	
};

int C::noConstant = 6;
