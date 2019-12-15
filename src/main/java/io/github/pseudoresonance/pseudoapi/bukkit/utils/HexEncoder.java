package io.github.pseudoresonance.pseudoapi.bukkit.utils;

import java.nio.charset.Charset;

import org.bukkit.ChatColor;

public class HexEncoder {

	private static final String SEQUENCE_HEADER = "" + ChatColor.RESET + ChatColor.UNDERLINE + ChatColor.RESET;
	private static final String SEQUENCE_FOOTER = "" + ChatColor.RESET + ChatColor.ITALIC + ChatColor.RESET;
	
	
	/**
	 * Returns a String encoded in Bukkit color codes
	 * 
	 * @param hiddenString String to encode
	 * @return String of color codes
	 */
	public static String encodeString(String hiddenString) {
		return quote(stringToColors(hiddenString));
	}
	
	/**
	 * Returns whether or not string contains encoded data
	 * 
	 * @param input String to check
	 * @return Whether or not string contains encoded data
	 */
	public static boolean hasHiddenString(String input) {
		if (input == null) return false;
		return input.indexOf(SEQUENCE_HEADER) > -1 && input.indexOf(SEQUENCE_FOOTER) > -1;
	}	
	
	/**
	 * Returns encoded data
	 * 
	 * @param input String to decode
	 * @return Encoded data
	 */
	public static String extractHiddenString(String input) {
		return colorsToString(extract(input));
	}
	
	
	/**
	 * Returns string with encoded data replaced by another string
	 * 
	 * @param input String to recode
	 * @param hiddenString Data to encode into string
	 * @return String with encoded data replaced by another string
	 */
	public static String replaceHiddenString(String input, String hiddenString) {
		if (input == null) return null;
		int start = input.indexOf(SEQUENCE_HEADER);
		int end = input.indexOf(SEQUENCE_FOOTER);
		if (start < 0 || end < 0) {
			return null;
		}
		return input.substring(0, start + SEQUENCE_HEADER.length()) + stringToColors(hiddenString) + input.substring(end, input.length());
	}
	
	private static String quote(String input) {
		if (input == null) return null;
		return SEQUENCE_HEADER + input + SEQUENCE_FOOTER;
	}
	
	private static String extract(String input) {
		if (input == null) return null;
		
		int start = input.indexOf(SEQUENCE_HEADER);
		int end = input.indexOf(SEQUENCE_FOOTER);
		
		if (start < 0 || end < 0) {
			return null;
		}
		
		return input.substring(start + SEQUENCE_HEADER.length(), end);
	}
	
	private static String stringToColors(String normal) {
		if (normal == null) return null;
		
		byte[] bytes = normal.getBytes(Charset.forName("UTF-8"));
		char[] chars = new char[bytes.length * 4];
		
		for (int i = 0; i < bytes.length; i++) {
			char[] hex = byteToHex(bytes[i]);
			chars[i * 4] = ChatColor.COLOR_CHAR;
			chars[i * 4 + 1] = hex[0];
			chars[i * 4 + 2] = ChatColor.COLOR_CHAR;
			chars[i * 4 + 3] = hex[1];
		}
		
		return new String(chars);
	}
	
	private static String colorsToString(String colors) {
		if (colors == null) return null;
		
		colors = colors.toLowerCase().replace("" + ChatColor.COLOR_CHAR, "");
		
		if (colors.length() % 2 != 0) {
			colors = colors.substring(0, (colors.length() / 2) * 2);
		}
		
		char[] chars = colors.toCharArray();
		byte[] bytes = new byte[chars.length / 2];
		
		for (int i = 0; i < chars.length; i += 2) {
			bytes[i / 2] = hexToByte(chars[i], chars[i + 1]);
		}

		return new String(bytes, Charset.forName("UTF-8"));
	}
	
	private static int hexToUnsignedInt(char c) {
		if (c >= '0' && c <= '9') {
			return c - 48;
		} else if (c >= 'a' && c <= 'f') {
			return c - 87;
		} else {
			throw new IllegalArgumentException("Invalid hex char: out of range");
		}
	}
	
	private static char unsignedIntToHex(int i) {
		if (i >= 0 && i <= 9) {
			return (char) (i + 48);
		} else if (i >= 10 && i <= 15) {
			return (char) (i + 87);
		} else {
			throw new IllegalArgumentException("Invalid hex int: out of range");
		}
	}

	private static byte hexToByte(char hex1, char hex0) {
		return (byte) (((hexToUnsignedInt(hex1) << 4) | hexToUnsignedInt(hex0)) + Byte.MIN_VALUE);
	}
	
	private static char[] byteToHex(byte b) {
		int unsignedByte = (int) b - Byte.MIN_VALUE;
		return new char[]{unsignedIntToHex((unsignedByte >> 4) & 0xf), unsignedIntToHex(unsignedByte & 0xf)};
	}
	
}