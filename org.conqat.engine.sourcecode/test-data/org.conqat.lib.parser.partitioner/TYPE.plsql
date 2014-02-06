create or replace procedure my_test (
  a foo.bar%TYPE,
  b x.y%TYPE
) as
begin

	if $$PLSQL_LINE = 17 then
		some_function_call ();
	end if;
	
	some_other_function ($$PLSQL_UNIT); 

end my_test;
 