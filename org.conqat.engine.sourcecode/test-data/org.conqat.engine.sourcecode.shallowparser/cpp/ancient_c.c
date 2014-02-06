// This example uses K&R style functions, which are usually not used today, 
// but supported by most compilers still.
// Also see http://en.wikipedia.org/wiki/C_%28programming_language%29#K.26R_C

char *someVar;

int foo (a, b)
	int a;
	char *b;
{
	printf ("do something useful");
}

main (argc, argv)
	char **argv;
{
	printf ("This is a very ancient style of method decl!");
}

