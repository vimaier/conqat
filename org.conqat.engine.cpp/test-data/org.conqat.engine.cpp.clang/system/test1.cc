#include <cstdio>
#include <iostream>

using namespace std;

void printSummary () {
  printf ("Hello World!\n");
}

void printSummary2 () {
  cout << "Hello World!" << endl;
}

int calculatePositiveSum (int a, int b) {
  int result;
  if (a > 0 && b > 0) {
    result = a+b;
  }
  return result;
}

char derefNullPtr (char *c) {
  if (c == 0) {
    printf ("Warning");
  }
  return *c;
}

