import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RestUtils {
	public static void get(String requestURL) {
		try {

			URL u = new URL(requestURL);

			HttpURLConnection con = (HttpURLConnection) u.openConnection();
			
			con.setRequestMethod("GET");
			
			StringBuilder sb = new StringBuilder();
			if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
				//Stream을 처리해줘야 하는 귀찮음이 있음. 
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


			System.out.println("응답코드 : " + con.getResponseCode());

			System.out.println("응답메세지 : " + con.getResponseMessage());

		} catch (MalformedURLException e) {

			System.out.println(requestURL+" is not a URL I understand");

		} catch (IOException e) {

		}

	}


}
