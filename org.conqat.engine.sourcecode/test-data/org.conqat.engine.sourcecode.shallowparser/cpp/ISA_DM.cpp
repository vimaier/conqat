// Sample code as used in the context of the ISA dialog mananger

// main method
int DML_c DM_CALLBACK AppMain __2((int, argc), (char **, argv))
{
	int a;
	// do something	
}
 
DM_Integer DML_c DM_ENTRY InitFunctions( DM_ID Dialog )
{
	DM_Boolean Rc = FALSE;

	Rc = init_fkts( Dialog );
	if( Rc == TRUE )
		return( 0 );
	else
		return( 1 );
}

LONG DML_pascal DM_CALLBACK MyFilter(DM_ID ObjektID, MSG *msg)
{
	// callback
}

DM_Boolean DML_default DM_ENTRY ActivateWindow __1(
	(DM_ID, window) )			/* ID of the window */
{
	// activation function
}
