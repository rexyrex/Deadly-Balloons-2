package Entities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class SlowField {
	private double x;
	private double y;
	private double r;
	
	private double maxR;
	
	private long beforeStartTime;
	private long beforeDuration;
	public boolean isStart() {
		return isStart;
	}

	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}

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

	public double getMaxR() {
		return maxR;
	}

	public void setMaxR(double maxR) {
		this.maxR = maxR;
	}

	private long beforeElapsed;
	
	private long duration;
	private long startTime;
	private long elapsed;
	private boolean isStart;
	
	public SlowField(double x, double y, double maxR, long duration) {
		this.x = x;
		this.y = y;
		this.r = 0;
		this.maxR = maxR;
		this.beforeStartTime = System.nanoTime();
		this.beforeDuration=1000;
		this.beforeElapsed=0;
		
		
		this.duration=duration;
		startTime=0;
		elapsed = 0;
		isStart=false;		
	}
	
	public void pauseUpdate() {
		
	}
	
	public boolean update() {
		if(!isStart) {
			beforeElapsed = (System.nanoTime() - beforeStartTime) / 1000000;
			if(beforeElapsed > beforeDuration) {
				isStart = true;
				startTime = System.nanoTime();
			}
		} else {
			//started
			elapsed = (System.nanoTime() - startTime) / 1000000;
			if(elapsed > duration) {
				return true;
			}
		}
		return false;
	}
	
	public void draw(Graphics2D g){
		g.setColor(new Color(0,191,255,100));
		g.setStroke(new BasicStroke(1));
		
		if(!isStart) {
			
			r = maxR * ((double)beforeElapsed / beforeDuration);
			g.setColor(new Color(0,191,255,100));
			g.fillOval((int)(x-r), (int)(y-r), (int)(2*r), (int)(2*r));
			g.setColor(new Color(0,191,255,255));
			g.drawOval((int)(x-r), (int)(y-r), (int)(2*r), (int)(2*r));
			
			g.setColor(new Color(25,25,112,255));
			g.drawRect((int)(x), (int)(y), 20, 5);
			g.fillRect((int)(x), (int)(y), (int)(20-20*beforeElapsed/beforeDuration), 5);

		} else {
			
			r = maxR;
			g.setColor(new Color(30,144,255,100));
			g.fillOval((int)(x-r), (int)(y-r), (int)(2*r), (int)(2*r));
			g.setColor(new Color(30,144,255,255));
			g.drawOval((int)(x-r), (int)(y-r), (int)(2*r), (int)(2*r));
			
			g.setColor(new Color(25,25,112,255));
			g.drawRect((int)(x-10), (int)(y-2.5), 20, 5);
			g.fillRect((int)(x-10), (int)(y-2.5), (int)(20-20*elapsed/duration), 5);
		}
	}

}
