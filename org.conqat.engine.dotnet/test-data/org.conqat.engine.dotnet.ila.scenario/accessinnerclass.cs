using System;

namespace edu.tum.cs.conqat.dotnet {

    public class Target {
        public class InTarget
        {
        }
    }

    // Depends on class Target due to inheritance
    public class Source {
        internal void Method()
        {
            Object o = new Target.InTarget();
        }
    }

}