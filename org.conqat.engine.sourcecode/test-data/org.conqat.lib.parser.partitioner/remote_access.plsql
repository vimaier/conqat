create or replace procedure my_test as
begin
	raise_salary@network(emp_id, foo);
	pkgname.some_call@MY.WORLD(17, 18, 19);
end my_test;
 