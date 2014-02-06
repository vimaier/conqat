create or replace type Bar
as object (
	au VARCHAR(4)
	, au2 VARCHAR(40)
	
	, STATIC FUNCTION INIT 
	  RETURN GTX
	  
	, CONSTRUCTOR FUNCTION GTY
	( SELF IN OUT GTY
	)
	  RETURN SELF AS RESULT
	  
	, MEMBER PROCEDURE get_xy
	( PI1 IN VARCHAR2
	, PO1 OUT NUMBER
	)
	
)
/
sho err
