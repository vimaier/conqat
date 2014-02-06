create OR replace package foo as


  function test1(a varchar2) return int as 
    b varchar2;
    c int; 
  begin
    return do_something_special ();
  exception
    when others then return null;
  end test1;
    
  function test2(a varchar2) return int as 
    b varchar2;
    c int; 
  begin
    return do_something_special ();
  exception
    when exception1 then raise;
    when exception2 then raise;
    when exception3 then raise;
    when others then return null;
  end test2;

  function test3(a varchar2) return int as 
    b varchar2;
    c int; 
  begin
    return do_something_special ();
  exception
    when exception1 then raise;
    when exception2 then raise;
    when exception3 then raise;
  end test3;
  
end foo;
