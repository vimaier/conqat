namespace edu.tum.cs.conqat.dotnet {

    public class Target {
        public int a = 5;
    }

    public class SomeClass
    {
        public Target a = new Target();
    }

    // Depends on class Target due to inheritance
    public class Source {
        public void Method()
        {
            SomeClass someClass = new SomeClass();
            Target target = someClass.a;
        }
    }

}