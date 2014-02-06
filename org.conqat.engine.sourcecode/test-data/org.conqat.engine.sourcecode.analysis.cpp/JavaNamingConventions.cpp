class TestClass {
	void fooMethodA();
	void anothermethod();
	
	// invalid
	void THIS_IS_A_METHOD() {
		printf("");
	}
	
	bool ThisMethodNameLooksLikeAClass();

	int barVariable1;
	char NOT_A_CONST;
	float NotAClass;

	static const char LINE_BREAK = '\n';
	static const char *all_lower_case = "[a-z]";
	static const float Pi = 3.14159265;
}

class NOT_A_VALID_CLASS_NAME {
}

class lowerCase {
}

namespace aaa1::bbb2bbb::ccc;
namespace Editor::ui;
namespace editor::Model;

class MyClass {
	static MyClass *instance;
}

// This is a valid initialization of a static class variable
static MyClass *MyClass::instance = new MyClass ();
static MyClass *mynamespace::MyClass::instance = new MyClass ();

// The following lines test the heuristic to distinguish constants and functions
const String ClassName::CAPITAL("1"); // Constant -> No finding
const String ClassName::CAPITAL(); // Function -> Finding
const String ClassName::CAPITAL(a, "1"); // Constant -> No finding
const String ClassName::CAPITAL(int a, char b);  // Function -> Finding
const String ClassName::CAPITAL(a, b); // Constant -> No finding
const String ClassName::CAPITAL(a b); // Function -> Finding
const String ClassName::CAPITAL(int); // Function -> Finding
