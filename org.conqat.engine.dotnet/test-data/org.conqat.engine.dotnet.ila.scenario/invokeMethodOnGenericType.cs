using System;

namespace edu.tum.cs.conqat.dotnet {

    public class Target<T> {

        public static void SomeMethod()
        {
        }
    }


    // Depends on class Target due to method parameter
    public class Source {
        public void caller() {
            Target<String>.SomeMethod();
        }
    }

}