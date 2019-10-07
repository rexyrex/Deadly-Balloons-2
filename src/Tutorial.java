import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class Tutorial {

	public void render(Graphics2D g, GamePanel gp, int stage) {
		g.setColor(new Color(255,255,255,120));
		g.fillRect(0,0,gp.WIDTH,gp.HEIGHT);
		g.setColor(Color.BLACK);
		g.setFont(new Font("Gulim", Font.BOLD,40));
		String s = gp.tutPauseContent[stage];
		int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
		g.drawString(s, (gp.WIDTH-length)/2, gp.HEIGHT/2);
	
	}
}
