namespace Namespace.Subnamespace
{
    public class ExceptionHandler
    {
        public static event HandleException ExceptionOccurred
        {
            add
            {
                    
                if (!_exceptionHandler.Contains(value))
                    _exceptionHandler.Add(value);
            }
            remove
            {
                if (_exceptionHandler.Contains(value))
                    _exceptionHandler.Add(value);
            }
        }
    }
}