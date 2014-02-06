
public class Person {

	private final String firstname;

	private final String lastname;

	private final int age;

	public Person(String f, String l, int a) {
		if (a <= 0)
			throw new IllegalArgumentException("Age must be positive!");
		firstname = f;
		lastname = l;
		age = a;
	}

	public Person(Person p) {
		firstname = p.firstname;
		lastname = p.lastname;
		age = p.age;
	}
	
	public String getFirstname() {
		return firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public int getAge() {
		return age;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(firstname);
		sb.append(" ");
		sb.append(lastname);
		sb.append("(");
		sb.append(age);
		sb.append(")");
		return sb.toString();
	}
}
