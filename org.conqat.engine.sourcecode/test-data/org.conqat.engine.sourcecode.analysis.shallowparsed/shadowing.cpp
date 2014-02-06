// Note that this is not 100% valid C, as the compiler would forbid some of the constructs.
// However, it makes for a good test :)


int foo = 5;

void test () {
	int foo = 3;  // this is shadowing
	
	int bar = 5;
	
	// this is not shadowing
	for (bar = 3; bar < 4; ++bar) {
		printf ();	
	}

	// but this is shadowing
	for (int bar = 3; bar < 4; ++bar) {
		printf ();	
	}
}

int baz = 4;

// parameters can shadow as well
void test2 (int baz) {
	
}

void test3 (int param) {
	int param = 5; // shadows parameter
	
	for (int i = 0; i < 4; ++i) {
		int i = 5; // again shadowing
	}
}

void test4 () {
	for (int i = 0; i < 4; ++i) {
		globalVar1[globalVar2[i]] = 5; // no shadowing (had parsing problems for this)
	}
	int i = 5; // this is no shadowing!
}


