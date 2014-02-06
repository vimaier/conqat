int test_me () {


	// short if 1
	if (a && b || c) ;
	else do_something ();

	// short if 2
	if (a && b || c) ;

	// short if 3
	if (a && b || c) do_something ();
	else ;

	// short while
	while (call_method ()) ;

	// very short (infinite) for
	for (;;);
}

