package Menu;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

import Panels.GamePanel;
import Utils.RestUtils;
import VFX.Explosion;

public class Menu {
	
	public int btnLength = 160;
	public int btnHeight = 50;
	public Rectangle playBtn = new Rectangle(GamePanel.WIDTH /2 - btnLength/2, 200, btnLength, btnHeight);
	public Rectangle helpBtn = new Rectangle(GamePanel.WIDTH /2 - btnLength/2, 300, btnLength, btnHeight);
	public Rectangle creditsBtn = new Rectangle(GamePanel.WIDTH /2 - btnLength/2, 400, btnLength, btnHeight);
	public Rectangle quitBtn = new Rectangle(GamePanel.WIDTH /2 - btnLength/2, 500, btnLength, btnHeight);
	
	public Rectangle backFromCreditsBtn = new Rectangle(GamePanel.WIDTH /2 - btnLength/2, 500, btnLength, btnHeight);
	public Rectangle goToControlsBtn = new Rectangle(GamePanel.WIDTH /2 - btnLength/2, 600, btnLength, btnHeight);
	public Rectangle controlsToMainBtn = new Rectangle(GamePanel.WIDTH /2 - btnLength/2, 600, btnLength, btnHeight);
	
	public Rectangle tutorialModeBtn = new Rectangle(GamePanel.WIDTH /2 - btnLength/2, 200, btnLength, btnHeight);
	public Rectangle defaultModeBtn = new Rectangle(GamePanel.WIDTH /2 - btnLength/2, 300, btnLength, btnHeight);
	public Rectangle survivalModeBtn = new Rectangle(GamePanel.WIDTH /2 - btnLength/2, 400, btnLength, btnHeight);
	public Rectangle backFromModesBtn = new Rectangle(GamePanel.WIDTH /2 - btnLength/2, 500, btnLength, btnHeight);
	
	public Rectangle ezpzLvlBtn = new Rectangle(GamePanel.WIDTH /2 - btnLength/2, 200, btnLength, btnHeight);
	public Rectangle sehoonLvlBtn = new Rectangle(GamePanel.WIDTH /2 - btnLength/2, 300, btnLength, btnHeight);
	public Rectangle impossibleLvlBtn = new Rectangle(GamePanel.WIDTH /2 - btnLength/2, 400, btnLength, btnHeight);
	public Rectangle backFromDefaultLvlsBtn = new Rectangle(GamePanel.WIDTH /2 - btnLength/2, 500, btnLength, btnHeight);
	
	public boolean loadedHighScores;
	
	public static ArrayList<Explosion> explosions;
	
	private GamePanel gp;
	
	public Menu(GamePanel gp) {
		explosions = new ArrayList<Explosion>();
		loadedHighScores = false;
		this.gp = gp;
	}
	
	public void clickAnim(Graphics g, int x, int y) {
		Graphics2D g2d = (Graphics2D) g;
		explosions.add(new Explosion(x, y, 10, 50, 5, new Color(255,255,255,255)));
		explosions.add(new Explosion(x, y, 10, 50, 2.5, new Color(255,255,255,255)));
		explosions.add(new Explosion(x, y, 10, 50, 3.5, new Color(255,255,255,255)));
	}
	
	public void clearMenuAnims() {
		explosions.clear();
	}
	
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		Font menuTitleFont = new Font("Gulim", Font.BOLD, 50);
		Font menuBtnFont = new Font("Gulim", Font.PLAIN, 30);

		
		g.setColor(new Color(0,0,0));
		g.fillRect(0,0,GamePanel.WIDTH,GamePanel.HEIGHT);	
		
		//draw explosion
		for(int i=0; i<explosions.size(); i++){
			explosions.get(i).draw(g2d);
		}
		
		//explosion update
		for(int i=0; i<explosions.size(); i++){
			boolean remove = explosions.get(i).update();
			if(remove){
				explosions.remove(i);
				i--;
			}
		}
		
		if(GamePanel.menuState == GamePanel.MenuState.MAIN) {
			
			g.setFont(menuTitleFont);
			g.setColor(Color.white);
			
			renderTitle("Deadly Balloons 2.0", g);
						
			g.setFont(menuBtnFont);
			
			g.drawString("Play", playBtn.x+20, playBtn.y+35);
			g2d.draw(playBtn);
			
			g.drawString("Help", helpBtn.x+20, helpBtn.y+35);
			g2d.draw(helpBtn);
			
			g.drawString("Credits", creditsBtn.x+20, creditsBtn.y+35);
			g2d.draw(creditsBtn);
			
			g.drawString("Quit", quitBtn.x+20, quitBtn.y+35);
			g2d.draw(quitBtn);
		}
		
		if(GamePanel.menuState == GamePanel.MenuState.CREDITS) {
			g.setFont(menuTitleFont);
			g.setColor(Color.white);
			renderTitle("Credits", g);
			
			g.setFont(menuBtnFont);
			g.drawString("Back", backFromCreditsBtn.x+20, backFromCreditsBtn.y+35);
			g2d.draw(backFromCreditsBtn);
			
			if(!loadedHighScores) {
				RestUtils.get("https://deadly-balloons-2.firebaseio.com/DefaultLevels/MrYang.json");
			}
			
			loadedHighScores = true;
		}
		
		if(GamePanel.menuState == GamePanel.MenuState.HELP) {
			g.setFont(menuTitleFont);
			g.setColor(Color.white);
			renderTitle("Help", g);
			
			Color c = new Color(255,255,255,255);
			Font f = new Font("TimesRoman", Font.PLAIN,20);
			String s = 
						"Welcome to Deadly Balloons 2.0\n" + 
						"The objective is to pop all the balloons without dying!\n" +
						"\n" +
						"Remember to spend your money on upgrades\n"+
						"from the shop on the right\n" +
						"Check out your upgraded stats on the left panel\n"+
						"\n"+
						"You have a plethora of abilities in your arsenal\n"+
						"Make good use of every ability to defeat the final boss!\n"+
						"\n"+
						"If you are new, check out the tutorial\n"+
						"and practice on the Easy Levels first!\n"+
						"\n"+
						"Tis a high skill-cap game :) Good Luck!"
						;
			renderText(c,f,s, 50, 120);
			

			g.setFont(menuBtnFont);
			g.drawString("Controls", goToControlsBtn.x+20, goToControlsBtn.y+35);
			g2d.draw(goToControlsBtn);
		}
		
		if(GamePanel.menuState == GamePanel.MenuState.HELP) {
			g.setFont(menuTitleFont);
			g.setColor(Color.white);
			renderTitle("Help", g);
			
			Color c = new Color(255,255,255,255);
			Font f = new Font("TimesRoman", Font.PLAIN,20);
			String s = 
						"Key / Ability / Stamina Cost / Other Requirements\n" + 
						"SPACE / Fire / 0 / None\n" +
						"Q / Push Enemies Away / 500 / None\n" +
						"W / Freeze Enemies in AOE / 800 / None\n"+
						"E / Place Black Hole / 700 / None\n" +
						"R / Invincibility Toggle / 200 + 30 per tick / None\n"+
						"D / Dash (Temp speed boost) / 200 / None\n"+
						"X / Bomb / 0 / 1 Bomb (dropped by enemies)\n"+
						"A / Toggle Addon / 0 / At least 1 Addon (dropped)\n"+
						"T / Place Turret / 400 / Max 5 Turrets\n"+
						"S / Place Shelter / 0 / 1 Shelter (dropped)\n"+
						"F / Collect / 700 / None\n"+
						"NUMPAD 0 / Pause / 0 / None\n"+
						""
						;
			renderText(c,f,s, 50, 120);
			

			g.setFont(menuBtnFont);
			g.drawString("Main Menu", controlsToMainBtn.x+20, controlsToMainBtn.y+35);
			g2d.draw(controlsToMainBtn);
		}		
		
		if(GamePanel.menuState == GamePanel.MenuState.PLAY_MODES) {
			g.setFont(menuTitleFont);
			g.setColor(Color.white);
			renderTitle("Modes", g);

			g.setFont(menuBtnFont);
			
			g.drawString("Tutorial", tutorialModeBtn.x+20, tutorialModeBtn.y+35);
			g2d.draw(tutorialModeBtn);
			
			g.drawString("Default", defaultModeBtn.x+20, defaultModeBtn.y+35);
			g2d.draw(defaultModeBtn);
			
			g.drawString("Survival", survivalModeBtn.x+20, survivalModeBtn.y+35);
			g2d.draw(survivalModeBtn);
			
			g.drawString("Back", backFromModesBtn.x+20, backFromModesBtn.y+35);
			g2d.draw(backFromModesBtn);
		}
		
		if(GamePanel.menuState == GamePanel.MenuState.DEFAULT_LEVELS) {
			g.setFont(menuTitleFont);
			g.setColor(Color.white);
			renderTitle("Default Levels", g);

			g.setFont(menuBtnFont);
			
			g.setColor(Color.green);
			g.drawString("EZPZ", ezpzLvlBtn.x+20, ezpzLvlBtn.y+35);
			g2d.draw(ezpzLvlBtn);
			
			g.setColor(Color.yellow);
			g.drawString("Mr. Yang", sehoonLvlBtn.x+20, sehoonLvlBtn.y+35);
			g2d.draw(sehoonLvlBtn);
			
			g.setColor(Color.red);
			g.drawString("Rex", impossibleLvlBtn.x+20, impossibleLvlBtn.y+35);
			g2d.draw(impossibleLvlBtn);
			
			g.setColor(Color.white);
			g.drawString("Back", backFromDefaultLvlsBtn.x+20, backFromDefaultLvlsBtn.y+35);
			g2d.draw(backFromDefaultLvlsBtn);
		}

	}
	
	private void renderTitle(String title, Graphics g) {
		int length = (int) g.getFontMetrics().getStringBounds(title, g).getWidth();
		g.drawString(title, (GamePanel.WIDTH-length)/2, 100);
	}
	
	public void renderText(Color c, Font f, String s, int x, int y) {
		Graphics2D g = gp.g;
		g.setColor(c);
		g.setFont(f);		
		drawMultipleStrings(s,x,y);
	}
	
	public void drawMultipleStrings(String text, int x, int y) {
		Graphics2D g = gp.g;
		for (String line : text.split("\n")) {
            g.drawString(line, x,  y += g.getFontMetrics().getHeight());
		}
	}
}
