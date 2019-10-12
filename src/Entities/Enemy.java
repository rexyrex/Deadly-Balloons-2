package Entities;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashMap;

import Panels.GamePanel;
import Utils.MathUtils;
import Utils.RandomUtils;
import VFX.Text;


public class Enemy {
	private double x;
	private double y;
	private int r;
	
	private double dx;
	private double dy;
	private double rad;
	private double speed;
	
	private double maxHealth;
	private double health;
	private int type;
	private int rank;
	
	private boolean gettingBombed;
	private long gettingBombedTimer;
	private long gettingBombedInterval;
	
	private boolean targeted;
	
	
	private Color color1;
	
	private boolean ready;
	private boolean dead;
	
	//when hit
	private boolean hit;
	private long hitTimer;
	
	private boolean isPulled = false;
	
	private double dxStore;
	private double dyStore;
	private double dxa = 0;
	private double dya = 0;
	
	private String[] fnames = { "양","장","임","김","안"};
	private String[] lnames = { "세훈","주환","희주","석주","민형","광천","성호","상준"};
	
	private String name;

	
	private boolean slow;
	private boolean stunned;
	private long stunnedElapsed;
	private long stunnedLength;
	private long stunnedStart;
	
	private boolean regenMode;
	private long regenTimer;
	private long regenLength;
	private long lastRegenTime;
	private long regenDelay;
	
	private HashMap<String, Double> skillSet;
	
	//charge enemy related vars
	private enum ChargeState{
		STATIONARY, CHARGING
	}
	
	private ChargeState chargeState;
	
	private double destX;
	private double destY;
	
	private int canvas_width;
	private int canvas_height;
	
	private long lastChargeCompleteTime;
	
	private int money;
	
	private int moneyEnemyAlpha;
	private boolean moneyEnemyAlphaInc;
	
	
	//constructor
	public Enemy(int type, int rank, double moneyMult){
		this.type = type;
		this.rank = rank;
		regenMode = false;
		regenLength = 2000;
		regenDelay = 10000;
		lastRegenTime = System.nanoTime();
		
		stunned =false;
		stunnedElapsed = 0;
		stunnedStart = System.nanoTime();
		stunnedLength = 0;

		
		this.canvas_height = GamePanel.HEIGHT;
		this.canvas_width = GamePanel.WIDTH;

		
		int fn = (int) (Math.random() * fnames.length);
		int ln = (int) (Math.random() * lnames.length);
		
		this.name = fnames[fn] + " " + lnames[ln];
		
		skillSet = new HashMap();
		
		//default
		if(type ==1){
			//skillSet.put("blackhole chance skill", 1.0);
			//skillSet.put("boss skill", 1.0);
			//color1 = Color.blue;
			color1 = new Color(0,0,255,128);
			if(rank ==1){
				speed = 2;
				r=10;
				health = 1;
				money = 1;
			}
			if(rank ==2){
				speed = 2;
				r = 20;
				health = 2;
				money = 2;
			}
			if(rank ==3){
				speed = 1.5;
				r = 40;
				health = 3;
				money = 3;
			}
			if(rank ==4){
				speed = 1.5;
				r = 67;
				health = 14;
				money = 4;
			}
		}
		
		if(type ==2){
			//color1 = Color.red;
			color1 = new Color(255,0,0,128);
			if(rank == 1){
				speed = 3;
				r=12;
				health = 1;
				money = 1;
			}
			if(rank == 2){
				speed = 3;
				r=24;
				health = 2;
				money = 2;
			}
			if(rank == 3){
				speed = 2.6;
				r=44;
				health = 4;
				money = 4;
			}
			if(rank == 4){
				speed = 2.4;
				r=66;
				health = 14;
				money = 5;
			}
		}
		
		if(type ==3){
			skillSet.put("blackhole chance skill", 0.2);
			skillSet.put("bomb chance skill",0.2);
			//color1 = Color.GREEN;
			color1 = new Color(0,255,0,128);
			if(rank == 1){
				speed = 1.5;
				r=10;
				health = 5;
				money = 1;
			}
			if(rank == 2){
				speed = 1.5;
				r=20;
				health = 6;
				money = 3;
			}
			if(rank == 3){
				speed = 1.5;
				r=50;
				health = 7;
				money = 5;
			}
			if(rank == 4){
				speed = 1.2;
				r=90;
				health = 19;
				money = 6;
			}
		}
		
		if(type ==4){
			//color1 = Color.BLACK;
			color1 = new Color(0,0,0,128);
			if(rank == 1){
				speed = 3.77;
				r=15;
				health = 7;
				money = 2;
			}
			if(rank == 2){
				speed = 3.47;
				r=30;
				health = 7;
				money = 4;
			}
			if(rank == 3){
				speed = 2.97;
				r=70;
				health = 17;
				money = 5;
			}
			if(rank == 4){
				speed = 2.77;
				r=100;
				health = 27;
				money = 7;
			}
		}
		if(type ==5){
			//color1 = rainbow;
			skillSet.put("follow player skill", 3.0);
			color1 = new Color(255,255,0,128);
			if(rank == 1){
				speed = 4.7;
				r=20;
				health = 7;
				money = 3;
			}
			if(rank == 2){
				speed = 3.9;
				r=37;
				health = 12;
				money = 4;
			}
			if(rank == 3){
				speed = 3.4;
				r=82;
				health = 20;
				money = 6;
			}
			if(rank == 4){
				speed = 2.9;
				r=107;
				health = 75;
				money = 7;
			}
		}
		
		if(type ==6){
			skillSet.put("charge skill", 3.0);
			color1 = new Color(155,155,255,128);
			skillSet.put("health bar skill", 1.0);
			//name = "haha";
			if(rank == 1){
				speed = 12;
				r = 30;
				health = 20;
				maxHealth = 20;
				money = 5;
			}
			
			if(rank == 2){
				speed = 15;
				r = 40;
				health = 30;
				maxHealth = 30;
				money = 7;
			}
			if(rank == 3){
				speed = 17;
				r = 50;
				health = 40;
				maxHealth = 40;
				money = 10;
			}
			
			if(rank == 4){
				speed = 20;
				r = 60;
				health = 50;
				maxHealth = 50;
				money = 12;
			}	
		}
		
		if(type ==7){
			skillSet.put("growing skill", 2.47);
			color1 = new Color(102,222,255,128);
			skillSet.put("health bar skill", 1.0);
			//name = "haha";
			if(rank == 1){
				speed = 6;
				r = 10;
				health = 25;
				maxHealth = 25;
				money = 4;
			}
			
			if(rank == 2){
				speed = 5;
				r = 20;
				health = 40;
				maxHealth = 40;
				money = 6;
			}
			if(rank == 3){
				speed = 4;
				r = 30;
				health = 55;
				maxHealth = 55;
				money = 8;
			}
			
			if(rank == 4){
				speed = 3;
				r = 40;
				health = 70;
				maxHealth = 70;
				money = 9;
			}	
		}
		
		//money enemy
		if(type ==8){
			skillSet.put("money skill", 1.0);
			skillSet.put("change direction skill", 2.0);			
			skillSet.put("health bar skill", 1.0);
			moneyEnemyAlpha = 255;
			color1 = new Color(255,215,0,moneyEnemyAlpha);

			if(rank == 1){
				speed = 5;
				r = 40;
				health = 1000;
				maxHealth = 1000;
				money = 0;
			}
		}
		
		//tutorial enemy
		if(type ==1000){
			color1 = new Color(50,205,50,241);
			if(rank == 1){
				speed = 1.7;
				r = 60;
				health = 15;
				maxHealth = 15;
				money = 0;
			}
			if(rank == 2){
				speed = 1.5;
				r = 70;
				health = 22;
				maxHealth = 22;
				money = 0;
			}
			if(rank == 3){
				speed = 1.2;
				r = 80;
				health = 30;
				maxHealth = 30;
				money = 0;
			}
		}
		
		if(type ==100){
			skillSet.put("boss 1 skill", 3.0);
			skillSet.put("health bar skill", 1.0);
			skillSet.put("regen skill", 10000.0);
			skillSet.put("spawn powerup skill", 0.1);
			color1 = new Color(255,0,255,128);
			name = "닥터 양";
			if(rank == 1){
				speed = 4;
				r = 90;
				health = 1000;
				maxHealth = 1000;
				money = 500;
			}			
		}
		
		if(type ==101){
			skillSet.put("boss 2 skill", 3.0);
			skillSet.put("health bar skill", 1.0);
			skillSet.put("regen skill", 10000.0);
			skillSet.put("spawn powerup skill", 0.1);
			skillSet.put("blackhole chance skill", 1.0);
			skillSet.put("bomb chance skill",0.7);
			skillSet.put("growing skill", 1.0);
			color1 = new Color(255,255,255,128);
			name = "42";
			if(rank == 1){
				speed = 6;
				r = 50;
				health = 5000;
				maxHealth = 5000;
				money = 5000;
			}
		}
		
		if(type==91) {
			skillSet.put("boss 0 skill", 3.0);
			skillSet.put("health bar skill", 1.0);
			skillSet.put("regen skill", 10000.0);
			skillSet.put("spawn powerup skill", 0.1);
			color1 = new Color(255,0,255,128);
			name = "Donald";
			if(rank == 1){
				speed = 4;
				r = 90;
				health = 2000;
				maxHealth = 2000;
				money = 500;
			}	
		}
		
		money = (int) (money * moneyMult);
		
		x = Math.random() * GamePanel.WIDTH /2 + GamePanel.WIDTH/4;
		y = -r;
		
		double angle = Math.random() * 140 + 20;
		rad = Math.toRadians(angle);
		
		dx = Math.cos(rad) * speed;
		dy = Math.sin(rad) * speed;
		
		if(skillSet.containsKey("charge skill")) {
			//charge enemy vars
			chargingEnemyInit();
		}
		
		if(x > r && x < GamePanel.WIDTH - r && y > r && y < GamePanel.HEIGHT - r) {
			ready = true;
		}
		dead = false;
		
		hit = false;
		hitTimer = 0;
		
		gettingBombed = false;
		gettingBombedTimer = System.nanoTime();
		gettingBombedInterval = 100;
		
	}
	
	public void chargingEnemyInit() {
		lastChargeCompleteTime = System.nanoTime();
		chargeState = ChargeState.STATIONARY;
		destX = RandomUtils.getRandomDest(canvas_width, canvas_height)[0];
		destY = RandomUtils.getRandomDest(canvas_width, canvas_height)[1];
		//goTowards(destX,destY);
		dx=0;
		dy=0;
	}
	
	public int getMoney() { return money; }
	
	public String getName() { return name; }
	public double getx(){ return x; }
	public double gety(){ return y; }
	public void setx(double x) { this.x = x; }
	public void sety(double y) { this.y = y; }
	public int getr(){ return r; }
	public int getType(){ return type;}
	public int getRank() { return rank;}
	public boolean isGettingBombed() { return gettingBombed;}
	public void setGettingBombed(boolean b) { gettingBombed = b;}
	public void setIsPulled(boolean b) { isPulled = b; }
	public boolean getIsPulled() { return isPulled; }
	
	public void setSlow(boolean b){slow = b;}
	public void setTargeted(boolean b){targeted = b;}
	
	public void changeRadius(int r) {this.r = r;}
	public int getRadius() {return r;}
	
	public void stun(long stunLength) {
		stunned = true;
		stunnedStart = System.nanoTime();
		stunnedLength = stunLength;
	}
	
	public void produceRandomEnemy(){
		int typeChance = (int)(Math.random() * 100);
		int rankChance = (int)(Math.random() * 100);
		int actualType;
		int actualRank;
		
		
		if(typeChance<10){
			actualType = 5;
		} else if(typeChance<20){
			actualType = 4;
		} else if(typeChance<45){
			actualType = 3;
		} else if(typeChance<70){
			actualType = 2;
		} else {
			actualType = 1;
		}
		
		if(rankChance<20){
			actualRank = 4;
		} else if(rankChance<45){
			actualRank = 3;
		} else if(rankChance<70){
			actualRank = 2;
		} else {
			actualRank = 1;
		}
		
		Enemy e = new Enemy(actualType,actualRank, 0);
		e.setx(x);
		e.sety(y);
		GamePanel.enemies.add(e);
	}
	
	public void produceChargingEnemy() {
		
		Enemy e = new Enemy(6,1, 0);
		e.setx(x);
		e.sety(y);
		e.chargingEnemyInit();
		GamePanel.enemies.add(e);
	}

	
	public void heal(){
		int healAmount = (int)(Math.random() * 500);
		if(health+healAmount > maxHealth){
			health = maxHealth;
		} else {
			GamePanel.texts.add(new Text(x, y, 1700, "healed " + healAmount));
			health += healAmount;
		}
	}
	
	public void changeDirectionRandomly(){
		double angle = Math.random() * 360;
		rad = Math.toRadians(angle);
		
		dx = Math.cos(rad) * speed;
		dy = Math.sin(rad) * speed;
	}
	
	public void changeSpeedRandomly(){
		speed = Math.random() * 5 + 1;
	}
	
	public void changeColorRandomly(){
		int r = (int)(Math.random() * 255);
		int g = (int)(Math.random() * 255);
		int b = (int)(Math.random() * 255);
		int a = (int)(Math.random() * 255);
		color1 = new Color(r,g,b,a);
	}
	
	public void split(){
		if(rank > 1){
			int amount = 0;
			switch(type) {
				case 1: amount = 3; break;
				case 2: amount = 3; break;
				case 3: amount = 3; break;
				case 4: amount = 2; break;
				case 5: amount = 4; break;
				case 6: amount = 2; break;
				case 7: amount = 2; break;
				default : amount = 0; break;
			}
			
			
			for(int i=0; i<amount; i++){
				Enemy e = new Enemy(getType(), getRank()-1, 1);
				e.setSlow(slow);
				e.setGettingBombed(gettingBombed);
				e.x = this.x;
				e.y = this.y;
				
				double angle = 0;
				if(!ready){
					angle = Math.random()*140+20;
				} else {
					angle = Math.random() * 360;
				}
				e.rad = Math.toRadians(angle);
				e.dx = Math.cos(e.rad) * speed;
				e.dy = Math.sin(e.rad) * speed;
				//e.money = money/amount;
				
				//Growing enemies radius based on previous size
				if(skillSet.containsKey("growing skill")) {
					e.r = (int) (this.r * 0.7);
				}
				
				//Charging enemies charge state reset
				if(skillSet.containsKey("charge skill")) {
					e.dx = 0;
					e.dy = 0;
					e.chargeState = ChargeState.STATIONARY;
					lastChargeCompleteTime = System.nanoTime();
				}
				GamePanel.enemies.add(e);
			}
		}
	}
	
	public void placeBomb(){
		GamePanel.bombs.add(new Bomb(x,y,true));
	}
	
	public void placeBlackHole(){
		GamePanel.blackholes.add(new BlackHole(x,y,5,true,Math.random()*150,3000));
	}
	
	public boolean isInRange(double bx, double by, double br){
		double xDiff = bx - x;
		double yDiff = by - y;
		double distance = Math.sqrt(xDiff*xDiff +yDiff*yDiff);
		if(distance > (br+r)){
			return false;
		} else {
			return true;
		}
	}
	
	public void saveVector(){
		if(!isPulled){
			dxStore = dx;
			dyStore = dy;
			dxa = 0;
			dya = 0;
		}
	}
	
	public void restoreVector(){
		if(!isPulled){
			dx = dxStore;
			dy = dyStore;
			dxa = 0;
			dya = 0;
			chargeState = ChargeState.STATIONARY;
		}
	}
	
	public boolean pointCollide(double px, double py, double pr){
		double dist = Math.sqrt((px-x)*(px-x)+(py-y)*(py-y)) - pr;
		if(dist <= r){
			pushAway(px,py);
			return true;
		}
		return false;
	}
	
	public void wallCollide(double wx, double wy, double wwidth, double wheight){
		
		double leftTopdist = Math.sqrt((wx-x)*(wx-x)+(wy-y)*(wy-y));
		double rightTopdist = Math.sqrt((wx+wwidth-x)*(wx+wwidth-x) + (wy-y)*(wy-y));
		double leftBotdist = Math.sqrt((wx-x)*(wx-x)+(wy+wheight-y)*(wy+wheight-y));
		double rightBotdist = Math.sqrt((wx+wwidth-x)*(wx+wwidth-x) + (wy+wheight-y)*(wy+wheight-y));
		
		if(leftBotdist <= r){			
			//dx = -speed;
			//dy = speed;
			pushAway(wx,wy+wheight);
		}
		if(rightBotdist <= r){
			//dx = speed;
			//dy = speed;
			pushAway(wx+wwidth,wy+wheight);
		}
		
		if(leftTopdist <= r){
			//dy = -speed;
			//dx = -speed;
			pushAway(wx,wy);
		}
		
		if(rightTopdist <= r){			
			//dx = speed;
			//dy = -speed;
			pushAway(wx+wwidth,wy);
		}
		
		double wx1 = (wx + wwidth);
		double wy1 = (wy + wheight);
		
		// collide from top or bottom
		if (x <= (wx1) && x >= wx) {
			
			// top to bottom
			if (dy >= 0 && (y + r) >= wy && (y-r ) <= (wy1)) {
				dy = -Math.sin(rad) * speed;
				
			}

			// bot to top
			if (dy <= 0 && (y - r) <= (wy1) && (y+r ) >= wy) {
				dy = Math.sin(rad) * speed;
				
			}
		}
				
		// collide form the sides
		if ((y <= (wy1) && y >= wy)) {
			// left to right collide
			if (dx >= 0 && (x + r) >= wx && (x-r) <= (wx1)) {
				dx = -Math.cos(rad) * speed;
				
			}

			// right to left collide
			if (dx <= 0 && (x - r) <= (wx1) && (x+r ) >= wx ) {
				dx = Math.cos(rad) * speed;
				
			}
		}
	}
	
	public void startRegenMode(){
		if(((System.nanoTime() - lastRegenTime)/1000000 > regenDelay) && regenMode==false) {
			regenMode = true;
			regenTimer = System.nanoTime();
		}
	}
	
	public void pushAway(double px, double py){
		if(skillSet.containsKey("charge skill")) {
			//GamePanel.texts.add(new Text(x, y,2000,"I Cant be Pushed :)", true, Color.RED, Font.BOLD));
			chargingEnemyInit();
			return;
		}
		
		double xDiff = px-x;
		double yDiff = py-y;
		
			//double angle = Math.atan((y-py)/(x-px));
			if(xDiff<0)
				rad = Math.atan((yDiff)/(xDiff))+Math.toRadians(180);
			else if(yDiff<0)
				rad = Math.atan((yDiff)/(xDiff))+Math.toRadians(360);
			else
				rad = Math.atan((yDiff)/(xDiff));			
			
			dx = -Math.cos(rad) * speed;
			dy = -Math.sin(rad) * speed;
	}
	
	public void pullTowards(double px, double py){
		double xDiff = px-x;
		double yDiff = py-y;
		
			//double angle = Math.atan((y-py)/(x-px));
			if(xDiff<0)
				rad = Math.atan((yDiff)/(xDiff))+Math.toRadians(180);
			else if(yDiff<0)
				rad = Math.atan((yDiff)/(xDiff))+Math.toRadians(360);
			else
				rad = Math.atan((yDiff)/(xDiff));			
			
			dx = Math.cos(rad) * 2;
			dy = Math.sin(rad) * 2;

			isPulled = true;
	}
	
	public void randomizeDirection(){
		dx = Math.cos(Math.toRadians(Math.random()*360))*speed;
		dy = Math.cos(Math.toRadians(Math.random()*360))*speed;

	}
	
	public void goTowards(double px, double py){
		double xDiff = px-x;
		double yDiff = py-y;
		
			//double angle = Math.atan((y-py)/(x-px));
			if(xDiff<0)
				rad = Math.atan((yDiff)/(xDiff))+Math.toRadians(180);
			else if(yDiff<0)
				rad = Math.atan((yDiff)/(xDiff))+Math.toRadians(360);
			else
				rad = Math.atan((yDiff)/(xDiff));			
			
			dx = Math.cos(rad) * speed;
			dy = Math.sin(rad) * speed;
	}
	
	public void hit(double dmg){
		
		if(skillSet.containsKey("money skill")) {
			GamePanel.player.addScore((int) (dmg * skillSet.get("money skill")));
			GamePanel.texts.add(new Text(x, y,1000,"+" +(int) dmg, true, Color.GREEN, Font.BOLD));
		}
		
		if(!regenMode){
			health-= dmg;
			if(health <= 0){
				dead = true;
			}
			hit = true;
	
			hitTimer = System.nanoTime();
		} else {
			
			gainHealth(3);
		}
	}
	
	public void gainHealth(int gain){
		if(health+gain > maxHealth){
			health = maxHealth;
		} else {
			GamePanel.texts.add(new Text(getx(), gety(), 2000, "힐링된다!"));
			health += gain;
		}
	}
	
	public boolean isDead(){
		return dead;
	}
	
	public void update(Player player, ArrayList<Text> texts){
		
		if(skillSet.containsKey("change direction skill")) {
			if(RandomUtils.runChance(skillSet.get("change direction skill"))) {
				changeDirectionRandomly();
			}
		}
		
		if(skillSet.containsKey("money skill")) {
			if(RandomUtils.runChance(1.0)) {
				GamePanel.texts.add(new Text(x, y,1000,"-100", true, Color.RED, Font.BOLD));
				health-=100;
			}
		}
		
		if(skillSet.containsKey("bomb chance skill")) {
			if(RandomUtils.runChance(skillSet.get("bomb chance skill"))) {
				placeBomb();
			}
		}
		
		if(skillSet.containsKey("follow player skill")) {
			if(RandomUtils.runChance(skillSet.get("follow player skill"))) {
				goTowards(player.getx(), player.gety());
			}			
		}
		
		if(skillSet.containsKey("blackhole chance skill")) {
			if(RandomUtils.runChance(skillSet.get("blackhole chance skill"))) {
				texts.add(new Text(getx(), gety(), 2000, "Hostile Hole!"));
				placeBlackHole();
			}			
		}
		
		if(skillSet.containsKey("growing skill")) {
			if(RandomUtils.runChance(skillSet.get("growing skill"))) {
				texts.add(new Text(getx(), gety(), 2000, "Growing!"));
				r = r +1;
			}			
		}
		
		if(skillSet.containsKey("spawn powerup skill")) {

		}
		
		if(skillSet.containsKey("charge skill")) {
			//check if reached destination
			if(MathUtils.getDist(x, y, destX, destY)<r/2) {				
				chargeState = ChargeState.STATIONARY;
				this.dx = 0;
				this.dy=0;
				destX = RandomUtils.getRandomDestNoBorder(canvas_width, canvas_height)[0];
				destY = RandomUtils.getRandomDestNoBorder(canvas_width, canvas_height)[1];
				lastChargeCompleteTime = System.nanoTime();
			}	
			if((System.nanoTime() - lastChargeCompleteTime)/1000000000 > (skillSet.get("charge skill"))) {
				//if not charging, start charging
				if(chargeState == ChargeState.STATIONARY) {
					chargeState = ChargeState.CHARGING;
					speed = 20;
					texts.add(new Text(getx(), gety(), 2000, "Charge!"));
					goTowards(destX, destY);
				}
				
				
			}			
		}
		
		if(skillSet.containsKey("regen skill")) {
			//if(RandomUtils.runChance(1)){
				startRegenMode();
			//}
		}
		
		if(skillSet.containsKey("boss 0 skill")){
			if(RandomUtils.runChance(0.7)){
				int nSpawns = 2;
				texts.add(new Text(getx(), gety(), 2000, "+" + nSpawns + " spawned!"));
				for(int ss=0; ss<nSpawns; ss++){						
					produceRandomEnemy();
				}
			}
			
			if(RandomUtils.runChance(3)){
				changeDirectionRandomly();
			}
			
			if(RandomUtils.runChance(0.5)){
				changeSpeedRandomly();
			}
			
			if(RandomUtils.runChance(1.2)){
				goTowards(player.getx(), player.gety());
			}

		}
		
		if(skillSet.containsKey("boss 1 skill")){
			if(RandomUtils.runChance(0.7)){
				int nSpawns = 2;
				texts.add(new Text(getx(), gety(), 2000, "+" + nSpawns + " spawned!"));
				for(int ss=0; ss<nSpawns; ss++){						
					produceChargingEnemy();
				}
			}
			
			if(RandomUtils.runChance(3)){
				changeDirectionRandomly();
			}
			
			if(RandomUtils.runChance(0.5)){
				changeSpeedRandomly();
			}
			
			if(RandomUtils.runChance(1.5)){
				goTowards(player.getx(), player.gety());
			}

		}
		
		if(skillSet.containsKey("boss 2 skill")){
			if(RandomUtils.runChance(0.8)){
				int nSpawns = 2;
				texts.add(new Text(getx(), gety(), 2000, "+" + nSpawns + " spawned!"));
				for(int ss=0; ss<nSpawns; ss++){						
					produceChargingEnemy();
				}
			}
			
			if(RandomUtils.runChance(3)){
				changeDirectionRandomly();
			}
			
			if(RandomUtils.runChance(0.5)){
				changeSpeedRandomly();
			}
			
			if(RandomUtils.runChance(1.5)){
				goTowards(player.getx(), player.gety());
			}
			
			if(RandomUtils.runChance(0.1)){
				int tmpInterval = GamePanel.HEIGHT / 7;
				
				for(int j=0; j<7; j++) {
					if(RandomUtils.runChance(50)) {
						GamePanel.powerups.add(new PowerUp(17, 35 + j*tmpInterval,0));
					}					
				}					
				
			}
		}
		
		if(stunned) {
			stunnedElapsed = (System.nanoTime() - stunnedStart)/1000000;
			if(stunnedElapsed > stunnedLength) {
				stunned = false;
			}
		}
		
		if(!stunned) {
			if(slow){
				x+= dx*1.5;
				y+= dy *1.5;
			} else {
				x += dx;
				y += dy;
			}		
			
			x+= dxa;
			y+= dya;
		}
		
		
		if(!ready){
			if(x>r&&x<GamePanel.WIDTH-r
					&& y>r && y<GamePanel.HEIGHT-r){
				ready = true;
			}
		}
		/*
		if(x<r && dx<0) dx = -dx;
		if(y<r && dy<0) dy = -dy;
		if(x> GamePanel.WIDTH-r && dx>0) dx = -dx;
		if(y> GamePanel.HEIGHT-r && dy>0) dy = -dy;
		*/
		if(x<0 && dx<0) x = GamePanel.WIDTH;
		if(y<0 && dy<0) y = GamePanel.HEIGHT;
		if(x> GamePanel.WIDTH && dx>0) x = 0;
		if(y> GamePanel.HEIGHT && dy>0) y = 0;
		
		if(gettingBombed){
			long elapsed = (System.nanoTime()-gettingBombedTimer)/1000000;
			if(elapsed>gettingBombedInterval){
				gettingBombedTimer = System.nanoTime();
				hit(GamePanel.player.getBombDmg());
			}
		}
		
		if(hit){
			long elapsed = (System.nanoTime()-hitTimer)/1000000;
			if(elapsed > 50){

				hit = false;
				hitTimer = 0;
			}
		}
		
		if(regenMode){
			long elapsed = (System.nanoTime() - regenTimer)/1000000;
			if(elapsed > regenLength){
				regenMode = false;
				regenTimer = 0;
				lastRegenTime = System.nanoTime();
			}
		}
		
	}
	
	public void draw(Graphics2D g){
		
		if(skillSet.containsKey("charge skill")) {
			g.setColor(color1);
			g.setStroke(new BasicStroke(5));
			g.draw(new Line2D.Double(getx(), gety(), destX, destY));
			g.setStroke(new BasicStroke(1));
			double percentTillNextCharge = ((System.nanoTime() - lastChargeCompleteTime)/1000000000)/ skillSet.get("charge skill");
			
			int newAlpha = (int) (150 * percentTillNextCharge)+90;
			
			if(percentTillNextCharge < 1) {
				newAlpha = (int) (150 * percentTillNextCharge)+90;
			} else {
				newAlpha = 255;
			}
			if(newAlpha > 0) {			
				color1 = new Color(color1.getRed(), color1.getGreen(), color1.getBlue(),  newAlpha);
			}
		}
		
		//draw name
		if(type==6 || rank>3){
			g.setColor(new Color(245,222,179));
			g.setStroke(new BasicStroke(3));
			g.setFont(new Font("Gulim",Font.BOLD,16));
			String s = name;
			int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
			g.drawString(s, (int)(x-(length)/2), (int)(y));
			g.setStroke(new BasicStroke(1));
		}
		
		//render enemy circle
		if(skillSet.containsKey("money skill")) {
			if(moneyEnemyAlpha<77) {
				moneyEnemyAlphaInc = true;
			} else if(moneyEnemyAlpha>250){
				moneyEnemyAlphaInc = false;
			}
			if(moneyEnemyAlphaInc) {
				moneyEnemyAlpha+=17;
			} else {
				moneyEnemyAlpha-=17;
			}
			
			
			color1 = new Color(255,215, 0, moneyEnemyAlpha);
		}
		
		if(hit){
			g.setColor(color1.brighter());
			g.fillOval((int)(x-r),(int)(y-r), 2*r, 2*r);
			
			g.setStroke(new BasicStroke(3));
			g.setColor(Color.WHITE.darker());		
			g.drawOval((int)(x-r),(int)(y-r), 2*r, 2*r);
			g.setStroke(new BasicStroke(1));
		} else {
			if(isPulled){
				g.setColor(color1.darker());
			} else if(stunned){
				g.setColor(color1.brighter());
			}else
			{
				g.setColor(color1);
			}
			g.fillOval((int)(x-r),(int)(y-r), 2*r, 2*r);
			
			g.setStroke(new BasicStroke(3));
			g.setColor(color1.darker());		
			g.drawOval((int)(x-r),(int)(y-r), 2*r, 2*r);
			g.setStroke(new BasicStroke(1));
		}
		
		if(stunned) {
			int stunBarLength = (int) r;
			g.setColor(new Color(220,243,255,200));
			long timeSinceStun = (System.nanoTime() - stunnedStart)/1000000;
			double stunProgress = (double)timeSinceStun/(double)stunnedLength;
			if(stunProgress > 1) {
				stunProgress = 1;
			}			
			g.drawRect((int)(x-stunBarLength/2), (int)(y+stunBarLength/2), stunBarLength, stunBarLength/5);
			g.fillRect((int)(x-stunBarLength/2), (int)(y+stunBarLength/2), (int)(stunBarLength*stunProgress), stunBarLength/5);
		}
		
		if(targeted){
			//g.setColor(Color.red);
			//g.drawOval((int)(x-r/4),(int)(y-r/4), r/2, r/2);
		}
		
		//turn green when regen mode
		if(regenMode){
			g.setColor(new Color(0,255,0,144));
			g.fillOval((int)(x-r),(int)(y-r), 2*r, 2*r);
		}
		
		//draw regen bar
		if(skillSet.containsKey("regen skill")) {
			int healthBarLength = (int) r;
			g.setColor(Color.GREEN.darker());
			long timeSinceLastRegen = (System.nanoTime() - lastRegenTime)/1000000;
			double regenProgress = (double)timeSinceLastRegen/(double)regenDelay;
			if(regenProgress > 1) {
				regenProgress = 1;
			}			
			g.drawRect((int)(x-healthBarLength/2), (int)(y+healthBarLength/2), healthBarLength, healthBarLength/5);
			g.fillRect((int)(x-healthBarLength/2), (int)(y+healthBarLength/2), (int)(healthBarLength*regenProgress), healthBarLength/5);
		}
		
		
		if(skillSet.containsKey("health bar skill")){
			int healthBarLength = (int) r;
			g.setColor(Color.black);
			g.drawRect((int)(x-healthBarLength/2), (int)(y-healthBarLength/2), healthBarLength, healthBarLength/5);
			g.fillRect((int)(x-healthBarLength/2), (int)(y-healthBarLength/2), (int)(healthBarLength*(double)health/(double)maxHealth), healthBarLength/5);
		}
		
		
		
		
		
	}
	
}
