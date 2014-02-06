using System;
using System.Windows.Forms;
using NUnit.Framework;

namespace NUnit.UiKit.Tests
{
	[TestFixture]
	public class TextBoxTests
	{
		private TextBox textBox;
		
		[SetUp]
		public void Init()
		{
			textBox = new TextBox();
			textBox.Multiline=false;
			textBox.CreateControl();
		}

		public void CleanUp()
		{
			textBox.Dispose();
		}

		[Test]
		public void InitialState()
		{
			Assert.AreEqual( "", textBox.Text );
			Assert.AreEqual( 0, textBox.Lines.Length );
			Assert.AreEqual( false, textBox.Multiline );
		}

		[Test]
		public void AppendText()
		{
			textBox.AppendText( "AAAA" );
			textBox.AppendText( "BBB" );
			textBox.AppendText( "CCCCC" );
			Assert.AreEqual( "AAAABBBCCCCC", textBox.Text );
			Assert.AreEqual( 1, textBox.Lines.Length );
			Assert.AreEqual( "AAAABBBCCCCC", textBox.Lines[0] );
		}

		[Test]
		public void AppendTextPlusCR()
		{
			textBox.AppendText( "AAAA\r" );
			textBox.AppendText( "BBB\r" );
			textBox.AppendText( "CCCCC\r" );
//			Assert.AreEqual( "AAAA\rBBB\rCCCCC\r", textBox.Text );
			Assert.AreEqual( 4, textBox.Lines.Length );
			Assert.AreEqual( "AAAA", textBox.Lines[0] );
			Assert.AreEqual( "BBB", textBox.Lines[1] );
			Assert.AreEqual( "CCCCC", textBox.Lines[2] );
		}

		[Test]
		public void AppendTextPlusLF()
		{
			textBox.AppendText( "AAAA\n" );
			textBox.AppendText( "BBB\n" );
			textBox.AppendText( "CCCCC\n" );
//			Assert.AreEqual( "AAAA\nBBB\nCCCCC\n", textBox.Text );
			Assert.AreEqual( 4, textBox.Lines.Length );
			Assert.AreEqual( "AAAA", textBox.Lines[0] );
			Assert.AreEqual( "BBB", textBox.Lines[1] );
			Assert.AreEqual( "CCCCC", textBox.Lines[2] );
		}

		[Test]
		public void AppendTextPlusCRLF()
		{
			textBox.AppendText( "AAAA\r\n" );
			textBox.AppendText( "BBB\r\n" );
			textBox.AppendText( "CCCCC\r\n" );
//			Assert.AreEqual( "AAAA\r\nBBB\r\nCCCCC\r\n", textBox.Text );
			Assert.AreEqual( 4, textBox.Lines.Length );
			Assert.AreEqual( "AAAA", textBox.Lines[0] );
			Assert.AreEqual( "BBB", textBox.Lines[1] );
			Assert.AreEqual( "CCCCC", textBox.Lines[2] );
		}
	}
}
