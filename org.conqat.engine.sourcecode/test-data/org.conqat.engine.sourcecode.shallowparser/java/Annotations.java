/** Code fragment that shows usage of braces in annoations */
@MyAnnotation("String")
@AnotherAnnotation({ 1, 2, 3 })
public class Foo {

	/** {@inheritDoc} */
	@Override
	@MultiAnnotation(foo = 1, test = "string", bar = { "foo", "bar" })
	public String toString() {
		
		@SuppressWarnings ("rawtype")
		MyType<String> s = new MyType();
		
		return super.toString();
	}
	
	@org.hibernate.annotations.Table(
		    appliesTo = "MELDUNGSEINGANGSDATEI",
		    indexes = {
		        @org.hibernate.annotations.Index(name="I_MED_STATUSEXTENSION", columnNames = {"STATUS", "EXTENSION"})
		    }
		)
	public void foo () {
		// does nothing interesting
	}
}