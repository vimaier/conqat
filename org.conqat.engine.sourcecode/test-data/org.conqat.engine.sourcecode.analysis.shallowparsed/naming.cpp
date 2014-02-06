
namespace foo {

	const int CONST1 = 17;
}

class C1 {
 public:
	
	const int CONST2;
	
	map<double, double> attribute1;
	
	void method1 (
		int p1,
		double *p2) {
		float lvar1 = 0.0;
		std::vector<char> v1;
	}
	
	int method2 (int p3);
};

const int C1::CONST2 = 17;

int C1::method2 (
	int p3 = 5) {
	for (p3 = 0; p3 < 4; ++p3) {
		p3 += 1;
	}
	
	for (int i = 1, j; i < 3; ++i) {
		j = i*i;
		int k = j+i, 
			l = k+1;
		p3 += l;
	}
}


float global1 = 0.0, 
	global2 = 3.141;
std::vector<char> global3;

