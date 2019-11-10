package Entities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import Panels.GamePanel;

public class EnemyTurret {
	private double x;
	private double y;
	private double r;
	
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getR() {
		return r;
	}

	public void setR(double r) {
		this.r = r;
	}

	private double angle;
	
	private long fireStartTime;
	private long fireElapsed;
	private long fireInterval;
	
	private boolean gettingBombed;
	private long gettingBombedTimer;
	private long gettingBombedInterval;
	
	private double health;
	private double maxHealth;
	
	private boolean healer;
	
	private int animationAngle;
	private double animationRad;
	
	//when hit
	private boolean hit;
	private long hitTimer;
	
	Color color;
	
	public boolean isGettingBombed() { return gettingBombed;}
	public void setGettingBombed(boolean b) { gettingBombed = b;}
	
	public EnemyTurret(double x, double y, double maxHealth, boolean healer) {
		this.x = x;
		this.y = y;
		this.r = 20;
		fireStartTime = System.nanoTime();
		fireElapsed = 0;
		fireInterval = 1000;
		
		this.maxHealth = maxHealth;
		health = maxHealth;
		
		this.healer = healer;
		
		gettingBombed = false;
		gettingBombedTimer = System.nanoTime();
		gettingBombedInterval = 100;
		
		animationAngle = 0;
		animationRad = Math.toRadians(animationAngle);
		
		hit = false;
		hitTimer = 0;
		
		color = new Color(0,100,0,170);
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
	
	public void hit(double dmg) {
		health -= dmg;
		hit = true;
		hitTimer = System.nanoTime();
	}
	
	public boolean update() {
		animationAngle+= 2;
		animationRad = Math.toRadians(animationAngle);
		
		if(hit){
			long elapsed = (System.nanoTime()-hitTimer)/1000000;
			if(elapsed > 50){
				hit = false;
				hitTimer = 0;
			}
		}
		
		if(gettingBombed){
			long elapsed = (System.nanoTime()-gettingBombedTimer)/1000000;
			if(elapsed>gettingBombedInterval){
				gettingBombedTimer = System.nanoTime();
				hit(GamePanel.player.getBombDmg());
			}
		}		
		
		if(health<=0) {
			return true;
		}
		
		fireElapsed = (System.nanoTime() - fireStartTime) / 1000000;
		if(fireElapsed > fireInterval) {
			//fire
			GamePanel.enemyBullets.add(new EnemyBullet(0,x,y,10, 10, false, true));
			fireStartTime = System.nanoTime();
		}
		return false;
	}
	
	public void draw(Graphics2D g){
		g.setStroke(new BasicStroke(3));	
		g.setColor(color);
		
		if(hit) {
			g.setColor(color.brighter());
		}


		g.drawOval((int)(x-r),(int)(y-r), (int)(2*r), (int)(2*r));
		
		
		double progress = (double)fireElapsed/fireInterval;
		if(progress > 1) {
			progress = 1;
		}
//		g.drawOval(
//				(int)(x-r/2 * (progress) - r/2 ),
//				(int)(y-r/2* (progress) - r/2), 
//				(int)(2*r/2 * (progress) + r/2), 
//				(int)(2*r/2 * (progress) + r/2));
		g.drawOval(
				(int)(x-r + r/2 * (progress)),
				(int)(y-r + r/2 * (progress)), 
				(int)(2*r - r * (progress)), 
				(int)(2*r - r * (progress)));
		
		
		g.draw(
				new Line2D.Double(
						x + Math.cos(animationRad) * r, 
						y + Math.sin(animationRad) * r, 
						x + Math.cos(animationRad + Math.PI) * r, 
						y + Math.sin(animationRad + Math.PI) * r));
		g.draw(
				new Line2D.Double(
						x + Math.cos(animationRad + Math.PI/2) * r, 
						y + Math.sin(animationRad + Math.PI/2) * r, 
						x + Math.cos(animationRad + Math.PI  + Math.PI/2) * r, 
						y + Math.sin(animationRad + Math.PI + Math.PI/2) * r));
		//g.rotate(Math.toRadians(angle));
		

		g.drawRect((int)(x-r/2), (int)(y+1.4*r), (int)r, (int)(r/5));
		g.setColor(color.darker());
		g.fillRect((int)(x-r/2), (int)(y+1.4*r), (int)(r*(health/maxHealth)), (int)(r/5));		
		g.setStroke(new BasicStroke(1));	
	}
}
