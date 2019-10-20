package Utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RestUtils {
	public static String get(String requestURL) {
		try {

			URL u = new URL(requestURL);

			HttpURLConnection con = (HttpURLConnection) u.openConnection();
			
			con.setRequestMethod("GET");
			
			StringBuilder sb = new StringBuilder();
			if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
				//Stream�?� 처리해줘야 하는 귀찮�?��?� 있�?�. 
				BufferedReader br = new BufferedReader(
						new InputStreamReader(con.getInputStream(), "utf-8"));
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line).append("\n");
				}
				br.close();
				System.out.println("" + sb.toString());
			} else {
				System.out.println(con.getResponseMessage());
			}


			System.out.println("�?�답코드 : " + con.getResponseCode());

			return sb.toString();

		} catch (MalformedURLException e) {

			System.out.println(requestURL+" is not a URL I understand");

		} catch (IOException e) {

		}
		
		return "";
	}
	
	public static void put(String requestURL, String inputJSON) {
		try {

			URL u = new URL(requestURL);

			HttpURLConnection con = (HttpURLConnection) u.openConnection();
			
			con.setRequestMethod("PUT");
			con.setRequestProperty("Content-Type", "application/json; utf-8");
			con.setRequestProperty("Accept", "application/json");
			con.setDoOutput(true);
			
			try(OutputStream os = con.getOutputStream()) {
			    byte[] input = inputJSON.getBytes("utf-8");
			    os.write(input, 0, input.length);           
			}
			
			StringBuilder sb = new StringBuilder();
			if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
				BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line).append("\n");
				}
				br.close();
				System.out.println("" + sb.toString());
			} else {
				System.out.println(con.getResponseMessage());
			}


			System.out.println("Resp Code : " + con.getResponseCode());

			System.out.println("Resp Msg : " + con.getResponseMessage());

		} catch (MalformedURLException e) {

			System.out.println(requestURL+" is not a URL I understand");

		} catch (IOException e) {

		}
	}


}
