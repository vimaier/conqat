#include <cstdlib>


int leak (int a) {
  void *buffer = malloc (16);
  if (a > 0) {
    free (buffer);
  }
  return a;
}

