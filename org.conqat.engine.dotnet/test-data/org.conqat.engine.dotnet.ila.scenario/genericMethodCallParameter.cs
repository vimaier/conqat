using System;

namespace edu.tum.cs.conqat.dotnet {

    public class Target {
    }

    public class SomeOtherClass { }

    public class SomeClass
    {
        public void SomeMethod<T, B>(){
        }
    }

    // Depends on class Target due to method parameter
    public class Source {
        public void caller() {
            SomeClass x = new SomeClass();
            x.SomeMethod<Target, SomeOtherClass>();
        }
    }

}