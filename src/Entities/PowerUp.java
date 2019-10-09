package Entities;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import Panels.GamePanel;
import Utils.RandomUtils;


public class PowerUp {
	private double x;
	private double y;
	private double dx;
	private double dy;


	private int r;
	private int type;
	private String name;
	private String collectText;
	
	private Color color1;
	
	private long timer;
	private long lifeTime;
	
	private double staminaGain;
	
	private boolean beingCollected;
	
	//1 -- +1 life
	//2 -- +1 power
	//3 -- +2 power
	//4 -- slowdown time
	//5 -- player speed
	//6 -- +1 Bomb
	//7 -- fireSpeed
	//8 -- start Spazing
	//9 -- addons
	//10 - following missile
	//11 - + stamina
	//12 - + max stamina
	
	public PowerUp(int type, double x, double y){
		this.type = type;
		this.x = x;
		this.y = y;
		this.dx = 0;
		this.dy = 2;
		timer = System.nanoTime();
		lifeTime = 10000;
		
		beingCollected = false;
		
		if(type == 1){
			color1 = Color.pink;
			r=3;
			name="Life";
			collectText = "Life +1";
		}
		if(type == 2){
			color1 = Color.yellow;
			r=3;
			name="Power";
			collectText = "Power +1";
		}
		if(type ==3){
			color1 = Color.yellow;
			r=5;
			name="Power";
			collectText = "Power +2";
		}
		if(type == 4){
			color1 = Color.white;
			r = 22;
			name="SPEED UP ENEMIES";
			collectText = "Speed Up!";
		}
		if(type == 5){
			color1 = Color.CYAN;
			r = 5;
			name="M.Speed";
			collectText = "Speed +1";
		}
		if(type == 6){
			color1 = Color.BLACK;
			r = 4;
			name="BOMB";
			collectText = "Bomb +1";
		}
		if(type == 7){
			color1 = Color.PINK;
			r = 4;
			name="Att. Speed";
			collectText = "Rate of Fire +1";
		}
		if(type == 8){
			color1 = Color.blue;
			r = 4;
			name="Spaz";
			collectText = "Spazzing Out!";
		}
		if(type == 9){
			color1 = Color.yellow.brighter();
			r = 4;
			name = "Add On";
			collectText = "AddOn Equipped!";
		}
		if(type == 10){
			color1 = Color.ORANGE.brighter();
			r = 4;
			name = "Seeker Missiles";
			collectText = "Missiles Activated!";
		}
		if(type == 11){
			color1 = Color.RED.brighter();
			r = 5;
			name = "+ Stamina";
			staminaGain = Math.random() * 100 + 100;
			collectText = "Stamina +" + staminaGain;
		}
		if(type == 12){
			color1 = Color.RED.brighter();
			r = 6;
			name = "+ Max Stamina";
			staminaGain = Math.random() * 100 + 100;
			collectText = "Max Stamina +" + staminaGain;
		}
		if(type == 13){
			color1 = Color.PINK;
			r = 6;
			name = "SHELTER";
			collectText = "Shelter Added!";
		}
		
		if(type == 14){
			color1 = Color.GREEN;
			r = 6;
			name = "Army";
			collectText = "Army Spawning!";
		}
		
		if(type == 15){
			color1 = Color.ORANGE;
			r = 6;
			name = "Turret SuperCharge";
			collectText = "Supercharging Turrets!";
		}
		
		if(type == 16){
			color1 = Color.GREEN;
			r = 7;
			name = "Friends";
			collectText = "Hi friends!";
		}
		
		if(type == 17){
			color1 = Color.RED;
			r = 30;
			name = "DIE";
			collectText = "Ouch...";
		}
		
		if(type == 18){
			color1 = new Color(255,255,204);
			r = 7;
			name = "Lightning";
			collectText = "Zap!";
		}
		
		if(type == 19){
			color1 = Color.BLACK;
			r = 7;
			name = "Torpedos";
			collectText = "Go Get Em!";
		}
		
		
	}
	
	public void showCollectText() {
		GamePanel.texts.add(new Text(GamePanel.player.getx(), GamePanel.player.gety(), 2000, collectText));
	}
	
	public void showCollectTextAtPowerUp() {
		GamePanel.texts.add(new Text(x, y, 2000, collectText));
	}
	
	public double getx(){ return x; }
	public double gety(){ return y; }
	public double getDx() {
		return dx;
	}

	public void setDx(double dx) {
		this.dx = dx;
	}

	public double getDy() {
		return dy;
	}

	public void setDy(double dy) {
		this.dy = dy;
	}
	public int getr(){ return r; }
	public int getType(){ return type; }
	public double getStaminaGain() { return staminaGain; }
	
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
	
	public void setBeingCollected(boolean beingCollected) {
		this.beingCollected = beingCollected;
	}
	
	public void recoverMovement() {
		dx = 0;
		dy = 2;
	}
	
	public void collect() {
		if(type==1){
			GamePanel.player.gainLife();
		}
		if(type==2){
			GamePanel.player.increasePower(1);
		}
		if(type==3){
			GamePanel.player.increasePower(2);
		}
		if(type==4){
			GamePanel.slowDownTimer = System.nanoTime();
			for(int j = 0; j<GamePanel.enemies.size(); j++){
				GamePanel.enemies.get(j).setSlow(true);
			}
		}
		if(type==5){
			GamePanel.player.incSpeed();
		}
		if(type==6){
			GamePanel.player.incBombs();
		}
		if(type==7){
			GamePanel.player.incFireRate();
		}
		if(type==8){
			GamePanel.player.startSpazing();
		}
		if(type==9){
			GamePanel.player.gainAddOn();
		}
		if(type==10){
			GamePanel.player.startFiringSide();
		}
		if(type==11){
			GamePanel.player.gainStamina(staminaGain);
		}
		if(type==12){
			GamePanel.player.increaseMaxStamina(staminaGain);
		}
		if(type==13){
			GamePanel.player.gainWalls(1);
		}
		
		if(type==14){					
			int tmpInterval = GamePanel.HEIGHT / 10;
			
			for(int j=0; j<10; j++) {
				GamePanel.friends.add(new Friend(35 + j*tmpInterval, GamePanel.HEIGHT, 35+ j*tmpInterval, 0, 1));
			}	
		}
		
		if(type==15) {
			for(int j=0; j<GamePanel.turrets.size(); j++) {
				GamePanel.turrets.get(j).setSuperCharged(true);
			}
		}
		
		if(type==16){
			for(int j=0; j<4; j++) {
				GamePanel.friends.add(new Friend(GamePanel.player.getx(), GamePanel.player.gety(), 0, 0, 2));
			}
		}
		
		if(type==17){
			GamePanel.player.loseLife();
		}
		
		if(type==18) {
			GamePanel.lightnings.add(new Lightning(GamePanel.enemies, 1, GamePanel.player.getLightningStunLength()));
		}
		
		if(type==19) {
			double tDestX;
			double tDestY;
			for(int t=0; t< 3; t++) {
				int enemyIndex = (int) (Math.random() * GamePanel.enemies.size());
				if(GamePanel.enemies.size() == 0) {
					tDestX = Math.random() * GamePanel.WIDTH;
					tDestY = Math.random() * GamePanel.HEIGHT;
				} else {
					tDestX = GamePanel.enemies.get(enemyIndex).getx();
					tDestY = GamePanel.enemies.get(enemyIndex).gety();
				}
				double[] offset = RandomUtils.getRandomOffset(5, 5);
				tDestX = tDestX + offset[0];
				tDestY = tDestY + offset[1];
				
				if(tDestX < 0) {
					tDestX = 0;
				}
				
				if(tDestY < 0) {
					tDestY = 0;
				}
				
				if(tDestX >= GamePanel.WIDTH) {
					tDestX = GamePanel.WIDTH;
				}
				
				if(tDestY >= GamePanel.HEIGHT) {
					tDestY = GamePanel.HEIGHT;
				}
				
				GamePanel.torpedos.add(new Torpedo(GamePanel.player.getx(), GamePanel.player.gety(), tDestX, tDestY));
			}
		}
	}
	
	public void goTowards(double px, double py, double speed){
		double xDiff = px-x;
		double yDiff = py-y;
			double rad;
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
	
	public boolean update(){
		
		y+= dy;
		x+= dx;
		
		long elapsed = (System.nanoTime() - timer)/1000000;
		if(elapsed > lifeTime){
			return true;
		}
		
		if(y>GamePanel.HEIGHT+r){
			y = 0;
		}
		
		return false;
		
	}
	
	public void draw(Graphics2D g){
		g.setColor(color1);
		g.fillRect((int)(x-r),(int)(y-r), 2*r, 2*r);
		
		g.setStroke(new BasicStroke(3));		
		g.setColor(color1.darker());
		g.drawRect((int)(x-r),(int)(y-r), 2*r, 2*r);
		g.setStroke(new BasicStroke(1));
		
		long elapsed = (System.nanoTime() - timer)/1000000;
		g.drawRect((int)(x-8),(int)(y+2.3*r),16, 6);
		g.fillRect((int)(x-8),(int)(y+2.3*r),(int)(16-16*((double)elapsed/lifeTime)), 6);
		
		g.setFont(new Font("Gulim",Font.PLAIN,16));
		int length = (int) g.getFontMetrics().getStringBounds(name, g).getWidth();
		g.drawString(name, (int)(x-length/2), (int)y-7);
	}
	
	
}
