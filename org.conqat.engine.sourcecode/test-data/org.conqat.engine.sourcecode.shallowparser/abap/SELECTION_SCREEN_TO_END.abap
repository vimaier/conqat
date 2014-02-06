*&---------------------------------------------------------------------*
*& Report   selection_screen_to_end                                    *
*&                                                                     *
*&---------------------------------------------------------------------*
*&                                                                     *
*&                                                                     *
*&---------------------------------------------------------------------*

REPORT  selection_screen_to_end.
DATA: ls_value TYPE value_type.
* Constants
INCLUDE constants.

SELECTION-SCREEN BEGIN OF BLOCK a1 WITH FRAME TITLE text-003.
SELECTION-SCREEN SKIP.
SELECTION-SCREEN COMMENT /2(79) text-005.
SELECTION-SCREEN PUSHBUTTON /2(20) charly USER-COMMAND start.
SELECTION-SCREEN SKIP.
SELECTION-SCREEN END OF BLOCK a1.

TABLES some_table.

INITIALIZATION.
  MOVE text-002 TO charly.

AT SELECTION-SCREEN.
  IF some_table-state = 'GO'.
    CALL FUNCTION 'FOO'.
 
    ls_value-a1 = 'bar'.
    ls_value-a2 = '400'.
    ls_value-a3 = 'X'.

    CALL METHOD zcl_util_general=>get_info
      EXPORTING
        info_for          = ls_value
      EXCEPTIONS
        not_found = 1
        OTHERS                  = 2.
    IF sy-subrc <> 0.
      MESSAGE ID sy-msgid TYPE sy-msgty NUMBER sy-msgno
                 WITH sy-msgv1 sy-msgv2 sy-msgv3 sy-msgv4.
    ENDIF.

  ENDIF.