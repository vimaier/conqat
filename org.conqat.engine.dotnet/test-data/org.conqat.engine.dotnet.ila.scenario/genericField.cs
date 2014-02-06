using System.Collections.Generic;

namespace edu.tum.cs.conqat.dotnet {

    public class Target {
    }

    // Depends on class Target due to method parameter
    public class Source {
        List<Target> field;
    }

}