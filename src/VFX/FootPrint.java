package VFX;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class FootPrint {
	private double x;
	private double y;
	private double r;
	
	private long duration;
	private long startTime;
	
	Color c;
	
	public FootPrint(double x, double y, double r, Color c) {
		this.x = x;
		this.y = y;
		this.r = r;
		this.duration = 200;
		this.startTime = System.nanoTime();
		this.c = c;
	}
	
	public boolean update() {		
		if((System.nanoTime() - startTime)/1000000 > duration) {
			return true;
		}		
		return false;
	}
	
	public void draw(Graphics2D g) {
		long elapsed = (System.nanoTime() - startTime)/1000000;
		int alpha = (int)(c.getAlpha() - c.getAlpha()*elapsed/duration);
		if(alpha < 0) {
			alpha = 0;
		}
		Color alphaColor = new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
		g.setColor(alphaColor);
		g.fillOval((int)(x-r),(int)(y-r),(int)(2*r),(int)(2*r));
		g.setStroke(new BasicStroke(3));
		g.setColor(alphaColor.darker());
		g.fillOval((int)(x-r),(int)(y-r),(int)(2*r),(int)(2*r));
		g.setStroke(new BasicStroke(1));
	}
}
