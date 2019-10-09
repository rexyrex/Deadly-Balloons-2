package Entities;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import Panels.GamePanel;
import Utils.MathUtils;

public class Torpedo {
	private double x;
	private double y;
	private double r;

	private double dx;
	private double dy;
	
	private double destX;
	private double destY;
	
	private double rad;
	private double speed;
	
	private Color c;
	
	public Torpedo(double x, double y, double destX, double destY) {
		
		this.x = x;
		this.y = y;
		this.r = 20;
		
		this.destX = destX;
		this.destY = destY;
		
		this.speed = 5;
		
		rad = Math.toRadians(180);
		
		dx = Math.cos(rad) * speed;
		dy = Math.sin(rad) * speed;
		c = Color.black;
		
		goTowards(destX, destY);
	}
	
	public void goTowards(double px, double py){
		speed += 1.0;
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
	
	public void explode() {
		GamePanel.bombs.add(new Bomb(destX, destY, false, true));
	}
	
	public boolean update() {
		goTowards(destX, destY);
		x += dx;
		y += dy;
		
		if(MathUtils.getDist(x, y, destX, destY)<2*r) {
			speed = 7;			
		}
		
		if(MathUtils.getDist(x, y, destX, destY)<r) {
			return true;			
		}
		return false;
	}
	
	public void draw(Graphics2D g) {
		//draw torpedo
		g.setColor(c);
		g.fillOval((int)(x-r),(int)(y-r), (int)(2*r), (int)(2*r));		
		g.setStroke(new BasicStroke(3));
		g.setColor(c.darker());		
		g.drawOval((int)(x-r),(int)(y-r), (int)(2*r), (int)(2*r));
		g.setStroke(new BasicStroke(1));
		
		//draw destination
		g.setColor(c);
		g.setStroke(new BasicStroke(3));
		g.drawOval((int)(destX-r),(int)(destY-r), (int)(2*r), (int)(2*r));
		g.draw(new Line2D.Double(destX - r*2, destY, destX + r*2, destY));
		g.draw(new Line2D.Double(destX, destY- r*2, destX, destY+ r*2));
		g.setStroke(new BasicStroke(1));
	}
}
