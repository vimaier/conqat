using System;
using System.Collections.Generic;
namespace edu.tum.cs.conqat.dotnet {

    public class Target {
    }

    public class SomeClass<A, T>
    {

    }

    public class Y { }
    public class X { }
    public class Z { }

    // Depends on class Target due to method parameter
    public class Source {
        public void caller() {
            SomeClass<Dictionary<X, List<Y>>, Dictionary<Z, Target>> x = new SomeClass<Dictionary<X, List<Y>>, Dictionary<Z, Target>>();
        }
    }

}