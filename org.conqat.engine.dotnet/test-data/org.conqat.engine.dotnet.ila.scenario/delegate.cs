using System;

namespace edu.tum.cs.conqat.dotnet {

    // Declare delegate -- defines required signature:
    delegate void Target(string message);

    public class Source {
        // Regular method that matches signature:
        static void SampleDelegateMethod(string message) {
            Console.WriteLine(message);
        }

        static void SourceMethod() {
            // Instantiate delegate with named method:
            Target d1 = SampleDelegateMethod;
            // Instantiate delegate with anonymous method:
            Target d2 = delegate(string message) {
                Console.WriteLine(message);
            };

            // Invoke delegate d1:
            d1("Hello");
            // Invoke delegate d2:
            d2(" World");
        }

    }

}