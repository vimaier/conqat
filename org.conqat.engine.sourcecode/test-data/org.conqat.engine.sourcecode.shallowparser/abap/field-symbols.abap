FORM foo  USING xy LIKE xy.
  FIELD-SYMBOLS: <select> LIKE LINE OF xy-t_select,
                 <select2> LIKE LINE OF xy-t_select_int.

  LOOP AT xy-t_select ASSIGNING <select>.
    CLEAR: foo_select_merge.
    foo_select_merge-data_ext = <select>.
    READ TABLE xy_obj-t_select_int ASSIGNING <select>
    WITH KEY a1 = <select>-swtdoc-pod
             a2 = <select>-swtdoc-partner
             a3 = <select>-swtdoc-moveindate
             a4 = <select>-swtdoc-service_prov_new
             a5 = <select>-swtdoc-distributor.
    IF bar = 0.
      bar-data_int = <select>.
      DELETE TABLE xyz FROM <select>.
    ENDIF.
    APPEND baz TO xys.
  ENDLOOP.

ENDFORM.       