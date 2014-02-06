using System.Collections.Generic;
using System;

namespace edu.tum.cs.conqat.dotnet {

    public class SomeClass {
        public void doSomething() {
        }
    }
    public class SubSomeClass : SomeClass {
    }

    public class SourceBase<Target> where Target : SomeClass {
        public Target SomeMember;
        public Target anyMethod() {
            return (Target) new SomeClass();
        }
    }

    public class Source : SourceBase<SomeClass> {

        void AnyMethod() {
            SomeMember.doSomething();
            anyMethod().doSomething();
        }
    }

}