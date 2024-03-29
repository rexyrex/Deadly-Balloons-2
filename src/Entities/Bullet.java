package Entities;
import java.awt.Color;
import java.awt.Graphics2D;

import Panels.GamePanel;


public class Bullet {
	private double x;
	private double y;
	private int r;
	
	//private static int count = 0;

	
	private double dx;
	private double dy;
	private double dxa;
	private double dya;
	

	
	private double rad;
	private double speed;
	
	private Enemy e;
	private boolean missileFoundTarget;
	
	private boolean isRage = false;
	private boolean isSideMissile = false;
	private boolean isRightMissile = false;
	private boolean isLeftMissile = false;
	private boolean isMissileReady;
	private boolean isMissileUpdated;
	private long missileUpdateTimer;
	private long missileUpdateDelay;
	
	private boolean isAddOn;
	
	private boolean isTurret;
	
	private String type;
	
	//spin bullet test
	private double spinAngle;
	
	private double[] xHistory = new double[20];
	private double[] yHistory = new double[20];
	
	private Color color1;
	
	private double dmg;
	
	public double getDmg() {
		return dmg;
	}

	public void setDmg(double dmg) {
		this.dmg = dmg;
	}

	//normal
	public Bullet(double angle, int x, int y, double dmg, boolean isRaged){
		this.x = x;
		this.y = y;
		if(isRaged) {
			r = 5;
		} else {
			r = 2;
		}
		this.isRage = isRaged;
		
		rad = Math.toRadians(angle);
		if(isRaged) {
			speed = 12;
		} else {
			speed = 10;
		}
		
		dx = Math.cos(rad) * speed;
		dy = Math.sin(rad) * speed;
		dya = 0;
		dxa = 0;
		
		isSideMissile = false;
		isAddOn = false;
				
		if(isRaged) {
			color1 = new Color(148,0,211,255); //purple
		} else {
			color1 = Color.YELLOW;
		}
		
		this.dmg = dmg;
	}
	
	//colored normal bullet
	public Bullet(double angle, int x, int y, double dmg, Color c){
		this.x = x;
		this.y = y;
		r = 2;
		
		rad = Math.toRadians(angle);
		speed = 10;
		dx = Math.cos(rad) * speed;
		dy = Math.sin(rad) * speed;
		dya = 0;
		dxa = 0;
		
		isSideMissile = false;
		isAddOn = false;
				
		color1 = c;
		this.dmg = dmg;
	}
	
	//customizable
	public Bullet(String type, double angle, int x, int y, double dmg){
		this.x = x;
		this.y = y;
		r = 2;
		
		rad = Math.toRadians(angle);
		speed = 10;
		dx = Math.cos(rad) * speed;
		dy = Math.sin(rad) * speed;
		dya = 0;
		dxa = 0;
		
		isSideMissile = false;
		isAddOn = false;
		this.type = type;
				
		color1 = Color.BLACK;
		this.dmg = dmg;
	}
	
	//addon
	public Bullet(double angle, int x, int y, int r, Color c, double dmg){
		this.x = x;
		this.y = y;
		this.r = r;
		
		isSideMissile = false;
		isAddOn = true;
		
				
		rad = Math.toRadians(angle);
		speed = 10;
		dx = Math.cos(rad) * speed;
		dy = Math.sin(rad) * speed;
		dya = 0;
		dxa = 0;
		
		color1 = c;
		this.dmg = dmg;
	}
	
	//TURRET BULLETS
	public Bullet(double angle, int x, int y, Color c, double dmg){
		this.x = x;
		this.y = y;
		this.r = 3;
		
		isSideMissile = false;
		isAddOn = false;
		isTurret = true;
		isMissileReady = true;
				
		rad = Math.toRadians(angle);
		speed = 10;
		dx = Math.cos(rad) * speed;
		dy = Math.sin(rad) * speed;
		dya = 0;
		dxa = 0;
		
		color1 = c;
		this.dmg = dmg;
	}
	
	//side missile
	public Bullet(String type, int x, int y, int r, Color c, double dmg){
		this.x = x;
		this.y = y;
		this.r = r;
		//count++;
		
		this.dmg = dmg;

		isSideMissile = true;
		isAddOn = false;
		
		isMissileUpdated =false;
		isMissileReady = false;
		missileUpdateTimer = System.nanoTime();
		missileUpdateDelay = 0;
		e= null;
		missileFoundTarget = false;
		
		int angle;
		if(type.equals("right")){
			angle = 35;
			this.dxa =  -0.2;
			isRightMissile = true;
			this.dya = -0.1;
			
		} else if(type.equals("left")){
			angle = 145;
			this.dxa = 0.2;
			isLeftMissile = true;
			this.dya = -0.1;
		} else if(type.equals("topRight")){
			angle = 325;
			this.dxa =  -0.2;
			isRightMissile = true;
			this.dya = 0.1;
		} else {
			angle = 215;
			//angle = 210;
			this.dxa = 0.2;
			isLeftMissile = true;
			this.dya = 0.1;
		}
		
		
			
		rad = Math.toRadians(angle);
			
		speed = 8;
		dx = Math.cos(rad) * speed;
		dy = Math.sin(rad) * speed;
		
		
		
		color1 = c;
	}
	
	public double getx(){ return x; }
	public double gety(){ return y; }
	public double getr(){ return r; }
	
	public double getSpeed(){ return speed;}
	public void setdx(double d){ dx=d;}
	public void setdy(double d){ dy=d;}
	
	public boolean isSideMissile(){return isSideMissile;}
	public boolean isTurret(){return isTurret;}
	
	public void updateAcceleration(){
		if(!isMissileReady){
			if(isRightMissile && dx<0){
				dxa =0;
				dx=0;
				isMissileReady = true;
			} 
			
			if(isLeftMissile && dx>0){
				dxa =0;
				dx=0;
				isMissileReady = true;
			} 		
			
			dya -= 0.01;
			if(dya>0){
				dya = 0;			
			}
			
			if(Math.abs(dy)>Math.abs(speed*1.5)){
				dya = 0;
			}	
		}
	}
	
	public void updateEnemyPosition(Enemy e1){
		if(!missileFoundTarget){
			
			missileFoundTarget = true;
			e=e1;
		} else if(!e.isDead()){
			double ex = e.getx();
			double ey = e.gety();

			
			long elapsed = (System.nanoTime() - missileUpdateTimer)/1000000;
			if(elapsed > missileUpdateDelay){
				isMissileUpdated = false;
				missileUpdateTimer = System.nanoTime();
			}
			if(isMissileReady && !isMissileUpdated){
				isMissileUpdated = true;
				double xDiff = ex - x;
				double yDiff = ey - y;
	
				double angle;
				if(xDiff <0)
					angle =  Math.toDegrees(Math.atan(yDiff/xDiff))+180;
				else if(yDiff <0)
					angle =  Math.toDegrees(Math.atan(yDiff/xDiff))+360;
				else 
					angle =  Math.toDegrees(Math.atan(yDiff/xDiff));
				double rad = Math.toRadians(angle);
				dx = speed * Math.cos(rad) + dxa;
				dy = speed * Math.sin(rad) + dya;
				
			}
		} else if(e.isDead()){
			if(isSideMissile) // if it is a side missile it follows another enemy. if not, bullet continues
				missileFoundTarget=false;
		}
	}
	
	//for managing previous x and y positions
	public void historyUpdate(double hx, double hy){
		
		int l = xHistory.length;
		for(int i=0; i<l-1; i++){
			xHistory[l-i-1] = xHistory[l-i-2];
		}
		xHistory[0] = hx;
		for(int i=0; i<l-1; i++){
			yHistory[l-i-1] = yHistory[l-i-2];
		}
		yHistory[0] = hy;
	}
	
	public int getHistoryLength(){
		int l=0;
		for(int i=0; i<xHistory.length-1; i++){
			if(xHistory[i]!=0){
				l++;
			}
		}
		return l;
	}
	
	public boolean update(){
		historyUpdate(x,y);
		x += dx;
		y += dy;
		dx += dxa;
		dy += dya;
		
		if(isAddOn){ // MAKE IT SPIN
			spinAngle+=70;
			if(spinAngle>359){
				spinAngle = 0;
			}
			double rad = Math.toRadians(spinAngle);
			double radius = 2.5;
			//if spinning
			x+= radius * Math.cos(rad);
			y+= radius * Math.sin(rad);
		}
		if(isSideMissile){
			updateAcceleration();
			if(x<0) x=GamePanel.WIDTH;
			if(y<0) y=GamePanel.HEIGHT;
			if(x>GamePanel.WIDTH) x = 0;
			if(y>GamePanel.HEIGHT) y = 0;
			
		}
		
		if(x<-r || x > GamePanel.WIDTH + r ||
				y<-r || y>GamePanel.HEIGHT + r){
			return true;
		}		
		return false;
	}
	
	public void draw(Graphics2D g){
		g.setColor(color1);
		g.fillOval((int)(x-r),(int)(y-r),2*r,2*r);
		
		if(isSideMissile || isAddOn || isRage){
			for(int i=0; i<getHistoryLength(); i++){
				
				if(i>(int)(getHistoryLength()/7))
					g.setColor(Color.red);
				else
					g.setColor(Color.orange);
				g.fillOval((int)(xHistory[i]-r/(i/2+2)),(int)(yHistory[i]-r/(i/2+2)),2*r/(i/2+2),2*r/(i/2+2));
			}
		}
		
	}
	
	
	
}
