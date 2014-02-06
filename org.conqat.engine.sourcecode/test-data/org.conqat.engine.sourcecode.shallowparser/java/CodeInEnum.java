/**
 * Code fragment that shows usage of additional code in enum literals. (from
 * http://javahowto.blogspot.de/2008/04/java-enum-examples.html and modified )
 */
public enum Element {

	EARTH,

	WIND() {
		public String info() {
			return "HOT";
		}
	},

	FIRE {
		public String info() {
			return "HOT";
		}
	};

	public String info() {
		return "element";
	};
}