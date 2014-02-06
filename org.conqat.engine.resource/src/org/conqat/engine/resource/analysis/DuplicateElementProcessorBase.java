/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 The ConQAT Project                                   |
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
package org.conqat.engine.resource.analysis;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;

import org.conqat.engine.commons.util.ConQATInputProcessorBase;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.engine.resource.util.UniformPathUtils;
import org.conqat.lib.commons.collections.ListMap;
import org.conqat.lib.commons.digest.Digester;
import org.conqat.lib.commons.digest.MD5Digest;

/**
 * Base class for processors that deal with duplicate elements. Duplicate
 * elements are detected using a simple hash-based approach.
 * 
 * @author $Author: pfaller $
 * @version $Rev: 43118 $
 * @ConQAT.Rating YELLOW Hash: 8C6298B0E60BEB319DF3093B8FAC0D9A
 */
public abstract class DuplicateElementProcessorBase extends
		ConQATInputProcessorBase<IResource> {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "equal-name-required", attribute = "value", description = ""
			+ "Duplicate elements require to have also an equal name, not only equal content.", optional = true)
	public boolean isEqualNameRequired = false;

	/** The digester that is used to calculate the hash codes. */
	private final MessageDigest digester = Digester.getMD5();

	/**
	 * Maps from the hashed element to a list of element. So every list with
	 * length > 2 describes a set of duplicate elements.
	 */
	private final ListMap<HashedElement, IElement> hashedElements = new ListMap<HashedElement, IElement>();

	/**
	 * Performs the detection of duplicate elements and calls
	 * {@link #processDuplicate(List)} for each set of detected duplicate
	 * elements.
	 */
	protected void processDuplicates() throws ConQATException {
		for (IElement element : ResourceTraversalUtils.listElements(input,
				IElement.class)) {
			hashedElements.add(new HashedElement(element), element);
		}

		for (HashedElement hashedElement : hashedElements.getKeys()) {
			List<IElement> elementList = hashedElements
					.getCollection(hashedElement);
			if (elementList.size() > 1) {
				processDuplicate(elementList);
			}
		}
	}

	/**
	 * Template method to process a set of duplicate elements. Due to
	 * implementations issues the provided type is a list. However, semantically
	 * it is a set as every element is guaranteed to occur only once and the
	 * ordering is undefined. Length of the list is guaranteed to be >=2.
	 */
	protected abstract void processDuplicate(List<IElement> elements);

	/**
	 * A class that allows hashing of elements. The hash code of this class is
	 * defined by a MD5 digest of the element, the <code>equals()</code> method
	 * compares element contents.
	 */
	private class HashedElement {

		/** The element associated with the hashed element. */
		private final IElement element;

		/** The digest of the element. */
		private final MD5Digest digest;

		/** Create new hashed element. */
		public HashedElement(IElement element) throws ConQATException {
			this.element = element;
			digester.reset();
			digester.update(element.getContent());
			if (isEqualNameRequired) {
				byte[] elementName = UniformPathUtils.getElementName(
						element.getUniformPath()).getBytes();
				digester.update(elementName);
			}
			digest = new MD5Digest(digester);
		}

		/** Hash code is based on the digest. */
		@Override
		public int hashCode() {
			return digest.hashCode();
		}

		/** Compares element contents. */
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof HashedElement)) {
				return false;
			}

			HashedElement otherElement = (HashedElement) obj;

			if (!otherElement.digest.equals(digest)) {
				return false;
			}

			try {
				byte[] otherContent = otherElement.element.getContent();
				byte[] thisContent = element.getContent();
				return Arrays.equals(otherContent, thisContent);
			} catch (ConQATException e) {
				getLogger()
						.warn("Could not access content" + e.getMessage(), e);
				return false;
			}

		}
	}

}