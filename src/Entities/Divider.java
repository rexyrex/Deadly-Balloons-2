package Entities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import Panels.GamePanel;
import Utils.MathUtils;
import VFX.Explosion;

public class Divider {
	private double x1;
	private double y1;
	
	private double x2;
	private double y2;
	
	private long lifeTime;
	
	private long startTime;
	private long elapsed;
	
	private double dividerBlockRadius;
	
	private double rad;
	
	Color c;
	
	public Divider(double x1, double y1, double x2, double y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		
		GamePanel.explosions.add(new Explosion(x1, y1,0, 40));
		GamePanel.explosions.add(new Explosion(x2, y2,0, 40));
		
		startTime = System.nanoTime();
		lifeTime = 3000;
		elapsed = 0;
		
		dividerBlockRadius = 10;
		
		double xDiff =x2-x1;
		double yDiff = y2-y1;
		
		if(xDiff<0)
			rad = Math.atan((yDiff)/(xDiff))+Math.toRadians(180);
		else if(yDiff<0)
			rad = Math.atan((yDiff)/(xDiff))+Math.toRadians(360);
		else
			rad = Math.atan((yDiff)/(xDiff));	
		
		c = new Color(0,0,0,255);
	}
	
	public void pauseUpdate() {
		startTime = System.nanoTime() - elapsed * 1000000;
	}
	
	public boolean update() {
		
		elapsed = (System.nanoTime() - startTime) / 1000000;
		if(elapsed > lifeTime) {
			//spawn divider blocks
			
			while( x1 <= GamePanel.WIDTH && x1 >=0 && y1 <= GamePanel.HEIGHT && y1 >=0) {
				//System.out.println("x1: " + x1 + ", y1: " + y1);
				GamePanel.dividerBlocks.add(new DividerBlock(x1,y1,dividerBlockRadius));
				
				x1 = x1 + dividerBlockRadius * 1.5 * Math.cos(rad);
				y1 = y1 + dividerBlockRadius * 1.5 * Math.sin(rad);
				//System.out.println("[NEXT] x1: " + x1 + ", y1: " + y1);
			}
			
			
			return true;
		}
		
		return false;
	}
	
	public void draw(Graphics2D g){
		double progress = ((double)elapsed / lifeTime);
		if(progress > 1) {
			progress = 1;
		}
		
		g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), (int)(255 * progress)));
		g.setStroke(new BasicStroke((int)(15 * progress)));
		
		g.draw(new Line2D.Double(x1, y1, x2, y2));
		
		g.setStroke(new BasicStroke(1));
	}
	
	
}
