package Entities;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import Panels.GamePanel;

public class Tutorial {
	
	private GamePanel gp;
	public int totalStages;
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
	
	public int btnLength = 100;
	public int btnHeight = 20;
	public Rectangle endTutBtn;
	
	private boolean isEndTutBtnBlink;
	private long blinkStartTime;
	private long blinkLength;
	private long blinkElapsed;
	
	public Tutorial(GamePanel gp) {
		this.gp = gp;
		
		endTutBtn = new Rectangle(gp.WIDTH-135, 160, btnLength, btnHeight);
		isEndTutBtnBlink = false;
		blinkStartTime = 0;
		blinkElapsed = 0;
		blinkLength = 1000;
		
		totalStages = 12;
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
	}
	
	public void clearStageKeysPressed() {
		stageKeysPressed.clear();
	}
	
	//reset Tutorial
	public void init() {
		
		gp.player.setScore(0);
		
		for(int i=0; i<totalStages; i++) {
			stageEntered[i] = false;
			stageStartTime[i] = 0;
			//time between 
			stageStartDelay[i] = 0000;
			textFadeTime[i] = 2000;
			textFadeStartTime[i] = 0;
			textRenderStarted[i] = false;
			
			stageReqMet[i] = false;
			//fade out during this time
			nextStageDelay[i] = 2000;
			nextStageQueuedTime[i] = 0;
		}
		
		//override defaults
	
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
			setUpStage(gp.currentTutorialStage);
			clearStageKeysPressed();
		}		
	}
	
	public void setUpStage(int stage) {
		
		gp.player.gainStamina(1000);
		
		if(stage==9) {
			gp.player.addScore(2000);
		}
		if(stage==1) {
			blinkStartTime = System.nanoTime();
			isEndTutBtnBlink = true;
		}
	}
	
	public boolean checkReqMet(int stage) {
		//check if stage requirements are met
		switch(stage) {
		case 0:
			if(getKeyPressedCount(KeyEvent.VK_SPACE) > 0) {
				return true;
			}
			break;
		case 1:
			if(getKeyPressedCount(KeyEvent.VK_Q) > 0) {
				return true;
			}
			break;
		case 2:
			if(getKeyPressedCount(KeyEvent.VK_W) > 0) {
				return true;
			}
			break;
		case 3:
			if(getKeyPressedCount(KeyEvent.VK_E) > 0) {
				return true;
			}
			break;
		case 4:
			if(getKeyPressedCount(KeyEvent.VK_R) > 0) {
				return true;
			}
			break;
		case 5:
			if(getKeyPressedCount(KeyEvent.VK_D) > 0) {
				return true;
			}
			break;
		case 6:
			if(getKeyPressedCount(KeyEvent.VK_X) > 0) {
				return true;
			}
			break;
		case 7:
			if(getKeyPressedCount(KeyEvent.VK_T) > 0 &&
					getKeyPressedCount(KeyEvent.VK_1) + getKeyPressedCount(KeyEvent.VK_2) +
					getKeyPressedCount(KeyEvent.VK_3) + getKeyPressedCount(KeyEvent.VK_4)
					+getKeyPressedCount(KeyEvent.VK_5) > 0
					) {
				return true;
			}
			break;
		case 8:
			if(getKeyPressedCount(KeyEvent.VK_S) > 0) {
				return true;
			}
			break;
		case 9:
			if(gp.player.getScore() < 2000) {
				return true;
			}
			break;
		case 10:
			if(getKeyPressedCount(KeyEvent.VK_F) > 0) {
				return true;
			}
			break;
		case 11:
			return false;
		default: return false;
		}
		
		return false;
	}

	public void render(int stage) {
		Graphics2D g = gp.g;
		
		//blink btn update
		int endTutBtnAlpha = 0;
		blinkElapsed = (System.nanoTime() - blinkStartTime)/1000000;
		if(isEndTutBtnBlink) {
			if(blinkElapsed > blinkLength) {
				blinkStartTime = System.nanoTime();
			} else if(blinkElapsed > blinkLength/2) {
				endTutBtnAlpha = (int) (255 - 255 * blinkElapsed / blinkLength);
			} else {
				endTutBtnAlpha = (int) (255 * blinkElapsed / blinkLength);
			}
			
			if(endTutBtnAlpha< 0)
			{
				endTutBtnAlpha = 0;			
			}
			
			if(endTutBtnAlpha>255) {
				endTutBtnAlpha = 255;
			}
		}
		
		g.setColor(new Color(255,0,0,endTutBtnAlpha));
		g.fillRect(endTutBtn.x, endTutBtn.y, endTutBtn.width, endTutBtn.height);
		g.setFont(new Font("Comic Sans MS", Font.BOLD, 15));
		g.setColor(Color.white);
		g.drawString("End Tutorial", endTutBtn.x+6, endTutBtn.y+15);
		g.draw(endTutBtn);
		
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
		
		if(stageReqMet[stage]) {
			long fadeOutElapsed = (System.nanoTime() -nextStageQueuedTime[stage])/1000000;
			alpha = (int) (255 - 255 * fadeOutElapsed / nextStageDelay[stage]);
			if(alpha < 0) alpha = 0;
		}
		
		Color c;
		Font f;
		String s;		
		
		switch(stage) {
			case 0:				
				//g.setColor(new Color(255,255,255,120));
				//g.fillRect(0,0,gp.WIDTH,gp.HEIGHT);
				c = new Color(255,255,0,alpha);
				f = new Font("Comic Sans MS", Font.BOLD,40);
				s = "Arrow Keys to move\nSpace Bar to Shoot";
				renderText(c,f,s);
				break;
			case 1: 
				c = new Color(255,255,0,alpha);
				f = new Font("Comic Sans MS", Font.BOLD,40);
				s = "Press Q to Push\nEnemies Away! (500 Stamina)";
				renderText(c,f,s);
				break;
			case 2: 
				c = new Color(255,255,0,alpha);
				f = new Font("Comic Sans MS", Font.BOLD,40);
				s = "Press W to Freeze\nEnemies in Range! (800 Stamina)";
				renderText(c,f,s);
				break;
			case 3: 
				c = new Color(255,255,0,alpha);
				f = new Font("Comic Sans MS", Font.BOLD,40);
				s = "Press E to place\nA BlackHole! (700 Stamina)";
				renderText(c,f,s);
				break;
			case 4: 
				c = new Color(255,255,0,alpha);
				f = new Font("Comic Sans MS", Font.BOLD,35);
				s = "Press R to become\nTemporarily Invincible!\n(200 +30/tick Stamina)";
				renderText(c,f,s);
				break;
			case 5: 
				c = new Color(255,255,0,alpha);
				f = new Font("Comic Sans MS", Font.BOLD,40);
				s = "Press D to Dash! (200 Stamina)";
				renderText(c,f,s);
				break;
			case 6: 
				c = new Color(255,255,0,alpha);
				f = new Font("Comic Sans MS", Font.BOLD,40);
				s = "Press X to Place A bomb!\n(Costs 1 Bomb)";
				renderText(c,f,s);
				break;
			case 7: 
				c = new Color(255,255,0,alpha);
				f = new Font("Comic Sans MS", Font.BOLD,25);
				s = "Press T to Place A Turret!\n(200 Stamina)\nAnd press the turret's number\nTo teleport to it!\n[Max 5 Turrets at Once]";
				renderText(c,f,s);
				break;
			case 8: 
				c = new Color(255,255,0,alpha);
				f = new Font("Comic Sans MS", Font.BOLD,30);
				s = "Press S to Place A Shelter!\n(Costs 1 Shelter)\nEnemies can't get to \nyou in here!";
				renderText(c,f,s);
				break;
			case 9: 
				c = new Color(255,255,0,alpha);
				f = new Font("Comic Sans MS", Font.BOLD,40);
				s = "Buy Items from the Shop -->\n<--And See your upgraded stats";
				renderText(c,f,s);
				break;		
			case 10: 
				c = new Color(255,255,0,alpha);
				f = new Font("Comic Sans MS", Font.BOLD,35);
				s = "Press F to quickly collect\n nearby powerups!";
				renderText(c,f,s);
				break;				
			case 11: 
				c = new Color(255,255,0,alpha);
				f = new Font("Comic Sans MS", Font.BOLD,25);
				s = "Try out different types\nOf Power Ups!\nAnd when you're done,\nPress the End Tutorial Button!\n(On the top right)";
				renderText(c,f,s);
				break;	
			default : break;
		}	
	
	}
	
	public void renderText(Color c, Font f, String s) {
		Graphics2D g = gp.g;
		g.setColor(c);
		g.setFont(f);		
		drawMultipleStrings(s);
	}
	
	public void drawMultipleStrings(String text) {
		Graphics2D g = gp.g;
		
		int y = gp.HEIGHT/2+100;
		for (String line : text.split("\n")) {
			int length = (int) g.getFontMetrics().getStringBounds(line, g).getWidth();
            g.drawString(line, (gp.WIDTH-length)/2,  y += g.getFontMetrics().getHeight());
		}
	}
}
