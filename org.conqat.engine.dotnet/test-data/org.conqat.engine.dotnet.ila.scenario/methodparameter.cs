using System;

namespace edu.tum.cs.conqat.dotnet {

    public class Target {
    }

    // Depends on class Target due to method parameter
    public class Source {
        public void caller(Target callee) {
            
        }
    }

}