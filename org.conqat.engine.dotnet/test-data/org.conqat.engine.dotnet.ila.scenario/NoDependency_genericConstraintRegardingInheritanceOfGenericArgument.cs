using System;
namespace edu.tum.cs.conqat.dotnet {

    public class Source {

        public void Method<Target, T>() where T : Target
        {
            Type x = typeof(Target);
        }
    }
}