package io.github.pseudoresonance.pseudoapi.bukkit.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONArray;

public class JsonReader {

	private static String readAll(Reader rd) {
		StringBuilder sb = new StringBuilder();
		int cp;
		try {
			while ((cp = rd.read()) != -1) {
				sb.append((char) cp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 * Returns {@link JSONArray} of JSON read from a given URL
	 * 
	 * @param url URL to read JSON from
	 * @return {@link JSONArray} of read JSON
	 */
	public static JSONArray readJsonFromUrl(String url) {
		try {
			InputStream is = new URL(url).openStream();
			try {
				BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
				String jsonText = readAll(rd);
				JSONArray json = new JSONArray(jsonText);
				return json;
			} finally {
				is.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}