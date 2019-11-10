package Entities;

import java.awt.Color;
import java.awt.Graphics2D;

import Panels.GamePanel;

public class EnemyBullet {
	private double x;
	private double y;
	private int r;
	
	private double dx;
	private double dy;
	private double dxa;
	private double dya;
	
	private double rad;
	public double getRad() {
		return rad;
	}

	public void setRad(double rad) {
		this.rad = rad;
	}

	private double speed;
	
	Color color1;
	
	private double[] xHistory = new double[20];
	private double[] yHistory = new double[20];
	
	private boolean lethal;
	
	
	public boolean isLethal() {
		return lethal;
	}

	public void setLethal(boolean lethal) {
		this.lethal = lethal;
	}
	
	private boolean heal;

	public boolean isHeal() {
		return heal;
	}

	public void setHeal(boolean heal) {
		this.heal = heal;
	}

	public EnemyBullet(double angle, double x, double y, int r, double speed, boolean lethal, boolean heal) {
		if(lethal) {
			color1 = Color.RED;
		} else if(heal){
			color1 = Color.green;
		} else {		
			color1 = Color.BLACK;
		}
		
		this.x = x;
		this.y = y;
		this.r = r;
		this.speed = speed;
		
		rad = Math.toRadians(angle);		
		dx = Math.cos(rad) * speed;
		dy = Math.sin(rad) * speed;
		dya = 0;
		dxa = 0;
		
		this.heal = heal;
		this.lethal = lethal;
	}
	
	public double getx(){ return x; }
	public double gety(){ return y; }
	public double getr(){ return r; }
	
	public double getSpeed(){ return speed;}
	public void setdx(double d){ dx=d;}
	public void setdy(double d){ dy=d;}
	
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
	
	public boolean update(){
		historyUpdate(x,y);
		x += dx;
		y += dy;
		dx += dxa;
		dy += dya;
		
		if(heal) {
			//Check if rex boss exists
			boolean rexBossExists = false;
			double rexBossX = 0;
			double rexBossY = 0;
			for(int i=0; i<GamePanel.enemies.size(); i++) {
				Enemy e = GamePanel.enemies.get(i);
				if(e.getType() == 1000000) {
					//rexboss exists
					rexBossExists = true;
					rexBossX = e.getx();
					rexBossY = e.gety();
				}
			}
			if(rexBossExists) {
				goTowards(rexBossX, rexBossY);
			} else {
				return true;
			}
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
		
		for(int i=0; i<getHistoryLength(); i++){
			
			if(i>(int)(getHistoryLength()/7))
				if(lethal) {
					g.setColor(Color.orange);
				} else if(heal){
					g.setColor(new Color(124,252,0,255));
				} else {
					g.setColor(Color.GRAY);
				}				
			else {
				if(lethal) {
					g.setColor(Color.yellow);
				}  else if(heal){
					g.setColor(new Color(173,255,47,255));
				} else {
					g.setColor(Color.DARK_GRAY);
				}
			}
			
			g.fillOval((int)(xHistory[i]-r/(i/2+2)),(int)(yHistory[i]-r/(i/2+2)),2*r/(i/2+2),2*r/(i/2+2));
				
		}
		
		
	}
}
