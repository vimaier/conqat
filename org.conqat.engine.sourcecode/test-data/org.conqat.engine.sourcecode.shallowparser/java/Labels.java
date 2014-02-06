// class that will not compile, but shows usage of labels in Java

public class Labels {

	private Set<Set<T>> apriori_gen(Set<Set<T>> L_k_1, int k) {

		for (int i = 0; i < asList.size(); i++) {
			inner: for (int j = i + 1; j < asList.size(); j++) {
				Set<T> union = CollectionUtils.unionSet(asList.get(i),
						asList.get(j));
				if (union.size() == k) {
					for (T item : union) {
						Set<T> check = new HashSet<T>(union);
						check.remove(item);
						if (!L_k_1.contains(check)) {
							continue inner;
						}
					}
					C_k.add(union);
				}
			}
		}

		switch (k) {
		case 7:
			break;
		case SOME_CONSTANT:
			break;
		case some.other.MyClass.THE_CONSTANT:
			break;
		}

		return C_k;
	}
}