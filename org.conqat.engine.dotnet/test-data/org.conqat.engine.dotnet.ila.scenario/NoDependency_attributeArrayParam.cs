using System;
namespace edu.tum.cs.conqat.dotnet {

    public class Target {
        
    }    
    
    
    public class SomeAttribute : System.Attribute{
        public SomeAttribute(System.String[] types) { }
    }

    public class Source {

        [SomeAttribute(new String[] { "lala", "edu.tum.cs.conqat.dotnet.Target" })]
        private void MyMethod()
        {

        }
    }

}