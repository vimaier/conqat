
// virtual destructor in template class

template <class T>
class Foo {
public:
	virtual ~Foo<T>() {}
	
	typename Foo<T>::iterator my_func(typename Foo<T>::iterator iter) {
		typename NamedObjectMap<T>::iterator incr = iter++;
		if (iter == Foo<T>::end()) {
			return Foo<T>::end();
		}
		return Foo<T>::find(incr);
	}
};

template <class T>
void Foo<T>::initialize() {
	log("Foo::initialize()"); 
}

template <class T>
T& Foo<T>::operator*() {
	return 17;
}

