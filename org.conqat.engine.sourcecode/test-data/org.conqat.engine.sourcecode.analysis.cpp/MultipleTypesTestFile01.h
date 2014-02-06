/* Test case with other types than classes */

typedef int foo;

struct Point {
	int x;
	int y;
}

class MultipleTypesTestFile01 {
	Point point;
	int width;
	int height;
	
	virtual Rectangle();
	virtual ~Rectangle();
	
	double calculateArea();
}

