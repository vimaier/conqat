namespace edu.tum.cs.conqat.dotnet {

    public class Target {
        public int a = 5;
    }

    // Depends on class Target due to inheritance
    public class Source {
        public void Method()
        {
            Target target = new Target();
            target.a = 2;
        }
    }

}