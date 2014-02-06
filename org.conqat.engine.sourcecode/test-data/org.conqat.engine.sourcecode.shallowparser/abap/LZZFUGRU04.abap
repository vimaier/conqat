FUNCTION funcx.

  DATA:
    ls_a TYPE a_type.

  SELECT * INTO ls_a
    FROM a_type
      WHERE o = iv_o
      and   param   = 'N'.
  ENDSELECT.

  IF sy-subrc = 0.
    ev_n = ls_a-param_value.
  ELSE.
    RAISE constant_not_found.
  ENDIF.

  SELECT COUNT( * ) INTO ev_count
    FROM tablex
      WHERE o = iv_o
      and   flag     = iv_flag. 

  IF ev_n <> 0 and sy-subrc <> 0.
    RAISE unable_to_read_settype_info.
  ENDIF.

ENDFUNCTION.