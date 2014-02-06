using System;
namespace edu.tum.cs.conqat.dotnet {

    public class Target {
        
    }    
    
    
    public class SomeAttribute : System.Attribute{
        public String type;
    }

    public class Source {

        [SomeAttribute(type = "edu.tum.cs.conqat.dotnet.Target")]
        private void MyMethod()
        {
            
        }
    }

}