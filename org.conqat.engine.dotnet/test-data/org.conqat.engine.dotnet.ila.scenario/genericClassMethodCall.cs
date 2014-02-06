using System;

namespace edu.tum.cs.conqat.dotnet {

    public class SomeClass {
    }

    public static class Target<T>
    {
        public static void doSomething()
        {
            int a;
            a = 34;
            a++;
        }
    }

    // Depends on class Target due to method parameter
    public class Source {
        public void caller() {
            Target<SomeClass>.doSomething();
        }
    }

}