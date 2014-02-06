using System;
using NUnit.Framework;

namespace NUnit.Tests.TimingTests
{
	/// <summary>
	/// Summary description for ConsoleOutputTimingFixture.
	/// </summary>
	[TestFixture]
	public class ConsoleOutputTimingFixture
	{
		[Test]
		public void Write1000Lines()
		{
			int start = Environment.TickCount;
			
			for( int i = 1; i < 1000; ++ i ) 
			{ 
				Console.WriteLine( 
					"Console output line number: {0}", 
					i ); 
			}

			int elapsed = Environment.TickCount - start;
			Console.WriteLine( "*** Elapsed Time: {0} milliseconds", elapsed );
			Assert.IsTrue( elapsed <=500, string.Format( "Too slow: {0} milliseconds", elapsed ) );
		}

		[Test]
		public void Write10000Lines()
		{
			int start = Environment.TickCount;
			
			for( int i = 1; i < 10000; ++ i ) 
			{ 
				Console.WriteLine( 
					"Console output line number: {0}", 
					i ); 
			}

			int elapsed = Environment.TickCount - start;
			Console.WriteLine( "*** Elapsed Time: {0} milliseconds", elapsed );
			Assert.IsTrue( elapsed <=5000, string.Format( "Too slow: {0} milliseconds", elapsed ) );
		}

		[Test]
		public void Write1000LinesInPieces()
		{
			int start = Environment.TickCount;
			
			for( int i = 1; i < 1000; ++ i ) 
			{ 
				Console.Write( "Console" );
				Console.Write( ' ' );
				Console.Write( "output" );
				Console.Write( ' ' );
				Console.Write( "line" );
				Console.Write( ' ' );
				Console.Write( "number" );
				Console.Write( ':' );
				Console.WriteLine( "{0}", i );
			}

			int elapsed = Environment.TickCount - start;
			Console.WriteLine( "*** Elapsed Time: {0} milliseconds", elapsed );
			Assert.IsTrue( elapsed <=500, string.Format( "Too slow: {0} milliseconds", elapsed ) );
		}
	}
}
