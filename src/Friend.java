import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class Friend {
	private double x;
	private double y;
	private int r;
	
	private double dx;
	private double dy;
	private double rad;
	private double speed;
	
	private Color color1;
	
	private double destX;
	private double destY;
	
	private int type;
	
	private boolean dead;
	
	private long lifeSpan;
	private long birthDate;
	
	private long fireDelay;
	private long firingTimer;
	
	public Friend(double x, double y, double destX, double destY, int type) {		
		birthDate = System.nanoTime();		
		this.x = x;
		this.y = y;
		this.r = 10;

		this.destX = destX;
		this.destY = destY;
		this.type = type;
		
		if(type==1) {
			color1 = new Color(225,225,225,255);			
			lifeSpan = 7000;
			fireDelay = 220;
			firingTimer = System.nanoTime();
			speed = 3;
		}
		
		if(type==2) {
			color1 = new Color(225,225,225,255);			
			lifeSpan = 15000;
			fireDelay = 220;
			firingTimer = System.nanoTime();
			speed = 8;
		}
		
		//double angle = Math.random() * 140 + 20;
		//rad = Math.toRadians(angle);
		
		//dx = Math.cos(rad) * speed;
		//dy = Math.sin(rad) * speed;
		
		goTowards(destX, destY);
	}
	
	
	public double getX() {
		return x;
	}


	public int getR() {
		return r;
	}


	public void setR(int r) {
		this.r = r;
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


	public boolean isDead() {
		return dead;
	}
	
	public void goTowards(double px, double py){
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
	
	public void update() {
		x += dx;
		y += dy;
		
		if(x<0 && dx<0) x = GamePanel.WIDTH;
		if(y<0 && dy<0) y = GamePanel.HEIGHT;
		if(x> GamePanel.WIDTH && dx>0) x = 0;
		if(y> GamePanel.HEIGHT && dy>0) y = 0;
		
		//fire
		long elapsed = (System.nanoTime() - firingTimer) / 1000000;
		if(elapsed > fireDelay){
			firingTimer = System.nanoTime();
			GamePanel.bullets.add(new Bullet(270, (int)x,(int)y, GamePanel.player.getFriendDmg()));
		}
		//check age
		if((System.nanoTime() - birthDate)/1000000 > lifeSpan) {
			dead = true;
		}
		
		if(type==2 && RandomUtils.runChance(4)){
				destX = RandomUtils.getRandomDest(GamePanel.WIDTH, GamePanel.HEIGHT)[0];
				destY = RandomUtils.getRandomDest(GamePanel.WIDTH, GamePanel.HEIGHT)[1];
				goTowards(destX, destY);				
		}
	}
	
	public void draw(Graphics2D g) {
		g.setColor(color1);		
		g.fillOval((int)(x-r),(int)(y-r), 2*r, 2*r);		
		g.setStroke(new BasicStroke(3));
		g.setColor(color1.darker());		
		g.drawOval((int)(x-r),(int)(y-r), 2*r, 2*r);
		g.setStroke(new BasicStroke(1));
		
		//life bar
		double secSinceBirth = (System.nanoTime() - birthDate)/1000000;
		double percentage =  secSinceBirth/(double)lifeSpan;
		g.drawRect((int)(x-2*r), (int)(y+2*r), (int)(4*r), (int)(r));
		g.fillRect((int)(x-2*r), (int)(y+2*r), (int)(4*r*(1-percentage)), (int)(r));
	}
}
