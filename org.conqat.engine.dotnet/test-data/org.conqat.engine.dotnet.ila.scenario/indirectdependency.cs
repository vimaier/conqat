using System;

namespace edu.tum.cs.conqat.dotnet {

    public class Target {
        public static string GetAString()
        {
            return "Hallo";
        }
    }

    public class Source {
        public void MyMethod()
        {
            String[] tmp = Target.GetAString().Split('l');
        }
    }

}