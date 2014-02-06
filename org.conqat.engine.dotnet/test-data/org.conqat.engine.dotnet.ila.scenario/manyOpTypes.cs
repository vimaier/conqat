using System;

namespace edu.tum.cs.conqat.dotnet {

    public class Target {
    }

    public class SomeClass {

    }
    // Depends on class Target due to method parameter
    public class Source {

        String arg = "123";
        
        public void caller(int x) {
            SomeClass y = new SomeClass();
            Object obj = new object();
            Target tar = (Target)obj;
            String asdsd = arg;
            someMethod("Hallo");
            int a = x;
            int a_withalonglongname = x;
        }

        public void someMethod(String x){
            Console.WriteLine(x);
            switch (x) {
                case "Hallo":
                    break;
                case "Charlie":
                    break;
                default:
                    break;
            }
        }
    }

}