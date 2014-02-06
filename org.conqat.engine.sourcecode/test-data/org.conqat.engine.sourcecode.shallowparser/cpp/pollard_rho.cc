#include <iostream>
#include <algorithm>
#include <cmath>

using namespace std;

typedef unsigned long long ull;

ull gcd (ull a, ull b) {
  if (a > b)
    swap (a, b);

  if (a == 0) 
    return 1;

  while (a > 0) {
    b %= a;
    swap (a, b);
  }
  return b;
}

ull f (ull x, ull a, ull b, ull n) {
  x *= x;
  x %= n;
  x *= a;
  x %= n;
  x += b;
  x %= n;
  return x;
}

inline ull diff (ull a, ull b) {
  return a < b ? b-a : a-b;
}

void pr (ull n) {

  ull a = rand () % n, b = rand () % n;
  
  cout << "Parameters: " << a << " " << b << endl;

  int k1 = 1, k2 = f (1, a, b, n);
  int steps = 1;

  while (gcd (n, diff(k1, k2)) == 1) {
    ++steps;
    k1 = f (k1, a, b, n);
    k2 = f (k2, a, b, n);
    k2 = f (k2, a, b, n);
  }

  cout << "Found factor " << gcd (n, diff(k1, k2)) << " after " 
       << steps << " steps" << endl;
}

int main () {
  ull l;

  while (cin >> l) 
    pr (l);

  return 0;
}

