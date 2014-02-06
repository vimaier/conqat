package my_test is

TYPE foo is RECORD (
  name TAB.SOMETHING%TYPE,
  return VARCHAR2(24),  -- problem: return as identifier
  errcode NUMBER
);

procedure test_me (foo varchar2) as
	/* Problem: new with qualified name? */
	varname FOO.BAR := new FOO.BAR ();
	
begin
      varname := 5;
end;

end;