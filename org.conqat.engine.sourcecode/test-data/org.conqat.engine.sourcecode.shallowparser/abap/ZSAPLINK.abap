*/---------------------------------------------------------------------\
*|   This file is part of SAPlink.                                     |
*|                                                                     |
*|   The code of this project is provided to you under the current     |
*|   version of the SAP Code Exchange Terms of Use. You can find the   |
*|   text on the SAP Code Exchange webpage at http://www.sdn.sap.com   |
*|                                                                     |
*|   SAPlink is provided to you AS IS with no guarantee, warranty or   |
*|   support.                                                          |
*\---------------------------------------------------------------------/
*/---------------------------------------------------------------------\
*| /  __ \           | |      (_) |         | |                        |
*| | /  \/ ___  _ __ | |_ _ __ _| |__  _   _| |_ ___  _ __ ___         |
*| | |    / _ \| '_ \| __| '__| | '_ \| | | | __/ _ \| '__/ __|        |
*| | \__/\ (_) | | | | |_| |  | | |_) | |_| | || (_) | |  \__ \        |
*|  \____/\___/|_| |_|\__|_|  |_|_.__/ \__,_|\__\___/|_|  |___/        |
*|---------------------------------------------------------------------|
*| Lead Developers : ed herrmann                                       |
*|                        ewherrmann+saplinkcred@gmail.com             |
*|                   dan mcweeney                                      |
*|                        daniel.mcweeney+saplinkcred@gmail.com        |
*|---------------------------------------------------------------------|
*| For a full list of contributors visit:                              |
*|                                                                     |
*| project homepage: http://saplink.org                                |
*| discussion group:                                                   |
*|            https://cw.sdn.sap.com/cw/groups/saplink?view=discussions|
*| project wiki:     https://wiki.sdn.sap.com/wiki/display/ABAP/SAPlink|
*\---------------------------------------------------------------------/
REPORT  zsaplink.

*/------------------------DATA----------------------------\
TABLES: sscrfields, e071, e07t.

TYPE-POOLS: icon, slis, sabc, stms, trwbo.

TYPES: BEGIN OF t_plugin,
         object TYPE ko100-object,
         text TYPE ko100-text,
       END OF t_plugin.

TYPES: BEGIN OF t_objecttable,
         classname TYPE string,
         object TYPE ko100-object,
         text TYPE ko100-text,
       END OF t_objecttable.

TYPES: BEGIN OF t_nuggetobject,
          objtype TYPE string,
          objname TYPE string,
          exists TYPE flag,
       END OF t_nuggetobject.
*addition of package data
****   Read all objects of the package
TYPES: BEGIN OF t_objects_package,
          select     TYPE char1,
          object     TYPE tadir-object,
          object_txt TYPE string,
          obj_name   TYPE tadir-obj_name,
          srcsystem  TYPE tadir-srcsystem,
          down_flag  TYPE char1,
          status     TYPE char1,
          msg        TYPE string,
       END OF t_objects_package.

DATA objects_package TYPE TABLE OF t_objects_package.
DATA packageline TYPE t_objects_package.
FIELD-SYMBOLS: <obj> LIKE LINE OF objects_package.
DATA tabletypeline TYPE ko105.
DATA tabletypesin TYPE TABLE OF ko105.
DATA tabletypesout TYPE tr_object_texts.
DATA tabletypeoutline TYPE ko100.
DATA lt_fieldcat  TYPE          slis_t_fieldcat_alv.
DATA ls_fieldcat  LIKE LINE OF  lt_fieldcat.
DATA ls_layout    TYPE          slis_layout_alv.
DATA lv_count TYPE i.
DATA lv_pers  TYPE i.

*end of data addition for packages
*addition of Transport data
TYPES: BEGIN OF t_requestobject,
          object   TYPE e071-object,
          obj_name TYPE e071-obj_name,
       END OF t_requestobject.

TYPES: tt_requestobject TYPE TABLE OF t_requestobject.

DATA it_requestobject TYPE TABLE OF t_requestobject.
DATA wa_requestobject TYPE t_requestobject.

*end of data addition for transport

DATA pluginline TYPE t_plugin.
DATA pluginlist TYPE TABLE OF t_plugin.
DATA hidid(3) TYPE c.
DATA currenttab TYPE string.
DATA isslinkee(1) TYPE c VALUE ' '.
DATA objecttable TYPE TABLE OF t_objecttable.
DATA objectline TYPE t_objecttable.
DATA _objname TYPE string.
DATA _objtype TYPE string.
DATA nuggetname TYPE string.
DATA targetobject TYPE REF TO zsaplink.
DATA xml TYPE string.
DATA excclass TYPE REF TO zcx_saplink.
DATA errormsg TYPE string.
DATA statusmsg TYPE string.
DATA _pluginexists TYPE flag.
DATA _objectexists TYPE flag.
DATA _flag TYPE flag.

DATA errorflag TYPE flag.
DATA it_nuggetobject TYPE TABLE OF t_nuggetobject.
DATA wa_nuggetobject TYPE t_nuggetobject.

DATA deffilename TYPE string.
DATA retfilename TYPE string.
DATA retpath TYPE string.
DATA retfullpath TYPE string.
DATA retuseract TYPE i.
DATA retfiletable TYPE filetable.
DATA retrc TYPE sysubrc.
DATA retuseraction TYPE i.

DATA nugg TYPE REF TO zsaplink_nugget.
DATA stemp TYPE string.
DATA anxmldoc TYPE REF TO if_ixml_document.
DATA ixmldocument TYPE REF TO if_ixml_document.

DATA foo TYPE REF TO data.
DATA len TYPE i.

DATA: l_marker TYPE i,
      l_offset TYPE i,
      l_total_offset TYPE i.

DATA:
  es_selected_request TYPE trwbo_request_header,
  es_selected_task TYPE trwbo_request_header,
  iv_organizer_type TYPE trwbo_calling_organizer,
  is_selection TYPE trwbo_selection.

*\--------------------------------------------------------------------/


*/------------------------SELECTION SCREEN----------------------------\

SELECTION-SCREEN BEGIN OF TABBED BLOCK tabb FOR 20 LINES.
SELECTION-SCREEN TAB (17) text-tb2 USER-COMMAND nugg
                     DEFAULT SCREEN 120.
SELECTION-SCREEN TAB (17) text-tb1 USER-COMMAND obj
                     DEFAULT SCREEN 110.
SELECTION-SCREEN END OF BLOCK tabb.

*Slinkee tab
SELECTION-SCREEN BEGIN OF SCREEN 110 AS SUBSCREEN.
SELECTION-SCREEN BEGIN OF BLOCK main WITH FRAME.
SELECTION-SCREEN BEGIN OF BLOCK splk WITH FRAME TITLE text-slk.
PARAMETERS import TYPE c RADIOBUTTON GROUP 2 DEFAULT 'X'
  USER-COMMAND updown.
PARAMETERS export TYPE c RADIOBUTTON GROUP 2.
SELECTION-SCREEN END OF BLOCK splk.

SELECTION-SCREEN BEGIN OF BLOCK opt WITH FRAME TITLE text-opt.
PARAMETERS filename(300) LOWER CASE TYPE c MODIF ID did.
PARAMETERS slpkg  TYPE tadir-devclass MODIF ID did.
PARAMETERS overwr TYPE c AS CHECKBOX MODIF ID did.
PARAMETERS plugin TYPE ko100-object MODIF ID uid.
PARAMETERS objname(40) TYPE c MODIF ID uid.
SELECTION-SCREEN END OF BLOCK opt.
SELECTION-SCREEN END OF BLOCK main.
SELECTION-SCREEN END OF SCREEN 110.

*Nugget tab
SELECTION-SCREEN BEGIN OF SCREEN 120 AS SUBSCREEN.
SELECTION-SCREEN BEGIN OF BLOCK main2 WITH FRAME.
SELECTION-SCREEN BEGIN OF BLOCK splk2 WITH FRAME TITLE text-slk.
PARAMETERS nugi TYPE c RADIOBUTTON GROUP 3 DEFAULT 'X'
  USER-COMMAND updown.
PARAMETERS nugd TYPE c RADIOBUTTON GROUP 3.
SELECTION-SCREEN ULINE.
PARAMETERS nugc TYPE c RADIOBUTTON GROUP 3.
PARAMETERS nuga TYPE c RADIOBUTTON GROUP 3.
PARAMETERS nugp TYPE c RADIOBUTTON GROUP 3.
PARAMETERS nugr TYPE c RADIOBUTTON GROUP 3.
SELECTION-SCREEN END OF BLOCK splk2.

SELECTION-SCREEN BEGIN OF BLOCK opt2 WITH FRAME TITLE text-opt.
PARAMETERS nuggnam(300) TYPE c MODIF ID nnm.
PARAMETERS nplugin TYPE  ko100-object MODIF ID npg.
PARAMETERS nobjnam(40) TYPE c MODIF ID npg.
*      parameters nPlugIn type  KO100-object modif id npg.
PARAMETER package      TYPE tadir-devclass MODIF ID npc.
SELECT-OPTIONS  reqnugg FOR e071-trkorr NO INTERVALS
  NO-EXTENSION MODIF ID rnm.
PARAMETER nugfile(300) LOWER CASE TYPE c MODIF ID nfl.
PARAMETERS novrwr TYPE c AS CHECKBOX MODIF ID now.
SELECTION-SCREEN END OF BLOCK opt2.
SELECTION-SCREEN END OF BLOCK main2.
SELECTION-SCREEN END OF SCREEN 120.
*\--------------------------------------------------------------------/


*/----------------------selection screen events-----------------------\
INITIALIZATION.
  CALL METHOD zsaplink=>getplugins(
    CHANGING
      objecttable = objecttable ).

  IMPORT isslinkee FROM MEMORY ID 'ISSLNK'.

  IF isslinkee = 'X'.
    tabb-dynnr = 110.
    tabb-activetab = 'OBJ'.
  ELSE.
    tabb-dynnr   = 120.
    tabb-activetab = 'NUGG'.
  ENDIF.

AT SELECTION-SCREEN.
  CASE sscrfields-ucomm.
    WHEN 'OBJ'.
      isslinkee = 'X'.
    WHEN 'NUGG'.
      isslinkee = ' '.
  ENDCASE.
  EXPORT isslinkee TO MEMORY ID 'ISSLNK'.

AT SELECTION-SCREEN OUTPUT.
*** hide/show fields according to current selection
  IF import = 'X'.
    hidid = 'UID'.
    IF slpkg IS INITIAL.
      slpkg = '$TMP'.
    ENDIF.
  ELSEIF export = 'X'.
    hidid = 'DID'.
  ENDIF.
  " Set default devclass for import
  IF nugi = 'X' AND package IS INITIAL.
    package = '$TMP'.
  ENDIF.

  LOOP AT SCREEN.
    IF screen-group1 = hidid.
      screen-active = '0'.
      screen-invisible = '1'.
      MODIFY SCREEN.
    ENDIF.
  ENDLOOP.

  LOOP AT SCREEN.
    IF nugc = 'X'.
      IF screen-group1 = 'NNM'.
        screen-active = '1'.
        screen-invisible = '0'.
        MODIFY SCREEN.
      ELSEIF screen-group1 = 'NPG' OR screen-group1 = 'NFL'
        OR screen-group1 = 'NOW' OR screen-group1 = 'NPC'
        OR screen-group1 = 'RNM'.
        screen-active = '0'.
        screen-invisible = '1'.
        MODIFY SCREEN.
      ENDIF.
    ELSEIF nugi = 'X'.
      IF screen-group1 = 'NFL' OR screen-group1 = 'NOW'.
        screen-active = '1'.
        screen-invisible = '0'.
        MODIFY SCREEN.
      ELSEIF screen-group1 = 'NNM' OR screen-group1 ='NPG'
         OR screen-group1 = 'RNM'.
        screen-active = '0'.
        screen-invisible = '1'.
        MODIFY SCREEN.
      ENDIF.
    ELSEIF nuga = 'X'.
      IF screen-group1 = 'NFL' OR screen-group1 = 'NPG'.
        screen-active = '1'.
        screen-invisible = '0'.
        MODIFY SCREEN.
      ELSEIF screen-group1 = 'NNM' OR screen-group1 = 'NOW'
         OR screen-group1 = 'NPC' OR screen-group1 = 'RNM'.
        screen-active = '0'.
        screen-invisible = '1'.
        MODIFY SCREEN.
      ENDIF.
    ELSEIF nugp = 'X'.
      IF screen-group1 = 'NFL' OR screen-group1 = 'NPC'.
        screen-active = '1'.
        screen-invisible = '0'.
        MODIFY SCREEN.
      ELSEIF screen-group1 = 'NNM' OR screen-group1 = 'NOW'
        OR screen-group1 = 'NPG' OR screen-group1 = 'RNM'.
        screen-active = '0'.
        screen-invisible = '1'.
        MODIFY SCREEN.
      ENDIF.
    ELSEIF nugd = 'X'.
      IF screen-group1 = 'NFL'.
        screen-active = '1'.
        screen-invisible = '0'.
        MODIFY SCREEN.
      ELSEIF screen-group1 = 'NNM' OR screen-group1 ='NPG'
       OR screen-group1 = 'NOW' OR screen-group1 = 'NPC'
       OR screen-group1 = 'RNM'.
        screen-active = '0'.
        screen-invisible = '1'.
        MODIFY SCREEN.
      ENDIF.
    ELSEIF nugr = 'X'.
      IF screen-group1 = 'NFL' OR screen-group1 = 'RNM'.
        screen-active = '1'.
        screen-invisible = '0'.
        MODIFY SCREEN.
      ELSEIF screen-group1 = 'NNM' OR screen-group1 = 'NOW'
        OR screen-group1 = 'NPG' OR screen-group1 = 'NPC'.
        screen-active = '0'.
        screen-invisible = '1'.
        MODIFY SCREEN.
      ENDIF.
    ENDIF.
  ENDLOOP.

*** value request for input fields
AT SELECTION-SCREEN ON VALUE-REQUEST FOR plugin.
  REFRESH pluginlist.
  LOOP AT objecttable INTO objectline.
    MOVE-CORRESPONDING objectline TO pluginline.
    APPEND pluginline TO pluginlist.
  ENDLOOP.

  CALL FUNCTION 'F4IF_INT_TABLE_VALUE_REQUEST'
    EXPORTING
      retfield        = 'OBJECT'
      window_title    = 'Installed Plugins'(inp)
      dynpprog        = sy-repid
      dynpnr          = '1000'
      dynprofield     = 'PLUGIN'
      value_org       = 'S'
    TABLES
      value_tab       = pluginlist
    EXCEPTIONS
      parameter_error = 1
      no_values_found = 2
      OTHERS          = 3.

AT SELECTION-SCREEN ON VALUE-REQUEST FOR nplugin.
  REFRESH pluginlist.
  LOOP AT objecttable INTO objectline.
    MOVE-CORRESPONDING objectline TO pluginline.
    APPEND pluginline TO pluginlist.
  ENDLOOP.

  CALL FUNCTION 'F4IF_INT_TABLE_VALUE_REQUEST'
    EXPORTING
      retfield        = 'OBJECT'
      window_title    = 'Installed Plugins'(inp)
      dynpprog        = sy-repid
      dynpnr          = '1000'
      dynprofield     = 'NPLUGIN'
      value_org       = 'S'
    TABLES
      value_tab       = pluginlist
    EXCEPTIONS
      parameter_error = 1
      no_values_found = 2
      OTHERS          = 3.

AT SELECTION-SCREEN ON VALUE-REQUEST FOR filename.
  CALL METHOD cl_gui_frontend_services=>file_open_dialog
    EXPORTING
      multiselection    = abap_false
      file_filter       = '*.slnk'
      default_extension = 'slnk'
    CHANGING
      file_table        = retfiletable
      rc                = retrc
      user_action       = retuseraction.
  READ TABLE retfiletable INTO filename INDEX 1.

AT SELECTION-SCREEN ON VALUE-REQUEST FOR nugfile.
  CALL METHOD cl_gui_frontend_services=>file_open_dialog
    EXPORTING
      multiselection    = abap_false
      file_filter       = 'Nugget files (*.nugg)|*.nugg|'
      default_extension = 'nugg'
    CHANGING
      file_table        = retfiletable
      rc                = retrc
      user_action       = retuseraction.
  READ TABLE retfiletable INTO nugfile INDEX 1.

* begin-->search help on objname according to selected plugin
* provided by Michael Diehl
AT SELECTION-SCREEN ON VALUE-REQUEST FOR objname.
  DATA l_object_type LIKE  euobj-id.
* l_object_type = plugin.  "commented ewH

*ewH-->get most current value of plugin param
  PERFORM get_current_screen_value USING 'PLUGIN' '0110'
                                CHANGING l_object_type.

  IF  l_object_type IS NOT INITIAL.
*  rrq --> START of implementation for object specific value help

    DATA: temp_object TYPE ko100-object.
*   move the object type to a field like the ObjectTable expects
    temp_object = l_object_type.
    READ TABLE objecttable INTO objectline WITH KEY object = temp_object.
    IF sy-subrc = 0.
*    if it is found...intanciate it so you can call the right value help
      CREATE OBJECT targetobject
        TYPE
        (objectline-classname)
        EXPORTING
          name = _objname.
      _objtype = l_object_type.
      CALL METHOD targetobject->valuehelp
        EXPORTING
          i_objtype = _objtype
        RECEIVING
          e_objname = _objname.
      objname = _objname.
    ENDIF.

*commented out...moved logic to instance method of ZSAPLINK.  to be overwritten by
*objects that don't use the repository Info_system f4 Function
*   CALL FUNCTION 'REPOSITORY_INFO_SYSTEM_F4'
*     EXPORTING
*       object_type           = l_object_type
*       object_name           = objname
*       suppress_selection    = 'X'
*       use_alv_grid          = ''
*       without_personal_list = ''
*     IMPORTING
*       object_name_selected  = objname
*     EXCEPTIONS
*       cancel                = 1.
*<-- rrq end of implentation for object specific value help
  ENDIF.


AT SELECTION-SCREEN ON VALUE-REQUEST FOR nobjnam.
  DATA l_object_type LIKE  euobj-id.
* l_object_type = nplugin. "commented ewH

*ewH-->get most current value of plugin param
  PERFORM get_current_screen_value USING 'NPLUGIN' '0120'
                                CHANGING l_object_type.

  IF  l_object_type IS NOT INITIAL.
*  rrq --> START of implementation for object specific value help

    DATA: temp_object TYPE ko100-object.
*   move the object type to a field like the ObjectTable expects
    temp_object = l_object_type.
    READ TABLE objecttable INTO objectline WITH KEY object = temp_object.
    IF sy-subrc = 0.
*    if it is found...intanciate it so you can call the right value help
      CREATE OBJECT targetobject
        TYPE
        (objectline-classname)
        EXPORTING
          name = _objname.
      _objtype = l_object_type.
      CALL METHOD targetobject->valuehelp
        EXPORTING
          i_objtype = _objtype
        RECEIVING
          e_objname = _objname.
      nobjnam = _objname.


    ENDIF.
*commented out...moved logic to instance method of ZSAPLINK.  to be overwritten by
*objects that don't use the repository Info_system f4 Function
*   CALL FUNCTION 'REPOSITORY_INFO_SYSTEM_F4'
*     EXPORTING
*       object_type           = l_object_type
*       object_name           = objname
*       suppress_selection    = 'X'
*       use_alv_grid          = ''
*       without_personal_list = ''
*     IMPORTING
*       object_name_selected  = objname
*     EXCEPTIONS
*       cancel                = 1.
*<-- rrq end of implentation for object specific value help
  ENDIF.
* <--end of search help on objname according to selected plugin
* provided by Michael Diehl

AT SELECTION-SCREEN ON VALUE-REQUEST FOR reqnugg-low.
  iv_organizer_type = 'W'.
*  is_selection-reqstatus = 'R'.
  CALL FUNCTION 'TR_PRESENT_REQUESTS_SEL_POPUP'
    EXPORTING
      iv_organizer_type   = iv_organizer_type
      is_selection        = is_selection
    IMPORTING
      es_selected_request = es_selected_request
      es_selected_task    = es_selected_task.

  reqnugg-low = es_selected_request-trkorr.

*\--------------------------------------------------------------------/

*/----------------------main------------------------------------------\
START-OF-SELECTION.
  CLEAR: errormsg, statusmsg.
************* S L I N K E E *************
  IF isslinkee IS NOT INITIAL.
    _objname = objname.
*   Export slinkee
    IF export = 'X'.
      IF plugin IS INITIAL.
        MESSAGE s208(00) WITH 'object type required'.
        EXIT.
      ELSEIF _objname IS INITIAL.
        MESSAGE s208(00) WITH 'object name required'.
        EXIT.
      ENDIF.
      READ TABLE objecttable INTO objectline WITH KEY object = plugin.
      IF sy-subrc <> 0.
        CONCATENATE 'Plugin for object type'(plo) plugin
          'is not installed on this system'(noi) INTO errormsg
          SEPARATED BY space.
        PERFORM writemessage USING 'E' errormsg.
        EXIT.
      ENDIF.
      CREATE OBJECT targetobject
        TYPE
        (objectline-classname)
        EXPORTING
          name = _objname.
      TRY.
          ixmldocument = targetobject->createixmldocfromobject( ).
        CATCH zcx_saplink INTO excclass.
          errormsg = excclass->get_text( ).
          PERFORM writemessage USING 'E' errormsg.
      ENDTRY.
      IF errormsg IS NOT INITIAL.
        EXIT.
      ENDIF.
      xml = zsaplink=>convertixmldoctostring( ixmldocument ).

      CONCATENATE plugin '_' _objname '.slnk' INTO deffilename.
      CLEAR errorflag.
      PERFORM downloadxmltolm USING deffilename xml
                              CHANGING errorflag.
      IF errorflag IS NOT INITIAL.
        MESSAGE s208(00) WITH 'Action cancelled'.
        EXIT.
      ENDIF.
      PERFORM displayxmlonscreen USING xml.
*   Import slinkee
    ELSEIF import = 'X'.
      IF filename IS INITIAL.
        MESSAGE s208(00) WITH 'slinkee filename required'.
        EXIT.
      ENDIF.
      PERFORM uploadxmlfromlm USING filename xml.
      IF sy-subrc <> 0.
        EXIT.
      ENDIF.
      ixmldocument = zsaplink=>convertstringtoixmldoc( xml ).
*     run some checks before install
      PERFORM checkobject USING ixmldocument
                          CHANGING _objtype
                                   _objname
                                   _pluginexists
                                   _objectexists.
      MOVE _objtype TO plugin.
      READ TABLE objecttable INTO objectline WITH KEY object = plugin.

      IF _objtype = 'NUGG'.
        MESSAGE s208(00) WITH 'use nugget tab for nugget import'.
        EXIT.
      ELSEIF _pluginexists IS INITIAL.
        CONCATENATE 'Plugin for object type'(plo) _objtype
          'is not installed on this system'(noi) INTO errormsg
          SEPARATED BY space.
        PERFORM writemessage USING 'E' errormsg.
        EXIT.
      ELSEIF _objectexists = 'X' AND overwr IS INITIAL.
        CONCATENATE _objtype objectline-text _objname
          'already exists. Use overwrite orginals option to replace'
          INTO errormsg SEPARATED BY space.
        PERFORM writemessage USING 'E' errormsg.
        EXIT.
      ELSEIF _objectexists = 'X' AND overwr = 'X'.
        CONCATENATE _objtype _objname INTO stemp SEPARATED BY space.
        PERFORM confirmoverwrite USING stemp
                              CHANGING _flag.
        IF _flag = 'A'. "cancel
          PERFORM writemessage USING 'W' 'Import cancelled by user'.
          EXIT.
        ENDIF.
      ENDIF.

*     install object
      PERFORM installobject USING ixmldocument
                                  overwr
                                  slpkg
                         CHANGING errorflag
                                  statusmsg.
      IF errorflag = 'X'.
        PERFORM writemessage USING 'E' statusmsg.
        EXIT.
      ELSE.
        PERFORM writemessage USING 'S' statusmsg.
      ENDIF.
      MESSAGE s208(00) WITH 'Import successful'.
    ENDIF.
  ELSE.
************* N U G G E T *************
*   create empty nugget
    IF nugc = 'X'.
      IF nuggnam IS INITIAL.
        MESSAGE s208(00) WITH 'enter name of new nugget to be created'.
        EXIT.
      ENDIF.
      stemp = nuggnam.
      PERFORM createemptynugget USING stemp.
*   add object to nugget
    ELSEIF nuga = 'X'.
      IF nplugin IS INITIAL.
        MESSAGE s208(00) WITH 'object type required'.
        EXIT.
      ELSEIF nobjnam IS INITIAL.
        MESSAGE s208(00) WITH 'object name required'.
        EXIT.
      ELSEIF nugfile IS INITIAL.
        MESSAGE s208(00) WITH 'nugget filename required'.
        EXIT.
      ENDIF.
      READ TABLE objecttable INTO objectline WITH KEY object = nplugin.
      IF sy-subrc <> 0.
        CONCATENATE 'Plugin for object type'(plo) nplugin
          'is not installed on this system'(noi) INTO errormsg
            SEPARATED BY space.
        PERFORM writemessage USING 'E' errormsg.
        EXIT.
      ENDIF.
      stemp = nuggnam.
      PERFORM uploadxmlfromlm USING nugfile xml.
      IF sy-subrc <> 0.
        EXIT.
      ENDIF.
      ixmldocument = zsaplink=>convertstringtoixmldoc( xml ).
      CREATE OBJECT nugg
        EXPORTING
          ixmldocument = ixmldocument.

      _objname = nobjnam.
      stemp = nplugin.

      TRY.
          nugg->addobjecttonugget( objname = _objname objtype = stemp ).
        CATCH zcx_saplink INTO excclass.
          errormsg = excclass->get_text( ).
          PERFORM writemessage USING 'E' errormsg.
          EXIT.
      ENDTRY.
      ixmldocument = nugg->createixmldocfromnugget( ).
      xml = zsaplink=>convertixmldoctostring( ixmldocument ).
*      concatenate  nuggNam '.nugg' into sTemp.
      stemp = nugfile.
      PERFORM putonmachine USING stemp xml.
*   import nugget
    ELSEIF nugi = 'X'.
      IF nugfile IS INITIAL.
        MESSAGE s208(00) WITH 'nugget filename required'.
        EXIT.
      ENDIF.
      PERFORM uploadxmlfromlm USING nugfile xml.
      IF sy-subrc <> 0.
        EXIT.
      ENDIF.
      ixmldocument = zsaplink=>convertstringtoixmldoc( xml ).
      nuggetname = zsaplink_nugget=>getnuggetinfo( ixmldocument ).
      CONCATENATE 'Start import of nugget' nuggetname INTO statusmsg
        SEPARATED BY space.

      PERFORM writemessage USING 'S' statusmsg.
      SKIP.

      CREATE OBJECT nugg
        EXPORTING
          ixmldocument = ixmldocument.

*     check for installed plugins
      CLEAR errorflag.
      REFRESH it_nuggetobject.
      anxmldoc = nugg->getnextobject( ).
      WHILE anxmldoc IS NOT INITIAL.
        CLEAR: _objtype, _objname, _pluginexists, _objectexists,
               wa_nuggetobject.
        PERFORM checkobject USING anxmldoc
                            CHANGING _objtype
                                     _objname
                                     _pluginexists
                                     _objectexists.
        IF _pluginexists IS INITIAL.
          CONCATENATE 'Plugin for object type'(plo) _objtype
            'is not installed on this system'(noi) INTO errormsg
            SEPARATED BY space.
          PERFORM writemessage USING 'E' errormsg.
          errorflag = 'X'.
        ELSEIF _objectexists = 'X' AND novrwr IS INITIAL.
          CONCATENATE _objtype _objname 'already exists. Use overwrite'
            'orginals option to replace'
              INTO errormsg SEPARATED BY space.
          PERFORM writemessage USING 'W' errormsg.
          errorflag = 'X'.
        ENDIF.
        wa_nuggetobject-objtype = _objtype.
        wa_nuggetobject-objname = _objname.
        wa_nuggetobject-exists = _objectexists.
        APPEND wa_nuggetobject TO it_nuggetobject.

        anxmldoc = nugg->getnextobject( ).
      ENDWHILE.

      IF errorflag = 'X'.
        EXIT.
      ENDIF.

*     confirm overwrite
      LOOP AT it_nuggetobject INTO wa_nuggetobject WHERE exists = 'X'.
        CLEAR _flag.
        CONCATENATE wa_nuggetobject-objtype wa_nuggetobject-objname
          INTO stemp SEPARATED BY space.
        PERFORM confirmoverwrite USING stemp
                              CHANGING _flag.
        IF _flag = '1'. "yes
          CONTINUE.
        ELSEIF _flag = '2'. "yes to all
          CLEAR errorflag.
          EXIT.
        ELSEIF _flag = 'A'. "cancel
          PERFORM writemessage USING 'W' 'Import cancelled by user'.
          errorflag = 'X'.
          EXIT.
        ENDIF.
      ENDLOOP.

      IF errorflag = 'X'.
        EXIT.
      ENDIF.

*     install
      nugg->reset( ). "reset nugget iterator
      anxmldoc = nugg->getnextobject( ).
      WHILE anxmldoc IS NOT INITIAL.
        CLEAR statusmsg.
        PERFORM installobject USING anxmldoc
                                    novrwr
                                    package
                           CHANGING errorflag
                                    statusmsg.
        IF errorflag = 'X'.
          PERFORM writemessage USING 'E' statusmsg.
          EXIT.
        ELSE.
          PERFORM writemessage USING 'S' statusmsg.
          anxmldoc = nugg->getnextobject( ).
        ENDIF.
      ENDWHILE.

      IF errorflag = 'X'.
        EXIT.
      ENDIF.
*   display objects in a nugget
    ELSEIF nugd = 'X'.
      IF nugfile IS INITIAL.
        MESSAGE s208(00) WITH 'nugget filename required'.
        EXIT.
      ENDIF.
      PERFORM uploadxmlfromlm USING nugfile xml.
      IF sy-subrc <> 0.
        EXIT.
      ENDIF.
      ixmldocument = zsaplink=>convertstringtoixmldoc( xml ).
      nuggetname = zsaplink_nugget=>getnuggetinfo( ixmldocument ).
      WRITE: / 'Object list for nugget ', nuggetname. SKIP.

      CREATE OBJECT nugg
        EXPORTING
          ixmldocument = ixmldocument.
      anxmldoc = nugg->getnextobject( ).

      IF anxmldoc IS INITIAL.
        errormsg = 'You have an empty Nugget'.
        PERFORM writemessage USING 'W' errormsg.
        EXIT.
      ENDIF.

      WHILE anxmldoc IS NOT INITIAL.
        CALL METHOD zsaplink=>getobjectinfofromixmldoc
          EXPORTING
            ixmldocument = anxmldoc
          IMPORTING
            objtypename  = _objtype
            objname      = _objname.

        CONCATENATE _objtype _objname INTO statusmsg SEPARATED BY space.
        PERFORM writemessage USING 'S' statusmsg.
        anxmldoc = nugg->getnextobject( ).
      ENDWHILE.

*   rrq: enhancement 3-->
*   add package to nugget
    ELSEIF nugp = 'X'.
      IF package  IS INITIAL.
        MESSAGE s208(00) WITH 'package required'.
        EXIT.
      ENDIF.
      IF nugfile IS INITIAL.
        MESSAGE s208(00) WITH 'nugget filename required'.
        EXIT.
      ENDIF.
      "// Mar: Added logic discard deleted objects from Package - 10/05/2009
      IF sy-saprl NE '701'.
        "// Mar: Added logic discard deleted objects from Package - 10/05/2009

        SELECT object obj_name srcsystem      " ##TOO_MANY_ITAB_FIELDS
            FROM tadir
            INTO CORRESPONDING FIELDS OF TABLE objects_package
            WHERE devclass  EQ package
            AND  pgmid      EQ 'R3TR'.

        "// Mar: Added logic discard deleted objects from Package - 10/05/2009
      ELSE.
        SELECT object obj_name srcsystem      " ##TOO_MANY_ITAB_FIELDS
            FROM tadir
            INTO CORRESPONDING FIELDS OF TABLE objects_package
            WHERE devclass  EQ package
            AND  pgmid      EQ 'R3TR'
            AND  delflag    NE 'X'.
      ENDIF.
      "// Mar: Added logic discard deleted objects from Package - 10/05/2009

      IF sy-subrc <> 0.
        MESSAGE s208(00) WITH 'Package does not exist or empty'.
        RETURN.
      ENDIF.

      PERFORM addobjectstonugget.
*   <-- rrq: enhancement 3

*   rrq: enhancement 42-->
*   add objects from a transport to a nugget
    ELSEIF nugr = 'X'.
      DATA: reqname TYPE string.

      DATA: l_trkorr TYPE e07t-trkorr,
            l_as4text TYPE e07t-as4text.

      IF nugfile IS INITIAL.
        MESSAGE s208(00) WITH 'nugget filename required'.
        EXIT.
      ENDIF.

      IF reqnugg[] IS INITIAL.
        MESSAGE s208(00) WITH 'Request number required'.
        EXIT.
      ENDIF.

      SELECT SINGLE trkorr FROM e070 INTO l_trkorr
        WHERE trkorr IN reqnugg.

      IF sy-subrc <> 0.
        MESSAGE s208(00) WITH 'Transport not found'.
        EXIT.
      ENDIF.

      SELECT SINGLE trkorr as4text
      FROM  e07t
      INTO (l_trkorr, l_as4text)
      WHERE  trkorr   IN reqnugg
        AND  langu    EQ sy-langu.

*     ewH-->retrieve tasks as well as transports
      RANGES: ra_reqnugg FOR e070-trkorr.
      DATA: wa_trkorr TYPE e070-trkorr,
            it_trkorr TYPE TABLE OF e070-trkorr,
            wa_reqnugg LIKE LINE OF ra_reqnugg.

      SELECT trkorr FROM e070 INTO TABLE it_trkorr
        WHERE strkorr IN reqnugg.

      ra_reqnugg[] = reqnugg[].

      LOOP AT it_trkorr INTO wa_trkorr.
        wa_reqnugg-sign = 'I'.
        wa_reqnugg-option = 'EQ'.
        wa_reqnugg-low = wa_trkorr.
        APPEND wa_reqnugg TO ra_reqnugg.
      ENDLOOP.
*     <--ewH

      SELECT object obj_name
      FROM  e071
      INTO TABLE it_requestobject
*      WHERE  TRKORR in ReqNugg.
      WHERE  trkorr IN ra_reqnugg "ewH
      AND pgmid = 'R3TR'. "ewH: don't need subobjects

      IF sy-subrc = 0.
        reqname = l_trkorr.
      ELSE.
        MESSAGE s208(00) WITH 'No R3TR objects in request'.
        EXIT.
      ENDIF.

      LOOP AT it_requestobject INTO wa_requestobject.
        MOVE-CORRESPONDING wa_requestobject TO packageline.
        APPEND packageline TO objects_package.
      ENDLOOP.

      PERFORM addobjectstonugget.
    ENDIF.
  ENDIF.

*\--------------------------------------------------------------------/

*/----------------------displayXMLOnScreen----------------------------\
FORM displayxmlonscreen USING xmlstring TYPE string.
  DATA printxmldoc TYPE REF TO cl_xml_document.
  DATA rc TYPE sysubrc.

  CREATE OBJECT printxmldoc.
  rc = printxmldoc->parse_string( xmlstring ).
  CALL METHOD printxmldoc->display( ).

ENDFORM.                    "displayXMLOnScreen
*\--------------------------------------------------------------------/

*/----------------------downloadXMLToLM-------------------------------\
FORM downloadxmltolm USING   deffilename TYPE string
                             xmlstring TYPE string
                    CHANGING _errorflag TYPE flag.

  DATA retfilename TYPE string.
  DATA retpath TYPE string.
  DATA retfullpath TYPE string.
  DATA retuseract TYPE i.
  DATA deffilename_cleaned TYPE string.

  CLEAR _errorflag.

  "*--- get rid of namespace slashes ---*
  deffilename_cleaned = deffilename.
  REPLACE ALL OCCURRENCES OF '/' IN deffilename_cleaned WITH '.'.

  CALL METHOD cl_gui_frontend_services=>file_save_dialog
    EXPORTING
      default_file_name = deffilename_cleaned
    CHANGING
      filename          = retfilename
      path              = retpath
      fullpath          = retfullpath
      user_action       = retuseract.

  IF retuseract <> 0.
    _errorflag = 'X'.
  ELSE.
    PERFORM putonmachine USING retfullpath xmlstring.
  ENDIF.


ENDFORM.                    "downloadXMLToLM
*\--------------------------------------------------------------------/


*/------------------------putOnMachine--------------------------------\
FORM putonmachine USING fullpath TYPE string xmlstring TYPE string.

*rrq: issue 43--> replace binary with char table
*old code removed, use subversion for recovery
*types: begin of t_char,
*        maxChar(65535) type C,
*       end of t_char.

*data: tempTable_char type table of t_char,
*  DATA: temptable_char TYPE table_of_strings,
*        tempstring TYPE string.
  " Gregor Wolf, 2012-02-19: Switch to binary download as it's done in ZAKE
  DATA xlm_xstring  TYPE xstring.
  DATA temptable    TYPE w3mimetabtype.
  DATA bin_filesize TYPE i.

  IF retuseract = 0.
    " Gregor Wolf, 2012-02-19:
    " Independent of Server OS the download should always use Windows Linebreaks
    REPLACE ALL OCCURRENCES OF cl_abap_char_utilities=>newline
      IN xmlstring WITH cl_abap_char_utilities=>cr_lf.

    CALL FUNCTION 'SCMS_STRING_TO_XSTRING'
      EXPORTING
        text   = xmlstring
      IMPORTING
        buffer = xlm_xstring.

    CALL FUNCTION 'SCMS_XSTRING_TO_BINARY'
      EXPORTING
        buffer        = xlm_xstring
      IMPORTING
        output_length = bin_filesize
      TABLES
        binary_tab    = temptable.

    cl_gui_frontend_services=>gui_download(
       EXPORTING
         bin_filesize = bin_filesize
         filename     = fullpath
         filetype     = 'BIN'
       CHANGING
         data_tab     = temptable
       EXCEPTIONS
         file_write_error          = 1
         no_batch                  = 2
         gui_refuse_filetransfer   = 3
         invalid_type              = 4
         no_authority              = 5
         unknown_error             = 6
         header_not_allowed        = 7
         separator_not_allowed     = 8
         filesize_not_allowed      = 9
         header_too_long           = 10
         dp_error_create           = 11
         dp_error_send             = 12
         dp_error_write            = 13
         unknown_dp_error          = 14
         access_denied             = 15
         dp_out_of_memory          = 16
         disk_full                 = 17
         dp_timeout                = 18
         file_not_found            = 19
         dataprovider_exception    = 20
         control_flush_error       = 21
         not_supported_by_gui      = 22
         error_no_gui              = 23
         OTHERS                    = 24
    ).
    IF sy-subrc <> 0.
      MESSAGE ID sy-msgid TYPE sy-msgty NUMBER sy-msgno
                 WITH sy-msgv1 sy-msgv2 sy-msgv3 sy-msgv4.
    ENDIF.
*    SPLIT xmlstring AT cl_abap_char_utilities=>cr_lf
*    INTO TABLE temptable_char.

*    CALL METHOD cl_gui_frontend_services=>gui_download
*      EXPORTING
*        filename = fullpath
*        filetype = 'DAT'
*      CHANGING
*        data_tab = temptable_char.
  ENDIF.
*<--rrq: issue 43
ENDFORM.                    "putOnMachine
*\--------------------------------------------------------------------/


*/----------------------uploadXMLFromLM-------------------------------\
FORM uploadxmlfromlm USING p_filename xmlstring TYPE string .
  DATA retfiletable TYPE filetable.
  DATA retrc TYPE sysubrc.
  DATA retuseraction TYPE i.
  DATA temptable TYPE table_of_strings.
  DATA temptable_bin TYPE TABLE OF x255.
  DATA filelength TYPE i.
  DATA l_filename TYPE string.

  l_filename = p_filename.
  CALL METHOD cl_gui_frontend_services=>gui_upload
    EXPORTING
      filename                = l_filename
      filetype                = 'BIN'       " File Type Binary
    IMPORTING
      filelength              = filelength
    CHANGING
      data_tab                = temptable_bin
    EXCEPTIONS
      file_open_error         = 1
      file_read_error         = 2
      no_batch                = 3
      gui_refuse_filetransfer = 4
      invalid_type            = 5
      no_authority            = 6
      unknown_error           = 7
      bad_data_format         = 8
      header_not_allowed      = 9
      separator_not_allowed   = 10
      header_too_long         = 11
      unknown_dp_error        = 12
      access_denied           = 13
      dp_out_of_memory        = 14
      disk_full               = 15
      dp_timeout              = 16
      not_supported_by_gui    = 17
      error_no_gui            = 18
      OTHERS                  = 19.
  IF sy-subrc <> 0.
    CASE sy-subrc.
      WHEN '1'.
        PERFORM writemessage USING 'E' 'File Open Error'.
      WHEN OTHERS.
        PERFORM writemessage USING 'E' 'Unknown Error occured'.
    ENDCASE.
  ENDIF.

  CALL FUNCTION 'SCMS_BINARY_TO_STRING'
    EXPORTING
      input_length = filelength
    IMPORTING
      text_buffer  = xmlstring
    TABLES
      binary_tab   = temptable_bin.
  IF sy-subrc <> 0.
    " Just catch the sy-subrc when there was nothing replaced
    sy-subrc = 0.
  ENDIF.
*  call method CL_GUI_FRONTEND_SERVICES=>GUI_UPLOAD
*        exporting
*          FILENAME = l_fileName
*        changing
*          data_tab = tempTable.
*  PERFORM createstring USING temptable CHANGING xmlstring.

ENDFORM.                    "uploadXMLFromLM
*\--------------------------------------------------------------------/

**/----------------------createString----------------------------------\
*FORM createstring
*      USING
*        temptable TYPE table_of_strings
*      CHANGING
*        bigstring TYPE string.
*  DATA stemp TYPE string.
*  LOOP AT temptable INTO stemp.
*    CONCATENATE bigstring stemp cl_abap_char_utilities=>cr_lf
*      INTO bigstring.
*  ENDLOOP.
*
*ENDFORM.                    "createString
**\--------------------------------------------------------------------/

*/----------------------installObject---------------------------------\
FORM installobject USING l_ixmldocument TYPE REF TO if_ixml_document
                         l_overwriteflag TYPE flag
                         value(l_package) TYPE tadir-devclass
                CHANGING l_errorflag TYPE flag
                         l_message TYPE string.

  DATA l_objname TYPE string.
  DATA l_objtype TYPE string.
  DATA l_objtable TYPE TABLE OF t_objecttable.
  DATA l_objline TYPE t_objecttable.
  DATA l_targetobject TYPE REF TO zsaplink.
  DATA l_installobject TYPE string.
  DATA l_excclass TYPE REF TO zcx_saplink.

  CLEAR l_errorflag.
  CALL METHOD zsaplink=>getobjectinfofromixmldoc
    EXPORTING
      ixmldocument = l_ixmldocument
    IMPORTING
      objtypename  = l_objtype
      objname      = l_objname.

  CALL METHOD zsaplink=>getplugins( CHANGING objecttable = l_objtable ).

  READ TABLE l_objtable INTO l_objline WITH KEY object = l_objtype.

  IF sy-subrc <> 0.
    CONCATENATE 'There is no installed SAPlink plugin for object type'
      l_objtype l_objline-text INTO l_message SEPARATED BY space.
    l_errorflag = 'X'.
  ELSE.
    CREATE OBJECT l_targetobject
      TYPE
      (l_objline-classname)
      EXPORTING
        name = l_objname.
    IF l_package IS INITIAL.
      l_package = '$TMP'.
    ENDIF.
    TRY.
        l_installobject = l_targetobject->createobjectfromixmldoc(
                                        ixmldocument = l_ixmldocument
                                        devclass     = l_package  "Allow overwrite of default devclass.
                                        overwrite    = l_overwriteflag ).
*    bad times
      CATCH zcx_saplink INTO l_excclass.
        l_message = l_excclass->get_text( ).
        l_errorflag = 'X'.
    ENDTRY.
*   good times
    IF l_installobject IS NOT INITIAL.
      CONCATENATE 'Installed: ' l_objtype '-' l_installobject
       INTO l_message SEPARATED BY space.
    ENDIF.
  ENDIF.

ENDFORM.                    "installObject
*\--------------------------------------------------------------------/

*/----------------------confirmOverwrite------------------------------\
FORM confirmoverwrite USING l_objinfo TYPE string
                   CHANGING l_answer TYPE flag.

  DATA l_message TYPE string.
  DATA l_title TYPE string.

  CLEAR l_answer.
  l_title = 'Overwrite confirm. Proceed with CAUTION!'.

  CONCATENATE 'You have selected to overwrite originals.'
    l_objinfo 'will be overwritten. Are you sure?'
    INTO l_message SEPARATED BY space.

  CALL FUNCTION 'POPUP_TO_CONFIRM'
    EXPORTING
      titlebar              = l_title
      text_question         = l_message
      text_button_1         = 'Yes'
      text_button_2         = 'Yes to all'
      default_button        = '1'
      display_cancel_button = 'X'
    IMPORTING
      answer                = l_answer.
ENDFORM.                    "confirmOverwrite
*\--------------------------------------------------------------------/

*/----------------------checkObject-----------------------------------\
FORM checkobject USING l_ixmldocument TYPE REF TO if_ixml_document
              CHANGING l_objtype      TYPE string
                       l_objname      TYPE string
                       l_pluginexists TYPE flag
                       l_objectexists TYPE flag.

  zsaplink=>checkobject(
    EXPORTING
      i_ixmldocument = l_ixmldocument    " IF_IXML_DOCUMENT
    IMPORTING
      e_objtype      = l_objtype
      e_objname      = l_objname
      e_pluginexists = l_pluginexists    " General Flag
      e_objectexists = l_objectexists    " General Flag
  ).

ENDFORM.                    "checkObject
*\--------------------------------------------------------------------/

*/---------------------get_current_screen_value-----------------------\
FORM get_current_screen_value  USING    l_screen_field
                                        l_screen_number
                               CHANGING l_screen_value.

  DATA it_dynpfields TYPE STANDARD TABLE OF dynpread.
  DATA wa_dynpfields TYPE dynpread.


  wa_dynpfields-fieldname = l_screen_field.
  APPEND wa_dynpfields TO it_dynpfields.


  CALL FUNCTION 'DYNP_VALUES_READ'
    EXPORTING
      dyname                   = sy-cprog
      dynumb                   = l_screen_number
      translate_to_upper       = 'X'
*     REQUEST                  = ' '
*     PERFORM_CONVERSION_EXITS = ' '
*     PERFORM_INPUT_CONVERSION = ' '
*     DETERMINE_LOOP_INDEX     = ' '
    TABLES
      dynpfields               = it_dynpfields
    EXCEPTIONS
      invalid_abapworkarea     = 1
      invalid_dynprofield      = 2
      invalid_dynproname       = 3
      invalid_dynpronummer     = 4
      invalid_request          = 5
      no_fielddescription      = 6
      invalid_parameter        = 7
      undefind_error           = 8
      double_conversion        = 9
      stepl_not_found          = 10
      OTHERS                   = 11.
  IF sy-subrc <> 0.
*  MESSAGE ID SY-MSGID TYPE SY-MSGTY NUMBER SY-MSGNO
*  WITH SY-MSGV1 SY-MSGV2 SY-MSGV3 SY-MSGV4.
  ELSE.
    READ TABLE it_dynpfields INTO wa_dynpfields
      WITH KEY fieldname = l_screen_field.
    IF sy-subrc = 0.
      l_screen_value = wa_dynpfields-fieldvalue.
    ENDIF.
  ENDIF.


ENDFORM.                    " get_current_screen_value
*/---------------------writeMessage-----------------------\
FORM writemessage USING value(p_type) TYPE sy-msgty
                        value(p_msg).
  CASE p_type.
    WHEN 'E' OR 'A' OR 'X'.
      WRITE / icon_led_red AS ICON.
    WHEN 'W'.
      WRITE / icon_led_yellow AS ICON.
    WHEN OTHERS.
      WRITE / icon_led_green AS ICON.
  ENDCASE.

  WRITE p_msg.
ENDFORM.                    "WriteMessage

*/-------------------------pf_status_set-------------------\
FORM pf_status_set USING rt_extab TYPE slis_t_extab.

  SET PF-STATUS 'SELOBJ' EXCLUDING rt_extab.

ENDFORM.                    "pf_status_set
*/-------------------------user_command_user-------------------\
FORM user_command_user USING r_ucomm LIKE sy-ucomm
                  rs_selfield TYPE slis_selfield.
  CASE r_ucomm.
    WHEN 'TAKE'.
      rs_selfield-exit = 'X'.
  ENDCASE.
ENDFORM.                    "user_command_user

*---------------build_fieldCatalog---------------------------------*
FORM build_fieldcatalog .
*** Display list to select the objects for downloading
  ls_fieldcat-fieldname = 'OBJECT'.
  ls_fieldcat-seltext_l = 'Object/Plugin'.
  APPEND ls_fieldcat TO lt_fieldcat.

  ls_fieldcat-fieldname = 'OBJECT_TXT'.
  ls_fieldcat-seltext_l = 'Object/Plugin'.
  APPEND ls_fieldcat TO lt_fieldcat.

  ls_fieldcat-fieldname = 'OBJ_NAME'.
  ls_fieldcat-seltext_l = 'Object name'.
  APPEND ls_fieldcat TO lt_fieldcat.

  ls_fieldcat-fieldname = 'DOWN_FLAG'.
  ls_fieldcat-seltext_s = 'Plugin'.
  ls_fieldcat-seltext_l =
  'Plugin available'.
  APPEND ls_fieldcat TO lt_fieldcat.

  ls_fieldcat-fieldname = 'MSG'.
  ls_fieldcat-seltext_s = 'Message'.
  ls_fieldcat-seltext_l =
  'Status Message'.
  APPEND ls_fieldcat TO lt_fieldcat.

  ls_layout-box_fieldname     = 'SELECT'.
  ls_layout-f2code            = 'MYPICK' .
  ls_layout-colwidth_optimize = 'X'.
  ls_layout-lights_fieldname  = 'STATUS'.
ENDFORM.                    " build_fieldCatalog
*&--------------------------------------------------------------------*
*&      Form  ShowInitialGrid
FORM showinitialgrid  TABLES   p_objects.

  CALL FUNCTION 'REUSE_ALV_GRID_DISPLAY'
    EXPORTING
      i_callback_program       = 'ZSAPLINK'
      i_callback_pf_status_set = 'PF_STATUS_SET'
      i_callback_user_command  = 'USER_COMMAND_USER'
      i_grid_title             = 'Select objects'
      it_fieldcat              = lt_fieldcat
      is_layout                = ls_layout
    TABLES
      t_outtab                 = p_objects
    EXCEPTIONS
      OTHERS                   = 0.

ENDFORM.                    " ShowInitialGrid
*&---------------------------------------------------------------------*
*&      Form  showResultsGrid
FORM showresultsgrid  TABLES   p_objects.
*    ** Display results
  CALL FUNCTION 'REUSE_ALV_GRID_DISPLAY'
    EXPORTING
      i_callback_program      = 'ZSAPLINK'
      i_callback_user_command = 'USER_COMMAND_USER'
      it_fieldcat             = lt_fieldcat
      i_grid_title            = 'Download results'
      is_layout               = ls_layout
    TABLES
      t_outtab                = p_objects
    EXCEPTIONS
      OTHERS                  = 0.

ENDFORM.                    " showResultsGrid
*&---------------------------------------------------------------------*
*&      Form  check_objects
*&---------------------------------------------------------------------*
*       text
*----------------------------------------------------------------------*
*  -->  p1        text
*  <--  p2        text
*----------------------------------------------------------------------*
FORM check_objects .
  DATA: lo_object   TYPE REF TO object,
        lo_saplink  TYPE REF TO zsaplink,
        l_flag      TYPE flag,
        l_name      TYPE string.
  DATA: ptab TYPE abap_parmbind_tab,
        ptab_line TYPE abap_parmbind.
  LOOP AT objects_package ASSIGNING <obj>.
*     Check what can be downloaded and what can not.
    READ TABLE objecttable INTO objectline
        WITH KEY object = <obj>-object.
    IF sy-subrc = 0.
* Validate if object is fully implemented and can be accessed
      REFRESH ptab.
      ptab_line-name = 'NAME'.
      ptab_line-kind = cl_abap_objectdescr=>exporting.
      l_name = <obj>-obj_name.
      GET REFERENCE OF l_name
              INTO ptab_line-value.
      INSERT ptab_line INTO TABLE ptab.

      CREATE OBJECT lo_object
        TYPE
        (objectline-classname)
        PARAMETER-TABLE
        ptab.
      lo_saplink ?= lo_object.
      l_flag = lo_saplink->checkexists( ).
*        Plug-in exists... set flag and make selected by default
      IF l_flag IS NOT INITIAL.
        <obj>-down_flag = 'X'.
        <obj>-select = 'X'.
      ELSE.
        DELETE TABLE objects_package FROM <obj>.
        CONTINUE.
      ENDIF.
    ELSE.
      <obj>-msg = 'No Plugin Available'.
      <obj>-down_flag = ' '.
    ENDIF.
*     get texts
    REFRESH tabletypesin.
    tabletypeline-object = <obj>-object.
    APPEND tabletypeline TO tabletypesin.

    CALL FUNCTION 'TRINT_OBJECT_TABLE'
      TABLES
        tt_types_in  = tabletypesin
        tt_types_out = tabletypesout.

    LOOP AT tabletypesout INTO tabletypeoutline.
      <obj>-object      = tabletypeoutline-object.
      <obj>-object_txt = tabletypeoutline-text.
    ENDLOOP.

  ENDLOOP.
  SORT objects_package BY down_flag DESCENDING object ASCENDING.

ENDFORM.                    " check_objects
*&---------------------------------------------------------------------*
*&      Form  CreateEmptyNugget
*&---------------------------------------------------------------------*
*       text
*----------------------------------------------------------------------*
*  -->  p1        text
*  <--  p2        text
*----------------------------------------------------------------------*
FORM createemptynugget USING p_nuggname.

  ixmldocument = zsaplink_nugget=>createemptyxml(
    nuggetname = p_nuggname ).
  xml = zsaplink=>convertixmldoctostring( ixmldocument ).
  CONCATENATE 'NUGG_' p_nuggname '.nugg' INTO stemp.
  CLEAR errorflag.
  PERFORM downloadxmltolm USING stemp xml
                          CHANGING errorflag.
  IF errorflag IS NOT INITIAL.
    EXIT.
  ENDIF.

ENDFORM.                    " CreateEmptyNugget

*rrq: enhancement 3 & 42-->
*&---------------------------------------------------------------------*
*&      Form  addObjectstoNugget
*&---------------------------------------------------------------------*
FORM addobjectstonugget .

  PERFORM check_objects.
  PERFORM build_fieldcatalog.

  PERFORM showinitialgrid TABLES objects_package.

  IF sy-ucomm <> 'TAKE'.
    RETURN.
  ENDIF .

*  Downloading
  CALL FUNCTION 'SAPGUI_PROGRESS_INDICATOR'
    EXPORTING
      percentage = 1
      text       = 'Upload file'.

  PERFORM uploadxmlfromlm USING nugfile xml.

  ixmldocument = zsaplink=>convertstringtoixmldoc( xml ).

  CREATE OBJECT nugg
    EXPORTING
      ixmldocument = ixmldocument.

  DESCRIBE TABLE objects_package LINES lv_count.
  LOOP AT objects_package ASSIGNING <obj>
  WHERE down_flag = 'X' AND select = 'X'.
    lv_pers = sy-tabix * 100 / lv_count .
    CALL FUNCTION 'SAPGUI_PROGRESS_INDICATOR'
      EXPORTING
        percentage = lv_pers
        text       = <obj>-obj_name.

    _objname = <obj>-obj_name. "nobjNam.
    stemp = <obj>-object.      "nplugin.
    TRY.
        nugg->addobjecttonugget(
        objname = _objname objtype = stemp ).
      CATCH zcx_saplink INTO excclass.
        errormsg = excclass->get_text( ).
*        perform writeMessage using 'E' errorMsg.
        <obj>-msg = errormsg.
        <obj>-status = 1.
        CONTINUE.
    ENDTRY.
    <obj>-msg = 'Added to nugget'.
    <obj>-status = 3.
  ENDLOOP.

  READ TABLE objects_package INTO packageline
    WITH KEY status = 3. "ewH:do not download if none added

  IF sy-subrc = 0.
    ixmldocument = nugg->createixmldocfromnugget( ).
    xml = zsaplink=>convertixmldoctostring( ixmldocument ).
    stemp = nugfile.
    PERFORM putonmachine USING stemp xml.
  ENDIF.

  PERFORM showresultsgrid TABLES objects_package.

ENDFORM.                    " addObjectstoNugget
*  <--rrq: enhancement 3 & 42