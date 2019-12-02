package Entities;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Random;

import Audio.AudioPlayer;
import Panels.GamePanel;
import VFX.Explosion;
import VFX.FootPrint;
import VFX.ParticleEffect;
import VFX.Text;

public class Player {

	private int x;
	private int y;
	private int r; // radius
	
	private int dx;
	private int dy;
	private int speed;
	
	private HashMap<String, AudioPlayer> sfx;
	
	private int lives;
	
	private Color color1;
	private Color color2;
	
	//addon colors
	private Color c1 = new Color(255,0,0,164);
	private Color c2 = new Color(0,255,0,164);
	private Color c3 = new Color(0,0,255,164);
	private Color c4 = new Color(255,255,0,164);

	private boolean left;
	private boolean right;
	private boolean up;
	private boolean down;
	
	private boolean spazing;
	private long spazingTimer;
	private long spazingDelay;
	private long spazLength;
	private long spazActualTimer;
	
	private int fixedSpazAngle=0;
	
	private boolean bombing;
	private long bombingTimer;
	private long bombingLength;
	
	private int bombs;
	
	private int addOn;
	private boolean addOnEnable;
	private double addOnShootAngle;
	private long addOnShootTimer;
	private long addOnShootDelay;

	
	//addOnAngles
	private double spinAngle=0;
	private double spinAngle2=180;
	private double spinAngle3=90;
	private double spinAngle4=270;

	
	private boolean firing;
	private long firingTimer;
	private long firingDelay;
	private long maxFiringDelay;
	
	//following missile
	private boolean firingSide;
	private long firingSideTimer;
	private long firingSideDelay;
	private long firingSideActualTimer;
	private long firingSideLength;
	
	private boolean recovering;
	private long recoveryTimer;
	private long recoveryBlinkTimer;
	
	private double pushRadius;
	private boolean isPushing;
	private long pushTimer;
	private long pushLength;
	private long pushDrawTimer;
	private long pushDrawLength;
	
	private long puCollectTimer;
	private long puCollectLength;
	private boolean isCollectingPu;
	private double puCollectRadius;
	
	private double maxStamina;
	private double currentStamina;
	private double staminaRegen;
	private long staminaTimer;
	private long staminaGainDelay;
	
	private int score;
	
	private boolean invincible;
	private long inviTimer;
	private long inviDelay;
	private double inviStaminaCost;
	
	private boolean isSuperSpeed;
	private int superSpeed;
	private long superSpeedLength;
	private long superSpeedStartTime;
	private int speedBeforeSuperSpeed;
	private int superSpeedDamping;
	
	private boolean isRaged;
	private boolean isInRageField;
	private double dmgMultiplier;
	private long rageStartTime;
	private long rageLength;
	private long rageElapsed;
	
	private boolean aoeFreezeAnimRunning;
	private long aoeFreezeStartTime;
	private long aoeFreezeLength;
	private long aoeFreezeElapsed;
	
	public HashMap<String, Long> skillCdMap;
	public HashMap<String, Long> skillLastUsedMap;
	public HashMap<String, Color> skillColorMap;
	
	public String[] skills = {"F - Collect", "W - FreezeAOE", "E - BlackHole", "Q - Push"};
	public long[] skillCds = {15000, 20000, 10000, 5000};	
	public Color[] skillColors = {
		new Color(218,165,32),
		new Color(175,238,238),
		new Color(0,0,0),
		new Color(255,255,255)
	};
	
	private Turret[] ts = new Turret[5];
	private boolean[] tsAvailability = new boolean[5];
	
	private double rad;
	
	private boolean immobalized;
	
	private boolean isStunned;
	private long stunDuration;
	private long stunStartTime;
	private long stunElapsed;
	
	private boolean isSlowed;
	
	private int nWalls;	
	
	private int powerLevel;
	private int power;
	private int[] requiredPower = {
			3,6,9,10,10,12,12
	};
	
	private double dropRateMultiplier;
	private double spawnTimeMultiplier;

	//Damage
	private double bulletDmg;
	private double sideMissileDmg;
	private double turretDmg;
	private double bombDmg;
	private double addonDmg;
	private double friendDmg;
	private double lightningDmg;
	
	private long lightningStunLength;
	

	

	//Constructor
	public Player(){
		color1 = Color.WHITE;
		color2 = Color.RED;
		
		sfx = new HashMap<String, AudioPlayer>();
		sfx.put("laser", new AudioPlayer("/sfx/shoot.wav"));
		sfx.put("side", new AudioPlayer("/sfx/side_missile.wav"));
		
		skillCdMap = new HashMap<String, Long>();
		skillLastUsedMap = new HashMap<String, Long>();
		skillColorMap = new HashMap<String,Color>();

		
		//init skill Last used Map
		for(int i=0; i<skills.length; i++) {
			skillLastUsedMap.put(skills[i], (long) 0);
		}
		
		//init skill cd Map
		for(int i=0; i<skills.length; i++) {
			skillCdMap.put(skills[i], skillCds[i]);
		}
		
		//init skill color Map
		for(int i=0; i<skills.length; i++) {
			skillColorMap.put(skills[i], skillColors[i]);
		}
		
		init();
	}
	
	public void init() {
		x = GamePanel.WIDTH/2;
		y = GamePanel.HEIGHT/2;
		r = 8;
		
		//turret update
		for(int i=0; i<tsAvailability.length; i++) {
			tsAvailability[i] = true;
		}
		
		for(int i=0; i<ts.length; i++) {
			ts[i] = null;
		}
		
		dropRateMultiplier = 1.0;
		spawnTimeMultiplier = 1.0;
		bulletDmg = 1.0;
		bombDmg = 1.0;
		sideMissileDmg = 1.0;
		turretDmg = 1.0;
		addonDmg = 1.0;
		friendDmg = 1.0;
		lightningDmg = 7.0;
		
		isRaged = false;
		isInRageField = false;
		rageStartTime = 0;
		rageLength = 7200;
		rageElapsed = 0;
		dmgMultiplier = 1.0;
		
		aoeFreezeStartTime = 0;
		aoeFreezeLength = 420;
		aoeFreezeAnimRunning = false;
		
		lightningStunLength = 2200;
		
		powerLevel = 0;
		power = 0;
		
		dx = 0;
		dy= 0;
		speed = 4;
		 
		lives = 3;
		
		firing = false;
		firingTimer = System.nanoTime();
		firingDelay = 250;
		maxFiringDelay = 140;
		
		firingSide = false;
		firingSideTimer = System.nanoTime();
		firingSideDelay = 300;
		firingSideLength = 3000;
		firingSideActualTimer = System.nanoTime();
		
		recovering = false;
		recoveryTimer = 0;
		recoveryBlinkTimer = 0;
		
		spazing = false;
		spazingTimer = 0;
		spazingDelay = 7;
		spazLength = 700;
		spazActualTimer = 0;
		
		isCollectingPu = false;
		puCollectTimer = 0;
		puCollectLength = 3000;
		puCollectRadius = 200;
		
		isSuperSpeed = false;
		superSpeedStartTime = 0;
		superSpeedLength = 180;
		superSpeed = 22;
		superSpeedDamping = 1;
		
		bombing = false;
		bombingTimer = System.nanoTime();
		bombingLength = 700;
		
		bombs =1;
		
		addOn = 0;
		addOnShootTimer = System.nanoTime();
		addOnShootAngle = 270;
		addOnShootDelay = 370;
		
		addOnEnable = true;
		
		pushRadius = 200;
		isPushing = false;
		pushTimer = System.nanoTime();
		pushLength = 1000;
		pushDrawLength = 300;
		
		maxStamina = 1200;
		currentStamina = 1200;
		staminaRegen = 8.7;
		staminaTimer = System.nanoTime();
		staminaGainDelay = 50;
		
		inviTimer = System.nanoTime();
		inviDelay = 50;
		inviStaminaCost = 30;
		
		invincible = false;
		immobalized = false;
		
		stunDuration = 3000;
		stunElapsed = System.nanoTime();
		stunStartTime = 0;
		
		isSlowed = false;
		
		nWalls = 1;
		
		score = 1000;
	}
	
	public void useSkillWithCd(String skill) {
		if(skillLastUsedMap.containsKey(skill)) {
			skillLastUsedMap.put(skill, System.nanoTime());
		}		
	}
	
	public long skillTimeRemaining(String skill) {
		if(skillCdMap.containsKey(skill)) {
			long elapsed = (System.nanoTime() - skillLastUsedMap.get(skill))/1000000;
			long timeRemaining = skillCdMap.get(skill) - elapsed;
			if(timeRemaining<0) {
				timeRemaining = 0;
			}
			return timeRemaining;
		}		
		return 0;
	}
	
	//return time elapsed since skill used
	public HashMap<String, Long> getCdSkills(){
		HashMap<String,Long> skillsOnCd = new HashMap<String,Long>();
		for(int i=0; i<skills.length; i++) {
			if(skillTimeRemaining(skills[i]) > 0) {
				long elapsed = (System.nanoTime() - skillLastUsedMap.get(skills[i])) / 1000000;
				skillsOnCd.put(skills[i], elapsed);
			}
		}
		
		return skillsOnCd;		
	}
	
	public double getSpawnTimeMultiplier() {
		return spawnTimeMultiplier;
	}

	public void setSpawnTimeMultiplier(double spawnTimeMultiplier) {
		this.spawnTimeMultiplier = spawnTimeMultiplier;
	}
	
	public long getLightningStunLength() {
		return lightningStunLength;
	}

	public void setLightningStunLength(long lightningStunLength) {
		this.lightningStunLength = lightningStunLength;
	}

	public double getLightningDmg() {
		return lightningDmg;
	}

	public void setLightningDmg(double lightningDmg) {
		this.lightningDmg = lightningDmg;
	}

	public double getFriendDmg() {
		return friendDmg;
	}

	public void setFriendDmg(double friendDmg) {
		this.friendDmg = friendDmg;
	}

	public double getAddonDmg() {
		return addonDmg;
	}

	public void setAddonDmg(double addonDmg) {
		this.addonDmg = addonDmg;
	}

	public double getSideMissileDmg() {
		return sideMissileDmg;
	}

	public void setSideMissileDmg(double sideMissileDmg) {
		this.sideMissileDmg = sideMissileDmg;
	}

	public double getTurretDmg() {
		return turretDmg;
	}

	public void setTurretDmg(double turretDmg) {
		this.turretDmg = turretDmg;
	}
	
	public double getBombDmg() {
		return bombDmg;
	}

	public void setBombDmg(double bombDmg) {
		this.bombDmg = bombDmg;
	}

	public double getbulletDmg() {
		return bulletDmg;
	}

	public void setbulletDmg(double bulletDmg) {
		this.bulletDmg = bulletDmg;
	}
	
	public long getSpazDuration() {
		return spazLength;
	}
	
	public long getSideMissileDuration() {
		return firingSideLength; 
	}
	
	public boolean isCollectingPu() {
		return isCollectingPu;
	}

	
	//setters
	public void setLeft(boolean b){ left = b;}
	public void setRight(boolean b){ right = b;}
	public void setUp(boolean b){ up = b;}
	public void setDown(boolean b){ down = b;}
	public void setFiring(boolean b){ firing = b;}
	public void setSpazing(boolean b){spazing = b;}
	public boolean isRecovering(){ return recovering;}
	public int getScore() { return score; }
	public void setScore(int score) {this.score = score;  }
	public boolean isPushing() { return isPushing; }
	public double getPushRadius(){ return pushRadius; }
	public double getCollectRadius() { return puCollectRadius; }
	public void startInvincible(){ invincible = true; inviTimer = System.nanoTime();}
	public void stopInvincible(){ invincible = false; }
	public void setImmobalized(boolean b){ immobalized = b; }
	
	public boolean isImmobalized() {return immobalized;}
	
	public void stun(long stunDuration) {
		if(!invincible && !recovering) {
			this.stunDuration = stunDuration;
			this.stunStartTime = System.nanoTime();
			isStunned = true;
		}		
	}
	
	public void slowOn() {
		isSlowed = true;
	}
	
	public void slowOff() {
		isSlowed = false;
	}
	
	public boolean isSuperSpeed() {
		return isSuperSpeed;
	}
	
	public void startSuperSpeed() {
		
		//particle effect
		GamePanel.particleEffects.add(new ParticleEffect(x, y, 2, 15));
		
		superSpeedStartTime = System.nanoTime();
		isSuperSpeed = true;
		speedBeforeSuperSpeed = speed;
		speed = superSpeed;
	}
	
	public void stopSuperSpeed() {
		isSuperSpeed = false;
		speed = speedBeforeSuperSpeed;
	}
	
	public void startRage() {
		rageStartTime = System.nanoTime();
		isRaged = true;
		dmgMultiplier = 2.0;
	}
	
	public void stopRage() {
		isRaged = false;
		dmgMultiplier = 1.0;
	}
	
	public void enterRageField() {
		isInRageField = true;
		dmgMultiplier = 1.2;
	}
	
	public void leaveRageField() {
		isInRageField = false;
		dmgMultiplier = 1.0;
	}
	
	
//	private double bulletDmg;
//	private double sideMissileDmg;
//	private double turretDmg;
//	private double bombDmg;
//	private double addonDmg;
	public void upgradeAbilities() {
		//randomly upgrades abilities
		
		sideMissileDmg += Math.random()/10 + 0.05;
		turretDmg += Math.random()/10 + 0.05;
		bombDmg += Math.random()/12 + 0.03;
		addonDmg += Math.random()/15 + 0.03;
		spazLength += Math.random() * 20 + 50;
		firingSideLength += Math.random() * 20 + 40;
		friendDmg += Math.random()/10 + 0.05;
		lightningDmg += Math.random()/5;
		lightningStunLength += Math.random() * 30 + 50;
	}
	
	public boolean attemptPurchase(int cost) {
		if(GamePanel.gameState != GamePanel.GameState.PLAY) {
			return false;
		}
		if(score >= cost) {
			score -= cost;
			return true;
		}
		return false;
	}
	
	public void incSpawnRate(double amount) {
		spawnTimeMultiplier -= amount;
	}
	
	public void incDropRate(double amount) {
		dropRateMultiplier += amount;
	}
	
	public double getDropRateMultiplier() {
		return dropRateMultiplier;
	}
	
	public void toggleInvincible(){
		if(invincible){
			stopInvincible();
		} else {
			startInvincible();
		}
	}
	
	public double getAttSpeed(){
		return ((double)maxFiringDelay / firingDelay) * 100;
	}
	
	public boolean isInvincible() { return invincible; }
	
	public void gainAddOn(){ addOn++;	
		if(addOn==3){
			spinAngle=0;
			spinAngle2 = 120;
			spinAngle3 = 240;
		}
		if(addOn==4){
			spinAngle=0;
			spinAngle2=180;
			spinAngle3=90;
			spinAngle4=270;
		}	
	}
	
	public void freezeAOE(long duration) {
		aoeFreezeAnimRunning = true;
		aoeFreezeStartTime = System.nanoTime();
		for(int i=0; i<GamePanel.enemies.size(); i++){
			if(GamePanel.enemies.get(i).isInRange(getx(), gety(), getPushRadius()))
				GamePanel.enemies.get(i).stun(duration);
		}
	}
	
	public void moveToward(double px, double py){
		double xDiff = px-x;
		double yDiff = py-y;
		
			//double angle = Math.atan((y-py)/(x-px));
			if(xDiff<0)
				rad = Math.atan((yDiff)/(xDiff))+Math.toRadians(180);
			else if(yDiff<0)
				rad = Math.atan((yDiff)/(xDiff))+Math.toRadians(360);
			else
				rad = Math.atan((yDiff)/(xDiff));			
			
			dx = (int) (Math.cos(rad) * 4);
			dy = (int) (Math.sin(rad) * 4);
	}
	
	public void moveAwayFrom(double px, double py){
		double xDiff = px-x;
		double yDiff = py-y;
		
			//double angle = Math.atan((y-py)/(x-px));
			if(xDiff<0)
				rad = Math.atan((yDiff)/(xDiff))+Math.toRadians(180);
			else if(yDiff<0)
				rad = Math.atan((yDiff)/(xDiff))+Math.toRadians(360);
			else
				rad = Math.atan((yDiff)/(xDiff));			
			
			dx = (int) (-Math.cos(rad) * speed);
			dy = (int) (-Math.sin(rad) * speed);
			
			x += dx;
			y += dy;
	}
	
	public void changeDirectionRandomly(){
		double angle = Math.random() * 360;
		rad = Math.toRadians(angle);
		
		dx = (int) (Math.cos(rad) * speed);
		dy = (int) (Math.sin(rad) * speed);
	}
	
	public void startPushing() {
		isPushing = true;
		pushTimer = System.nanoTime();
	}
	
	public void startCollecting() {
		isCollectingPu = true;
		puCollectTimer = System.nanoTime();
	}
	
	public void placeBlackHole(){
		GamePanel.blackholes.add(new BlackHole(x,y,5));
	}
	
	
	
	public void incFireRate(){ firingDelay -= 15; if(firingDelay<maxFiringDelay){firingDelay=maxFiringDelay;}}
	public long getFiringDelay() { return firingDelay; }
	
	public void incBombs(){bombs++;}
	public int getBombs(){return bombs;}
	
	public void addScore(int i) { score+=i; };
	
	public void loseLife(){
		if(!invincible && !recovering){
			lives--;
			recovering = true;
			isStunned = false;
			recoveryTimer = System.nanoTime();
		}
	}
	
	public void startBombing(){
		if(bombs>0){
			bombs--;
			bombing = true;
			bombingTimer = System.nanoTime();
		}
	}
	
	public void startSpazing(){
		spazing = true;
		spazActualTimer = System.nanoTime();
	}
	
	public void startFiringSide(){
		firingSide = true;
		firingSideTimer = System.nanoTime();
		firingSideActualTimer = System.nanoTime();
	}
	
	public void increasePower(int i){
		power += i;
		
		if(power > getRequiredPower()) {
			power -= getRequiredPower();
			powerLevel++;
			
			if(powerLevel>=requiredPower.length-1) {
				double incDmg = Math.random()/10 + 0.05;
				double displayIncDmg = (double) Math.round(incDmg * 100) / 100;
				GamePanel.texts.add(new Text(getx(), gety(),2000,"Dmg inc by " + displayIncDmg));
				incBulletDmg(incDmg);
			}
		}
	}
	
	public void incBulletDmg(double inc) {
		bulletDmg += inc;
	}
	
	public int getPowerLevel() {return powerLevel;}
	public int getPower() { return power; }
	public int getRequiredPower() { 
		if(powerLevel>=requiredPower.length-1){
			return requiredPower[requiredPower.length-1];
		}		
		
		return requiredPower[powerLevel];
		
	}
	
	public void toggleAddOn() { if(addOnEnable){addOnEnable=false;}else{addOnEnable=true;} }
	
	public void incSpeed(){ speed++; if(speed>7) speed=7;}
	public int getSpeed(){return speed;}
	
	public void setBombing(boolean b){bombing = b; if(b==true){bombingTimer=System.nanoTime();}}
	public boolean isBombing(){return bombing;}
	
	public double getMaxStamina() { return maxStamina; } 
	public double getCurrentStamina() { return currentStamina; }

	public boolean useStamina(double s) {
		if (s > currentStamina) {
			GamePanel.alertStaminaLow();
			GamePanel.texts.add(new Text(x, y, 200, ""+s+" stamina needed!",true,new Color(255,0,0,255)));
			return false;
		} else {
			GamePanel.texts.add(new Text(x, y, 200, "-"+s+" stamina",true,new Color(255,0,0,255)));
			currentStamina -= s;
			return true;
		}
	}

	public void gainStamina(double s) {
		if ((s + currentStamina) > maxStamina) {
			currentStamina = maxStamina;
		} else {
			currentStamina += s;
		}
	}
	
	public void increaseMaxStamina(double s){
		maxStamina += s;
	}

	public boolean updateTurretAvailability(){
		for(int i=0; i<ts.length; i++){
			if(ts[i]==null){
				tsAvailability[i] = true;
			} else if(ts[i].isDead()){
				tsAvailability[i] = true;
			} else {
				tsAvailability[i] = false;
			}
		}
		for(int i=0; i<ts.length; i++){
			if(ts[i]==null){
				return true;
			} else if(ts[i].isDead()){
				return true;
			}
		}
		return false;
	}
	
	public int getTurretAvailabilityIndex(){
		for(int i=0; i<ts.length; i++){
			if(tsAvailability[i] == true){
				return i;
			}
		}
		return 0;
	}
	public void placeTurret(){
		int i =0;
		if(updateTurretAvailability()){
			i = getTurretAvailabilityIndex();
			Turret t = new Turret(x,y,i);
			GamePanel.turrets.add(t);
			ts[i] = t;
			tsAvailability[i] = false;
		}	
	}
	
	public void placeBomb(){
		if(bombs>0){
			bombs--;
			GamePanel.bombs.add(new Bomb(x,y));
		}
		
	}
	
	public void placeShelter(){
		if(nWalls>0){
			nWalls--;
			GamePanel.shelters.add(new Shelter(x,y,100));
		}
		
	}
	
	public void gainWalls(int gain){
		nWalls += gain;
	}
	
	public int getShelterCount(){
		return nWalls;
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
	
	
	public void tpToTurret(int i){ // removal of turret happens in gamePanel
		updateTurretAvailability();
		if(tsAvailability[i] == false){
			Turret t = ts[i];
			x = (int) t.getx();
			y = (int) t.gety();
			t.setDead(true);			
			tsAvailability[i] = true;		
			GamePanel.explosions.add(new Explosion(x,y,r,r+20,1));
			GamePanel.explosions.add(new Explosion(x,y,r,r+50,2));
			GamePanel.explosions.add(new Explosion(x,y,r,r+70,3));
		}
		
	}
	
	public void updateEnemyAngle(double ex, double ey){
		double xDiff = ex - x;
		double yDiff = ey - y;
		
		if(xDiff <0)
			addOnShootAngle =  Math.toDegrees(Math.atan((y-ey)/(x-ex)))+180;
		else if(yDiff <0)
			addOnShootAngle =  Math.toDegrees(Math.atan((y-ey)/(x-ex)))+360;
		else 
			addOnShootAngle =  Math.toDegrees(Math.atan((y-ey)/(x-ex)));
	}
	
	public void upgradeMissileDuration() {
		firingSideLength += 300;
	}
	
	public void upgradeSpazDuration() {
		spazLength += 300;
	}
	
	public void pauseUpdate() {
		if(isStunned) {
			stunStartTime = System.nanoTime() - stunElapsed * 1000000;			
		}
	}
	
	public void update(){
		if(!immobalized && !isStunned){
			if(left){
				dx = -speed;
			}
			if(right){
				dx = speed;
			}
			if(up){
				dy = -speed;
			}
			if(down){
				dy = speed;
			}
		}
		
		if(isStunned) {
			stunElapsed = (System.nanoTime() - stunStartTime) / 1000000;
			if(stunElapsed > stunDuration) {
				isStunned = false;
			}
		}
		
		if(!isSlowed) {
			x+= dx;
			y += dy;
		} else {
			x+= (int)(dx / 2);
			y += (int)(dy / 2);
		}
		
		
		if(x<0) x=GamePanel.WIDTH;
		if(y<0) y=GamePanel.HEIGHT;
		if(x>GamePanel.WIDTH) x = 0;
		if(y>GamePanel.HEIGHT) y = 0;
		
		dx=0;
		dy=0;
		
		//invincible
		if(invincible){
			long inviElapsed = (System.nanoTime() - inviTimer)/1000000;
			if(inviElapsed > inviDelay){
				inviTimer = System.nanoTime();
				if(useStamina(inviStaminaCost)){
					invincible = true;
				} else {
					invincible = false;
					
				}
			}
		}
		
		//dash
		if(isSuperSpeed) {
			long superSpeedElapsed = (System.nanoTime() - superSpeedStartTime) / 1000000;
			
			GamePanel.footPrints.add(new FootPrint(x,y,r,color1));
			
			if(superSpeedElapsed > superSpeedLength) {
				stopSuperSpeed();
			} else if(speed > speedBeforeSuperSpeed +1) {
				speed -= superSpeedDamping;
			}
		}
		
		//rage
		if(isRaged) {
			rageElapsed = (System.nanoTime() - rageStartTime) / 1000000;
			
			if(rageElapsed > rageLength) {
				stopRage();
			}
		}
		
		//aoe freeze anim
		if(aoeFreezeAnimRunning) {
			aoeFreezeElapsed = (System.nanoTime() - aoeFreezeStartTime) / 1000000;
			
			if(aoeFreezeElapsed > aoeFreezeLength) {
				aoeFreezeAnimRunning = false;
			}
		}
		
		//stamina
		long staminaElapsed = (System.nanoTime() - staminaTimer)/1000000;
		if(staminaElapsed > staminaGainDelay){
			staminaTimer = System.nanoTime();
			double futureStamina = currentStamina + staminaRegen;
			if(futureStamina > maxStamina){
				currentStamina = maxStamina;
			} else {
				currentStamina += staminaRegen;
			}
		}
		
		//side missiles
		if(firingSide){
			long elapsedActual = (System.nanoTime() - firingSideActualTimer)/1000000;
			if(elapsedActual > firingSideLength){
				firingSide = false;
				firingSideActualTimer = 0;
			}
			long elapsed = (System.nanoTime()-firingSideTimer)/1000000;
			if(elapsed > firingSideDelay){
				sfx.get("side").play();
				firingSideTimer = System.nanoTime();
				Random rn = new Random();
				int cR = (int) (rn.nextDouble()*100);
				int cG = (int) (rn.nextDouble()*100);
				int cB = (int) (rn.nextDouble()*255);
				GamePanel.bullets.add(new Bullet("left",x-r/2,y+r/2,5,Color.cyan, sideMissileDmg));
				cR = (int) (rn.nextDouble()*100);
				cG = (int) (rn.nextDouble()*255);
				cB = (int) (rn.nextDouble()*255);
				GamePanel.bullets.add(new Bullet("right",x+2*r,y+r/2,5,new Color(cR,cR,cR,255), sideMissileDmg));
				cR = (int) (rn.nextDouble()*255);
				cG = (int) (rn.nextDouble()*100);
				cB = (int) (rn.nextDouble()*100);
				GamePanel.bullets.add(new Bullet("topLeft",x-r/2,y+r/2,5,new Color(cR,cR,cR,255), sideMissileDmg));
				cR = (int) (rn.nextDouble()*255);
				cG = (int) (rn.nextDouble()*100);
				cB = (int) (rn.nextDouble()*100);
				GamePanel.bullets.add(new Bullet("topRight",x+2*r,y+r/2,5,new Color(cR,cR,cR,255), sideMissileDmg));
				
				//rocket from turrets if supercharged
				for(int k=0; k< GamePanel.turrets.size(); k++) {
					if(GamePanel.turrets.get(k).isSuperCharged()) {
						double turretX = GamePanel.turrets.get(k).getx();
						double turretY = GamePanel.turrets.get(k).gety();
						GamePanel.bullets.add(new Bullet("topRight",(int)(turretX+2*r),(int)(turretY+r/2),5,new Color(cR,cR,cR,255), sideMissileDmg));
					}					
				}				
			}
			
		}
		
		//pushing
		if(isPushing){
			long elapsed = (System.nanoTime() - pushTimer)/1000000;
			if(elapsed > pushLength){
				isPushing = false;
				pushTimer = System.nanoTime();
			}
		}
		
	
		
		//firing
		if(firing){
			long elapsed = (System.nanoTime() - firingTimer) / 1000000;
			if(elapsed > firingDelay){
				//sfx.get("laser").stop();
				sfx.get("laser").play();
				firingTimer = System.nanoTime();
				
				if(powerLevel < 1){
					GamePanel.bullets.add(new Bullet(270, x,y, bulletDmg * dmgMultiplier, isRaged));
				}
				else if(powerLevel < 2){
					GamePanel.bullets.add(new Bullet(270, x+5,y, bulletDmg* dmgMultiplier, isRaged));
					GamePanel.bullets.add(new Bullet(270, x-5,y, bulletDmg* dmgMultiplier, isRaged));
					
				} else if(powerLevel < 3) {
					GamePanel.bullets.add(new Bullet(270, x+0,y, bulletDmg* dmgMultiplier, isRaged));
					GamePanel.bullets.add(new Bullet(265, x-5,y, bulletDmg* dmgMultiplier, isRaged));
					GamePanel.bullets.add(new Bullet(275, x+5,y, bulletDmg* dmgMultiplier, isRaged));
					
				} else if(powerLevel < 4) {
					GamePanel.bullets.add(new Bullet(270, x+0,y, bulletDmg* dmgMultiplier, isRaged));
					GamePanel.bullets.add(new Bullet(265, x-5,y, bulletDmg* dmgMultiplier, isRaged));
					GamePanel.bullets.add(new Bullet(275, x+5,y, bulletDmg* dmgMultiplier, isRaged));
					GamePanel.bullets.add(new Bullet(257, x-8,y, bulletDmg* dmgMultiplier, isRaged));
					GamePanel.bullets.add(new Bullet(283, x+8,y, bulletDmg* dmgMultiplier, isRaged));
					
				} else if(powerLevel < 5){
					GamePanel.bullets.add(new Bullet(270, x+0,y, bulletDmg* dmgMultiplier, isRaged));
					GamePanel.bullets.add(new Bullet(265, x-5,y, bulletDmg* dmgMultiplier, isRaged));
					GamePanel.bullets.add(new Bullet(275, x+5,y, bulletDmg* dmgMultiplier, isRaged));
					GamePanel.bullets.add(new Bullet(257, x-8,y, bulletDmg* dmgMultiplier, isRaged));
					GamePanel.bullets.add(new Bullet(283, x+8,y, bulletDmg* dmgMultiplier, isRaged));
					//
					GamePanel.bullets.add(new Bullet(200, x-10, y, bulletDmg* dmgMultiplier, isRaged));
					GamePanel.bullets.add(new Bullet(340, x+10, y, bulletDmg* dmgMultiplier, isRaged));
					GamePanel.bullets.add(new Bullet(90, x, y, bulletDmg* dmgMultiplier, isRaged));
					
				} else if(powerLevel < 6){
					GamePanel.bullets.add(new Bullet(270, x+0,y, bulletDmg* dmgMultiplier, isRaged));
					GamePanel.bullets.add(new Bullet(265, x-5,y, bulletDmg* dmgMultiplier, isRaged));
					GamePanel.bullets.add(new Bullet(275, x+5,y, bulletDmg* dmgMultiplier, isRaged));
					GamePanel.bullets.add(new Bullet(257, x-8,y, bulletDmg* dmgMultiplier, isRaged));
					GamePanel.bullets.add(new Bullet(283, x+8,y, bulletDmg* dmgMultiplier, isRaged));
					
					GamePanel.bullets.add(new Bullet(200, x-10, y, bulletDmg* dmgMultiplier, isRaged));
					GamePanel.bullets.add(new Bullet(340, x+10, y, bulletDmg* dmgMultiplier, isRaged));
					
					GamePanel.bullets.add(new Bullet(90, x, y, bulletDmg* dmgMultiplier, isRaged));
					GamePanel.bullets.add(new Bullet(80, x+7, y, bulletDmg* dmgMultiplier, isRaged));
					GamePanel.bullets.add(new Bullet(100, x-7, y, bulletDmg* dmgMultiplier, isRaged));
					
				} else {
					GamePanel.bullets.add(new Bullet(270, x+0,y, bulletDmg* dmgMultiplier, isRaged));
					GamePanel.bullets.add(new Bullet(265, x-5,y, bulletDmg* dmgMultiplier, isRaged));
					GamePanel.bullets.add(new Bullet(275, x+5,y, bulletDmg* dmgMultiplier, isRaged));
					GamePanel.bullets.add(new Bullet(257, x-8,y, bulletDmg* dmgMultiplier, isRaged));
					GamePanel.bullets.add(new Bullet(283, x+8,y, bulletDmg* dmgMultiplier, isRaged));
					
					GamePanel.bullets.add(new Bullet(200, x-10, y, bulletDmg* dmgMultiplier, isRaged));
					GamePanel.bullets.add(new Bullet(340, x+10, y, bulletDmg* dmgMultiplier, isRaged));
					
					GamePanel.bullets.add(new Bullet(160, x-10, y, bulletDmg* dmgMultiplier, isRaged));
					GamePanel.bullets.add(new Bullet(20, x+10, y, bulletDmg* dmgMultiplier, isRaged));
					
					GamePanel.bullets.add(new Bullet(90, x, y, bulletDmg* dmgMultiplier, isRaged));
					GamePanel.bullets.add(new Bullet(80, x+7, y, bulletDmg* dmgMultiplier, isRaged));
					GamePanel.bullets.add(new Bullet(100, x-7, y, bulletDmg* dmgMultiplier, isRaged));
					
				}
			}
			
			
		}
		
		
		if(addOn>0 && addOnEnable){
			double radA = Math.toRadians(spinAngle);
			double radB = Math.toRadians(spinAngle2);
			double radC = Math.toRadians(spinAngle3);
			double radD = Math.toRadians(spinAngle4);
			
			//timer
			long elapsed = (System.nanoTime() - addOnShootTimer)/1000000;
			
			if(elapsed>addOnShootDelay){
				addOnShootTimer = System.nanoTime();
				
					GamePanel.bullets.add(new Bullet(addOnShootAngle, (int)(x-r/2+5*r*Math.cos(radA)),(int)(y-r/2+5*r*Math.sin(radA)),4,c1, addonDmg));
				if(addOn>1)
					GamePanel.bullets.add(new Bullet(addOnShootAngle, (int)(x-r/2+5*r*Math.cos(radB)),(int)(y-r/2+5*r*Math.sin(radB)),4,c2, addonDmg));
				if(addOn>2)
					GamePanel.bullets.add(new Bullet(addOnShootAngle, (int)(x-r/2+5*r*Math.cos(radC)),(int)(y-r/2+5*r*Math.sin(radC)),4,c3, addonDmg));
				if(addOn>3)
					GamePanel.bullets.add(new Bullet(addOnShootAngle, (int)(x-r/2+5*r*Math.cos(radD)),(int)(y-r/2+5*r*Math.sin(radD)),4,c4, addonDmg));
				if(addOn>4)
					GamePanel.bullets.add(new Bullet(addOnShootAngle, (int)(x-r/2+10*r*Math.cos(radA)),(int)(y-r/2+10*r*Math.sin(radA)),6,c1, addonDmg));
				if(addOn>5)
					GamePanel.bullets.add(new Bullet(addOnShootAngle, (int)(x-r/2+10*r*Math.cos(radB)),(int)(y-r/2+10*r*Math.sin(radB)),6,c2, addonDmg));
				if(addOn>6)
					GamePanel.bullets.add(new Bullet(addOnShootAngle, (int)(x-r/2+10*r*Math.cos(radC)),(int)(y-r/2+10*r*Math.sin(radC)),6,c3, addonDmg));
				if(addOn>7)
					GamePanel.bullets.add(new Bullet(addOnShootAngle, (int)(x-r/2+10*r*Math.cos(radD)),(int)(y-r/2+10*r*Math.sin(radD)),6,c4, addonDmg));
				
				
				
			}
			addOnShootAngle = Math.random()*360;
			int rotateSpeed = 7;
			if(addOn>4){
				rotateSpeed = 10;
			}
			spinAngle2+=rotateSpeed;
			spinAngle+=rotateSpeed;
			spinAngle3+=rotateSpeed;
			spinAngle4+=rotateSpeed;
			if(spinAngle>360){spinAngle=0;}
			if(spinAngle2>360){spinAngle2=0;}
			if(spinAngle3>360){spinAngle3=0;}
			if(spinAngle4>360){spinAngle4=0;}
			
		}
		
		if(isCollectingPu) {
			long puElapsed = (System.nanoTime() - puCollectTimer)/ 1000000;
			if(puElapsed > puCollectLength) {
				isCollectingPu = false;
				puCollectTimer = System.nanoTime();
			}
		}
		
		if(spazing){
			
			long actualElapsed = (System.nanoTime() - spazActualTimer)/1000000;
				if(actualElapsed>spazLength){
					spazing= false;
					spazActualTimer = System.nanoTime();
				}
				
				//for(int i=0; i<47; i++){
					long sElapsed = (System.nanoTime() - spazingTimer)/1000000;
					if(sElapsed>spazingDelay){
						spazingTimer = System.nanoTime();
						
						
						fixedSpazAngle+=12;
						int secondAngle = fixedSpazAngle +180;
						int thirdAngle = fixedSpazAngle + 90;
						int fourthAngle = fixedSpazAngle + 270;
						
						if(thirdAngle>360){
							thirdAngle = fixedSpazAngle - 270;
						}
						if(fourthAngle>360){
							fourthAngle = fixedSpazAngle - 90;
						}
						
						if(secondAngle>360){
							secondAngle = fixedSpazAngle - 180;
						}
						if(fixedSpazAngle>360){
							fixedSpazAngle = 1;
						}
						GamePanel.bullets.add(new Bullet(fixedSpazAngle, x, y, bulletDmg, new Color(137, 207,240)));
						GamePanel.bullets.add(new Bullet(secondAngle, x, y, bulletDmg, new Color(137, 207,240)));
						GamePanel.bullets.add(new Bullet(thirdAngle, x, y, bulletDmg, new Color(137, 207,240)));
						GamePanel.bullets.add(new Bullet(fourthAngle, x, y, bulletDmg, new Color(137, 207,240)));
						
						//spaz at turrets
						for(int k=0; k< GamePanel.turrets.size(); k++) {
							if(GamePanel.turrets.get(k).isSuperCharged()) {
								GamePanel.bullets.add(
										new Bullet(fixedSpazAngle, 
												(int) GamePanel.turrets.get(k).getx(), 
												(int) GamePanel.turrets.get(k).gety(), 
												bulletDmg,
												new Color(137, 207,240)));
							}
						}
						
					}
				
				//}
		}
		
		if(bombing){
			long bElapsed = (System.nanoTime()-bombingTimer)/1000000;
			if(bElapsed>bombingLength){
				bombingTimer = 0;
				bombing = false;
			}		
		}
		
	
		
		if(recovering){
			long elapsed = (System.nanoTime() - recoveryTimer)/1000000;
			if(elapsed > 2000){
				recovering = false;
				recoveryTimer = 0;
			}
		}
	}
	
	public void draw(Graphics2D g){
		long rElapsed = (System.nanoTime() - recoveryTimer)/1000000;
		long elapsed = (System.nanoTime() - recoveryBlinkTimer)/1000000;
		
		if(recovering && elapsed >77){
			g.setColor(color2);
			g.fillOval(x-r,y-r,2*r,2*r);
			
			g.setStroke(new BasicStroke(3));
			g.setColor(color2.darker());
			g.drawOval(x-r, y-r, r*2, r*2);
			g.setStroke(new BasicStroke(1));
			recoveryBlinkTimer = System.nanoTime();
			
		} else {		
			g.setColor(color1);
			g.fillOval(x-r,y-r,2*r,2*r);
			
			g.setStroke(new BasicStroke(3));
			g.setColor(color1.darker());
			g.drawOval(x-r, y-r, r*2, r*2);
			g.setStroke(new BasicStroke(1));
		}
		
		if(isStunned) {
			double stunAlpha = 200 * (1-stunElapsed / stunDuration);
			if(stunAlpha < 1) {
				stunAlpha = 1;
			}
			if(stunAlpha > 200) {
				stunAlpha = 200;
			}
			
			g.setColor(new Color(12,23,56, (int)stunAlpha));
			g.fillOval(x-(int)(r*1.5),y-(int)(r*1.5),2*(int)(r*1.5),2*(int)(r*1.5));
			
			g.drawRect((int)(x+ 1.7*r), (int)(y - 1.2*r), 8, 20);
			g.fillRect((int)(x+ 1.7*r), (int)(y - 1.2*r + 20 * stunElapsed/stunDuration), 8, (int)(20 - 20 * stunElapsed/stunDuration));
		}
		
		if(recovering){
			g.setFont(new Font("Comic Sans MS",Font.BOLD,25));
			g.drawRect((int)(x-2*r), (int)(y+2*r), (int)(4*r), (int)(r));
			g.fillRect((int)(x-2*r), (int)(y+2*r), (int)(4*r*((double)rElapsed/2000)), (int)(r));
			String s = "recovering";
			int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
			g.drawString(s, (int)(x-length/2), (int)(y-2*r));
		}
		
		if(invincible){
			g.setColor(Color.CYAN);
			g.fillOval((int)(x-r), (int)(y-r), 2*r, 2*r);
		}
		
		if(isRaged || isInRageField) {
			g.setStroke(new BasicStroke(3));
			g.setColor(new Color(255,0,255,255));//purple
			g.fillOval((int)(x-r), (int)(y-r), 2*r, 2*r);
			
			g.setColor(Color.red); 
			g.drawOval((int)(x-r), (int)(y-r), 2*r, 2*r);
			g.setStroke(new BasicStroke(1));
		}
		
		if(isRaged) {			
			g.drawRect((int)(x-2*r), (int)(y-2*r), (int)(4*r), (int)(r));
			g.fillRect((int)(x-2*r), (int)(y-2*r), (int)(4*r*((double)rageElapsed/rageLength)), (int)(r));
		}
		
		if(aoeFreezeAnimRunning) {
			int freezeAnimAlpha = (int) (255 - 255 * aoeFreezeElapsed / aoeFreezeLength);
			if(freezeAnimAlpha < 0) {
				freezeAnimAlpha = 0;
			}
			g.setColor(new Color(220,243,255,freezeAnimAlpha));
			g.fillOval((int)(x-pushRadius), (int)(y-pushRadius), (int)(2*pushRadius), (int)(2*pushRadius));
		}
		
		if(!isPushing){
			g.setColor(new Color(255,255,255,32));
			g.drawOval((int)(x-pushRadius), (int)(y-pushRadius), (int)(2*pushRadius), (int)(2*pushRadius));
		} else {
			long dElapsed = (System.nanoTime() - pushDrawTimer)/1000000;
			if(dElapsed > pushDrawLength){
				pushDrawTimer = (System.nanoTime());
			}
			double radius = pushRadius*((double)dElapsed / (double)pushDrawLength);
			
			g.setColor(new Color(255,255,255,128));
			g.drawOval((int)(x-radius), (int)(y-radius), (int)(2*radius), (int)(2*radius));
		}
		
		if(isCollectingPu) {
			g.setColor(new Color(255,215,0,70));
			g.fillOval((int)(x-puCollectRadius), (int)(y-puCollectRadius), (int)(2*puCollectRadius), (int)(2*puCollectRadius));
		} else {
			g.setColor(new Color(255,215,0,32));
			g.drawOval((int)(x-puCollectRadius), (int)(y-puCollectRadius), (int)(2*puCollectRadius), (int)(2*puCollectRadius));
		}
		
		if(addOn>0 && addOnEnable){		
			double radA = Math.toRadians(spinAngle);
			double radB = Math.toRadians(spinAngle2);
			double radC = Math.toRadians(spinAngle3);
			double radD = Math.toRadians(spinAngle4);
				
			
				g.setColor(Color.RED);
				g.fillOval((int)(x-r/2+5*r*Math.cos(radA)), (int)(y-r/2+5*r*Math.sin(radA)), r, r);
				g.setStroke(new BasicStroke(3));
				g.setColor(Color.RED.darker());
				g.drawOval((int)(x-r/2+5*r*Math.cos(radA)), (int)(y-r/2+5*r*Math.sin(radA)), r, r);
				g.setStroke(new BasicStroke(1));
				
			if(addOn>1){	
				g.setColor(Color.GREEN);
				g.fillOval((int)(x-r/2+5*r*Math.cos(radB)), (int)(y-r/2+5*r*Math.sin(radB)), r, r);
				g.setStroke(new BasicStroke(3));
				g.setColor(Color.GREEN.darker());
				g.drawOval((int)(x-r/2+5*r*Math.cos(radB)), (int)(y-r/2+5*r*Math.sin(radB)), r, r);
				g.setStroke(new BasicStroke(1));
			}
			
			if(addOn>2){
				g.setColor(Color.BLUE);
				g.fillOval((int)(x-r/2+5*r*Math.cos(radC)), (int)(y-r/2+5*r*Math.sin(radC)), r, r);
				g.setStroke(new BasicStroke(3));
				g.setColor(Color.BLUE.darker());
				g.drawOval((int)(x-r/2+5*r*Math.cos(radC)), (int)(y-r/2+5*r*Math.sin(radC)), r, r);
				g.setStroke(new BasicStroke(1));
			}
			
			if(addOn>3){
				g.setColor(Color.YELLOW);
				g.fillOval((int)(x-r/2+5*r*Math.cos(radD)), (int)(y-r/2+5*r*Math.sin(radD)), r, r);
				g.setStroke(new BasicStroke(3));
				g.setColor(Color.YELLOW.darker());
				g.drawOval((int)(x-r/2+5*r*Math.cos(radD)), (int)(y-r/2+5*r*Math.sin(radD)), r, r);
				g.setStroke(new BasicStroke(1));
			}
			
			if(addOn>4){
				g.setColor(Color.GREEN);
				g.fillOval((int)(x-r/2+10*r*Math.cos(radA)), (int)(y-r/2+10*r*Math.sin(radA)), 2*r, 2*r);
				g.setStroke(new BasicStroke(3));
				g.setColor(Color.GREEN.darker());
				g.drawOval((int)(x-r/2+10*r*Math.cos(radA)), (int)(y-r/2+10*r*Math.sin(radA)), 2*r, 2*r);
				g.setStroke(new BasicStroke(1));
			}
			
			if(addOn>5){
				g.setColor(Color.BLUE);
				g.fillOval((int)(x-r/2+10*r*Math.cos(radB)), (int)(y-r/2+10*r*Math.sin(radB)), 2*r, 2*r);
				g.setStroke(new BasicStroke(3));
				g.setColor(Color.BLUE.darker());
				g.drawOval((int)(x-r/2+10*r*Math.cos(radB)), (int)(y-r/2+10*r*Math.sin(radB)), 2*r, 2*r);
				g.setStroke(new BasicStroke(1));
			}
			
			if(addOn>6){
				g.setColor(Color.YELLOW);
				g.fillOval((int)(x-r/2+10*r*Math.cos(radC)), (int)(y-r/2+10*r*Math.sin(radC)), 2*r, 2*r);
				g.setStroke(new BasicStroke(3));
				g.setColor(Color.YELLOW.darker());
				g.drawOval((int)(x-r/2+10*r*Math.cos(radC)), (int)(y-r/2+10*r*Math.sin(radC)), 2*r, 2*r);
				g.setStroke(new BasicStroke(1));
			}
			
			if(addOn>7){
				g.setColor(Color.RED);
				g.fillOval((int)(x-r/2+10*r*Math.cos(radD)), (int)(y-r/2+10*r*Math.sin(radD)), 2*r, 2*r);
				g.setStroke(new BasicStroke(3));
				g.setColor(Color.RED.darker());
				g.drawOval((int)(x-r/2+10*r*Math.cos(radD)), (int)(y-r/2+10*r*Math.sin(radD)), 2*r, 2*r);
				g.setStroke(new BasicStroke(1));
			}
			
		}
	}
	
	public int getx() { return x;}
	public int gety() { return y;}
	public int getr() { return r;}
	public int getLives() { return lives;}

	
	public void gainLife() {
		lives++;
		
	}

	public boolean isDead() {
		return lives<=0;
	}
	
}
