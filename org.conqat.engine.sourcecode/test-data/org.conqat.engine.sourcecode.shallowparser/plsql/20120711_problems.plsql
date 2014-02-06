create or replace package body my_test is

	procedure foo (PI_1 IN date := sysdate, PI_2 IN date := null) is
	begin
		PI_1 := PI_2;
	end;

	member function foo2 (SELF IN OUT bereich, PI1 IN GT_X, PO1 OUT NUMBER) RETURN BOOLEAN is
	begin
		PI_1 := PI_2;
	end;

end;

create or replace trigger my_trigger INSTEAD OF DELETE OR INSERT OR UPDATE ON my_tab 
FOR EACH ROW 
DECLARE
  r_t INTEGER;
BEGIN
END my_trigger;
 
create or replace type my_obj under xyz (
	my_vc VARCHAR(27),
	
	MEMBER PROCEDURE get_foo (foo INTEGER),
	
	MEMBER PROCEDURE get_bar (foo INTEGER),
)
/
