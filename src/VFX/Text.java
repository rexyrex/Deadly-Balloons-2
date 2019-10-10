package VFX;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;


public class Text {
	private double x;
	private double y;
	private long time;
	private String s;
	private int f; // font type
	
	private long start;
	
	private boolean other;
	private Color c;
	private int fontSize;
	
	public Text(double x, double y, long time, String s){
		this.x = x;
		this.y = y;
		this.time = time;
		this.s = s;
		start = System.nanoTime();
		f = Font.PLAIN;
		fontSize = 15;
	}
	
	public Text(double x, double y, long time, String s, boolean other, Color c){
		this.x = x;
		this.y = y;
		this.time = time;
		this.s = s;
		start = System.nanoTime();
		this.c = c;
		this.other = other;
		f = Font.PLAIN;
		fontSize = 15;
	}
	
	public Text(double x, double y, long time, String s, boolean other, Color c, int f){
		this.x = x;
		this.y = y;
		this.time = time;
		this.s = s;
		start = System.nanoTime();
		this.c = c;
		this.other = other;
		this.f = f;
		fontSize = 15;
	}
	
	public Text(double x, double y, long time, String s, boolean other, Color c, int f, int fontSize){
		this.x = x;
		this.y = y;
		this.time = time;
		this.s = s;
		start = System.nanoTime();
		this.c = c;
		this.other = other;
		this.f = f;
		this.fontSize = fontSize;
	}
	
	public boolean update(){
		long elapsed = (System.nanoTime()-start)/1000000;
		if(elapsed>time){
			return true;
		}
		return false;
	}
	public void draw(Graphics2D g){
		g.setFont(new Font("Gulim", f,fontSize));
		long elapsed = (System.nanoTime()-start)/1000000;
		int alpha = (int) ((255*Math.sin(3.14 * elapsed/time)));
		if(alpha>255) alpha = 255;
		if(alpha<0) alpha = 0;
		
		g.setColor(new Color(255,255,255,alpha) );
		if(other){
			g.setColor(c);
		}
		int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
		g.drawString(s, (int)(x-(length/2)), (int)y);
	}
}
