package Entities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class DividerBlock {
	private double x;
	private double y;
	private double r;
	private long timer;
	private long lifeTime;
	private Color c;
	
	private long elapsed;
	
	
	private long hitTimer;
	private long hitLength;
	
	private long circleTimer;
	private long circleLength;
	private double circleRadius;
	private Color circleC;
	
	private boolean hit;
	
	public DividerBlock(double x, double y, double r){
		this.x = x;
		this.y = y;
		this.r = r;
		this.timer = System.nanoTime();
		this.lifeTime = 7000;
		this.c = new Color(0,0,0,177);
		this.circleC = new Color(255,0,0,200);
		this.circleTimer = System.nanoTime();
		this.circleRadius = 0;
		this.circleLength = 700;
		hit= false;
		hitTimer = System.nanoTime();
		hitLength = 100;
	}
	
	public double getx() { return x; }
	public double gety() { return y; }
	public double getr() { return r; }
	
	public void hit(){
		hit = true;
		hitTimer = System.nanoTime();		
	}
	
	public void pauseUpdate() {
		timer = System.nanoTime() - elapsed * 1000000;
	}
	
	public boolean update(){
		elapsed = (System.nanoTime() - timer)/1000000;
		if(elapsed > lifeTime){
			return true;
		}
		
		long hitElapsed = (System.nanoTime() - hitTimer)/1000000;
		if(hitElapsed > hitLength){
			hit = false;
		}
		
		long circleElapsed = (System.nanoTime() - circleTimer)/1000000;
		if(circleElapsed > circleLength){
			circleTimer = System.nanoTime();
			
		}
		
		if(hit){
			circleLength = 300;
			circleC = new Color(255,0,0,177);
			c = new Color ( 255,7,7, 100);
		} else {
			circleLength = 700;
			circleC = new Color(200,0,0, 255);
			c = new Color ( 0,0,0, (int)(200-200*((double)elapsed/lifeTime)));
		}
		
		circleRadius = r*((double)circleElapsed/circleLength);		
		
		return false;
	}
	
	public void draw(Graphics2D g){
		
		g.setColor(c);
		g.fillOval((int)(x-r), (int)(y-r), (int)(2*r), (int)(2*r));
		g.setStroke(new BasicStroke(3));
		g.setColor(c.darker());
		g.drawOval((int)(x-r), (int)(y-r), (int)(2*r), (int)(2*r));
		g.setStroke(new BasicStroke(1));
		
		
		long elapsed = (System.nanoTime() - timer)/1000000;
		int lifeBarLength = (int) r;
		g.setColor(new Color(220,243,255,200));
		double stunProgress = 1 - (double)elapsed/(double)lifeTime;
		if(stunProgress < 0) {
			stunProgress = 0;
		}			
		g.drawRect((int)(x-lifeBarLength/2), (int)(y+lifeBarLength/4), lifeBarLength, lifeBarLength/5);
		g.fillRect((int)(x-lifeBarLength/2), (int)(y+lifeBarLength/4), (int)(lifeBarLength*stunProgress), lifeBarLength/5);
		
		g.setColor(circleC);
		g.drawOval((int)(x-circleRadius), (int)(y-circleRadius), (int)(2*circleRadius), (int)(2*circleRadius));
		
	}
}
