using System;

namespace edu.tum.cs.conqat.dotnet {

    public class Target {
    }

    public class SomeClass<T>
    {

    }

    // Depends on class Target due to method parameter
    public class Source {
        public void caller() {
            SomeClass<Target> x = new SomeClass<Target>();
        }
    }

}