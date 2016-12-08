package main;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.DatatypeConverter;

public class OAEP {

	private MessageDigest digest;
	private int k;

	public OAEP() {

		k = 128;

		digest = null;

		try {
			digest = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {

			e.printStackTrace();
		}

	}

	public void OAEP_encode(String M, String seedString) {

		byte[] Mbytes = toByteArray(M);

		String L = "";

		digest.update(toByteArray(L));

		byte[] lHash = digest.digest();

		byte[] PS = new byte[k - Mbytes.length - 2 * digest.getDigestLength() - 2];

		byte[] temp = concatenate(concatenate(lHash, PS), concatenate(new byte[] { 0x01 }, Mbytes));

		byte[] DB = new byte[k - digest.getDigestLength() - 1];

		System.arraycopy(temp, 0, DB, 0, temp.length);

		byte[] seed = toByteArray(seedString);

		byte[] dbMask = MGF(seed, (k - digest.getDigestLength() - 1));

		byte[] maskedDB = xor(DB, dbMask);

		byte[] seedMask = MGF(maskedDB, digest.getDigestLength());

		byte[] maskedSeed = xor(seed, seedMask);

		byte[] EM = concatenate(concatenate(new byte[] { 0x00 }, maskedSeed), maskedDB);

		System.out.println("Encoded string: " + toHex(EM));
		System.out.println("maskedSeed: " + toHex(maskedSeed));
		System.out.println("maskedDB: " + toHex(maskedDB));
		System.out.println("-----");

	}

	public void OAEP_decode(String emString) {

		byte[] EM = toByteArray(emString);

		String L = "";

		digest.update(toByteArray(L));

		byte[] lHash = digest.digest();

		byte[] maskedSeed = new byte[digest.getDigestLength()];

		byte[] maskedDB = new byte[k - digest.getDigestLength() - 1];

		System.arraycopy(EM, 1, maskedSeed, 0, maskedSeed.length);
		System.arraycopy(EM, maskedSeed.length + 1, maskedDB, 0, maskedDB.length);

		byte[] seedMask = MGF(maskedDB, digest.getDigestLength());
		byte[] seed = xor(maskedSeed, seedMask);

		byte[] dbMask = MGF(seed, k - digest.getDigestLength() - 1);

		byte[] DB = xor(maskedDB, dbMask);

		Byte b = new Byte((byte) 0x01);

		byte[] M = null;

		for (int i = 0; i < DB.length; i++) {

			Byte a = DB[i];

			if (a.compareTo(b) == 0) {

				M = new byte[DB.length - i];

				System.arraycopy(DB, i +1, M, 0, M.length -1);
				// System.arraycopy(src, srcPos, dest, destPos, length);

			}

		}

		System.out.println("Decoded string: " + toHex(M));
		System.out.println("maskedSeed: " + toHex(maskedSeed));
		System.out.println("maskedDB: " + toHex(maskedDB));

	}

	private byte[] MGF(byte[] seed, int maskLen) {

		int ceil = (maskLen + digest.getDigestLength() - 1) / digest.getDigestLength();

		byte[] T = new byte[0];

		for (int i = 0; i < ceil; i++) {

			T = concatenate(T, SHA1(seed, i));

		}

		byte[] output = new byte[maskLen];
		System.arraycopy(T, 0, output, 0, output.length);
		return output;

	}

	private byte[] SHA1(byte[] mask, int i) {

		digest.update(mask);
		digest.update(new byte[3]);
		digest.update((byte) i);
		byte[] digestBytes = digest.digest();

		return digestBytes;

	}

	private byte[] concatenate(byte[] a, byte[] b) {
		byte[] c = new byte[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}

	private String toHex(byte[] array) {
		return DatatypeConverter.printHexBinary(array);
	}

	private byte[] toByteArray(String s) {
		return DatatypeConverter.parseHexBinary(s);
	}

	private byte[] xor(byte[] a, byte[] b) {

		if (a.length != b.length) {
			throw new InternalError("Must be the same length");
		}

		byte[] output = new byte[a.length];

		for (int i = 0; i < output.length; i++) {
			output[i] = (byte) (a[i] ^ b[i]);
		}

		return output;
	}

}