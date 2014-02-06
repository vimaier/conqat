prompt this file contains code that reveals problems that we ran into Aug 17, 2011

package my_test is

	procedure test_me (foo varchar2) as
		/* Problem: reference to other schema */
		varname FOO.BAR@BAZ := 55;
		
		/* Problem: TIMESTAMP with PAREN */
		my_timestamp TIMESTAMP(4) := systimestamp;
	begin
	  /* Problem: .schema */
	  if some.schema is not null then
          varname := 5;
	  end if;
	  
	  /* Problem: ESCAPE */
	  if p_n like 'FOO_%' ESCAPE '§' then
	     varname := 7;
	  end if;
	  
	  /* Problem: return as identifier */
	  foo(i).return := 17;
	end;

end;
 
/* Problem: DELETE with only identifier. */
DELETE something;
 