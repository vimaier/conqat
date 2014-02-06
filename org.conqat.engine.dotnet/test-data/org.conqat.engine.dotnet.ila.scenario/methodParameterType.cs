using System;

namespace edu.tum.cs.conqat.dotnet {

    public class Target {
    }

    public class SomeClass
    {
        public static Target myTarget = new Target();

        public void SomeMethod(Target callee)
        {

        }
    }

    // Depends on class Target due to method parameter
    public class Source {
        public void SourceMethod()
        {
            SomeClass x = new SomeClass();
            x.SomeMethod(SomeClass.myTarget);
        }
    }

}