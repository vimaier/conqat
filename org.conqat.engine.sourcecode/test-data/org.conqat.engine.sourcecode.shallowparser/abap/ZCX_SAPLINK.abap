class ZCX_SAPLINK definition
  public
  inheriting from CX_STATIC_CHECK
  create public .

public section.
*"* public components of class ZCX_SAPLINK
*"* do not include other source files here!!!

  constants EXISTING type SOTR_CONC value '0003FFD79BE81EE295981D86AD3C909B'. "#EC NOTEXT
  constants SYSTEM_ERROR type SOTR_CONC value '0003FFD79BE81EE295981D86AD3D309B'. "#EC NOTEXT
  constants NOT_AUTHORIZED type SOTR_CONC value '0003FFD79BE81EE295981D86AD3CD09B'. "#EC NOTEXT
  constants ERROR_MESSAGE type SOTR_CONC value '0003FFD79BE81EE295981D86AD3C709B'. "#EC NOTEXT
  constants ZCX_SAPLINK type SOTR_CONC value '0003FFD79BE81EE295981D86AD3D509B'. "#EC NOTEXT
  data MSG type STRING value '44F7518323DB08BC02000000A7E42BB6'. "#EC NOTEXT .
  constants NOT_FOUND type SOTR_CONC value '0003FFD79BE81EE295981D86AD3CF09B'. "#EC NOTEXT
  constants LOCKED type SOTR_CONC value '0003FFD79BE81EE295981D86AD3CB09B'. "#EC NOTEXT
  constants NO_PLUGIN type SOTR_CONC value '0003FFD79BE81EE295981D86AD3D109B'. "#EC NOTEXT
  data OBJECT type STRING .

  methods CONSTRUCTOR
    importing
      !TEXTID like TEXTID optional
      !PREVIOUS like PREVIOUS optional
      !MSG type STRING default '44F7518323DB08BC02000000A7E42BB6'
      !OBJECT type STRING optional .
protected section.
*"* protected components of class ZCX_SAPLINK
*"* do not include other source files here!!!
private section.
*"* private components of class ZCX_SAPLINK
*"* do not include other source files here!!!
ENDCLASS.



CLASS ZCX_SAPLINK IMPLEMENTATION.


* <SIGNATURE>---------------------------------------------------------------------------------------+
* | Instance Public Method ZCX_SAPLINK->CONSTRUCTOR
* +-------------------------------------------------------------------------------------------------+
* | [--->] TEXTID                         LIKE        TEXTID(optional)
* | [--->] PREVIOUS                       LIKE        PREVIOUS(optional)
* | [--->] MSG                            TYPE        STRING (default ='44F7518323DB08BC02000000A7E42BB6')
* | [--->] OBJECT                         TYPE        STRING(optional)
* +--------------------------------------------------------------------------------------</SIGNATURE>
method CONSTRUCTOR.
CALL METHOD SUPER->CONSTRUCTOR
EXPORTING
TEXTID = TEXTID
PREVIOUS = PREVIOUS
.
 IF textid IS INITIAL.
   me->textid = ZCX_SAPLINK .
 ENDIF.
me->MSG = MSG .
me->OBJECT = OBJECT .
endmethod.
ENDCLASS.