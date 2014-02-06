/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
|                                                                          |
| Licensed under the Apache License, Version 2.0 (the "License");          |
| you may not use this file except in compliance with the License.         |
| You may obtain a copy of the License at                                  |
|                                                                          |
|    http://www.apache.org/licenses/LICENSE-2.0                            |
|                                                                          |
| Unless required by applicable law or agreed to in writing, software      |
| distributed under the License is distributed on an "AS IS" BASIS,        |
| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. |
| See the License for the specific language governing permissions and      |
| limitations under the License.                                           |
+-------------------------------------------------------------------------*/
package org.conqat.engine.html_presentation.util;

import java.awt.Color;
import java.io.Writer;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.html_presentation.CSSMananger;
import org.conqat.lib.commons.color.ColorUtils;
import org.conqat.lib.commons.html.CSSDeclarationBlock;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterMatcher;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriterHelper;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.json.AbstractJsonWriter;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;
import com.thoughtworks.xstream.io.json.JsonWriter;

/**
 * Utility code for dealing with JSON. This uses XStream for serialization to
 * JSON (which is supports quite nicely and in a configurable way), and GSON for
 * deserialization (as XStream does not support deserialization for JSON and
 * GSON is quite good at it).
 * 
 * 
 * @author $Author: hummelb $
 * @version $Rev: 46669 $
 * @ConQAT.Rating YELLOW Hash: A82A30CA9D7DA138CE450E7C7CE52F40
 */
public class JsonUtils {

	/**
	 * The shared xstream instance used. Should be accessed via
	 * {@link #getOrCreateXStream()}.
	 */
	private static XStream xstream;

	/**
	 * The shared GSON instance used. Should be accessed via
	 * {@link #getOrCreateGSON()}.
	 */
	private static Gson gson;

	/**
	 * Serializes the given object to JSON.
	 */
	public static String serializeToJSON(Object object) {
		if (object == null) {
			return "null";
		}

		XStream xstream = getOrCreateXStream();
		synchronized (xstream) {
			return xstream.toXML(object);
		}
	}

	/** Returns {@link #xstream} and performs initialization if necessary. */
	private synchronized static XStream getOrCreateXStream() {
		if (xstream == null) {
			xstream = new XStream(new CustomizedJsonStreamDriver());
			xstream.autodetectAnnotations(true);

			xstream.setMode(XStream.NO_REFERENCES);

			xstream.registerConverter(new MapConverter());
			xstream.registerConverter(new MapEntryConverter());

			xstream.registerConverter(new ToStringConverterBase<CSSDeclarationBlock>(
					CSSDeclarationBlock.class) {
				@Override
				protected String valueToString(CSSDeclarationBlock value) {
					return CSSMananger.getInstance().getCSSClassName(value);
				}
			});

			xstream.registerConverter(new ToStringConverterBase<Color>(
					Color.class) {
				@Override
				protected String valueToString(Color value) {
					return ColorUtils.toHtmlString(value);
				}
			});

			// need special treatment for lists to also support Arrays.asList()
			xstream.registerConverter(new CollectionConverter(xstream
					.getMapper()) {
				/**
				 * The existing implementation only explicitly white-lists
				 * certain collections, however, it seems to work for other
				 * collections as well. Hence, we adjust it here.
				 */
				@Override
				public boolean canConvert(
						@SuppressWarnings("rawtypes") Class type) {
					return type != null
							&& Collection.class.isAssignableFrom(type);
				}
			});
		}

		return xstream;
	}

	/**
	 * Deserializes a JSON string.
	 * 
	 * @throws ConQATException
	 *             if the input string could not be parsed as JSON or the class
	 *             is not constructible.
	 */
	public static <T> T deserializeFromJSON(String json, Class<T> expectedClass)
			throws ConQATException {
		Gson gson = getOrCreateGSON();
		synchronized (gson) {
			try {
				return gson.fromJson(json, expectedClass);
			} catch (JsonSyntaxException e) {
				throw new ConQATException("Input was invalid JSON.", e);
			} catch (Throwable t) {
				throw new ConQATException(
						"Trouble during JSON deserialization: "
								+ t.getMessage(), t);
			}
		}
	}

	/** Reformats a JSON string to be pretty printed. */
	public static String prettyPrintJSON(String json) throws ConQATException {
		try {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			return gson.toJson(new JsonParser().parse(json));
		} catch (JsonSyntaxException e) {
			throw new ConQATException("Input was invalid JSON.", e);
		} catch (Throwable t) {
			throw new ConQATException("Trouble during JSON pretty printing: "
					+ t.getMessage(), t);
		}
	}

	/** Returns {@link #gson} and performs initialization if necessary. */
	private synchronized static Gson getOrCreateGSON() {
		if (gson == null) {
			gson = new Gson();
		}

		return gson;
	}

	/**
	 * A {@link JsonHierarchicalStreamDriver} with a couple of tweaks to make
	 * the resulting JSON more readable.
	 */
	private static final class CustomizedJsonStreamDriver extends
			JsonHierarchicalStreamDriver {

		/** {@inheritDoc} */
		@Override
		public HierarchicalStreamWriter createWriter(Writer out) {
			JsonWriter.Format format = new JsonWriter.Format(new char[0],
					new char[0], JsonWriter.Format.COMPACT_EMPTY_ELEMENT);

			return new JsonWriter(out, AbstractJsonWriter.DROP_ROOT_MODE,
					format) {

				/** {@inheritDoc} */
				@SuppressWarnings("rawtypes")
				@Override
				protected boolean isArray(Class clazz) {
					// this special case is required to stop XStream from
					// wrapping maps into an additional array in JSON output
					if (clazz != null && Map.class.isAssignableFrom(clazz)) {
						return false;
					}

					return super.isArray(clazz);
				}
			};
		}
	}

	/**
	 * Converter used to enforce a more JSON-like format to maps. To be used in
	 * conjunction with {@link MapEntryConverter}.
	 */
	@SuppressWarnings("rawtypes")
	private static final class MapConverter extends WriteOnlyConverterBase<Map> {

		/** Constructor. */
		public MapConverter() {
			super(Map.class);
		}

		/** {@inheritDoc} */
		@Override
		protected void marshalValue(Map map, HierarchicalStreamWriter writer,
				MarshallingContext context) {
			for (Object entry : map.entrySet()) {
				context.convertAnother(entry);
			}
		}
	}

	/**
	 * Converter used to enforce a more JSON-like format to maps and their
	 * entries. To be used in conjunction with {@link MapConverter}.
	 */
	@SuppressWarnings("rawtypes")
	private static final class MapEntryConverter extends
			WriteOnlyConverterBase<Map.Entry> {

		/** Constructor. */
		private MapEntryConverter() {
			super(Map.Entry.class);
		}

		/** {@inheritDoc} */
		@Override
		protected void marshalValue(Entry entry,
				HierarchicalStreamWriter writer, MarshallingContext context) {

			Object key = entry.getKey();
			Object value = entry.getValue();

			if (value == null) {
				writer.startNode(key.toString());
				writer.setValue("null");
			} else {
				ExtendedHierarchicalStreamWriterHelper.startNode(writer,
						key.toString(), value.getClass());
				context.convertAnother(value);
			}
			writer.endNode();
		}
	}

	/**
	 * Base class for converters that only support writing (no deserialization).
	 */
	private static abstract class WriteOnlyConverterBase<T> extends
			SingleClassConverterMatcherBase implements Converter {

		/** Constructor. */
		protected WriteOnlyConverterBase(Class<T> matchedClass) {
			super(matchedClass);
		}

		/**
		 * {@inheritDoc}
		 * <p>
		 * Throws {@link UnsupportedOperationException}.
		 */
		@Override
		public Object unmarshal(HierarchicalStreamReader reader,
				UnmarshallingContext context) {
			throw new UnsupportedOperationException();
		}

		/** {@inheritDoc} */
		@SuppressWarnings("unchecked")
		@Override
		public void marshal(Object source, HierarchicalStreamWriter writer,
				MarshallingContext context) {
			marshalValue((T) source, writer, context);
		}

		/** Performs serialization for a given value. */
		protected abstract void marshalValue(T source,
				HierarchicalStreamWriter writer, MarshallingContext context);
	}

	/** Base class for simple converters that just convert to a string. */
	private static abstract class ToStringConverterBase<T> extends
			SingleClassConverterMatcherBase implements SingleValueConverter {

		/** Constructor. */
		protected ToStringConverterBase(Class<T> matchedClass) {
			super(matchedClass);
		}

		/** {@inheritDoc} */
		@SuppressWarnings("unchecked")
		@Override
		public String toString(Object value) {
			return valueToString((T) value);
		}

		/** Template method for converting the value to a string. */
		protected abstract String valueToString(T value);

		/**
		 * {@inheritDoc}
		 * <p>
		 * Throws {@link UnsupportedOperationException}.
		 */
		@Override
		public Object fromString(String s) {
			throw new UnsupportedOperationException();
		}

	}

	/** Base class for {@link ConverterMatcher}s that match a single class. */
	private static abstract class SingleClassConverterMatcherBase implements
			ConverterMatcher {

		/** The matched class. */
		private final Class<?> matchedClass;

		/** Constructor. */
		protected SingleClassConverterMatcherBase(Class<?> matchedClass) {
			this.matchedClass = matchedClass;
		}

		/** {@inheritDoc} */
		@SuppressWarnings("rawtypes")
		@Override
		public boolean canConvert(Class type) {
			return type != null && matchedClass.isAssignableFrom(type);
		}
	}
}
