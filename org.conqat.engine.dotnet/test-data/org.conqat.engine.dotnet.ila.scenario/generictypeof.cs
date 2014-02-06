using System.Collections.Generic;

namespace edu.tum.cs.conqat.dotnet {

    public class Target {

    }

    public class Source {
        private void MyMethod()
        {
            System.Type type = typeof(List<Target>);
        }
    }

}