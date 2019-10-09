package Entities;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import Panels.GamePanel;
import Utils.RandomUtils;

public class Lightning {
	
	private long duration;
	private long startTime;
	private long elapsed;
	private int alpha;
	
	private long stunDuration;
	
	private double dmg;
	
	private boolean hitOnce;
	ArrayList<Enemy> enemies;
	
	public Lightning(ArrayList<Enemy> enemies, double dmg, long stunDuration) {
		this.duration = 1200;
		startTime = System.nanoTime();
		elapsed = 0;
		alpha = 0;
		this.dmg = dmg;
		hitOnce = false;
		int chanceConnect = 99;
		this.enemies = new ArrayList<Enemy>();
		
		for(int i=0; i<enemies.size(); i++) {
			this.enemies.add(enemies.get(i));
			if(RandomUtils.runChance(100-chanceConnect)) {
				break;
			}
			chanceConnect--;
		}
		this.stunDuration = stunDuration;
		hitEnemies();
		
	}
	
	public void update() {
		elapsed = (System.nanoTime() - startTime) / 1000000;
	}
	
	public void hitEnemies() {
		//damage enemies
		for(int i=0; i<enemies.size(); i++) {
			enemies.get(i).hit(dmg);
			enemies.get(i).stun(stunDuration);
		}
	}
	
	public boolean isOver() {
		return elapsed > duration;
	}
	
	public void draw(Graphics2D g) {
		//cannot use this method because delayed dmg to enemies can cause null pointer error (if killed by sth else)
//		if(elapsed < duration/5) {
//			//get brighter
//			long timeLeftBrighter = (duration / 5) - elapsed ;
//			alpha = (int)(255 - 255 * (timeLeftBrighter)/(duration/5));
//			System.out.println("Getting brighter");
//		} else {
//			//get darker
//			long timeLeftDarker = duration - elapsed ;
//			alpha = (int)(255 * (timeLeftDarker)/(duration));
//			System.out.println("Getting darker");
//		}
		

		//get darker
		long timeLeftDarker = duration - elapsed ;
		alpha = (int)(150 * (timeLeftDarker)/(duration));
		
		g.setColor(new Color(255,255,255,alpha));
		g.fillRect(0,0,GamePanel.WIDTH,GamePanel.HEIGHT);
		
		g.setColor(new Color(255,255,51,alpha));
		g.setStroke(new BasicStroke(5));
		if(enemies.size()>0) {
			g.draw(new Line2D.Double(GamePanel.player.getx(), GamePanel.player.gety(), enemies.get(0).getx(), enemies.get(0).gety()));
		}
		
		
		for(int i=0; i<enemies.size()-1; i++) {
			Enemy e1 = enemies.get(i);
			Enemy e2 = enemies.get(i+1);
			
			g.setColor(new Color(255,255,51,alpha+60));
			g.draw(new Line2D.Double(e1.getx(), e1.gety(), e2.getx(), e2.gety()));
		}
		g.setStroke(new BasicStroke(1));
	}
}
