import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

public class Menu {
	
	public int btnLength = 160;
	public int btnHeight = 50;
	public Rectangle playBtn = new Rectangle(GamePanel.WIDTH /2 - btnLength/2, 200, btnLength, btnHeight);
	public Rectangle helpBtn = new Rectangle(GamePanel.WIDTH /2 - btnLength/2, 300, btnLength, btnHeight);
	public Rectangle creditsBtn = new Rectangle(GamePanel.WIDTH /2 - btnLength/2, 400, btnLength, btnHeight);
	public Rectangle quitBtn = new Rectangle(GamePanel.WIDTH /2 - btnLength/2, 500, btnLength, btnHeight);
	
	public Rectangle backFromCreditsBtn = new Rectangle(GamePanel.WIDTH /2 - btnLength/2, 500, btnLength, btnHeight);
	public Rectangle backFromHelpBtn = new Rectangle(GamePanel.WIDTH /2 - btnLength/2, 500, btnLength, btnHeight);
	
	public Rectangle tutorialModeBtn = new Rectangle(GamePanel.WIDTH /2 - btnLength/2, 200, btnLength, btnHeight);
	public Rectangle defaultModeBtn = new Rectangle(GamePanel.WIDTH /2 - btnLength/2, 300, btnLength, btnHeight);
	public Rectangle survivalModeBtn = new Rectangle(GamePanel.WIDTH /2 - btnLength/2, 400, btnLength, btnHeight);
	public Rectangle backFromModesBtn = new Rectangle(GamePanel.WIDTH /2 - btnLength/2, 500, btnLength, btnHeight);
	
	public Rectangle ezpzLvlBtn = new Rectangle(GamePanel.WIDTH /2 - btnLength/2, 200, btnLength, btnHeight);
	public Rectangle sehoonLvlBtn = new Rectangle(GamePanel.WIDTH /2 - btnLength/2, 300, btnLength, btnHeight);
	public Rectangle impossibleLvlBtn = new Rectangle(GamePanel.WIDTH /2 - btnLength/2, 400, btnLength, btnHeight);
	public Rectangle backFromDefaultLvlsBtn = new Rectangle(GamePanel.WIDTH /2 - btnLength/2, 500, btnLength, btnHeight);
	
	public static ArrayList<Explosion> explosions;
	
	public Menu() {
		explosions = new ArrayList<Explosion>();
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
		}
		
		if(GamePanel.menuState == GamePanel.MenuState.HELP) {
			g.setFont(menuTitleFont);
			g.setColor(Color.white);
			renderTitle("Help", g);
			

			g.setFont(menuBtnFont);
			g.drawString("Back", backFromHelpBtn.x+20, backFromHelpBtn.y+35);
			g2d.draw(backFromHelpBtn);
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
}
