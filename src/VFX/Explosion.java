package VFX;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;


public class Explosion {
	
	private double x;
	private double y;
	private double r;
	private double maxRadius;
	private double radiusChangeSpeed;
	private Color c;
	
	private boolean isMenu;
	
	//constructor
	public Explosion(double x, double y, double r, double maxRadius){
		this.x = x;
		this.y = y;
		this.r = r;
		radiusChangeSpeed = 3;
		this.maxRadius = maxRadius;
		c=(new Color(255,255,255,147));
		isMenu = false;
	}
	
	//turret explosion
	public Explosion(double x, double y, double r, double maxRadius, double speed){
		this.x = x;
		this.y = y;
		this.r = r;
		radiusChangeSpeed = speed;
		this.maxRadius = maxRadius;
		c=(new Color(255,0,3,177));
		isMenu = false;
	}
	
	//menu explosion
	public Explosion(double x, double y, double r, double maxRadius, double speed, Color c){
		this.x = x;
		this.y = y;
		this.r = r;
		radiusChangeSpeed = speed;
		this.maxRadius = maxRadius;
		this.c=c;
		isMenu = true;
	}
	
	public boolean update(){
		r+=radiusChangeSpeed;
		if(r>= maxRadius){
			return true;
		}
		return false;
	}
	
	public void draw(Graphics2D g){
		g.setColor(c);
		
		if(isMenu) {
			g.setStroke(new BasicStroke(3));
		} else {
			g.setStroke(new BasicStroke(1));
		}
		
		g.drawOval((int)(x-r),(int)(y-r), (int) (2*r), (int) (2*r));
		g.setStroke(new BasicStroke(1));
	}
	
}
