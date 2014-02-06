using System;
using System.Drawing;
using System.Collections;
using System.ComponentModel;
using System.Windows.Forms;

namespace NUnit.UiKit
{
	/// <summary>
	/// Summary description for TestForm.
	/// </summary>
	public class TestForm : System.Windows.Forms.Form
	{
		private NUnit.UiKit.ResultTabs resultTabs;
		/// <summary>
		/// Required designer variable.
		/// </summary>
		private System.ComponentModel.Container components = null;

		public TestForm()
		{
			//
			// Required for Windows Form Designer support
			//
			InitializeComponent();

			//
			// TODO: Add any constructor code after InitializeComponent call
			//
		}

		/// <summary>
		/// Clean up any resources being used.
		/// </summary>
		protected override void Dispose( bool disposing )
		{
			if( disposing )
			{
				if(components != null)
				{
					components.Dispose();
				}
			}
			base.Dispose( disposing );
		}

		#region Windows Form Designer generated code
		/// <summary>
		/// Required method for Designer support - do not modify
		/// the contents of this method with the code editor.
		/// </summary>
		private void InitializeComponent()
		{
			this.resultTabs = new NUnit.UiKit.ResultTabs();
			this.SuspendLayout();
			// 
			// resultTabs
			// 
			this.resultTabs.Dock = System.Windows.Forms.DockStyle.Fill;
			this.resultTabs.Location = new System.Drawing.Point(0, 0);
			this.resultTabs.Name = "resultTabs";
			this.resultTabs.Size = new System.Drawing.Size(824, 536);
			this.resultTabs.TabIndex = 0;
			// 
			// TestForm
			// 
			this.AutoScaleBaseSize = new System.Drawing.Size(6, 15);
			this.ClientSize = new System.Drawing.Size(824, 536);
			this.Controls.Add(this.resultTabs);
			this.Name = "TestForm";
			this.Text = "TestForm";
			this.ResumeLayout(false);

		}
		#endregion
	}
}
