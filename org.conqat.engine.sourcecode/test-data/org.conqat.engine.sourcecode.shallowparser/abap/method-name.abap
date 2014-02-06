CLASS lcl_a IMPLEMENTATION.

METHOD zif_cqse_source_object~get_meta_data.
   SELECT SINGLE * FROM reposrc INTO CORRESPONDING FIELDS OF meta_data
     WHERE progname = me->program_name
     AND r3state = 'A'.
   meta_data-objname = me->tadir_entry-obj_name.
   meta_data-objtype = me->tadir_entry-object.
   meta_data-devclass = me->tadir_entry-devclass.
ENDMETHOD. "ZIF_CQSE_SOURCE_OBJECT~GET_DATE_INFOS

ENDCLASS.               "lcl_a
