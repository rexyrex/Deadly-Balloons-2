package Audio;

import java.io.BufferedInputStream;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class AudioPlayer {
	
	private Clip clip;
	
	public AudioPlayer(String s){
		try{
			
			try (InputStream in = getClass().getResourceAsStream(s)) {
	            InputStream bufferedIn = new BufferedInputStream(in);
	            try (AudioInputStream audioIn = AudioSystem.getAudioInputStream(bufferedIn)) {
	                clip = AudioSystem.getClip();
	                clip.open(audioIn);
	            }
	        } catch (Exception e) {
	           e.printStackTrace();
	       }
			
//			AudioInputStream ais = 
//					AudioSystem.getAudioInputStream(
//						getClass().getResourceAsStream(
//							s								
//								)	
//							);
//			AudioFormat baseFormat = ais.getFormat();
//			AudioFormat decodeFormat = new AudioFormat(
//					AudioFormat.Encoding.PCM_SIGNED,
//					baseFormat.getSampleRate(),
//					16,
//					baseFormat.getChannels(),
//					baseFormat.getChannels() * 2,
//					baseFormat.getSampleRate(),
//					false
//			);
//			AudioInputStream dais = 
//					AudioSystem.getAudioInputStream(
//							decodeFormat, ais);
//			clip = AudioSystem.getClip();
//			clip.open(dais);
			
		} catch(Exception e){}
		
	}
	
	public void play(){
		if(clip == null) return;
		stop();
		clip.setFramePosition(0);
		clip.start();
	}
	
	public void playLoop() {
		if(clip == null) return;
		stop();
		clip.setFramePosition(0);
		clip.loop(Clip.LOOP_CONTINUOUSLY);
	}
	
	public void stop(){
		if(clip.isRunning()) clip.stop();
	}
	
	public void close(){
		stop();
		clip.close();
	}
}
