
namespace Namespace.Subnamespace
{
    public class WindowListener
    {
        protected void WndProc(Message m)
        {
            if (_mainForm is Form)
                ((MainPc)((Form)_mainForm).Pc).DoOpenProgramBySystemMessage( param.Split( new char[] { ' ' } ) );
            else
                ((MainPc)((MainFormControl)_mainForm).Pc).DoOpenProgramBySystemMessage(param.Split(new char[] { ' ' }));
        }        
	}
}
