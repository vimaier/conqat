// tests heuristic recognition of macros

void my_loop(void)
{
	DISABLE_STUFF
	#ifndef FOO
	for (;;)
	{
	}
	#endif
}

void test () {
	INIT_STUFF
	if (bar) 
		UN_INIT_STUFF
	else { DO_SOMETHING_ELSE }
}


void problems_in_macros_between_if_else () {
	if (calculate()) {
		doX ();
	}
#if CONDITIONALLY_ENABLED
	else {
		doY ();
	}
#endif

}

