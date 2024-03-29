package Entities;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

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
	
	private boolean healHit;
	private long healHitTimer;
	
	private boolean isPulled = false;
	
	private double dxStore;
	private double dyStore;
	private double dxa = 0;
	private double dya = 0;
	
	private String[] fnames = { "Rex", "Marcus", "Sehoon"};
	private String[] lnames = { "Kim", "Siew", "Yang"};
	
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
	private long regenOnElapsed;
	
	private HashMap<String, Double> skillSet;
	
	private enum RexBossModes{
		NORMAL, FIRING, HEALSPAWN, CHARGE
	}	
	
	private RexBossModes rexBossMode;
	
	private long lastModeChangeStartTime;
	private long lastModeChangeElapsed;
	private long modeDuration;
	
	private long healerSpawnStartTime;
	private long healerSpawnElapsed;
	private long healerSpawnDuration;
		
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
	
	private int pulseEnemyMinRadius;
	private int pulseEnemyMaxRadius;
	private boolean pulseEnemyRadiusInc;
	
	private long lastShootTime;
	
	private double dropMultiplier;
	
	private double fireAngle;
	
	private double maxSpeed;
	
	
	private double slowFieldRadius;
	private double maxSlowFieldRadius;
	
	private HashMap<Long, EnemyBullet> rexBulletStartMap;
	private HashMap<Long, Long> rexBulletElapsedMap;
	
	
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
		
		lastShootTime = System.nanoTime();
		
		fireAngle = Math.random() * 360;

		
		this.canvas_height = GamePanel.HEIGHT;
		this.canvas_width = GamePanel.WIDTH;
		
		lastModeChangeElapsed = 0;
		lastModeChangeStartTime = System.nanoTime();
		modeDuration = 30000;
		
		healerSpawnElapsed = 0;
		healerSpawnStartTime = System.nanoTime();
		healerSpawnDuration = 7000;

		
		int fn = (int) (Math.random() * fnames.length);
		int ln = (int) (Math.random() * lnames.length);
		
		this.name = fnames[fn] + " " + lnames[ln];
		
		skillSet = new HashMap<String, Double>();
		
		dropMultiplier = 1.0;
		
		slowFieldRadius = 0;
		maxSlowFieldRadius = 0;
		
		rexBossMode = RexBossModes.NORMAL;
		rexBulletStartMap = new HashMap<>();
		rexBulletElapsedMap = new HashMap<>();
		
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
			dropMultiplier = 1.1;
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
			dropMultiplier = 1.2;
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
			dropMultiplier = 1.3;
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
			dropMultiplier = 1.5;
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
			dropMultiplier = 1.5;
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
				dropMultiplier = 1.7;
			}
			if(rank == 3){
				speed = 17;
				r = 50;
				health = 40;
				maxHealth = 40;
				money = 10;
				dropMultiplier = 1.9;
			}
			
			if(rank == 4){
				speed = 20;
				r = 60;
				health = 50;
				maxHealth = 50;
				money = 12;
				dropMultiplier = 2.2;
			}	
		}
		
		//growing
		if(type ==7){
			skillSet.put("growing skill", 2.47);
			
			skillSet.put("health bar skill", 1.0);
			//name = "haha";
			if(rank == 1){
				color1 = new Color(102,222,200,60);
				speed = 4;
				r = 10;
				health = 20;
				maxHealth = 20;
				money = 4;
			}
			
			if(rank == 2){
				color1 = new Color(102,222,220,80);
				speed = 3.7;
				r = 20;
				health = 32;
				maxHealth = 32;
				money = 6;
				dropMultiplier = 1.7;
			}
			if(rank == 3){
				color1 = new Color(102,222,240,100);
				speed = 3.4;
				r = 30;
				health = 45;
				maxHealth = 45;
				money = 8;
				dropMultiplier = 2.0;
			}
			
			if(rank == 4){
				color1 = new Color(102,222,255,128);
				speed = 3;
				r = 40;
				health = 55;
				maxHealth = 55;
				money = 9;
				dropMultiplier = 2.5;
			}	
		}
		
		//money enemy
		if(type ==8){
			dropMultiplier = 2;
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
		
		//pulsing enemy
		if(type ==9){
			skillSet.put("change direction skill", 2.2);
			skillSet.put("change speed skill", 1.2);
			skillSet.put("health bar skill", 1.0);
			skillSet.put("pulse skill", 1.0);
			skillSet.put("slow field skill", 0.27);
			
			color1 = new Color(176,196,222,200);

			if(rank == 1){
				speed = 6;
				r = 25;
				health = 15;
				maxHealth = 15;
				money = 10;
			}
			
			if(rank == 2){
				speed = 5;
				r = 34;
				health = 25;
				maxHealth = 25;
				money = 14;
				dropMultiplier = 1.7;
			}
			if(rank == 3){
				speed = 4;
				r = 44;
				health = 35;
				maxHealth = 35;
				money = 20;
				dropMultiplier = 2.0;
			}
			
			if(rank == 4){
				speed = 3;
				r = 55;
				health = 50;
				maxHealth = 50;
				money = 25;
				dropMultiplier = 2.5;
			}	
			pulseEnemyMaxRadius = (int)(r*1.2);
			pulseEnemyMinRadius = (int)(r*0.8);
		}
		
		//shooting enemy
		if(type ==10){
			//skillSet.put("change direction skill", 3.2);
			//skillSet.put("change speed skill", 3.2);
			//skillSet.put("health bar skill", 1.0);
			//skillSet.put("pulse skill", 1.0);
			
			skillSet.put("shooting skill",3200.0);
			
			color1 = new Color(112,128,144,200);

			if(rank == 1){
				speed = 6;
				r = 20;
				health = 15;
				maxHealth = 15;
				money = 10;
			}
			
			if(rank == 2){
				speed = 5;
				r = 25;
				health = 22;
				maxHealth = 22;
				money = 14;
				dropMultiplier = 1.7;
			}
			if(rank == 3){
				speed = 4;
				r = 32;
				health = 33;
				maxHealth = 33;
				money = 20;
				dropMultiplier = 2.0;
			}
			
			if(rank == 4){
				speed = 3;
				r = 40;
				health = 47;
				maxHealth = 47;
				money = 25;
				dropMultiplier = 2.5;
			}	

		}
		
		//shotgun enemy
		if(type ==11){
			//skillSet.put("change direction skill", 3.2);
			//skillSet.put("change speed skill", 3.2);
			//skillSet.put("health bar skill", 1.0);
			//skillSet.put("pulse skill", 1.0);
			
			skillSet.put("shotgun skill",4000.0);
			maxSpeed = 8;
			color1 = new Color(112,128,144,200);

			if(rank == 1){
				speed = 5;
				r = 25;
				health = 14;
				maxHealth = 14;
				money = 10;
			}
			
			if(rank == 2){
				speed = 4;
				r = 35;
				health = 24;
				maxHealth = 24;
				money = 12;
				dropMultiplier = 1.7;
			}
			if(rank == 3){
				speed = 3;
				r = 42;
				health = 34;
				maxHealth = 34;
				money = 15;
				dropMultiplier = 2.0;
			}
			
			if(rank == 4){
				speed = 2;
				r = 50;

				health = 45;
				maxHealth = 45;
				money = 20;
				dropMultiplier = 2.5;
			}
		}
		
		//swarm enemy
		if(type ==12){
			dropMultiplier = 2;
			color1 = new Color(255,105,180,200);

			if(rank == 1){
				speed = 7;
				r = 10;
				health = 5;
				maxHealth = 4;
				money = 7;
			}
			
			if(rank == 2){
				skillSet.put("health bar skill", 1.0);
				speed = 2;
				r = 100;
				health = 200;
				maxHealth = 200;
				money = 100;
				dropMultiplier = 3;
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
				health = 7000;
				maxHealth = 7000;
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
		
		//Rex Level 1st boss
		if(type == 1000000) {
			skillSet.put("rex boss 1 skill", 3.0);
			skillSet.put("health bar skill", 1.0);
			color1 = new Color(255,255,255,0);
			
			rexBossMode = RexBossModes.NORMAL;
			
			name = "Rexyrex";
			if(rank == 1){
				speed = 6;
				r = 72;
				health = 20000;
				maxHealth = 20000;
				money = 500;
				
				slowFieldRadius = 0;
				maxSlowFieldRadius = 220;
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
		
		healHit = false;
		healHitTimer = 0;
		
		gettingBombed = false;
		gettingBombedTimer = System.nanoTime();
		gettingBombedInterval = 100;
		
	}
	
	public double getSlowFieldRadius() {
		return slowFieldRadius;
	}

	public void setSlowFieldRadius(double slowFieldRadius) {
		this.slowFieldRadius = slowFieldRadius;
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
	
	public double getDropMultiplier() { return dropMultiplier;}
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

	
	public void rexHeal(){
		int healAmount = (int)(Math.random() * 20);
		health += healAmount;
		if(health > maxHealth) {
			health = maxHealth;
		}		
		healHit = true;
		healHitTimer = System.nanoTime();
		
	}
	
	public void changeDirectionRandomly(){
		double angle = Math.random() * 360;
		rad = Math.toRadians(angle);
		
		dx = Math.cos(rad) * speed;
		dy = Math.sin(rad) * speed;
	}
	
	public void changeSpeedRandomly(double min, double max){
		speed = Math.random() * (max-min) + min;
		
		dx = Math.cos(rad) * speed;
		dy = Math.sin(rad) * speed;
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
				case 8: amount = 2; break;
				case 9: amount = 2; break;
				case 10: amount = 2; break;
				case 11: amount = 2; break;
				case 12: amount = 27; break;
				default : amount = 0; break;
			}
			
			
			for(int i=0; i<amount; i++){
				Enemy e = new Enemy(getType(), getRank()-1, 1);
				e.setSlow(slow);
				e.setGettingBombed(gettingBombed);
				e.x = this.x;
				e.y = this.y;
				double angle = Math.random() * 360;
				
				e.rad = Math.toRadians(angle);
				e.dx = Math.cos(e.rad) * e.speed;
				e.dy = Math.sin(e.rad) * e.speed;
				//e.money = money/amount;
				
				//Growing enemies radius based on previous size
				if(skillSet.containsKey("growing skill")) {
					e.r = (int) (this.r);
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
				
		// collide from the sides
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

		
		double xDiff = px-x;
		double yDiff = py-y;
		
			//double angle = Math.atan((y-py)/(x-px));
			if(xDiff<0)
				rad = Math.atan((yDiff)/(xDiff))+Math.toRadians(180);
			else if(yDiff<0)
				rad = Math.atan((yDiff)/(xDiff))+Math.toRadians(360);
			else
				rad = Math.atan((yDiff)/(xDiff));	
			
			if(skillSet.containsKey("shotgun skill")) {
				rad += Math.PI;
			}
			
			dx = -Math.cos(rad) * speed;
			dy = -Math.sin(rad) * speed;
			
			if(skillSet.containsKey("charge skill")) {
				//GamePanel.texts.add(new Text(x, y,2000,"I Cant be Pushed :)", true, Color.RED, Font.BOLD));
				x += dx;
				y += dy;
				chargingEnemyInit();
			}
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
			
			gainHealth((int)(dmg*4));
		}
	}
	
	public void gainHealth(int gain){
		if(health+gain > maxHealth){
			health = maxHealth;
		} else {
			GamePanel.texts.add(new Text(getx(), gety(), 2000, "Heal"));
			health += gain;
		}
	}
	
	public boolean isDead(){
		return dead;
	}
	
	public void pauseUpdate() {
		regenTimer = System.nanoTime() - regenOnElapsed * 1000000;
		lastModeChangeStartTime = System.nanoTime() - lastModeChangeElapsed * 1000000;	
		healerSpawnStartTime = System.nanoTime() - healerSpawnElapsed * 1000000;	
	}
	
	public void changeRexMode() {
		switch(rexBossMode) {
			case NORMAL : rexBossMode = RexBossModes.FIRING; break;
			case FIRING : rexBossMode = RexBossModes.HEALSPAWN; break;
			case HEALSPAWN : rexBossMode = RexBossModes.CHARGE; break;
			case CHARGE : rexBossMode = RexBossModes.NORMAL; break;
			default : rexBossMode = RexBossModes.NORMAL; break;
		}
	}
	
	public void update(Player player, ArrayList<Text> texts){
		
		if(skillSet.containsKey("rex boss 1 skill")) {
			//change direction
			if(RandomUtils.runChance(2.7)) {
				changeDirectionRandomly();
			}
			
			//change speed
			if(RandomUtils.runChance(0.7)) {
				changeSpeedRandomly(4,10);
			}
			
			//change modes
			lastModeChangeElapsed = (System.nanoTime() - lastModeChangeStartTime) / 1000000;
			if(lastModeChangeElapsed > modeDuration) {
				changeRexMode();
				lastModeChangeStartTime = System.nanoTime();
			}
			
			//spawn divider
			if(rexBossMode == RexBossModes.NORMAL) {
				if(RandomUtils.runChance(1.27)) {
					int choice = ThreadLocalRandom.current().nextInt(1, 6 + 1);
					double width1 = RandomUtils.getNumBetween(0, GamePanel.WIDTH);
					double width2 = RandomUtils.getNumBetween(0, GamePanel.WIDTH);
					double height1 = RandomUtils.getNumBetween(0, GamePanel.HEIGHT);
					double height2 = RandomUtils.getNumBetween(0, GamePanel.HEIGHT);
					
					switch(choice) {
						case 1: 
							//Bot - Top
							GamePanel.dividers.add(new Divider(width1, GamePanel.HEIGHT, width2, 0));
							break;
						case 2: 
							//Bot - Right
							GamePanel.dividers.add(new Divider(width1, GamePanel.HEIGHT, GamePanel.WIDTH, height2));
							break;
						case 3:
							//Bot - Left
							GamePanel.dividers.add(new Divider(width1, GamePanel.HEIGHT, 0, height2));
							break;
						case 4: 
							//Top - Left
							GamePanel.dividers.add(new Divider(width1, 0, 0, height2));
							break;
						case 5: 
							//Top - Right
							GamePanel.dividers.add(new Divider(width1, 0, GamePanel.WIDTH, height2));
							break;
						case 6: 
							//Left - Right
							GamePanel.dividers.add(new Divider(0, height1, GamePanel.WIDTH, height2));
							break;
					}
				}
			}
			
			//slow field on when charging
			if(rexBossMode == RexBossModes.CHARGE) {
				slowFieldRadius = maxSlowFieldRadius;
			} else {
				slowFieldRadius = 0;
			}
			
			//charge at player
			if(rexBossMode == RexBossModes.CHARGE && RandomUtils.runChance(7.2)) {
				goTowards(player.getx(), player.gety());
			}
			
			//spawn turrets
			healerSpawnElapsed = (System.nanoTime() - healerSpawnStartTime) / 1000000;
			if(rexBossMode == RexBossModes.HEALSPAWN && healerSpawnElapsed > healerSpawnDuration) {
				healerSpawnStartTime = System.nanoTime();
				//spawn 4 healers : 1 on each qudrant
				//healers may not spawn
				double h1x = RandomUtils.getNumBetween(20, GamePanel.WIDTH/2);
				double h1y = RandomUtils.getNumBetween(20, GamePanel.HEIGHT/2);
				
				double h2x = RandomUtils.getNumBetween(20, GamePanel.WIDTH/2);
				double h2y = RandomUtils.getNumBetween(GamePanel.HEIGHT/2, GamePanel.HEIGHT -20);
				
				double h3x = RandomUtils.getNumBetween(GamePanel.WIDTH/2, GamePanel.WIDTH -20);
				double h3y = RandomUtils.getNumBetween(20, GamePanel.HEIGHT/2);
				
				double h4x = RandomUtils.getNumBetween(GamePanel.WIDTH/2, GamePanel.WIDTH -20);
				double h4y = RandomUtils.getNumBetween(GamePanel.HEIGHT/2, GamePanel.HEIGHT -20);
				
				if(RandomUtils.runChance(77.2)) {
					GamePanel.enemyTurrets.add(new EnemyTurret(h1x,h1y,72,true));
				}
				
				if(RandomUtils.runChance(77.2)) {
					GamePanel.enemyTurrets.add(new EnemyTurret(h2x,h2y,72,true));
				}
				
				if(RandomUtils.runChance(77.2)) {
					GamePanel.enemyTurrets.add(new EnemyTurret(h3x,h3y,72,true));
				}
				
				if(RandomUtils.runChance(77.2)) {
					GamePanel.enemyTurrets.add(new EnemyTurret(h4x,h4y,72,true));
				}
			}
			
			//add bullet
			if(rexBossMode == RexBossModes.FIRING && RandomUtils.runChance(27.2)) {
				
				double fireAngleDeg = 360 * Math.random();
				boolean lethal = false;
				if(RandomUtils.runChance(27.2)) {
					lethal=true;
				}
				rexBulletStartMap.put(System.nanoTime(), new EnemyBullet(fireAngleDeg, 0, 0, 0, 0, lethal, false));
			}
			
			//update bullet elapsed
			ArrayList<Long> rexBulletsToRemove = new ArrayList();
			for (Map.Entry<Long, EnemyBullet> entry : rexBulletStartMap.entrySet()) {
				long rexBulletElapsed = 0;
				
				if(rexBulletElapsedMap.containsKey(entry.getKey())) {
					rexBulletElapsed = rexBulletElapsedMap.get(entry.getKey());
				}
				
				rexBulletElapsed = (System.nanoTime() - entry.getKey()) / 1000000;
				rexBulletElapsedMap.put(entry.getKey(),  rexBulletElapsed);
				
				//shoot bullet
				if(rexBulletElapsed > 4000) {
					EnemyBullet tmpEb = entry.getValue();
					
					double fireAngleRad = tmpEb.getRad();
					double aimDestX = x+Math.cos(fireAngleRad) * r;
					double aimDestY = y+Math.sin(fireAngleRad) * r;
					
					EnemyBullet eb = new EnemyBullet(Math.toDegrees(tmpEb.getRad()), aimDestX, aimDestY, 20, 10, tmpEb.isLethal(), false);
					GamePanel.enemyBullets.add(eb);
					rexBulletsToRemove.add(entry.getKey());
				
				}
			}	
			
			for(int j=0; j<rexBulletsToRemove.size(); j++) {
				rexBulletStartMap.remove(rexBulletsToRemove.get(j));
				rexBulletElapsedMap.remove(rexBulletsToRemove.get(j));	
			}
			
			
		}
		
		if(skillSet.containsKey("change direction skill")) {
			if(RandomUtils.runChance(skillSet.get("change direction skill"))) {
				changeDirectionRandomly();
			}
		}
		
		if(skillSet.containsKey("change speed skill")) {
			if(RandomUtils.runChance(skillSet.get("change speed skill"))) {
				changeSpeedRandomly(2,9);
			}
		}
		
		if(skillSet.containsKey("pulse skill")) {
			if(r<pulseEnemyMinRadius) {
				pulseEnemyRadiusInc = true;
			} else if(r>pulseEnemyMaxRadius){
				pulseEnemyRadiusInc = false;
			}
			
			if(pulseEnemyRadiusInc) {
				r+=1;
			} else {
				r-=1;
			}
		}
		
		if(skillSet.containsKey("shooting skill")) {
			long shootElapsed = (System.nanoTime() - lastShootTime) / 1000000;
			double shootDelay = skillSet.get("shooting skill");
			if(shootElapsed > (long)shootDelay) {
				lastShootTime = System.nanoTime();
				GamePanel.enemyBullets.add(new EnemyBullet(fireAngle, x, y, 20, 10, true, false));
				fireAngle = Math.random() * 360;
			}
		}
		
		if(skillSet.containsKey("shotgun skill")) {
			long shootElapsed = (System.nanoTime() - lastShootTime) / 1000000;
			double shootDelay = skillSet.get("shotgun skill");
			
			speed = maxSpeed * (1-(double)shootElapsed / shootDelay);
			
			dx = Math.cos(rad) * speed;
			dy = Math.sin(rad) * speed;
			
			if(shootElapsed > (long)shootDelay) {
				lastShootTime = System.nanoTime();
				GamePanel.enemyBullets.add(new EnemyBullet(fireAngle, x, y, 12, 7, false, false));
				GamePanel.enemyBullets.add(new EnemyBullet(fireAngle+25, x, y, 12, 7, false, false));
				GamePanel.enemyBullets.add(new EnemyBullet(fireAngle-25, x, y, 12, 7, false, false));
				
				rad = Math.toRadians(fireAngle) + Math.PI;
				fireAngle = Math.random() * 360;
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
		
		if(skillSet.containsKey("slow field skill")) {
			if(RandomUtils.runChance(skillSet.get("slow field skill"))) {
				GamePanel.slowFields.add(new SlowField(x, y, 50 * Math.random() + 50, 15000));
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
				changeSpeedRandomly(4,8);
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
				changeSpeedRandomly(4,8);
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
			
			if(RandomUtils.runChance(4)){
				changeDirectionRandomly();
			}
			
			if(RandomUtils.runChance(0.7)){
				changeSpeedRandomly(4,8);
			}
			
			if(RandomUtils.runChance(2.0)){
				goTowards(player.getx(), player.gety());
			}
			
			if(RandomUtils.runChance(0.1)){
				int tmpInterval = GamePanel.HEIGHT / 7;
				
				for(int j=0; j<7; j++) {
					if(RandomUtils.runChance(50)) {
						GamePanel.powerups.add(new PowerUp(12, 35 + j*tmpInterval,0));
					}
				}
			}
		}
		
		if(stunned) {
			if(GamePanel.gameState == GamePanel.GameState.PAUSED) {
				stunnedStart = System.nanoTime();
			}
			stunnedElapsed = (System.nanoTime() - stunnedStart)/1000000;
			if(stunnedElapsed > stunnedLength) {
				stunned = false;
			}
		}
		
		if(!stunned) {
			if(slow){
				x+= dx*0.5;
				y+= dy *0.5;
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
		
		if(healHit) {
			long elapsed = (System.nanoTime()-healHitTimer)/1000000;
			if(elapsed > 50){

				healHit = false;
				healHitTimer = 0;
			}
		}
		
		if(regenMode){
			regenOnElapsed = (System.nanoTime() - regenTimer)/1000000;
			if(regenOnElapsed > regenLength){
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
		if(rank>=4 && r > 55){
			g.setColor(new Color(245,222,179));
			g.setStroke(new BasicStroke(3));
			g.setFont(new Font("Gulim",Font.BOLD,16));
			String s = name;
			int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
			g.drawString(s, (int)(x-(length)/2), (int)(y));
			g.setStroke(new BasicStroke(1));
		}
		
		
		if(skillSet.containsKey("rex boss 1 skill")) {			
			//Boss tint depending on mode
			Color rexBossTintColor = new Color(255,255,255,255);
			switch(rexBossMode) {
				case NORMAL : rexBossTintColor = new Color(255,255,255,50); break;
				case FIRING : rexBossTintColor = new Color(255,0,0,50); break;
				case HEALSPAWN : rexBossTintColor = new Color(210,105,30,50); break;
				case CHARGE : rexBossTintColor = new Color(30,144,255,50); break;
				default: rexBossTintColor = new Color(255,255,255,50); break;
			}
			color1 = rexBossTintColor;
			g.setColor(new Color(color1.getRed(), color1.getGreen(), color1.getBlue(), 255));
			//g.fillOval((int)(x-slowFieldRadius),(int)(y-slowFieldRadius), (int)(2*slowFieldRadius), (int)(2*slowFieldRadius));
			
			//Mode progress bar
			int modeBarLength = (int) r;
			double modeProgress = 1 - (double)lastModeChangeElapsed/(double)modeDuration;
			if(modeProgress < 0) {
				modeProgress = 0;
			}			
			g.drawRect((int)(x-modeBarLength/2), (int)(y+modeBarLength * 3/5), modeBarLength, modeBarLength/7);
			g.fillRect((int)(x-modeBarLength/2), (int)(y+modeBarLength * 3/5), (int)(modeBarLength*modeProgress), modeBarLength/7);
			
			//draw slow field around boss			
			if(rexBossMode == RexBossModes.CHARGE) {
				g.setColor(new Color(135,206,250,120));
				g.setStroke(new BasicStroke(2));
				
				g.drawOval((int)(x-slowFieldRadius),(int)(y-slowFieldRadius), (int)(2*slowFieldRadius), (int)(2*slowFieldRadius));
				
				g.setColor(new Color(135,206,250,40));
				g.fillOval((int)(x-slowFieldRadius),(int)(y-slowFieldRadius), (int)(2*slowFieldRadius), (int)(2*slowFieldRadius));
			}
			
			//draw shoot bullet
			for (Map.Entry<Long, EnemyBullet> entry : rexBulletStartMap.entrySet()) {
				long rexBulletElapsed = rexBulletElapsedMap.get(entry.getKey());
				long rexBulletDelay = 4000;
				
				double aimDestX = 0;
				double aimDestY = 0;
				double aimBarLength = (double)r;
				
				double aimChargeDestX = 0;
				double aimChargeDestY = 0;
				double aimChargeBarLength = (double)r * rexBulletElapsed / rexBulletDelay;
				
				double fireAngleRad = entry.getValue().getRad();
				aimDestX = x+Math.cos(fireAngleRad) * aimBarLength;
				aimDestY = y+Math.sin(fireAngleRad) * aimBarLength;
				
				aimChargeDestX = x+Math.cos(fireAngleRad) * aimChargeBarLength;
				aimChargeDestY = y+Math.sin(fireAngleRad) * aimChargeBarLength;
							
				g.setStroke(new BasicStroke(5));
				g.setColor(Color.WHITE);
				g.draw(new Line2D.Double(x, y, aimDestX, aimDestY));
				g.setStroke(new BasicStroke(1));
				
				g.setStroke(new BasicStroke(4));
				if(entry.getValue().isLethal()) {
					g.setColor(Color.RED);
				} else {
					g.setColor(Color.BLACK);
				}
				
				g.draw(new Line2D.Double(x, y, aimChargeDestX, aimChargeDestY));
				
				g.setStroke(new BasicStroke(1));
			}			
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
			g.drawRect((int)(x-stunBarLength/2), (int)(y+stunBarLength/4), stunBarLength, stunBarLength/10);
			g.fillRect((int)(x-stunBarLength/2), (int)(y+stunBarLength/4), (int)(stunBarLength*stunProgress), stunBarLength/10);
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
		
		//Aim bar
		if(skillSet.containsKey("shooting skill")) {
			
			long shootElapsed = (System.nanoTime() - lastShootTime) / 1000000;
			double shootDelay = skillSet.get("shooting skill");
			
			double aimDestX = 0;
			double aimDestY = 0;
			double aimBarLength = (double)r;
			
			double aimChargeDestX = 0;
			double aimChargeDestY = 0;
			double aimChargeBarLength = (double)r * shootElapsed / shootDelay;
			
			double fireAngleRad = Math.toRadians(fireAngle);
			aimDestX = x+Math.cos(fireAngleRad) * aimBarLength;
			aimDestY = y+Math.sin(fireAngleRad) * aimBarLength;
			
			aimChargeDestX = x+Math.cos(fireAngleRad) * aimChargeBarLength;
			aimChargeDestY = y+Math.sin(fireAngleRad) * aimChargeBarLength;
						
			g.setStroke(new BasicStroke(5));
			g.setColor(Color.WHITE);
			g.draw(new Line2D.Double(x, y, aimDestX, aimDestY));
			g.setStroke(new BasicStroke(1));
			
			g.setStroke(new BasicStroke(4));
			g.setColor(Color.RED);
			g.draw(new Line2D.Double(x, y, aimChargeDestX, aimChargeDestY));
			g.setStroke(new BasicStroke(1));

		}
		
		if(skillSet.containsKey("shotgun skill")) {
			long shootElapsed = (System.nanoTime() - lastShootTime) / 1000000;
			double shootDelay = skillSet.get("shotgun skill");
			
			for(int i=0; i<3; i++) {
			
				double aimDestX = 0;
				double aimDestY = 0;
				double aimBarLength = (double)r;
				
				double aimChargeDestX = 0;
				double aimChargeDestY = 0;
				double aimChargeBarLength = (double)r * shootElapsed / shootDelay;
				
				double fireAngleRad = Math.toRadians(fireAngle + (-25) + 25*i);
				aimDestX = x+Math.cos(fireAngleRad) * aimBarLength;
				aimDestY = y+Math.sin(fireAngleRad) * aimBarLength;
				
				aimChargeDestX = x+Math.cos(fireAngleRad) * aimChargeBarLength;
				aimChargeDestY = y+Math.sin(fireAngleRad) * aimChargeBarLength;
				
				g.setStroke(new BasicStroke(5));
				g.setColor(Color.WHITE);
				g.draw(new Line2D.Double(x, y, aimDestX, aimDestY));
				g.setStroke(new BasicStroke(1));
				
				g.setStroke(new BasicStroke(4));
				g.setColor(Color.BLACK);
				g.draw(new Line2D.Double(x, y, aimChargeDestX, aimChargeDestY));
				g.setStroke(new BasicStroke(1));
			}
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
			if(healHit) {
				g.setColor(Color.GREEN);
			}
			g.drawRect((int)(x-healthBarLength/2), (int)(y-healthBarLength/2), healthBarLength, healthBarLength/5);
			g.fillRect((int)(x-healthBarLength/2), (int)(y-healthBarLength/2), (int)(healthBarLength*(double)health/(double)maxHealth), healthBarLength/5);
		}
		
		
		
		
		
	}
	
}
