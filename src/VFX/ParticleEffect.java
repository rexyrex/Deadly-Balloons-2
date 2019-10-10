package VFX;

import java.awt.Color;
import java.awt.Graphics2D;

public class ParticleEffect {
	private double[] xs;
	private double[] ys;
	private double[] rs;
	
	private double[] dxs;
	private double[] dys;
	
	private double[] speeds;
	
	private double maxSpeed;
	
	private long duration;
	private long startTime;
	
	private int numParticles;
	
	private Color c;
	
	//dash particle effect
	public ParticleEffect(double x, double y, double r, int numParticles) {
		this.duration = 300;
		this.startTime = System.nanoTime();
		
		this.maxSpeed = 10;
		
		xs = new double[numParticles];
		ys = new double[numParticles];
		rs = new double[numParticles];
		
		dxs = new double[numParticles];
		dys = new double[numParticles];
		
		speeds = new double[numParticles];
		
		for(int i=0; i<numParticles; i++) {
			xs[i] = x;
			ys[i] = y;
			rs[i] = r;
			speeds[i] = Math.random() * maxSpeed;
			updateDxDyRandom(i);
		}
	}
	
	public void updateDxDyTowards(double goX, double goY, int index){
		double xDiff = goX-xs[index];
		double yDiff = goY-ys[index];
		
			double rad;
			if(xDiff<0)
				rad = Math.atan((yDiff)/(xDiff))+Math.toRadians(180);
			else if(yDiff<0)
				rad = Math.atan((yDiff)/(xDiff))+Math.toRadians(360);
			else
				rad = Math.atan((yDiff)/(xDiff));			
			
			dxs[index] = Math.cos(rad) * speeds[index];
			dys[index] = Math.sin(rad) * speeds[index];
	}
	
	public void updateDxDyRandom(int index) {
		double degrees = Math.random() * 360;
		double rad = 180 / Math.PI * degrees;
		dxs[index] = Math.cos(rad) * speeds[index];
		dys[index] = Math.sin(rad) * speeds[index];
	}
	
	public boolean update() {
		
		for(int i=0; i<numParticles; i++) {
			xs[i] += dxs[i];
			ys[i] += dys[i];
		}
		
		//damping
		for(int i=0; i<numParticles; i++) {
			dxs[i] *= 0.8;
			dys[i] *= 0.8;
		}
		
		if((System.nanoTime() - startTime)/1000000 > duration) {
			return true;
		}		
		return false;
	}
	
	public void draw(Graphics2D g) {
		for(int i=0; i<numParticles; i++) {
			g.setColor(c);
			g.fillOval((int)(xs[i]-rs[i]),(int)(ys[i]-rs[i]),(int)(2*rs[i]),(int)(2*rs[i]));
		}
	}
	
}
