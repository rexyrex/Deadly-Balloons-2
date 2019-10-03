import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
public class Game {

	public static void main(String args[]){		
		final JFrame window = new JFrame("Shooter Game");
		window.setLayout(new BorderLayout());
		window.setPreferredSize(new Dimension(1500,700));

		
		ShopPanel sPanel = new ShopPanel();
		window.add(sPanel, BorderLayout.EAST);
		
		InfoPanel iPanel = new InfoPanel();
		window.add(iPanel, BorderLayout.WEST);
		
		GamePanel gp = new GamePanel(window, sPanel, iPanel);
		window.add(gp, BorderLayout.CENTER);
		
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel container = new JPanel();
		//window.setContentPane(gp);
		//gp.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
		
		//container.add(shopWindow)
		
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		
	}
	
}
