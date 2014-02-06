set scan off define off;

prompt this file contains code that reveals problems that we ran into Jul 11, 2011

package my_test is

	/* Problem1: delete as function name */
	procedure delete (var1 int, var2 string) as
		/* Problem 2: left/right as variable names */
		right int;
		left int;
	begin
		/* Problem 3: "multiset union" construct. */
		left := left multiset union right;
	end;

    /** Problem 4: umlauts */
	procedure testÄÖÜ (varäöü int, varß string) as
	begin
	
	end;

end;
 
 