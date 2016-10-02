import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Created by Angel on 02-Oct-16.
 */
public class Request {
	private String username;

	public Request(String username) {
		this.username = username;
	}

	//http://ws.audioscrobbler.com/2.0/?method=user.getrecenttracks&user=cesar_biker&api_key=56cd5196a6731ab53dea8d0dcc0fd1ea&format=json
	public void doRequest() {
		String urlParameters = "method=user.getrecenttracks&user="+ username + "&api_key=56cd5196a6731ab53dea8d0dcc0fd1ea&format=json";
		byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
		int postDataLength = postData.length;
		String request = "http://ws.audioscrobbler.com/2.0/";
		try {
			URL url = new URL(request);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setInstanceFollowRedirects(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty( "charset", "utf-8");
			conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
			conn.setUseCaches(false);
			try(DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
				wr.write(postData);
			}

			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder bigString = new StringBuilder();
			String line;
			JSONParser jsonParser = new JSONParser();

			while ((line = rd.readLine()) != null) {
				//System.out.println(line);
				bigString.append(line);
			}

			JSONObject jsonObject = (JSONObject) jsonParser.parse(bigString.toString());
			JSONObject jsonObject1 = (JSONObject) jsonObject.get("recenttracks");
			JSONArray jsonArray = (JSONArray) jsonObject1.get("track");
			JSONObject song = (JSONObject) jsonArray.get(0);

			String nom = song.get("name").toString();
			String artista = ((JSONObject) song.get("artist")).get("#text").toString();
			String nowplaying = ((JSONObject) song.get("@attr")).get("nowplaying").toString();
			JSONObject imageObj = (JSONObject)((JSONArray) song.get("image")).get(3);

			System.out.println("Canço: " + nom + ", Artista: " + artista);
			System.out.println("Imatge: " + imageObj.get("#text"));


			rd.close();
			conn.disconnect();
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}
}
