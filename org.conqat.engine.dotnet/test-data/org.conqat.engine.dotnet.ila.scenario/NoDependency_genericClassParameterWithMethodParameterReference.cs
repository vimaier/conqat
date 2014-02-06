using System.Collections.Generic;
using System;

namespace edu.tum.cs.conqat.dotnet
{

    public class Source<Target>
    {
        int SomeMethod(Target x)
        {
            return 3;
        }

        int SomeOtherMethod(out Target x)
        {
            x = default(Target);
            return 3;
        }

        int SomeOtherMethod(out Target[] x)
        {
            x = default(Target[]);
            return 3;
        }

    }

}