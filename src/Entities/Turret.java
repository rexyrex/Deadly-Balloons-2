package Entities;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Random;

import Panels.GamePanel;


public class Turret {
	private long lifeSpan;
	private long lifeTimer;
	private double x;
	private double y;
	private double r;
	
	private double angleSpeed;
	private double angle;
	private double targetAngle;
	
	private boolean dead;
	
	private long bulletTimer;
	private long bulletDelay;	
	private long normalBulletDelay;

	private int thisCount;
	
	private boolean superCharged;
	private long superChargedDuration;
	private long superChargedStartTime;
	
	Color color1;
	
	public Turret(double x, double y, int index){
		this.x = x;
		this.y = y;
		this.r = 10;
		angle = 270;
		angleSpeed = 2;
		targetAngle = 270;
		superCharged = false;
		superChargedDuration = 10000;
		superChargedStartTime = 0;

		thisCount = index;

		bulletTimer = System.nanoTime();
		normalBulletDelay = 500;
		bulletDelay = 500;
		
		lifeSpan = 20000;
		lifeTimer = System.nanoTime();
		Random rn = new Random();
		int cR = (int) (rn.nextDouble()*100);
		int cG = (int) (rn.nextDouble()*100);
		int cB = (int) (rn.nextDouble()*127);
		//color1 = new Color(cR, cG, cB, 240);
		color1 = new Color(255,255,255,255);
		dead = false;
	}
	
	public int getIndex() {
		return thisCount;
	}
	
	public double getx() { return x; }
	public double gety() { return y; }
	public double getr() { return r; }
	public void setDead(boolean b) { dead = b;}
	public boolean isDead() { return dead; }
	
	public void updateEnemyPosition(Enemy e){
		double ex = e.getx();
		double ey = e.gety();
		double xDiff = ex - x;
		double yDiff = ey - y;
		
		if(xDiff<0)
			targetAngle = Math.toDegrees(Math.atan(yDiff/xDiff))+180;
		else if(yDiff<0)
			targetAngle = Math.toDegrees(Math.atan(yDiff/xDiff))+360;
		else
			targetAngle = Math.toDegrees(Math.atan(yDiff/xDiff));
		
		
	}
	
	public boolean isSuperCharged() {
		
		return superCharged;
	}

	public void setSuperCharged(boolean superCharged) {
		this.superCharged = superCharged;
		if(superCharged==true) {
			superChargedStartTime = System.nanoTime();
			bulletDelay = normalBulletDelay /3;
		} else {
			bulletDelay = normalBulletDelay;
		}
		
		
	}
	
	public boolean update(){
		//update Angle

		angle = targetAngle;
		
		if(angle>359){
			angle = 0;
		}
		
		if(angle<0){
			angle = 359;
		}
		
		if(superCharged && (System.nanoTime() - superChargedStartTime)/1000000 > superChargedDuration) {
			setSuperCharged(false);
		}
		

		
		//fire
		long elapsed = (System.nanoTime() - bulletTimer)/1000000;
		if(elapsed > bulletDelay){
			bulletTimer = System.nanoTime();
			GamePanel.bullets.add(new Bullet(angle,(int)x,(int)y,color1, GamePanel.player.getTurretDmg()));
		}
		
		//check lifespan
		long lifeElapsed = (System.nanoTime() - lifeTimer)/1000000;
		if(lifeElapsed > lifeSpan){
			dead = true;
			return true;
		}
		
		return false;
	}
	


	public void draw(Graphics2D g){
		if(superCharged) {
			g.setColor(Color.RED);
		} else {
			g.setColor(color1);
		}
		g.drawOval((int)(x-r),(int)(y-r), (int)(2*r), (int)(2*r));
		g.drawRect((int)(x-r),(int)(y-r), 20, 20);
		//g.rotate(Math.toRadians(angle));
		
		if(superCharged) {
			g.setColor(Color.RED.darker());
		} else {
			g.setColor(color1.darker());
		}	
		
		g.drawRect((int)(x-r-5), (int)(y+1.4*r), 30, 8);
		g.fillRect((int)(x-r-5), (int)(y+1.4*r), (int)(30-30.0*(System.nanoTime()-lifeTimer)/1000000/lifeSpan), 8);
		

		
		g.setColor(Color.white);
		g.setFont(new Font("Gulim", Font.BOLD,25));
		g.drawString(Integer.toString(thisCount+1), (int)(x+r*1.5), (int)(y+r));
		
	}
	
}
