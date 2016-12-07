package main;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.DatatypeConverter;

public class OAEP {

	// private String mgfSeed;
	// private int maskLen;
	private MessageDigest digest, digest2;

	public OAEP() {
		digest = null;

		try {
			digest = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {

			e.printStackTrace();
		}

		digest2 = null;

		try {
			digest2 = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {

			e.printStackTrace();
		}

		OAEP_encode("fd5507e917ecbe833878");

	}

	private byte[] MGF(byte[] seed, int maskLen) {

		int ceil = (maskLen + digest.getDigestLength() - 1) / digest.getDigestLength();

		byte[] T = new byte[0];

		for (int i = 0; i < ceil; i++) {

			T = concatenate(T, SHA1(seed, i));

		}

		byte[] output = new byte[maskLen];
		System.arraycopy(T, 0, output, 0, output.length);
		System.out.println(toHex(output));
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

	private void OAEP_encode(String M) {
		byte[] Mbytes = toByteArray(M);

		String L = "da39a3ee5e6b4b0d3255bfef95601890afd80709";
		//digest2.update(toByteArray(L));
		byte[] lHash = toByteArray(L);
		// int PSsize = (128 - M.length() - (2 * digest2.getDigestLength()) -
		// 2);
		byte[] PS = new byte[128 - M.length() - 2 * digest2.getDigestLength() - 2];
		// Arrays.fill(ps, (byte) 00);

		byte[] pshash = concatenate(lHash, PS);
		byte[] zeroOneMessage = concatenate(new byte[] { 0x01 }, Mbytes);
		

		int DBLength = 128 - digest2.getDigestLength() - 1;
		
		//System.out.println(DBLength);

		byte[] DB = new byte[DBLength];
		
		byte[]temp = concatenate(pshash, zeroOneMessage);
		
		System.arraycopy(temp, 0, DB, 0, temp.length);

		//DB = concatenate(zeroOneMessage, pshash);

		String seedString = "1e652ec152d0bfcd65190ffc604c0933d0423381";
		byte[] seed = toByteArray(seedString);
		//System.out.println((128 - digest2.getDigestLength() - 1));
		byte[] dbMask = MGF(seed, (128 - digest2.getDigestLength() - 1));

		byte[] maskedDB = xor(DB, dbMask);
		
		byte[] seedMask = MGF(maskedDB, digest2.getDigestLength());

		byte[] maskedSeed = xor(seed, seedMask);


		byte[] almost = concatenate(new byte[] { 0x00 }, maskedSeed);

		byte[] EM = concatenate(almost, maskedDB);

		System.out.println(toHex(EM));

	}

	private byte[] xor(byte[] a, byte[] b) {
		
		if (a.length != b.length) {
			throw new InternalError("Byte a must equal Byte b");
		}

		// Create a temporary array to store results in
		byte[] output = new byte[a.length];

		// Loop through the array and xor each bit
		for (int i = 0; i < output.length; i++) {
			output[i] = (byte) (a[i] ^ b[i]);
		}

		return output;
	}

	private void OAEP_decode() {

	}

}