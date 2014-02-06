using System;
namespace edu.tum.cs.conqat.dotnet {

    public class Target {
        
    }    
    
    
    public class SomeAttribute : System.Attribute{
        public System.Type type;
    }

    public class Source {

        [SomeAttribute(type = typeof(Target))]
        private void MyMethod()
        {
            
        }
    }

}