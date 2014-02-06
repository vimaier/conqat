using System;

namespace edu.tum.cs.conqat.dotnet {

    public class SomeClass<T> {
    }
    public class Target {
    }

    public class Source {
        public void caller() {
            SomeClass<Target>[] x = new SomeClass<Target>[4];
        }
    }

}