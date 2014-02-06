void test () {
	switch (foo) {
	
		// C allows parentheses here
		case (MY_VALUE): break;
		
		// ... and casts
		case (my_type)(OTHER_VALUE): {
			do_something ();
		}
		
		// and C++ also can access values in classes and namespaces
		case FOO::Bar::baz:
			exit ();
	}
}

