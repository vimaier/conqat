#ifndef BOOST_ADEL_SPLAY_TREE_HPP
#define BOOST_ADEL_SPLAY_TREE_HPP

#include "nothing.hpp"

namespace boost {

  template<typename Key, typename Tp = nothing,
	   typename Compare = ::std::less<KeyType>,
	   typename Alloc = ::std::allocator<::std::pair<KeyType,ValueType> > >
  class splay_tree
  {
  public:
    typedef Key key_type;
    typedef Tp mapped_type;
    typedef ::std::pair<const Key, Tp> value_type; 
    typedef Compare key_compare;

    splay_tree (): root (0) {}

    void insert (const value_type &vt) 
    {
      // TODO
    }

  protected:

    // perform top-down splaying with key k
    void splay (const key_type &k) 
    {
      if (root == 0) return;

      node *curr = root;
      node *snd = curr->kv.first < k ? curr->left : curr->right;
      node *trd = snd->kv.first < k ? curr->left : curr->right;

    }


  private:
    node *root;


    struct node 
    {
      ::std::pair<k, ValueType> kv;
      node *left;
      node *right;
    };
  };

}


#endif // BOOST_ADEL_SPLAY_TREE_HPP

