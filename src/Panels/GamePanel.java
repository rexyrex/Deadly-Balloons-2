package Panels;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import Audio.AudioPlayer;
import Entities.BlackHole;
import Entities.Bomb;
import Entities.Bullet;
import Entities.Enemy;
import Entities.EnemyBullet;
import Entities.Friend;
import Entities.Lightning;
import Entities.Player;
import Entities.PowerUp;
import Entities.Shelter;
import Entities.SlowField;
import Entities.SpawnIndicator;
import Entities.Torpedo;
import Entities.Turret;
import Entities.Tutorial;
import Menu.Menu;
import Menu.MouseInput;
import Utils.HighScoreUtils;
import Utils.ImageUtils;
import Utils.MathUtils;
import Utils.RandomUtils;
import Utils.StringUtils;
import VFX.Explosion;
import VFX.FootPrint;
import VFX.ParticleEffect;
import VFX.Text;

public class GamePanel extends JPanel implements Runnable, KeyListener{
	
	/**
	 * 
	 */
	private JFrame jframe;
	
	private static final long serialVersionUID = 1L;
	public static int WIDTH = 700;
	public static int HEIGHT = 700;
	
	private AudioPlayer bgmusic;
	public static HashMap<String, AudioPlayer> sfx;
	
	private Thread thread;
	private boolean running;
	private boolean victorious;
	
	private ArrayList<String> waveNames;
	private ArrayList<HashMap<Enemy, Integer>> waveData;
	private String levelTitle;
	
	private BufferedImage image;
	public Graphics2D g;
	
	private int FPS = 30;
	//private double averageFPS;
	
	public static Player player;
	public static ArrayList<Bullet> bullets;
	public static ArrayList<Enemy> enemies;
	public static ArrayList<Friend> friends;
	public static ArrayList<PowerUp> powerups;
	public static ArrayList<Explosion> explosions;
	public static ArrayList<Text> texts;
	public static ArrayList<Turret> turrets;
	public static ArrayList<BlackHole> blackholes;
	public static ArrayList<Bomb> bombs;
	public static ArrayList<Shelter> shelters;
	public static ArrayList<Lightning> lightnings;
	public static ArrayList<Torpedo> torpedos;
	public static ArrayList<SpawnIndicator> spawnIndicators;
	public static ArrayList<FootPrint> footPrints;
	public static ArrayList<ParticleEffect> particleEffects;
	public static ArrayList<EnemyBullet> enemyBullets;
	public static ArrayList<SlowField> slowFields;
	
	//highscores
	public static HashMap<String, HashMap<String, Map<String,String>>> highScoreMap;
	
	//key presses
	private Set<Integer> keysPressed = new HashSet<Integer>();
	
	//Drop related
	public static HashMap<Integer, Long> puDropElapsedMap;
	public static HashMap<Integer, Long> puLastDropTimeMap;
	public static HashMap<Integer, Long> puDropTimeMap;
	public static HashMap<Integer, Double> puDropRateMap;
	public static HashMap<Integer, Integer> puCountMap;
	public static HashMap<Integer, Integer> puMinWaveMap;
	
	private double puDropRates[] = {
			0.17, // 1. extra life
			0.35, // 2. power +1
			0.7, // 3. power +2
			0.2, // 4. slow enemies
			0.15, // 5. inc speed
			0.25, // 6. bomb
			0.14, // 7. firerate
			0.09, // 8. addon
			0.5, // 9. stamina
			0.12, // 10. max stamina
			0.05, // 11. shelter
			0 // 12. die
	};
	
	private long puDropTimes[] = {
			14200, // 101. spaz
			17700, // 102. side missile
			38300, // 103. army
			27400, // 104. supercharge
			49500, // 105. friends
			14100, // 106. lightning
			25300, // 107. torpedo
			30000 // 108. rage
	};
	
	private int puMinWaves[] = {
			0, // 101. spaz
			1, // 102. side missile
			10, // 103. army
			4, // 104. supercharge
			12, // 105. friends
			1, // 106. lightning
			7, // 107. torpedo
			5 // 108. rage
	};
	
	private long URDTimeMillis;
	private long elapsedTime;
	
	private long waveStartTimer;
	private long waveStartTimerDiff;
	public static int waveNumber;
	private boolean waveStart;
	private int waveDelay = 4700;
	
	private String lastPlayedLvl;
	
	private long lvlStartTime;
	private long lvlElapsedTime;
	
	private long pauseStartTime;
	private long pauseElapsedTime;
	private long totalPausedTime;
	
	private long skillCdWarnStartTime;
	private long skillCdWarnLength;
	private boolean isSkillCdWarn;
	
	private static long staminaLowStartTime;
	private long staminaLowLength;
	private static boolean isStaminaLow;
	
	private int pauseBackgroundAlpha;
	public boolean pauseHideKeyboard;
	
	private long shKbBtnBlinkElapsed;
	private long shKbBtnblinkStartTime;
	
	public static long slowDownTimer;
	private long slowDownTimerDiff;
	private int slowDownLength = 6000;
	
	
	private BufferedImage img;
	public BufferedImage pauseImg;
	
	public InfoPanel ip;
	public ShopPanel sp;
	
	private Menu menu;
	private Tutorial tutorial;
	public int btnLength = 170;
	public int btnHeight = 50;
	
	public static String username;
	
	//Tutorial Related
	public int currentTutorialStage;

	ArrayList<ArrayList<Integer>> allowedKeysPerTutStage;
	
	//Game Over Btns
	public Rectangle backFromGameOverBtn = new Rectangle(GamePanel.WIDTH /2 - btnLength/2, 525, btnLength, btnHeight);
	public Rectangle retryLvlBtn = new Rectangle(GamePanel.WIDTH /2 - btnLength/2, 455, btnLength, btnHeight);
	
	//Pause Btns
	public Rectangle resumeFromPausedBtn = new Rectangle(GamePanel.WIDTH /2 - btnLength/2, 555, btnLength, btnHeight);
	public Rectangle quitFromPauseBtn = new Rectangle(GamePanel.WIDTH /2 - btnLength/2, 625, btnLength, btnHeight);
	public Rectangle pauseHideKeyboardBtn;
	public enum GameState {
		TUTORIAL, PLAY, MENU, GAME_OVER, PAUSED
	}
	
	public enum MenuState {
		MAIN, HIGH_SCORES, CREDITS, PLAY_MODES, HELP, CONTROLS, DEFAULT_LEVELS, SURVIVAL_LEVELS, LOADING
	}
	
	public enum GameMode {
		TUTORIAL, DEFAULT, SURVIVAL
	}
	
	public static GameState gameState;
	public static MenuState menuState;
	public static GameMode gameMode;
	
	public static GameMode lastGameMode;
	
	public GamePanel(JFrame jframe, ShopPanel sp, InfoPanel ip, String username){
		super();		
		this.jframe = jframe;
		this.ip = ip;
		this.sp = sp;
		GamePanel.username = username;
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setFocusable(true);
		requestFocus();	
		
		//init shop panel and instructions panel
		sp.init();
		ip.init2();
	}
	
	
	public void addNotify(){
		super.addNotify();
		if(thread == null){
			thread = new Thread(this);
			thread.start();
		}
		addKeyListener(this);
		menu = new Menu(this);
		tutorial = new Tutorial(this);
		addMouseListener(new MouseInput(menu, tutorial, this));
	}

	//RUN METHOD
	@Override
	public void run() {  
		
		running = true;
		
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) image.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
        try {
            img = ImageIO.read(getClass().getResourceAsStream("/img/backImg6.png"));
            pauseImg = ImageIO.read(getClass().getResourceAsStream("/img/keyboard_img_populated.png"));
            pauseImg = ImageUtils.resize(pauseImg, 0.950);
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		player = new Player();
		bullets = new ArrayList<Bullet>();
		friends = new ArrayList<Friend>();
		enemies = new ArrayList<Enemy>();
		powerups = new ArrayList<PowerUp>();
		explosions = new ArrayList<Explosion>();
		texts = new ArrayList<Text>();
		turrets = new ArrayList<Turret>();
		blackholes = new ArrayList<BlackHole>();
		bombs = new ArrayList<Bomb>();
		shelters = new ArrayList<Shelter>();
		lightnings = new ArrayList<Lightning>();
		torpedos = new ArrayList<Torpedo>();
		spawnIndicators = new ArrayList<SpawnIndicator>();
		footPrints = new ArrayList<FootPrint>();
		particleEffects = new ArrayList<ParticleEffect>();
		enemyBullets = new ArrayList<EnemyBullet>();
		slowFields = new ArrayList<SlowField>();
		
		waveNames= new ArrayList<String>();
		waveData = new ArrayList<HashMap<Enemy, Integer>>();
		
		puDropElapsedMap = new HashMap<Integer, Long>();
		puLastDropTimeMap = new HashMap<Integer, Long>();
		puDropTimeMap = new HashMap<Integer, Long>();
		puDropRateMap = new HashMap<Integer, Double>();
		puCountMap = new HashMap<Integer, Integer>();
		puMinWaveMap = new HashMap<Integer, Integer>();
		
		highScoreMap = new HashMap<String, HashMap<String, Map<String,String>>>();

		
		//Populate dropRateMap
		for(int i=0; i<puDropRates.length; i++) {
			puDropRateMap.put(i+1, puDropRates[i]);
		}

		//Populate dropTimeMap
		for(int i=0; i<puDropTimes.length; i++) {
			puDropTimeMap.put(i+101, puDropTimes[i]);
		}
		
		//Populate minWaveMap
		for(int i=0; i<puMinWaves.length; i++) {
			puMinWaveMap.put(i+101, puMinWaves[i]);
		}
		
		//bgmusic = new AudioPlayer("/Music/bgfinal.mp3");
		//bgmusic.play();

		sfx = new HashMap<String, AudioPlayer>();
		
		sfx.put("hit", new AudioPlayer("/sfx/enemy_hit.wav"));
		sfx.put("player die", new AudioPlayer("/sfx/die.wav"));
		sfx.put("enemy die", new AudioPlayer("/sfx/enemydie.wav"));
		sfx.put("place black hole", new AudioPlayer("/sfx/place_black_hole.wav"));
		sfx.put("collect powerup", new AudioPlayer("/sfx/powerup_collect.wav"));
		sfx.put("freeze", new AudioPlayer("/sfx/freeze.wav"));
		sfx.put("rage", new AudioPlayer("/sfx/rage.wav"));
		sfx.put("collect", new AudioPlayer("/sfx/collect.wav"));
		sfx.put("push", new AudioPlayer("/sfx/push.wav"));
		sfx.put("dash", new AudioPlayer("/sfx/dash.wav"));
		sfx.put("invincible on", new AudioPlayer("/sfx/invincible.wav"));
		sfx.put("invincible off", new AudioPlayer("/sfx/invincible_off.wav"));
		sfx.put("turret", new AudioPlayer("/sfx/turret.wav"));
		sfx.put("bomb", new AudioPlayer("/sfx/bomb.wav"));
		sfx.put("explosion", new AudioPlayer("/sfx/explosion.wav"));
		sfx.put("tp turret", new AudioPlayer("/sfx/tp_turret.wav"));
		sfx.put("shelter", new AudioPlayer("/sfx/shelter.wav"));
		sfx.put("addon", new AudioPlayer("/sfx/addon.wav"));
		sfx.put("lightning", new AudioPlayer("/sfx/lightning.wav"));
		sfx.put("army", new AudioPlayer("/sfx/army.wav"));
		sfx.put("spaz", new AudioPlayer("/sfx/spaz.wav"));
		sfx.put("super charge turret", new AudioPlayer("/sfx/super_charge_turret.wav"));
		sfx.put("friends", new AudioPlayer("/sfx/friends.wav"));
		sfx.put("torpedo", new AudioPlayer("/sfx/torpedo.wav"));
		sfx.put("slow enemies", new AudioPlayer("/sfx/speed_up_enemies.wav"));
		sfx.put("skill cd", new AudioPlayer("/sfx/skill_cd.wav"));
		sfx.put("stamina low", new AudioPlayer("/sfx/stamina_low.wav"));
		
		sfx.put("shop buy", new AudioPlayer("/sfx/buy_shop2.wav"));
		sfx.put("pause", new AudioPlayer("/sfx/pause.wav"));
		sfx.put("menu select", new AudioPlayer("/sfx/menu_select.wav"));
		sfx.put("menu back", new AudioPlayer("/sfx/menu_back.wav"));
		
		
		long startTime;
		long waitTime;
		long totalTime = 0;
		int frameCount = 0;
		int maxFrameCount = 60;
		long targetTime = 1000/FPS;
		long gameStartTime = System.nanoTime();
		
		gameState = GameState.MENU;
		menuState = MenuState.MAIN;
		gameMode = GameMode.DEFAULT;
		
		//Game Loop
		while(running){			
			requestFocus();
			startTime = System.nanoTime();
			if(gameState == GameState.PAUSED) {				
				long pStartTick = System.nanoTime();
				pauseElapsedTime = (System.nanoTime() - pauseStartTime)/ 1000000;
				gameRender();
				pauseRender();
				gameDraw();
				pauseUpdate(pStartTick);
			}
			
			if(gameState == GameState.GAME_OVER) {
				gameOverRender();
				gameDraw();
			}
				
			if(gameState == GameState.MENU) {
				menu.render(g);
				gameDraw();
			}
				
			if(gameState == GameState.PLAY) {
				lvlElapsedTime = (System.nanoTime() - lvlStartTime)/1000000  - totalPausedTime;
				ip.updateStats2();
				
				gameUpdate();	
				gameRender();
				if(gameMode == GameMode.TUTORIAL) {
					tutorial.update(currentTutorialStage);
					tutorial.render(currentTutorialStage);					
				}			
				gameDraw();
			}			
			
			elapsedTime = (System.nanoTime() - gameStartTime)/1000000;
			
			URDTimeMillis = (System.nanoTime() - startTime)/1000000;
			
			waitTime = targetTime - URDTimeMillis;
			
			if(waitTime>0){//i added this brah!
				try {
					Thread.sleep(waitTime);
				} catch (InterruptedException e) {					
					e.printStackTrace();
				}
			}
			
			totalTime += System.nanoTime() - startTime;
			frameCount++;
			if(frameCount == maxFrameCount){
				//averageFPS = 1000.0 / ((totalTime/frameCount)/1000000);
				String fpsStr = String.format("%.0f", 1000.0 / ((totalTime/frameCount)/1000000));
				System.out.println("FPS: " + fpsStr);
				frameCount = 0;
				totalTime = 0;
			}			
		}
	}
	
	public void initNewLvl(String lvlName, GameMode gm, boolean replayLastLvl) {
		if(replayLastLvl) {
			lvlName = lastPlayedLvl;
			gm = lastGameMode;
		}
		
		lastGameMode = gm;
		lastPlayedLvl = lvlName;
		gameMode = gm;
		
		//init shop
		sp.resetPurchases();
		
		//clear menu anims
		menu.clearMenuAnims();
		
		waveNames.clear();
		waveData.clear();
		bullets.clear();
		friends.clear();
		enemies.clear();
		powerups.clear();
		explosions.clear();
		texts.clear();
		turrets.clear();
		blackholes.clear();
		bombs.clear();
		shelters.clear();
		lightnings.clear();
		torpedos.clear();
		footPrints.clear();
		spawnIndicators.clear();
		particleEffects.clear();
		enemyBullets.clear();
		slowFields.clear();
		
		player.init();
		waveStartTimer = 0;
		waveStartTimerDiff = 0;
		waveStart = true;
		waveNumber = 0;
		victorious = false;
		
		skillCdWarnLength = 370;
		skillCdWarnStartTime = 0;
		isSkillCdWarn = false;
		
		staminaLowLength = 370;
		staminaLowStartTime = 0;
		isStaminaLow = false;
		
		pauseHideKeyboard = true;
		pauseBackgroundAlpha = 120;
		
		if(gm == GameMode.TUTORIAL) {
			currentTutorialStage = 0;
			tutorial.init();
		}
		
		//Init last pu drop times
		for(int i=0; i<puDropTimes.length; i++) {
			puLastDropTimeMap.put(i+101, System.nanoTime());
			puDropElapsedMap.put(i+101, 0l);
		}
		
		
		
		
		//Init pu count map
	    for(int i=0; i<puDropTimes.length; i++) {
	    	puCountMap.put(i+101, 0);
	    }
		
	    
	    if(gm == GameMode.DEFAULT || gm == GameMode.TUTORIAL) {	    	
	    
			JSONObject waveDataJSONArr = null;
			//load JSON Data
			JSONParser parser = new JSONParser();
			try {
				InputStream iStream = getClass().getResourceAsStream("/LevelData/"+ lvlName +".json");
				//BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("Resources/LevelData/Level2.json"), "UTF-8"));
				BufferedReader br = new BufferedReader(new InputStreamReader(iStream, "UTF-8"));
				waveDataJSONArr = (JSONObject) parser.parse(br);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (org.json.simple.parser.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			JSONObject waveDataObj = (JSONObject) waveDataJSONArr;
			JSONArray levelData = (JSONArray) waveDataJSONArr.get("waves");
			levelTitle = (String) waveDataObj.get("levelName");
	
			for(Object waveObj : levelData) {
				//JsonObject Cast
				JSONObject waveJsonObj = (JSONObject) waveObj;
				
				//load titles			
				String waveName = (String) waveJsonObj.get("waveTitle");
				waveNames.add( waveName );
				
				//load enemy info
				HashMap<Enemy, Integer> enemyToAdd = new HashMap<Enemy, Integer>();
				JSONArray enemyDataJsonArr = (JSONArray) waveJsonObj.get("waveEnemies");
				for(Object enemyInfoObj : enemyDataJsonArr) {
					//JsonObject Cast
					JSONObject enemyInfoJsonObj = (JSONObject) enemyInfoObj;
					
					
					
					int tmpEnemyType = (int) (long) enemyInfoJsonObj.get("type");
					int tmpEnemyRank = (int) (long) enemyInfoJsonObj.get("rank");
					int tmpEnemyCount = (int) (long) enemyInfoJsonObj.get("count");
					//System.out.println(tmpEnemyType+ ","+ tmpEnemyRank+ ","+ tmpEnemyCount);
					Enemy tmpEnemy = new Enemy(tmpEnemyType, tmpEnemyRank, 1);
					enemyToAdd.put(tmpEnemy, tmpEnemyCount);				
				}
				waveData.add(enemyToAdd);
			}
			
			
			//load background
	        try {
	        	String lvlBackgroundImg = (String) waveDataObj.get("background");
	            img = ImageIO.read(getClass().getResourceAsStream("/img/"+lvlBackgroundImg));
	            img = ImageUtils.resize(img, WIDTH, HEIGHT);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
        
	    } else {
	    	//Survival Mode
	    	levelTitle = lvlName;
	    	player.addScore(3000);
			//load survival background
	        try {

	            img = ImageIO.read(getClass().getResourceAsStream("/img/"+"survival.png"));
	            img = ImageUtils.resize(img, WIDTH, HEIGHT);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }	    	
	    }
	    

		
        
        pauseStartTime = 0;
        totalPausedTime = 0;
        
		lvlStartTime = System.nanoTime();
	}

	
	private void gameOverRender() {
		g.setColor(new Color(0,0,0));
		g.fillRect(0,0,WIDTH,HEIGHT);
		g.setColor(Color.WHITE);
		g.setFont(new Font("Comic Sans MS",Font.BOLD,40));
		String s;
		if(victorious) {
			s = "V I C T O R Y";
		} else {
			s = "G A M E  O V E R";
		}		
		
		if(gameMode == GameMode.TUTORIAL) {
			if(currentTutorialStage >= tutorial.totalStages-1) {
				s = "Tutorial Complete!";
			} else {
				s = "Tutorial Ended";
			}
		}
		
		
		int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
		g.drawString(s, (WIDTH-length)/2, HEIGHT/2 - 120);
		//s = "Final Score: " + player.getScore();
		
		g.setFont(new Font("Comic Sans MS",Font.PLAIN,25));
		s = "Level : " + levelTitle;
		length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
		g.drawString(s, (WIDTH-length)/2, HEIGHT/2-50);		
		
		s = "Completed Waves : " + (waveNumber-1);
		length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
		g.drawString(s, (WIDTH-length)/2, HEIGHT/2+0);
		
		//Only show time when NOT survival mode
		if(gameMode != GameMode.SURVIVAL) {
			s = "Time : " + StringUtils.getTime(lvlElapsedTime);
			length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
			g.drawString(s, (WIDTH-length)/2, HEIGHT/2+50);
		}

		
		g.drawString("Retry", retryLvlBtn.x+18, retryLvlBtn.y+33);
		g.draw(retryLvlBtn);
		
		g.drawString("Main Menu", backFromGameOverBtn.x+18, backFromGameOverBtn.y+33);
		g.draw(backFromGameOverBtn);
		
	}
	
	private void pauseUpdate(long pauseStartTick) {
		
		player.pauseUpdate();
		
		for(int i=0; i< enemies.size(); i++){
			enemies.get(i).pauseUpdate();
		}
		
		//spawn Indicator pause Update
		if(gameMode != GameMode.TUTORIAL || currentTutorialStage > 10) {
		    for (Map.Entry<Integer, Long> puLastDropEntry : puLastDropTimeMap.entrySet()) {
		    	int powerUpType = puLastDropEntry.getKey();
		    	long powerUpLastDropTime = puLastDropEntry.getValue();
		    	long puElapsed = puDropElapsedMap.get(powerUpType);
		    	
		    	long newPowerUpLastDropTime = System.nanoTime() - puElapsed * 1000000;
		    	puLastDropTimeMap.put(puLastDropEntry.getKey(), newPowerUpLastDropTime);
		    }
		}
	}

	private void pauseRender() {
		g.setColor(new Color(255,255,255,pauseBackgroundAlpha));
		g.fillRect(0,0,WIDTH,HEIGHT);
		int keyboardImgWidth = pauseImg.getWidth();
		int keyboardImgX = (WIDTH - keyboardImgWidth)/2;
		int keyboardImgY = 50;
		
		//draw img
		if(pauseHideKeyboard) {
			g.setColor(new Color(255,255,255,255));
			g.fillRect(keyboardImgX,keyboardImgY,pauseImg.getWidth(), pauseImg.getHeight());
			g.drawImage(pauseImg,keyboardImgX,keyboardImgY , null);
		}
		
		g.setColor(new Color(255,69,0));
		g.setFont(new Font("Comic Sans MS", Font.BOLD,42));
		String s = "PAUSED";
		int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
		g.drawString(s, (WIDTH-length)/2, HEIGHT/2+150);
		
		
		
		g.setStroke(new BasicStroke(3));
		
		//blinking btn
		int btnAlpha = 0;
		shKbBtnBlinkElapsed = (System.nanoTime() - shKbBtnblinkStartTime)/1000000;

		if(shKbBtnBlinkElapsed > 500) {
			shKbBtnblinkStartTime = System.nanoTime();
		} else if(shKbBtnBlinkElapsed > 500/2) {
			btnAlpha = (int) (255 - 255 * shKbBtnBlinkElapsed / 500);
		} else {
			btnAlpha = (int) (255 * shKbBtnBlinkElapsed / 500);
		}
		
		if(btnAlpha< 0)
		{
			btnAlpha = 0;			
		}
		
		if(btnAlpha>255) {
			btnAlpha = 255;
		}
		
		g.setFont(new Font("Comic Sans MS", Font.BOLD,15));
		pauseHideKeyboardBtn = new Rectangle(keyboardImgX, HEIGHT/2+150-35, 100, 35);
		g.setColor(new Color(0,0,0,255));
		g.drawString("Show/Hide", pauseHideKeyboardBtn.x+10, pauseHideKeyboardBtn.y+16);
		
		g.drawString("Keys", pauseHideKeyboardBtn.x+10, pauseHideKeyboardBtn.y+30);
		g.draw(pauseHideKeyboardBtn);
		g.setColor(new Color(0,0,0,btnAlpha));
		g.fill(pauseHideKeyboardBtn);
		
		g.setFont(new Font("Comic Sans MS", Font.BOLD,30));
		resumeFromPausedBtn = new Rectangle(GamePanel.WIDTH /2 - btnLength/2, 520, btnLength, btnHeight);
		g.setColor(new Color(0,128,0));
		g.fill(resumeFromPausedBtn);
		g.setColor(new Color(255,255,255));
		g.drawString("Resume", resumeFromPausedBtn.x+22, resumeFromPausedBtn.y+35);
		
		quitFromPauseBtn = new Rectangle(GamePanel.WIDTH /2 - btnLength/2, 580, btnLength, btnHeight);
		g.setColor(new Color(255,69,0));
		g.fill(quitFromPauseBtn);
		g.setColor(new Color(255,255,255));
		g.drawString("Give up", quitFromPauseBtn.x+22, quitFromPauseBtn.y+35);

		g.setStroke(new BasicStroke(1));
		

		
	}
	
	//DRAW TO GAME SCREEN
	private void gameDraw() {
		Graphics g2 = this.getGraphics();
		g2.drawImage(image, 0, 0, null);		
	}

	//DRAW TO OFFSCREEN
	private void gameRender() {	
		
		//draw background
		g.setColor(new Color(122,155,155));
		g.fillRect(0, 0, WIDTH, HEIGHT);
		g.setColor(Color.BLACK);
		
		//draw random dino img
		g.drawImage(img, 0, 0, null);
		
		//draw slowdown screen
		if(slowDownTimer !=0){
			g.setColor(new Color(255,255,255,25));
			g.fillRect(0, 0, WIDTH, HEIGHT);
		}
		
		//draw bombing screen
		if(player.isBombing()){
			g.setColor(new Color(255,0,0,144));
			g.fillRect(0,0,WIDTH,HEIGHT);
		}
		
		//text
		//g.drawString("FPS : "+averageFPS, 10, 10);
		
		//draw player
		player.draw(g);
		
		//draw footprints
		for(int i=0; i<footPrints.size(); i++) {
			footPrints.get(i).draw(g);
		}
		
		//draw particle effects
		for(int i=0; i<particleEffects.size(); i++) {
			particleEffects.get(i).draw(g);
		}

		//draw bullets
		for(int i=0; i<bullets.size(); i++){
			bullets.get(i).draw(g);
		}
		
		//draw enemy bullets
		for(int i=0; i<enemyBullets.size(); i++){
			enemyBullets.get(i).draw(g);
		}
		
		//draw slow fields
		for(int i=0; i<slowFields.size(); i++){
			slowFields.get(i).draw(g);
		}
		
		//draw friend
		for(int i=0; i< friends.size(); i++) {
			friends.get(i).draw(g);
		}
		
		//draw enemy
		for(int i=0; i< enemies.size(); i++){
			enemies.get(i).draw(g);
			//g.draw(new Line2D.Double(enemies.get(i).getx(), enemies.get(i).gety(), player.getx(), player.gety()));
		}					
			
		//draw turrets
		for(int i=0; i< turrets.size(); i++){
			turrets.get(i).draw(g);
		}
		
		//draw bomb
		for(int i=0; i<bombs.size(); i++){
			bombs.get(i).draw(g);
		}
		
		//draw lightning
		for(int i=0; i<lightnings.size(); i++){
			lightnings.get(i).draw(g);
		}	
		
		//draw torpedos
		for(int i=0; i<torpedos.size(); i++){
			torpedos.get(i).draw(g);
		}
			
		//draw line wall
		for(int i=0; i<shelters.size(); i++){
			shelters.get(i).draw(g);
		}		
			
		//draw spawn indicators
		for(int i=0; i< spawnIndicators.size(); i++) {
			spawnIndicators.get(i).draw(g);
		}
			
		//draw powerups
		for(int i = 0; i < powerups.size(); i++){
			powerups.get(i).draw(g);
		}
			
		//draw explosion
		for(int i=0; i<explosions.size(); i++){
			explosions.get(i).draw(g);
		}
			
		//draw text
		for(int i=0; i<texts.size(); i++){
			texts.get(i).draw(g);
		}
			
		//draw black hole
		for(int i=0; i<blackholes.size(); i++){
			blackholes.get(i).draw(g);
		}
			
		//draw wave number
		if(waveStartTimer != 0 && gameState == GameState.PLAY){
			g.setFont(new Font("TimesRoman", Font.PLAIN,40));
			String s = "";
			if(gameMode == GameMode.DEFAULT) {
				s = " W A V E  " + waveNumber + "  :   " + waveNames.get(waveNumber-1);
			} else {
				//Survival Mode
				s = " W A V E  " + waveNumber;
			}
			
			int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
			int alpha = (int) (255 * Math.sin(3.14 * waveStartTimerDiff / waveDelay));
			if(alpha>255) alpha = 255;
			if(alpha<0 ) alpha = 0;
			g.setColor(new Color(255,255,255,alpha));
			g.drawString(s, WIDTH/2 - length/2, HEIGHT/2);
		}
		
		//draw player lives
		for(int i=0; i<player.getLives(); i++){
			g.setColor(Color.WHITE);
			g.fillOval(20+(20*i), 20, player.getr()*2, player.getr()*2);
			g.setStroke(new BasicStroke(3));
			
			g.setColor(Color.WHITE.darker());
			g.drawOval(20+(20*i), 20, player.getr()*2, player.getr()*2);
			g.setStroke(new BasicStroke(1));
		}
		
		//draw player power
		g.setColor(Color.yellow);
		g.fillRect(20, 40, player.getPower()*8, 8);
		g.setColor(Color.yellow.darker());
		g.setStroke(new BasicStroke(2));
		
		for(int i =0; i< player.getRequiredPower(); i++){
			g.drawRect(20+8*i, 40, 8, 8);
		}
		
		g.setStroke(new BasicStroke(1));		
		
		
		//draw player score
		if(gameMode != GameMode.SURVIVAL) {
			g.setColor(Color.WHITE);
			g.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
			g.drawString("Time : " + StringUtils.getTime(lvlElapsedTime), WIDTH-135, 30);
		} else {
			//Survival Mode
			g.setColor(Color.RED);
			g.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
			g.drawString("SURVIVED : " + (waveNumber-1), WIDTH-135, 30);
		}
		
		
		//draw player speed
		g.setColor(Color.WHITE);
		g.setFont(new Font("Comic Sans MS",Font.BOLD,14));
		g.drawString("Move Speed : " + player.getSpeed()+"/7", WIDTH-135, 50);
		
		//draw player speed
		g.setColor(Color.WHITE);
		g.setFont(new Font("Comic Sans MS",Font.BOLD,12));
		g.drawString("Att Speed : " + (int)player.getAttSpeed()+"/100", WIDTH-135, 70);
		
		//draw player bombs
		g.setColor(new Color(255,20,147));
		g.setFont(new Font("Comic Sans MS",Font.BOLD,14));
		g.drawString("BOMBS : " + player.getBombs(), WIDTH-135, 90);
		
		//draw player shelter count
		g.setColor(Color.WHITE);
		g.setFont(new Font("Comic Sans MS",Font.BOLD,14));
		g.drawString("Shelters : " + player.getShelterCount(), WIDTH-135, 110);
		
		//draw player turrets
		g.setColor(Color.RED);
		g.setFont(new Font("Comic Sans MS",Font.BOLD,14));
		g.drawString("Turrets : " + (5-turrets.size()) + "/5", WIDTH-135, 130);
		
		//draw player money
		g.setColor(new Color(50,205,50));
		g.setFont(new Font("Comic Sans MS",Font.BOLD,14));
		g.drawString("Money : " + player.getScore(), WIDTH-135, 150);
		
		//Draw SKills on CD
		HashMap<String, Long> cdSkills = player.getCdSkills();
		if(cdSkills.size() > 0) {
			//There are skills on cd
			g.setColor(new Color(255,255,255));
			g.setFont(new Font("Comic Sans MS",Font.BOLD,14));
			g.drawString("Skill CD's", WIDTH-135, 170);
			
			//if skillcd warn, rect color blinks red
			long skillCdWarnElapsed = (System.nanoTime() - skillCdWarnStartTime)/1000000;
			if(skillCdWarnElapsed > skillCdWarnLength && isSkillCdWarn) {
				isSkillCdWarn = false;
			}			
			//skills cd rect gets bigger when warn
			int skillsCdRectSizeUp = 0;
			if(isSkillCdWarn) {
				int skillCdWarnAlpha = (int) (255 - 255 * skillCdWarnElapsed / skillCdWarnLength);
				if(skillCdWarnAlpha < 100) {skillCdWarnAlpha = 100;}
				skillsCdRectSizeUp = (int) (157 - 157 * skillCdWarnElapsed / skillCdWarnLength);
				if(skillsCdRectSizeUp < 0) {skillsCdRectSizeUp = 0;}
				
				//black out screen so cd rect can be seen better
				g.setColor(new Color(0,0,0,skillCdWarnAlpha));
				g.fillRect(0,0,WIDTH,HEIGHT);
				
				g.setColor(new Color(255,0,0,skillCdWarnAlpha));
			} else {
				g.setColor(new Color(255,0,0,100));
			}
			
			//cd skills list surrounding rect
			g.fillRect(WIDTH-140 -skillsCdRectSizeUp , 175 - skillsCdRectSizeUp, 125 + skillsCdRectSizeUp*2, 25 * cdSkills.size() + 10 + skillsCdRectSizeUp*2);
			
			//content within skill cd rect
			g.setColor(new Color(255,255,255));
			int cdSkillCount = 0;
			for (Map.Entry<String, Long> entry : cdSkills.entrySet()) {
				cdSkillCount++;
				
				g.setColor(player.skillColorMap.get(entry.getKey()));
				g.drawString(entry.getKey(), WIDTH-135, 170 + 25 * cdSkillCount);
				
				//cd bar
				g.drawRect(WIDTH-135, 170 + 25 * cdSkillCount + 4, 100, 6);
				
				long elapsed = entry.getValue();
				long cd = player.skillCdMap.get(entry.getKey());				
				g.fillRect(WIDTH-135, 170 + 25 * cdSkillCount + 4, (int) (100-100 * elapsed/cd), 6);
		    }
			

		}
		
		
		
		//draw slowdown meter
		if(slowDownTimer !=0){
			g.setColor(Color.WHITE);
			g.drawRect(20, 60, 100, 8);
			g.fillRect(20,60, (int)(100-100.0*slowDownTimerDiff/slowDownLength), 8);
		}
		
		//draw stamina meter
		long staminaLowElapsed = (System.nanoTime() - staminaLowStartTime)/1000000; 
		
		if(staminaLowElapsed > staminaLowLength && isStaminaLow) {
			isStaminaLow = false;
		}	
		
		int staminaBarSizeUp = 0;
		if(isStaminaLow) {
			staminaBarSizeUp = (int) (100 - 100 * staminaLowElapsed / staminaLowLength);
		}
		
		if(staminaBarSizeUp < 0) {
			staminaBarSizeUp = 0;
		}
		
		
		g.setColor(new Color(0,0,0,222));
		g.drawRect(WIDTH/4- staminaBarSizeUp, HEIGHT-60- staminaBarSizeUp/6, WIDTH/2+ staminaBarSizeUp*2, HEIGHT/100+ staminaBarSizeUp/3);
		double ratio = player.getCurrentStamina()/player.getMaxStamina() * 100;
		if(ratio < 30){
			g.setColor(new Color(255,0,0,222));
		} else if(ratio < 50){
			g.setColor(new Color(255,255,0,222));
		} else if(ratio < 70){
			g.setColor(new Color(255,128,0,222));
		} else {
			g.setColor(new Color(0,255,0,222));
		}

		
		g.fillRect(WIDTH/4 - staminaBarSizeUp, HEIGHT-60- staminaBarSizeUp/6, (int)((WIDTH/2)*(player.getCurrentStamina()/player.getMaxStamina())) + staminaBarSizeUp*2, HEIGHT/100 + staminaBarSizeUp/3);
		//g.setColor(new Color(34,139,34,255));
		g.setFont(new Font("Comic Sans MS", Font.BOLD, 14 + staminaBarSizeUp / 2));
		g.drawString("Stamina : " + (int)player.getCurrentStamina() + " / " + (int)(player.getMaxStamina() ), WIDTH/4- staminaBarSizeUp, HEIGHT-63- staminaBarSizeUp/6);
	}

	//GAME UPDATE
	private void gameUpdate() {
		if(gameMode == GameMode.TUTORIAL) {
			if(enemies.size() == 0) {
				//waveNumber++;
				waveNumber = 1;
				waveStart = true;
				waveStartTimer = 0;
				waveStartTimerDiff = 0;
			}
		} else {
			//new wave
			if(waveStartTimer == 0 && enemies.size() ==0){
				waveNumber++;
				waveStart = false;
				waveStartTimer = System.nanoTime();
				
				//Default Mode Win Condition
				if(gameMode == GameMode.DEFAULT && waveNumber > waveNames.size()) {		
					g.setColor(new Color(0,0,0,255));
					g.fillRect(0,0,WIDTH,HEIGHT);
					g.setFont(new Font("Comic Sans MS", Font.BOLD, 30));
					g.setColor(Color.white);
					
					int length = (int) g.getFontMetrics().getStringBounds("Saving Score...", g).getWidth();
					g.drawString("Saving Score...", (GamePanel.WIDTH-length)/2, (GamePanel.HEIGHT)/2);
					gameDraw();
					if(!victorious) {
						HighScoreUtils.addHighScore("DefaultLevels", levelTitle, StringUtils.getTime(lvlElapsedTime), username);
					}
					victorious = true;
					gameState = GameState.GAME_OVER;
				}
				
			} else {
				waveStartTimerDiff = (System.nanoTime() - waveStartTimer)/1000000;
				if(waveStartTimerDiff>waveDelay){
					waveStart = true;
					waveStartTimer = 0;
					waveStartTimerDiff = 0;
				}
			}
		}

		//create Enemies
		if(waveStart && enemies.size()==0){
			createNewEnemies();
		}
		
		//player update
		player.update();
		
		//footprints update
		for(int i=0; i<footPrints.size(); i++){
			boolean remove = footPrints.get(i).update();
			if(remove){
				footPrints.remove(i);
				i--;
			}
		}
		
		//particle effects update
		for(int i=0; i<particleEffects.size(); i++){
			boolean remove = particleEffects.get(i).update();
			if(remove){
				particleEffects.remove(i);
				i--;
			}
		}
		
		//bullet update
		for(int i=0; i<bullets.size(); i++){
			boolean remove = bullets.get(i).update();
			if(remove){
				bullets.remove(i);
				i--;
			}
		}
		
		//enemy bullet update
		for(int i=0; i<enemyBullets.size(); i++){
			boolean remove = enemyBullets.get(i).update();
			if(remove){
				enemyBullets.remove(i);
				i--;
			}
		}
		
		//slow fields update
		for(int i=0; i<slowFields.size(); i++){
			boolean remove = slowFields.get(i).update();
			if(remove){
				slowFields.remove(i);
				i--;
			}
		}
		
		//friend update
		for(int i=0; i<friends.size(); i++) {
			Friend f = friends.get(i);
			f.update();
		}
		
		//enemy update
		for(int i=0; i< enemies.size(); i++){
			Enemy e = enemies.get(i);
			e.update(player, texts);			
		}
		
		//lightning update
		for(int i=0; i< lightnings.size(); i++){
			Lightning l = lightnings.get(i);
			l.update();
		}
		
		//lightning remove update
		for(int i=0; i< lightnings.size(); i++){
			Lightning l = lightnings.get(i);
			if(l.isOver()) {
				lightnings.remove(i);
				i--;
			}
		}
		
		//torpedo update
		for(int i=0; i< torpedos.size(); i++){
			Torpedo t = torpedos.get(i);
			boolean torpedoDead = t.update();
			if(torpedoDead) {
				t.explode();
				torpedos.remove(i);
				i--;
			}
		}
		
		//time bomb update
		for(int i=0; i<bombs.size(); i++){
			boolean remove = bombs.get(i).update();
			for(int j=0; j< enemies.size(); j++){
				if(bombs.get(i).getIsBombing() && !bombs.get(i).isHostile() && enemies.get(j).isInRange(bombs.get(i).getx(), bombs.get(i).gety(), bombs.get(i).getmaxr())){
					enemies.get(j).setGettingBombed(true);							
				}
			}
			if(remove){
				bombs.remove(i);
				i--;
			}
		}
		
		boolean inRangeOfAtLeastOneBomb = false;		
		for(int j=0; j<enemies.size(); j++){
			for(int i=0; i<bombs.size(); i++){
				if(bombs.get(i).getIsBombing() && !bombs.get(i).isHostile()){
					if(enemies.get(j).isInRange(bombs.get(i).getx(), bombs.get(i).gety(), bombs.get(i).getmaxr())){
						inRangeOfAtLeastOneBomb = true;
					}
					
				}			
			}
			
			if(inRangeOfAtLeastOneBomb == false){
				enemies.get(j).setGettingBombed(false);	
			}
		}
		
		//hostile time bomb update
		for(int i=0; i<bombs.size(); i++){
			//if player in range, bomb is bombing, and bomb is hostile
			if(player.isInRange(bombs.get(i).getx(), bombs.get(i).gety(), bombs.get(i).getmaxr()) && bombs.get(i).getIsBombing() && bombs.get(i).isHostile())
				if(!player.isRecovering())
					player.loseLife();
		}
		
		boolean playerInAtLeastOneBlackHole = false;
		
		//black hole update
		for(int i=0; i<blackholes.size(); i++){
			boolean remove = blackholes.get(i).update();
			if(blackholes.get(i).getPulseStatus()==true){
				
				if(!blackholes.get(i).isHostile()){
					for(int j=0; j<enemies.size(); j++){
						
						if(enemies.get(j).isInRange(blackholes.get(i).getx(), blackholes.get(i).gety(), blackholes.get(i).getPullRadius())){
							enemies.get(j).saveVector();
							enemies.get(j).pullTowards(blackholes.get(i).getx(), blackholes.get(i).gety());
							
						} else {
							enemies.get(j).setIsPulled(false);							
						}
					}
				} else {
					if(player.isInRange(blackholes.get(i).getx(), blackholes.get(i).gety(), blackholes.get(i).getPullRadius())){
						playerInAtLeastOneBlackHole = true;
						player.moveToward(blackholes.get(i).getx(), blackholes.get(i).gety());
						player.setImmobalized(true);
					}
				}
			}
			if(remove){
				for(int j=0; j<enemies.size(); j++){
					if(enemies.get(j).getIsPulled()){
						enemies.get(j).setIsPulled(false);
						enemies.get(j).restoreVector();
						//enemies.get(j).randomizeDirection();
					}
				}
				
				blackholes.remove(i);
				i--;
			}
			
		}
		
		//slow Field update
		boolean playerInAtLeastOneSlowField = false;
		
		for(int i=0; i<slowFields.size(); i++) {
			SlowField sf = slowFields.get(i);
			if(sf.isStart()) {
				if(player.isInRange(sf.getX(), sf.getY(), sf.getMaxR())){
					playerInAtLeastOneSlowField = true;
				}
			}			
		}
		
		if(playerInAtLeastOneSlowField) {
			player.slowOn();
		} else {
			player.slowOff();
		}
		
		
		
		if(playerInAtLeastOneBlackHole == false){
			player.setImmobalized(false);
		}
		
		// player is pushing update
		if(player.isPushing()){
			for(int i=0; i<enemies.size(); i++){
				if(enemies.get(i).isInRange(player.getx(), player.gety(), player.getPushRadius()))
					enemies.get(i).pushAway(player.getx(), player.gety());
			}
		}
		
		//Player auto collect powerup update
		for(int i=0; i<powerups.size(); i++) {
			PowerUp p = powerups.get(i);
			if(powerups.get(i).isInRange(player.getx(), player.gety(), player.getCollectRadius()) && player.isCollectingPu()) {
				//p.setBeingCollected(true);
				//p.collect();
				//p.showCollectTextAtPowerUp();
				//explosions.add(new Explosion(p.getx(), p.gety(),p.getr(), p.getr()+30));
				//powerups.remove(i);		
				p.goTowards(player.getx(), player.gety(), 8.27);
			} else {
				p.recoverMovement();
			}
		}

		//Shelter update
		for(int i=0; i<shelters.size(); i++){
			boolean remove = shelters.get(i).update();
			Shelter l = shelters.get(i);
			//enemy collision
			for(int j=0; j<enemies.size(); j++){
				if(enemies.get(j).pointCollide(shelters.get(i).getx(), shelters.get(i).gety(), shelters.get(i).getr())){
					shelters.get(i).hit();
				}
			}
			
			//enemy bullet collision
			for(int j=0; j<enemyBullets.size(); j++) {
				if(MathUtils.getDist(l.getx(), l.gety(), enemyBullets.get(j).getx(), enemyBullets.get(j).gety()) < l.getr() + enemyBullets.get(j).getr()) {
					enemyBullets.remove(j);
				}
			}
						
			if(remove){
				shelters.remove(i);
				i--;
			}
		}
		
		//enemy position update
		double[] distances = new double[enemies.size()];
		for(int i=0; i< enemies.size(); i++){
			double ex = enemies.get(i).getx();
			double ey = enemies.get(i).gety();
			distances[i] = Math.sqrt((ex-player.getx())*(ex-player.getx()) + (ey-player.gety())*(ey-player.gety()))-enemies.get(i).getr();
		}
		if(waveStart && enemies.size()!=0){
			int index = (int)smallestDistance(distances)[1];
			Enemy eR = enemies.get(index);
			enemies.get(index).setTargeted(true);
			player.updateEnemyAngle(eR.getx(), eR.gety());
			
			//Seeker missile update
			for(int i=0; i< bullets.size(); i++){
				Bullet b = bullets.get(i);
				
				for(int j=0; j< enemies.size(); j++){
					double ex = enemies.get(j).getx();
					double ey = enemies.get(j).gety();
					distances[j] = Math.sqrt((ex-b.getx())*(ex-b.getx()) + (ey-b.gety())*(ey-b.gety()))-enemies.get(j).getr();					
				}
				int index2 = (int)smallestDistance(distances)[1];
				Enemy eR2 = enemies.get(index2);
				
				if(b.isSideMissile()){
					b.updateEnemyPosition(eR2);					
				}
				if(b.isTurret()){
					b.updateEnemyPosition(eR2);
				}
			}
			
			//Turret angle update
			for(int i=0; i<turrets.size(); i++){
				Turret t = turrets.get(i);
				for(int j=0; j< enemies.size(); j++){
					double ex = enemies.get(j).getx();
					double ey = enemies.get(j).gety();
					distances[j] = Math.sqrt((ex-t.getx())*(ex-t.getx()) + (ey-t.gety())*(ey-t.gety()))-enemies.get(j).getr();					
				}
				int index3 = (int)smallestDistance(distances)[1];
				Enemy eR3 = enemies.get(index3);
					t.updateEnemyPosition(eR3);			
			}		
			//mark target enemy
			for(int i=0; i< enemies.size(); i++){				
				if(i!=index){
					enemies.get(i).setTargeted(false);
				}
			}
		}
		
		//turret update
		for(int i=0; i<turrets.size(); i++){
			Turret t = turrets.get(i);
			boolean remove = t.update();			
			if(remove){
				t.setDead(true);
				explosions.add(new Explosion(t.getx(), t.gety(),(int)t.getr(), (int)t.getr()+30));
				turrets.remove(i);
				i--;
			}
		}
		
		//powerup update
		for(int i=0; i<powerups.size(); i++){
			boolean remove = powerups.get(i).update();
			if(remove){
				powerups.remove(i);
				i--;
			}
		}
		
		
		//spawn indicators create
		if(gameMode != GameMode.TUTORIAL || currentTutorialStage > 10) {
		    for (Map.Entry<Integer, Long> puDropRateEntry : puLastDropTimeMap.entrySet()) {
		    	int powerUpType = puDropRateEntry.getKey();
		    	long powerUpLastDropTime = puDropRateEntry.getValue();
		    	long puElapsed = (System.nanoTime() - powerUpLastDropTime) / 1000000;
		    	puDropElapsedMap.put(powerUpType, puElapsed);
		    	double dropTime = (puDropTimeMap.get(powerUpType) * player.getSpawnTimeMultiplier());
		    	//Check min wave requirements for each powerup (ignore if tutorial mode)
		    	if(waveNumber >= puMinWaveMap.get(powerUpType) || gameMode == GameMode.TUTORIAL) {
					if(puElapsed > RandomUtils.getPlusMinusPercentage(dropTime, 0.2)) {
						puLastDropTimeMap.put(powerUpType, System.nanoTime());
						int[] newPuPos = RandomUtils.getRandomDest(WIDTH, HEIGHT);
						PowerUp newPu = new PowerUp(powerUpType, newPuPos[0], newPuPos[1]);
						spawnIndicators.add(new SpawnIndicator("PowerUp", newPu));
					}
		    	}
		    }
		    
		    //spawn indicators update
			for(int i=0; i<spawnIndicators.size(); i++){
				boolean remove = spawnIndicators.get(i).update();
				if(remove){
					spawnIndicators.get(i).spawn();
					spawnIndicators.remove(i);
					i--;
				}
			}
		}
	    
	    
		//explosion update
		for(int i=0; i<explosions.size(); i++){
			boolean remove = explosions.get(i).update();
			if(remove){
				explosions.remove(i);
				i--;
			}
		}
		
		//text update
		for(int i=0; i<texts.size(); i++){
			boolean remove = texts.get(i).update();
			if(remove){
				texts.remove(i);
				i--;
			}
		}
		
		//Bullet-enemy collision
		for(int i=0; i<bullets.size(); i++){
			
			Bullet b = bullets.get(i);
			double bx = b.getx();
			double by = b.gety();
			double br = b.getr();
			
			for(int j=0; j<enemies.size(); j++){
				Enemy e = enemies.get(j);
				double ex = e.getx();
				double ey = e.gety();
				double er = e.getr();
			
				double dx = bx - ex;
				double dy = by - ey;
				double dist = Math.sqrt(dx * dx + dy * dy);
				
				if(dist < br + er){
					sfx.get("hit").play();
					e.hit(b.getDmg());
					bullets.remove(i);
					i--;
					break;
				}
			}
		}
		
		//enemyBullet-player collision
		for(int i=0; i<enemyBullets.size(); i++){
			
			EnemyBullet b = enemyBullets.get(i);
			double bx = b.getx();
			double by = b.gety();
			double br = b.getr();
			
			
			double ex = player.getx();
			double ey = player.gety();
			double er = player.getr();
		
			double dx = bx - ex;
			double dy = by - ey;
			double dist = Math.sqrt(dx * dx + dy * dy);
			
			if(dist < br + er){
				//sfx.get("hit").play();
				if(b.isLethal()) {
					player.loseLife();
				} else {
					player.stun(3000);
				}
				
				enemyBullets.remove(i);
				i--;
				break;
			}
		}
		
		//check dead enemies
		for(int i=0; i<enemies.size(); i++){
			if(enemies.get(i).isDead()){
				sfx.get("enemy die").play();
				Enemy e= enemies.get(i);

				//power up drop
			    for (Map.Entry<Integer, Double> puDropRateEntry : puDropRateMap.entrySet()) {
			    	int powerUpType = puDropRateEntry.getKey();
			    	double powerUpDropRate = puDropRateEntry.getValue();
					if(RandomUtils.runChance(powerUpDropRate * player.getDropRateMultiplier() * e.getDropMultiplier())) {
						double[] offset = RandomUtils.getRandomOffset(5, 5);
						powerups.add(new PowerUp(powerUpType, e.getx()+offset[0],e.gety()+offset[1]));
					}
			    }

				player.addScore(e.getMoney());
				texts.add(new Text(e.getx(), e.gety(),1000,"+" +e.getMoney(), true, Color.GREEN, Font.BOLD));
				
				enemies.remove(i);
				i--;
				
				e.split();
				explosions.add(new Explosion(e.getx(), e.gety(),e.getr(), e.getr()+30));				
			}
		}
		
		//check dead friend
		for(int i=0; i<friends.size(); i++){
			Friend f = friends.get(i);
			if(f.isDead()){
				friends.remove(i);
				explosions.add(new Explosion(f.getX(), f.getY(),f.getR(), f.getR()+30));
			}			
		}
		
		//check dead player
		if(player.isDead()){
			sfx.get("player die").play();
			
			if(gameMode == GameMode.SURVIVAL && waveNumber > 1) {
				g.setColor(new Color(0,0,0,255));
				g.fillRect(0,0,WIDTH,HEIGHT);
				g.setFont(new Font("Comic Sans MS", Font.BOLD, 30));
				g.setColor(Color.white);
				
				int length = (int) g.getFontMetrics().getStringBounds("Saving Score...", g).getWidth();
				g.drawString("Saving Score...", (GamePanel.WIDTH-length)/2, (GamePanel.HEIGHT)/2);
				gameDraw();

				HighScoreUtils.addHighScore("SurvivalLevels", levelTitle, String.valueOf(waveNumber-1), username);

			}

			
			
			gameState = GameState.GAME_OVER;
		}
		
		//check player enemy collision
		if(!player.isRecovering()){
			int px = player.getx();
			int py = player.gety();
			int pr = player.getr();
			
			for(int i=0; i<enemies.size(); i++){
				Enemy e = enemies.get(i);
				double ex = e.getx();
				double ey = e.gety();
				double er = e.getr();
				
				double dx = px - ex;
				double dy = py - ey;
				double dist = Math.sqrt(dx*dx+dy*dy);
				
				if(dist<(pr+er)){
					if(!player.isRecovering() && !player.isInvincible()){
						player.loseLife();
						texts.add(new Text(player.getx(), player.gety(),2000,"-1 Life!"));
					}
				}				
			}
		}
		
		//player powerup collision
		int px = player.getx();
		int py = player.gety();
		int pr = player.getr();
		for(int i=0; i<powerups.size();i++){
			PowerUp p = powerups.get(i);
			double x = p.getx();
			double y = p.gety();
			double r = p.getr();
			double dx = px - x;
			double dy = py - y;
			double dist = Math.sqrt(dx*dx+dy*dy);
			
			//collect powerup
			if(dist<pr+r){
				int type = p.getType();
				sfx.get("collect powerup").play();
				p.showCollectText();
				p.collect();	
				powerups.remove(i);
				i--;
			}
			
		}
		
		//slowdown update
		if(slowDownTimer != 0){
			slowDownTimerDiff = (System.nanoTime() - slowDownTimer)/1000000;
			if(slowDownTimerDiff>slowDownLength){
				slowDownTimer = 0;
				for(int j = 0; j<enemies.size(); j++){
					enemies.get(j).setSlow(false);
				}
			}
		}
		
	}
	
	public void removeTurret(int index){
		for(int i=0; i<turrets.size(); i++){
			if(turrets.get(i).getIndex()==index){
				turrets.remove(i);
			}
		}
	}
	
	private double[] smallestDistance(double list[]){
		double minVal=99999999;
		int minValPos=0;
		for(int i=0; i<list.length; i++){
			if(list[i]<minVal){
				minVal = list[i];
				minValPos = i;
			}
		}
		return new double[] {minVal,(double)minValPos};
	}
	
	//ENEMY WAVES
	private void createNewEnemies(){
		enemies.clear();
		
		//Default Mode
		if(gameMode == GameMode.DEFAULT || gameMode == GameMode.TUTORIAL) {
			HashMap<Enemy, Integer> currWaveData = waveData.get(waveNumber-1);
		    for (Map.Entry<Enemy, Integer> entry : currWaveData.entrySet()) {
		        for(int i=0; i<entry.getValue(); i++) {
		        	Enemy tmpEnemy = entry.getKey();
		        	//Cannot instantiate tmpEnemy multiple times as it creates same enemy
		        	enemies.add( new Enemy(tmpEnemy.getType(), tmpEnemy.getRank(), 1));
		        }
		    }
		} else if(gameMode == GameMode.SURVIVAL) {
			player.addScore((int)(500 * Math.pow(waveNumber, 1.4)));
			int tmpEnemyType = 1;
			switch(levelTitle) {
				case "Bigger": tmpEnemyType = 7; break;
				case "Charge": tmpEnemyType = 6; break;
				case "Shooter": tmpEnemyType = 13; break;
				default: break;
			}
			
	        for(int i=0; i<waveNumber; i++) {
	        	enemies.add( new Enemy(tmpEnemyType, 4, 1));
	        }
	        enemies.add(new Enemy(8, 1, 1));
		}

	}

	@Override
	public void keyPressed(KeyEvent key) {
		// TODO Auto-generated method stub
		int keyCode = key.getKeyCode();
		
		keysPressed.add(key.getKeyCode());
		
		if(keysPressed.contains(KeyEvent.VK_CONTROL) && keysPressed.contains(KeyEvent.VK_T)) {
			//super turret?
		}
		
		if(gameMode==GameMode.TUTORIAL) {
			tutorial.updateKeyPressedCount(keyCode);
		}
		
		if(keyCode == KeyEvent.VK_LEFT){
			player.setLeft(true);
		}
		if(keyCode == KeyEvent.VK_RIGHT){
			player.setRight(true);
		}
		if(keyCode == KeyEvent.VK_UP){
			player.setUp(true);
		}
		if(keyCode == KeyEvent.VK_DOWN){
			player.setDown(true);
		}
		if(keyCode == KeyEvent.VK_SPACE){
			player.setFiring(true);
			//sfx.get("laser").play();
		}
		if(keyCode == KeyEvent.VK_X){
			sfx.get("bomb").play();
			player.placeBomb();			
		}	
		
		if(keyCode == KeyEvent.VK_A){
			sfx.get("addon").play();
			player.toggleAddOn();
		}
		if(!keysPressed.contains(KeyEvent.VK_CONTROL) && keyCode == KeyEvent.VK_T){
			if(turrets.size()<5){
				if(player.useStamina(400)){
					sfx.get("turret").play();
					player.placeTurret();
				}
			} else {
				texts.add(new Text(player.getx(), player.gety(),2000,"Max 5 Turrets"));
			}
		}
	
		
		if(keyCode == KeyEvent.VK_S){
			if(player.getShelterCount()>0){
				sfx.get("shelter").play();
				player.placeShelter();
			}
		}
		
		
		if(keyCode == KeyEvent.VK_D){
			if(!player.isSuperSpeed()) {
				if(player.useStamina(200)) {
					sfx.get("dash").play();
					player.startSuperSpeed();
				}	
			}
		
		}
		
		if(keyCode == KeyEvent.VK_Q){
			if(player.useStamina(500)){
				sfx.get("push").play();
				player.startPushing();
			}
		}
		
		if(keyCode == KeyEvent.VK_W){
			if(player.skillTimeRemaining("W - FreezeAOE") > 0) {
				skillCdWarn();
				return;
			}
			if(player.useStamina(800)){
				sfx.get("freeze").play();
				player.useSkillWithCd("W - FreezeAOE");
				player.freezeAOE(7000);
			}
		}
		
		if(keyCode == KeyEvent.VK_E){
			if(player.useStamina(700)){
				sfx.get("place black hole").play();
				player.placeBlackHole();
			}
		}	
		
		if(keyCode == KeyEvent.VK_R){			
			if(player.isInvincible()) {
				sfx.get("invincible off").play();
				player.toggleInvincible();
			} else {
				if(player.useStamina(200)){
					sfx.get("invincible on").play();
					player.toggleInvincible();		
				}
			}			
		}
		
		if(keyCode == KeyEvent.VK_F){
			if(player.skillTimeRemaining("F - Collect") > 0) {
				skillCdWarn();
				return;
			}
			if(player.useStamina(700)) {				
				sfx.get("collect").play();
				player.useSkillWithCd("F - Collect");
				player.startCollecting();
			}			
		}		
		
		if(keyCode == KeyEvent.VK_1){
			sfx.get("tp turret").play();
			player.tpToTurret(0);
			removeTurret(0);
		}
		if(keyCode == KeyEvent.VK_2){
			sfx.get("tp turret").play();
			player.tpToTurret(1);
			removeTurret(1);
		}
		if(keyCode == KeyEvent.VK_3){
			sfx.get("tp turret").play();
			player.tpToTurret(2);
			removeTurret(2);
		}
		if(keyCode == KeyEvent.VK_4){
			sfx.get("tp turret").play();
			player.tpToTurret(3);
			removeTurret(3);
		}
		if(keyCode == KeyEvent.VK_5){
			sfx.get("tp turret").play();
			player.tpToTurret(4);
			removeTurret(4);
		}		
		//Pause
		if(keyCode == KeyEvent.VK_NUMPAD0 || keyCode == KeyEvent.VK_P){		
			if(gameState == GameState.PLAY) {
				sfx.get("pause").play();
				pauseGame();
			} else if(gameState == GameState.PAUSED){
				sfx.get("pause").play();
				resumeGame();
			}	
		}
		
		//Shop Related
		if(keyCode == KeyEvent.VK_F1) {
			sp.buyLifeBtn.doClick();
		}
		if(keyCode == KeyEvent.VK_F2) {
			sp.buyPowerBtn.doClick();
		}
		if(keyCode == KeyEvent.VK_F3) {
			sp.buyAbilityBtn.doClick();
		}
		if(keyCode == KeyEvent.VK_F4) {
			sp.dropRateBtn.doClick();
		}
		if(keyCode == KeyEvent.VK_F5) {
			waveNumber = 17;
		}
		if(keyCode == KeyEvent.VK_F6) {
			enemies.clear();
		}
		if(keyCode == KeyEvent.VK_F7) {
			slowFields.add(new SlowField(player.getx(), player.gety(), 250, 15000));
		}
	}

	@Override
	public void keyReleased(KeyEvent key) {
		// TODO Auto-generated method stub
		int keyCode = key.getKeyCode();
		
		keysPressed.remove(keyCode);
		
		if(keyCode == KeyEvent.VK_LEFT){
			player.setLeft(false);
		}
		if(keyCode == KeyEvent.VK_RIGHT){
			player.setRight(false);
		}
		if(keyCode == KeyEvent.VK_UP){
			player.setUp(false);
		}
		if(keyCode == KeyEvent.VK_DOWN){
			player.setDown(false);
		}
		if(keyCode == KeyEvent.VK_SPACE){
			player.setFiring(false);
		}
		
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public static void alertStaminaLow() {
		sfx.get("stamina low").play();
		staminaLowStartTime = System.nanoTime();
		isStaminaLow = true;
	}
	
	public void skillCdWarn() {
		sfx.get("skill cd").play();
		skillCdWarnStartTime = System.nanoTime();
		isSkillCdWarn = true;
		texts.add(new Text(player.getx(), player.gety(),2000,"Cooldown!"));
	}
	
	public void pauseGame() {
		gameState = GameState.PAUSED;	
		//jframe.setState(Frame.ICONIFIED);
		pauseStartTime = System.nanoTime();
		shKbBtnblinkStartTime = System.nanoTime();
	}
	
	public void resumeGame() {
		gameState = GameState.PLAY;
		totalPausedTime += pauseElapsedTime;
		System.out.println(totalPausedTime);
	}
	
	public static void updatePuCount(PowerUp pu, boolean remove) {
		if(puCountMap.containsKey(pu.getType())) {
			if(remove) {
				puCountMap.put(pu.getType(), puCountMap.get(pu.getType())-1);
			} else {
				puCountMap.put(pu.getType(), puCountMap.get(pu.getType())+1);
			}
		}
	}
}
