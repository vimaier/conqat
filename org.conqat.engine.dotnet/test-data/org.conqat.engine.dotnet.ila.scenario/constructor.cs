using System;

namespace edu.tum.cs.conqat.dotnet {

    public class Target {

    }

    // Depends on class Target due to inheritance
    public class Source {
        public void Method()
        {
            Object o = new Target();
        }
    }

}