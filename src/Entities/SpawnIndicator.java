package Entities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import Panels.GamePanel;

public class SpawnIndicator {
	double x;
	double y;
	double r;
	
	double maxR;
	double minR;
	
	double shrinkSpeed;
	
	double maxCount;
	double count;
	
	Color c;
	
	String type;
	PowerUp pu;
	
	//powerup spawn
	public SpawnIndicator(String type, PowerUp pu) {
		this.x = pu.getx();
		this.y = pu.gety();
		this.r = maxR;
		maxCount = 3;
		count = 0;
		shrinkSpeed = 0.6;
		
		maxR = 15;
		minR = 1;
		
		this.type = type;
		this.pu = pu;
		
		c = pu.getColor();
	}
	
	public void spawn() {
		if(type.equals("PowerUp")) {
			pu.resetTimer();
			GamePanel.updatePuCount(pu, false);
			GamePanel.powerups.add(pu);
		}
	}
	
	public boolean update() {
		if(r >= minR) {
			r -= shrinkSpeed;
		} else {
			r = maxR;
			count++;
		}
		
		if(count > maxCount) {
			return true;
		} 
		
		return false;
	}
	
	public void draw(Graphics2D g) {
		g.setColor(c);
		g.setStroke(new BasicStroke(2));
		g.drawRect((int)(x-r),(int)(y-r), (int)(2*r), (int)(2*r));
		g.setStroke(new BasicStroke(1));
	}
}
