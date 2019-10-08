import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import Audio.AudioPlayer;

public class GamePanel extends JPanel implements Runnable, KeyListener{
	
	/**
	 * 
	 */
	private JFrame jframe;
	
	private static final long serialVersionUID = 1L;
	public static int WIDTH = 700;
	public static int HEIGHT = 700;
	
	private AudioPlayer bgmusic;
	private HashMap<String, AudioPlayer> sfx;
	
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
	
	public static long slowDownTimer;
	private long slowDownTimerDiff;
	private int slowDownLength = 6000;
	
	private BufferedImage img;
	
	public InfoPanel ip;
	public ShopPanel sp;
	
	private Menu menu;
	private Tutorial tutorial;
	public int btnLength = 170;
	public int btnHeight = 50;
	
	//Tutorial Related
	int currentTutorialStage;

	ArrayList<ArrayList<Integer>> allowedKeysPerTutStage;
	
	//Game Over Btns
	public Rectangle backFromGameOverBtn = new Rectangle(GamePanel.WIDTH /2 - btnLength/2, 525, btnLength, btnHeight);
	public Rectangle retryLvlBtn = new Rectangle(GamePanel.WIDTH /2 - btnLength/2, 455, btnLength, btnHeight);
	
	//Pause Btns
	public Rectangle resumeFromPausedBtn = new Rectangle(GamePanel.WIDTH /2 - btnLength/2, 455, btnLength, btnHeight);
	public Rectangle quitFromPauseBtn = new Rectangle(GamePanel.WIDTH /2 - btnLength/2, 525, btnLength, btnHeight);
	
	public enum GameState {
		TUTORIAL, PLAY, MENU, GAME_OVER, PAUSED
	}
	
	public enum MenuState {
		MAIN, CREDITS, PLAY_MODES, HELP, DEFAULT_LEVELS, SURVIVAL_LEVELS
	}
	
	public enum GameMode {
		TUTORIAL, DEFAULT, SURVIVAL
	}
	
	public static GameState gameState;
	public static MenuState menuState;
	public static GameMode gameMode;
	
	public static GameMode lastGameMode;
	
	public GamePanel(JFrame jframe, ShopPanel sp, InfoPanel ip){
		super();		
		this.jframe = jframe;
		this.ip = ip;
		this.sp = sp;
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
		menu = new Menu();
		tutorial = new Tutorial(this);
		addMouseListener(new MouseInput(menu, this));
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
            img = ImageIO.read(getClass().getResource("img/backImg6.png"));
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
		waveNames= new ArrayList<String>();
		waveData = new ArrayList<HashMap<Enemy, Integer>>();
		
		//bgmusic = new AudioPlayer("/Music/bgfinal.mp3");
		//bgmusic.play();

		sfx = new HashMap<String, AudioPlayer>();
		
		sfx.put("hit", new AudioPlayer("/SFX/enemy_hit.mp3"));
		sfx.put("player die", new AudioPlayer("/SFX/die.mp3"));
		sfx.put("enemy die", new AudioPlayer("/SFX/enemydie.mp3"));
		sfx.put("place black hole", new AudioPlayer("/SFX/place_black_hole.mp3"));
		sfx.put("collect powerup", new AudioPlayer("/SFX/powerup_collect.mp3"));
		

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
				gameRender();
				pauseRender();
				gameDraw();
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
				lvlElapsedTime = (System.nanoTime() - lvlStartTime)/1000000;
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
				System.out.println(1000.0 / ((totalTime/frameCount)/1000000));
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
		
		if(gm == GameMode.TUTORIAL) {
			currentTutorialStage = 0;
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
		
		player.init();
		waveStartTimer = 0;
		waveStartTimerDiff = 0;
		waveStart = true;
		waveNumber = 0;
		victorious = false;
		
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
		
		lvlStartTime = System.nanoTime();
	}

	
	private void gameOverRender() {
		g.setColor(new Color(0,0,0));
		g.fillRect(0,0,WIDTH,HEIGHT);
		g.setColor(Color.WHITE);
		g.setFont(new Font("Century Gothic",Font.BOLD,40));
		String s;
		if(victorious) {
			s = "V I C T O R Y";
		} else {
			s = "G A M E  O V E R";
		}		
		int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
		g.drawString(s, (WIDTH-length)/2, HEIGHT/2 - 120);
		//s = "Final Score: " + player.getScore();
		
		g.setFont(new Font("Century Gothic",Font.PLAIN,25));
		s = "Level : " + levelTitle;
		length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
		g.drawString(s, (WIDTH-length)/2, HEIGHT/2-50);		
		
		s = "Completed Waves : " + (waveNumber-1);
		length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
		g.drawString(s, (WIDTH-length)/2, HEIGHT/2+0);
		
		s = "Time : " + StringUtils.getTime(lvlElapsedTime);
		length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
		g.drawString(s, (WIDTH-length)/2, HEIGHT/2+50);
		
		g.drawString("Retry", retryLvlBtn.x+18, retryLvlBtn.y+33);
		g.draw(retryLvlBtn);
		
		g.drawString("Main Menu", backFromGameOverBtn.x+18, backFromGameOverBtn.y+33);
		g.draw(backFromGameOverBtn);
		
	}

	private void pauseRender() {
		g.setColor(new Color(255,255,255,120));
		g.fillRect(0,0,WIDTH,HEIGHT);
		g.setColor(Color.RED);
		g.setFont(new Font("Gulim", Font.BOLD,80));
		String s = "PAUSED";
		int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
		g.drawString(s, (WIDTH-length)/2, HEIGHT/2);
		
		g.setFont(new Font("Gulim", Font.BOLD,30));
		
		g.setColor(Color.green);
		g.drawString("Resume", resumeFromPausedBtn.x+22, resumeFromPausedBtn.y+35);
		g.draw(resumeFromPausedBtn);
		
		g.setColor(Color.RED);
		g.drawString("Give up", quitFromPauseBtn.x+22, quitFromPauseBtn.y+35);
		g.draw(quitFromPauseBtn);
		
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
			g.setColor(new Color(255,0,0,25));
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

		//draw bullets
		for(int i=0; i<bullets.size(); i++){
			bullets.get(i).draw(g);
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
			g.setFont(new Font("Gulim", Font.PLAIN,40));
			String s = " W A V E  " + waveNumber + "  :   " + waveNames.get(waveNumber-1);
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
		g.setColor(Color.WHITE);
		g.setFont(new Font("Century Gothic", Font.BOLD, 14));
		g.drawString("Time : " + StringUtils.getTime(elapsedTime), WIDTH-130, 30);
		
		//draw player speed
		g.setColor(Color.WHITE);
		g.setFont(new Font("Century Gothic",Font.PLAIN,14));
		g.drawString("Move Speed : " + player.getSpeed()+"/8", WIDTH-130, 50);
		
		//draw player speed
		g.setColor(Color.WHITE);
		g.setFont(new Font("Century Gothic",Font.PLAIN,14));
		g.drawString("Att Speed : " + (int)player.getAttSpeed()+"/50", WIDTH-130, 70);
		
		//draw player bombs
		g.setColor(Color.BLACK);
		g.setFont(new Font("Century Gothic",Font.PLAIN,14));
		g.drawString("BOMBS : " + player.getBombs(), WIDTH-130, 90);
		
		//draw player shelter count
		g.setColor(Color.WHITE);
		g.setFont(new Font("Century Gothic",Font.PLAIN,14));
		g.drawString("Shelters : " + player.getShelterCount(), WIDTH-130, 110);
		
		//draw player turrets
		g.setColor(Color.RED);
		g.setFont(new Font("Century Gothic",Font.PLAIN,14));
		g.drawString("Turrets : " + (5-turrets.size()) + "/5", WIDTH-130, 130);
		
		//draw player money
		g.setColor(Color.GREEN);
		g.setFont(new Font("Century Gothic",Font.PLAIN,14));
		g.drawString("Money : " + player.getScore(), WIDTH-130, 150);
		
		//draw slowdown meter
		if(slowDownTimer !=0){
			g.setColor(Color.WHITE);
			g.drawRect(20, 60, 100, 8);
			g.fillRect(20,60, (int)(100-100.0*slowDownTimerDiff/slowDownLength), 8);
		}
		
		//draw stamina meter
		g.setColor(new Color(0,0,0,222));
		g.drawRect(200, 640, 300, 8);
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
		g.fillRect(200, 640, (int)(300*(player.getCurrentStamina()/player.getMaxStamina())), 8);
		g.setColor(new Color(255,0,0,255));
		g.setFont(new Font("Century Gothic", Font.PLAIN, 14));
		g.drawString("Stamina : " + (int)player.getCurrentStamina() + " / " + (int)player.getMaxStamina(), 200, 660);
	}

	//GAME UPDATE
	private void gameUpdate() {
		if(gameMode == GameMode.TUTORIAL) {
			if(enemies.size() == 0) {
				waveNumber++;
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
				
				if(waveNumber > waveNames.size()) {
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
		
		//bullet update
		for(int i=0; i<bullets.size(); i++){
			boolean remove = bullets.get(i).update();
			if(remove){
				bullets.remove(i);
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
		
		// player auto collect powerup update
		if(player.isCollectingPu()) {
			for(int i=0; i<powerups.size(); i++) {
				PowerUp p = powerups.get(i);
				if(powerups.get(i).isInRange(player.getx(), player.gety(), player.getCollectRadius())) {
					
					//p.setBeingCollected(true);
					//p.collect();
					//p.showCollectTextAtPowerUp();
					
					//explosions.add(new Explosion(p.getx(), p.gety(),p.getr(), p.getr()+30));
					//powerups.remove(i);		
					p.goTowards(player.getx(), player.gety(), 2);
				} else {
					p.recoverMovement();
				}
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
			
			//player collision
//			if(player.isInRange(l.getx(), l.gety(), l.getr())){
//				player.moveAwayFrom(l.getx(), l.gety());
//				if(l.getx() == player.getx() && l.gety() == player.gety()){
//					player.changeDirectionRandomly();
//				}
//			}
			
			if(remove){
				shelters.remove(i);
				i--;
			}
		}
		
		//enemy position update
		double[] distances = new double[enemies.size()];
		//double[] distances2 = new double[enemies.size()];
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
		
		//is player bombing detect update
		/*if(player.isBombing()){
			for(int i=0; i<enemies.size(); i++){
				enemies.get(i).setGettingBombed(true);
			}
		} else {
			for(int i=0; i<enemies.size(); i++){
				enemies.get(i).setGettingBombed(false);
			}
		}*/
		
		
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
		
		//check dead enemies
		for(int i=0; i<enemies.size(); i++){
			if(enemies.get(i).isDead()){
				sfx.get("enemy die").play();
				Enemy e= enemies.get(i);
				
				int randNum = (int)(Math.random() * 1000);
				int randNum2 = (int)(Math.random() * 1000);
				//power up drop
				
				//Extra life
				if(RandomUtils.runChance(0.3 * player.getDropRateMultiplier())) {
					double[] offset = RandomUtils.getRandomOffset(5, 5);
					powerups.add(new PowerUp(1, e.getx()+offset[0],e.gety()+offset[1]));
				}
				
				//Power + 1
				if(RandomUtils.runChance(1.4 * player.getDropRateMultiplier())) {
					double[] offset = RandomUtils.getRandomOffset(5, 5);
					powerups.add(new PowerUp(3, e.getx()+offset[0],e.gety()+offset[1]));
				}
				
				//Power + 2
				if(RandomUtils.runChance(0.7 * player.getDropRateMultiplier())) {
					double[] offset = RandomUtils.getRandomOffset(5, 5);
					powerups.add(new PowerUp(2, e.getx()+offset[0],e.gety()+offset[1]));
				}
				
				//Faster Enemies
				if(RandomUtils.runChance(2)) {
					double[] offset = RandomUtils.getRandomOffset(5, 5);
					powerups.add(new PowerUp(4, e.getx()+offset[0],e.gety()+offset[1]));
				}
				
				//Player Speed Increase
				if(RandomUtils.runChance(0.3 * player.getDropRateMultiplier())) {
					double[] offset = RandomUtils.getRandomOffset(5, 5);
					powerups.add(new PowerUp(5, e.getx()+offset[0],e.gety()+offset[1]));
				}
				
				//Bomb +1
				if(RandomUtils.runChance(0.3 * player.getDropRateMultiplier())) {
					double[] offset = RandomUtils.getRandomOffset(5, 5);
					powerups.add(new PowerUp(6, e.getx()+offset[0],e.gety()+offset[1]));
				}
				
				//Att Speed +1
				if(RandomUtils.runChance(0.3 * player.getDropRateMultiplier())) {
					double[] offset = RandomUtils.getRandomOffset(5, 5);
					powerups.add(new PowerUp(7, e.getx()+offset[0],e.gety()+offset[1]));
				}
				
				//Spaz
				if(RandomUtils.runChance(1.2 * player.getDropRateMultiplier())) {
					double[] offset = RandomUtils.getRandomOffset(5, 5);
					powerups.add(new PowerUp(8, e.getx()+offset[0],e.gety()+offset[1]));
				}
				
				//Seeker missile
				if(RandomUtils.runChance(1.0 * player.getDropRateMultiplier())) {
					double[] offset = RandomUtils.getRandomOffset(5, 5);
					powerups.add(new PowerUp(10, e.getx()+offset[0],e.gety()+offset[1]));
				}
				
				//Add on
				if(RandomUtils.runChance(0.2 * player.getDropRateMultiplier())) {
					double[] offset = RandomUtils.getRandomOffset(5, 5);
					powerups.add(new PowerUp(9, e.getx()+offset[0],e.gety()+offset[1]));
				}
				
				//Stamina
				if(RandomUtils.runChance(0.7 * player.getDropRateMultiplier())) {
					double[] offset = RandomUtils.getRandomOffset(5, 5);
					powerups.add(new PowerUp(11, e.getx()+offset[0],e.gety()+offset[1]));
				}
				
				//Max Stamina
				if(RandomUtils.runChance(0.15 * player.getDropRateMultiplier())) {
					double[] offset = RandomUtils.getRandomOffset(5, 5);
					powerups.add(new PowerUp(12, e.getx()+offset[0],e.gety()+offset[1]));
				}
				
				//Wall
				if(RandomUtils.runChance(0.1 * player.getDropRateMultiplier())) {
					double[] offset = RandomUtils.getRandomOffset(5, 5);
					powerups.add(new PowerUp(13, e.getx()+offset[0],e.gety()+offset[1]));
				}	
				
				//Army
				if(RandomUtils.runChance(0.5 * player.getDropRateMultiplier())) {
					double[] offset = RandomUtils.getRandomOffset(5, 5);
					powerups.add(new PowerUp(14, e.getx()+offset[0],e.gety()+offset[1]));
				}	
				
				//Friends
				if(RandomUtils.runChance(0.5 * player.getDropRateMultiplier())) {
					double[] offset = RandomUtils.getRandomOffset(5, 5);
					powerups.add(new PowerUp(16, e.getx()+offset[0],e.gety()+offset[1]));
				}
				
				//Turret supercharge
				if(RandomUtils.runChance(0.8 * player.getDropRateMultiplier())) {
					double[] offset = RandomUtils.getRandomOffset(5, 5);
					powerups.add(new PowerUp(15, e.getx()+offset[0],e.gety()+offset[1]));
				}
				
				//DIE Powerup (avoid)
				if(RandomUtils.runChance(0)) {
					double[] offset = RandomUtils.getRandomOffset(5, 5);
					powerups.add(new PowerUp(17, e.getx()+offset[0],e.gety()+offset[1]));
				}
				
				//Lightning Powerup
				if(RandomUtils.runChance(1.2 * player.getDropRateMultiplier())) {
					double[] offset = RandomUtils.getRandomOffset(5, 5);
					powerups.add(new PowerUp(18, e.getx()+offset[0],e.gety()+offset[1]));
				}
				
				//Torpedo Powerup
				if(RandomUtils.runChance(0.7 * player.getDropRateMultiplier())) {
					double[] offset = RandomUtils.getRandomOffset(5, 5);
					powerups.add(new PowerUp(19, e.getx()+offset[0],e.gety()+offset[1]));
				}

				player.addScore(e.getMoney());
				texts.add(new Text(e.getx(), e.gety(),1000,"+" +e.getMoney(), true, Color.GREEN, Font.BOLD));
				
				enemies.remove(i);
				i--;
				
				e.explode();
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
		
		//seeker bullet
		/*
		int listPos;
		double[] list = new double[enemies.size()];
		for(int i=0; i<bullets.size(); i++){
			double bx = bullets.get(i).getx();
			double by = bullets.get(i).gety();
			for(int j=0; j<enemies.size();j++){
				double ex = enemies.get(j).getx();
				double ey = enemies.get(j).gety();
				list[j] = Math.sqrt((bx-ex)*(bx-ex) + (by-ey)*(by-ey));				
			}
			
			int enemy = (int) smallestDistance(list)[1];
			if(enemies.get(enemy).getx() > bx){
				bullets.get(i).setdx(bullets.get(i).getSpeed());
			} else {
				bullets.get(i).setdx(-bullets.get(i).getSpeed());
			}
			
			if(enemies.get(enemy).gety() > by){
				bullets.get(i).setdy(bullets.get(i).getSpeed());
			} else {
				bullets.get(i).setdy(-bullets.get(i).getSpeed());
			}
			
			
		}*/
		
		
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
		HashMap<Enemy, Integer> currWaveData = waveData.get(waveNumber-1);
	    for (Map.Entry<Enemy, Integer> entry : currWaveData.entrySet()) {
	        for(int i=0; i<entry.getValue(); i++) {
	        	Enemy tmpEnemy = entry.getKey();
	        	//Cannot instantiate tmpEnemy multiple times as it creates same enemy
	        	enemies.add( new Enemy(tmpEnemy.getType(), tmpEnemy.getRank(), 1));
	        }
	    }
	}

	@Override
	public void keyPressed(KeyEvent key) {
		// TODO Auto-generated method stub
		int keyCode = key.getKeyCode();
		
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
		if(keyCode == KeyEvent.VK_Z){
			player.setFiring(true);
			//sfx.get("laser").play();
		}
		if(keyCode == KeyEvent.VK_S){
			//player.startSpazing();
		}
		if(keyCode == KeyEvent.VK_X){		
				player.placeBomb();			
		}
		if(keyCode == KeyEvent.VK_C){
			//startbombing?
		}
		if(keyCode == KeyEvent.VK_SLASH){////////////////////
			//waveNumber++;
			//createNewEnemies();
		}
		if(keyCode == KeyEvent.VK_1){
			if(player.useStamina(50)){
				player.tpToTurret(0);
				player.startPushing();
				removeTurret(0);
			}
		}
		
		if(keyCode == KeyEvent.VK_F){
			if(player.useStamina(750)) {
				player.startCollecting();
			}			
		}
		
		if(keyCode == KeyEvent.VK_2){
			if(player.useStamina(50)){
				player.tpToTurret(1);
				player.startPushing();
				removeTurret(1);
			}
		}
		if(keyCode == KeyEvent.VK_3){
			if(player.useStamina(50)){
				player.tpToTurret(2);
				player.startPushing();
				removeTurret(2);
			}
		}
		if(keyCode == KeyEvent.VK_4){
			if(player.useStamina(50)){
				player.tpToTurret(3);
				player.startPushing();
				removeTurret(3);
			}
		}
		if(keyCode == KeyEvent.VK_5){
			if(player.useStamina(50)){
				player.tpToTurret(4);
				player.startPushing();
				removeTurret(4);
			}
		}
		
		if(keyCode == KeyEvent.VK_6){

		}
		if(keyCode == KeyEvent.VK_A){
			player.toggleAddOn();
		}
		if(keyCode == KeyEvent.VK_D){
			//player.startFiringSide();
		}
		if(keyCode == KeyEvent.VK_Q){
			//player.gainAddOn();
		}
		if(keyCode == KeyEvent.VK_T){
			if(turrets.size()<5){
				if(player.useStamina(420)){
					player.placeTurret();
				}
			} else {
				texts.add(new Text(player.getx(), player.gety(),2000,"Max 5 Turrets at once!"));
			}
		}
		if(keyCode == KeyEvent.VK_B){
			if(player.useStamina(700)){
				sfx.get("place black hole").play();
				player.placeBlackHole();
			}
		}
		if(keyCode == KeyEvent.VK_C){
			if(player.useStamina(600)){
				player.startPushing();
			}
		}
		if(keyCode == KeyEvent.VK_E){
			if(player.getShelterCount()>0){
				player.placeShelter();
			}
		}
		
		if(keyCode == KeyEvent.VK_I){			
			if(player.useStamina(100)){
				player.toggleInvincible();		
			}
		}
		
		if(keyCode == KeyEvent.VK_SPACE){		
			if(gameState == GameState.PLAY) {
				gameState = GameState.PAUSED;	
				jframe.setState(Frame.ICONIFIED);
			} else if(gameState == GameState.PAUSED){
				gameState = GameState.PLAY;
			}
			
	}
		
	}

	@Override
	public void keyReleased(KeyEvent key) {
		// TODO Auto-generated method stub
		int keyCode = key.getKeyCode();
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
		if(keyCode == KeyEvent.VK_Z){
			player.setFiring(false);
		}
		
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
