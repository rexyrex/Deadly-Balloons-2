package Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import Panels.GamePanel;

public class HighScoreUtils {
	public static void addHighScore(String gameMode, String levelName, String timeStr, String userName) {
		
		int userScore;
		if(gameMode.equals("DefaultLevels")) {
			userScore = getScoreInSeconds(timeStr);
		} else {
			userScore = Integer.parseInt(timeStr);
		}
		
		
		//get current highscores
		String top3Str = getHighscores(gameMode, levelName);
		JSONParser parser = new JSONParser();
		Object obj = null;
		try {
			obj = parser.parse(top3Str);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject top3JSON = (JSONObject) obj;
		
		HashMap<String, Integer> scoreMap = new HashMap<String, Integer>();
		
		Iterator<String> keys = top3JSON.keySet().iterator();
		
		while(keys.hasNext()) {
			String key = keys.next();
			String val = (String) top3JSON.get(key);
			if(gameMode.equals("DefaultLevels")) {
				scoreMap.put(key, getScoreInSeconds(val));
			} else {
				scoreMap.put(key, Integer.parseInt(val));
			}
			
		}
		
		//if username already exists then update score only if better score
		if(scoreMap.containsKey(userName)) {
			if(gameMode.equals("DefaultLevels")) {
				if(scoreMap.get(userName) > userScore) {
					scoreMap.put(userName, userScore);
				} else {
					return;
				}
			} else {
				if(scoreMap.get(userName) < userScore) {
					scoreMap.put(userName, userScore);
				} else {
					return;
				}
			}
			
		} else {
			//add user to highscore sheet
			scoreMap.put(userName, userScore);
		}
		
		Map<String,Integer> sortedScoreMap;
		//sort new scores
		if(gameMode.equals("DefaultLevels")) {
			sortedScoreMap = sortByValue(scoreMap);
		} else {
			sortedScoreMap = sortByValueReverse(scoreMap);
		}		
		
		//convert hashMap to Json		
		JSONObject newJson = new JSONObject();		
		
		//System.out.println("-- sorted --");
		ArrayList<String> keyList = new ArrayList(sortedScoreMap.keySet());		
		for (int i = 0; i < keyList.size(); i++) {
			//get key
			String key = keyList.get(i);
			//System.out.println("Key :: " + key);
			//get value corresponding to key
			int value = sortedScoreMap.get(key);
			//System.out.println("Value :: " + value);
			//System.out.println("--------------------------------");
			if(gameMode.equals("DefaultLevels")){
				newJson.put(key, secondsToScoreStr(value));
			} else {
				//survival
				newJson.put(key, String.valueOf(value));
			}
			
		}	

		//put to server
		RestUtils.put("https://deadly-balloons-2.firebaseio.com/HighScores/" + gameMode + "/" + levelName + ".json", newJson.toJSONString());
		
	}
	
	public static void populateAllHighScores() {
		//Personal Record
		HashMap<String, String> personalBestMap = new HashMap<String, String>();
		
		
		String[] defaultLevelNames = {"Rex","Classic", "MrYang"};
		
		//Default Levels 
		HashMap<String, Map<String,String>> defaultLevels = new HashMap<String, Map<String,String>>();
		for(String lvlName : defaultLevelNames) {
			defaultLevels.put(lvlName, getTopFive("DefaultLevels", lvlName));
			personalBestMap.put(lvlName, getPersonalHighscore("DefaultLevels", lvlName));
		}
		
		String[] survivalLevelNames = {"Bigger","Charge", "Shooter"};
		
		//Survival Levels 
		HashMap<String, Map<String,String>> survivalLevels = new HashMap<String, Map<String,String>>();
		for(String lvlName : survivalLevelNames) {
			survivalLevels.put(lvlName, getTopFive("SurvivalLevels", lvlName));
			personalBestMap.put(lvlName, getPersonalHighscore("SurvivalLevels", lvlName));
		}
		
		GamePanel.highScoreMap.put("DefaultLevels", defaultLevels);
		GamePanel.highScoreMap.put("SurvivalLevels", survivalLevels);
		GamePanel.personalBestMap.put("PersonalRecords", personalBestMap);
	}
	
	public static Map<String, String> getTopFive(String gameMode, String levelName){
		Map<String,String> topFiveMap = new LinkedHashMap<String,String>();
		
		//get current highscores
		String top3Str = getHighscores(gameMode, levelName);
		JSONParser parser = new JSONParser();
		Object obj = null;
		try {
			obj = parser.parse(top3Str);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		JSONObject top3JSON = (JSONObject) obj;
		
		HashMap<String, Integer> scoreMap = new HashMap<String, Integer>();
		
		Iterator<String> keys = top3JSON.keySet().iterator();
		
		while(keys.hasNext()) {
			String key = keys.next();
			String val = (String) top3JSON.get(key);
			//System.out.println("key/val = " + key + " = " + getScoreInSeconds(val));
			if(gameMode.equals("DefaultLevels")) {
				scoreMap.put(key, getScoreInSeconds(val));
			} else {
				//survival
				scoreMap.put(key, Integer.parseInt(val));
			}			
		}
		
		//sort new scores
		Map<String,Integer> sortedScoreMap;
		if(gameMode.equals("DefaultLevels")) {
			sortedScoreMap = sortByValue(scoreMap);
		} else {
			sortedScoreMap = sortByValueReverse(scoreMap);
		}
		
		
		//convert hashMap to Json
		
		JSONObject newJson = new JSONObject();
		
		
		//System.out.println("-- sorted --");
		ArrayList<String> keyList = new ArrayList(sortedScoreMap.keySet());		
		for (int i = 0; i < keyList.size(); i++) {
			//get key
			String key = keyList.get(i);
			//System.out.println("Key :: " + key);
			//get value corresponding to key
			int value = sortedScoreMap.get(key);
			//System.out.println("Value :: " + value);
			//System.out.println("--------------------------------");
			if(gameMode.equals("DefaultLevels")) {
				topFiveMap.put(key, secondsToScoreStr(value));
			} else {
				topFiveMap.put(key, String.valueOf(value));
			}
			if(i == 4) {
				break;
			}
		}	
		
		return topFiveMap;
	}
	
	public static String getPersonalHighscore(String gameMode, String levelName) {
		String highScore = RestUtils.get("https://deadly-balloons-2.firebaseio.com/HighScores/" + gameMode + "/" + levelName + "/" + GamePanel.username + ".json");
		if(null == (highScore)) {
			highScore = "No Score";
		} else if(highScore.equals("null")) {
			highScore = "No Score";
		}
		
		return GamePanel.username + " : " + highScore;
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
    
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValueReverse(Map<K, V> map) {
        List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Entry.comparingByValue());
        
        Collections.reverse(list);
        
        Map<K, V> result = new LinkedHashMap<>();
        for (Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
}
