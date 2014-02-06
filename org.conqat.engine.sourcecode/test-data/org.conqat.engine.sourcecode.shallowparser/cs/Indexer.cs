using System;

namespace NUnit.Util
{
	public class AssemblyWatcher: IList<Item>
	{
		// Interesting language feature: indexer with explicit overriding interface
		IItem IList<Item>.this[int index] {
			get { return Items[index]; }
			set { Items[index] = value;}		
		}
	}
}