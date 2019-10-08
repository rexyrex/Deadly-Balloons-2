import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.HashMap;

public class Tutorial {
	
	private GamePanel gp;
	private int totalStages;
	private boolean[] stageEntered;
	private long[] stageStartDelay;
	private long[] stageStartTime;
	
	private long[] textFadeTime;
	private long[] textFadeStartTime;
	private boolean[] textRenderStarted;
	
	private boolean[] stageReqMet;
	private long[] nextStageDelay;
	private long[] nextStageQueuedTime;
	
	public HashMap<Integer, Integer> stageKeysPressed;
	
	
	public Tutorial(GamePanel gp) {
		this.gp = gp;
		
		totalStages = 10;
		stageEntered = new boolean[totalStages];
		stageStartDelay = new long[totalStages];
		stageStartTime = new long[totalStages];
		textFadeTime = new long[totalStages];
		textFadeStartTime = new long[totalStages];
		textRenderStarted = new boolean[totalStages];
		nextStageQueuedTime = new long[totalStages];
		
		nextStageDelay = new long[totalStages];
		stageReqMet= new boolean[totalStages];
		
		stageKeysPressed = new HashMap<Integer,Integer>();
		
		init();
	}
	
	public void clearStageKeysPressed() {
		stageKeysPressed.clear();
	}
	
	//reset Tutorial
	public void init() {
		for(int i=0; i<totalStages; i++) {
			stageEntered[i] = false;
			stageStartTime[i] = 0;
			stageStartDelay[i] = 5000;
			textFadeTime[i] = 2000;
			textFadeStartTime[i] = 0;
			textRenderStarted[i] = false;
			
			stageReqMet[i] = false;
			nextStageDelay[i] = 3000;
			nextStageQueuedTime[i] = 0;
		}
	}
	
	public int getKeyPressedCount(int keyCode) {
		
		if(!stageKeysPressed.containsKey(keyCode)) {
			return 0;
		}
		
		return stageKeysPressed.get(keyCode);
	}
	
	public void updateKeyPressedCount(int keyCode) {
		if(!stageKeysPressed.containsKey(keyCode)) {
			stageKeysPressed.put(keyCode, 1);
		} else {
			stageKeysPressed.put(keyCode, stageKeysPressed.get(keyCode) + 1);
		}
	}
	
	public void update(int stage) {
		//inc stage if conditions are met
		
		if(!stageReqMet[stage]) {
			
			stageReqMet[stage] = checkReqMet(stage);
			
			if(stageReqMet[stage]) {
				nextStageQueuedTime[stage] = System.nanoTime();
			}		
			
		} 
		
		long elapsed = (System.nanoTime() - nextStageQueuedTime[stage])/1000000;
		if(stageReqMet[stage] && elapsed > nextStageDelay[stage]) {
			gp.currentTutorialStage++;
			clearStageKeysPressed();
		}		
	}
	
	public boolean checkReqMet(int stage) {
		//check if stage requirements are met
		switch(stage) {
		case 0:
			if(getKeyPressedCount(KeyEvent.VK_Z) > 0) {
				return true;
			}
			break;
		
		
		default: return false;
		}
		
		return false;
	}

	public void render(int stage) {
		Graphics2D g = gp.g;
		
		if(!stageEntered[stage]) {
			stageEntered[stage] = true;
			stageStartTime[stage] = System.nanoTime();
		}
		
		long elapsed = (System.nanoTime() - stageStartTime[stage])/1000000;
		//System.out.println(elapsed);
		if(elapsed < stageStartDelay[stage]) {
			return;
		}
		
		if(!textRenderStarted[stage]) {
			textRenderStarted[stage] = true;
			textFadeStartTime[stage] = System.nanoTime();
		}
		
		long textElapsed = (System.nanoTime() - textFadeStartTime[stage])/1000000;
		//System.out.println("text Elapsed: " + textElapsed);
		int alpha = (int) (255 * textElapsed / textFadeTime[stage]);
		if(alpha > 255) alpha = 255;
		//System.out.println("Alpha: " + alpha);
		
		Color c;
		Font f;
		String s;		
		
		switch(stage) {
			case 0:				
				
				//g.setColor(new Color(255,255,255,120));
				//g.fillRect(0,0,gp.WIDTH,gp.HEIGHT);
				c = new Color(255,255,0,alpha);
				f = new Font("Gulim", Font.BOLD,40);
				s = "FIRST HINT";
				renderText(c,f,s);
				break;
			case 1: 
				c = new Color(255,255,0,alpha);
				f = new Font("Gulim", Font.BOLD,40);
				s = "Second HINT";
				renderText(c,f,s);
				break;
			default : break;
		}	
	
	}
	
	public void renderText(Color c, Font f, String s) {
		Graphics2D g = gp.g;
		g.setColor(c);
		g.setFont(f);
		int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
		g.drawString(s, (gp.WIDTH-length)/2, gp.HEIGHT/2);
	}
}
