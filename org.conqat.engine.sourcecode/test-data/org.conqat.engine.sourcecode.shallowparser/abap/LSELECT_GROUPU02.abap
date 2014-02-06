FUNCTION SELECT_COUNT.
*"----------------------------------------------------------------------
*"*"Local Interface:
*"  IMPORTING
*"     REFERENCE(I_KEY) TYPE  KEY
*"  EXPORTING
*"     REFERENCE(E_COUNT) TYPE  SY-DBCNT
*"----------------------------------------------------------------------


  SELECT COUNT(*) FROM some_table INTO e_count
                           WHERE key = i_key AND
                                 state >= '000' AND state <= '999'.  


ENDFUNCTION.