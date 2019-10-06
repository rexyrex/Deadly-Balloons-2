import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MouseInput implements MouseListener{

	private Menu menu;
	private GamePanel gp;
	
	public MouseInput(Menu menu, GamePanel gp) {
		this.menu = menu;
		this.gp = gp;
	}
	
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		int mx = e.getX();
		int my = e.getY();
		
		if(GamePanel.gameState == GamePanel.GameState.MENU) {
			//click anim
			menu.clickAnim(gp.getGraphics(), mx, my);
			
			//Main Menu
			if(GamePanel.menuState == GamePanel.MenuState.MAIN) {			
				//play btn
				if(mouseIn(mx,my,menu.playBtn)) {
					GamePanel.menuState = GamePanel.MenuState.PLAY_MODES;
					return;
				}
				
				//help btn
				if(mouseIn(mx,my,menu.helpBtn)) {
					GamePanel.menuState = GamePanel.MenuState.HELP;
					return;
				}				
				
				//credits btn
				if(mouseIn(mx,my,menu.creditsBtn)) {
					GamePanel.menuState = GamePanel.MenuState.CREDITS;
					return;
				}				
				
				//quit btn
				if(mouseIn(mx,my,menu.quitBtn)) {
						System.exit(1);
				}
				
			}
			
			//Modes Menu
			if(GamePanel.menuState == GamePanel.MenuState.PLAY_MODES) {			
				//tutorial btn
				if(mouseIn(mx,my,menu.tutorialModeBtn)) {
					//GamePanel.gameMode = GamePanel.GameMode.TUTORIAL;
					//GamePanel.gameState = GamePanel.GameState.PLAY;
					return;
				}
				
				//default btn
				if(mouseIn(mx,my,menu.defaultModeBtn)) {
					GamePanel.menuState = GamePanel.MenuState.DEFAULT_LEVELS;
					return;
				}				
				
				//survival btn
				if(mouseIn(mx,my,menu.survivalModeBtn)) {
					//GamePanel.menuState = GamePanel.MenuState.SURVIVAL_LEVELS;
					return;
				}
				
				//back btn
				if(mouseIn(mx,my,menu.backFromModesBtn)) {
					GamePanel.menuState = GamePanel.MenuState.MAIN;
					return;
				}
				
			}
			
			//Default Levels Menu
			if(GamePanel.menuState == GamePanel.MenuState.DEFAULT_LEVELS) {			
				//ezpz lvl btn
				if(mouseIn(mx,my,menu.ezpzLvlBtn)) {
					gp.initNewLvl("EZPZ", GamePanel.GameMode.DEFAULT, false);
					GamePanel.gameState = GamePanel.GameState.PLAY;					
					return;
				}
				
				//sehoon lvl btn
				if(mouseIn(mx,my,menu.sehoonLvlBtn)) {
					gp.initNewLvl("MrYang", GamePanel.GameMode.DEFAULT, false);
					GamePanel.gameMode = GamePanel.GameMode.DEFAULT;
					GamePanel.gameState = GamePanel.GameState.PLAY;					
					return;
				}				
				
				//survival btn
				if(mouseIn(mx,my,menu.impossibleLvlBtn)) {
					//GamePanel.gameMode = GamePanel.GameMode.DEFAULT;
					//GamePanel.gameState = GamePanel.GameState.PLAY;
					return;
				}
				
				//back btn
				if(mouseIn(mx,my,menu.backFromDefaultLvlsBtn)) {
					GamePanel.menuState = GamePanel.MenuState.PLAY_MODES;
					return;
				}
				
			}
			
			//Credits Menu
			if(GamePanel.menuState == GamePanel.MenuState.CREDITS) {
				//back btn
				if(mouseIn(mx,my,menu.backFromCreditsBtn)) {
					GamePanel.menuState = GamePanel.MenuState.MAIN;
					return;
				}
			}
			
			//Help Menu
			if(GamePanel.menuState == GamePanel.MenuState.HELP) {
				//back btn
				if(mouseIn(mx,my,menu.backFromHelpBtn)) {
					GamePanel.menuState = GamePanel.MenuState.MAIN;
					return;
				}
			}			
		}
		
		//Game Over Menu
		if(GamePanel.gameState == GamePanel.GameState.GAME_OVER) {
			//replay
			if(mouseIn(mx,my,gp.retryLvlBtn)) {
				gp.initNewLvl("", gp.gameMode,true);
				GamePanel.gameState = GamePanel.GameState.PLAY;
				return;
			}
			//back to main menu
			if(mouseIn(mx,my,gp.backFromGameOverBtn)) {
				GamePanel.menuState = GamePanel.MenuState.MAIN;
				GamePanel.gameState = GamePanel.GameState.MENU;
				return;
			}
		}
		
	}
	
	public boolean mouseIn(int mx, int my, Rectangle r) {
		if(mx >= r.x && mx <= r.x + r.width) {
			if(my >= r.y && my <= r.y + r.height) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
