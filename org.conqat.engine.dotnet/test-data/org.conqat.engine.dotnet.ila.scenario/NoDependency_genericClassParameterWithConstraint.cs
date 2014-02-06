using System.Collections.Generic;
using System;

namespace edu.tum.cs.conqat.dotnet {

    public class SomeClass { }

    public class Source<Target> where Target : SomeClass {
        Target SomeMethod() {
            return (Target)new SomeClass();
        }
    }

}