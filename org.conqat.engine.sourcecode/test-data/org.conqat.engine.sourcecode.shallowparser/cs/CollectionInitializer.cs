// examples inspired from http://msdn.microsoft.com/en-us/library/bb531208.aspx

class Foo
{
    // Auto-implemented properties. 
    public int X { get; set; }
    
    Dictionary<int, StudentName> students = new Dictionary<int, StudentName>()
	{
	    { 111, new StudentName {FirstName="Sachin", LastName="Karnik", ID=211}},
	    { 112, new StudentName {FirstName="Dina", LastName="Salimzianova", ID=317}},
	    { 113, new StudentName {FirstName="Andy", LastName="Ruth", ID=198}}
	};
    
    List<int> digits = new List<int> { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
    
    public void TestMethod(int input)
    {

	    Dictionary<int, StudentName> students = new Dictionary<int, StudentName>()
		{
		    { 111, new StudentName {FirstName="Sachin", LastName="Karnik", ID=211}},
		    { 112, new StudentName {FirstName="Dina", LastName="Salimzianova", ID=317}},
		    { 113, new StudentName {FirstName="Andy", LastName="Ruth", ID=198}}
		};
    
		var pet = new { Age = 10, Name = "Fluffy" };
		
    	List<int> digits = new List<int> { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
    }
}

