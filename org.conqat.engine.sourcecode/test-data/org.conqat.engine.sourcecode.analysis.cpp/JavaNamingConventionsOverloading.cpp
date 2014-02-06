
//The following class tests operator overloading and default arguments


class TestClassOverloading {

 int myFuncWithoutDefaultArgument(int a, int b);

 //Default Argument -> No finding
 int myFuncWithDefaultArgument(int a, int b, int c = 12);

 inline bool operator == (TestClassOverloading &b) const //Operator Overloading -> No finding
    {
        return false;
    }
}
