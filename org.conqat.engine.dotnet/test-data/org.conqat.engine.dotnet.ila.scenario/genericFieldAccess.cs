using System.Collections.Generic;
using System;
namespace edu.tum.cs.conqat.dotnet {

    public class Target {
    }

    public class SomeClass {
        public static List<Target> field = new List<Target>();
    }

    // Depends on class Target due to method parameter
    public class Source {
        void someMethod() {
            String x = SomeClass.field.ToString();
        }
    }

}