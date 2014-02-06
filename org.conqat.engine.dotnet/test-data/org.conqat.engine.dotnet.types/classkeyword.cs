namespace A {

    public abstract class Controller<CONTEXTOBJ> : Parent
        where CONTEXTOBJ : class, ILockable, new()
    {
        #region Const & Fields
        private CONTEXTOBJ _contextObject;
        #endregion

        #region Properties
        public CONTEXTOBJ ContextObject
        {
            get
            {
                
                return _contextObject;
            }
            set
            {
                _contextObject = value;

                
            }
        }
        #endregion
    }//class
}//namespace
