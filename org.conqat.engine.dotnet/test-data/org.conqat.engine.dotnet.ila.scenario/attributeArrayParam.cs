using System;
namespace edu.tum.cs.conqat.dotnet {

    public class Target {
        
    }    
    
    
    public class SomeAttribute : System.Attribute{
        public SomeAttribute(System.Type[] types) { }
    }

    public class Source {

        [SomeAttribute(new Type[] { typeof(String), typeof(Target) })]
        private void MyMethod()
        {

        }
    }

}