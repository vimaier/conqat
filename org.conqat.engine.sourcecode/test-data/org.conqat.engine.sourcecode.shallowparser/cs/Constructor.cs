using System;

namespace NUnit.Util
{
	public class AssemblyWatcher
	{
		// Interesting language feature: constructor call in base constructor call
		public AssemblyWatcher(string message, Exception inner, string name) 
			: base(message, inner, new List<String>{ name }) 
			{ }
		
		// Interesting language feature: array initializer in base constructor call
		public AssemblyWatcher() 
			: base(new string[] {"Hallo", "Du"}) 
			{ }
	}
}
