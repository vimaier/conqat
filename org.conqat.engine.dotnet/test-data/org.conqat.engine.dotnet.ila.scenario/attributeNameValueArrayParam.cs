using System;
namespace edu.tum.cs.conqat.dotnet {

    public class Target {
        
    }    
    
    
    public class SomeAttribute : System.Attribute{
        public System.Type[] types;
    }

    public class Source {

        [SomeAttribute(types = new Type[] { typeof(String), typeof(Target) })]
        private void MyMethod()
        {

        }
    }

}