package Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONObject;

public class LoginUtils {
	public static void loginRecord(String username) {
		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		
		JSONObject newJson = new JSONObject();
		newJson.put(username, timeStamp);
		
		//put to server
		RestUtils.put("https://deadly-balloons-2.firebaseio.com/Logins/" + username + ".json", newJson.toJSONString());
	}
}
