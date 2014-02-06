using System;

namespace edu.tum.cs.conqat.dotnet {

    public class SomeClass<T, X, Y> {

        public static void SomeMethod()
        {
        }
    }

    public class Target<T, X, Y> { }

    public class Source {
        public void caller() {
            SomeClass<int, Target<String, int, String>, String>.SomeMethod();
        }
    }

}