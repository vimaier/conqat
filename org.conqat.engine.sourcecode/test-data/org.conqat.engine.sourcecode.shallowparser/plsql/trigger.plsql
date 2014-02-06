create or replace trigger trg_test
  before insert on foo
  for each row
begin
  if inserting
  then
    foo ();
  end if;
end;
/
Show errors;
