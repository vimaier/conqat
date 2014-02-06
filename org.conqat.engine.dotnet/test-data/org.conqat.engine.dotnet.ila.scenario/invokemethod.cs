namespace edu.tum.cs.conqat.dotnet {

    public class Target {
        public void TargetMethod()
        {
        }
    }

    // Depends on class Target due to inheritance
    public class Source {
        public void Method()
        {
            Target target = new Target();
            target.TargetMethod();
        }
    }

}