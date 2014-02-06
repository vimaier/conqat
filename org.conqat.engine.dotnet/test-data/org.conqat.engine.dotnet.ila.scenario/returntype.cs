using System;

namespace edu.tum.cs.conqat.dotnet {

    public class Target {
    }

    public class SubTarget : Target
    {
    }

    // Depends on class Target due to method parameter
    public class Source {

        public Target MyMethod() {
            return new SubTarget();
        }
    }

}