import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;

import Audio.AudioPlayer;

public class GamePanel extends JPanel implements Runnable, KeyListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static int WIDTH = 700;
	public static int HEIGHT = 700;
	
	private AudioPlayer bgmusic;
	private HashMap<String, AudioPlayer> sfx;
	
	private Thread thread;
	private boolean running;
	
	private String[] lvlNames = {"손 풀 기", 
			"손 풀 기 2", "손 풀 기 3", 
			"이 건 머 징 ?", "다 합 쳐", 
			"양 세 훈", "보안 박사를 뽑아야돼",
			"점심때 머먹지?","엥?",
			"커 진 다","띠 용",
			"쉽 다","좀 많 네",
			"거 의 끝","어 려 운 건 끝",
			"폭 풍 전 야",
			"42","ㅅㄱㄹ"};
	
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
	public static ArrayList<Wall> walls;
	public static ArrayList<LineWall> linewalls;
	
	private long URDTimeMillis;
	private long elapsedTime;
	
	private long waveStartTimer;
	private long waveStartTimerDiff;
	private int waveNumber;
	private boolean waveStart;
	private int waveDelay = 4700;
	
	private long slowDownTimer;
	private long slowDownTimerDiff;
	private int slowDownLength = 6000;
	
	private BufferedImage img;
	
	public GamePanel(){
		super();
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setFocusable(true);
		requestFocus();	

	}
	
	public void addNotify(){
		super.addNotify();
		if(thread == null){
			thread = new Thread(this);
			thread.start();
		}
		addKeyListener(this);
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
            img = ImageIO.read(new File("Resources/img/backImg6.png"));
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
		walls = new ArrayList<Wall>();
		linewalls = new ArrayList<LineWall>();
		
		//bgmusic = new AudioPlayer("/Music/bgfinal.mp3");
		//bgmusic.play();
		
		player.addScore(1000);
		
		sfx = new HashMap<String, AudioPlayer>();
		
		sfx.put("hit", new AudioPlayer("/SFX/enemy_hit.mp3"));
		sfx.put("player die", new AudioPlayer("/SFX/die.mp3"));
		sfx.put("enemy die", new AudioPlayer("/SFX/enemydie.mp3"));
		sfx.put("place black hole", new AudioPlayer("/SFX/place_black_hole.mp3"));
		sfx.put("collect powerup", new AudioPlayer("/SFX/powerup_collect.mp3"));
		waveStartTimer = 0;
		waveStartTimerDiff = 0;
		waveStart = true;
		waveNumber = 0;
		
		
		long startTime;

		long waitTime;
		long totalTime = 0;
		
		int frameCount = 0;
		int maxFrameCount = 30;
		
		long targetTime = 1000/FPS;
		
		long gameStartTime = System.nanoTime();

		
		//Game Loop
		while(running){
			requestFocus();
			startTime = System.nanoTime();
			
			gameUpdate();
			gameRender();
			gameDraw();
			
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
				frameCount = 0;
				totalTime = 0;
			}
			
		}
		
		g.setColor(new Color(0,100,255));
		g.fillRect(0,0,WIDTH,HEIGHT);
		g.setColor(Color.WHITE);
		g.setFont(new Font("Century Gothic",Font.PLAIN,18));
		String s = "G A M E  O V E R";
		int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
		g.drawString(s, (WIDTH-length)/2, HEIGHT/2);
		//s = "Final Score: " + player.getScore();
		
		s = "Max Wave : " + waveNumber;
		length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
		g.drawString(s, (WIDTH-length)/2, HEIGHT/2+30);
		
		s = "Time : " + StringUtils.getTime(elapsedTime);
		length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
		g.drawString(s, (WIDTH-length)/2, HEIGHT/2+60);
		gameDraw();
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
			g.setColor(new Color(255,255,255,64));
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
			
		//draw wall
		for(int i=0; i<walls.size(); i++){
			walls.get(i).draw(g);
		}
			
		//draw line wall
		for(int i=0; i<linewalls.size(); i++){
			linewalls.get(i).draw(g);
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
		if(waveStartTimer != 0){
			g.setFont(new Font("Gulim", Font.PLAIN,40));
			String s = " W A V E  " + waveNumber + "  :   " + lvlNames[waveNumber-1];
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
		
		//draw player walls
		g.setColor(Color.RED.darker());
		g.setFont(new Font("Century Gothic",Font.PLAIN,14));
		g.drawString("Walls : " + player.getWalls(), WIDTH-130, 110);
		
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
		//new wave
		if(waveStartTimer == 0 && enemies.size() ==0){
			waveNumber++;
			waveStart = false;
			waveStartTimer = System.nanoTime();
		} else {
			waveStartTimerDiff = (System.nanoTime() - waveStartTimer)/1000000;
			if(waveStartTimerDiff>waveDelay){
				waveStart = true;
				waveStartTimer = 0;
				waveStartTimerDiff = 0;
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
			
			
			
			
			//enemy special : place bomb
//			if(e.getType() >= 3 && Math.random() < 0.002){
//				texts.add(new Text(e.getx(), e.gety(), 2000, "Hostile Bomb!"));
//				e.placeBomb();
//			}
			
			//enemy special : place black hole
//			if(e.getType() >= 2 && Math.random()<0.002){
//				texts.add(new Text(e.getx(), e.gety(), 2000, "Hostile Hole!"));
//				e.placeBlackHole();
//			}
			
		}
		
		//time bomb update
		for(int j=0; j< enemies.size(); j++){
			for(int i=0; i<bombs.size(); i++){
				boolean remove = bombs.get(i).update();
				if(bombs.get(i).getIsBombing() && !bombs.get(i).isHostile() && enemies.get(j).isInRange(bombs.get(i).getx(), bombs.get(i).gety(), bombs.get(i).getmaxr())){
					enemies.get(j).setGettingBombed(true);							
				}
				
				if(remove){
					bombs.remove(i);
					i--;
				}
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
		
		//wall update
		for(int i=0; i<walls.size(); i++){
			boolean remove = walls.get(i).update();
			if(remove){
				walls.remove(i);
				i--;
			}
		}
		
		//wall enemy collision
		for(int j=0; j<walls.size(); j++){
			for(int i=0; i<enemies.size(); i++){
			
				Wall w = walls.get(j);
				enemies.get(i).wallCollide(w.getx(), w.gety(), w.getWidth(), w.getHeight());
			}
		}
		
		//line wall update
		for(int i=0; i<linewalls.size(); i++){
			boolean remove = linewalls.get(i).update();
			LineWall l = linewalls.get(i);
			//collision
			for(int j=0; j<enemies.size(); j++){
				if(enemies.get(j).pointCollide(linewalls.get(i).getx(), linewalls.get(i).gety(), linewalls.get(i).getr())){
					linewalls.get(i).hit();
				}
			}
			
			//player collision
			if(player.isInRange(l.getx(), l.gety(), l.getr())){
				player.moveAwayFrom(l.getx(), l.gety());
				if(l.getx() == player.getx() && l.gety() == player.gety()){
					player.changeDirectionRandomly();
				}
			}
			
			if(remove){
				linewalls.remove(i);
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
					e.hit();
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
				if(RandomUtils.runChance(0.3 + player.getDropRateBonus())) {
					double[] offset = RandomUtils.getRandomOffset(5, 5);
					powerups.add(new PowerUp(1, e.getx()+offset[0],e.gety()+offset[1]));
				}
				
				//Power + 1
				if(RandomUtils.runChance(1.4 + player.getDropRateBonus())) {
					double[] offset = RandomUtils.getRandomOffset(5, 5);
					powerups.add(new PowerUp(3, e.getx()+offset[0],e.gety()+offset[1]));
				}
				
				//Power + 2
				if(RandomUtils.runChance(0.7 + player.getDropRateBonus())) {
					double[] offset = RandomUtils.getRandomOffset(5, 5);
					powerups.add(new PowerUp(2, e.getx()+offset[0],e.gety()+offset[1]));
				}
				
				//Faster Enemies
				if(RandomUtils.runChance(2)) {
					double[] offset = RandomUtils.getRandomOffset(5, 5);
					powerups.add(new PowerUp(4, e.getx()+offset[0],e.gety()+offset[1]));
				}
				
				//Player Speed Increase
				if(RandomUtils.runChance(0.3 + player.getDropRateBonus())) {
					double[] offset = RandomUtils.getRandomOffset(5, 5);
					powerups.add(new PowerUp(5, e.getx()+offset[0],e.gety()+offset[1]));
				}
				
				//Bomb +1
				if(RandomUtils.runChance(0.3 + player.getDropRateBonus())) {
					double[] offset = RandomUtils.getRandomOffset(5, 5);
					powerups.add(new PowerUp(6, e.getx()+offset[0],e.gety()+offset[1]));
				}
				
				//Att Speed +1
				if(RandomUtils.runChance(0.3 + player.getDropRateBonus())) {
					double[] offset = RandomUtils.getRandomOffset(5, 5);
					powerups.add(new PowerUp(7, e.getx()+offset[0],e.gety()+offset[1]));
				}
				
				//Spaz
				if(RandomUtils.runChance(1.2 + player.getDropRateBonus())) {
					double[] offset = RandomUtils.getRandomOffset(5, 5);
					powerups.add(new PowerUp(8, e.getx()+offset[0],e.gety()+offset[1]));
				}
				
				//Seeker missile
				if(RandomUtils.runChance(1.0 + player.getDropRateBonus())) {
					double[] offset = RandomUtils.getRandomOffset(5, 5);
					powerups.add(new PowerUp(10, e.getx()+offset[0],e.gety()+offset[1]));
				}
				
				//Add on
				if(RandomUtils.runChance(0.2 + player.getDropRateBonus())) {
					double[] offset = RandomUtils.getRandomOffset(5, 5);
					powerups.add(new PowerUp(9, e.getx()+offset[0],e.gety()+offset[1]));
				}
				
				//Stamina
				if(RandomUtils.runChance(0.7 + player.getDropRateBonus())) {
					double[] offset = RandomUtils.getRandomOffset(5, 5);
					powerups.add(new PowerUp(11, e.getx()+offset[0],e.gety()+offset[1]));
				}
				
				//Max Stamina
				if(RandomUtils.runChance(0.15 + player.getDropRateBonus())) {
					double[] offset = RandomUtils.getRandomOffset(5, 5);
					powerups.add(new PowerUp(12, e.getx()+offset[0],e.gety()+offset[1]));
				}
				
				//Wall
				if(RandomUtils.runChance(0.1 + player.getDropRateBonus())) {
					double[] offset = RandomUtils.getRandomOffset(5, 5);
					powerups.add(new PowerUp(13, e.getx()+offset[0],e.gety()+offset[1]));
				}	
				
				//Army
				if(RandomUtils.runChance(0.5 + player.getDropRateBonus())) {
					double[] offset = RandomUtils.getRandomOffset(5, 5);
					powerups.add(new PowerUp(14, e.getx()+offset[0],e.gety()+offset[1]));
				}	
				
				//Friends
				if(RandomUtils.runChance(0.5 + player.getDropRateBonus())) {
					double[] offset = RandomUtils.getRandomOffset(5, 5);
					powerups.add(new PowerUp(16, e.getx()+offset[0],e.gety()+offset[1]));
				}
				
				//Turret supercharge
				if(RandomUtils.runChance(0.8 + player.getDropRateBonus())) {
					double[] offset = RandomUtils.getRandomOffset(5, 5);
					powerups.add(new PowerUp(15, e.getx()+offset[0],e.gety()+offset[1]));
				}
				
				//DIE Powerup (avoid)
				if(RandomUtils.runChance(1.2)) {
					double[] offset = RandomUtils.getRandomOffset(5, 5);
					powerups.add(new PowerUp(17, e.getx()+offset[0],e.gety()+offset[1]));
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
			running = false;
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
				if(type==1){
					player.gainLife();
					texts.add(new Text(player.getx(), player.gety(),2000,"Life +1"));
				}
				if(type==2){
					player.increasePower(1);
					texts.add(new Text(player.getx(), player.gety(),2000,"Power +1"));
				}
				if(type==3){
					player.increasePower(2);
					texts.add(new Text(player.getx(), player.gety(),2000,"Power +2"));
				}
				if(type==4){
					slowDownTimer = System.nanoTime();
					for(int j = 0; j<enemies.size(); j++){
						enemies.get(j).setSlow(true);
					}
					texts.add(new Text(player.getx(), player.gety(),2000,"Speed Up!"));
				}
				if(type==5){
					player.incSpeed();
					texts.add(new Text(player.getx(), player.gety(),2000,"Speed +1"));
				}
				if(type==6){
					player.incBombs();
					texts.add(new Text(player.getx(), player.gety(),2000,"BOMB +1"));
				}
				if(type==7){
					player.incFireRate();
					texts.add(new Text(player.getx(), player.gety(),2000,"RATE OF FIRE +1"));
				}
				if(type==8){
					player.startSpazing();
					texts.add(new Text(player.getx(), player.gety(),2000,"SPAZ OUT"));
				}
				if(type==9){
					player.gainAddOn();
					texts.add(new Text(player.getx(), player.gety(),2000,"AddOn Equiped!"));
				}
				if(type==10){
					player.startFiringSide();
					texts.add(new Text(player.getx(), player.gety(),2000,"Missiles Activated!"));
				}
				if(type==11){
					player.gainStamina(p.getStaminaGain());
					texts.add(new Text(player.getx(), player.gety(),2000,""+(int)p.getStaminaGain() + " stamina gained!"));
				}
				if(type==12){
					player.increaseMaxStamina(p.getStaminaGain());
					texts.add(new Text(player.getx(), player.gety(),2000,"Max Stamina increased by "+(int)p.getStaminaGain() + "!"));
				}
				if(type==13){
					int gain = (int) (Math.random() * 10+5);
					player.gainWalls(gain);
					texts.add(new Text(player.getx(), player.gety(),2000,"Wall + "+gain + "!"));
				}
				
				if(type==14){					
					int tmpInterval = HEIGHT / 10;
					
					for(int j=0; j<10; j++) {
						friends.add(new Friend(35 + j*tmpInterval, HEIGHT, 35+ j*tmpInterval, 0, 1));
					}	
					texts.add(new Text(player.getx(), player.gety(),2000,"Army of Nerds!"));
				}
				
				if(type==15) {
					for(int j=0; j<turrets.size(); j++) {
						turrets.get(j).setSuperCharged(true);
					}
				}
				
				if(type==16){
					for(int j=0; j<4; j++) {
						friends.add(new Friend(player.getx(), player.gety(), 0, 0, 2));
					}
					texts.add(new Text(player.getx(), player.gety(),2000,"칭구 칭구"));
				}
				
				if(type==17){
					player.loseLife();
					texts.add(new Text(player.getx(), player.gety(),2000,"아프다..."));
				}
				
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

		
		if(waveNumber == 1){
			for(int i=0; i<5; i++){
				enemies.add(new Enemy(1,4, 1));
			}
		}
		
		if(waveNumber == 2){
			for(int i=0; i<6; i++){				
				enemies.add(new Enemy(2,4, 1));
			}
		}
		
		if(waveNumber == 3){			
			for(int i=0; i<2; i++){
				enemies.add(new Enemy(3,4,1));
			}
		}
		
		if(waveNumber == 4){
			for(int i=0; i<5; i++){
				enemies.add(new Enemy(6,3,1));
			}
		}
		
		if(waveNumber == 5){
			for(int i=0; i<2; i++){
				enemies.add(new Enemy(1,4,1));	
				enemies.add(new Enemy(2,4,1));	
				enemies.add(new Enemy(3,4,1));	
				enemies.add(new Enemy(6,3,1));	
			}
		}
		
		if(waveNumber == 6){
			enemies.add(new Enemy(100,1,1));
		}
		
		if(waveNumber == 7){
			for(int i=0; i<3; i++){
				enemies.add(new Enemy(1,4,1));
				enemies.add(new Enemy(6,4,1));
			}
		}
		
		if(waveNumber == 8){
			for(int i=0; i<3; i++){
				enemies.add(new Enemy(3,4,1));	
				enemies.add(new Enemy(4,4,1));				
			}
		}
		
		if(waveNumber == 9){
			for(int i=0; i<3; i++){
				enemies.add(new Enemy(1,4,1));
				enemies.add(new Enemy(5,3,1));
			}
		}
		
		if(waveNumber == 10){
			for(int j=0; j<6; j++) {
				enemies.add(new Enemy(7,4,1));
			}
		}
		
		if(waveNumber == 11){			
			enemies.add(new Enemy(1,4,1));
			enemies.add(new Enemy(2,4,1));
			enemies.add(new Enemy(3,4,1));
			enemies.add(new Enemy(4,4,1));
			enemies.add(new Enemy(7,4,1));
		}
		
		if(waveNumber == 12){
			for(int j=0; j<3; j++) {
				enemies.add(new Enemy(6,4,1));
				enemies.add(new Enemy(7,4,1));
			}
		}
		
		if(waveNumber == 13){
			for(int j=0; j<2; j++) {
				enemies.add(new Enemy(3,4,1));
				enemies.add(new Enemy(5,4,1));
				enemies.add(new Enemy(7,4,1));
			}
		}
		
		if(waveNumber == 14){
			for(int j=0; j<2; j++) {
				enemies.add(new Enemy(5,4,1));
				enemies.add(new Enemy(3,4,1));
				enemies.add(new Enemy(7,4,1));
			}
		}
		
		if(waveNumber == 15){
			enemies.add(new Enemy(6,4,1));
			enemies.add(new Enemy(1,4,1));
			enemies.add(new Enemy(2,4,1));
			enemies.add(new Enemy(3,4,1));
			enemies.add(new Enemy(4,4,1));
			enemies.add(new Enemy(7,4,1));
		}
		
		if(waveNumber == 16){			
			for(int i=0; i<10; i++)
				enemies.add(new Enemy(1,4,1));
		}
		
		if(waveNumber == 17){			
			enemies.add(new Enemy(101,1,1));
		}
		
		if(waveNumber == 18){
			running=false;
		}
	}

	@Override
	public void keyPressed(KeyEvent key) {
		// TODO Auto-generated method stub
		int keyCode = key.getKeyCode();
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
				removeTurret(0);
			}
		}
		
		if(keyCode == KeyEvent.VK_F){
			//bombs.add(new Bomb(player.getx(),player.gety(),false,true));
			//friends.add(new Friend(player.getx(), player.gety(), 0,0,2));
		}
		
		if(keyCode == KeyEvent.VK_2){
			if(player.useStamina(50)){
				player.tpToTurret(1);
				removeTurret(1);
			}
		}
		if(keyCode == KeyEvent.VK_3){
			if(player.useStamina(50)){
				player.tpToTurret(2);
				removeTurret(2);
			}
		}
		if(keyCode == KeyEvent.VK_4){
			if(player.useStamina(50)){
				player.tpToTurret(3);
				removeTurret(3);
			}
		}
		if(keyCode == KeyEvent.VK_5){
			if(player.useStamina(50)){
				player.tpToTurret(4);
				removeTurret(4);
			}
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
			if(player.getWalls()>0){
				if(player.useStamina(100)){				
					player.placeLineWall();
				}
			}
		}
		if(keyCode == KeyEvent.VK_O){
			//if(player.useStamina(15)){
				//player.placeWall();
			//}
		}
		
		if(keyCode == KeyEvent.VK_I){			
				player.toggleInvincible();			
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
