// Code used for testing of name resolution

// The test is data-driven and contains the results in the form 
// of comments starting with the keyword "EXPECTED" followed by a colon

namespace ns1 {

class Foo {
 public:
	// EXPECTED: ns1::Foo::Foo
	Foo();

	// EXPECTED: ns1::Foo::~Foo
	virtual ~Foo();	
	
	// EXPECTED: ns1::Foo::doSomething
	virtual int doSomething() = 0;

	// EXPECTED: ns1::Foo::doAnother
	inline void doAnother (int x, int y) { int z = x+y; }
 
 private:
	// EXPECTED: ns1::Foo::_member
	int _member;
}

// EXPECTED: ns1::Foo::Foo
Foo::Foo() : _member (2) { 
	// actual code
}

}

// EXPECTED: ns1::Foo::~Foo
ns1::Foo::~Foo() {
	// release stuff
}


