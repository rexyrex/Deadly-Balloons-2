package Panels;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
public class Game {

	public static void main(String args[]){		
		final JFrame window = new JFrame("Rex Shooter");
		window.setLayout(new BorderLayout());
		//window.setPreferredSize(new Dimension(1500,700));

		//window.setBackground(new Color(0,0,0,0));
		ShopPanel sPanel = new ShopPanel();
		
		
		InfoPanel iPanel = new InfoPanel();
		
		
		GamePanel gp = new GamePanel(window, sPanel, iPanel);
		
		
		window.add(gp, BorderLayout.CENTER);
		window.add(iPanel, BorderLayout.WEST);
		window.add(sPanel, BorderLayout.EAST);
		
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		
	}
	
}
