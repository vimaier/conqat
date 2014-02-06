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
package org.conqat.lib.commons.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.conqat.lib.commons.filesystem.FileSystemUtils;

/**
 * Utility methods for dealing with raw byte arrays. This is located in the I/O
 * package, as the typical application for these methods is binary I/O on byte
 * array level.
 * 
 * @author $Author: heineman $
 * @version $Rev: 40000 $
 * @ConQAT.Rating GREEN Hash: 3C21A902FA8811331396F0FA39FDE880
 */
public class ByteArrayUtils {

	/** The number of bytes used to encode a double as a byte array. */
	public static final int DOUBLE_BYTE_ARRAY_LENGTH = 8;

	/** The number of bytes used to encode a long as a byte array. */
	public static final int LONG_BYTE_ARRAY_LENGTH = 8;

	/** Converts a double value to a byte array. */
	public static byte[] doubleToByteArray(double value) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		try {
			dos.writeDouble(value);
			dos.close();
		} catch (IOException e) {
			throw new AssertionError("Can not happen as we work in memory: "
					+ e.getMessage());
		}
		return bos.toByteArray();
	}

	/** Converts a long value to a byte array. */
	public static byte[] longToByteArray(long value) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		try {
			dos.writeLong(value);
			dos.close();
		} catch (IOException e) {
			throw new AssertionError("Can not happen as we work in memory: "
					+ e.getMessage());
		}
		return bos.toByteArray();
	}

	/**
	 * Converts a byte array to a double value.
	 * 
	 * @throws IOException
	 *             if the array is too short (less than
	 *             {@value #DOUBLE_BYTE_ARRAY_LENGTH} bytes) or the bytes can
	 *             not be converted to a double. Overall, this method is only
	 *             guaranteed to work if the input array was created by
	 *             {@link #doubleToByteArray(double)}.
	 */
	public static double byteArrayToDouble(byte[] value) throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(value);
		DataInputStream dis = new DataInputStream(bis);
		try {
			return dis.readDouble();
		} finally {
			FileSystemUtils.close(dis);
		}
	}

	/**
	 * Converts a byte array to a long value.
	 * 
	 * @throws IOException
	 *             if the array is too short (less than
	 *             {@value #LONG_BYTE_ARRAY_LENGTH} bytes) or the bytes can not
	 *             be converted to a long. Overall, this method is only
	 *             guaranteed to work if the input array was created by
	 *             {@link #longToByteArray(long)}.
	 */
	public static long byteArrayToLong(byte[] value) throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(value);
		DataInputStream dis = new DataInputStream(bis);
		try {
			return dis.readLong();
		} finally {
			FileSystemUtils.close(dis);
		}
	}

	/**
	 * Decompresses a single byte[] using GZIP. A null input array will cause
	 * this method to return null.
	 * 
	 * @throws IOException
	 *             if the input array is not valid GZIP compressed data (as
	 *             created by {@link #compress(byte[])}).
	 */
	public static byte[] decompress(byte[] value) throws IOException {
		if (value == null) {
			return null;
		}

		ByteArrayOutputStream bos = new ByteArrayOutputStream(value.length);
		ByteArrayInputStream bis = new ByteArrayInputStream(value);
		GZIPInputStream gzis = new GZIPInputStream(bis);

		FileSystemUtils.copy(gzis, bos);

		// it does not matter if we close in case of exceptions, as these are
		// in-memory resources
		gzis.close();
		bos.close();

		return bos.toByteArray();
	}

	/**
	 * Compresses a single byte[] using GZIP. A null input array will cause this
	 * method to return null.
	 */
	public static byte[] compress(byte[] value) {
		if (value == null) {
			return null;
		}

		ByteArrayOutputStream bos = new ByteArrayOutputStream(value.length);
		try {
			GZIPOutputStream gzos = new GZIPOutputStream(bos);
			gzos.write(value);

			// it does not matter if we close in case of exceptions, as this is
			// an in-memory resource
			gzos.close();
		} catch (IOException e) {
			throw new AssertionError("Can not happen as we work in memory: "
					+ e.getMessage());
		}

		return bos.toByteArray();
	}

	/** Returns whether the prefix is a prefix of the given key. */
	public static boolean isPrefix(byte[] prefix, byte[] key) {
		if (key.length < prefix.length) {
			return false;
		}
		for (int i = 0; i < prefix.length; ++i) {
			if (prefix[i] != key[i]) {
				return false;
			}
		}
		return true;
	}

	/** Returns true if a1 is (lexicographically) less than a2. */
	public static boolean isLess(byte[] a1, byte[] a2, boolean resultIfEqual) {
		int limit = Math.min(a1.length, a2.length);
		for (int i = 0; i < limit; ++i) {
			if (unsignedByte(a1[i]) < unsignedByte(a2[i])) {
				return true;
			}
			if (unsignedByte(a1[i]) > unsignedByte(a2[i])) {
				return false;
			}
		}

		if (a1.length < a2.length) {
			return true;
		}
		if (a1.length > a2.length) {
			return false;
		}

		return resultIfEqual;
	}

	/** Returns the unsigned byte interpretation of the parameter. */
	public static int unsignedByte(byte b) {
		return b & 0xff;
	}

	/** Returns the concatenation of the given arrays. */
	public static byte[] concat(byte[]... arrays) {
		return concat(Arrays.asList(arrays));
	}

	/** Returns the concatenation of the given arrays. */
	public static byte[] concat(Iterable<byte[]> arrays) {
		int length = 0;
		for (byte[] array : arrays) {
			length += array.length;
		}

		byte[] result = new byte[length];
		int start = 0;
		for (byte[] array : arrays) {
			System.arraycopy(array, 0, result, start, array.length);
			start += array.length;
		}
		return result;
	}
}
