using System;

namespace edu.tum.cs.conqat.dotnet {

    public class Target {
    }

    public class Source {

        private int privateField = 5;
        public int publicField = 5;
        internal protected int internalProtectedField = 5;
        protected int protectedField = 5;
        internal int internalField = 5;


        private void privateMethod() { }
        public void publicMethod() { }
        internal protected void internalProtectedMethod() { }
        protected void protectedMethod() { }
        internal void internalMethod() { }

        public static Target x;
    }

}