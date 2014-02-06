create or replace procedure my_test as
 	CURSOR cur_pk (lid NUMBER, limit NUMBER) IS 
 		SELECT foo FROM bar;
begin
	some_other_function (17+18+19); 
end my_test;
 