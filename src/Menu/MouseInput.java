package Menu;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import Entities.Tutorial;
import Panels.GamePanel;
import Utils.HighScoreUtils;

public class MouseInput implements MouseListener{

	private Menu menu;
	private GamePanel gp;
	private Tutorial tutorial;
	
	public MouseInput(Menu menu, Tutorial tutorial, GamePanel gp) {
		this.menu = menu;
		this.gp = gp;
		this.tutorial = tutorial;
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
					gp.sfx.get("menu select").play();
					GamePanel.menuState = GamePanel.MenuState.PLAY_MODES;
					return;
				}
				
				//play btn
				if(mouseIn(mx,my,menu.highScoresBtn)) {
					gp.sfx.get("menu select").play();
					GamePanel.menuState = GamePanel.MenuState.HIGH_SCORES;
					return;
				}
				
				//help btn
				if(mouseIn(mx,my,menu.helpBtn)) {
					gp.sfx.get("menu select").play();
					GamePanel.menuState = GamePanel.MenuState.HELP;
					return;
				}				
				
				//credits btn
				if(mouseIn(mx,my,menu.creditsBtn)) {
					gp.sfx.get("menu select").play();
					GamePanel.menuState = GamePanel.MenuState.CREDITS;
					return;
				}				
				
				//quit btn
				if(mouseIn(mx,my,menu.quitBtn)) {
					gp.sfx.get("menu back").play();
						System.exit(1);
				}
				
			}
			
			//Modes Menu
			if(GamePanel.menuState == GamePanel.MenuState.PLAY_MODES) {			
				//tutorial btn
				if(mouseIn(mx,my,menu.tutorialModeBtn)) {
					gp.sfx.get("menu select").play();
					gp.initNewLvl("Tutorial", GamePanel.GameMode.TUTORIAL, false);
					GamePanel.gameState = GamePanel.GameState.PLAY;
					return;
				}
				
				//default btn
				if(mouseIn(mx,my,menu.defaultModeBtn)) {
					gp.sfx.get("menu select").play();
					GamePanel.menuState = GamePanel.MenuState.DEFAULT_LEVELS;
					return;
				}				
				
				//survival btn
				if(mouseIn(mx,my,menu.survivalModeBtn)) {
					gp.sfx.get("menu select").play();
					//GamePanel.menuState = GamePanel.MenuState.SURVIVAL_LEVELS;
					return;
				}
				
				//back btn
				if(mouseIn(mx,my,menu.backFromModesBtn)) {
					gp.sfx.get("menu back").play();
					GamePanel.menuState = GamePanel.MenuState.MAIN;
					return;
				}
				
			}
			
			//Default Levels Menu
			if(GamePanel.menuState == GamePanel.MenuState.DEFAULT_LEVELS) {			
				//classic lvl btn
				if(mouseIn(mx,my,menu.ezpzLvlBtn)) {
					gp.sfx.get("menu select").play();
					gp.initNewLvl("Classic", GamePanel.GameMode.DEFAULT, false);
					GamePanel.gameState = GamePanel.GameState.PLAY;					
					return;
				}
				
				//sehoon lvl btn
				if(mouseIn(mx,my,menu.sehoonLvlBtn)) {
					gp.sfx.get("menu select").play();
					gp.initNewLvl("MrYang", GamePanel.GameMode.DEFAULT, false);
					GamePanel.gameMode = GamePanel.GameMode.DEFAULT;
					GamePanel.gameState = GamePanel.GameState.PLAY;					
					return;
				}				
				
				//survival btn
				if(mouseIn(mx,my,menu.impossibleLvlBtn)) {
					gp.sfx.get("menu select").play();
					//GamePanel.gameMode = GamePanel.GameMode.DEFAULT;
					//GamePanel.gameState = GamePanel.GameState.PLAY;
					return;
				}
				
				//back btn
				if(mouseIn(mx,my,menu.backFromDefaultLvlsBtn)) {
					gp.sfx.get("menu back").play();
					GamePanel.menuState = GamePanel.MenuState.PLAY_MODES;
					return;
				}
				
			}
			
			//Credits Menu
			if(GamePanel.menuState == GamePanel.MenuState.CREDITS) {
				//back btn
				if(mouseIn(mx,my,menu.backFromCreditsBtn)) {
					gp.sfx.get("menu back").play();
					GamePanel.menuState = GamePanel.MenuState.MAIN;
					return;
				}
			}
			
			//Help Menu
			if(GamePanel.menuState == GamePanel.MenuState.HELP) {
				//controls btn
				if(mouseIn(mx,my,menu.goToControlsBtn)) {
					gp.sfx.get("menu select").play();
					GamePanel.menuState = GamePanel.MenuState.CONTROLS;
					return;
				}
			}		
			
			//Controls Menu
			if(GamePanel.menuState == GamePanel.MenuState.CONTROLS) {
				//back btn
				if(mouseIn(mx,my,menu.controlsToMainBtn)) {
					gp.sfx.get("menu back").play();
					GamePanel.menuState = GamePanel.MenuState.MAIN;
					return;
				}
			}		
			
			//High Scores Menu
			if(GamePanel.menuState == GamePanel.MenuState.HIGH_SCORES) {
				//back btn
				if(mouseIn(mx,my,menu.backFromHighScoresBtn)) {
					HighScoreUtils.getHighscores("DefaultLevels", "MrYang");
					gp.sfx.get("menu back").play();
					GamePanel.menuState = GamePanel.MenuState.MAIN;
					return;
				}
			}	
		}
		
		//Game is Running State
		if(GamePanel.gameState == GamePanel.GameState.PLAY) {
			//Tutorial Mode
			if(GamePanel.gameMode == GamePanel.GameMode.TUTORIAL) {
				//end tutorial btn
				if(mouseIn(mx,my,tutorial.endTutBtn)) {
					gp.sfx.get("menu back").play();
					GamePanel.gameState = GamePanel.GameState.GAME_OVER;
					return;
				}
			}
		}
		
		//Game Over Menu
		if(GamePanel.gameState == GamePanel.GameState.GAME_OVER) {
			//replay
			if(mouseIn(mx,my,gp.retryLvlBtn)) {
				gp.sfx.get("menu select").play();
				gp.initNewLvl("", gp.gameMode,true);
				GamePanel.gameState = GamePanel.GameState.PLAY;
				return;
			}
			//back to main menu
			if(mouseIn(mx,my,gp.backFromGameOverBtn)) {
				gp.sfx.get("menu back").play();
				GamePanel.menuState = GamePanel.MenuState.MAIN;
				GamePanel.gameState = GamePanel.GameState.MENU;
				return;
			}
		}
		
		//Paused Menu
		if(GamePanel.gameState == GamePanel.GameState.PAUSED) {
			//quit (back to main menu)
			if(mouseIn(mx,my,gp.quitFromPauseBtn)) {
				gp.sfx.get("menu back").play();
				GamePanel.menuState = GamePanel.MenuState.MAIN;
				GamePanel.gameState = GamePanel.GameState.MENU;
				return;
			}
			//resume
			if(mouseIn(mx,my,gp.resumeFromPausedBtn)) {
				gp.sfx.get("menu select").play();
				gp.resumeGame();
				return;
			}
			
			//show/hide keyboard
			if(mouseIn(mx,my,gp.pauseHideKeyboardBtn)) {
				gp.sfx.get("menu select").play();
				gp.pauseHideKeyboard = !gp.pauseHideKeyboard;
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
