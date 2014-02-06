// this file contains a couple of interesting cases for testing our parser.
// this file will not compile!

enum State {
  ON=1, OFF, DONT_KNOW=17
};

// forward declarations
class MyClass;
struct MyStruct;
struct MyNamespace::MyStruct;
enum MyEnum;

// diffent kinds of types for variables
MyStruct *ms1;
struct MyStruct *ms2;
MyNamespace::MyStruct *ms3;

// actual struct
struct MyStruct {
  int a;
};


// extern stuff

extern "C" void foo();

extern "C" {
	void bar ();
}

void foo (int *a) {
	if (a) delete a;
}

