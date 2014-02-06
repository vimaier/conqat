

create or replace trigger MyTrig
  AFTER INSERT OR DELETE OR UPDATE of acb, def ON ghi
  REFERENCING OLD AS alt NEW AS neu
  FOR EACH ROW
  WHEN (NVL (dsds) <> NVL (dsds))
DECLARE
  bla VARCHAR2;
BEGIN

END;
/

show errors;

