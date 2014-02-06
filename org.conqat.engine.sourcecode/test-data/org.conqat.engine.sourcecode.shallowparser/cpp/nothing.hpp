#ifndef BOOST_ADEL_NOTHING_HPP
#define BOOST_ADEL_NOTHING_HPP

#include <utility>

namespace boost {
  
  // a really empty struct
  struct nothing {
    inline nothing () {}
    inline nothing (const nothing &) {}
    inline void operator= (const nothing &) {}
  };

}

namespace std {

  // specialize pair for Nothing to allow optimizing away some space
  template<class T>
  struct pair<T, boost::nothing> {
    typedef T first_type; 
    typedef boost::nothing second_type;  

    T first;
    static boost::nothing second;
    
    inline pair() : first(T()) {}
    inline pair(const T &a, const boost::nothing &b) : first(a) {}
  };

}


#endif //  BOOST_ADEL_NOTHING_HPP

