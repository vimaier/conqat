using System;

namespace edu.tum.cs.conqat.dotnet {

    // Declare delegate -- defines required signature:
    delegate void SomeDelegate(string message);

    public class Target {

        public static void SampleDelegateMethod(string message) {
            Console.WriteLine(message);
        }
    }

    public class Source {
        // Regular method that matches signature:

        void SourceMethod() {
            // Instantiate delegate with named method:
            SomeDelegate d1 = Target.SampleDelegateMethod;
            // Instantiate delegate with anonymous method:
            SomeDelegate d2 = delegate(string message) {
                Console.WriteLine(message);
            };

            // Invoke delegate d1:
            d1("Hello");
            // Invoke delegate d2:
            d2(" World");
        }

    }

}