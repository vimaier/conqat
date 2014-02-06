using System;
using System.IO;

namespace NUnit.TestUtilities
{
	/// <summary>
	/// Summary description for TempTestAssembly.
	/// </summary>
	public class TempTestAssembly : IDisposable
	{
		string code;
		string name;
		string path;

		public TempTestAssembly( string code ) : this( code, "test.dll" ) { }

		public TempTestAssembly( string code, string name ) : this( code, name, name ) { }

		public TempTestAssembly( string code, string name, string path )
		{
			this.code = code;
			this.name = name;
			this.path = path;
		}

		public void Dispose()
		{
			File.Delete( this.path );

			string path = this.path;
			while(true)
			{
				path = Path.GetDirectoryName(path);
				if(path == null || path.Length == 0 || Directory.GetFiles(path).Length > 0)
				{
					break;
				}

				Directory.Delete(path);
			}
		}
	}
}
