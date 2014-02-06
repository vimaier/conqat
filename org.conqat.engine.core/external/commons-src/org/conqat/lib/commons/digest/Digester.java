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
package org.conqat.lib.commons.digest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.List;

import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.error.EnvironmentError;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Utility functions for creation of digests.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46577 $
 * @ConQAT.Rating GREEN Hash: 7068740A7328597D6D8E32AD6E710A06
 */
public class Digester {

	/**
	 * Digester used to create Digester hashes organized by thread. This is used
	 * to avoid recreation of digesters, while keeping the code thread safe
	 * (i.e. each thread has its own instance).
	 */
	private static ThreadLocal<MessageDigest> digesters = new ThreadLocal<MessageDigest>() {
		/** {@inheritDoc} */
		@Override
		protected MessageDigest initialValue() {
			return getMD5();
		}
	};

	/**
	 * Computes an MD5 hash for a string. The hash is always 32 characters long
	 * and only uses characters from [0-9A-F].
	 */
	public static String createMD5Digest(String base) {
		return createMD5Digest(base.getBytes());
	}

	/**
	 * Computes an MD5 hash for a string. The hash is always 32 characters long
	 * and only uses characters from [0-9A-F].
	 */
	public static String createMD5Digest(byte[] data) {
		MessageDigest digester = digesters.get();
		digester.reset();
		digester.update(data);
		return StringUtils.encodeAsHex(digester.digest());
	}

	/**
	 * Computes an MD5 hash for a collection of strings. The strings are sorted
	 * before MD5 computation, so that the resulting MD5 hash is independent of
	 * the order of the strings in the collection.
	 */
	public static String createMD5Digest(Collection<String> bases) {
		List<String> sortedBases = CollectionUtils.sort(bases);
		return createMD5Digest(StringUtils.concat(sortedBases,
				StringUtils.EMPTY_STRING));
	}

	/**
	 * Returns Digester digester or throws an AssertionError if the Digester
	 * could not be located.
	 */
	public static MessageDigest getMD5() {
		try {
			return MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new EnvironmentError(
					"MD5 algorithm found. Please check your JRE installation",
					e);
		}
	}
}