namespace edu.tum.cs.conqat.dotnet {

    public class OuterClass
    {
        public class Target
        {

        }
    }

    // Depends on class Target due to inheritance
    public class Source {
        public void MyMethod()
        {
            OuterClass.Target target = new OuterClass.Target();
        }
    }

}