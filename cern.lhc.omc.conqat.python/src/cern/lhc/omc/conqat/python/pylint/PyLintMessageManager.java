
package cern.lhc.omc.conqat.python.pylint;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableSet;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.commons.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import cern.lhc.omc.conqat.python.BundleContext;

/**
 * 
 * @author vimaier
 */
public class PyLintMessageManager {

	/** Singleton instance. */
	private static PyLintMessageManager instance = null;

	/** Mapping from bug types to short description strings. */
	private final Map<String, PyLintMessageType> messages = new HashMap<String, PyLintMessageType>();

	/** Constructor. */
	private PyLintMessageManager() throws ConQATException {
		BundleContext bundleContext = BundleContext.getInstance();
		String messagesPath = bundleContext.getResourceManager().getAbsoluteResourcePath("pylint_messages.xml");
		try(InputStream messages = new FileInputStream(new File(messagesPath))) {
				readMessages(XMLUtils.parse(new InputSource(messages)));
		} catch (SAXException e) {
			throw new ConQATException("Parsing error!", e);
		} catch (IOException e) {
			throw new ConQATException("I/O error!", e);
		}
	}


	/** Reads the relevant messages into this table. */
	private void readMessages(Document doc) {
		for (Element message : XMLUtils.getNamedChildren(
				doc.getDocumentElement(), "Message")) {
			String type = message.getAttribute("type");
			String shortDescription = extractElementText(message, "ShortDescription");
			String longDescription = extractElementText(message, "LongDescription");
			String details = extractElementText(message, "Details");
			PyLintMessageType messageType = new PyLintMessageType(type, shortDescription, longDescription, details);
			messages.put(type, messageType);
		}

	}

	/**
	 * Extracts the text content of the first element of given name in the
	 * parent element.
	 */
	private String extractElementText(Element parent, String elementName) {
		Element element = XMLUtils.getNamedChild(parent, elementName);
		if (element == null) {
			return StringUtils.EMPTY_STRING;
		}
		return element.getTextContent();
	}

	/** Returns the singleton instance. */
	public static PyLintMessageManager getInstance() throws ConQATException {
		if (instance == null) {
			instance = new PyLintMessageManager();
		}
		return instance;
	}

	/** Returns the set of all rules (as IDs) */
	public UnmodifiableSet<String> getRuleIds() {
		return CollectionUtils.asUnmodifiable(messages.keySet());
	}

	/**
	 * Returns the short description for a given bug pattern type or the empty
	 * String if none is available.
	 */
	public String getShortDescription(String type) {
		String result = messages.get(type).getShortDescription();
		if (result == null) {
			return StringUtils.EMPTY_STRING;
		}
		return result;
	}

	
	/**
	 * Returns the long description for a given message type or the empty
	 * String if none is available.
	 */
	public String getLongDescription(String type) {
		String result = messages.get(type).getLongDescription();
		if (result == null) {
			return StringUtils.EMPTY_STRING;
		}
		return result;
	}

	/** Returns the readable category name for the given bug pattern type. */
	public String getCategory(String type) {
		switch(type.charAt(0)) {  
		/* type examples: E0101, W0011
		 * See http://docs.pylint.org/output.html#source-code-analysis-section
		 */ 
		case 'W':
			return "Warning";
		case 'E':
			return "Error";
		case 'C':
			return "Convention";
		case 'R':
			return "REFACTOR";
		case 'F':
			return "REFACTOR";
		default:
			return "Other PyLint category";
		}
	}

	/**
	 * Returns the detailed description for a given bug pattern type or the
	 * empty String if none is available.
	 */
	public String getDetailedDescription(String type) {
		String result = messages.get(type).getDetails();
		if (result == null) {
			return StringUtils.EMPTY_STRING;
		}
		return result;
	}
	

	@SuppressWarnings("javadoc")
	class PyLintMessageType{
		
		private String type;
		private String shortDescription;
		private String longDescription;
		private String details;

		/**
		 * @param type
		 * @param shortDescription
		 * @param longDescription
		 * @param details
		 */
		public PyLintMessageType(String type, String shortDescription,
				String longDescription, String details) {
			this.type = type;
			this.shortDescription = shortDescription;
			this.longDescription = longDescription;
			this.details = details;
		}

		public String getType() {
			return type;
		}

		public String getShortDescription() {
			return shortDescription;
		}

		public String getLongDescription() {
			return longDescription;
		}

		public String getDetails() {
			return details;
		}		
		
	}
	
//	public static void main(String[] args) {
//		PyLintMessageManager msgManager = null;
//		try {
//			msgManager = PyLintMessageManager.getInstance();
//		} catch (ConQATException e) {
//			e.printStackTrace();
//		}
//		
//		if(null != msgManager)
//			msgManager.getCategory("E02");
//	}
	
}
