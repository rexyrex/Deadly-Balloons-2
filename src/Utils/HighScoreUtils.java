package Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class HighScoreUtils {
	public static void addHighScore(String gameMode, String levelName, String timeStr, String userName) throws ParseException {
		
		int userScore = getScoreInSeconds(timeStr);
		
		//get current highscores
		String top3Str = getHighscores(gameMode, levelName);
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(top3Str);
		JSONObject top3JSON = (JSONObject) obj;
		
		HashMap<String, Integer> scoreMap = new HashMap<String, Integer>();
		
		Iterator<String> keys = top3JSON.keySet().iterator();
		
		while(keys.hasNext()) {
			String key = keys.next();
			String val = (String) top3JSON.get(key);
			System.out.println("key/val = " + key + " = " + getScoreInSeconds(val));
			scoreMap.put(key, getScoreInSeconds(val));
		}
		
		//if username already exists then update score only if better score
		if(scoreMap.containsKey(userName)) {
			if(scoreMap.get(userName) > userScore) {
				scoreMap.put(userName, userScore);
			} else {
				return;
			}
		} else {
			//add user to highscore sheet
			scoreMap.put(userName, userScore);
		}
		
		//sort new scores
		Map<String,Integer> sortedScoreMap = sortByValue(scoreMap);
		
		//convert hashMap to Json
		
		JSONObject newJson = new JSONObject();
		
		
		System.out.println("-- sorted --");
		ArrayList<String> keyList = new ArrayList(sortedScoreMap.keySet());		
		for (int i = 0; i < keyList.size(); i++) {
			//get key
			String key = keyList.get(i);
			System.out.println("Key :: " + key);
			//get value corresponding to key
			int value = sortedScoreMap.get(key);
			System.out.println("Value :: " + value);
			System.out.println("--------------------------------");
			
			newJson.put(key, secondsToScoreStr(value));
		}	

		//put to server
		RestUtils.put("https://deadly-balloons-2.firebaseio.com/HighScores/" + gameMode + "/" + levelName + ".json", newJson.toJSONString());
		
	}
	
	public static Map<String, String> getTopFive(String gameMode, String levelName) throws ParseException{
		Map<String,String> topFiveMap = new LinkedHashMap<String,String>();
		
		//get current highscores
		String top3Str = getHighscores(gameMode, levelName);
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(top3Str);
		JSONObject top3JSON = (JSONObject) obj;
		
		HashMap<String, Integer> scoreMap = new HashMap<String, Integer>();
		
		Iterator<String> keys = top3JSON.keySet().iterator();
		
		while(keys.hasNext()) {
			String key = keys.next();
			String val = (String) top3JSON.get(key);
			System.out.println("key/val = " + key + " = " + getScoreInSeconds(val));
			scoreMap.put(key, getScoreInSeconds(val));
		}
		
		//sort new scores
		Map<String,Integer> sortedScoreMap = sortByValue(scoreMap);
		
		//convert hashMap to Json
		
		JSONObject newJson = new JSONObject();
		
		
		System.out.println("-- sorted --");
		ArrayList<String> keyList = new ArrayList(sortedScoreMap.keySet());		
		for (int i = 0; i < keyList.size(); i++) {
			//get key
			String key = keyList.get(i);
			System.out.println("Key :: " + key);
			//get value corresponding to key
			int value = sortedScoreMap.get(key);
			System.out.println("Value :: " + value);
			System.out.println("--------------------------------");
			
			topFiveMap.put(key, secondsToScoreStr(value));
			if(i == 4) {
				break;
			}
		}	
		
		return topFiveMap;
	}
	
	public static String getHighscores(String gameMode, String levelName) {
		return RestUtils.get("https://deadly-balloons-2.firebaseio.com/HighScores/" + gameMode + "/" + levelName + ".json");
	}
	
	//parse "10m 23s" format to integer seconds
	public static int getScoreInSeconds(String score) {
		int secondsScore = 0;
		
		String[] vals = score.split("m");
		//minutes
		int mins = Integer.parseInt(vals[0].replace(" ", ""));
		int secs = Integer.parseInt(vals[1].replace("s", "").replace(" ", ""));
		secondsScore = mins * 60 + secs;
		
		return secondsScore;
	}
	
	public static String secondsToScoreStr(int seconds) {
		String score = "";
		int mins = seconds / 60;
		int secs = seconds % 60;
		return "" + mins + "m " + secs +"s";
	}
	
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Entry.comparingByValue());

        Map<K, V> result = new LinkedHashMap<>();
        for (Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
}
