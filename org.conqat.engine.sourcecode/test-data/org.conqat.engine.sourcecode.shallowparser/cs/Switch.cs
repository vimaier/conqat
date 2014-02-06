using System;

namespace NUnit.Util
{
	public class AssemblyWatcher
	{
		public boolean DoIt(HttpWebResponse response) {
		
			// interesting language feature: Fully qualified constants in case
			switch(response.StatusCode)
			{
				case HttpStatusCode.Accepted:
				case HttpStatusCode.Ambiguous:
				case HttpStatusCode.Forbidden:
				{
					return true;			
				}
				default:
				{
					return false;
				}
			}
			
		}
	}
}
