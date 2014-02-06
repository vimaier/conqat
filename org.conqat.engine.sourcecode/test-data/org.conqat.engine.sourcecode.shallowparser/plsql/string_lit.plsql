PROCEDURE FOO AS
BEGIN
  -- Orginal syntax.
  DBMS_OUTPUT.put_line('This is Tim''s string!');

  -- New syntax.
  DBMS_OUTPUT.put_line(q'#This is Tim's string!#');
  DBMS_OUTPUT.put_line(q'[This is Tim's string!]');
  DBMS_OUTPUT.put_line(nq'(This  is (Tim's) string!)');
  DBMS_OUTPUT.put_line(nq'{This is Tim's string!}');
  DBMS_OUTPUT.put_line(q'<This is Tim's string!>');
  DBMS_OUTPUT.put_line(q'!This is Tim's string...!');
END;

SHOW ERRORS;