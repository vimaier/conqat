using System.Collections.Generic;

namespace edu.tum.cs.conqat.dotnet {

    public class Target {
    }

    public class SomeClass
    {
        public List<Target> someMethod()
        {
            return new List<Target>();
        }
    }


    public class Source {
        public void Caller() {
            SomeClass x = new SomeClass();
            List<Target> y = x.someMethod();
        }
    }

}