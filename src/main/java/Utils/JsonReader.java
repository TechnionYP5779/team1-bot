package Utils;

import java.io.*;
import java.net.*;
import java.nio.charset.*;

import org.apache.commons.io.*;
import org.json.*;

public class JsonReader {

	private static String readAll(Reader rd) throws IOException {
//    String s = IOUtils.toString(rd), $ = s.substring(s.indexOf('\n') + 1);
//    return $.substring(0, $.lastIndexOf('\n'));
		return IOUtils.toString(rd);
	}

	public static JSONArray readJsonFromUrl(String url) {
		try (InputStream $ = new URL(url).openStream()){
			
			try {
				return new JSONArray(readAll(new BufferedReader(extracted($))));
			} catch (JSONException | IOException ¢) {
				¢.printStackTrace();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	private static InputStreamReader extracted(InputStream $) {
		return new InputStreamReader($, Charset.forName("UTF-8"));
	}

	public static JSONArray ReadJsonFromFile(String filename) {
		try {
			return new JSONArray(readAll(new BufferedReader(new FileReader(filename))));
		} catch (JSONException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}