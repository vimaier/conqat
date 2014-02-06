prompt this file contains code that reveals problems that we ran into Jul 21, 2011

create type my_type_name
/

set server_param on

package my_test is

	/* Problem1: out as function name */
	procedure out (var1 int, var2 string) as
		/* Problem 2: others as varname */
		test_me exception;
		others exception;
		
		/* Problem: identifier called result */
		result CLOB;
		
		/* Problem: Interval types */
		foo1 INTERVAL YEAR TO MONTH;
		foo2 INTERVAL YEAR(3) TO MONTH;
		foo3 INTERVAL DAY TO SECOND;
		foo4 INTERVAL DAY(3) TO SECOND(4);
	begin
		others := 1;
		
		/* Problem 3: COMMIT/ROLLBACK */
		MY_TRANSACTION.COMMIT;
		MY_TRANSACTION.ROLLBACK;
		
		/* Problem 4: new */
		var := new MY_TYPE(p1 => v1, p2 => v2, p3 => NULL);
		
		/* Problem: From */
		extract (second from my_timestamp);
		
		/* Problem: pipe row */
		PIPE ROW (foo(bar));
		
		/** Problem: label called continue */
		<<continue>>
		
		/** Problem: strange IDs */
		$INI_FOO := 5;
	end;


end;
 
AGGREGATE USING foo_bar;

GRANT EXECUTE ON foo TO PUBLIC;

ALTER TABLE foo;

CREATE PUBLIC SYNONYM foo FOR bar;
 
/* Problem 5: UNDER */
create type MY_TYPE UNDER OTHER_TYPE (
	CONSTRUCTOR FUNCTION MY_TYPE RETURN self as Result
)
/