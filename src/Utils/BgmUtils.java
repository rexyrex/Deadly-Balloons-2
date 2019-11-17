package Utils;

import java.util.HashMap;
import java.util.Map;

import Audio.AudioPlayer;

public class BgmUtils {
	public static HashMap<String, AudioPlayer> bgm;
	
	public BgmUtils() {
		bgm = new HashMap<String, AudioPlayer>();
		
		bgm.put("Rex", new AudioPlayer("/Music/adventures.wav"));
		bgm.put("Menu", new AudioPlayer("/Music/Chillstep_2.wav"));
		bgm.put("Classic", new AudioPlayer("/Music/FutureAmbient_1.wav"));
		bgm.put("MrYang", new AudioPlayer("/Music/HipHopNoir_1.wav"));
		bgm.put("Tutorial", new AudioPlayer("/Music/8Bit_4.wav"));
		
		bgm.put("Bigger", new AudioPlayer("/Music/DirtyElectroHouse_5.wav"));
		bgm.put("Shooter", new AudioPlayer("/Music/DarkDnB_1.wav"));
		bgm.put("Charge", new AudioPlayer("/Music/EDM_1.wav"));
		
	}
	
	public void playBgm(String title) {
		//stop previous bgm
		for (Map.Entry<String, AudioPlayer> bgmEntry : bgm.entrySet()) {
			bgmEntry.getValue().stop();
		}	
		
		bgm.get(title).playLoop();
	}
}
