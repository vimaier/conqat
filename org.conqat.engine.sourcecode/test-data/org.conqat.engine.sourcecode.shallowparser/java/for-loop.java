// Test case for CR#4418

class Foo {
	public void
	setPort(int port) {
	  for (int i = 0; i < resolvers.size(); i++)
	    ((Resolver)resolvers.get(i)).setPort(port);
	}
}