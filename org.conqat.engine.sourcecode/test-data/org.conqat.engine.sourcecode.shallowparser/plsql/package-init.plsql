create or replace package body pgk_foo
is

  procedure fancy ()
  is
  begin
  	foo ();
  end;
  
begin
	-- init code for package
	declare
	   local_var NUMBER;
	begin
	    local_var : = 1;
	end;
end;
  