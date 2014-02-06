using System.ComponentModel;
using System;
namespace edu.tum.cs.conqat.dotnet {

    public class Target {

    }

    public class Source {

        [TypeConverter(typeof(Target))]
        private void MyMethod()
        {
            
        }
    }

}