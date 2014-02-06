using System;

namespace edu.tum.cs.conqat.dotnet {

    public class Target {
    }

    public class Source {
        public void caller() {
            object[] x = new object[] { new Target() };
        }
    }

}