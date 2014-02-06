class MyTest {
	int privateInt;
private:
	char a;
public:
	int foo();
	int bar;
protected:
	float b;
private:
	void *c;
public:
	static const int validPublic;
}

struct MyStruct {
public:
	int publicInt;
}


class A {
private:

	class B {
	public:
		B() {}
	};

	// This one is private
	int c;

public:

	class D {
	private:
		E() {}
	};

	// This one is public
	int f;
}
